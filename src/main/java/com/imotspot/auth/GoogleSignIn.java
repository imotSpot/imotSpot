package com.imotspot.auth;

import com.vaadin.server.Page;
import com.vaadin.ui.Notification;
import org.scribe.builder.ServiceBuilder;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Token;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

/**
 *
 * @author Matti Tahvonen
 */
public class GoogleSignIn {

    // todo hide credentials
    private final String GOOGLE_API_KEY = "630229759772-2100fc6154umojo7dlq3j1rajj1qt98m.apps.googleusercontent.com";
    private final String GOOGLE_API_SECRET = "NUq8VPbsaw8CcFMsn_ptzqxb";

    private OAuthService service;
    private String redirectUrl;

    public GoogleSignIn() {
        // todo fix exception for redirect link
//        redirectUrl = Page.getCurrent().getLocation().toString();
    }

    public void authDenied(String reason) {
        Notification.show("authDenied:" + reason,
                Notification.Type.ERROR_MESSAGE);
    }

    public String getSignInUrl() {
        service = createService();
        String url = service.getAuthorizationUrl(null);
        return url;
    }

    private OAuthService createService() {
        ServiceBuilder sb = new ServiceBuilder();
        sb.provider(Google2Api.class);
        sb.apiKey(GOOGLE_API_KEY);
        sb.apiSecret(GOOGLE_API_SECRET);
        sb.scope("email");
//        redirectUrl = Page.getCurrent().getLocation().toString()+"dashboard/";
        redirectUrl = Page.getCurrent().getLocation().toString();
        if(redirectUrl.contains("#")) {
            redirectUrl = redirectUrl.substring(0, redirectUrl.indexOf("#"));
        }
        sb.callback(redirectUrl);
        return sb.build();
    }
//
//    @Override
//    public boolean handleRequest(VaadinSession session, VaadinRequest request,
//            VaadinResponse response) throws IOException {
//        if (request.getParameter("code") != null) {
//            String code = request.getParameter("code");
//            Verifier v = new Verifier(code);
//            Token t = service.getAccessToken(null, v);
//
//            OAuthRequest r = new OAuthRequest(Verb.GET,
//                    "https://www.googleapis.com/plus/v1/people/me");
//            service.signRequest(t, r);
//            Response resp = r.send();
//
//            GooglePlusAnswer answer = new Gson().fromJson(resp.getBody(),
//                    GooglePlusAnswer.class);
//
//            DashboardEventBus.post(new DashboardEvent.UserLoginRequestedEvent(answer.emails[0].value, answer.displayName));
//
//            VaadinSession.getCurrent().removeRequestHandler(this);
//
//            ((VaadinServletResponse) response).getHttpServletResponse().
//                    sendRedirect(Page.getCurrent().getLocation().toString()+"dashboard/");
//            return true;
//        }
//
//        return false;
//    }

    public Token getAccessToken(Token t, Verifier v) {
        return service.getAccessToken(t, v);
    }

    public void signRequest(Token t, OAuthRequest r) {
        service.signRequest(t, r);
    }
}
