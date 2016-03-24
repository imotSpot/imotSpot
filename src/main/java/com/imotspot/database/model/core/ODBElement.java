package com.imotspot.database.model.core;

import com.imotspot.database.DBOperation;
import com.imotspot.database.OrientDBServer;
import com.imotspot.interfaces.AppComponent;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;

import javax.inject.Inject;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public abstract class ODBElement {

    public static String NAME;
    public static String PARENT_NAME;

    @Inject
    public OrientDBServer dbServer;
    private OrientGraph graph;

    public ODBElement() {
        AppComponent.daggerInjector().inject(this);

        NAME = getClass().getSimpleName();
        PARENT_NAME = getClass().getSuperclass().getSimpleName();
    }

    public <E extends ODBElement> E saveOrUpdateInNewTX() {
        return dbServer.doInTX(new DBOperation<E>() {
            @Override
            public <E> E execute(OrientGraph graph) {
                ODBElement.this.graph = graph;
                try {
                    return (E) saveOrUpdate();
                } finally {
                    ODBElement.this.graph = null;
                }
            }
        });
    }

    public <E extends ODBElement> E loadInNewTX() {
        return dbServer.doInTX(new DBOperation<E>() {
            @Override
            public <E> E execute(OrientGraph graph) {
                ODBElement.this.graph = graph;
                try {
                    return (E) load();
                } finally {
                    ODBElement.this.graph = null;
                }
            }
        });
    }

    public OrientGraph graph() {
        if (graph == null) {
            graph = dbServer.graph();
        }
        return graph;
    }

    public ODBElement useGraph(OrientGraph graph) {
        this.graph = graph;
        return this;
    }

    public void closeGraph() {
        graph.shutdown();
        graph = null;
    }

    protected abstract <E extends ODBElement> E duplicate();

    abstract public <E extends ODBElement> E load();

    abstract public <E extends ODBElement> E saveOrUpdate();

    abstract public <E extends ODBElement> E update();

    abstract public <E extends ODBElement> E save();

    abstract public void remove();

    protected void createVertexType() {
        OrientGraphNoTx graph = dbServer.getGraphNoTx();
        if (!graph.getRawGraph().getMetadata().getSchema().existsClass(NAME)) {

            if (PARENT_NAME != ODBVertex.class.getSimpleName()) {
                if (!graph.getRawGraph().getMetadata().getSchema().existsClass(PARENT_NAME)) {
                    graph.createVertexType(PARENT_NAME);
                }
                graph.createVertexType(NAME, PARENT_NAME);
            } else {
                graph.createVertexType(NAME);
            }
        }
    }

    protected void createEdgeType() {
        OrientGraphNoTx graph = dbServer.getGraphNoTx();
        if (!graph.getRawGraph().getMetadata().getSchema().existsClass(NAME)) {

            if (PARENT_NAME != ODBEdge.class.getSimpleName()) {
                if (!graph.getRawGraph().getMetadata().getSchema().existsClass(PARENT_NAME)) {
                    graph.createEdgeType(PARENT_NAME);
                }
                graph.createEdgeType(NAME, PARENT_NAME);
            } else {
                graph.createEdgeType(NAME);
            }
        }
    }

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
                if (where.length() > 0) {
                    where += " and ";
                }
                where += fields[i] + "='" + values[i] + "' ";
            }
        }
        return where;
    }

    public void setProperty(String property, Object val) throws InvocationTargetException, IllegalAccessException {
        String methodName = property;
        methodName = buildMethodName("set", property);
        Method m = findSetterMethod(model(), methodName, val);
        m.invoke(model(), new Object[]{val});
    }

    private String buildMethodName(String prefix, String property) {
        return String.format("%s%c%s", prefix, Character.toUpperCase(property.charAt(0)), property.substring(1));
    }

    private Method findSetterMethod(Serializable serializable, String methodName, Object val) {
        for (Method m : serializable.getClass().getMethods()) {
            if (methodName.equals(m.getName())) {
                Class<?>[] params = m.getParameterTypes();
                if (params.length < 1) {
                    continue;
                }
                if (val == null) {
                    return m;
                }
                Class methodParam = params[0];
                if (methodParam.isPrimitive()) {
                    methodParam = translateFromPrimitive(methodParam);
                }
                if (methodParam.isAssignableFrom(val.getClass())) {
                    return m;
                }
            }
        }
        throw new IllegalArgumentException("Could not find setter " + methodName);
    }

    private static Class<?> translateFromPrimitive(Class<?> primitive) {
        if (!primitive.isPrimitive())
            return primitive;
        return Object.class;
    }

    protected String[] getIdentificatorFieldNames() {
        return new String[]{getIdentificatorFieldName()};
    }

    protected Serializable[] getIdentificatorValues() {
        return new Serializable[]{getIdentificatorValue()};
    }

    abstract protected String getIdentificatorFieldName();

    abstract protected Serializable getIdentificatorValue();

    abstract public Serializable model();

}
