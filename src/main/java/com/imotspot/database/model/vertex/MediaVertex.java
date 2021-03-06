package com.imotspot.database.model.vertex;

import com.imotspot.database.model.core.ODBVertex;
import com.imotspot.model.imot.interfaces.Media;

import java.io.Serializable;
import java.util.List;

public abstract class MediaVertex extends ODBVertex {

    private final static String IdentifierFieldName = "uri";

    private Media media;

    public MediaVertex(Media media) {
        super();
        this.media = media;
    }

    @Override
    protected String getIdentificatorFieldName() {
        return IdentifierFieldName;
    }

    @Override
    protected Serializable getIdentificatorValue() {
        return media.uri();
    }

    protected List<Serializable> properties() {
        List<Serializable> props = super.properties();
        addProp(props, "name", media.name());
        return props;
    }

    @Override
    public Media model() {
        return media;
    }
}
