package edu.illinois.ncsa.versus.web.client;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.ScrollPanel;

import edu.illinois.ncsa.mmdb.web.client.MMDB;
import edu.illinois.ncsa.mmdb.web.client.PermissionUtil;
import edu.illinois.ncsa.mmdb.web.client.PermissionUtil.PermissionCallback;
import edu.illinois.ncsa.mmdb.web.client.dispatch.MyDispatchAsync;
import edu.illinois.ncsa.mmdb.web.client.event.AllDatasetsUnselectedEvent;
import edu.illinois.ncsa.mmdb.web.client.event.AllDatasetsUnselectedHandler;
import edu.illinois.ncsa.mmdb.web.client.event.DatasetSelectedEvent;
import edu.illinois.ncsa.mmdb.web.client.event.DatasetSelectedHandler;
import edu.illinois.ncsa.mmdb.web.client.event.DatasetUnselectedEvent;
import edu.illinois.ncsa.mmdb.web.client.event.DatasetUnselectedHandler;
import edu.illinois.ncsa.mmdb.web.client.ui.CollectionPage;
import edu.illinois.ncsa.mmdb.web.client.ui.DatasetWidget;
import edu.illinois.ncsa.mmdb.web.client.ui.ListDatasetsPage;
import edu.illinois.ncsa.mmdb.web.client.ui.ProfileWidget;
import edu.illinois.ncsa.mmdb.web.client.ui.RequestNewPasswordPage;
import edu.illinois.ncsa.mmdb.web.client.ui.SignupPage;
import edu.illinois.ncsa.mmdb.web.client.ui.UploadPage;
import edu.illinois.ncsa.mmdb.web.client.ui.admin.RoleAdministrationWidget;
import edu.illinois.ncsa.mmdb.web.client.ui.admin.UserManagementWidget;
import edu.illinois.ncsa.versus.web.client.event.LoggedInEvent;
import edu.illinois.ncsa.versus.web.client.event.LoggedInHandler;
import edu.illinois.ncsa.versus.web.client.event.LoggedOutEvent;
import edu.illinois.ncsa.versus.web.client.event.LoggedOutHandler;
import edu.illinois.ncsa.versus.web.client.ui.ListCollectionsPage;
import edu.illinois.ncsa.versus.web.client.ui.LoginPage;
import edu.illinois.ncsa.versus.web.client.ui.MainMenu;
import edu.illinois.ncsa.versus.web.client.ui.UserMenu;
import edu.illinois.ncsa.versus.web.client.ui.Workflow;
import edu.uiuc.ncsa.cet.bean.rbac.medici.Permission;
import net.customware.gwt.dispatch.client.DispatchAsync;

/**
 * Entry point classes define
 * <code>onModuleLoad()</code>.
 */
public class Versus_web implements EntryPoint, ValueChangeHandler<String> {

    private static final HandlerManager eventBus = MMDB.eventBus;

    private Workflow workflowWidget;

    private DockLayoutPanel appPanel;

    private ScrollPanel centralPanel;

    public static final DispatchAsync dispatchAsync = new MyDispatchAsync();

    private PermissionUtil permissionUtil;

    /**
     * This is the entry point method.
     */
    @Override
    public void onModuleLoad() {

        permissionUtil = new PermissionUtil(dispatchAsync);

        appPanel = new DockLayoutPanel(Unit.EM);

        // header panel
        HorizontalPanel headerPanel = new HorizontalPanel();
        headerPanel.addStyleName("headerPanel");
        headerPanel.add(new MainMenu());

        // login status widget        
        UserMenu userMenu = new UserMenu(eventBus, dispatchAsync);
        userMenu.addStyleName("loginWidget");
        headerPanel.add(userMenu);

        centralPanel = new ScrollPanel();
        centralPanel.addStyleName("centralPanelLayout");

        appPanel.addNorth(headerPanel, 2.5);
        appPanel.add(centralPanel);
        RootLayoutPanel.get().add(appPanel);
        
        bind();

        // history support
        History.addValueChangeHandler(this);
        History.fireCurrentHistoryState();
    }

    private void bind() {
        eventBus.addHandler(LoggedInEvent.TYPE, new LoggedInHandler() {

            @Override
            public void onLoggedIn(LoggedInEvent loggedInEvent) {
                final String token = History.getToken();
                if (token.startsWith("login")) {
                    History.newItem("", true);
                } else {
                    parseHistoryToken(token);
                }
            }
        });

        eventBus.addHandler(LoggedOutEvent.TYPE, new LoggedOutHandler() {

            @Override
            public void onLoggedOut(LoggedOutEvent loggedOutEvent) {
                //FIXME: When logged out, clear permissions cache
                permissionUtil = new PermissionUtil(dispatchAsync);
                sessionState = null;
                History.newItem("login");
            }
        });

        eventBus.addHandler(DatasetSelectedEvent.TYPE, new DatasetSelectedHandler() {

            @Override
            public void onDatasetSelected(DatasetSelectedEvent event) {
                GWT.log("Dataset selected " + event.getUri());
                MMDB.getSessionState().datasetSelected(event.getUri());
                getSessionState().datasetSelected(event.getUri());
            }
        });

        eventBus.addHandler(DatasetUnselectedEvent.TYPE, new DatasetUnselectedHandler() {

            @Override
            public void onDatasetUnselected(DatasetUnselectedEvent event) {
                GWT.log("Dataset unselected " + event.getUri());
                MMDB.getSessionState().datasetUnselected(event.getUri());
                getSessionState().datasetUnselected(event.getUri());
            }
        });

        eventBus.addHandler(AllDatasetsUnselectedEvent.TYPE, new AllDatasetsUnselectedHandler() {

            @Override
            public void onAllDatasetsUnselected(AllDatasetsUnselectedEvent event) {
                GWT.log("All datasets unselected");
                Set<String> toDeselect = new HashSet<String>(MMDB.getSessionState().getSelectedDatasets());
                for (String datasetUri : toDeselect) {
                    DatasetUnselectedEvent ue = new DatasetUnselectedEvent();
                    ue.setUri(datasetUri);
                    eventBus.fireEvent(ue);
                }
            }
        });
    }

    @Override
    public void onValueChange(ValueChangeEvent<String> event) {
        final String token = event.getValue();

        GWT.log("History changed: " + event.getValue(), null);

        if (token.startsWith("logout")) {
            Authentication.logout(eventBus);
        } else if (token.startsWith("login")) {
            centralPanel.clear();
            centralPanel.add(new LoginPage(dispatchAsync, eventBus)); // FIXME should not have to instantiate MMDB
        } else if (token.startsWith("signup")) {
            centralPanel.clear();
            centralPanel.add(new SignupPage(dispatchAsync));
        } else if (token.startsWith("requestNewPassword")) {
            centralPanel.clear();
            centralPanel.add(new RequestNewPasswordPage(dispatchAsync));
        } else {
            Authentication.checkLogin(dispatchAsync, eventBus);
        }
    }

    private void parseHistoryToken(String token) {
        if (token.startsWith("dataset")) {
            Map<String, String> params = getParams();
            final String datasetUri = params.get("id");
            final String section = params.get("section");
            DatasetWidget datasetWidget = new DatasetWidget(dispatchAsync, eventBus);
            centralPanel.clear();
            centralPanel.add(datasetWidget);
            if (datasetUri != null) {
                if (section != null) {
                    datasetWidget.showDataset(datasetUri, URL.decode(section));
                } else {
                    datasetWidget.showDataset(datasetUri, null);
                }
            }
        } else if (token.startsWith("modifyPermissions")) {
            permissionUtil.doIfAllowed(Permission.VIEW_ADMIN_PAGES, new PermissionCallback() {

                @Override
                public void onAllowed() {
                    centralPanel.clear();
                    centralPanel.add(new UserManagementWidget(dispatchAsync));
                }
            });
        } else if (token.startsWith("accessControl")) {
            permissionUtil.doIfAllowed(Permission.VIEW_ADMIN_PAGES, new PermissionCallback() {

                @Override
                public void onAllowed() {
                    centralPanel.clear();
                    centralPanel.add(new RoleAdministrationWidget(dispatchAsync));
                }
            });
        } else if (token.startsWith("profile")) {
            centralPanel.clear();
            centralPanel.add(new ProfileWidget(dispatchAsync));
        } else if (token.startsWith("listDatasets")) {
            centralPanel.clear();
            centralPanel.add(new ListDatasetsPage(dispatchAsync, eventBus));
        } else if (token.startsWith("listCollections")) {
            centralPanel.clear();
            centralPanel.add(new ListCollectionsPage(dispatchAsync, eventBus));
        } else if (token.startsWith("collection")) {
            centralPanel.clear();
            centralPanel.add(new CollectionPage(getParams().get("uri"),
                    dispatchAsync, eventBus));
        } else if (token.startsWith("upload")) {
            centralPanel.clear();
            centralPanel.add(new UploadPage(dispatchAsync));
        } else {
            centralPanel.clear();
            workflowWidget = new Workflow(dispatchAsync, eventBus);
            centralPanel.add(workflowWidget);
        }
    }

    public static Map<String, String> getParams() {
        Map<String, String> params = new HashMap<String, String>(16);
        String paramString = History.getToken().substring(
                History.getToken().indexOf('?') + 1);
        if (!paramString.isEmpty()) {
            for (String paramEntry : paramString.split("&")) {
                String[] terms = paramEntry.split("=");
                if (terms.length == 2) {
                    params.put(terms[0], terms[1]);
                }
            }
        }
        return params;
    }
    
    private static VersusUserSessionState sessionState;
    
    public static VersusUserSessionState getSessionState() {
        if(sessionState == null) {
            sessionState = new VersusUserSessionState();
        }
        return sessionState;
    }
}
