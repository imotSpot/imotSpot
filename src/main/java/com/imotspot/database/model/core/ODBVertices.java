package com.imotspot.database.model.core;

import com.imotspot.database.DBOperation;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;
import lombok.Value;
import lombok.experimental.Accessors;
import lombok.experimental.NonFinal;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Value
@NonFinal
@Accessors(fluent = true)
public class ODBVertices<S extends Serializable> extends ODBElements {

    Class<ODBVertex> vertexType;

    public <V extends ODBVertex> ODBVertices(Class<V> vertexType) {
        super();
        this.vertexType = (Class<ODBVertex>) vertexType;
    }

    public Iterable<S> get() {
        return dbServer.doInTX(new DBOperation<Iterable<S>>() {
            @Override
            public Iterable<S> execute(OrientGraph graph) {
                useGraph(graph);
                try {
                    return getVertices();
                } catch (Exception e) {
                    e.printStackTrace();
                    return new ArrayList<S>();
                } finally {
                    useGraph(null);
                }
            }
        });
    }

    private <V extends ODBVertex> Iterable<S> getVertices() throws IllegalAccessException, InstantiationException {
        // Object ret = orientGraph.command(new OCommandGremlin("g.v('9:68128').both().both()")).execute();
        String oSqlCommand = "SELECT * FROM " + vertexType.getSimpleName();
        Object ret = graph().command(new OCommandSQL(oSqlCommand)).execute();

//        List<V> res = new ArrayList<>();
        List<S> models = new ArrayList<>();
        for (OrientVertex vertex : (Iterable<OrientVertex>) ret) {

            V modelVert = (V) vertexType.newInstance();

            modelVert.load(vertex.getRecord());
            models.add((S) modelVert.model());
        }

        return models;
    }
}
