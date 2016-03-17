package com.imotspot.database.model.vertex;

import com.imotspot.dashboard.domain.User;
import com.imotspot.database.model.core.ODBVertex;

import java.io.Serializable;
import java.util.List;

public class UserVertex extends ODBVertex {

    private final static String IdentifierFieldName = "oauthIdentifier";

    private User user;

    public UserVertex(User user) {
        super();
        this.user = user;
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
