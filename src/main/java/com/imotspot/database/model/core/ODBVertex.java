package com.imotspot.database.model.core;

import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.tinkerpop.blueprints.Element;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;
import lombok.Value;
import lombok.experimental.Accessors;
import lombok.experimental.NonFinal;

import java.io.Serializable;

/**
 * Graph vertex proxy to project models, capable to communicate with the database.
 *
 * @param <T> The project model that this element is proxy for.
 */
@Value
@NonFinal
@Accessors(fluent = true)
public abstract class ODBVertex<T extends Serializable> extends ODBElement {

    @NonFinal
    protected OrientVertex vertex;

    public ODBVertex() {
        super();
        createVertexType();
    }

    public ODBVertex useGraph(OrientGraph graph) {
        return (ODBVertex) super.useGraph(graph);
    }

    public <V extends ODBVertex> V vertex(OrientVertex vertex) {
        V copy = (V) duplicate();
        copy.vertex = vertex;
        return copy;
    }

    @Override
    public ODBVertex load() {
        OrientVertex v = get();
        load(v.getRecord());
        return vertex(v);
    }

    @Override
    public ODBVertex load(ODocument document) {

        for (String key : document.fieldNames()) {
            loadPropertyToModel(document, key);
        }

        loadRelationsToModel(document);
        return vertex(vertex);
    }

    protected void loadPropertyToModel(ODocument document, String key) {
        try {
            setProperty(key, document.field(key));
        } catch (Exception e) {
            return;
        }
    }

    protected void loadRelationsToModel(ODocument document) {
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
