package com.imotspot.model.imot;

import lombok.Data;

@Data
public class Country implements HasName {
    private String name;

    public Country(String country) {
        this.name = country;
    }
}
