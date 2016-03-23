package com.imotspot.model.imot;

import com.imotspot.interfaces.HasName;
import lombok.Data;

@Data
public class Feature implements HasName {
    private String name;
}
