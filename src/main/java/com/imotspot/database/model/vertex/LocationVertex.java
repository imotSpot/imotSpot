package com.imotspot.database.model.vertex;

import com.imotspot.dashboard.domain.imot.Location;
import com.imotspot.database.model.core.ODBVertex;
import com.imotspot.database.model.edge.CountryEdge;
import com.imotspot.database.model.edge.DistrictEdge;
import com.imotspot.database.model.edge.LocationMarkerEdge;
import com.imotspot.database.model.edge.SityEdge;
import com.tinkerpop.blueprints.Element;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;

import java.io.Serializable;

public class LocationVertex extends ODBVertex {

    private final static String IdentifierFieldName = "address";

    private Location location;

    public LocationVertex(Location location) {
        super();
        this.location = location;
    }

    @Override
    public OrientVertex update(OrientGraph graph, Element vertex) {
        OrientVertex locationVertex = super.update(graph, vertex);

        Vertex countryVertex = (Vertex) new CountryVertex(location.getCountry()).saveOrUpdate();
        Vertex districtVertex = (Vertex) new DistrictVertex(location.getDistrict()).saveOrUpdate();
        Vertex sityVertex = (Vertex) new CityVertex(location.getCity()).saveOrUpdate();
        Vertex locationMarkerVertex = (Vertex) new LocationMarkerVertex(location.getMarker()).saveOrUpdate();

        new CountryEdge(locationVertex, countryVertex).saveOrUpdate();
        new DistrictEdge(locationVertex, districtVertex).saveOrUpdate();
        new SityEdge(locationVertex, sityVertex).saveOrUpdate();
        new LocationMarkerEdge(locationVertex, locationMarkerVertex).saveOrUpdate();

        return locationVertex;
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
