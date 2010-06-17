package edu.illinois.ncsa.versus.web.client;

import java.util.List;

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.illinois.ncsa.mmdb.web.client.UploadWidget;
import edu.illinois.ncsa.mmdb.web.client.dispatch.MyDispatchAsync;
import edu.illinois.ncsa.mmdb.web.client.event.DatasetUploadedEvent;
import edu.illinois.ncsa.mmdb.web.client.event.DatasetUploadedHandler;
import edu.illinois.ncsa.mmdb.web.client.presenter.DatasetTablePresenter;
import edu.illinois.ncsa.mmdb.web.client.view.DynamicTableView;
import edu.illinois.ncsa.versus.web.client.event.NewJobEvent;
import edu.illinois.ncsa.versus.web.client.event.NewJobHandler;
import edu.illinois.ncsa.versus.web.client.event.SelectExtractorEvent;
import edu.illinois.ncsa.versus.web.client.event.SelectMeasureEvent;
import edu.illinois.ncsa.versus.web.client.presenter.CurrentSelectionsPresenter;
import edu.illinois.ncsa.versus.web.client.presenter.JobStatusPresenter;
import edu.illinois.ncsa.versus.web.client.presenter.SelectExtractorPresenter;
import edu.illinois.ncsa.versus.web.client.presenter.SelectMeasurePresenter;
import edu.illinois.ncsa.versus.web.client.view.CurrentSelectionsView;
import edu.illinois.ncsa.versus.web.client.view.JobStatusView;
import edu.illinois.ncsa.versus.web.client.view.ListThumbails;
import edu.illinois.ncsa.versus.web.client.view.SelectExtractorView;
import edu.illinois.ncsa.versus.web.client.view.SelectMeasureView;
import edu.illinois.ncsa.versus.web.shared.Job;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Versus_web implements EntryPoint {
	private final RegistryServiceAsync registryService = GWT.create(RegistryService.class);

	private FlowPanel content;

	private HandlerManager eventBus;

	private UploadWidget uploadWidget;

	private VerticalPanel previousDisclosureBody;
	
	public static final MyDispatchAsync dispatchAsync        = new MyDispatchAsync();

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		eventBus = new HandlerManager(null);
		// drag and drop
		PickupDragController dragController = new PickupDragController(RootPanel.get(), true);
		DockLayoutPanel appPanel = new DockLayoutPanel(Unit.EM);
		HTML header = new HTML("Versus");
		header.addStyleName("headerPanel");
		HTML footer = new HTML("");
		footer.addStyleName("footer");
		HTML navigation = new HTML("files");
		navigation.addStyleName("navigation");
		ListThumbails listThumbails = new ListThumbails(eventBus, dragController);
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
//		dragController.registerDropController(secondDropBox.getDropController());
//		
		
		DisclosurePanel newExecutionDisclosure = new DisclosurePanel("New Execution");
		newExecutionDisclosure.addStyleName("mainSection");
		newExecutionDisclosure.setAnimationEnabled(true);
		newExecutionDisclosure.setOpen(true);
		VerticalPanel newExecutionPanel = new VerticalPanel();
		newExecutionPanel.setWidth("100%");
		newExecutionDisclosure.add(newExecutionPanel);
		content.add(newExecutionDisclosure);
		
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
		DisclosurePanel previousDisclosure = new DisclosurePanel("Previous Executions");
		previousDisclosure.addStyleName("mainSection");
		previousDisclosure.setAnimationEnabled(true);
		previousDisclosure.setOpen(true);
		previousDisclosureBody = new VerticalPanel();
		previousDisclosureBody.setWidth("90%");
		previousDisclosure.add(previousDisclosureBody);
		content.add(previousDisclosure);
		
		// dataset selection
		DynamicTableView datasetTableView = new DynamicTableView();
		final DatasetTablePresenter datasetTablePresenter = new DatasetTablePresenter(dispatchAsync, eventBus, datasetTableView);
		datasetTablePresenter.bind();
		DisclosurePanel selectionDisclosure = new DisclosurePanel("Select Datasets");
		selectionDisclosure.addStyleName("mainSection");
		selectionDisclosure.setAnimationEnabled(true);
		selectionDisclosure.setOpen(true);
		VerticalPanel selectionDisclosureBody = new VerticalPanel();
		selectionDisclosureBody.setWidth("100%");
		selectionDisclosureBody.add(datasetTableView.asWidget());
		selectionDisclosure.add(selectionDisclosureBody);
		content.add(selectionDisclosure);
		
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
		
		appPanel.addNorth(header, 2);
//		appPanel.addSouth(footer, 2);
		appPanel.addWest(listThumbails, 10);
		
		ScrollPanel contentScrollPanel = new ScrollPanel(content);
		
		appPanel.add(contentScrollPanel);
		RootLayoutPanel.get().add(appPanel);
		populate();
		datasetTablePresenter.refresh();
		
		bind();
	}

	private void bind() {
		eventBus.addHandler(NewJobEvent.TYPE, new NewJobHandler() {
			
			@Override
			public void onNewJob(NewJobEvent newJobEvent) {
				Job job = newJobEvent.getJob();
				JobStatusView jobStatusView = new JobStatusView();
				JobStatusPresenter jobStatusPresenter = new JobStatusPresenter(eventBus, jobStatusView, job);
				jobStatusPresenter.go(previousDisclosureBody);
				previousDisclosureBody.add(jobStatusView);
				
			}
		});
	}

	private void populate() {
		registryService.getExtractors(new AsyncCallback<List<String>>() {
			
			@Override
			public void onSuccess(List<String> result) {
				for (String name : result) {
					eventBus.fireEvent(new SelectExtractorEvent(name));
				}
			}
			
			@Override
			public void onFailure(Throwable caught) {
				GWT.log("Error retrieving extractors", caught);
			}
		});
		
		registryService.getMeasures(new AsyncCallback<List<String>>() {
			
			@Override
			public void onSuccess(List<String> result) {
				for (String name : result) {
					eventBus.fireEvent(new SelectMeasureEvent(name));
				}
			}
			
			@Override
			public void onFailure(Throwable caught) {
				GWT.log("Error retrieving measures", caught);
			}
		});
	}
}
