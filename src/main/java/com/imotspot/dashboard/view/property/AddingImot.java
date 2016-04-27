package com.imotspot.dashboard.view.property;


import com.imotspot.googlemap.Geocoding;
import com.imotspot.googlemap.json.GeocodingAnswer;
import com.imotspot.googlemap.json.LocationAnswer;
import com.imotspot.interfaces.DashboardEditListener;
import com.imotspot.model.User;
import com.imotspot.model.imot.*;
import com.imotspot.model.imot.enumerations.Condition;
import com.imotspot.model.imot.enumerations.ImotType;
import com.imotspot.model.imot.interfaces.Media;
import com.vaadin.data.Property;
import com.vaadin.event.ShortcutAction;
import com.vaadin.server.ClassResource;
import com.vaadin.server.FileResource;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.tapio.googlemaps.GoogleMap;
import com.vaadin.tapio.googlemaps.client.LatLon;
import com.vaadin.tapio.googlemaps.client.events.MapClickListener;
import com.vaadin.tapio.googlemaps.client.overlays.GoogleMapMarker;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@SuppressWarnings("serial")
public class AddingImot extends Window implements Upload.Receiver, Upload.SucceededListener {

    private final DashboardEditListener listener;
    private static final LatLon centerSofia = new LatLon(42.697702770146975, 23.32174301147461);
    private static final ClassResource noImage = new ClassResource("/com/imotspot/img/no_image.png");
    private HorizontalLayout horLayout;
    private TextField price;
    private TextField year;
    private TextArea description;
    private TextField country;
    private TextField district;
    private TextField city;
    private TextField address;
    private ComboBox condition;
    private ComboBox type;
    private GoogleMap googleMap;
    private Image imageView;
    private File file;
    private String savePath;
    private String imageName = "";
    private String imageWidth = "90px";
    private String imageHeight = "90px";
    private List<Media> listOfImages = new ArrayList<>();
    private Integer imageCounter = 0;

    public AddingImot(final DashboardEditListener listener,
                      final String currentName) {
        this.listener = listener;
        setCaption("Add Imot");
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
        googleMap.setZoom(13);
        googleMap.setCenter(new LatLon(42.697702770146975, 23.32174301147461));
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

        horLayout = new HorizontalLayout();
        horLayout.setWidth("350px");
        horLayout.setHeight("150px");
        horLayout.setSpacing(true);

        condition = new ComboBox("condition");
        condition.setFilteringMode(FilteringMode.STARTSWITH);
        for (Condition c: enumConditionList){
            condition.addItem(c.toString());
        }

        Upload uploadImages = new Upload("Upload up to 3 images", this);
        uploadImages.setButtonCaption("Upload images");
        uploadImages.addSucceededListener(this);
//        DragAndDropWrapper drag = new DragAndDropWrapper(uploadImages);
//        drag.setEnabled(true);

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

        content.addComponents(type, price, year, description, country, district, city, address, condition, horLayout, uploadImages);
        return content;
    }

    private void addImageToLayout(HorizontalLayout layout, FileResource resource){

        imageView = new Image();
        imageView.setWidth(imageWidth);
        imageView.setHeight(imageHeight);
        imageView.setSource(resource);
        imageView.setCaption(imageName);

        layout.addComponent(imageView);
        layout.setComponentAlignment(imageView, Alignment.MIDDLE_CENTER);
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
                imotToSave.setType(ImotType.valueOf(type.getValue().toString()));
                imotToSave.setPrice(Float.parseFloat(price.getValue()));
                imotToSave.setYear(year.getValue());
                imotToSave.setDescription(description.getValue());
                imotToSave.setMedia(listOfImages);
                imotToSave.setCondition(Condition.valueOf(condition.getValue().toString()));
                imotToSave.setOwner((User) VaadinSession.getCurrent().getAttribute(User.class.getName()));
                imotToSave.setPublished(new Date());

                try {
                    listener.dashboardNameEdited(imotToSave);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                close();
            }
        });
        save.setClickShortcut(ShortcutAction.KeyCode.ENTER, null);

        footer.addComponents(cancel, save);
        footer.setExpandRatio(cancel, 1);
        footer.setComponentAlignment(cancel, Alignment.TOP_RIGHT);
        return footer;
    }

    @Override
    public OutputStream receiveUpload(String filename, String mimeType) {
        imageCounter++;
        savePath = "../uploads/" + filename;
        imageName = filename;
        FileOutputStream fos = null; // Stream to write to
        try {
            // Open the file for writing.
            file = new File(savePath);
            fos = new FileOutputStream(file);

        } catch (final java.io.FileNotFoundException e) {
            new Notification("Could not open file",
                    e.getMessage(),
                    Notification.Type.ERROR_MESSAGE).show(Page.getCurrent());
            return null;
        }

        return fos; // Return the output stream to write to
    }

    @Override
    public void uploadSucceeded(Upload.SucceededEvent succeededEvent) {

        if (imageCounter <= 3) {
            FileResource resource = new FileResource(new File(savePath));

            addImageToLayout(horLayout, resource);

            Picture image = null;
            try {
                image = new Picture(new URI(savePath));
                byte[] data = Files.readAllBytes(new File(savePath).toPath());
                image.imageAsArray(data);
                listOfImages.add(image);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            } catch (IOException e){
                e.printStackTrace();
            }

            new Notification("File " + succeededEvent.getFilename() + " uploaded successfull", Notification.Type.HUMANIZED_MESSAGE).show(Page.getCurrent());
        } else {
            new Notification("Maximum of 3 images to upload is exceeded!!!", Notification.Type.HUMANIZED_MESSAGE).show(Page.getCurrent());
        }
    }
}
