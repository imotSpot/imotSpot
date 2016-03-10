package com.imotspot.auth;

import com.google.gson.Gson;
import com.imotspot.dashboard.domain.User;
import com.vaadin.server.*;
import org.scribe.model.*;

import java.io.IOException;

public class AuthCallbackRequestHandler implements RequestHandler {

    private final Token requestToken;
    private final AuthData data;

    private static final String CLOSE_WINDOW_HTML =
            "<!DOCTYPE html><html lang=\"en\"><head><meta charset=\"utf-8\"/>" +
                    "<script>window.close();</script>" +
                    "</head><body>" +
                    "</body></html>";

    /**
     * Only handles request that match the data id.
     *
     * @param requestToken may be null (in case of OAuth2)
     * @param data
     */
    public AuthCallbackRequestHandler(Token requestToken, AuthData data) {
        this.requestToken = requestToken;
        this.data = data;
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

                GooglePlusAnswer answer = new Gson().fromJson(resp.getBody(),
                        GooglePlusAnswer.class);

                String name = answer.name;
                if (answer.name == null) {
                    name = answer.emails[0].value.substring(0, answer.emails[0].value.indexOf("@"));
                }

                User user = new User();

                user.setFirstName(name);
                user.setLastName("");
                user.setRole("user");
                VaadinSession.getCurrent().setAttribute(User.class.getName(), user);
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
        }
        return true;
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