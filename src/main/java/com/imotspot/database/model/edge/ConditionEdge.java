package com.imotspot.database.model.edge;

import com.imotspot.database.model.core.ODBEdge;
import com.imotspot.database.model.core.ODBVertex;

public class ConditionEdge extends ODBEdge {

    public ConditionEdge(ODBVertex outVertex, ODBVertex inVertex) {
        super(outVertex, inVertex);
    }
}
