package com.imotspot.model.imot;

import com.imotspot.model.imot.interfaces.Named;
import lombok.Data;

@Data
//@Accessors(fluent = true)
public class Feature implements Named {
    private static final long serialVersionUID = 1L;

    private String name;
}
