package com.imotspot.database;

import com.tinkerpop.blueprints.Element;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;

import java.net.URISyntaxException;

public abstract class DBOperation {
    abstract public Element execute(OrientGraph graph) throws URISyntaxException, Exception;
}
