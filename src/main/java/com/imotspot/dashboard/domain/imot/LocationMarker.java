package com.imotspot.dashboard.domain.imot;

import lombok.Data;

@Data
public class LocationMarker implements HasName {
    private float lat;
    private float lng;
    private String name;
    private String address;

    public LocationMarker(float lat, float lng) {
        this.lat = lat;
        this.lng = lng;
    }
}
