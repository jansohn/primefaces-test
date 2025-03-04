package org.primefaces.test;

import java.io.Serializable;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.persistence.EntityManager;
import lombok.Data;

@Data
@Named
@ViewScoped
public class TestView implements Serializable {

    private String string;

    @Inject
    private EntityManager em;
    private CachingJpaLazyDataModel<TestJpa> lazyDataModel;

    @PostConstruct
    public void init() {
        string = "PrimeFaces JPA DataTable";

        lazyDataModel = CachingJpaLazyDataModel.<TestJpa>myBuilder()
                .entityClass(TestJpa.class)
                .entityManager(() -> em)
                .caseSensitive(false)
                .build();
    }
}
