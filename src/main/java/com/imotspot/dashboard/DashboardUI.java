package com.imotspot.dashboard;

import com.google.common.eventbus.Subscribe;
import com.imotspot.dagger.AppComponent;
import com.imotspot.dashboard.data.DataProvider;
import com.imotspot.dashboard.event.DashboardEvent.BrowserResizeEvent;
import com.imotspot.dashboard.event.DashboardEvent.CloseOpenWindowsEvent;
import com.imotspot.dashboard.event.DashboardEvent.UserLoggedOutEvent;
import com.imotspot.dashboard.event.DashboardEvent.UserLoginRequestedEvent;
import com.imotspot.dashboard.event.DashboardEventBus;
import com.imotspot.dashboard.view.MainView;
import com.imotspot.model.User;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.server.Page;
import com.vaadin.server.Page.BrowserWindowResizeEvent;
import com.vaadin.server.Page.BrowserWindowResizeListener;
import com.vaadin.server.Responsive;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import javax.inject.Inject;
import java.util.Locale;

@Theme("dashboard")
//@Widgetset("com.vaadin.demo.dashboard.DashboardWidgetSet")
@Title("Imot Spot Dashboard")
@SuppressWarnings("serial")
public final class DashboardUI extends UI {

    /*
     * This field stores an access to the dummy backend layer. In real
     * applications you most likely gain access to your beans trough lookup or
     * injection; and not in the UI but somewhere closer to where they're
     * actually accessed.
     */
    private final DataProvider dataProvider;
    private final DashboardEventBus dashboardEventbus;

    public DashboardUI() {
        this(AppComponent.daggerInjector().dashboardEventBus(), AppComponent.daggerInjector().dataProvider());
    }

    @Inject
    public DashboardUI(DashboardEventBus dashboardEventbus, DataProvider dataProvider) {
        this.dashboardEventbus = dashboardEventbus;
        this.dataProvider = dataProvider;
    }

    @Override
    protected void init(final VaadinRequest request) {
        setLocale(Locale.US);

        DashboardEventBus.register(this);
        Responsive.makeResponsive(this);
        addStyleName(ValoTheme.UI_WITH_MENU);

        updateContent();

        // Some views need to be aware of browser resize events so a
        // BrowserResizeEvent gets fired to the event bus on every occasion.
        Page.getCurrent().addBrowserWindowResizeListener(
                new BrowserWindowResizeListener() {
                    @Override
                    public void browserWindowResized(
                            final BrowserWindowResizeEvent event) {
                        DashboardEventBus.post(new BrowserResizeEvent());
                    }
                });
    }

    /**
     * Updates the correct content for this UI based on the current user status.
     * If the user is logged in with appropriate privileges, main view is shown.
     * Otherwise login view is shown.
     */
    private void updateContent() {
        User user = (User) VaadinSession.getCurrent().getAttribute(
                User.class.getName());
        if (user == null) {
            user = new User("");
            user.setFirstName("guest");
            user.setLastName("");
            user.setRole("guest");
            VaadinSession.getCurrent().setAttribute(User.class.getName(), user);
        }
            setContent(new MainView());
            removeStyleName("loginview");
            getNavigator().navigateTo(getNavigator().getState());
//        } else {
//            setContent(new LoginView());
//            addStyleName("loginview");
//        }
    }

    @Subscribe
    public void userLoginRequested(final UserLoginRequestedEvent event) {
        User user = getDataProvider().authenticate(event.getUserName(),
                event.getPassword());
        VaadinSession.getCurrent().setAttribute(User.class.getName(), user);
        updateContent();
    }

    @Subscribe
    public void userLoggedOut(final UserLoggedOutEvent event) {
        // When the user logs out, current VaadinSession gets closed and the
        // page gets reloaded on the login screen. Do notice the this doesn't
        // invalidate the current HttpSession.
        VaadinSession.getCurrent().close();
        Page.getCurrent().reload();
    }

    @Subscribe
    public void closeOpenWindows(final CloseOpenWindowsEvent event) {
        for (Window window : getWindows()) {
            window.close();
        }
    }

    /**
     * @return An instance for accessing the (dummy) services layer.
     */
    public static DataProvider getDataProvider() {
        return ((DashboardUI) getCurrent()).dataProvider;
    }

    public static DashboardEventBus getDashboardEventbus() {
        return ((DashboardUI) getCurrent()).dashboardEventbus;
    }
}
