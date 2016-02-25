package com.imotspot.config;

public enum ConfigKey {
    embeddedDB(true),
    databaseBaseDir("../orientdb-community-2.1.11"),
    databasesDirName("databases"),
    databasesWebStudioDirName("www"),
    databaseConfigFileName("orientdb/orientdb-embed-config.xml");

    private Object value;

    ConfigKey(Object value) {
        this.value = value;
    }

    public Object value() {
        return value;
    }
}