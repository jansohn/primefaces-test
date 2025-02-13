package org.primefaces.test;

import java.io.Serializable;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.primefaces.event.SelectEvent;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import lombok.Data;

@Data
@Named
@ViewScoped
public class TestView implements Serializable {

    private String string;
    private List<TestObject> list;
    private TestNestedObject selectedSong;

    @PostConstruct
    public void init() {
        string = "Welcome to PrimeFaces!!!";
        list = new ArrayList<>(Arrays.asList(
                new TestObject("Thriller", "Michael Jackson", 1982),
                new TestObject("Back in Black", "AC/DC", 1980),
                new TestObject("The Bodyguard", "Whitney Houston", 1992),
                new TestObject("The Dark Side of the Moon", "Pink Floyd", 1973)));

        int counter = 0;
        for (TestObject item : list) {
            for (int index = 0; index < 4; index++) {
                item.addSong(new TestNestedObject(String.format("Song %d", ++counter), Duration.ofMinutes(counter)));
            }
        }
    }

    public void onRowSelect(SelectEvent<TestNestedObject> event) {
        FacesMessage msg = new FacesMessage("Song Selected", String.valueOf(event.getObject()));
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void onPlaySong(TestObject testObject) {
        FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Playing Song From", String.format("%s (%s)", testObject, this.selectedSong));
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

}
