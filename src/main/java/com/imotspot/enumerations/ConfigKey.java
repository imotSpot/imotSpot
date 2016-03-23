package com.imotspot.enumerations;

public enum ConfigKey {
    embeddedDB(true),
    databaseName("imotSpot"),
    databasePoolMinSize(1),
    databasePoolMaxSize(10),
    databaseBaseDir("../orientdb-community-2.1.12/"),
    databasesDirName("databases"),
    databasesWebStudioDirName("www"),
    databaseConfigFileName("orientdb/orientdb-embed-config.xml"),
    databaseUsername("admin"),
    databasePassword("admin");

    private Object value;

    ConfigKey(Object value) {
        this.value = value;
    }

    public Object value() {
        return value;
    }
}