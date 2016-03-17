package com.imotspot.database;

import com.imotspot.config.ConfigKey;
import com.imotspot.config.Configuration;
import com.imotspot.dagger.AppComponent;
import com.imotspot.dashboard.domain.imot.*;
import com.imotspot.database.model.vertex.LocationVertex;
import com.imotspot.template.FileDocument;
import com.imotspot.template.FileTemplate;
import com.orientechnologies.common.concur.ONeedRetryException;
import com.orientechnologies.orient.server.OServer;
import com.orientechnologies.orient.server.OServerMain;
import com.tinkerpop.blueprints.Element;
import com.tinkerpop.blueprints.impls.orient.OrientElement;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;

import javax.inject.Inject;

public class OrientDBServer {
    protected Configuration config;
    private OServer server;
    private OrientGraphFactory factory;
    private String databaseName;

    @Inject
    public OrientDBServer(Configuration config) {
        this.config = config;
        databaseName = this.config.getConfigAsString(ConfigKey.databaseName);
    }

    public static void main(String[] args) throws Exception {
        OrientDBServer orientDB = AppComponent.daggerInjector().dbServer();

        orientDB.start();

        orientDB.doInTX(new DBOperation() {
            @Override
            public OrientElement execute(OrientGraph graph) throws Exception {

                Location location = new Location();
                location.setAddress("sofia address");
                location.setCountry(new Country("Bulgaria"));
                location.setSity(new Sity("Sofia"));
                location.setDistrict(new District("Sofiiska"));
                LocationMarker marker = new LocationMarker(42.695537f, 23.2539071f);
                marker.setAddress("Sofia Bulgaria");
                marker.setName("Sofia Bulgaria");
                location.setMarker(marker);

                new LocationVertex(location).saveOrUpdate();

//                Vertex marko = graph.addVertex("class:Imot", "name", "app in sofia", "price", 290);
//
//                Iterable<Vertex> imotVertices = graph.getVertices();
//                System.out.println(imotVertices.iterator().next());
                return null;
            }
        });
    }

    /**
     * for the web studio to work you'll need:
     * 1) set databaseBaseDir to point to the orientdb dir or to where the databases and www dirs are
     * 2) unpack the ${databaseBaseDir}/plugins/studio-x.x.zip/www as ${databaseBaseDir}/www/studio
     */
    public void start() throws Exception {
        server = OServerMain.create();
        FileTemplate template = new FileTemplate(new FileDocument(config.getConfigAsString(ConfigKey.databaseConfigFileName)));
        server.startup(template.transform(config.asMap()));
        server.activate();
    }

    public void stop() throws Exception {
        server.shutdown();
    }

    public OrientGraphFactory getGraphFactory() {
        return getGraphFactory(databaseName);
    }

    public OrientGraphFactory getGraphFactory(String databaseName) {
        if (factory == null) {
            String databasePath = config.getConfigAsString(ConfigKey.databaseBaseDir) + config.getConfigAsString(ConfigKey.databasesDirName) + "/";
            factory = new OrientGraphFactory("plocal:" + databasePath + databaseName,
                    config.getConfigAsString(ConfigKey.databaseUsername), config.getConfigAsString(ConfigKey.databasePassword))
                    .setupPool(config.getConfigAsInteger(ConfigKey.databasePoolMinSize), config.getConfigAsInteger(ConfigKey.databasePoolMaxSize));
        }
        return factory;
    }

    public void releaseGraphFactory() {
        factory.close();
        factory = null;
    }

    public OrientGraph getGraph() {
        if (factory == null) {
            getGraphFactory();
        }
        return factory.getTx();
    }

    public OrientGraphNoTx getGraphNoTx() {
        if (factory == null) {
            getGraphFactory();
        }
        return factory.getNoTx();
    }

    public void doInTXWithRetry(DBOperation operation) throws Exception {
        doInTXWithRetry(operation, 3);
    }

    public void doInTXWithRetry(DBOperation operation, int maxRetries) throws Exception {
        OrientGraph graph = getGraph();
        for (int retry = 0; retry < maxRetries; ++retry) {
            try {
                operation.execute(graph);
                graph.commit();
                break;
            } catch (ONeedRetryException e) {
                // SOMEONE HAVE UPDATE THE INVOICE VERTEX AT THE SAME TIME, RETRY IT
            }
        }
    }

    public Element doInTX(DBOperation operation) {
        OrientGraph graph = getGraph();
        Element element = null;
        try {
            element = operation.execute(graph);
            graph.commit();
        } catch (Exception e) {
            graph.rollback();
        }
        return element;
    }
}
