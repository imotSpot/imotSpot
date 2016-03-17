package com.imotspot.database.model.vertex;

import com.imotspot.dashboard.domain.imot.Imot;
import com.imotspot.database.model.core.ODBVertex;

import java.io.Serializable;
import java.util.List;

public class ImotVertex extends ODBVertex {

    private final static String IdentifierFieldName = "location";

    private Imot imot;

    public ImotVertex(Imot imot) {
        super();
        this.imot = imot;
    }

    @Override
    protected String getIdentificatorFieldName() {
        return IdentifierFieldName;
    }

    @Override
    protected Serializable getIdentificatorValue() {
        return imot.getLocation();
    }

    protected List<Serializable> properties() {
        List<Serializable> props = super.properties();
        addProp(props, "price", imot.getPrice());
        addProp(props, "year", imot.getYear());
        addProp(props, "description", imot.getDescription());
        return props;
    }
}
