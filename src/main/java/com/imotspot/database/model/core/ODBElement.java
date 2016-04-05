package com.imotspot.database.model.core;

import com.imotspot.database.DBOperation;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Base abstract class for Vertex and Edge, adding basic functionality for talking to the database.
 *
 * @param <T> The project model that this element is proxy for.
 */
public abstract class ODBElement<T extends Serializable> extends ODBAbstract {

    public static Class modelClass;
    public static String NAME;
    public static String PARENT_NAME;

    public ODBElement() {
        super();

        this.modelClass = getClass();
        NAME = getClass().getSimpleName();
        PARENT_NAME = getClass().getSuperclass().getSimpleName();
    }

    public <E extends ODBElement> E saveOrUpdateInNewTX() {
        return dbServer.doInTX(new DBOperation<E>() {
            @Override
            public <E> E execute(OrientGraph graph) {
                useGraph(graph);
                try {
                    return (E) saveOrUpdate();
                } finally {
                    useGraph(null);
                }
            }
        });
    }

    public <E extends ODBElement> E loadInNewTX() {
        return dbServer.doInTX(new DBOperation<E>() {
            @Override
            public <E> E execute(OrientGraph graph) {
                useGraph(graph);
                try {
                    return (E) load();
                } finally {
                    useGraph(null);
                }
            }
        });
    }

    protected abstract <E extends ODBElement> E duplicate();

    abstract public <E extends ODBElement> E load();

    abstract public <E extends ODBElement> E load(ODocument oElement);

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

    public void setProperty(String property, Object val) throws InvocationTargetException, IllegalAccessException {
        String methodName = property;
        methodName = buildMethodName("set", property);
        Method m = findSetterMethod(model(), methodName, val);
        m.invoke(model(), new Object[]{val});
    }

    public Object getProperty(String property) throws InvocationTargetException, IllegalAccessException {
        String methodName = property;
        methodName = buildMethodName("get", property);
        Method m = findGetterMethod(model(), methodName);
        return m.invoke(model());
    }

    private String buildMethodName(String prefix, String property) {
        return String.format("%s%c%s", prefix, Character.toUpperCase(property.charAt(0)), property.substring(1));
    }

    private Method findGetterMethod(Serializable serializable, String methodName) {
        for (Method m : serializable.getClass().getMethods()) {
            if (methodName.equals(m.getName())) {
                Class<?>[] params = m.getParameterTypes();
                if (params.length < 1) {
                    return m;
                }
            }
        }
        throw new IllegalArgumentException("Could not find getter " + methodName);
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
