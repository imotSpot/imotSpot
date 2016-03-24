package com.imotspot.database;

import com.tinkerpop.blueprints.impls.orient.OrientGraph;

import java.net.URISyntaxException;

public abstract class DBOperation<E extends Object> {
    abstract public <E> E execute(OrientGraph graph) throws URISyntaxException, Exception;
}
