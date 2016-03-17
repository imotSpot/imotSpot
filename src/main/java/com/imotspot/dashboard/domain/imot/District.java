package com.imotspot.dashboard.domain.imot;

import lombok.Data;

@Data
public class District implements HasName {
    private String name;

    public District(String district) {
        this.name = district;
    }
}
