package com.imotspot.database.model.core;

import com.imotspot.model.imot.interfaces.Named;

import java.io.Serializable;

public abstract class NamedVertex extends ODBVertex {

    private final static String IdentifierFieldName = "name";

    private Named named;

    public NamedVertex(Named named) {
        super();
        this.named = named;
    }

    @Override
    protected NamedVertex duplicate() {
        NamedVertex namedVertex = null;
        try {
            namedVertex = this.getClass().getConstructor(named.getClass()).newInstance(named);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return namedVertex;
    }

    @Override
    protected String getIdentificatorFieldName() {
        return IdentifierFieldName;
    }

    @Override
    protected Serializable getIdentificatorValue() {
        return named.getName();
    }

    @Override
    public Serializable model() {
        return named;
    }
}
