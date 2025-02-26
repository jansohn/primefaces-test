package org.primefaces.test;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.primefaces.component.datatable.DataTable;

import jakarta.annotation.PostConstruct;
import jakarta.faces.context.FacesContext;
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
    private List<TestObject> list;
    private List<TestObject> filteredList;
    private List<String> artists;
    private TestObject selectedObject;
    private String filterArtist;

    @PostConstruct
    public void init() {
        string = "Welcome to PrimeFaces!!!";
        list = new ArrayList<>(Arrays.asList(
                new TestObject("Thriller", "Michael Jackson", 1982),
                new TestObject("Back in Black", "AC/DC", 1980),
                new TestObject("The Bodyguard", "Whitney Houston", 1992),
                new TestObject("The Dark Side of the Moon", "Pink Floyd", 1973)));
        artists = new ArrayList<>(Arrays.asList("Michael Jackson",
                "AC/DC",
                "Whitney Houston",
                "Pink Floyd"));
    }

    public void activateDefaultFilter() {
        this.selectedObject = list.get(0);
        this.filterArtist = "Michael Jackson";

        // filter and sort maps do not get automatically updated so we initialize them again explicitly
        DataTable dataTable = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent(":frmTest:dataTable");
        dataTable.initFilterBy(FacesContext.getCurrentInstance());
        dataTable.initSortBy(FacesContext.getCurrentInstance());
    }
}
