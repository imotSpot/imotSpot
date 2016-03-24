package com.imotspot.database;

import com.imotspot.enumerations.ConfigKey;
import com.imotspot.config.Configuration;
import com.imotspot.interfaces.AppComponent;
import com.imotspot.database.model.vertex.UserVertex;
import com.imotspot.enumerations.Condition;
import com.imotspot.logging.Logger;
import com.imotspot.logging.LoggerFactory;
import com.imotspot.model.User;
import com.imotspot.model.UserBean;
import com.imotspot.model.imot.*;
import com.imotspot.template.FileDocument;
import com.imotspot.template.FileTemplate;
import com.orientechnologies.common.concur.ONeedRetryException;
import com.orientechnologies.orient.server.OServer;
import com.orientechnologies.orient.server.OServerMain;
import com.tinkerpop.blueprints.impls.orient.OrientElement;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;
import com.vaadin.tapio.googlemaps.client.LatLon;
import com.vaadin.tapio.googlemaps.client.overlays.GoogleMapMarker;

import javax.inject.Inject;
import java.net.URI;
import java.util.Calendar;

public class OrientDBServer {
    private static final Logger logger = LoggerFactory.getLogger(OrientDBServer.class);

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

                User user = UserBean.builder()
                        .oauthIdentifier("test")
                        .firstName("test")
                        .build();
                UserVertex userVertex = new UserVertex(user);

                Location location = new Location();
                location.address("sofia address");
                location.country(new Country("Bulgaria"));
                location.city(new City("Sofia"));
                location.district(new District("Sofiiska"));
                LocationMarker marker = new LocationMarker(new GoogleMapMarker("test", new LatLon(42.695537, 23.2539071), false));
//                marker.address("Sofia Bulgaria");
//                marker.name("Sofia Bulgaria");
                location.marker(marker);

                Imot imot = new Imot(location);
                imot.owner(user);
                imot.price(100);
                imot.published(Calendar.getInstance().getTime());
                imot.year("1960");
                imot.description("test real estate imot");
                imot.condition(Condition.USED);
                imot.frontImage(new Picture(new URI("./pic.jpg")));

                user.addImot(imot);
                userVertex.saveOrUpdate();

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

    public OrientGraph graph() {
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
        OrientGraph graph = graph();
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

    public <E extends Object> E doInTX(DBOperation<E> operation) {
        OrientGraph graph = graph();
        E element = null;
        try {
            element = operation.execute(graph);
            graph.commit();
        } catch (Exception e) {
            logger.error("", e);
            graph.rollback();
        }
        return element;
    }
}
