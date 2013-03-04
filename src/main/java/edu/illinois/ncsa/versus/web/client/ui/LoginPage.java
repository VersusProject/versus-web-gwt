/**
 * *****************************************************************************
 * University of Illinois/NCSA Open Source License
 *
 * Copyright (c) 2010, NCSA. All rights reserved.
 *
 * Developed by: Cyberenvironments and Technologies (CET)
 * http://cet.ncsa.illinois.edu/
 *
 * National Center for Supercomputing Applications (NCSA)
 * http://www.ncsa.illinois.edu/
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * with the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * - Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimers. - Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimers in the documentation and/or other materials
 * provided with the distribution. - Neither the names of CET, University of
 * Illinois/NCSA, nor the names of its contributors may be used to endorse or
 * promote products derived from this Software without specific prior written
 * permission.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * CONTRIBUTORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS WITH
 * THE SOFTWARE.
 * *****************************************************************************
 */
/**
 *
 */
package edu.illinois.ncsa.versus.web.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.http.client.*;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import edu.illinois.ncsa.mmdb.web.client.TextFormatter;
import edu.illinois.ncsa.mmdb.web.client.dispatch.Authenticate;
import edu.illinois.ncsa.mmdb.web.client.dispatch.AuthenticateResult;
import edu.illinois.ncsa.mmdb.web.client.ui.TitlePanel;
import edu.illinois.ncsa.versus.web.client.Authentication;
import edu.illinois.ncsa.versus.web.client.Versus_web;
import net.customware.gwt.dispatch.client.DispatchAsync;

/**
 * @author Luigi Marini
 *
 */
public class LoginPage extends Composite {

    private final FlowPanel mainPanel;
    private TextBox usernameBox;
    private PasswordTextBox passwordBox;
    private SimplePanel feedbackPanel;
    private Label progressLabel;
    private final DispatchAsync dispatchasync;
    private final HandlerManager eventBus;

    /**
     * @param dispatchasync
     *
     */
    public LoginPage(DispatchAsync dispatchasync, HandlerManager eventBus) {

        this.dispatchasync = dispatchasync;

        this.eventBus = eventBus;

        mainPanel = new FlowPanel();

        mainPanel.addStyleName("page");

        initWidget(mainPanel);

        // page title
        mainPanel.add(createPageTitle());

        // login form
        mainPanel.add(createLoginForm());
    }

    /**
     *
     * @return
     */
    private Widget createPageTitle() {
        return new TitlePanel("Login");
    }

    /**
     *
     * @return
     */
    private Widget createLoginForm() {
        FlexTable table = new FlexTable();

        table.addStyleName("loginForm");

        feedbackPanel = new SimplePanel();

        table.setWidget(0, 0, feedbackPanel);

        table.getFlexCellFormatter().setColSpan(0, 0, 2);

        table.getFlexCellFormatter().setHorizontalAlignment(0, 0,
                HasHorizontalAlignment.ALIGN_CENTER);

        Label usernameLabel = new Label("Email:");

        table.setWidget(1, 0, usernameLabel);

        usernameBox = new TextBox();

        usernameBox.setTabIndex(1);

        usernameBox.addKeyUpHandler(new KeyUpHandler() {

            @Override
            public void onKeyUp(KeyUpEvent event) {
                if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
                    authenticate();
                }

            }
        });

        table.setWidget(1, 1, usernameBox);

        DeferredCommand.addCommand(new Command() {

            @Override
            public void execute() {
                usernameBox.setFocus(true);
            }
        });

        // sign up
        table.setWidget(1, 3, new Hyperlink("Sign up", "signup"));

        Label passwordLabel = new Label("Password:");

        table.setWidget(2, 0, passwordLabel);

        passwordBox = new PasswordTextBox();

        passwordBox.setTabIndex(2);

        passwordBox.addKeyUpHandler(new KeyUpHandler() {

            @Override
            public void onKeyUp(KeyUpEvent event) {
                if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
                    authenticate();
                }

            }
        });

        table.setWidget(2, 1, passwordBox);

        // forgot password link
        table.setWidget(2, 3, new Hyperlink("Forgot Password?", "requestNewPassword"));

        Button submitButton = new Button("Login", new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                progressLabel.setText("Logging in...");
                authenticate();
            }
        });

        submitButton.setTabIndex(3);

        table.setWidget(3, 1, submitButton);

        progressLabel = new Label("");
        table.setWidget(4, 0, progressLabel);
        table.getFlexCellFormatter().setColSpan(4, 0, 2);
        table.getFlexCellFormatter().setHorizontalAlignment(4, 0, HasAlignment.ALIGN_CENTER);

        return table;
    }

    /**
     * Authenticate against the REST endpoint to make sure user is authenticated
     * on the server side. If successful, login local.
     */
    protected void authenticate() {
        final String username = usernameBox.getText();
        final String password = passwordBox.getText();
        dispatchasync.execute(new Authenticate(username, password),
                new AsyncCallback<AuthenticateResult>() {

                    @Override
                    public void onFailure(Throwable arg0) {
                        fail(arg0.toString());
                    }

                    @Override
                    public void onSuccess(final AuthenticateResult arg0) {
                        if (arg0.getAuthenticated()) {
                            // now hit the REST authentication endpoint
                            String restUrl = "./api/authenticate";
                            RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, restUrl);
                            builder.setUser(TextFormatter.escapeEmailAddress(username));
                            builder.setPassword(password);
                            try {
                                GWT.log("attempting to authenticate " + username + " against " + restUrl, null);
                                builder.sendRequest("", new RequestCallback() {

                                    @Override
                                    public void onError(Request request, Throwable exception) {
                                        fail("Error attempting to reach ./api/authenticate \n" + exception.toString());
                                    }

                                    @Override
                                    public void onResponseReceived(Request request, Response response) {
                                        // success!
                                        String sessionKey = response.getText();
                                        GWT.log("REST auth status code = " + response.getStatusCode(), null);
                                        if (response.getStatusCode() > 300) {
                                            GWT.log("authentication failed: " + sessionKey, null);
                                            fail(String.valueOf(response.getStatusCode()));
                                        }
                                        GWT.log("user " + username + " associated with session key " + sessionKey, null);
                                        // login local
                                        String redirect = Versus_web.getParams().get("p");
                                        Authentication.login(arg0.getSessionId(), sessionKey, redirect, dispatchasync, eventBus);
                                    }
                                });
                            } catch (RequestException x) {
                                // another error condition
                                fail(x.toString());
                            }
                        } else {
                            fail("Error authenticating on the server.");
                        }
                    }
                });
    }

    void fail(String error) {
        GWT.log("Failed authenticating", null);
        Label message = new Label(
                "Incorrect username/password combination" + "\n" + error);
        message.addStyleName("loginError");
        feedbackPanel.clear();
        feedbackPanel.add(message);
        progressLabel.setText("");
    }
}
