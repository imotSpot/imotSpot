package com.imotspot.database.model.vertex;

import com.imotspot.database.model.core.ODBElement;
import com.imotspot.database.model.core.ODBVertex;
import com.imotspot.model.imot.enumerations.ImotType;
import groovy.lang.Delegate;

import java.io.Serializable;

public class TypeVertex extends ODBVertex {

    private final static String IdentifierFieldName = "type";

    @Delegate
    private ImotType type;

    public TypeVertex(ImotType type) {
        super();
        this.type = type;
    }

    @Override
    protected ODBElement duplicate() {
        TypeVertex typeV = new TypeVertex(type);
        return typeV;
    }

    @Override
    protected String getIdentificatorFieldName() {
        return IdentifierFieldName;
    }

    @Override
    protected Serializable getIdentificatorValue() {
        return type.name();
    }

    @Override
    public Serializable model() {
        return type;
    }
}
