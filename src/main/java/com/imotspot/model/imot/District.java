package com.imotspot.model.imot;

import com.imotspot.interfaces.HasName;
import lombok.Data;

@Data
public class District implements HasName {
    private String name;

    public District(String district) {
        this.name = district;
    }
}
