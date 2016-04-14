package com.imotspot.database.model.vertex;

import com.imotspot.database.model.core.ODBVertex;
import com.imotspot.database.model.edge.*;
import com.imotspot.model.imot.*;
import com.imotspot.model.imot.enumerations.Condition;
import com.imotspot.model.imot.enumerations.ImotType;
import com.imotspot.model.imot.interfaces.Media;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;

import java.io.Serializable;
import java.util.List;

public class ImotVertex extends ODBVertex {

    private final static String IdentifierFieldName = "location";

    private Imot imot;

    public ImotVertex() {
        this(new Imot(null));
    }

    public ImotVertex(Imot imot) {
        super();
        this.imot = imot;
    }

    @Override
    public ImotVertex update() {
        ImotVertex imotVertex = (ImotVertex) super.update();

//        UserVertex userVertex = (UserVertex) new UserVertex(imot.getOwner()).useGraph(graph()).load();
//        new ImotEdge(userVertex, imotVertex).useGraph(graph()).saveOrUpdate();

        if (imot.getLocation() != null) {
            LocationVertex locationVertex = (LocationVertex) new LocationVertex(imot.getLocation()).useGraph(graph()).saveOrUpdate();
            new ImotEdge(imotVertex, locationVertex).useGraph(graph()).saveOrUpdate();
        }

        if (imot.getType() != null) {
            TypeVertex typeVertex = (TypeVertex) new TypeVertex(imot.getType()).useGraph(graph()).saveOrUpdate();
            new TypeEdge(imotVertex, typeVertex).useGraph(graph()).saveOrUpdate();
        }

        if (imot.getFrontImage() != null) {
            PictureVertex frontImageVertex = (PictureVertex) new PictureVertex(imot.getFrontImage()).useGraph(graph()).saveOrUpdate();
            new PictureEdge(imotVertex, frontImageVertex).useGraph(graph()).saveOrUpdate();
        }

        if (imot.getCondition() != null) {
            ConditionVertex conditionVertex = (ConditionVertex) new ConditionVertex(imot.getCondition()).useGraph(graph()).saveOrUpdate();
            new ConditionEdge(imotVertex, conditionVertex).useGraph(graph()).saveOrUpdate();
        }

        for (Media media : imot.getMedia()) {
            if (media instanceof Picture) {
                PictureVertex mediaVertex = (PictureVertex) new PictureVertex((Picture) media).useGraph(graph()).saveOrUpdate();
                new PictureEdge(imotVertex, mediaVertex).useGraph(graph()).saveOrUpdate();
            } else {
                VideoVertex mediaVertex = (VideoVertex) new VideoVertex((Video) media).useGraph(graph()).saveOrUpdate();
                new VideoEdge(imotVertex, mediaVertex).useGraph(graph()).saveOrUpdate();
            }
        }

        for (Feature feature : imot.getFeatures()) {
            FeatureVertex featureVertex = (FeatureVertex) new FeatureVertex(feature).useGraph(graph()).saveOrUpdate();
            new FeatureEdge(imotVertex, featureVertex).useGraph(graph()).saveOrUpdate();
        }

        for (Appliance appliance : imot.getAppliances()) {
            ApplianceVertex applianceVertex = (ApplianceVertex) new ApplianceVertex(appliance).useGraph(graph()).saveOrUpdate();
            new ApplianceEdge(imotVertex, applianceVertex).useGraph(graph()).saveOrUpdate();
        }

        return imotVertex;
    }

    @Override
    protected void loadRelationsToModel(ODocument document) {
        try {
            for (OIdentifiable id : new OSQLSynchQuery<ODocument>("traverse out() from " + document.getIdentity() + " while $depth <= 1")) {
                if (LocationVertex.class.getSimpleName().equals(((ODocument) id.getRecord()).getClassName())) {
                    ODocument doc = id.getRecord();
                    imot.setLocation((Location) new LocationVertex(new Location()).load(doc).model());
                }

                if (TypeVertex.class.getSimpleName().equals(((ODocument) id.getRecord()).getClassName())) {
                    ODocument doc = id.getRecord();
                    imot.setType(ImotType.get(doc.field("type")));
                }

                if (ConditionVertex.class.getSimpleName().equals(((ODocument) id.getRecord()).getClassName())) {
                    ODocument doc = id.getRecord();
                    imot.setCondition(Condition.get(doc.field("condition")));
                }

                if (PictureVertex.class.getSimpleName().equals(((ODocument) id.getRecord()).getClassName())) {
                    ODocument doc = id.getRecord();
                    imot.setFrontImage((Picture) new PictureVertex(new Picture(doc.field("uri"))).load(doc).model());
                }
                // todo need to implement and other relations
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
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
        return imot.getLocation();
    }

    @Override
    protected ImotVertex duplicate() {
        return new ImotVertex(imot);
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
