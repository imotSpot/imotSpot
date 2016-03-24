package com.imotspot.database.model.vertex;

import com.imotspot.model.imot.Picture;

public class PictureVertex extends MediaVertex {

    public PictureVertex(Picture picture) {
        super(picture);
    }

    @Override
    protected PictureVertex duplicate() {
        return new PictureVertex((Picture) model());
    }
}
