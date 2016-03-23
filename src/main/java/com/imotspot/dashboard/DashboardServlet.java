package com.imotspot.dashboard;

import com.imotspot.interfaces.AppComponent;
import com.imotspot.database.OrientDBServer;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinServlet;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;

@SuppressWarnings("serial")
@WebServlet(urlPatterns = "/*")
@VaadinServletConfiguration(ui = DashboardUI.class, productionMode = false, widgetset = "com.imotspot.dashboard.GoogleMapWidgetSet")
public class DashboardServlet extends VaadinServlet {

    @Inject
    public OrientDBServer dbServer;

    @Override
    protected final void servletInitialized() throws ServletException {
        super.servletInitialized();

        AppComponent.daggerInjector().inject(this);
        startDatabase();

        getService().addSessionInitListener(new DashboardSessionInitListener());
    }

    private void startDatabase() throws ServletException {
        try {
            dbServer.start();
        } catch (Exception e) {
            throw new ServletException(e.getMessage());
        }
    }
}