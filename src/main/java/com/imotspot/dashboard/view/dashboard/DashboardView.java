package com.imotspot.dashboard.view.dashboard;

import com.google.common.eventbus.Subscribe;
import com.imotspot.dashboard.DashboardUI;
import com.imotspot.dashboard.event.DashboardEvent.CloseOpenWindowsEvent;
import com.imotspot.dashboard.event.DashboardEvent.NotificationsCountUpdatedEvent;
import com.imotspot.dashboard.event.DashboardEventBus;
import com.imotspot.dashboard.view.property.AddProperty;
import com.imotspot.database.model.vertex.UserVertex;
import com.imotspot.interfaces.DashboardEditListener;
import com.imotspot.logging.Logger;
import com.imotspot.logging.LoggerFactory;
import com.imotspot.model.DashboardNotification;
import com.imotspot.model.User;
import com.imotspot.model.imot.Imot;
import com.imotspot.model.imot.Location;
import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Responsive;
import com.vaadin.server.VaadinSession;
import com.vaadin.tapio.googlemaps.GoogleMap;
import com.vaadin.tapio.googlemaps.client.LatLon;
import com.vaadin.tapio.googlemaps.client.overlays.GoogleMapMarker;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.themes.ValoTheme;

import java.util.Collection;
import java.util.Iterator;
import java.util.function.Consumer;

@SuppressWarnings("serial")
public final class DashboardView extends Panel implements View, DashboardEditListener{
    private static final Logger logger = LoggerFactory.getLogger(DashboardView.class);
    private static final LatLon centerSofia = new LatLon(42.697702770146975, 23.32174301147461);
    public static final String EDIT_ID = "dashboard-edit";
    public static final String TITLE_ID = "dashboard-title";

    private Label titleLabel;
    private NotificationsButton notificationsButton;
    private CssLayout dashboardPanels;
    private final VerticalLayout root;
    private Window notificationsWindow;
    private GoogleMap googleMap;

    public DashboardView() {
        addStyleName(ValoTheme.PANEL_BORDERLESS);
        setSizeFull();
        DashboardEventBus.register(this);

        root = new VerticalLayout();
        root.setSizeFull();
        root.setMargin(true);
        root.addStyleName("dashboard-view");
        setContent(root);
        Responsive.makeResponsive(root);

        root.addComponent(buildHeader());

        root.addComponent(buildSparklines());

        Component content = buildContent();
        root.addComponent(content);
        root.setExpandRatio(content, 1);

        // All the open sub-windows should be closed whenever the root layout
        // gets clicked.
        root.addLayoutClickListener(new LayoutClickListener() {
            @Override
            public void layoutClick(final LayoutClickEvent event) {
                DashboardEventBus.post(new CloseOpenWindowsEvent());
            }
        });
    }

    private Component buildSparklines() {
        CssLayout sparks = new CssLayout();
        sparks.addStyleName("sparks");
        sparks.setWidth("100%");
        Responsive.makeResponsive(sparks);

        return sparks;
    }

    private Component buildHeader() {
        HorizontalLayout header = new HorizontalLayout();
        header.addStyleName("viewheader");
        header.setSpacing(true);

        titleLabel = new Label("Map");
        titleLabel.setId(TITLE_ID);
        titleLabel.setSizeUndefined();
        titleLabel.addStyleName(ValoTheme.LABEL_H1);
        titleLabel.addStyleName(ValoTheme.LABEL_NO_MARGIN);
        header.addComponent(titleLabel);

        notificationsButton = buildNotificationsButton();
        Component edit = buildEditButton();
        HorizontalLayout tools = new HorizontalLayout(notificationsButton, edit);
        tools.setSpacing(true);
        tools.addStyleName("toolbar");
        header.addComponent(tools);

        return header;
    }

    private NotificationsButton buildNotificationsButton() {
        NotificationsButton result = new NotificationsButton();
        result.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(final ClickEvent event) {
                openNotificationsPopup(event);
            }
        });
        return result;
    }

    private Component buildEditButton() {
        Button result = new Button();
        result.setId(EDIT_ID);
        result.setIcon(FontAwesome.EDIT);
        result.addStyleName("icon-edit");
        result.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
        result.setDescription("Add Imot");
        result.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(final ClickEvent event) {
                getUI().addWindow(
                        new AddProperty(DashboardView.this, titleLabel
                                .getValue()));
            }
        });
        return result;
    }

    private Component buildContent() {
        dashboardPanels = new CssLayout();
        dashboardPanels.addStyleName("dashboard-panels");
        Responsive.makeResponsive(dashboardPanels);

        googleMap = new GoogleMap(null, null, null);
        googleMap.setCenter(centerSofia);
        googleMap.setSizeFull();
        googleMap.setImmediate(true);
        googleMap.setZoom(13);

        dashboardPanels.addComponent(googleMap);
        // zopim chat
        String script = "window.$zopim||(function(d,s){var z=$zopim=function(c){\n" +
                "z._.push(c)},$=z.s=\n" +
                "d.createElement(s),e=d.getElementsByTagName(s)[0];z.set=function(o){z.set.\n" +
                "_.push(o)};z._=[];z.set._=[];$.async=!0;$.setAttribute('charset','utf-8');\n" +
                "$.src='//v2.zopim.com/?3pEeNjxc1QHeNdL8pg6BPqFOTO6wReIb';z.t=+new Date;$.\n" +
                "type='text/javascript';e.parentNode.insertBefore($,e)})(document,'script');\n";
        JavaScript.getCurrent().execute(script);

//        dashboardPanels.addComponent(buildTopGrossingMovies());
//        dashboardPanels.addComponent(buildNotes());
//        dashboardPanels.addComponent(buildTop10TitlesByRevenue());
//        dashboardPanels.addComponent(buildPopularMovies());

        return dashboardPanels;
    }
//
//    private Component buildTopGrossingMovies() {
////        TopGrossingMoviesChart topGrossingMoviesChart = new TopGrossingMoviesChart();
////        topGrossingMoviesChart.setSizeFull();
//        return createContentWrapper(topGrossingMoviesChart);
//    }
//
//    private Component buildNotes() {
//        TextArea notes = new TextArea("Notes");
//        notes.setValue("Remember to:\n· Zoom in and out in the Sales view\n· Filter the transactions and drag a set of them to the Reports tab\n· Create a new report\n· Change the schedule of the movie theater");
//        notes.setSizeFull();
//        notes.addStyleName(ValoTheme.TEXTAREA_BORDERLESS);
//        Component panel = createContentWrapper(notes);
//        panel.addStyleName("notes");
//        return panel;
//    }
//
//    private Component buildTop10TitlesByRevenue() {
//        Component contentWrapper = createContentWrapper(new TopTenMoviesTable());
//        contentWrapper.addStyleName("top10-revenue");
//        return contentWrapper;
//    }
//
//    private Component buildPopularMovies() {
//        return createContentWrapper(new TopSixTheatersChart());
//    }
//
//    private Component createContentWrapper(final Component content) {
//        final CssLayout slot = new CssLayout();
//        slot.setWidth("100%");
//        slot.addStyleName("dashboard-panel-slot");
//
//        CssLayout card = new CssLayout();
//        card.setWidth("100%");
//        card.addStyleName(ValoTheme.LAYOUT_CARD);
//
//        HorizontalLayout toolbar = new HorizontalLayout();
//        toolbar.addStyleName("dashboard-panel-toolbar");
//        toolbar.setWidth("100%");
//
//        Label caption = new Label(content.getCaption());
//        caption.addStyleName(ValoTheme.LABEL_H4);
//        caption.addStyleName(ValoTheme.LABEL_COLORED);
//        caption.addStyleName(ValoTheme.LABEL_NO_MARGIN);
//        content.setCaption(null);
//
//        MenuBar tools = new MenuBar();
//        tools.addStyleName(ValoTheme.MENUBAR_BORDERLESS);
//        MenuItem max = tools.addItem("", FontAwesome.EXPAND, new Command() {
//
//            @Override
//            public void menuSelected(final MenuItem selectedItem) {
//                if (!slot.getStyleName().contains("max")) {
//                    selectedItem.setIcon(FontAwesome.COMPRESS);
//                    toggleMaximized(slot, true);
//                } else {
//                    slot.removeStyleName("max");
//                    selectedItem.setIcon(FontAwesome.EXPAND);
//                    toggleMaximized(slot, false);
//                }
//            }
//        });
//        max.setStyleName("icon-only");
//        MenuItem root = tools.addItem("", FontAwesome.COG, null);
//        root.addItem("Configure", new Command() {
//            @Override
//            public void menuSelected(final MenuItem selectedItem) {
//                Notification.show("Not implemented in this demo");
//            }
//        });
//        root.addSeparator();
//        root.addItem("Close", new Command() {
//            @Override
//            public void menuSelected(final MenuItem selectedItem) {
//                Notification.show("Not implemented in this demo");
//            }
//        });
//
//        toolbar.addComponents(caption, tools);
//        toolbar.setExpandRatio(caption, 1);
//        toolbar.setComponentAlignment(caption, Alignment.MIDDLE_LEFT);
//
//        card.addComponents(toolbar, content);
//        slot.addComponent(card);
//        return slot;
//    }

    private void openNotificationsPopup(final ClickEvent event) {
        VerticalLayout notificationsLayout = new VerticalLayout();
        notificationsLayout.setMargin(true);
        notificationsLayout.setSpacing(true);

        Label title = new Label("Notifications");
        title.addStyleName(ValoTheme.LABEL_H3);
        title.addStyleName(ValoTheme.LABEL_NO_MARGIN);
        notificationsLayout.addComponent(title);

        Collection<DashboardNotification> notifications = DashboardUI
                .getDataProvider().getNotifications();
        DashboardEventBus.post(new NotificationsCountUpdatedEvent());

        for (DashboardNotification notification : notifications) {
            VerticalLayout notificationLayout = new VerticalLayout();
            notificationLayout.addStyleName("notification-item");

            Label titleLabel = new Label(notification.getFirstName() + " "
                    + notification.getLastName() + " "
                    + notification.getAction());
            titleLabel.addStyleName("notification-title");

            Label timeLabel = new Label(notification.getPrettyTime());
            timeLabel.addStyleName("notification-time");

            Label contentLabel = new Label(notification.getContent());
            contentLabel.addStyleName("notification-content");

            notificationLayout.addComponents(titleLabel, timeLabel,
                    contentLabel);
            notificationsLayout.addComponent(notificationLayout);
        }

        HorizontalLayout footer = new HorizontalLayout();
        footer.addStyleName(ValoTheme.WINDOW_BOTTOM_TOOLBAR);
        footer.setWidth("100%");
        Button showAll = new Button("View All Notifications",
                new ClickListener() {
                    @Override
                    public void buttonClick(final ClickEvent event) {
                        Notification.show("Not implemented in this demo");
                    }
                });
        showAll.addStyleName(ValoTheme.BUTTON_BORDERLESS_COLORED);
        showAll.addStyleName(ValoTheme.BUTTON_SMALL);
        footer.addComponent(showAll);
        footer.setComponentAlignment(showAll, Alignment.TOP_CENTER);
        notificationsLayout.addComponent(footer);

        if (notificationsWindow == null) {
            notificationsWindow = new Window();
            notificationsWindow.setWidth(300.0f, Unit.PIXELS);
            notificationsWindow.addStyleName("notifications");
            notificationsWindow.setClosable(false);
            notificationsWindow.setResizable(false);
            notificationsWindow.setDraggable(false);
            notificationsWindow.setCloseShortcut(KeyCode.ESCAPE, null);
            notificationsWindow.setContent(notificationsLayout);
        }

        if (!notificationsWindow.isAttached()) {
            notificationsWindow.setPositionY(event.getClientY()
                    - event.getRelativeY() + 40);
            getUI().addWindow(notificationsWindow);
            notificationsWindow.focus();
        } else {
            notificationsWindow.close();
        }
    }

    @Override
    public void enter(final ViewChangeEvent event) {
        notificationsButton.updateNotificationsCount(null);
        addPins();
    }

    @Override
    public void dashboardNameEdited(final Imot imot) {
//        titleLabel.setValue(name);
        User user = (User) VaadinSession.getCurrent().getAttribute(User.class.getName());

        if (user.role().equals("guest")) {
            return;
        }
//        Location location = new Location();
//        location.setAddress("sofia address");
//        location.setCountry(new Country("Bulgaria"));
//        location.setCity(new City("Sofia"));
//        location.setDistrict(new District("Sofiiska"));
//        LocationMarker marker = new LocationMarker(42.695537f, 23.2539071f);
//        marker.setAddress("Sofia Bulgaria");
//        marker.setName("Sofia Bulgaria");
//        location.setMarker(marker);

//        Imot imot = new Imot(location);
//        imot.setOwner(user);
//        imot.setPrice(100);
//        imot.setPublished(java.util.Calendar.getInstance().getTime());
//        imot.setYear("1960");
//        imot.setDescription(imot);
//        imot.setCondition(Condition.USED);
//            imot.setFrontImage(new Picture(new URI("./pic.jpg")));
//        LocationMarker myMarker = imot.getLocation().getMarker();
        GoogleMapMarker marker = imot.getLocation().marker().googleMarker();
        googleMap.addMarker(marker);
        googleMap.setCenter(centerSofia);

        user.addImot(imot);
        new UserVertex(user).saveOrUpdateInNewTX();
    }

    private void toggleMaximized(final Component panel, final boolean maximized) {
        for (Iterator<Component> it = root.iterator(); it.hasNext(); ) {
            it.next().setVisible(!maximized);
        }
        dashboardPanels.setVisible(true);

        for (Iterator<Component> it = dashboardPanels.iterator(); it.hasNext(); ) {
            Component c = it.next();
            c.setVisible(!maximized);
        }

        if (maximized) {
            panel.setVisible(true);
            panel.addStyleName("max");
        } else {
            panel.removeStyleName("max");
        }
    }

    @Override
    public void forEach(Consumer<? super Component> action) {

    }

    public static final class NotificationsButton extends Button {
        private static final String STYLE_UNREAD = "unread";
        public static final String ID = "dashboard-notifications";

        public NotificationsButton() {
            setIcon(FontAwesome.BELL);
            setId(ID);
            addStyleName("notifications");
            addStyleName(ValoTheme.BUTTON_ICON_ONLY);
            DashboardEventBus.register(this);
        }

        @Subscribe
        public void updateNotificationsCount(
                final NotificationsCountUpdatedEvent event) {
            setUnreadCount(DashboardUI.getDataProvider()
                    .getUnreadNotificationsCount());
        }

        public void setUnreadCount(final int count) {
            setCaption(String.valueOf(count));

            String description = "Notifications";
            if (count > 0) {
                addStyleName(STYLE_UNREAD);
                description += " (" + count + " unread)";
            } else {
                removeStyleName(STYLE_UNREAD);
            }
            setDescription(description);
        }
    }

    public void addPins() {

        User user = (User) VaadinSession.getCurrent().getAttribute(User.class.getName());
        for (Imot i : user.imots()) {
            Location loc = i.getLocation();
            googleMap.addMarker(new GoogleMapMarker(null,
                    new LatLon(loc.marker().lat(), loc.marker().lng()), true));
        }

    }
}
