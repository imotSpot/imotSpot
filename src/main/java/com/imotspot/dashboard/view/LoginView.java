package com.imotspot.dashboard.view;

import com.google.gson.Gson;
import com.imotspot.auth.ApiInfo;
import com.imotspot.auth.FacebookLink;
import com.imotspot.auth.GooglePlusAnswer;
import com.imotspot.auth.GoogleSignIn;
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
import org.vaadin.addon.oauthpopup.buttons.GitHubApi;
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
            "https://www.googleapis.com/plus/v1/people/me");

    public LoginView() {
        setCaption("Login");
        setModal(true);
        setClosable(true);
        center();

        setWidth(30, Unit.PERCENTAGE);

        Component loginForm = buildLoginForm();
        setContent(loginForm);

        redirectUrl = Page.getCurrent().getLocation().toString();
    }

    private Panel buildLoginForm() {

        Panel scrollPanel = new Panel();

        final CssLayout loginPanel = new CssLayout();
        final VerticalLayout loginInnerPanel = new VerticalLayout();
        loginInnerPanel.setSpacing(true);
//        loginInnerPanel.setSizeFull();
        loginPanel.setSizeFull();

        Responsive.makeResponsive(loginPanel);
        loginPanel.addStyleName("login-panel");

        Responsive.makeResponsive(loginInnerPanel);
        loginInnerPanel.addComponent(buildLabels());
        loginInnerPanel.addComponent(buildFields());
        loginInnerPanel.addComponent(buildThirdPartyButtons());
        loginInnerPanel.addComponent(new CheckBox("Remember me", true));
        loginPanel.addComponent(loginInnerPanel);
        scrollPanel.setContent(loginPanel);
        return scrollPanel;
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

        Label title = new Label("Imot Spot");
        title.setSizeUndefined();
        title.addStyleName(ValoTheme.LABEL_H3);
        title.addStyleName(ValoTheme.LABEL_LIGHT);
        labels.addComponent(title);
        return labels;
    }

    private Component buildThirdPartyButtons() {
        HorizontalLayout fieldsSecondRow = new HorizontalLayout();
        fieldsSecondRow.setSpacing(true);
        fieldsSecondRow.addStyleName("fields");
        fieldsSecondRow.setSizeUndefined();

        Link google = addGoogleButton();
//        OAuthPopupButton github = addGitHubButton();
        FacebookLink facebook = addFacebookButton();

        fieldsSecondRow.addComponents(new Label("Login with: "), google, facebook);
        fieldsSecondRow.setComponentAlignment(facebook, Alignment.BOTTOM_LEFT);

        return fieldsSecondRow;
    }

    private Link addGoogleButton() {

        Link signWithGooglePlus = new Link("", new ExternalResource(googleService.getSignInUrl()));
        signWithGooglePlus.setIcon(new ClassResource("/com/imotspot/auth/social-google-box-icon.png"));
        VaadinSession.getCurrent().addRequestHandler(this);
//        layout.addComponent(signWithGooglePlus);
        return signWithGooglePlus;

//        ApiInfo api = GOOGLE_API;
//        OAuthPopupButton button = new GooglePlusButton(api.apiKey, api.apiSecret);
//        addButton(api, button);
//
//        return button;
    }

    private FacebookLink addFacebookButton() {
        ApiInfo api = FACEBOOK_API;
        FacebookLink link = new FacebookLink(FacebookApi.class, api.apiKey, api.apiSecret, api.exampleGetRequest);
        addButton(api, link);

        return link;
    }

//    private OAuthPopupButton addGitHubButton() {
//        ApiInfo api = GITHUB_API;
//        OAuthPopupButton button = new GitHubButton(api.apiKey, api.apiSecret);
//        addButton(api, button);
//
//        return button;
//    }

    private void addButton(final ApiInfo service, FacebookLink link) {

        // In most browsers "resizable" makes the popup
        // open in a new window, not in a tab.
        // You can also set size with eg. "resizable,width=400,height=300"
        link.setPopupWindowFeatures("resizable,width=500,height=400");

        link.addOAuthListener(new Listener(service));
        link.addStyleName(ValoTheme.BUTTON_PRIMARY);

    }

    private class Listener implements OAuthListener {

        private final ApiInfo service;

        private Listener(ApiInfo service) {
            this.service = service;
        }

        @Override
        public void authSuccessful(final String accessToken,
                                   final String accessTokenSecret, String oauthRawResponse) {
            User user = new User();
            user.setFirstName("Angel");
            user.setLastName("Raev");
            user.setRole("user");
            VaadinSession.getCurrent().setAttribute(User.class.getName(), user);
            redirectUrl = Page.getCurrent().getLocation().toString();

            Page.getCurrent().reload();
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

}
