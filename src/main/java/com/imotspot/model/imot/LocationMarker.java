package com.imotspot.model.imot;

import com.imotspot.model.imot.interfaces.Named;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(fluent = true)
public class LocationMarker implements Named {
    private static final long serialVersionUID = 1L;

    private float lat;
    private float lng;
    private String name;
    private String address;

    public LocationMarker(float lat, float lng) {
        this.lat = lat;
        this.lng = lng;
    }
}
