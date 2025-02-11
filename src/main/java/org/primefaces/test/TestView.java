package org.primefaces.test;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.primefaces.event.ReorderEvent;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.component.UIData;
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

    public void onDraggableRow(UIData uiData) {
        System.out.println("Executing custom draggableRowFunction...");

        FacesContext context = FacesContext.getCurrentInstance();
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();
        String clientId = uiData.getClientId(context);
        int fromIndex = Integer.parseInt(params.get(clientId + "_fromIndex"));

        System.out.println("Setting datatable row index to: " + fromIndex);
        uiData.setRowIndex(fromIndex);
    }

    public void onRowReorder(ReorderEvent event) {
        System.out.println("before");
        for (TestObject item : list) {
            System.out.println(item.toString());
        }
        int fromIndex = event.getFromIndex();
        int toIndex = event.getToIndex();

        FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Row Moved", "From: " + fromIndex + ", To:" + toIndex);
        FacesContext.getCurrentInstance().addMessage(null, msg);

        if (toIndex >= fromIndex) {
            Collections.rotate(list.subList(fromIndex, toIndex + 1), -1);
        } else {
            Collections.rotate(list.subList(toIndex, fromIndex + 1), 1);
        }

        System.out.println("after");
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
