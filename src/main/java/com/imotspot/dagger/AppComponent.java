package com.imotspot.dagger;

import com.imotspot.database.OrientDBServer;
import dagger.Component;

import javax.inject.Singleton;

@Singleton
@Component(modules = AppModule.class)
public interface AppComponent {
    OrientDBServer dbServer();
}
