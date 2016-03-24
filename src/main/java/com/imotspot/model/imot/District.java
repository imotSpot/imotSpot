package com.imotspot.model.imot;

import com.imotspot.model.imot.interfaces.Named;
import lombok.Data;

@Data
//@Accessors(fluent = true)
public class District implements Named {
    private static final long serialVersionUID = 1L;

    private String name;

    public District(String district) {
        this.name = district;
    }
}
