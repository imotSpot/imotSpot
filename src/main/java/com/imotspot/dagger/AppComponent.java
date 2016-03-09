package com.imotspot.dagger;

import com.imotspot.dashboard.DashboardServlet;
import com.imotspot.dashboard.DashboardUI;
import com.imotspot.dashboard.data.DataProvider;
import com.imotspot.dashboard.event.DashboardEventBus;
import com.imotspot.database.OrientDBServer;
import dagger.Component;

import javax.inject.Singleton;

@Singleton
@Component(modules = AppModule.class)
public interface AppComponent {
    AppComponent appComponent = DaggerAppComponent.builder().appModule(new AppModule()).build();

    OrientDBServer dbServer();

    DashboardEventBus dashboardEventBus();

    DataProvider dataProvider();

    void inject(DashboardUI dashboardUI);

    void inject(DashboardServlet dashboardServlet);

    static AppComponent daggerInjector() {
        return appComponent;
    }
}
