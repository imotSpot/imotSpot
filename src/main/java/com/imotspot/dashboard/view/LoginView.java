package com.imotspot.dashboard.view;

import com.google.gson.Gson;
import com.imotspot.dashboard.GoogleAuth.ApiInfo;
import com.imotspot.dashboard.GoogleAuth.GooglePlusAnswer;
import com.imotspot.dashboard.GoogleAuth.GoogleSignIn;
import com.imotspot.dashboard.domain.User;
import com.imotspot.dashboard.event.DashboardEvent.UserLoginRequestedEvent;
import com.imotspot.dashboard.event.DashboardEventBus;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.server.*;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.themes.ValoTheme;
import org.scribe.builder.api.FacebookApi;
import org.scribe.model.*;
import org.vaadin.addon.oauthpopup.OAuthListener;
import org.vaadin.addon.oauthpopup.OAuthPopupButton;
import org.vaadin.addon.oauthpopup.buttons.FacebookButton;
import org.vaadin.addon.oauthpopup.buttons.GitHubApi;
import org.vaadin.addon.oauthpopup.buttons.GitHubButton;
import org.vaadin.addon.oauthpopup.buttons.GooglePlusApi;

import java.io.IOException;

@SuppressWarnings("serial")
public class LoginView extends Window implements RequestHandler {

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
            "https://graph.facebook.com/me");

    private static final ApiInfo GITHUB_API = new ApiInfo("GitHub",
            GitHubApi.class,
            "97a7e251c538106e7922",
            "6a36b0992e5e2b00a38c44c21a6e0dc8ae01d83b",
            "https://api.github.com/user");

    private static final ApiInfo GOOGLE_API = new ApiInfo("Google",
            GooglePlusApi.class,
            GOOGLE_API_KEY,
            GOOGLE_API_SECRET,
            "https://api.google.com/user");

    public LoginView() {
        setCaption("Login");
        setModal(true);
        setClosable(true);
//        setResizable(true);
//        setWidth(592.0f, Unit.PIXELS);
//        setSizeFull();

        Component loginForm = buildLoginForm();
//        setWidthUndefined();
//        setHeightUndefined();
        setContent(loginForm);

//        setWidth(loginForm.getWidth()*3, Unit.PIXELS);
//        setHeight(loginForm.getHeight()*3, Unit.PIXELS);
//
//        addComponent(loginForm);
//        setComponentAlignment(loginForm, Alignment.MIDDLE_CENTER);
        redirectUrl = Page.getCurrent().getLocation().toString();
    }

    private Component buildLoginForm() {
        VerticalLayout loginForm = new VerticalLayout();

        HorizontalLayout firstRow = new HorizontalLayout();
        firstRow.setSizeUndefined();
        firstRow.setSpacing(true);
        Responsive.makeResponsive(firstRow);
//        firstRow.addStyleName("panelwelcome");
        firstRow.addComponents(buildLabels(), buildFields());

        HorizontalLayout secondRow = new HorizontalLayout();
        secondRow.setSizeUndefined();
        secondRow.setSpacing(true);
        Responsive.makeResponsive(secondRow);
//        secondRow.addStyleName("panelwelcome");
        secondRow.addComponents(buildThirdPartyButtons(), new CheckBox("Remember me", true));

        loginForm.addComponents(firstRow, secondRow);
        loginForm.setComponentAlignment(firstRow, Alignment.MIDDLE_CENTER);
        loginForm.setComponentAlignment(secondRow, Alignment.MIDDLE_CENTER);

        return loginForm;
    }

    private Component buildThirdPartyButtons() {
        HorizontalLayout fieldsSecondRow = new HorizontalLayout();
        fieldsSecondRow.setSpacing(true);
        fieldsSecondRow.addStyleName("fields");

        addGoogleButton(fieldsSecondRow);
        addFacebookButton(fieldsSecondRow);
        addGitHubButton(fieldsSecondRow);

//        final Button signWithGooglePlus = new Button("");
//        signWithGooglePlus.addStyleName(ValoTheme.BUTTON_LINK);
//        final Button signWithFacebook = new Button("Facebook");
//        signWithFacebook.addStyleName(ValoTheme.BUTTON_PRIMARY);

//        VaadinSession.getCurrent().addRequestHandler(this);

//        signWithGooglePlus.setIcon(new ThemeResource("img/signin_button.png"));

//        opener.extend(signWithGooglePlus);

//        signWithGooglePlus.addClickListener(new ClickListener() {
//            @Override
//            public void buttonClick(ClickEvent clickEvent) {
//            }
//        });

//        signWithFacebook.addClickListener(new ClickListener() {
//            @Override
//            public void buttonClick(ClickEvent clickEvent) {
//                Notification notification = new Notification("Click facebook btn");
//                notification.setPosition(Position.BOTTOM_CENTER);
//                notification.setDelayMsec(20000);
//                notification.show(Page.getCurrent());
//            }
//        });

//        fieldsSecondRow.addComponent(signWithGooglePlus);
//        fieldsSecondRow.setComponentAlignment(signWithGooglePlus, Alignment.MIDDLE_RIGHT);

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
//        welcome.setWidth(400.00f, Unit.PIXELS);
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

    private void addGoogleButton(Layout layout) {

        Link signWithGooglePlus = new Link("", new ExternalResource(googleService.getSignInUrl()));
        signWithGooglePlus.setIcon(new ExternalResource("https://www.gliffy.com/go/icons/icn-google-30x30.png"));
        VaadinSession.getCurrent().addRequestHandler(this);
        layout.addComponent(signWithGooglePlus);

//        ApiInfo api = GOOGLE_API;
//        OAuthPopupButton button = new GooglePlusButton(api.apiKey, api.apiSecret);
//        addButton(api, button, layout);
    }

    private void addFacebookButton(Layout layout) {
        ApiInfo api = FACEBOOK_API;
        OAuthPopupButton button = new FacebookButton(api.apiKey, api.apiSecret);
        addButton(api, button, layout);
    }

    private void addGitHubButton(Layout layout) {
        ApiInfo api = GITHUB_API;
        OAuthPopupButton button = new GitHubButton(api.apiKey, api.apiSecret);
        addButton(api, button, layout);
    }

    private void addButton(final ApiInfo service, OAuthPopupButton button, Layout layout) {

        // In most browsers "resizable" makes the popup
        // open in a new window, not in a tab.
        // You can also set size with eg. "resizable,width=400,height=300"
        button.setPopupWindowFeatures("resizable,width=500,height=400");

        HorizontalLayout hola = new HorizontalLayout();
        hola.setSpacing(true);
        hola.addComponent(button);

        layout.addComponent(hola);

        button.addOAuthListener(new Listener(service, hola));
    }

    private class Listener implements OAuthListener {

        private final ApiInfo service;
        private final HorizontalLayout hola;

        private Listener(ApiInfo service, HorizontalLayout hola) {
            this.service = service;
            this.hola = hola;
        }

        @Override
        public void authSuccessful(final String accessToken,
                                   final String accessTokenSecret, String oauthRawResponse) {

            Notification.show("Authorized.");
            User user = new User();
            user.setFirstName("Angel");
            user.setLastName("Raev");
            user.setRole("user");
            VaadinSession.getCurrent().setAttribute(User.class.getName(), user);
            redirectUrl = Page.getCurrent().getLocation().toString();
            closeWindow();
        }

        @Override
        public void authDenied(String reason) {
            Notification.show("Auth failed.");
        }
    }

    @Override
    public boolean handleRequest(VaadinSession session, VaadinRequest request,
                                 VaadinResponse response) throws IOException {

        if (request.getParameter("code") != null) {
            String code = request.getParameter("code");
            Verifier v = new Verifier(code);
            Token t = googleService.getAccessToken(null, v);

            OAuthRequest r = new OAuthRequest(Verb.GET,
                    "https://www.googleapis.com/plus/v1/people/me");
            googleService.signRequest(t, r);
            Response resp = r.send();

            GooglePlusAnswer answer = new Gson().fromJson(resp.getBody(),
                    GooglePlusAnswer.class);

            User user = new User();
            user.setFirstName(answer.emails[0].value.substring(0, answer.emails[0].value.indexOf("@")));
            user.setLastName("");
            user.setRole("user");
            VaadinSession.getCurrent().setAttribute(User.class.getName(), user);
            VaadinSession.getCurrent().removeRequestHandler(this);

            ((VaadinServletResponse) response).getHttpServletResponse().
                    sendRedirect(redirectUrl);
            return true;
        }

        return false;
    }

    public void closeWindow(){
//        getUI().;
        getUI().getPage().reload();

        close();
    }
}
