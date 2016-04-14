package com.imotspot.model.imot.enumerations;

import java.io.Serializable;

public enum Condition implements Serializable {
    NEW(),
    USED();

    public static Condition get(String name) {
        for (Condition condition : Condition.values()) {
            if (condition.name().equals(name)) {
                return condition;
            }
        }
        return NEW;
    }
}
