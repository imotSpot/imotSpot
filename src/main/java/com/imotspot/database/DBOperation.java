package com.imotspot.database;

import com.tinkerpop.blueprints.impls.orient.OrientGraph;

import java.net.URISyntaxException;

/**
 * A tsk that executes calls to the graph database.
 *
 * @param <E>
 */
public abstract class DBOperation<E extends Object> {
    abstract public <E> E execute(OrientGraph graph) throws URISyntaxException, Exception;
}
