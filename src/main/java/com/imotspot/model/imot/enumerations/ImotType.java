package com.imotspot.model.imot.enumerations;

import java.io.Serializable;

public enum ImotType implements Serializable {
    ONE_BED(),
    TWO_BEDS(),
    THREE_BEDS(),
    FOUR_BEDS(),
    MANY_BEDS(),
    PENTHOUSE(),
    WORKSHOP(),
    OFFICE(),
    SHOP(),
    RESTAURANT(),
    HOTEL(),
    WAREHOUSE(),
    INDUSTRIAL_SPACE(),
    VILA(),
    HOUSE(),
    PART_OF_HOUSE(),
    PARCEL(),
    GARAGE(),
    AGRICULTURAL_LAND;

    public static ImotType get(String name) {
        for (ImotType type : ImotType.values()) {
            if (type.name().equals(name)) {
                return type;
            }
        }
        return ONE_BED;
    }
}
