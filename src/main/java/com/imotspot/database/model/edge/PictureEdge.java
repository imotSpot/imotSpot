package com.imotspot.database.model.edge;

import com.imotspot.database.model.core.ODBEdge;
import com.tinkerpop.blueprints.Vertex;

public class PictureEdge extends ODBEdge {

    public PictureEdge(Vertex outVertex, Vertex inVertex) {
        super(outVertex, inVertex);
    }

}
