package com.imotspot.database.model.edge;

import com.imotspot.database.model.core.ODBEdge;
import com.imotspot.database.model.core.ODBVertex;

public class LocationMarkerEdge extends ODBEdge {

    public LocationMarkerEdge(ODBVertex outVertex, ODBVertex inVertex) {
        super(outVertex, inVertex);
    }
}
