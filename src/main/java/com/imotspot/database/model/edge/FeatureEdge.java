package com.imotspot.database.model.edge;

import com.imotspot.database.model.core.ODBEdge;
import com.tinkerpop.blueprints.Vertex;

public class FeatureEdge extends ODBEdge {

    public FeatureEdge(Vertex outVertex, Vertex inVertex) {
        super(outVertex, inVertex);
    }

}
