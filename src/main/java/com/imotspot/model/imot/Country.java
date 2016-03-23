package com.imotspot.model.imot;

import com.imotspot.interfaces.HasName;
import lombok.Data;

@Data
public class Country implements HasName {
    private String name;

    public Country(String country) {
        this.name = country;
    }
}
