package com.imotspot.model.imot;

import com.imotspot.model.imot.interfaces.Named;
import com.vaadin.tapio.googlemaps.client.LatLon;
import com.vaadin.tapio.googlemaps.client.overlays.GoogleMapMarker;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(fluent = true)
public class LocationMarker implements Named {
    private static final long serialVersionUID = 1L;
    private final GoogleMapMarker googleMarker;
    private Double lat;
    private Double lng;
    private String name;

    public LocationMarker(Double lat, Double lng) {
        this(new GoogleMapMarker("", new LatLon(lat, lng), false));

    }

    public LocationMarker(GoogleMapMarker marker) {
        this.lat = marker.getPosition().getLat();
        this.lng = marker.getPosition().getLon();
        this.name = marker.getCaption();
        this.googleMarker = marker;
    }
}
