package edu.illinois.ncsa.versus.web.client;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.customware.gwt.dispatch.client.DispatchAsync;

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;

import edu.illinois.ncsa.mmdb.web.client.Authentication;
import edu.illinois.ncsa.mmdb.web.client.PermissionUtil;
import edu.illinois.ncsa.mmdb.web.client.PermissionUtil.PermissionCallback;
import edu.illinois.ncsa.mmdb.web.client.UploadWidget;
import edu.illinois.ncsa.mmdb.web.client.dispatch.MyDispatchAsync;
import edu.illinois.ncsa.mmdb.web.client.dispatch.JiraIssue.JiraIssueType;
import edu.illinois.ncsa.mmdb.web.client.event.DatasetUploadedEvent;
import edu.illinois.ncsa.mmdb.web.client.event.DatasetUploadedHandler;
import edu.illinois.ncsa.mmdb.web.client.event.LoggedInEvent;
import edu.illinois.ncsa.mmdb.web.client.event.LoggedInHandler;
import edu.illinois.ncsa.mmdb.web.client.event.LoggedOutEvent;
import edu.illinois.ncsa.mmdb.web.client.event.LoggedOutHandler;
import edu.illinois.ncsa.mmdb.web.client.presenter.DatasetTablePresenter;
import edu.illinois.ncsa.mmdb.web.client.ui.DatasetWidget;
import edu.illinois.ncsa.mmdb.web.client.ui.HomePage;
import edu.illinois.ncsa.mmdb.web.client.ui.JiraIssuePage;
import edu.illinois.ncsa.mmdb.web.client.ui.LoginPage;
import edu.illinois.ncsa.mmdb.web.client.ui.LoginStatusWidget;
import edu.illinois.ncsa.mmdb.web.client.ui.RequestNewPasswordPage;
import edu.illinois.ncsa.mmdb.web.client.ui.RoleAdministrationPage;
import edu.illinois.ncsa.mmdb.web.client.ui.SignupPage;
import edu.illinois.ncsa.mmdb.web.client.ui.UserManagementPage;
import edu.illinois.ncsa.mmdb.web.client.view.DynamicTableView;
import edu.illinois.ncsa.versus.web.client.event.AddAdapterEvent;
import edu.illinois.ncsa.versus.web.client.event.AddExtractorEvent;
import edu.illinois.ncsa.versus.web.client.event.AddMeasureEvent;
import edu.illinois.ncsa.versus.web.client.event.NewJobEvent;
import edu.illinois.ncsa.versus.web.client.event.NewJobHandler;
import edu.illinois.ncsa.versus.web.client.presenter.CurrentSelectionsPresenter;
import edu.illinois.ncsa.versus.web.client.presenter.JobStatusPresenter;
import edu.illinois.ncsa.versus.web.client.presenter.SelectAdapterPresenter;
import edu.illinois.ncsa.versus.web.client.presenter.SelectExtractorPresenter;
import edu.illinois.ncsa.versus.web.client.presenter.SelectMeasurePresenter;
import edu.illinois.ncsa.versus.web.client.view.CurrentSelectionsView;
import edu.illinois.ncsa.versus.web.client.view.JobStatusView;
import edu.illinois.ncsa.versus.web.client.view.ListThumbails;
import edu.illinois.ncsa.versus.web.client.view.SelectAdapterView;
import edu.illinois.ncsa.versus.web.client.view.SelectExtractorView;
import edu.illinois.ncsa.versus.web.client.view.SelectMeasureView;
import edu.illinois.ncsa.versus.web.shared.ComponentMetadata;
import edu.illinois.ncsa.versus.web.shared.Job;
import edu.illinois.ncsa.versus.web.shared.Submission;
import edu.uiuc.ncsa.cet.bean.rbac.medici.Permission;

/**
 * Entry point classes define
 * <code>onModuleLoad()</code>.
 */
public class Versus_web implements EntryPoint, ValueChangeHandler<String> {

    private final RegistryServiceAsync registryService = GWT.create(RegistryService.class);
    private HandlerManager eventBus;
    private UploadWidget uploadWidget;
    private VerticalPanel previousDisclosureBody;
    private DockLayoutPanel appPanel;
    private DatasetTablePresenter datasetTablePresenter;
    private ListThumbails listThumbails;
    private HorizontalPanel headerPanel;
    private TabLayoutPanel tabPanel;
    private SimpleLayoutPanel centralPanel;
    public static final DispatchAsync dispatchAsync = new MyDispatchAsync();
    private PermissionUtil permissionUtil;

    /**
     * This is the entry point method.
     */
    public void onModuleLoad() {

        // HACK required by PreviewWidget
//		MMDB.getSessionState().setCurrentUser(new PersonBean());

        eventBus = new HandlerManager(null);

        permissionUtil = new PermissionUtil(dispatchAsync);

        // drag and drop
        PickupDragController dragController = new PickupDragController(
                RootPanel.get(), true);
        appPanel = new DockLayoutPanel(Unit.EM);

        // header panel
        headerPanel = new HorizontalPanel();
        headerPanel.addStyleName("headerPanel");
        Hyperlink homeLink = new Hyperlink("Versus", "");
        homeLink.addStyleName("logo");
        headerPanel.add(homeLink);
        headerPanel.setCellHorizontalAlignment(homeLink,
                HasHorizontalAlignment.ALIGN_CENTER);
        headerPanel.setCellWidth(homeLink, "140px");

        headerPanel.add(homeLink);

        // login status widget
        LoginStatusWidget loginStatusWidget = new LoginStatusWidget(eventBus);
        loginStatusWidget.addStyleName("loginWidget");
        headerPanel.add(loginStatusWidget);

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

        appPanel.addNorth(headerPanel, 2);
        appPanel.addSouth(listThumbails, 10);
        appPanel.add(centralPanel);
        RootLayoutPanel.get().add(appPanel);

        bind();

        // history support
        History.addValueChangeHandler(this);
        History.fireCurrentHistoryState();
    }

    private Widget createWorkflowTabs() {

        VerticalPanel newExecutionPanel = new VerticalPanel();
        newExecutionPanel.setWidth("100%");

        HorizontalPanel selectionPanel = new HorizontalPanel();
        selectionPanel.setBorderWidth(0);
        selectionPanel.setWidth("100%");
        newExecutionPanel.add(selectionPanel);

        // adapters
        SelectAdapterView selectAdapterView = new SelectAdapterView();
        SelectAdapterPresenter selectAdapterPresenter = new SelectAdapterPresenter(
                registryService, eventBus, selectAdapterView);
        selectAdapterPresenter.go(selectionPanel);

        // extractors
        SelectExtractorView selectExtractorView = new SelectExtractorView();
        SelectExtractorPresenter selectExtractorPresenter = new SelectExtractorPresenter(
                registryService, eventBus, selectExtractorView);
        selectExtractorPresenter.go(selectionPanel);

        // measures
        SelectMeasureView selectMeasureView = new SelectMeasureView();
        SelectMeasurePresenter selectMeasurePresenter = new SelectMeasurePresenter(
                registryService, eventBus, selectMeasureView);
        selectMeasurePresenter.go(selectionPanel);

        // current selections
        SimplePanel currentSelectionPanel = new SimplePanel();
        CurrentSelectionsView currentSelectionsView = new CurrentSelectionsView();
        CurrentSelectionsPresenter currentSelectionsPresenter = new CurrentSelectionsPresenter(
                registryService, eventBus, currentSelectionsView);
        currentSelectionsPresenter.go(currentSelectionPanel);

        DockLayoutPanel comparePanel = new DockLayoutPanel(Unit.EM);
        comparePanel.addSouth(currentSelectionPanel, 8);
        comparePanel.add(new ScrollPanel(newExecutionPanel));

        // previous executions
        previousDisclosureBody = new VerticalPanel();
        previousDisclosureBody.setWidth("95%");
        ScrollPanel previousExecScroll = new ScrollPanel(previousDisclosureBody);

        // dataset selection
        DynamicTableView datasetTableView = new DynamicTableView();
        datasetTablePresenter = new DatasetTablePresenter(dispatchAsync,
                eventBus, datasetTableView);
        datasetTablePresenter.bind();
        VerticalPanel selectionDisclosureBody = new VerticalPanel();
        selectionDisclosureBody.setWidth("100%");
        selectionDisclosureBody.add(datasetTableView.asWidget());

        // upload widget
        uploadWidget = new UploadWidget(false);
        uploadWidget.addDatasetUploadedHandler(new DatasetUploadedHandler() {

            @Override
            public void onDatasetUploaded(DatasetUploadedEvent event) {
                GWT.log("Done uploading file");
                datasetTablePresenter.refresh();
            }
        });
        selectionDisclosureBody.add(uploadWidget);


        // tab panel layout
        tabPanel = new TabLayoutPanel(2, Unit.EM);
        tabPanel.setWidth("100%");
        tabPanel.add(selectionDisclosureBody, "Select Data");
        tabPanel.add(comparePanel, "Compare");
        tabPanel.add(previousExecScroll, "View Results");
        tabPanel.selectTab(0);

        populate();
        datasetTablePresenter.refresh();

        return tabPanel;
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
                jobStatusPresenter.go(previousDisclosureBody);
                previousDisclosureBody.add(jobStatusView);
            }
        });

        eventBus.addHandler(LoggedInEvent.TYPE, new LoggedInHandler() {

            @Override
            public void onLoggedIn(LoggedInEvent loggedInEvent) {
                parseHistoryToken(History.getToken());
            }
        });

        eventBus.addHandler(LoggedOutEvent.TYPE, new LoggedOutHandler() {

            @Override
            public void onLoggedOut(LoggedOutEvent loggedOutEvent) {
                History.newItem("login");
            }
        });
    }

    private void populate() {

        registryService.getAdapters(new AsyncCallback<List<ComponentMetadata>>() {

            @Override
            public void onFailure(Throwable caught) {
                GWT.log("Error retrieving adapters", caught);
            }

            @Override
            public void onSuccess(List<ComponentMetadata> result) {
                for (ComponentMetadata metadata : result) {
                    eventBus.fireEvent(new AddAdapterEvent(metadata));
                }
            }
        });

        registryService.getExtractors(new AsyncCallback<List<ComponentMetadata>>() {

            @Override
            public void onSuccess(List<ComponentMetadata> result) {
                for (ComponentMetadata metadata : result) {
                    eventBus.fireEvent(new AddExtractorEvent(metadata));
                }
            }

            @Override
            public void onFailure(Throwable caught) {
                GWT.log("Error retrieving extractors", caught);
            }
        });

        registryService.getMeasures(new AsyncCallback<List<ComponentMetadata>>() {

            @Override
            public void onSuccess(List<ComponentMetadata> result) {
                for (ComponentMetadata metadata : result) {
                    eventBus.fireEvent(new AddMeasureEvent(metadata));
                }
            }

            @Override
            public void onFailure(Throwable caught) {
                GWT.log("Error retrieving measures", caught);
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
            showLoginPage();
        } else if (token.startsWith("signup")) {
            centralPanel.clear();
            centralPanel.add(new SignupPage(dispatchAsync));
        } else if (token.startsWith("requestNewPassword")) {
            centralPanel.clear();
            centralPanel.add(new RequestNewPasswordPage(dispatchAsync));
        } else if (token.startsWith("jiraBug")) {
            centralPanel.clear();
            centralPanel.add(new JiraIssuePage(dispatchAsync, JiraIssueType.BUG));
        } else if (token.startsWith("jiraFeature")) {
            centralPanel.clear();
            centralPanel.add(new JiraIssuePage(dispatchAsync, JiraIssueType.FEATURE));
        } else {
            Authentication.checkLogin(dispatchAsync, eventBus);
        }
    }

    private void parseHistoryToken(String token) {
        if (token.startsWith("home")) {
            centralPanel.clear();
            centralPanel.add(new HomePage(dispatchAsync));
        } else if (token.startsWith("dataset")) {
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
                    centralPanel.add(new UserManagementPage(dispatchAsync));
                }
            });
        } else if (token.startsWith("accessControl")) {
            permissionUtil.doIfAllowed(Permission.VIEW_ADMIN_PAGES, new PermissionCallback() {

                @Override
                public void onAllowed() {
                    centralPanel.clear();
                    centralPanel.add(new RoleAdministrationPage(dispatchAsync));
                }
            });
        } else {
            centralPanel.clear();
            centralPanel.add(createWorkflowTabs());
        }
    }

    protected void showLoginPage() {
        centralPanel.clear();
        centralPanel.add(new LoginPage(dispatchAsync, eventBus)); // FIXME should not have to instantiate MMDB
    }

    public Map<String, String> getParams() {
        Map<String, String> params = new HashMap<String, String>();
        String paramString = History.getToken().substring(
                History.getToken().indexOf("?") + 1);
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
