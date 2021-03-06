package com.imotspot.dashboard.view.transactions;

import com.google.common.eventbus.Subscribe;
import com.imotspot.dashboard.DashboardUI;
import com.imotspot.dashboard.component.MovieDetailsWindow;
import com.imotspot.dashboard.event.DashboardEvent.BrowserResizeEvent;
import com.imotspot.dashboard.event.DashboardEventBus;
import com.imotspot.model.imot.Imot;
import com.imotspot.model.imot.Picture;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.Container.Filterable;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.Action;
import com.vaadin.event.Action.Handler;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.event.ShortcutListener;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.server.Responsive;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.Table.TableDragMode;
import com.vaadin.ui.themes.ValoTheme;
import org.vaadin.maddon.FilterableListContainer;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Set;

@SuppressWarnings({ "serial", "unchecked" })
public final class TransactionsView extends VerticalLayout implements View {

    private final Table table;
    private Button createReport;
    private static final DateFormat DATEFORMAT = new SimpleDateFormat(
            "MM/dd/yyyy hh:mm:ss a");
    private static final DecimalFormat DECIMALFORMAT = new DecimalFormat("#.##");
    private static final String[] DEFAULT_COLLAPSIBLE = {"description"};

    public TransactionsView() throws Exception {
        setSizeFull();
        addStyleName("transactions");
        DashboardEventBus.register(this);

        addComponent(buildToolbar());

        table = buildTable();
        addComponent(table);
        setExpandRatio(table, 1);
    }

    @Override
    public void detach() {
        super.detach();
        // A new instance of TransactionsView is created every time it's
        // navigated to so we'll need to clean up references to it on detach.
        DashboardEventBus.unregister(this);
    }

    private Component buildToolbar() {
        HorizontalLayout header = new HorizontalLayout();
        header.addStyleName("viewheader");
        header.setSpacing(true);
        Responsive.makeResponsive(header);

        Label title = new Label("Imot list");
        title.setSizeUndefined();
        title.addStyleName(ValoTheme.LABEL_H1);
        title.addStyleName(ValoTheme.LABEL_NO_MARGIN);
        header.addComponent(title);

        createReport = buildCreateReport();
        HorizontalLayout tools = new HorizontalLayout(buildFilter(),
                createReport);
        tools.setSpacing(true);
        tools.addStyleName("toolbar");
        header.addComponent(tools);

        return header;
    }

    private Button buildCreateReport() {
        final Button createReport = new Button("Search property");
        createReport
                .setDescription("Search by property");
        createReport.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(final ClickEvent event) {
//                createNewReportFromSelection();
            }
        });
        createReport.setEnabled(false);
        return createReport;
    }

    private Component buildFilter() {
        final TextField filter = new TextField();
        filter.addTextChangeListener(new TextChangeListener() {
            @Override
            public void textChange(final TextChangeEvent event) {
                Filterable data = (Filterable) table.getContainerDataSource();
                data.removeAllContainerFilters();
                data.addContainerFilter(new Filter() {
                    @Override
                    public boolean passesFilter(final Object itemId,
                            final Item item) {

                        if (event.getText() == null
                                || event.getText().equals("")) {
                            return true;
                        }

                        return filterByProperty("price", item,
                                event.getText())
                                || filterByProperty("description", item,
                                        event.getText())
                                || filterByProperty("year", item,
                                        event.getText());

                    }

                    @Override
                    public boolean appliesToProperty(final Object propertyId) {
                        if (propertyId.equals("price")
                                || propertyId.equals("description")
                                || propertyId.equals("year")) {
                            return true;
                        }
                        return false;
                    }
                });
            }
        });

        filter.setInputPrompt("Filter");
        filter.setIcon(FontAwesome.SEARCH);
        filter.addStyleName(ValoTheme.TEXTFIELD_INLINE_ICON);
        filter.addShortcutListener(new ShortcutListener("Clear",
                KeyCode.ESCAPE, null) {
            @Override
            public void handleAction(final Object sender, final Object target) {
                filter.setValue("");
                ((Filterable) table.getContainerDataSource())
                        .removeAllContainerFilters();
            }
        });
        return filter;
    }

    private Table buildTable() throws Exception {
        final Table table = new Table() {
            @Override
            protected String formatPropertyValue(final Object rowId,
                    final Object colId, final Property<?> property) {
                String result = super.formatPropertyValue(rowId, colId, property);
                if (colId.equals("time")) {
                    result = DATEFORMAT.format(((Date) property.getValue()));
                } else if (colId.equals("price")) {
                    if (property != null && property.getValue() != null) {
                        return "$" + DECIMALFORMAT.format(property.getValue());
                    } else {
                        return "";
                    }
                } else if(colId.equals("type")){
                    Object val = property.getValue();
                    if (val == null) {
                        result = "";
                    } else {
                        result = val.toString();
                    }
                } else if(colId.equals("frontImage")){
                    Object val = property.getValue();
                    if (val == null) {
                        result = "";
                    } else {
                        StreamResource.StreamSource imageSource = new StreamResource.StreamSource() {
                            @Override
                            public InputStream getStream() {
                                return new ByteArrayInputStream(((Picture) val).imageAsArray());
                            }
                        };

                        StreamResource imageResource = new StreamResource(imageSource, "");
                        Embedded image = new Embedded("", imageResource);
                        result = image.toString();

                        Image imageView = new Image();
                        imageView.setWidth("90px");
                        imageView.setHeight("90px");
                        imageView.setSource(imageResource);
                    }

                }
                return result;
            }
        };
        table.setSizeFull();
        table.addStyleName(ValoTheme.TABLE_BORDERLESS);
        table.addStyleName(ValoTheme.TABLE_NO_HORIZONTAL_LINES);
        table.addStyleName(ValoTheme.TABLE_COMPACT);
        table.setSelectable(true);

        table.setColumnCollapsingAllowed(true);
//        table.setColumnCollapsible("time", false);
//        table.setColumnCollapsible("price", false);

        table.setColumnReorderingAllowed(true);
        table.setContainerDataSource(new TempTransactionsContainer(DashboardUI
                .getDataProvider().getRecentImots(200)));
        table.setSortContainerPropertyId("time");
        table.setSortAscending(false);

        table.setColumnAlignment("price", Align.RIGHT);

        table.setVisibleColumns("frontImage", "type", "price", "year", "description", "published");
        table.setColumnHeaders("Image", "Type", "Price", "Year", "Description", "Published");

        table.setFooterVisible(true);
        table.setColumnFooter("price", "Total");

        table.setColumnFooter(
                "price",
                "$"
                        + DECIMALFORMAT.format(DashboardUI.getDataProvider()
                                .getTotalSum()));

        // Allow dragging items to the reports menu
        table.setDragMode(TableDragMode.MULTIROW);
        table.setMultiSelect(true);

        table.addActionHandler(new TransactionsActionHandler());

        table.addValueChangeListener(new ValueChangeListener() {
            @Override
            public void valueChange(final ValueChangeEvent event) {
                if (table.getValue() instanceof Set) {
                    Set<Object> val = (Set<Object>) table.getValue();
                    createReport.setEnabled(val.size() > 0);
                }
            }
        });
        table.setImmediate(true);

        return table;
    }

    private boolean defaultColumnsVisible() {
        boolean result = true;
        for (String propertyId : DEFAULT_COLLAPSIBLE) {
            if (table.isColumnCollapsed(propertyId) == Page.getCurrent()
                    .getBrowserWindowWidth() < 800) {
                result = false;
            }
        }
        return result;
    }

    @Subscribe
    public void browserResized(final BrowserResizeEvent event) {
        // Some columns are collapsed when browser window width gets small
        // enough to make the table fit better.
        if (defaultColumnsVisible()) {
            for (String propertyId : DEFAULT_COLLAPSIBLE) {
                table.setColumnCollapsed(propertyId, Page.getCurrent()
                        .getBrowserWindowWidth() < 800);
            }
        }
    }

    private boolean filterByProperty(final String prop, final Item item,
            final String text) {
        if (item == null || item.getItemProperty(prop) == null
                || item.getItemProperty(prop).getValue() == null) {
            return false;
        }
        String val = item.getItemProperty(prop).getValue().toString().trim()
                .toLowerCase();
        if (val.contains(text.toLowerCase().trim())) {
            return true;
        }
        return false;
    }

    void createNewReportFromSelection() {
//        UI.getCurrent().getNavigator()
//                .navigateTo(DashboardViewType.REPORTS.getViewName());
//        DashboardEventBus.post(new TransactionReportEvent(
//                (Collection<Transaction>) table.getValue()));
    }

    @Override
    public void enter(final ViewChangeEvent event) {
    }

    private class TransactionsActionHandler implements Handler {
        private final Action report = new Action("Create Report");

        private final Action discard = new Action("Discard");

        private final Action details = new Action("Imot details");

        @Override
        public void handleAction(final Action action, final Object sender,
                final Object target) {
            if (action == report) {
//                createNewReportFromSelection();
            } else if (action == discard) {
                Notification.show("Not implemented in this demo");
            } else if (action == details) {
                Item item = ((Table) sender).getItem(target);
                if (item != null) {
                    Long movieId = (Long) item.getItemProperty("movieId")
                            .getValue();
                    MovieDetailsWindow.open(DashboardUI.getDataProvider()
                            .getMovie(movieId), null, null);
                }
            }
        }

        @Override
        public Action[] getActions(final Object target, final Object sender) {
            return new Action[] { details, report, discard };
        }
    }

    private class TempTransactionsContainer extends
            FilterableListContainer<Imot> {

        public TempTransactionsContainer(
                final Collection<Imot> collection) {
            super(collection);
        }

        // This is only temporarily overridden until issues with
        // BeanComparator get resolved.
        @Override
        public void sort(final Object[] propertyId, final boolean[] ascending) {
            final boolean sortAscending = ascending[0];
            final Object sortContainerPropertyId = propertyId[0];
//            Collections.sort(getBackingList(), new Comparator<Transaction>() {
//                @Override
//                public int compare(final Transaction o1, final Transaction o2) {
//                    int result = 0;
//                    if ("time".equals(sortContainerPropertyId)) {
//                        result = o1.getTime().compareTo(o2.getTime());
//                    } else if ("country".equals(sortContainerPropertyId)) {
//                        result = o1.getCountry().compareTo(o2.getCountry());
//                    } else if ("city".equals(sortContainerPropertyId)) {
//                        result = o1.getCity().compareTo(o2.getCity());
//                    } else if ("theater".equals(sortContainerPropertyId)) {
//                        result = o1.getTheater().compareTo(o2.getTheater());
//                    } else if ("room".equals(sortContainerPropertyId)) {
//                        result = o1.getRoom().compareTo(o2.getRoom());
//                    } else if ("title".equals(sortContainerPropertyId)) {
//                        result = o1.getTitle().compareTo(o2.getTitle());
//                    } else if ("seats".equals(sortContainerPropertyId)) {
//                        result = new Integer(o1.getSeats()).compareTo(o2
//                                .getSeats());
//                    } else if ("price".equals(sortContainerPropertyId)) {
//                        result = new Double(o1.getPrice()).compareTo(o2
//                                .getPrice());
//                    }
//
//                    if (!sortAscending) {
//                        result *= -1;
//                    }
//                    return result;
//                }
//            });
        }
    }
}
