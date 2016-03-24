package com.imotspot.database.model.vertex;

import com.imotspot.database.model.core.ODBVertex;
import com.imotspot.model.imot.LocationMarker;

import java.io.Serializable;
import java.util.List;

public class LocationMarkerVertex extends ODBVertex {

    private final static String IdentifierFieldName = "lat";
    private final static String IdentifierFieldNameSecond = "lng";

    private LocationMarker locationMarker;

    public LocationMarkerVertex(LocationMarker locationMarker) {
        super();
        this.locationMarker = locationMarker;
    }

    @Override
    protected String getIdentificatorFieldName() {
        return IdentifierFieldName;
    }

    @Override
    protected Serializable getIdentificatorValue() {
        return locationMarker.getAddress();
    }

    @Override
    protected String[] getIdentificatorFieldNames() {
        return new String[]{IdentifierFieldName, IdentifierFieldNameSecond};
    }

    @Override
    protected Serializable[] getIdentificatorValues() {
        return new Serializable[]{locationMarker.getLat(), locationMarker.getLng()};
    }

    @Override
    protected LocationMarkerVertex duplicate() {
        return new LocationMarkerVertex(locationMarker);
    }

    protected List<Serializable> properties() {
        List<Serializable> props = super.properties();
        addProp(props, "name", locationMarker.getName());
        addProp(props, "address", locationMarker.getAddress());
        return props;
    }

    @Override
    public LocationMarker model() {
        return locationMarker;
    }
}
