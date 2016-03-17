package com.imotspot.dashboard.domain.imot;

import lombok.Data;

@Data
public class Sity implements HasName {
    private String name;

    public Sity(String sity) {
        this.name = sity;
    }
}
