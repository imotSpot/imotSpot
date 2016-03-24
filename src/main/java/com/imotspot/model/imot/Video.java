package com.imotspot.model.imot;

import com.imotspot.model.imot.interfaces.Media;
import lombok.Data;
import lombok.experimental.Accessors;

import java.net.URI;

@Data
@Accessors(fluent = true)
public class Video implements Media {
    private static final long serialVersionUID = 1L;

    private URI uri;
    private String name;
}
