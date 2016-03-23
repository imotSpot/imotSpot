package com.imotspot.model.imot;

import com.imotspot.interfaces.HasName;
import lombok.Data;

@Data
public class City implements HasName {
    private String name;

    public City(String city) {
        this.name = city;
    }
}
