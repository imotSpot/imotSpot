package com.imotspot.model.imot;

import lombok.Data;

import java.io.Serializable;

@Data
//@Accessors(fluent = true)
public class Location implements Serializable {
    private static final long serialVersionUID = 1L;

    private String address;
    private Country country;
    private City city;
    private District district;
    private LocationMarker marker;
}
