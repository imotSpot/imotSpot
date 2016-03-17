package com.imotspot.dashboard.domain.imot;

import lombok.Data;

import java.io.Serializable;

@Data
public class Location implements Serializable {
    private String address;
    private Country country;
    private Sity sity;
    private District district;
    private LocationMarker marker;
}
