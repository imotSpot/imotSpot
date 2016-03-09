package com.imotspot.dagger;

import com.imotspot.config.Configuration;
import com.imotspot.dashboard.data.DataProvider;
import com.imotspot.dashboard.data.dummy.DummyDataProvider;
import com.imotspot.dashboard.event.DashboardEventBus;
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

    @Provides
    DashboardEventBus provideEventBus() {
        return new DashboardEventBus();
    }

    @Provides
    @Singleton
    DataProvider provideDummyDataProvider() {
        return new DummyDataProvider();
    }
}
