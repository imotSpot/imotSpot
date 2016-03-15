package com.imotspot.database.model;

import com.imotspot.dashboard.domain.User;
import com.imotspot.database.DBOperation;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;

public class UserVertex extends ODBVertex {

    private final static String oauthIdentifierFieldName = "oauthIdentifier";

    private User user;

    public UserVertex(User user) {
        super();
        this.user = user;
    }

    public void save() {
        dbServer.doInTX(new DBOperation() {
            @Override
            public void execute(OrientGraph graph) {
                Vertex vertex = getByOauthId(graph);
                if (vertex != null) {
                    update(graph, vertex);
                    return;
                }

                UserVertex.this.save(graph);
            }
        });
    }

    public Vertex getByOauthId(OrientGraph graph) {
        for (Vertex vertex : graph.getVertices(VERTEX_NAME,
                new String[]{oauthIdentifierFieldName},
                new Object[]{user.getOauthIdentifier()})) {

            return vertex;
        }
        return null;
    }

    public void update(OrientGraph graph, Vertex vertex) {
        OrientVertex oVertex = graph.getVertex(vertex.getId());
        oVertex.setProperties("role", user.getRole(),
                "firstName", user.getFirstName(),
                "lastName", user.getLastName(),
                "email", user.getEmail());

        oVertex.save();
    }

    public void save(OrientGraph graph) {
        graph.addVertex("class:" + VERTEX_NAME,
                oauthIdentifierFieldName, user.getOauthIdentifier(),
                "role", user.getRole(),
                "firstName", user.getFirstName(),
                "lastName", user.getLastName(),
                "email", user.getEmail());
    }
}
