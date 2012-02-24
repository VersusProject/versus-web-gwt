package edu.illinois.ncsa.versus.web.client;

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.*;

import edu.illinois.ncsa.mmdb.web.client.MMDB;
import edu.illinois.ncsa.mmdb.web.client.PermissionUtil;
import edu.illinois.ncsa.mmdb.web.client.PermissionUtil.PermissionCallback;
import edu.illinois.ncsa.mmdb.web.client.dispatch.MyDispatchAsync;
import edu.illinois.ncsa.versus.web.client.event.LoggedInEvent;
import edu.illinois.ncsa.versus.web.client.event.LoggedInHandler;
import edu.illinois.ncsa.versus.web.client.event.LoggedOutEvent;
import edu.illinois.ncsa.versus.web.client.event.LoggedOutHandler;
import edu.illinois.ncsa.mmdb.web.client.ui.*;
import edu.illinois.ncsa.mmdb.web.client.ui.admin.RoleAdministrationWidget;
import edu.illinois.ncsa.mmdb.web.client.ui.admin.UserManagementWidget;
import edu.illinois.ncsa.versus.web.client.event.NewJobEvent;
import edu.illinois.ncsa.versus.web.client.event.NewJobHandler;
import edu.illinois.ncsa.versus.web.client.presenter.JobStatusPresenter;
import edu.illinois.ncsa.versus.web.client.ui.MainMenu;
import edu.illinois.ncsa.versus.web.client.ui.UserMenu;
import edu.illinois.ncsa.versus.web.client.ui.Workflow;
import edu.illinois.ncsa.versus.web.client.view.JobStatusView;
import edu.illinois.ncsa.versus.web.client.view.ListThumbails;
import edu.illinois.ncsa.versus.web.shared.Job;
import edu.illinois.ncsa.versus.web.shared.Submission;
import edu.uiuc.ncsa.cet.bean.rbac.medici.Permission;
import java.util.HashMap;
import java.util.Map;
import net.customware.gwt.dispatch.client.DispatchAsync;

/**
 * Entry point classes define
 * <code>onModuleLoad()</code>.
 */
public class Versus_web implements EntryPoint, ValueChangeHandler<String> {

    private static final HandlerManager eventBus = MMDB.eventBus;
    private Workflow workflowWidget;
    private DockLayoutPanel appPanel;
    private ListThumbails listThumbails;
    private SimpleLayoutPanel centralPanel;
    public static final DispatchAsync dispatchAsync = new MyDispatchAsync();
    private PermissionUtil permissionUtil;

    /**
     * This is the entry point method.
     */
    @Override
    public void onModuleLoad() {

        // HACK required by PreviewWidget
//		MMDB.getSessionState().setCurrentUser(new PersonBean());

//        eventBus = new HandlerManager(null);

        permissionUtil = new PermissionUtil(dispatchAsync);

        // drag and drop
        PickupDragController dragController = new PickupDragController(
                RootPanel.get(), true);
        appPanel = new DockLayoutPanel(Unit.EM);

        // header panel
        HorizontalPanel headerPanel = new HorizontalPanel();
        headerPanel.addStyleName("headerPanel");
        headerPanel.add(new MainMenu());


        // login status widget        
        UserMenu userMenu = new UserMenu(eventBus, dispatchAsync);
        userMenu.addStyleName("loginWidget");
        headerPanel.add(userMenu);

        centralPanel = new SimpleLayoutPanel();
        centralPanel.addStyleName("centralPanelLayout");

        // footer
        listThumbails = new ListThumbails(dispatchAsync, eventBus, dragController);

        // // drop box panel
        // HorizontalPanel dropBoxPanel = new HorizontalPanel();
        // dropBoxPanel.addStyleName("dropBoxesPanel");
        // content.add(dropBoxPanel);
        // // first drop box
        // DropBoxView firstDropBox = new DropBoxView();
        // DropBoxPresenter firstDropBoxPresenter = new
        // DropBoxPresenter(registryService, eventBus, firstDropBox);
        // firstDropBoxPresenter.go(dropBoxPanel);
        // dragController.registerDropController(firstDropBox.getDropController());
        // // second drop box
        // DropBoxView secondDropBox = new DropBoxView();
        // DropBoxPresenter secondDropBoxPresenter = new
        // DropBoxPresenter(registryService, eventBus, secondDropBox);
        // secondDropBoxPresenter.go(dropBoxPanel);
        // dragController.registerDropController(secondDropBox.getDropController());metadata.getId(),
        // metadata.getName()
        //
        // // testing drop targets
        // HorizontalPanel dropTarget = new HorizontalPanel();
        // dropTarget.setBorderWidth(1);
        // dropTarget.setSize("500px", "100px");
        // HorizontalPanelDropController dropController = new
        // HorizontalPanelDropController(dropTarget);
        // dragController.registerDropController(dropController);
        // content.add(dropTarget);

        appPanel.addNorth(headerPanel, 2.5);
        appPanel.addSouth(listThumbails, 10);
        appPanel.add(centralPanel);
        RootLayoutPanel.get().add(appPanel);

        bind();

        // history support
        History.addValueChangeHandler(this);
        History.fireCurrentHistoryState();
    }

    private void bind() {
        eventBus.addHandler(NewJobEvent.TYPE, new NewJobHandler() {

            @Override
            public void onNewJob(NewJobEvent newJobEvent) {
                Job job = newJobEvent.getJob();
                Submission submission = newJobEvent.getSubmission();
                JobStatusView jobStatusView = new JobStatusView(dispatchAsync);
                JobStatusPresenter jobStatusPresenter = new JobStatusPresenter(
                        eventBus, jobStatusView, job, submission);
                HasWidgets resultWidget = workflowWidget.getResultWidget();
                jobStatusPresenter.go(resultWidget);
                resultWidget.add(jobStatusView);
            }
        });

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
                History.newItem("login");
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
            centralPanel.add(new edu.illinois.ncsa.versus.web.client.ui.LoginPage(dispatchAsync, eventBus)); // FIXME should not have to instantiate MMDB
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
        } else if (token.startsWith("listCollections")) {
            centralPanel.clear();
            centralPanel.add(new ListCollectionsPage(dispatchAsync, eventBus));
        } else if (token.startsWith("upload")) {
            centralPanel.clear();
            centralPanel.add(new edu.illinois.ncsa.versus.web.client.ui.UploadPage(dispatchAsync));
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
}
