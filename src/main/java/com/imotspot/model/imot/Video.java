package com.imotspot.model.imot;

import lombok.Data;

import java.net.URI;

@Data
public class Video implements Media {
    private URI uri;
    private String name;
}
