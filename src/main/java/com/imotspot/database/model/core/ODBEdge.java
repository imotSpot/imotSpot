package com.imotspot.database.model.core;

import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.tinkerpop.blueprints.impls.orient.OrientEdge;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import lombok.Value;
import lombok.experimental.Accessors;
import lombok.experimental.NonFinal;
import org.apache.commons.lang.NotImplementedException;

import java.io.Serializable;

@Value
@NonFinal
@Accessors(fluent = true)
public abstract class ODBEdge extends ODBElement {

    //    @Delegate
    @NonFinal
    protected OrientEdge edge;

    protected ODBVertex outVertex;
    protected ODBVertex inVertex;

    public ODBEdge(ODBVertex outVertex, ODBVertex inVertex) {
//        this(null, outVertex, inVertex);
//    }
//
//    public ODBEdge(OrientEdge edge, ODBVertex outVertex, ODBVertex inVertex) {
        super();

        if (outVertex == null || inVertex == null) {
            throw new RuntimeException("can't save null object as vertex in edge");
        }
//        this.edge = edge;
        this.outVertex = outVertex;
        this.inVertex = inVertex;

        createEdgeType();
    }

    @Override
    protected ODBEdge duplicate() {
        ODBEdge userV = null;
        try {
            userV = this.getClass().getConstructor(ODBVertex.class, ODBVertex.class)
                    .newInstance(outVertex, inVertex);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return userV;
    }

//    protected ODBEdge duplicate(OrientEdge edge) {
//        ODBEdge userV = null;
//        try {
//            userV = this.getClass().asSubclass(this.getClass())
//                    .getConstructor(OrientEdge.class, ODBVertex.class, ODBVertex.class)
//                    .newInstance(edge, outVertex, inVertex);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return userV;
//    }
//
//    public <V extends ODBEdge> V edge(OrientEdge edge) {
//        return (V) duplicate(edge);
//    }

    public <V extends ODBEdge> V edge(OrientEdge edge) {
        ODBEdge duplicated = duplicate();
        duplicated.edge = edge;
        return (V) duplicated;
    }

    public ODBEdge useGraph(OrientGraph graph) {
        return (ODBEdge) super.useGraph(graph);
    }

    @Override
    public ODBEdge load() {
        return edge(get());
    }

    @Override
    public ODBEdge load(ODocument oElement) {
        return this;
    }

    @Override
    public ODBEdge saveOrUpdate() {
        return save();
    }

    @Override
    public ODBEdge update() {
        remove();
        return save();
    }

    @Override
    public ODBEdge save() {
        return edge(graph().addEdge("class:" + NAME, outVertex.vertex, inVertex.vertex, NAME));
    }

    @Override
    public void remove() {
        OrientEdge e = get();
        if (e != null) {
            graph().removeEdge(e);
        }
    }

    protected OrientEdge get() {
        // Object ret = orientGraph.command(new OCommandGremlin("g.v('9:68128').both().both()")).execute();
        String oSqlCommand = "SELECT * FROM " + NAME + " where out()";
        Object ret = graph().command(new OCommandSQL(oSqlCommand)).execute();
//        return (Iterable<Edge>) ret;
        return null;
    }

    @Override
    protected String getIdentificatorFieldName() {
        return "";
    }

    @Override
    protected Serializable getIdentificatorValue() {
        return null;
    }

    @Override
    public Serializable model() {
        throw new NotImplementedException("Edges have no model.");
    }
}
