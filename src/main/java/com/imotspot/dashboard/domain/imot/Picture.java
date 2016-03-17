package com.imotspot.dashboard.domain.imot;

import lombok.Data;

import java.net.URI;

@Data
public class Picture implements Media {
    private URI uri;
    private String name;

    public Picture(URI uri) {
        this.uri = uri;
    }
}
