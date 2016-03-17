package com.imotspot.database.model.core;

import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Element;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientEdge;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;
import lombok.Getter;

import java.io.Serializable;

@Getter
public abstract class ODBEdge extends ODBElement {

    private Vertex outVertex;
    private Vertex inVertex;

    public ODBEdge(Vertex outVertex, Vertex inVertex) {
        super();

        this.outVertex = outVertex;
        this.inVertex = inVertex;

        OrientGraphNoTx graphNoTx = dbServer.getGraphNoTx();
        if (!graphNoTx.getRawGraph().getMetadata().getSchema().existsClass(NAME)) {

            if (PARENT_NAME != ODBEdge.class.getSimpleName()) {
                if (!graphNoTx.getRawGraph().getMetadata().getSchema().existsClass(PARENT_NAME)) {
                    graphNoTx.createEdgeType(PARENT_NAME);
                }
                graphNoTx.createEdgeType(NAME, PARENT_NAME);
            } else {
                graphNoTx.createEdgeType(NAME);
            }
        }
    }
//
//    @Override
//    protected Element load(OrientGraph graph) {
//        return save(graph);
//    }

    @Override
    protected Element saveOrUpdate(OrientGraph graph) {
        return save(graph);
    }

    @Override
    public OrientEdge update(OrientGraph graph, Element edge) {
        remove(graph, edge);
        return (OrientEdge) save(graph);
    }

    @Override
    public Element save(OrientGraph graph) {
        return graph.addEdge("class:" + NAME, outVertex, inVertex, NAME);
    }

    @Override
    public void remove(OrientGraph graph, Element edge) {
        graph.removeEdge((Edge) edge);
    }

    @Override
    protected String getIdentificatorFieldName() {
        return "";
    }

    @Override
    protected Serializable getIdentificatorValue() {
        return null;
    }
}
