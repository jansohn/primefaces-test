package org.primefaces.test;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.primefaces.context.PrimeApplicationContext;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.JPALazyDataModel;
import org.primefaces.model.SortMeta;
import org.primefaces.util.BeanUtils;
import org.primefaces.util.Callbacks;
import org.primefaces.util.PropertyDescriptorResolver;

import jakarta.faces.FacesException;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.Type;

public class MyJpaLazyDataModel<T> extends JPALazyDataModel<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<T> results = new ArrayList<>();
    private Integer firstCache;
    private Integer pageSizeCache;
    private Map<String, SortMeta> sortByCache;
    private Map<String, FilterMeta> filterByCache;

    @Override
    public List<T> load(int first, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {
        if (this.useCache(first, pageSize, sortBy, filterBy)) { return this.results; }

        this.results = super.load(first, pageSize, sortBy, filterBy);
        System.out.println(String.format("Fetched %d results from DB.", this.results.size()));

        return this.results;
    }

    @Override
    public T getRowData(String rowKey) {
        System.out.println(String.format("Fetching row data (key: %s) from cached results...", rowKey));

        for (T result : this.results) {
            if (Objects.equals(rowKey, super.getRowKey(result))) { return result; }
        }

        return null;
    }

    private boolean useCache(Integer first, Integer pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {
        try {
            return Objects.equals(this.firstCache, first)
                    && Objects.equals(this.pageSizeCache, pageSize)
                    && Objects.equals(this.sortByCache, sortBy)
                    && Objects.equals(this.filterByCache, filterBy);
        } finally {
            this.firstCache = first;
            this.pageSizeCache = pageSize;
            this.sortByCache = sortBy;
            this.filterByCache = filterBy;
        }
    }

    public static <T> Builder<T> myBuilder() {
        return new Builder<>();
    }

    public static class Builder<T> {
        private final MyJpaLazyDataModel<T> model;

        public Builder() {
            model = new MyJpaLazyDataModel<>();
        }

        public Builder<T> entityClass(Class<T> entityClass) {
            model.entityClass = entityClass;
            return this;
        }

        public Builder<T> entityManager(Callbacks.SerializableSupplier<EntityManager> entityManager) {
            model.entityManager = entityManager;
            return this;
        }

        public Builder<T> rowKeyConverter(Converter<T> rowKeyConverter) {
            model.rowKeyConverter = rowKeyConverter;
            return this;
        }

        public Builder<T> rowKeyProvider(Callbacks.SerializableFunction<T, Object> rowKeyProvider) {
            model.rowKeyProvider = rowKeyProvider;
            return this;
        }

        public Builder<T> rowKeyField(String rowKey) {
            model.rowKeyField = rowKey;
            return this;
        }

        public Builder<T> rowKeyField(SingularAttribute<T, ?> rowKeyMetamodel) {
            model.rowKeyField = rowKeyMetamodel.getName();
            model.rowKeyType = rowKeyMetamodel.getJavaType();
            return this;
        }

        public Builder<T> rowKeyType(Class<?> rowKeyType) {
            model.rowKeyType = rowKeyType;
            return this;
        }

        public Builder<T> caseSensitive(boolean caseSensitive) {
            model.caseSensitive = caseSensitive;
            return this;
        }

        public Builder<T> wildcardSupport(boolean wildcardSupport) {
            model.wildcardSupport = wildcardSupport;
            return this;
        }

        public Builder<T> queryEnricher(QueryEnricher<T> queryEnricher) {
            model.queryEnricher = queryEnricher;
            return this;
        }

        public Builder<T> filterEnricher(FilterEnricher<T> filterEnricher) {
            model.filterEnricher = filterEnricher;
            return this;
        }

        public Builder<T> additionalFilterMeta(AdditionalFilterMeta additionalFilterMeta) {
            model.additionalFilterMeta = additionalFilterMeta;
            return this;
        }

        public Builder<T> sortEnricher(SortEnricher<T> sortEnricher) {
            model.sortEnricher = sortEnricher;
            return this;
        }

        public Builder<T> resultEnricher(Callbacks.SerializableConsumer<List<T>> resultEnricher) {
            model.resultEnricher = resultEnricher;
            return this;
        }

        public MyJpaLazyDataModel<T> build() {
            Objects.requireNonNull(model.entityClass, "entityClass not set");
            Objects.requireNonNull(model.entityManager, "entityManager not set");

            // some notes about required options for the rowKey to implement #getRowData/#getRowKey,
            // which is actually mandatory as required for selection
            // - rowKeyConverter
            // this is the easiest way and often already available in applications for entities, we just reuse all of it
            // - rowKeyField
            // this is now required but we can try to read it via JPA metamodel first
            // #getRowData needs it to fire a query with the rowKey in the WHERE clause
            // - rowKeyType
            // we will get the info from JPA or via reflection from rowKeyField
            // it's required for the internal implementation of #getRowData
            // - rowKeyProvider
            // it's just the internal implementation of #getRowKey

            // rowKeyConverter (either rowKeyConverter or rowKeyField are required)
            if (model.rowKeyConverter != null) {
                model.rowKeyProvider = model::getRowKeyFromConverter;
            }
            // rowKeyField
            else {
                FacesContext context = FacesContext.getCurrentInstance();

                // try to lookup from JPA metamodel, if not defined by user
                if (model.rowKeyField == null) {
                    EntityManagerFactory emf = model.entityManager.get().getEntityManagerFactory();

                    EntityType<T> entityType = emf.getMetamodel().entity(model.entityClass);
                    Type<?> idType = entityType.getIdType();
                    if (idType.getPersistenceType() != Type.PersistenceType.BASIC) {
                        throw new FacesException("Entity @Id is not a basic type. Define a rowKeyField!");
                    }

                    if (!BeanUtils.isPrimitiveOrPrimitiveWrapper(idType.getJavaType())) {
                        Converter converter = context.getApplication().createConverter(idType.getJavaType());
                        if (converter == null) {
                            throw new FacesException("Entity @Id is not a primitive and no Converter found for " + idType.getJavaType().getName()
                                    + "! Either define a rowKeyField or create a JSF Converter for it!");
                        }
                    }

                    SingularAttribute<?, ?> idAttribute = entityType.getId(idType.getJavaType());
                    model.rowKeyField = idAttribute.getName();
                    if (model.rowKeyType == null) {
                        model.rowKeyType = idType.getJavaType();
                    }
                    if (model.rowKeyProvider == null) {
                        model.rowKeyProvider = obj -> emf.getPersistenceUnitUtil().getIdentifier(obj);
                    }
                }
                // user-defined rowKeyField
                else {
                    PropertyDescriptorResolver propResolver =
                            PrimeApplicationContext.getCurrentInstance(context).getPropertyDescriptorResolver();

                    if (model.rowKeyType == null) {
                        model.rowKeyType = propResolver.get(model.entityClass, model.rowKeyField).getPropertyType();
                    }
                    if (model.rowKeyProvider == null) {
                        model.rowKeyProvider = obj -> propResolver.getValue(obj, model.rowKeyField);
                    }
                }
            }

            return model;
        }
    }
}
