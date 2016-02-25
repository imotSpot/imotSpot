package com.imotspot.dashboard.view;

import com.imotspot.dashboard.event.DashboardEvent.UserLoginRequestedEvent;
import com.imotspot.dashboard.event.DashboardEventBus;
import com.imotspot.dashboard.utility.auth.GoogleSignIn;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.server.*;
import com.vaadin.shared.Position;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.themes.ValoTheme;

@SuppressWarnings("serial")
public class LoginView extends VerticalLayout {

    public LoginView() {
        setSizeFull();

        Component loginForm = buildLoginForm();
        addComponent(loginForm);
        setComponentAlignment(loginForm, Alignment.MIDDLE_CENTER);
    }

    private Component buildLoginForm() {
        final VerticalLayout loginPanel = new VerticalLayout();
        loginPanel.setSizeUndefined();
        loginPanel.setSpacing(true);
        Responsive.makeResponsive(loginPanel);
        loginPanel.addStyleName("login-panel");

        loginPanel.addComponent(buildLabels());
        loginPanel.addComponent(buildFields());
        loginPanel.addComponent(buildThirdPartyButtons());
        loginPanel.addComponent(new CheckBox("Remember me", true));
        return loginPanel;
    }

    private Component buildThirdPartyButtons() {
        HorizontalLayout fieldsSecondRow = new HorizontalLayout();
        fieldsSecondRow.setSpacing(true);
        fieldsSecondRow.addStyleName("fields");

        BrowserWindowOpener opener = new BrowserWindowOpener(new GoogleSignIn().getSignInUrl());
        opener.setFeatures("height=400,width=400,resizable,position=center");
        opener.setWindowName("_self");

        final Button signWithGooglePlus = new Button("Google +");
        signWithGooglePlus.addStyleName(ValoTheme.BUTTON_PRIMARY);
        final Button signWithFacebook = new Button("Facebook");
        signWithFacebook.addStyleName(ValoTheme.BUTTON_PRIMARY);

//        signWithGooglePlus.setIcon(new ThemeResource("img/signin_button.png"));

        opener.extend(signWithGooglePlus);

        signWithGooglePlus.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent clickEvent) {
            }
        });

        signWithFacebook.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent clickEvent) {
                Notification notification = new Notification("Click facebook btn");
                notification.setPosition(Position.BOTTOM_CENTER);
                notification.setDelayMsec(20000);
                notification.show(Page.getCurrent());
            }
        });

        fieldsSecondRow.addComponents(signWithFacebook, signWithGooglePlus);
        fieldsSecondRow.setComponentAlignment(signWithGooglePlus, Alignment.MIDDLE_RIGHT);

        return fieldsSecondRow;
    }

    private Component buildFields() {
        HorizontalLayout fields = new HorizontalLayout();
        fields.setSpacing(true);
        fields.addStyleName("fields");

        final TextField username = new TextField("Username");
        username.setIcon(FontAwesome.USER);
        username.addStyleName(ValoTheme.TEXTFIELD_INLINE_ICON);

        final PasswordField password = new PasswordField("Password");
        password.setIcon(FontAwesome.LOCK);
        password.addStyleName(ValoTheme.TEXTFIELD_INLINE_ICON);

        final Button signin = new Button("Sign In");
        signin.addStyleName(ValoTheme.BUTTON_PRIMARY);
        signin.setClickShortcut(KeyCode.ENTER);
        signin.focus();

        fields.addComponents(username, password, signin);
        fields.setComponentAlignment(signin, Alignment.BOTTOM_LEFT);

        signin.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(final ClickEvent event) {
                DashboardEventBus.post(new UserLoginRequestedEvent(username
                        .getValue(), password.getValue()));
            }
        });

        return fields;
    }

    private Component buildLabels() {
        CssLayout labels = new CssLayout();
        labels.addStyleName("labels");

        Label welcome = new Label("Welcome");
        welcome.setSizeUndefined();
        welcome.addStyleName(ValoTheme.LABEL_H4);
        welcome.addStyleName(ValoTheme.LABEL_COLORED);
        labels.addComponent(welcome);

        Label title = new Label("Imot Spot");
        title.setSizeUndefined();
        title.addStyleName(ValoTheme.LABEL_H3);
        title.addStyleName(ValoTheme.LABEL_LIGHT);
        labels.addComponent(title);
        return labels;
    }
}
