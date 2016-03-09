package com.imotspot.dashboard;

import com.imotspot.dagger.AppModule;
import com.imotspot.dagger.DaggerAppComponent;
import com.imotspot.database.OrientDBServer;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinServlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;

@SuppressWarnings("serial")
@WebServlet(urlPatterns = "/*")
@VaadinServletConfiguration(ui = DashboardUI.class, productionMode = false, widgetset = "com.imotspot.dashboard.GoogleMapWidgetSet")
public class DashboardServlet extends VaadinServlet {

    private OrientDBServer dbServer;

    @Override
    protected final void servletInitialized() throws ServletException {
        super.servletInitialized();

        startDatabase();

        getService().addSessionInitListener(new DashboardSessionInitListener());
    }

    private void startDatabase() throws ServletException {
        dbServer = DaggerAppComponent.builder().appModule(new AppModule()).build().dbServer();
        try {
            dbServer.start();
        } catch (Exception e) {
            throw new ServletException(e.getMessage());
        }
    }
}