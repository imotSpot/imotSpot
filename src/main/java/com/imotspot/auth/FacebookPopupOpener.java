package com.imotspot.auth;

import com.vaadin.server.BrowserWindowOpener;
import com.vaadin.server.Page;
import org.scribe.builder.api.Api;
import org.vaadin.addon.oauthpopup.OAuthCallbackInjecter;
import org.vaadin.addon.oauthpopup.OAuthListener;

import java.net.URI;
import java.util.LinkedList;

public class FacebookPopupOpener extends BrowserWindowOpener {

    private LinkedList<OAuthListener> listeners = new LinkedList<OAuthListener>();

    private final AuthData data;

    private OAuthListener dataListener;

    public FacebookPopupOpener(Class<? extends Api> apiClass, String key, String secret, String getRequestLink, String redirectUrl) {
        super(FacebookPopupUI.class);
        this.data = new AuthData(apiClass, key, secret, getRequestLink, redirectUrl);
        setCallbackToDefault();
    }

    public void addOAuthListener(OAuthListener listener) {
        listeners.add(listener);
    }

    public void removeOAuthListener(OAuthListener listener) {
        listeners.remove(listener);
    }

    @Override
    public void attach() {
        super.attach();

        // Adding the session attribute.
        String attr = data.getSessionAttributeName();
        getSession().setAttribute(attr, data);
        setParameter("data", attr);

        dataListener = new OAuthListener() {
            @Override
            public void authSuccessful(String accessToken, String accessTokenSecret, String oauthRawResponse) {
                fireAuthSuccessful(accessToken, accessTokenSecret, oauthRawResponse);
            }

            @Override
            public void authDenied(String reason) {
                fireAuthFailed(reason);
            }
        };
        data.addListener(dataListener);
    }

    @Override
    public void detach() {
        super.detach();

        data.removeListener(dataListener);

        // Deleting the session attribute.
        getSession().setAttribute(data.getSessionAttributeName(), null);
    }

    public void setCallback(String callback) {
        data.setCallback(callback);
    }

    public void setCallbackToDefault() {
        setCallback(getDefaultCallback());
    }

    public void setScope(String scope) {
        data.setScope(scope);
    }

    private static String getDefaultCallback() {
        URI u = Page.getCurrent().getLocation();
        return u.getScheme() + "://" + u.getAuthority() + u.getPath();
    }

    private void fireAuthSuccessful(final String accessToken, final String accessTokenSecret, final String oauthRawResponse) {
        // Coming from different thread than the usual Vaadin server visit.
        // That's why we have to call access (TODO: session or UI?, seems like UI is correct.)
        // Doing this here so our listeners don't need to.
        getUI().access(new Runnable() {
            @Override
            public void run() {
                for (final OAuthListener li : listeners) {
                    li.authSuccessful(accessToken, accessTokenSecret, oauthRawResponse);
                }
            }
        });
    }

    private void fireAuthFailed(final String reason) {
        getUI().access(new Runnable() {
            @Override
            public void run() {
                for (final OAuthListener li : listeners) {
                    li.authDenied(reason);
                }

            }
        });
    }

    public void setCallbackInjecter(OAuthCallbackInjecter injecter) {
        data.setCallbackInjecter(injecter);
    }

}
