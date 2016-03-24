package com.imotspot.database.model.vertex;

import com.imotspot.database.model.core.ODBVertex;
import com.imotspot.database.model.edge.*;
import com.imotspot.model.imot.*;
import com.imotspot.model.imot.interfaces.Media;

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
    public ImotVertex update() {
        ImotVertex imotVertex = (ImotVertex) super.update();

        UserVertex userVertex = (UserVertex) ((UserVertex) new UserVertex(imot.owner()).useGraph(graph())).load();
        new ImotEdge(userVertex, imotVertex).useGraph(graph()).saveOrUpdate();

        if (imot.location() != null) {
            LocationVertex locationVertex = (LocationVertex) new LocationVertex(imot.location()).useGraph(graph()).saveOrUpdate();
            new ImotEdge(imotVertex, locationVertex).useGraph(graph()).saveOrUpdate();
        }

        if (imot.frontImage() != null) {
            PictureVertex frontImageVertex = (PictureVertex) new PictureVertex(imot.frontImage()).useGraph(graph()).saveOrUpdate();
            new PictureEdge(imotVertex, frontImageVertex).useGraph(graph()).saveOrUpdate();
        }

        if (imot.condition() != null) {
            ConditionVertex conditionVertex = (ConditionVertex) new ConditionVertex(imot.condition()).useGraph(graph()).saveOrUpdate();
            new ConditionEdge(imotVertex, conditionVertex).useGraph(graph()).saveOrUpdate();
        }

        for (Media media : imot.media()) {
            if (media instanceof Picture) {
                PictureVertex mediaVertex = (PictureVertex) new PictureVertex((Picture) media).useGraph(graph()).saveOrUpdate();
                new PictureEdge(imotVertex, mediaVertex).useGraph(graph()).saveOrUpdate();
            } else {
                VideoVertex mediaVertex = (VideoVertex) new VideoVertex((Video) media).useGraph(graph()).saveOrUpdate();
                new VideoEdge(imotVertex, mediaVertex).useGraph(graph()).saveOrUpdate();
            }
        }

        for (Feature feature : imot.features()) {
            FeatureVertex featureVertex = (FeatureVertex) new FeatureVertex(feature).useGraph(graph()).saveOrUpdate();
            new FeatureEdge(imotVertex, featureVertex).useGraph(graph()).saveOrUpdate();
        }

        for (Appliance appliance : imot.appliances()) {
            ApplianceVertex applianceVertex = (ApplianceVertex) new ApplianceVertex(appliance).useGraph(graph()).saveOrUpdate();
            new ApplianceEdge(imotVertex, applianceVertex).useGraph(graph()).saveOrUpdate();
        }

        return imotVertex;
    }

    @Override
    public Imot model() {
        return imot;
    }

    @Override
    protected String getIdentificatorFieldName() {
        return IdentifierFieldName;
    }

    @Override
    protected Serializable getIdentificatorValue() {
        return imot.location();
    }

    @Override
    protected ImotVertex duplicate() {
        return new ImotVertex(imot);
    }

    protected List<Serializable> properties() {
        List<Serializable> props = super.properties();
        addProp(props, "price", imot.price());
        addProp(props, "year", imot.year());
        addProp(props, "description", imot.description());
        addProp(props, "published", imot.published());
        return props;
    }
}
