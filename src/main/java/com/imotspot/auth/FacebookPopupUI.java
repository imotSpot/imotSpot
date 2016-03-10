package com.imotspot.auth;

import com.vaadin.server.Page;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.UI;
import org.scribe.model.Token;

public class FacebookPopupUI extends UI {

    public static final String DATA_PARAM_NAME = "data";

    private AuthCallbackRequestHandler callbackHandler;

    @Override
    protected void init(VaadinRequest request) {

        String attr;
        AuthData data;
        if ((attr = request.getParameter(DATA_PARAM_NAME)) == null) {
            throw new IllegalStateException(
                    String.format("No URI parameter named \"%s\".\n", DATA_PARAM_NAME) +
                            "Please use OAuthPopupButton or some of its subclass to open OAuthPopup.");
        } else if ((data = (AuthData) getSession().getAttribute(attr)) == null) {
            throw new IllegalStateException(
                    String.format("No session attribute named \"%s\" found.\n", attr) +
                            "Please use OAuthPopupButton or some of its subclass to open OAuthPopup.");
        } else {
            Token requestToken = data.createNewRequestToken();
            addCallbackHandler(requestToken, data);
            goToAuthorizationUrl(requestToken, data);
        }
    }

    @Override
    public void detach() {
        super.detach();

        // The session may have been already cleaned up by requestHandler,
        // not always though.
        // Doing it again doesn't do harm (?).
        callbackHandler.cleanUpSession(getSession());
    }

    private void addCallbackHandler(Token requestToken, AuthData data) {
        callbackHandler = new AuthCallbackRequestHandler(requestToken, data);
        getSession().addRequestHandler(callbackHandler);
    }

    public void removeCallbackHandler() {
        if (callbackHandler != null) {
            getSession().removeRequestHandler(callbackHandler);
            callbackHandler = null;
        }
    }

    private void goToAuthorizationUrl(Token requestToken, AuthData data) {
        String authUrl = data.getAuthorizationUrl(requestToken);
        Page.getCurrent().setLocation(authUrl);
    }

}
