package com.imotspot.database.model;

import com.imotspot.dagger.AppComponent;
import com.imotspot.database.OrientDBServer;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;

import javax.inject.Inject;

public class ODBVertex {

    protected String VERTEX_NAME;

    @Inject
    public OrientDBServer dbServer;

    public ODBVertex() {
        AppComponent.daggerInjector().inject(this);

        VERTEX_NAME = getClass().getSimpleName();

        OrientGraphNoTx graphNoTx = dbServer.getGraphNoTx();
        if (!graphNoTx.getRawGraph().getMetadata().getSchema().existsClass(VERTEX_NAME)) {
            graphNoTx.createVertexType(VERTEX_NAME);
        }
    }

}
