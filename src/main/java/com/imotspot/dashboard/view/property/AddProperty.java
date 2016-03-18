package com.imotspot.dashboard.view.property;

import com.imotspot.model.User;
import com.imotspot.model.imot.*;
import com.vaadin.event.ShortcutAction;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;

import java.util.Date;

@SuppressWarnings("serial")
public class AddProperty extends Window {

    private final DashboardEditListener listener;
    private TextField price;
    private TextField year;
    private TextField description;
    private TextField country;
    private TextField district;
    private TextField city;
    private TextField address;
    private TextField condition;
    private TextField images;

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
        VerticalLayout result = new VerticalLayout();
        result.setMargin(true);
        result.setSpacing(true);

        result.addComponent(buildAddForm());
        result.addComponent(buildFooter());

        return result;
    }

    private Component buildAddForm() {
        VerticalLayout content = new VerticalLayout();

        price = new TextField("price");
        year = new TextField("year");
        description = new TextField("description");
        country = new TextField("country");
        district = new TextField("district");
        city = new TextField("city");
        address = new TextField("address");
        condition = new TextField("condition");

        price.addStyleName("caption-on-left");
        year.addStyleName("caption-on-left");
        description.addStyleName("caption-on-left");
        country.addStyleName("caption-on-left");
        district.addStyleName("caption-on-left");
        city.addStyleName("caption-on-left");
        address.addStyleName("caption-on-left");
        condition.addStyleName("caption-on-left");

        content.addComponents(price, year, description, country, district, city, address, condition);
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
                currentLocation.setCountry(new Country(country.getValue()));
                currentLocation.setDistrict(new District(district.getValue()));
                currentLocation.setCity(new City(city.getValue()));
                currentLocation.setAddress(address.getValue());
                currentLocation.setMarker(new LocationMarker(42, 35));

                Imot propertyToSave = new Imot(currentLocation);
                propertyToSave.setPrice(Float.parseFloat(price.getValue()));
                propertyToSave.setYear(year.getValue());
                propertyToSave.setDescription(description.getValue());
                propertyToSave.setCondition(Condition.NEW);
                propertyToSave.setOwner((User) VaadinSession.getCurrent().getAttribute(User.class.getName()));
                propertyToSave.setPublished(new Date());

                listener.dashboardNameEdited(propertyToSave);
                close();
            }
        });
        save.setClickShortcut(ShortcutAction.KeyCode.ENTER, null);

        footer.addComponents(cancel, save);
        footer.setExpandRatio(cancel, 1);
        footer.setComponentAlignment(cancel, Alignment.TOP_RIGHT);
        return footer;
    }

    public interface DashboardEditListener {
        void dashboardNameEdited(Imot name);
    }
}
