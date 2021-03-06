package com.imotspot.database.model.vertex;

import com.imotspot.database.model.core.ODBVertex;
import com.imotspot.model.imot.enumerations.Condition;

import java.io.Serializable;

public class ConditionVertex extends ODBVertex {

    private final static String IdentifierFieldName = "name";

    private Condition condition;

    public ConditionVertex(Condition condition) {
        super();
        this.condition = condition;
    }

    @Override
    protected ConditionVertex duplicate() {
        return new ConditionVertex(condition);
    }

    @Override
    protected String getIdentificatorFieldName() {
        return IdentifierFieldName;
    }

    @Override
    protected Serializable getIdentificatorValue() {
        return condition.name();
    }

    @Override
    public Serializable model() {
        return condition;
    }
}
