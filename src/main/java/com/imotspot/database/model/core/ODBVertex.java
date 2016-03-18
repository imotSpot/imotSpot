package com.imotspot.database.model.core;

import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.tinkerpop.blueprints.Element;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;

import java.io.Serializable;

public abstract class ODBVertex<E extends Serializable> extends ODBElement {

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

//    @Override
//    protected Vertex load(OrientGraph graph) {
//        return getByIdentificator(graph);
//    }

    @Override
    protected Element saveOrUpdate(OrientGraph graph) {
        Element element = getByIdentificator(graph);
        if (element != null) {
            return update(graph, element);
        }

        return save(graph);
    }

    public Vertex getByIdentificator(OrientGraph graph) {
        // Object ret = orientGraph.command(new OCommandGremlin("g.v('9:68128').both().both()")).execute();
        String oSqlCommand = "SELECT * FROM " + NAME + " where " + constructSqlWhere();
        Object ret = graph.command(new OCommandSQL(oSqlCommand)).execute();
        Iterable<Vertex> vertices = (Iterable<Vertex>) ret;

        if (vertices.iterator().hasNext()) {
            return vertices.iterator().next();
        }

//        for (Vertex vertex : graph.getVertices(NAME,
//                getIdentificatorFieldNames(),
//                getIdentificatorValues())) {
//
//            return vertex;
//        }
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
        OrientVertex locationVertex = graph.addVertex("class:" + NAME, propertiesArray());
        update(graph, locationVertex);
        return locationVertex;
    }

    @Override
    public void remove(OrientGraph graph, Element vertex) {
        graph.removeVertex((Vertex) vertex);
    }

}
