package edu.illinois.ncsa.versus.web.client;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.illinois.ncsa.mmdb.web.client.MMDB;
import edu.illinois.ncsa.mmdb.web.client.UploadWidget;
import edu.illinois.ncsa.mmdb.web.client.dispatch.MyDispatchAsync;
import edu.illinois.ncsa.mmdb.web.client.event.DatasetUploadedEvent;
import edu.illinois.ncsa.mmdb.web.client.event.DatasetUploadedHandler;
import edu.illinois.ncsa.mmdb.web.client.presenter.DatasetTablePresenter;
import edu.illinois.ncsa.mmdb.web.client.ui.DatasetWidget;
import edu.illinois.ncsa.mmdb.web.client.ui.LoginPage;
import edu.illinois.ncsa.mmdb.web.client.view.DynamicTableView;
import edu.illinois.ncsa.versus.web.client.event.NewJobEvent;
import edu.illinois.ncsa.versus.web.client.event.NewJobHandler;
import edu.illinois.ncsa.versus.web.client.event.AddAdapterEvent;
import edu.illinois.ncsa.versus.web.client.event.AddExtractorEvent;
import edu.illinois.ncsa.versus.web.client.event.AddMeasureEvent;
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
import edu.uiuc.ncsa.cet.bean.PersonBean;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Versus_web implements EntryPoint, ValueChangeHandler<String> {
	private final RegistryServiceAsync registryService = GWT.create(RegistryService.class);

	private FlowPanel content;

	private HandlerManager eventBus;

	private UploadWidget uploadWidget;

	private VerticalPanel previousDisclosureBody;

	private DisclosurePanel previousDisclosure;

	private DockLayoutPanel appPanel;

	private ScrollPanel contentScrollPanel;

	private DatasetTablePresenter datasetTablePresenter;

	private ListThumbails listThumbails;

	private HTML logo;

	private HorizontalPanel headerPanel;
	
	public static final MyDispatchAsync dispatchAsync        = new MyDispatchAsync();

	private static final boolean DISCLOSURE_OPEN = false;

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		
		// HACK required by PreviewWidget
		MMDB.getSessionState().setCurrentUser(new PersonBean());
		
		eventBus = new HandlerManager(null);
		
		// drag and drop
		PickupDragController dragController = new PickupDragController(RootPanel.get(), true);
		appPanel = new DockLayoutPanel(Unit.EM);
		
		// header panel
		headerPanel = new HorizontalPanel();
		headerPanel.addStyleName("headerPanel");
		logo = new HTML("Versus");
		headerPanel.add(logo);
		headerPanel.setCellHorizontalAlignment(logo, HasHorizontalAlignment.ALIGN_CENTER);
		headerPanel.setCellWidth(logo, "140px");
		Hyperlink homeLink = new Hyperlink("Home", "");
		headerPanel.add(homeLink);
		
		// footer
		HTML footer = new HTML("");
		footer.addStyleName("footer");
		
		// left content
		listThumbails = new ListThumbails(eventBus, dragController);
		content = new FlowPanel();
		content.addStyleName("contentPanel");
		
//		// drop box panel
//		HorizontalPanel dropBoxPanel = new HorizontalPanel();
//		dropBoxPanel.addStyleName("dropBoxesPanel");
//		content.add(dropBoxPanel);
//		// first drop box
//		DropBoxView firstDropBox = new DropBoxView();
//		DropBoxPresenter firstDropBoxPresenter  = new DropBoxPresenter(registryService, eventBus, firstDropBox);
//		firstDropBoxPresenter.go(dropBoxPanel);
//		dragController.registerDropController(firstDropBox.getDropController());
//		// second drop box
//		DropBoxView secondDropBox = new DropBoxView();
//		DropBoxPresenter secondDropBoxPresenter  = new DropBoxPresenter(registryService, eventBus, secondDropBox);
//		secondDropBoxPresenter.go(dropBoxPanel);
//		dragController.registerDropController(secondDropBox.getDropController());metadata.getId(), metadata.getName()
//		
		
		DisclosurePanel newExecutionDisclosure = new DisclosurePanel("Launch New Comparison");
		newExecutionDisclosure.addStyleName("mainSection");
		newExecutionDisclosure.setAnimationEnabled(true);
		newExecutionDisclosure.setOpen(DISCLOSURE_OPEN);
		VerticalPanel newExecutionPanel = new VerticalPanel();
		newExecutionPanel.setWidth("100%");
		newExecutionDisclosure.add(newExecutionPanel);
		
		
//		// testing drop targets
//		HorizontalPanel dropTarget = new HorizontalPanel();
//		dropTarget.setBorderWidth(1);
//		dropTarget.setSize("500px", "100px");
//		HorizontalPanelDropController dropController = new HorizontalPanelDropController(dropTarget);
//		dragController.registerDropController(dropController);
//		content.add(dropTarget);
		
		HorizontalPanel selectionPanel = new HorizontalPanel();
		selectionPanel.setBorderWidth(0);
		selectionPanel.setWidth("100%");
		newExecutionPanel.add(selectionPanel);
		
		// adapters
		SelectAdapterView selectAdapterView = new SelectAdapterView();
		SelectAdapterPresenter selectAdapterPresenter = new SelectAdapterPresenter(registryService, eventBus, selectAdapterView);
		selectAdapterPresenter.go(selectionPanel);
		
		// extractors
		SelectExtractorView selectExtractorView = new SelectExtractorView();
		SelectExtractorPresenter selectExtractorPresenter = new SelectExtractorPresenter(registryService, eventBus, selectExtractorView);
		selectExtractorPresenter.go(selectionPanel);
		
		// measures
		SelectMeasureView selectMeasureView = new SelectMeasureView();
		SelectMeasurePresenter selectMeasurePresenter = new SelectMeasurePresenter(registryService, eventBus, selectMeasureView);
		selectMeasurePresenter.go(selectionPanel);
		
		// current selections
		CurrentSelectionsView currentSelectionsView = new CurrentSelectionsView();
		CurrentSelectionsPresenter currentSelectionsPresenter = new CurrentSelectionsPresenter(registryService, eventBus, currentSelectionsView); 
		currentSelectionsPresenter.go(newExecutionPanel);

		// previous executions
		previousDisclosure = new DisclosurePanel("View Previous Comparisons");
		previousDisclosure.addStyleName("mainSection");
		previousDisclosure.setAnimationEnabled(true);
		previousDisclosure.setOpen(DISCLOSURE_OPEN);
		previousDisclosureBody = new VerticalPanel();
		previousDisclosureBody.setWidth("90%");
		previousDisclosure.add(previousDisclosureBody);
		
		
		// dataset selection
		DynamicTableView datasetTableView = new DynamicTableView();
		datasetTablePresenter = new DatasetTablePresenter(dispatchAsync, eventBus, datasetTableView);
		datasetTablePresenter.bind();
		DisclosurePanel selectionDisclosure = new DisclosurePanel("Select Data");
		selectionDisclosure.addStyleName("mainSection");
		selectionDisclosure.setAnimationEnabled(true);
		selectionDisclosure.setOpen(DISCLOSURE_OPEN);
		VerticalPanel selectionDisclosureBody = new VerticalPanel();
		selectionDisclosureBody.setWidth("100%");
		selectionDisclosureBody.add(datasetTableView.asWidget());
		selectionDisclosure.add(selectionDisclosureBody);
		
		
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
		
		
		// layout
		content.add(selectionDisclosure);
		content.add(newExecutionDisclosure);
		content.add(previousDisclosure);
		contentScrollPanel = new ScrollPanel(content);
		
		appPanel.addNorth(headerPanel, 2);
//		appPanel.addSouth(footer, 2);
		appPanel.addWest(listThumbails, 10);
		appPanel.add(contentScrollPanel);
		RootLayoutPanel.get().add(appPanel);
		populate();
		datasetTablePresenter.refresh();
		
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
				JobStatusView jobStatusView = new JobStatusView();
				JobStatusPresenter jobStatusPresenter = new JobStatusPresenter(eventBus, jobStatusView, job, submission);
				jobStatusPresenter.go(previousDisclosureBody);
				previousDisclosureBody.add(jobStatusView);
				previousDisclosure.setOpen(true);
				
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
				}		appPanel.addNorth(logo, 2);
//				appPanel.addSouth(footer, 2);
				appPanel.addWest(listThumbails, 10);
				appPanel.add(contentScrollPanel);
				RootLayoutPanel.get().add(appPanel);
				populate();
				datasetTablePresenter.refresh();
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

        if (token.startsWith("dataset")) {
        	DatasetWidget datasetWidget = new DatasetWidget(dispatchAsync);
        	contentScrollPanel.clear();
        	contentScrollPanel.add(datasetWidget);
        	 String datasetUri = getParams().get("id");
             if (datasetUri != null) {
                 datasetWidget.showDataset(datasetUri);
             }
        } else {
        	contentScrollPanel.clear();
        	contentScrollPanel.add(content);
        }
	}
	
    public Map<String, String> getParams() {
        Map<String, String> params = new HashMap<String, String>();
        String paramString = History.getToken().substring(
                History.getToken().indexOf("?") + 1);
        if (!paramString.isEmpty()) {
            for (String paramEntry : paramString.split("&") ) {
                String[] terms = paramEntry.split("=");
                if (terms.length == 2) {
                    params.put(terms[0], terms[1]);
                }
            }
        }
        return params;
    }
}
