package com.imotspot.database.model;

import com.imotspot.dashboard.domain.User;
import com.imotspot.database.DBOperation;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;

public class UserVertex extends ODBVertex {

    private User user;

    public UserVertex(User user) {
        super();
        this.user = user;
    }

    public void save() {
        dbServer.doInTX(new DBOperation() {
            @Override
            public void execute(OrientGraph graph) {
                Vertex marko = graph.addVertex("class:" + VERTEX_NAME,
                        "role", user.getRole(),
                        "firstName", user.getFirstName(),
                        "lastName", user.getLastName(),
                        "email", user.getEmail());
            }
        });
    }
}
