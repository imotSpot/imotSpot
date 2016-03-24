package com.imotspot.database.model.vertex;

import com.imotspot.database.model.core.ODBVertex;
import com.imotspot.database.model.edge.CountryEdge;
import com.imotspot.database.model.edge.DistrictEdge;
import com.imotspot.database.model.edge.LocationMarkerEdge;
import com.imotspot.database.model.edge.SityEdge;
import com.imotspot.model.imot.Location;

import java.io.Serializable;

public class LocationVertex extends ODBVertex {

    private final static String IdentifierFieldName = "address";

    private Location location;

    public LocationVertex(Location location) {
        super();
        this.location = location;
    }

    @Override
    public LocationVertex update() {
        LocationVertex locationVertex = (LocationVertex) super.update();

        CountryVertex countryVertex = (CountryVertex) new CountryVertex(location.getCountry()).useGraph(graph()).saveOrUpdate();
        DistrictVertex districtVertex = (DistrictVertex) new DistrictVertex(location.getDistrict()).useGraph(graph()).saveOrUpdate();
        CityVertex sityVertex = (CityVertex) new CityVertex(location.getCity()).useGraph(graph()).saveOrUpdate();
        LocationMarkerVertex locationMarkerVertex = (LocationMarkerVertex) new LocationMarkerVertex(location.getMarker()).useGraph(graph()).saveOrUpdate();

        new CountryEdge(locationVertex, countryVertex).useGraph(graph()).saveOrUpdate();
        new DistrictEdge(locationVertex, districtVertex).useGraph(graph()).saveOrUpdate();
        new SityEdge(locationVertex, sityVertex).useGraph(graph()).saveOrUpdate();
        new LocationMarkerEdge(locationVertex, locationMarkerVertex).useGraph(graph()).saveOrUpdate();

        return locationVertex;
    }

    @Override
    protected LocationVertex duplicate() {
        return new LocationVertex(location);
    }

    @Override
    public Location model() {
        return location;
    }

    @Override
    protected String getIdentificatorFieldName() {
        return IdentifierFieldName;
    }

    @Override
    protected Serializable getIdentificatorValue() {
        return location.getAddress();
    }
}
