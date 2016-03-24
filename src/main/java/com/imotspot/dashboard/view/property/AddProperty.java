package com.imotspot.dashboard.view.property;


import com.imotspot.enumerations.Condition;
import com.imotspot.enumerations.ImotType;
import com.imotspot.googlemap.Geocoding;
import com.imotspot.googlemap.json.GeocodingAnswer;
import com.imotspot.googlemap.json.LocationAnswer;
import com.imotspot.helper.ImageUploader;
import com.imotspot.interfaces.DashboardEditListener;
import com.imotspot.model.User;
import com.imotspot.model.imot.*;
import com.vaadin.data.Property;
import com.vaadin.event.ShortcutAction;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.tapio.googlemaps.GoogleMap;
import com.vaadin.tapio.googlemaps.client.LatLon;
import com.vaadin.tapio.googlemaps.client.events.MapClickListener;
import com.vaadin.tapio.googlemaps.client.overlays.GoogleMapMarker;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@SuppressWarnings("serial")
public class AddProperty extends Window {

    private final DashboardEditListener listener;
    private static final LatLon centerSofia = new LatLon(42.697702770146975, 23.32174301147461);
    private TextField price;
    private TextField year;
    private TextArea description;
    private TextField country;
    private TextField district;
    private TextField city;
    private TextField address;
    private ComboBox condition;
    private ComboBox type;
    private TextField images;
    private GoogleMap googleMap;

    public AddProperty(final DashboardEditListener listener,
                         final String currentName) {
        this.listener = listener;
        setCaption("Add Property");
        setModal(true);
        setClosable(true);
        setResizable(true);

        addStyleName("edit-dashboard");

        setContent(buildContent(currentName));
    }

    private Component buildContent(final String currentName) {
        VerticalLayout verticalContent = new VerticalLayout();
        verticalContent.setMargin(true);
        verticalContent.setSpacing(true);
        verticalContent.setCaption(currentName);

        HorizontalLayout content = new HorizontalLayout();
        content.addComponent(buildAddForm());
        content.addComponent(buildGoogleMap());

        verticalContent.addComponent(content);
        verticalContent.addComponent(buildFooter());

        return verticalContent;
    }

    private Component buildGoogleMap() {
        HorizontalLayout mapLayout = new HorizontalLayout();
        mapLayout.setWidth(500f, Unit.PIXELS);
        mapLayout.setHeight(500f, Unit.PIXELS);

        googleMap = new GoogleMap(null, null, null);
        googleMap.setCenter(centerSofia);
        googleMap.setSizeFull();
        googleMap.setImmediate(true);
        googleMap.setMinZoom(4);
        googleMap.addMapClickListener(new MapClickListener() {
            @Override
            public void mapClicked(LatLon latLon) {
                try {
                    GeocodingAnswer answer = Geocoding.getJSONFromLatLon(latLon);
                    String addressFromMap = answer.results[0].formatted_address;
                    address.setValue(addressFromMap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        mapLayout.addComponent(googleMap);
        return mapLayout;
    }

    private Component buildAddForm() {
        VerticalLayout content = new VerticalLayout();

        List<ImotType> enumImotTypeList = Arrays.asList(ImotType.values());
        List<Condition> enumConditionList = Arrays.asList(Condition.values());
        ImageUploader receiver = new ImageUploader();

        type = new ComboBox("Choose type");
        type.setFilteringMode(FilteringMode.STARTSWITH);
        for (ImotType t: enumImotTypeList){
            type.addItem(t.toString());
        }

        price = new TextField("price");
        year = new TextField("year");
        description = new TextArea("description");
        country = new TextField("country");
        district = new TextField("district");
        city = new TextField("city");
        address = new TextField("address");

        condition = new ComboBox("condition");
        condition.setFilteringMode(FilteringMode.STARTSWITH);
        for (Condition c: enumConditionList){
            condition.addItem(c.toString());
        }

        Upload uploadImages = new Upload("Upload images", receiver);
        uploadImages.setButtonCaption("Upload images");
        uploadImages.addSucceededListener(receiver);
//        DragAndDropWrapper drag = new DragAndDropWrapper(uploadImages);

        price.addStyleName("caption-on-left");
        year.addStyleName("caption-on-left");
        description.addStyleName("caption-on-left");
        country.addStyleName("caption-on-left");
        district.addStyleName("caption-on-left");
        city.addStyleName("caption-on-left");
        address.addStyleName("caption-on-left");
        condition.addStyleName("caption-on-left");

        address.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent valueChangeEvent) {
                String text = valueChangeEvent.getProperty().getValue().toString();
                try {
                    GeocodingAnswer answer = Geocoding.getJSONfromAddress(text);
                    LocationAnswer loc = null;
                    if (answer.results.length != 0) {
                        loc = answer.results[0].geometry.location;
                        LatLon latLon = new LatLon(loc.lat, loc.lng);
                        GoogleMapMarker marker = new GoogleMapMarker(text, latLon, true);
                        googleMap.addMarker(marker);
                        googleMap.setCenter(latLon);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        content.addComponents(type, price, year, description, country, district, city, address, condition, uploadImages);
        return content;
    }

    private Component buildFooter() {
        HorizontalLayout footer = new HorizontalLayout();
        footer.setSpacing(true);
        footer.addStyleName(ValoTheme.WINDOW_BOTTOM_TOOLBAR);
        footer.setWidth(100.0f, Unit.PERCENTAGE);

        Button cancel = new Button("Cancel");
        cancel.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(final Button.ClickEvent event) {
                close();
            }
        });
        cancel.setClickShortcut(ShortcutAction.KeyCode.ESCAPE, null);

        Button save = new Button("Save");
        save.addStyleName(ValoTheme.BUTTON_PRIMARY);
        save.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(final Button.ClickEvent event) {
                Location currentLocation = new Location();
                currentLocation.country(new Country(country.getValue()));
                currentLocation.district(new District(district.getValue()));
                currentLocation.city(new City(city.getValue()));
                currentLocation.address(address.getValue());
                currentLocation.marker(new LocationMarker(googleMap.getMarkers().iterator().next()));

                Imot imotToSave = new Imot(currentLocation);
                imotToSave.type(ImotType.valueOf(type.getValue().toString()));
                imotToSave.price(Float.parseFloat(price.getValue()));
                imotToSave.year(year.getValue());
                imotToSave.description(description.getValue());
                imotToSave.condition(Condition.valueOf(condition.getValue().toString()));
                imotToSave.owner((User) VaadinSession.getCurrent().getAttribute(User.class.getName()));
                imotToSave.published(new Date());

                listener.dashboardNameEdited(imotToSave);
                close();
            }
        });
        save.setClickShortcut(ShortcutAction.KeyCode.ENTER, null);

        footer.addComponents(cancel, save);
        footer.setExpandRatio(cancel, 1);
        footer.setComponentAlignment(cancel, Alignment.TOP_RIGHT);
        return footer;
    }
}
