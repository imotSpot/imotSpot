package com.imotspot.database.model.vertex;

import com.imotspot.dashboard.domain.imot.Media;
import com.imotspot.database.model.core.ODBVertex;

import java.io.Serializable;
import java.util.List;

public class MediaVertex extends ODBVertex {

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
        return media.getUri();
    }

    protected List<Serializable> properties() {
        List<Serializable> props = super.properties();
        addProp(props, "name", media.getName());
        return props;
    }
}
