package org.primefaces.test;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.primefaces.model.DefaultLazyDataModel;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import lombok.Data;

@Data
@Named
@ViewScoped
public class TestView implements Serializable {

    private String string;
    private Integer integer;
    private BigDecimal decimal;
    private LocalDateTime localDateTime;
    private DefaultLazyDataModel<TestObject> lazyDataModel;
    private List<TestObject> list;
    private List<TestObject> filteredList;
    private List<String> artists;
    private TestObject selectedObject;
    private TestObject clickedTestObject;

    @PostConstruct
    public void init() {
        string = "Welcome to PrimeFaces!!!";
        list = new ArrayList<>(Arrays.asList(
                new TestObject("Thriller", "Michael Jackson", 1982, null),
                new TestObject("Back in Black", "AC/DC", 1980, "AC/DC band details"),
                new TestObject("The Bodyguard", "Whitney Houston", 1992, null),
                new TestObject("The Dark Side of the Moon", "Pink Floyd", 1973, null)));
        artists = new ArrayList<>(Arrays.asList("Michael Jackson",
                "AC/DC",
                "Whitney Houston",
                "Pink Floyd"));

        this.lazyDataModel = DefaultLazyDataModel.<TestObject>builder()
                .valueSupplier((filterBy) -> {
                    System.out.println("valueSupplier.get() called with filterBy size " + filterBy.size());
                    return Arrays.asList(
                            new TestObject("Thriller", "Michael Jackson", 1982, null),
                            new TestObject("Back in Black", "AC/DC", 1980, "AC/DC band details"),
                            new TestObject("The Bodyguard", "Whitney Houston", 1992, null),
                            new TestObject("The Dark Side of the Moon", "Pink Floyd", 1973, null));
                })
                .rowKeyField("id")
                .build();
    }

    public void onClickDetailsIcon(TestObject testObject) {
        System.out.println("clicked object: " + testObject.toString());
        this.clickedTestObject = testObject;
    }

    public String getDetailsMessage() {
        if (this.clickedTestObject != null) { return this.clickedTestObject.toString(); }

        return "";
    }
}
