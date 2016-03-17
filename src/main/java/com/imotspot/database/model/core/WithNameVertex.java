package com.imotspot.database.model.core;

import com.imotspot.dashboard.domain.imot.HasName;

import java.io.Serializable;

public abstract class WithNameVertex extends ODBVertex {

    private final static String IdentifierFieldName = "name";

    private HasName hasName;

    public WithNameVertex(HasName hasName) {
        super();
        this.hasName = hasName;
    }

    @Override
    protected String getIdentificatorFieldName() {
        return IdentifierFieldName;
    }

    @Override
    protected Serializable getIdentificatorValue() {
        return hasName.getName();
    }

}
