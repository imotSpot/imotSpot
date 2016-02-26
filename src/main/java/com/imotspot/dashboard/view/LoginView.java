package com.imotspot.dashboard.view;

import com.google.gson.Gson;
import com.imotspot.dashboard.domain.User;
import com.imotspot.dashboard.event.DashboardEvent.UserLoginRequestedEvent;
import com.imotspot.dashboard.event.DashboardEventBus;
import com.imotspot.dashboard.utility.auth.GooglePlusAnswer;
import com.imotspot.dashboard.utility.auth.GoogleSignIn;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.server.*;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.themes.ValoTheme;
import org.scribe.model.*;

import java.io.IOException;

@SuppressWarnings("serial")
public class LoginView extends Window implements RequestHandler {

    GoogleSignIn googleService = new GoogleSignIn();
    String redirectUrl;

    public LoginView() {
        setCaption("Login");
        setModal(true);
        setClosable(true);
        setResizable(true);
//        setWidth(300.0f, Unit.PIXELS);
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

//        BrowserWindowOpener opener = new BrowserWindowOpener(new GoogleSignIn().getSignInUrl());
//        opener.setFeatures("height=400,width=400,resizable,position=center");
//        opener.setWindowName("_self");

        final Link signWithGooglePlus = new Link("", new ExternalResource(googleService.getSignInUrl()));
        signWithGooglePlus.setIcon(new ExternalResource("https://www.gliffy.com/go/icons/icn-google-30x30.png"));
        VaadinSession.getCurrent().addRequestHandler(this);
//        signWithGooglePlus.addStyleName(ValoTheme.LINK_LARGE);

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

        fieldsSecondRow.addComponent(signWithGooglePlus);
        fieldsSecondRow.setComponentAlignment(signWithGooglePlus, Alignment.MIDDLE_RIGHT);

        return fieldsSecondRow;
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
//            DashboardEventBus.post(new DashboardEvent.UserLoginRequestedEvent(answer.emails[0].value, answer.displayName));

            VaadinSession.getCurrent().removeRequestHandler(this);

            ((VaadinServletResponse) response).getHttpServletResponse().
                    sendRedirect(redirectUrl);
            return true;
        }

        return false;

//        return googleService.handleRequest(session, request, response);
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
