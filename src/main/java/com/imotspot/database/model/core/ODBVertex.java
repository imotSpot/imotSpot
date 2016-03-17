package com.imotspot.database.model.core;

import com.tinkerpop.blueprints.Element;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;

public abstract class ODBVertex extends ODBElement {

    public ODBVertex() {
        super();

        OrientGraphNoTx graphNoTx = dbServer.getGraphNoTx();
        if (!graphNoTx.getRawGraph().getMetadata().getSchema().existsClass(NAME)) {

            if (PARENT_NAME != ODBVertex.class.getSimpleName()) {
                if (!graphNoTx.getRawGraph().getMetadata().getSchema().existsClass(PARENT_NAME)) {
                    graphNoTx.createVertexType(PARENT_NAME);
                }
                graphNoTx.createVertexType(NAME, PARENT_NAME);
            } else {
                graphNoTx.createVertexType(NAME);
            }
        }
    }

    @Override
    protected Element saveOrUpdate(OrientGraph graph) {
        Element element = getByIdentificator(graph);
        if (element != null) {
            return update(graph, element);
        }

        return save(graph);
    }

    public Vertex getByIdentificator(OrientGraph graph) {
        for (Vertex vertex : graph.getVertices(NAME,
                getIdentificatorFieldNames(),
                getIdentificatorValues())) {

            return vertex;
        }
        return null;
    }

    @Override
    public OrientVertex update(OrientGraph graph, Element vertex) {
        OrientVertex oVertex = graph.getVertex(vertex.getId());
        oVertex.setProperties(propertiesArray());

        oVertex.save();
        return oVertex;
    }

    @Override
    public OrientVertex save(OrientGraph graph) {
        return graph.addVertex("class:" + NAME, propertiesArray());
    }

    @Override
    public void remove(OrientGraph graph, Element vertex) {
        graph.removeVertex((Vertex) vertex);
    }

}
