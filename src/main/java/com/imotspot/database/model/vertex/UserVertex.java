package com.imotspot.database.model.vertex;

import com.imotspot.database.model.core.ODBVertex;
import com.imotspot.database.model.edge.ImotEdge;
import com.imotspot.model.User;
import com.imotspot.model.imot.Imot;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import lombok.Value;
import lombok.experimental.Accessors;
import lombok.experimental.Delegate;

import java.io.Serializable;
import java.util.List;

@Value
@Accessors(fluent = true)
public class UserVertex extends ODBVertex implements User {

    private final static String IdentifierFieldName = "oauthIdentifier";

    @Delegate
    private User user;

    public UserVertex(User user) {
        super();
        this.user = user;
    }

    public UserVertex newCustomMethod() {
        return this;
    }

    @Override
    protected UserVertex duplicate() {
        UserVertex userV = new UserVertex(user);
        return userV;
    }

    @Override
    protected void loadRelationsToModel(ODocument document) {
//        for (OIdentifiable id : new OSQLSynchQuery<ODocument>("select expand( out() ) from " + vertex.getId())) {
        try {
            for (OIdentifiable id : new OSQLSynchQuery<ODocument>("traverse out(" + ImotEdge.class.getSimpleName() +
                    ") from " + document.getIdentity() + " while $depth <= 1")) {
                if (ImotVertex.class.getSimpleName().equals(((ODocument) id.getRecord()).getClassName())) {
                    ODocument doc = (ODocument) id.getRecord();
                    user.addImot((Imot) new ImotVertex(new Imot(null)).load(doc).model());
                }
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    @Override
    public UserVertex update() {
        UserVertex userVertex = (UserVertex) super.update();

        for (Imot imot : user.imots()) {
            ImotVertex imotVertex = (ImotVertex) new ImotVertex(imot).useGraph(graph()).saveOrUpdate();
            new ImotEdge(userVertex, imotVertex).saveOrUpdate();
        }

        return userVertex;
    }

    @Override
    public User model() {
        return user;
    }

    @Override
    protected String getIdentificatorFieldName() {
        return IdentifierFieldName;
    }

    @Override
    protected Serializable getIdentificatorValue() {
        return user.oauthIdentifier();
    }

    protected List<Serializable> properties() {
        List<Serializable> props = super.properties();
        addProp(props, "role", user.role());
        addProp(props, "firstName", user.firstName());
        addProp(props, "lastName", user.lastName());
        addProp(props, "email", user.email());
        return props;
    }

}
