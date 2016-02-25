package com.imotspot.database;

import com.imotspot.config.ConfigKey;
import com.imotspot.config.Configuration;
import com.imotspot.template.FileDocument;
import com.imotspot.template.FileTemplate;
import com.orientechnologies.orient.server.OServer;
import com.orientechnologies.orient.server.OServerMain;

public class OrientDBServer {
    protected static final Configuration config = Configuration.getDefaultConfig();
    private OServer server;

    public static void main(String[] args) throws Exception {
        OrientDBServer orientDB = new OrientDBServer();

        orientDB.start();

        while (true) {
            Thread.sleep(1000);
        }
    }

    public void start() throws Exception {

        server = OServerMain.create();
        FileTemplate template = new FileTemplate(new FileDocument(config.getConfigAsString(ConfigKey.databaseConfigFileName)));
        server.startup(template.transform(config.asMap()));
        server.activate();

    }

    public void stop() throws Exception {
        server.shutdown();
    }
}
