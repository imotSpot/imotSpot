package com.imotspot.database.model.core;

import com.imotspot.interfaces.AppComponent;
import com.imotspot.database.DBOperation;
import com.imotspot.database.OrientDBServer;
import com.tinkerpop.blueprints.Element;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;

import javax.inject.Inject;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public abstract class ODBElement {

    public final String NAME;
    public final String PARENT_NAME;

    @Inject
    public OrientDBServer dbServer;

    public ODBElement() {
        AppComponent.daggerInjector().inject(this);

        NAME = getClass().getSimpleName();
        PARENT_NAME = getClass().getSuperclass().getSimpleName();
    }

    public Element saveOrUpdate() {
        return dbServer.doInTX(new DBOperation<Element>() {
            @Override
            public Element execute(OrientGraph graph) {
                return saveOrUpdate(graph);
            }
        });
    }

//    public Element load() {
//        return dbServer.doInTX(new DBOperation<Element>() {
//            @Override
//            public Element execute(OrientGraph graph) {
//                return load(graph);
//            }
//        });
//    }
//
//    abstract protected Element load(OrientGraph graph);

    abstract protected Element saveOrUpdate(OrientGraph graph);

    abstract public Element update(OrientGraph graph, Element element);

    abstract public Element save(OrientGraph graph);

    abstract public void remove(OrientGraph graph, Element element);

    protected Serializable[] propertiesArray() {
        List<Serializable> props = properties();
        return props.toArray(new Serializable[]{});
    }

    protected List<Serializable> properties() {
        ArrayList<Serializable> props = new ArrayList<>();
        addProps(props, getIdentificatorFieldNames(), getIdentificatorValues());
        return props;
    }

    protected void addProp(List<Serializable> props, String field, Serializable value) {
        if (value != null) {
            props.add(field);
            props.add(value);
        }
    }

    protected void addProps(List<Serializable> props, String[] fields, Serializable[] values) {
        for (int i = 0; i < fields.length; i++) {
            props.add(fields[i]);
            props.add(values[i]);
        }
    }

    protected String constructSqlWhere() {
        return constructSqlWhere(getIdentificatorFieldNames(), getIdentificatorValues());
    }

    protected String constructSqlWhere(String[] fields, Serializable[] values) {
        String where = "";
        for (int i = 0; i < fields.length; i++) {
            if (values[i] != null) {
                where += fields[i] + "='" + values[i] + "'";
            }
        }
        return where;
    }

    protected String[] getIdentificatorFieldNames() {
        return new String[]{getIdentificatorFieldName()};
    }

    protected Serializable[] getIdentificatorValues() {
        return new Serializable[]{getIdentificatorValue()};
    }

    abstract protected String getIdentificatorFieldName();

    abstract protected Serializable getIdentificatorValue();
}
