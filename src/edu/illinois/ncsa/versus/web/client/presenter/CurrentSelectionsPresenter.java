/**
 * 
 */
package edu.illinois.ncsa.versus.web.client.presenter;

import java.util.HashSet;
import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;

import edu.illinois.ncsa.mmdb.web.client.event.DatasetSelectedEvent;
import edu.illinois.ncsa.mmdb.web.client.event.DatasetSelectedHandler;
import edu.illinois.ncsa.mmdb.web.client.event.DatasetUnselectedEvent;
import edu.illinois.ncsa.mmdb.web.client.event.DatasetUnselectedHandler;
import edu.illinois.ncsa.versus.web.client.ExecutionService;
import edu.illinois.ncsa.versus.web.client.ExecutionServiceAsync;
import edu.illinois.ncsa.versus.web.client.RegistryServiceAsync;
import edu.illinois.ncsa.versus.web.client.event.AdapterSelectedEvent;
import edu.illinois.ncsa.versus.web.client.event.AdapterSelectedHandler;
import edu.illinois.ncsa.versus.web.client.event.AdapterUnselectedEvent;
import edu.illinois.ncsa.versus.web.client.event.AdapterUnselectedHandler;
import edu.illinois.ncsa.versus.web.client.event.ExtractorSelectedEvent;
import edu.illinois.ncsa.versus.web.client.event.ExtractorSelectedHandler;
import edu.illinois.ncsa.versus.web.client.event.ExtractorUnselectedEvent;
import edu.illinois.ncsa.versus.web.client.event.ExtractorUnselectedHandler;
import edu.illinois.ncsa.versus.web.client.event.MeasureSelectedEvent;
import edu.illinois.ncsa.versus.web.client.event.MeasureSelectedHandler;
import edu.illinois.ncsa.versus.web.client.event.MeasureUnselectedEvent;
import edu.illinois.ncsa.versus.web.client.event.MeasureUnselectedHandler;
import edu.illinois.ncsa.versus.web.client.event.NewJobEvent;
import edu.illinois.ncsa.versus.web.shared.ComponentMetadata;
import edu.illinois.ncsa.versus.web.shared.Job;
import edu.illinois.ncsa.versus.web.shared.Submission;

/**
 * @author lmarini
 *
 */
public class CurrentSelectionsPresenter implements Presenter {

	private final RegistryServiceAsync registryService;
	private final HandlerManager eventBus;
	private final Display display;
	private final Set<String> datasets;
	protected ComponentMetadata adapter;
	protected ComponentMetadata measure;
	protected ComponentMetadata extractor;
	private final ExecutionServiceAsync executionService = GWT.create(ExecutionService.class);

	public interface Display {
		Widget asWidget();

		void setAdapter(String name);

		void setMeasure(String name);
		
		void setExtractor(String name);
		
		HasClickHandlers getExecuteButton();
	}
	

	public CurrentSelectionsPresenter(RegistryServiceAsync registryService,
			HandlerManager eventBus, Display currentSelectionsView) {
				this.registryService = registryService;
				this.eventBus = eventBus;
				this.display = currentSelectionsView;
				this.datasets = new HashSet<String>();
	}

	@Override
	public void go(HasWidgets container) {
		bind();
		container.add(display.asWidget());
	}

	private void bind() {
		eventBus.addHandler(AdapterSelectedEvent.TYPE, new AdapterSelectedHandler() {
			
			@Override
			public void onAdapterSelected(AdapterSelectedEvent event) {
				adapter = event.getAdapterMetadata();
				display.setAdapter(adapter.getName());
			}
		});
		
		eventBus.addHandler(AdapterUnselectedEvent.TYPE, new AdapterUnselectedHandler() {
			
			@Override
			public void onAdapterUnselected(AdapterUnselectedEvent event) {
				adapter = null;
				display.setAdapter("");
			}
		});
		
		eventBus.addHandler(MeasureSelectedEvent.TYPE, new MeasureSelectedHandler() {
			
			@Override
			public void onMeasureSelected(MeasureSelectedEvent event) {
				measure = event.getMeasureMetadata();
				display.setMeasure(measure.getName());
			}
		});
		
		eventBus.addHandler(MeasureUnselectedEvent.TYPE, new MeasureUnselectedHandler() {
			
			@Override
			public void onMeasureUnselected(MeasureUnselectedEvent event) {
				measure = null;
				display.setMeasure("");
			}
		});
		
		eventBus.addHandler(ExtractorSelectedEvent.TYPE, new ExtractorSelectedHandler() {
			
			@Override
			public void onExtractorSelected(ExtractorSelectedEvent event) {
				extractor = event.getMeasureMetadata();
				display.setExtractor(extractor.getName());
			}
		});
		
		eventBus.addHandler(ExtractorUnselectedEvent.TYPE, new ExtractorUnselectedHandler() {
			
			@Override
			public void onExtractorUnselected(ExtractorUnselectedEvent event) {
				extractor = null;
				display.setExtractor("");
			}
		});
		
		eventBus.addHandler(DatasetSelectedEvent.TYPE, new DatasetSelectedHandler() {
			
			@Override
			public void onDatasetSelected(DatasetSelectedEvent event) {
				datasets.add(event.getUri());
			}
		});
		
		eventBus.addHandler(DatasetUnselectedEvent.TYPE, new DatasetUnselectedHandler() {
			
			@Override
			public void onDatasetUnselected(
					DatasetUnselectedEvent datasetUnselectedEvent) {
				datasets.remove(datasetUnselectedEvent.getUri());
			}
		});
		
		display.getExecuteButton().addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				submitExecution();
			}
		});
	}

	/**
	 * Submit a new job to the server.
	 */
	protected void submitExecution() {
		final Submission submission = new Submission();
		submission.setDatasetsURI(datasets);
		submission.setAdapter(adapter);
		submission.setMeasure(measure);
		submission.setExtraction(extractor);
		
		// submit execution
		executionService.submit(submission, new AsyncCallback<Job>() {
			
			@Override
			public void onSuccess(Job job) {
				GWT.log("Execution successfully submitted");
				eventBus.fireEvent(new NewJobEvent(job, submission));
			}
			
			@Override
			public void onFailure(Throwable caught) {
				GWT.log("Error submitting execution", caught);
			}
		});
	}
}
