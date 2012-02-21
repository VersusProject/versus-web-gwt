package edu.illinois.ncsa.versus.web.client;

import java.util.Date;

import net.customware.gwt.dispatch.client.DispatchAsync;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import edu.illinois.ncsa.mmdb.web.client.MMDB;
import edu.illinois.ncsa.mmdb.web.client.UserSessionState;

import edu.illinois.ncsa.mmdb.web.client.dispatch.GetUser;
import edu.illinois.ncsa.mmdb.web.client.dispatch.GetUserResult;
import edu.illinois.ncsa.versus.web.client.event.LoggedInEvent;
import edu.illinois.ncsa.versus.web.client.event.LoggedOutEvent;
import edu.uiuc.ncsa.cet.bean.PersonBean;

public class Authentication {
    /**
     * If user not logged in redirect to the required login page.
     * 
     * @param eventBus
     * 
     */
    public static void checkLogin(DispatchAsync dispatchAsync, HandlerManager eventBus) {
        boolean loggedIn = true;
        final String cookieSID = Cookies.getCookie("sid");
        if (cookieSID != null) {
            GWT.log("Sid: " + cookieSID, null);
        } else {
            loggedIn = false;
        }
        final String cookieSessionKey = Cookies.getCookie("sessionKey");
        if (cookieSessionKey != null) {
            GWT.log("Session key: " + cookieSessionKey, null);
        } else {
            loggedIn = false;
        }
        if (!loggedIn) {
            History.newItem("login?p=" + History.getToken());
        } else {
            // now check REST auth
            checkRestAuth(cookieSID, cookieSessionKey, dispatchAsync, eventBus);
        }
    }

    private static void checkRestAuth(final String cookieSID, final String cookieSessionKey, final DispatchAsync dispatchAsync, final HandlerManager eventBus) {
        String restUrl = "./api/checkLogin";
        RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, restUrl);
        try {
            GWT.log("checking login status @ " + restUrl, null);
            // we need to block.
            builder.sendRequest("", new RequestCallback() {
                public void onError(Request request, Throwable exception) {
                    logout(eventBus);
                }

                public void onResponseReceived(Request request, Response response) {
                    // success!
                    GWT.log("REST auth status code = " + response.getStatusCode(), null);
                    if (response.getStatusCode() > 300) {
                        GWT.log("not authenticated to REST services", null);
                        logout(eventBus);
                    } else {
                        login(cookieSID, cookieSessionKey, null, dispatchAsync, eventBus);
                    }
                }
            });
        } catch (RequestException x) {
            logout(eventBus);
        }
    }

    /**
     * Set the session id, add a cookie and add history token.
     * 
     * @param redirect
     * 
     * @param dispatchAsync
     * @param eventBus
     * 
     * @param sessionId
     * 
     */
    public static void login(final String userId, final String sessionKey, final String redirect, DispatchAsync dispatchAsync, final HandlerManager eventBus) {
        final UserSessionState state = MMDB.getSessionState();
        state.setSessionKey(sessionKey);
        // set cookie
        // TODO move to more prominent place... MMDB? A class with static properties?
        final long DURATION = 1000 * 60 * 60; // 60 minutes
        final Date expires = new Date(System.currentTimeMillis() + DURATION);
        Cookies.setCookie("sessionKey", sessionKey, expires);

        GetUser getUser = new GetUser();
        getUser.setUserId(userId);
        dispatchAsync.execute(getUser, new AsyncCallback<GetUserResult>() {

            @Override
            public void onFailure(Throwable caught) {
                GWT.log("Error retrieving user with id " + userId);
            }

            @Override
            public void onSuccess(GetUserResult result) {
                PersonBean personBean = result.getPersonBean();
                state.setCurrentUser(personBean);
                GWT.log("Current user set to " + personBean.getUri());
                Cookies.setCookie("sid", personBean.getUri(), expires);
                if (redirect != null) {
                    History.newItem(redirect);
                }
                eventBus.fireEvent(new LoggedInEvent(personBean));
            }
        });

    }

    public static void logout(HandlerManager eventBus) {
        UserSessionState state = MMDB.getSessionState();
        if (state.getCurrentUser() != null && state.getCurrentUser().getUri() != null) {
            GWT.log("user " + state.getCurrentUser().getUri() + " logging out", null);
            MMDB.clearSessionState();
        }
        // in case anyone is holding refs to the state, zero out the auth information in it
        state.setCurrentUser(null);
        state.setSessionKey(null);
        Cookies.removeCookie("sid");
        Cookies.removeCookie("sessionKey");
        clearBrowserCreds();
        eventBus.fireEvent(new LoggedOutEvent());
    }

    private static void clearBrowserCreds() {
        // now hit the REST authentication endpoint with bad creds
        String restUrl = "./api/logout";
        RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, restUrl);
        builder.setUser("_badCreds_");
        builder.setPassword("_reallyReallyBadCreds_");
        try {
            builder.sendRequest("", new RequestCallback() {
                public void onError(Request request, Throwable exception) {
                    // do something
                    Window.alert("error logging out " + exception.getMessage());
                }

                public void onResponseReceived(Request request, Response response) {
                    GWT.log("Cleared browser credentials by sending bad request.");
                }
            });
        } catch (RequestException x) {
            // another error condition, do something
            Window.alert("error logging out: " + x.getMessage());
        }
    }

    /**
     * Reload page after being logged in.
     * 
     * FIXME remove hardcoded paths
     */
    private void redirect() {
        if (History.getToken().startsWith("login")) {
            History.newItem("listDatasets", true);
        } else {
            History.fireCurrentHistoryState();
        }
    }
}
