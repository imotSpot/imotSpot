package com.imotspot.dashboard.utility.auth;

import com.google.gson.Gson;
import com.vaadin.server.*;
import com.vaadin.ui.Link;
import com.vaadin.ui.Notification;
import org.scribe.builder.ServiceBuilder;
import org.scribe.model.*;
import org.scribe.oauth.OAuthService;

import java.io.IOException;

/**
 *
 * @author Matti Tahvonen
 */
public class GoogleSignIn implements RequestHandler {

    private Link gplusLoginButton;

    OAuthService service;
    String redirectUrl;

    public GoogleSignIn() {
        // todo fix exception for redirect link
        redirectUrl = Page.getCurrent().getLocation().toString();
//        super("Login");
//        redirectUrl = Page.getCurrent().getLocation().toString();
//        service = createService();
//        String url = service.getAuthorizationUrl(null);
//
////        gplusLoginButton = new Link("Login with Google", new ExternalResource(url));
////        gplusLoginButton.addStyleName(ValoTheme.LINK_LARGE);
//
//        VaadinSession.getCurrent().addRequestHandler(this);

//        setContent(new MVerticalLayout(gplusLoginButton).alignAll(
//                Alignment.MIDDLE_CENTER).withFullHeight());
//        setModal(true);
//        setWidth("300px");
//        setHeight("200px");
    }

    public void authDenied(String reason) {
        Notification.show("authDenied:" + reason,
                Notification.Type.ERROR_MESSAGE);
    }

    private OAuthService createService() {
        ServiceBuilder sb = new ServiceBuilder();
        sb.provider(Google2Api.class);
        sb.apiKey("630229759772-2100fc6154umojo7dlq3j1rajj1qt98m.apps.googleusercontent.com");
        sb.apiSecret("NUq8VPbsaw8CcFMsn_ptzqxb");
        sb.scope("email");
        String callBackUrl = Page.getCurrent().getLocation().toString();
        if(callBackUrl.contains("#")) {
            callBackUrl = callBackUrl.substring(0, callBackUrl.indexOf("#"));
        }
        sb.callback(callBackUrl);
        return sb.build();
    }

    @Override
    public boolean handleRequest(VaadinSession session, VaadinRequest request,
            VaadinResponse response) throws IOException {
        if (request.getParameter("code") != null) {
            String code = request.getParameter("code");
            Verifier v = new Verifier(code);
            Token t = service.getAccessToken(null, v);

            OAuthRequest r = new OAuthRequest(Verb.GET,
                    "https://www.googleapis.com/plus/v1/people/me");
            service.signRequest(t, r);
            Response resp = r.send();

            GooglePlusAnswer answer = new Gson().fromJson(resp.getBody(),
                    GooglePlusAnswer.class);

//            userSession.login(answer.emails[0].value, answer.displayName);
//
//            close();
            VaadinSession.getCurrent().removeRequestHandler(this);

            ((VaadinServletResponse) response).getHttpServletResponse().
                    sendRedirect(redirectUrl);
            return true;
        }

        return false;
    }

    public String signIn() {
        service = createService();
        String url = service.getAuthorizationUrl(null);

//        gplusLoginButton = new Link("Login with Google", new ExternalResource(url));
//        gplusLoginButton.addStyleName(ValoTheme.LINK_LARGE);

        VaadinSession.getCurrent().addRequestHandler(this);
        return url;
    }
//
//    @Inject
//    @ConfigProperty(name = "imotspot.gpluskey")
//    private String gpluskey;
//
//    @Inject
//    @ConfigProperty(name = "imotspot.gplussecret")
//    private String gplussecret;

}
