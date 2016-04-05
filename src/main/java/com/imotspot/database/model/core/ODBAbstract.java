package com.imotspot.database.model.core;

import com.imotspot.database.OrientDBServer;
import com.imotspot.interfaces.AppComponent;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;

import javax.inject.Inject;
import java.io.Serializable;

/**
 * Base abstract class for models linked to the database.
 */
public abstract class ODBAbstract {

    @Inject
    public OrientDBServer dbServer;
    private OrientGraph graph;

    public ODBAbstract() {
        AppComponent.daggerInjector().inject(this);
    }

    public OrientGraph graph() {
        if (graph == null) {
            graph = dbServer.graph();
        }
        return graph;
    }

    public ODBAbstract useGraph(OrientGraph graph) {
        this.graph = graph;
        return this;
    }

    public void closeGraph() {
        graph.shutdown();
        graph = null;
    }

    protected String constructSqlWhere(String[] fields, Serializable[] values) {
        String where = "";
        for (int i = 0; i < fields.length; i++) {
            if (values[i] != null) {
                if (where.length() > 0) {
                    where += " and ";
                }
                where += fields[i] + "='" + values[i] + "' ";
            }
        }
        return where;
    }

}
