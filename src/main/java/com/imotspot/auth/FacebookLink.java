package com.imotspot.auth;

import com.vaadin.server.ClassResource;
import com.vaadin.server.Resource;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Link;
import org.scribe.builder.api.Api;
import org.vaadin.addon.oauthpopup.OAuthCallbackInjecter;
import org.vaadin.addon.oauthpopup.OAuthListener;

/**
 * A button that opens a OAuth authorization url in a separate browser window,
 * and lets the user to perform the authorization.
 * <p>
 * The success/failure of the authorization can be listened
 * with {@link #addOAuthListener(OAuthListener)}.
 * <p>
 * IMPORTANT: the callback comes from a different window, not from the usual
 * Vaadin server-visit thread. That's why the UI is not updated automatically,
 * UNLESS server push is enabled. So, it's good idea to enable @Push in the UI class.
 * <p>
 * This class may be subclassed for customization, or
 * used just by giving the Scribe Api class for the constructor.
 * The latter approach may not work for all Api's because
 * all the Apis don't work the same and some customization might
 * be necessary...
 * <p>
 * Available subclasses are at {@link org.vaadin.addon.oauthpop.buttons}.
 */
@SuppressWarnings("serial")
// I guess we might have as well just inherit Button, not CustomComponent...
public class FacebookLink extends CustomComponent {


    private final FacebookPopupOpener opener;

    private Link link;

    public FacebookLink(Class<? extends Api> apiClass, String key, String secret, String getRequestLink) {
        this.opener = new FacebookPopupOpener(apiClass, key, secret, getRequestLink);
        link = new Link();
        link.setIcon(new ClassResource("/com/imotspot/auth/social-facebook-box-blue-icon.png"));
        opener.extend(link);
        setCompositionRoot(link);
    }

    protected Link getButton() {
        return link;
    }

    /**
     * IMPORTANT: the listener call originates from a different window,
     * not from the usual Vaadin server-visit thread.
     * That's why the UI is not updated automatically, UNLESS server push is enabled.
     * So, it's good idea to enable @Push in the UI class.
     */
    public void addOAuthListener(OAuthListener listener) {
        opener.addOAuthListener(listener);
    }

    /**
     * IMPORTANT: the listener call originates from a different window,
     * not from the usual Vaadin server-visit thread.
     * That's why the UI is not updated automatically, UNLESS server push is enabled.
     * So, it's good idea to enable @Push in the UI class.
     */
    public void removeListener(OAuthListener listener) {
        opener.removeOAuthListener(listener);
    }

    @Override
    public void setCaption(String caption) {
        link.setCaption(caption);
    }

    @Override
    public void setDescription(String description) {
        link.setDescription(description);
    }

    @Override
    public void setIcon(Resource icon) {
        link.setIcon(icon);
    }

    /**
     * Comma-separated list of features given to the BrowserWindowOpener.
     * <p>
     * See here for feature names: https://vaadin.com/book/vaadin7/-/page/advanced.html
     */
    public void setPopupWindowFeatures(String features) {
        opener.setFeatures(features);
    }

    /**
     * Sets the callback URI for the OAuth.
     * <p>
     * Must be called before user opens the popup to have effect.
     * <p>
     * NOTE: OAuth Popup addon automatically adds "/oauthpopupcallback/X"
     * to the end of the callback path.
     * That's to let the callback handler know that it should handle the request.
     * <p>
     * Default: see {@link #setCallbackToDefault()}
     */
    public void setCallback(String callback) {
        opener.setCallback(callback);
    }

    /**
     * Sets the callback URI to default.
     * <p>
     * The default callback is constructed from current Page location:
     * SCHEME + "://" + AUTHORITY + PATH
     */
    public void setCallbackToDefault() {
        opener.setCallbackToDefault();
    }

    public void setScope(String scope) {
        opener.setScope(scope);
    }

    public void setCallbackInjecter(OAuthCallbackInjecter injecter) {
        opener.setCallbackInjecter(injecter);
    }
}
