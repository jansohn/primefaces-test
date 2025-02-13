package org.primefaces.test;

import java.io.Serializable;
import java.time.Duration;
import java.util.UUID;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class TestNestedObject implements Serializable {

    private String id;
    private String songName;
    private Duration duration;

    public TestNestedObject(String songName, Duration duration) {
        this.id = UUID.randomUUID().toString();
        this.songName = songName;
        this.duration = duration;
    }

}
