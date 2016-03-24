package com.imotspot.database.model.vertex;

import com.imotspot.model.imot.Video;

public class VideoVertex extends MediaVertex {

    public VideoVertex(Video video) {
        super(video);
    }

    @Override
    protected VideoVertex duplicate() {
        return new VideoVertex((Video) model());
    }
}
