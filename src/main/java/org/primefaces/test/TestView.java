package org.primefaces.test;

import java.io.Serializable;
import java.util.List;

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
    private MyJpaLazyDataModel<TestJpa> lazyDataModel;
    private TestJpa selectedRow;
    private ChildJpa clickedChildJpa;

    @PostConstruct
    public void init() {
        string = "PrimeFaces JPA DataTable";

        lazyDataModel = MyJpaLazyDataModel.<TestJpa>myBuilder()
                .entityClass(TestJpa.class)
                .entityManager(() -> em)
                .caseSensitive(false)
                .resultEnricher(this::enrichResult)
                .build();
    }

    private List<TestJpa> enrichResult(List<TestJpa> result) {
        int counter = 0;
        for (TestJpa entity : result) {
            System.out.println(String.format("Enriching parent result #%d '%s' (hash code: %d)...",
                    entity.getId(),
                    entity.getStringCol(),
                    entity.hashCode()));

            for (ChildJpa child : entity.getChildren()) {
                child.setDetails(String.format("Dynamic content %d", ++counter));
                System.out.println(String.format("Set details for child '%s' (hash code: %d)",
                        child,
                        child.hashCode()));
            }
        }

        return result;
    }

    public void onRowSelect() {
        if (selectedRow != null) {
            System.out.println(String.format("Selected parent #%d '%s' (hash code: %d)...",
                    selectedRow.getId(),
                    selectedRow.getStringCol(),
                    selectedRow.hashCode()));

            for (ChildJpa child : selectedRow.getChildren()) {
                System.out.println(String.format("with child '%s' (hash code: %d)",
                        child,
                        child.hashCode()));
            }
        }
    }

    public void onClickDetailsIcon(ChildJpa childJpa) {
        System.out.println("clicked object: " + childJpa);
        this.clickedChildJpa = childJpa;
    }

    public String getDetailsMessage() {
        if (this.clickedChildJpa != null) { return this.clickedChildJpa.getDetails(); }

        return "";
    }
}
