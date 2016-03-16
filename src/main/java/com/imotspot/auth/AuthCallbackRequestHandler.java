package com.imotspot.auth;

import com.google.gson.Gson;
import com.imotspot.dashboard.domain.User;
import com.imotspot.database.model.UserVertex;
import com.vaadin.server.*;
import org.scribe.model.*;

import java.io.IOException;

public class AuthCallbackRequestHandler implements RequestHandler {

    private final Token requestToken;
    private final AuthData data;
    private final GoogleSignIn googleService;

    private static final String CLOSE_WINDOW_HTML =
            "<!DOCTYPE html><html lang=\"en\"><head><meta charset=\"utf-8\"/>" +
                    "<script>window.close();</script>" +
                    "</head><body>" +
                    "</body></html>";

    public AuthData getData() {
        return data;
    }

    public GoogleSignIn getGoogleService() {
        return googleService;
    }

    /**
     * Only handles request that match the data id.
     *
     * @param data
     */
    public AuthCallbackRequestHandler(AuthData data, GoogleSignIn googleService) {
        this.requestToken = data.getRequestToken();
        this.data = data;
        this.googleService = googleService;
    }

    @Override
    public boolean handleRequest(VaadinSession session,
                                 VaadinRequest request, VaadinResponse response) throws IOException {

        if (data.isCallbackForMe(request)) {


            String verifier = request.getParameter(data.getVerifierParameterName());
            if (verifier != null) {
                // Got verifier!
                data.setVerifier(requestToken, new Verifier(verifier));
                finish(session, response);


                Token t = data.getAccessToken();

                OAuthRequest r = new OAuthRequest(Verb.GET,
                        data.getRequestLink());
                data.signRequest(t, r);
                Response resp = r.send();

                FacebookAnswer answer = new Gson().fromJson(resp.getBody(),
                        FacebookAnswer.class);

                String name = answer.name;
                String picUrl = answer.picture.data.url;
                String oauthId = "facebook" + answer.id;
                saveUser(oauthId, name, "", picUrl);

                VaadinSession.getCurrent().removeRequestHandler(this);
                ((VaadinServletResponse) response).getHttpServletResponse().
                        sendRedirect(data.getRedirectUrl());

                return true;
            }

            // No verifier in the parameters. That's most likely because the user
            // denied the OAuth.

            // TODO: current error message reporting (below) is not very useful

            String error = null;
            for (String errorName : data.getErrorParameterNames()) {
                error = request.getParameter(errorName);
                if (error != null) {
                    break;
                }
            }

            String errorMessage;
            if (error == null) {
                errorMessage = "OAuth failed.";
            } else {
                errorMessage = "OAuth denied: " + error;
            }

            data.setDenied(errorMessage);
            finish(session, response);
        } else if (request.getParameter("code") != null) {
            String code = request.getParameter("code");
            Verifier v = new Verifier(code);
            Token t = googleService.getAccessToken(null, v);

            OAuthRequest r = new OAuthRequest(Verb.GET,
                    "https://www.googleapis.com/plus/v1/people/me");
            googleService.signRequest(t, r);
            Response resp = r.send();

            GooglePlusAnswer answer = new Gson().fromJson(resp.getBody(),
                    GooglePlusAnswer.class);

            String name = answer.displayName;
            String picUrl = answer.image.url;
            String oauthId = "google" + answer.id;
            saveUser(oauthId, name, answer.emails[0].value, picUrl);

            VaadinSession.getCurrent().removeRequestHandler(this);
            ((VaadinServletResponse) response).getHttpServletResponse().
                    sendRedirect(data.getRedirectUrl());
            return true;
        }
        return false;
    }

    private void saveUser(String oauthId, String name, String email, String picUrl) {
        User user = new User();
        user.setOauthIdentifier(oauthId);
        user.setFirstName(name);
        user.setPicUrl(picUrl);
        user.setLastName("");
        user.setEmail(email);
        user.setRole("user");
        new UserVertex(user).save();
        VaadinSession.getCurrent().setAttribute(User.class.getName(), user);
    }

    private void finish(VaadinSession session, VaadinResponse response) throws IOException {
        response.setContentType("text/html");
        response.getWriter().append(CLOSE_WINDOW_HTML);
        cleanUpSession(session);
    }

    // NOTE: the finish() method above is not called if the user
    // doesn't browse back from the OAuth authentication page.
    // That's why we make cleanUpSession public so that others can clean up also.
    public void cleanUpSession(final VaadinSession session) {
        session.access(new Runnable() {
            @Override
            public void run() {
                session.removeRequestHandler(AuthCallbackRequestHandler.this);
            }
        });
    }

}
