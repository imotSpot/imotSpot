package com.imotspot.database.model.vertex;

import com.imotspot.database.model.core.ODBVertex;
import com.imotspot.database.model.edge.ImotEdge;
import com.imotspot.model.User;
import com.imotspot.model.imot.Imot;
import com.tinkerpop.blueprints.Element;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;

import java.io.Serializable;
import java.util.List;

public class UserVertex<E extends User> extends ODBVertex {

    private final static String IdentifierFieldName = "oauthIdentifier";

    private User user;

    public UserVertex(User user) {
        super();
        this.user = user;
    }

//    @Override
//    protected Vertex load(OrientGraph graph) {
//        Vertex userVertex = super.load(graph);
//
//        return userVertex;
//    }

    @Override
    public OrientVertex update(OrientGraph graph, Element vertex) {
        OrientVertex userVertex = super.update(graph, vertex);

        for (Imot imot : user.getImots()) {
            Vertex imotVertex = (Vertex) new ImotVertex(imot).saveOrUpdate();
            new ImotEdge(userVertex, imotVertex).saveOrUpdate();
        }

        return userVertex;
    }

    @Override
    protected String getIdentificatorFieldName() {
        return IdentifierFieldName;
    }

    @Override
    protected Serializable getIdentificatorValue() {
        return user.getOauthIdentifier();
    }

    protected List<Serializable> properties() {
        List<Serializable> props = super.properties();
        addProp(props, "role", user.getRole());
        addProp(props, "firstName", user.getFirstName());
        addProp(props, "lastName", user.getLastName());
        addProp(props, "email", user.getEmail());
        return props;
    }
}
