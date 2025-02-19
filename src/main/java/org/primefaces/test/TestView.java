package org.primefaces.test;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.primefaces.event.ReorderEvent;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.AjaxBehaviorEvent;
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
    private List<String> artists;

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

    public void onRowReorder(ReorderEvent event) {
        FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Row Moved", "From: " + event.getFromIndex() + ", To:" + event.getToIndex());
        FacesContext.getCurrentInstance().addMessage(null, msg);

        for (TestObject item : list) {
            System.out.println(item.toString());
        }
    }

    public void actionArtistSelected(AjaxBehaviorEvent event) {
        Object source = event.getSource();

        if (source instanceof String) {
            String artist = (String) source;
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Artist Selected", "New value: " + artist);
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
    }
}
