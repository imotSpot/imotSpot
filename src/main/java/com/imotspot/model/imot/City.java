package com.imotspot.model.imot;

import lombok.Data;

@Data
public class City implements HasName {
    private String name;

    public City(String city) {
        this.name = city;
    }
}
