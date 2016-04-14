package com.imotspot.database.model.vertex;

import com.imotspot.database.model.core.ODBVertex;
import com.imotspot.database.model.edge.CountryEdge;
import com.imotspot.database.model.edge.DistrictEdge;
import com.imotspot.database.model.edge.LocationMarkerEdge;
import com.imotspot.database.model.edge.SityEdge;
import com.imotspot.model.imot.*;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;

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

        CountryVertex countryVertex = (CountryVertex) new CountryVertex(location.country()).useGraph(graph()).saveOrUpdate();
        DistrictVertex districtVertex = (DistrictVertex) new DistrictVertex(location.district()).useGraph(graph()).saveOrUpdate();
        CityVertex sityVertex = (CityVertex) new CityVertex(location.city()).useGraph(graph()).saveOrUpdate();
        LocationMarkerVertex locationMarkerVertex = (LocationMarkerVertex) new LocationMarkerVertex(location.marker()).useGraph(graph()).saveOrUpdate();

        new CountryEdge(locationVertex, countryVertex).useGraph(graph()).saveOrUpdate();
        new DistrictEdge(locationVertex, districtVertex).useGraph(graph()).saveOrUpdate();
        new SityEdge(locationVertex, sityVertex).useGraph(graph()).saveOrUpdate();
        new LocationMarkerEdge(locationVertex, locationMarkerVertex).useGraph(graph()).saveOrUpdate();

        return locationVertex;
    }

    @Override
    protected void loadRelationsToModel(ODocument document) {
        try {
            for (OIdentifiable id : new OSQLSynchQuery<ODocument>("traverse out() from " + document.getIdentity() + " while $depth <= 1")) {
                if (CountryVertex.class.getSimpleName().equals(((ODocument) id.getRecord()).getClassName())) {
                    ODocument doc = id.getRecord();
                    location.country((Country) new CountryVertex(new Country(doc.field("name"))).load(doc).model());
                }

                if (CityVertex.class.getSimpleName().equals(((ODocument) id.getRecord()).getClassName())) {
                    ODocument doc = id.getRecord();
                    location.city((City) new CityVertex(new City(doc.field("name"))).load(doc).model());
                }

                if (DistrictVertex.class.getSimpleName().equals(((ODocument) id.getRecord()).getClassName())) {
                    ODocument doc = id.getRecord();
                    location.district((District) new DistrictVertex(new District(doc.field("name"))).load(doc).model());
                }

                if (LocationMarkerVertex.class.getSimpleName().equals(((ODocument) id.getRecord()).getClassName())) {
                    ODocument doc = id.getRecord();
                    location.marker((LocationMarker) new LocationMarkerVertex(new LocationMarker(doc.field("lat"), doc.field("lng"))).load(doc).model());
                }

            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
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
        return location.address();
    }
}
