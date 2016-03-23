package com.imotspot.model.imot;

import com.imotspot.interfaces.Media;
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
