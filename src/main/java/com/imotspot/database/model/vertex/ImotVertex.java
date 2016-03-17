package com.imotspot.database.model.vertex;

import com.imotspot.dashboard.domain.imot.*;
import com.imotspot.database.model.core.ODBVertex;
import com.imotspot.database.model.edge.*;
import com.tinkerpop.blueprints.Element;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;

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
    public OrientVertex update(OrientGraph graph, Element vertex) {
        OrientVertex imotVertex = super.update(graph, vertex);

//        Vertex userVertex = (Vertex) new UserVertex(imot.getOwner());
//        new ImotEdge(userVertex, imotVertex).saveOrUpdate();

        Vertex locationVertex = (Vertex) new LocationVertex(imot.getLocation()).saveOrUpdate();
        new ImotEdge(imotVertex, locationVertex).saveOrUpdate();

        Vertex frontImageVertex = (Vertex) new PictureVertex(imot.getFrontImage()).saveOrUpdate();
        new PictureEdge(imotVertex, frontImageVertex).saveOrUpdate();

        Vertex conditionVertex = (Vertex) new ConditionVertex(imot.getCondition()).saveOrUpdate();
        new PictureEdge(imotVertex, conditionVertex).saveOrUpdate();

        for (Media media : imot.getMedia()) {
            if (media instanceof Picture) {
                Vertex mediaVertex = (Vertex) new PictureVertex((Picture) media).saveOrUpdate();
                new PictureEdge(imotVertex, mediaVertex).saveOrUpdate();
            } else {
                Vertex mediaVertex = (Vertex) new VideoVertex((Video) media).saveOrUpdate();
                new VideoEdge(imotVertex, mediaVertex).saveOrUpdate();
            }
        }

        for (Feature feature : imot.getFeatures()) {
            Vertex featureVertex = (Vertex) new FeatureVertex(feature).saveOrUpdate();
            new FeatureEdge(imotVertex, featureVertex).saveOrUpdate();
        }

        for (Appliance appliance : imot.getAppliances()) {
            Vertex applianceVertex = (Vertex) new ApplianceVertex(appliance).saveOrUpdate();
            new ApplianceEdge(imotVertex, applianceVertex).saveOrUpdate();
        }

        return imotVertex;
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
        addProp(props, "published", imot.getPublished());
        return props;
    }
}
