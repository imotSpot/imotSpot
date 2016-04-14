package com.imotspot.dashboard.view;

import com.imotspot.auth.ApiInfo;
import com.imotspot.auth.AuthCallbackRequestHandler;
import com.imotspot.auth.AuthData;
import com.imotspot.auth.GoogleSignIn;
import com.imotspot.dashboard.event.DashboardEvent.UserLoginRequestedEvent;
import com.imotspot.dashboard.event.DashboardEventBus;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.server.*;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.themes.ValoTheme;
import org.scribe.builder.api.FacebookApi;
import org.vaadin.addon.oauthpopup.buttons.GitHubApi;
import org.vaadin.addon.oauthpopup.buttons.GooglePlusApi;

@SuppressWarnings("serial")
public class LoginView extends Window {

    private GoogleSignIn googleService = new GoogleSignIn();
    private String redirectUrl;
    private static final String GOOGLE_API_KEY = "630229759772-2100fc6154umojo7dlq3j1rajj1qt98m.apps.googleusercontent.com";
    private static final String GOOGLE_API_SECRET = "NUq8VPbsaw8CcFMsn_ptzqxb";
    private static final String FACEBOOK_APP_ID = "1676761445924727";
    private static final String FACEBOOK_APP_SECRET = "2c03150eee40273c535111c0e5dc5352";

    // Facebook test application at http://localhost:8080
    private static final ApiInfo FACEBOOK_API = new ApiInfo("Facebook",
            FacebookApi.class,
            FACEBOOK_APP_ID,
            FACEBOOK_APP_SECRET,
            "https://graph.facebook.com/me?fields=age_range,gender,name,id,link,email,picture,first_name,last_name");

    private static final ApiInfo GITHUB_API = new ApiInfo("GitHub",
            GitHubApi.class,
            "97a7e251c538106e7922",
            "6a36b0992e5e2b00a38c44c21a6e0dc8ae01d83b",
            "https://api.github.com/user");

    private static final ApiInfo GOOGLE_API = new ApiInfo("Google",
            GooglePlusApi.class,
            GOOGLE_API_KEY,
            GOOGLE_API_SECRET,
            "https://www.googleapis.com/plus/v1/people/me");

    public LoginView() {
        setCaption("Login");
        setModal(true);
        setClosable(true);
        center();

        setWidth(40, Unit.PERCENTAGE);
        redirectUrl = Page.getCurrent().getLocation().toString();

        Component loginForm = buildLoginForm();
        setContent(loginForm);

    }

    private VerticalLayout buildLoginForm() {
        final VerticalLayout loginInnerPanel = new VerticalLayout();

        loginInnerPanel.setSpacing(true);
        loginInnerPanel.addStyleName("login-panel");

        Responsive.makeResponsive(loginInnerPanel);
//        loginInnerPanel.addComponent(buildLabels());
//        loginInnerPanel.addComponent(buildFields());
        loginInnerPanel.addComponent(buildThirdPartyButtons());
        loginInnerPanel.addComponent(new CheckBox("Remember me", true));
        return loginInnerPanel;
    }

    private Component buildFields() {
        HorizontalLayout fields = new HorizontalLayout();
        fields.setSpacing(true);
        fields.addStyleName("fields");
        fields.setSizeUndefined();

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
        welcome.addStyleName(ValoTheme.LABEL_H4);
        welcome.addStyleName(ValoTheme.LABEL_COLORED);
        labels.addComponent(welcome);

        return labels;
    }

    private Component buildThirdPartyButtons() {
        HorizontalLayout fieldsSecondRow = new HorizontalLayout();
        fieldsSecondRow.setSpacing(true);
        fieldsSecondRow.addStyleName("fields");
        fieldsSecondRow.setSizeUndefined();

        AuthData data = new AuthData(FACEBOOK_API, redirectUrl);
        AuthCallbackRequestHandler responseHandler = new AuthCallbackRequestHandler(data, googleService);
        VaadinSession.getCurrent().addRequestHandler(responseHandler);

        Embedded facebook = new Embedded(null, new ClassResource("/com/imotspot/auth/social-facebook-box-blue-icon.png"));
        facebook.addClickListener(event -> getUI().getPage().setLocation(data.getSignInUrl()));
        facebook.addStyleName("imageLink");


        Embedded google = new Embedded(null, new ClassResource("/com/imotspot/auth/social-google-box-icon.png"));
        google.addClickListener(event -> getUI().getPage().setLocation(googleService.getSignInUrl()));
        google.addStyleName("imageLink");

        fieldsSecondRow.addComponents(new Label("Login with: "), google, facebook);
        fieldsSecondRow.setComponentAlignment(facebook, Alignment.BOTTOM_LEFT);

        return fieldsSecondRow;
    }

}
