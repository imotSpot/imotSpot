package com.imotspot.model.imot;

import com.imotspot.model.imot.interfaces.Named;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(fluent = true)
public class Appliance implements Named {
    private static final long serialVersionUID = 1L;

    private String name;

    public Appliance(String city) {
        this.name = city;
    }
}
