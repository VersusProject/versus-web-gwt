/*
 * This software was developed at the National Institute of Standards and
 * Technology by employees of the Federal Government in the course of
 * their official duties. Pursuant to title 17 Section 105 of the United
 * States Code this software is not subject to copyright protection and is
 * in the public domain. This software is an experimental system. NIST assumes
 * no responsibility whatsoever for its use by other parties, and makes no
 * guarantees, expressed or implied, about its quality, reliability, or
 * any other characteristic. We would appreciate acknowledgement if the
 * software is used.
 */
package edu.illinois.ncsa.versus.web.client.view;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.*;
import edu.illinois.ncsa.mmdb.web.client.MMDB;
import edu.illinois.ncsa.mmdb.web.client.PermissionUtil;
import edu.illinois.ncsa.mmdb.web.client.event.LoggedInEvent;
import edu.illinois.ncsa.mmdb.web.client.event.LoggedInHandler;
import edu.illinois.ncsa.mmdb.web.client.event.LoggedOutEvent;
import edu.illinois.ncsa.mmdb.web.client.event.LoggedOutHandler;
import edu.uiuc.ncsa.cet.bean.rbac.medici.Permission;
import net.customware.gwt.dispatch.client.DispatchAsync;

/**
 *
 * @author antoinev
 */
public class UserMenu extends Composite {

    private final HorizontalPanel mainPanel;
    private final Hyperlink loginAnchor;
    private final Hyperlink signupAnchor;
    private final DispatchAsync dispatch;
    private PermissionUtil permissionUtil;

    public UserMenu(HandlerManager eventBus, DispatchAsync dispatchAsync) {
        dispatch = dispatchAsync;
        permissionUtil = new PermissionUtil(dispatch);

        mainPanel = new HorizontalPanel();
        mainPanel.addStyleName("navMenu");
        initWidget(mainPanel);

        // login anchor
        loginAnchor = new Hyperlink("Login", "login");
        loginAnchor.addStyleName("navMenuLink");

        // signup anchor
        signupAnchor = new Hyperlink("Sign up", "signup");
        signupAnchor.addStyleName("navMenuLink");

        if (MMDB.getUsername() == null) {
            mainPanel.add(loginAnchor);
            mainPanel.add(signupAnchor);
        }

        eventBus.addHandler(LoggedInEvent.TYPE, new LoggedInHandler() {

            @Override
            public void onLoggedIn(LoggedInEvent loggedInEvent) {
                buildMenu(loggedInEvent);
            }
        });

        eventBus.addHandler(LoggedOutEvent.TYPE, new LoggedOutHandler() {

            @Override
            public void onLoggedOut(LoggedOutEvent loggedOutEvent) {
                //FIXME: When logged out, clear permissions cache
                permissionUtil = new PermissionUtil(dispatch);
                mainPanel.clear();
                mainPanel.add(loginAnchor);
                mainPanel.add(signupAnchor);
            }
        });
    }

    private void buildMenu(LoggedInEvent loggedInEvent) {
        mainPanel.clear();

        final MenuBar options = new MenuBar(true);
        options.insertItem(new MenuItem("Logout", new Command() {

            @Override
            public void execute() {
                History.newItem("logout");
            }
        }), 0);


        permissionUtil.doIfAllowed(Permission.VIEW_ADMIN_PAGES, new PermissionUtil.PermissionCallback() {

            @Override
            public void onAllowed() {
                options.insertItem(new MenuItem("Access control", new Command() {

                    @Override
                    public void execute() {
                        History.newItem("accessControl");
                    }
                }), 0);
                options.insertItem(new MenuItem("Users management", new Command() {

                    @Override
                    public void execute() {
                        History.newItem("modifyPermissions");
                    }
                }), 0);
                options.insertSeparator(2);
            }
        });


        final PopupPanel popup = new PopupPanel(true, true);
        popup.add(options);

        String name = loggedInEvent.getUser().getName();

        final Label label = new Label(name);

        label.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                if (popup.isShowing()) {
                    popup.hide();
                } else {
                    final int parentRight = label.getAbsoluteLeft() + label.getOffsetWidth();
                    final int parentBottom = label.getAbsoluteTop() + label.getOffsetHeight();

                    popup.setPopupPositionAndShow(new PopupPanel.PositionCallback() {

                        @Override
                        public void setPosition(int offsetWidth, int offsetHeight) {
                            int left = parentRight - offsetWidth;
                            int top = parentBottom - 1;
                            popup.setPopupPosition(left, top);
                        }
                    });
                }
            }
        });

        popup.addAutoHidePartner(label.getElement());
        mainPanel.add(label);
    }
}
