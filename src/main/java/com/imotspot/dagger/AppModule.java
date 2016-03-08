package com.imotspot.dagger;

import com.imotspot.config.Configuration;
import com.imotspot.database.OrientDBServer;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

@Module
public class AppModule {

    @Provides
    @Singleton
    Configuration provideConfiguration() {
        return Configuration.getDefaultConfig();
    }

    @Provides
    @Singleton
    OrientDBServer provideDBServer(Configuration configuration) {
        return new OrientDBServer(configuration);
    }
}
