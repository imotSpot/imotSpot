package com.imotspot.database;

import com.tinkerpop.blueprints.impls.orient.OrientGraph;

public abstract class DBOperation {
    abstract public void execute(OrientGraph graph);
}
