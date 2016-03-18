package com.imotspot.dashboard.domain.imot;

import lombok.Data;

@Data
public class City implements HasName {
    private String name;

    public City(String city) {
        this.name = city;
    }
}
