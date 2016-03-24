package com.imotspot.database.model.core;

import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.tinkerpop.blueprints.Element;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;
import lombok.Value;
import lombok.experimental.Accessors;
import lombok.experimental.NonFinal;

@Value
@NonFinal
@Accessors(fluent = true)
public abstract class ODBVertex extends ODBElement {

    @NonFinal
    protected OrientVertex vertex;

    public ODBVertex() {
//        this(null);
//    }
//
//    public ODBVertex(OrientVertex vertex) {
        super();
//        this.vertex = vertex;
        createVertexType();
    }

    public <V extends ODBVertex> V vertex(OrientVertex vertex) {
        ODBVertex copy = duplicate();
        copy.vertex = vertex;
        return (V) copy;
    }

    @Override
    public ODBVertex load() {
        OrientVertex vertex = get();

        for (String key : vertex.getPropertyKeys()) {
            loadPropertyToModel(vertex, key);
        }

        loadRelationsToModel();
        return vertex(vertex);
    }

    protected void loadPropertyToModel(Vertex vertex, String key) {
        try {
            setProperty(key, vertex.getProperty(key));
        } catch (Exception e) {
            return;
        }
    }

    protected void loadRelationsToModel() {
    }

    @Override
    public ODBVertex saveOrUpdate() {
        Element element = get();
        if (element != null) {
            return update().load();
        }

        return save();
    }

    @Override
    public ODBVertex save() {
        return vertex(graph().addVertex("class:" + NAME, propertiesArray())).update();
    }

    protected OrientVertex get() {
        // Object ret = orientGraph.command(new OCommandGremlin("g.v('9:68128').both().both()")).execute();
        String oSqlCommand = "SELECT * FROM " + NAME + " where " + constructSqlWhere();
        Object ret = graph().command(new OCommandSQL(oSqlCommand)).execute();
        Iterable<OrientVertex> vertices = (Iterable<OrientVertex>) ret;

        if (vertices.iterator().hasNext()) {
            vertex = vertices.iterator().next();
        }
        return vertex;
    }

    @Override
    public ODBVertex update() {
        OrientVertex oVertex = graph().getVertex(vertex.getId());
        oVertex.setProperties(propertiesArray());

        oVertex.save();
        return vertex(oVertex);
    }

    @Override
    public void remove() {
        Vertex v = get();
        if (v != null) {
            graph().removeVertex(v);
        }
    }
}
