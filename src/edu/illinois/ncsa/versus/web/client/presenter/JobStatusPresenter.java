/**
 * 
 */
package edu.illinois.ncsa.versus.web.client.presenter;

import java.util.Date;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;

import edu.illinois.ncsa.versus.web.client.ExecutionService;
import edu.illinois.ncsa.versus.web.client.ExecutionServiceAsync;
import edu.illinois.ncsa.versus.web.shared.Job;
import edu.illinois.ncsa.versus.web.shared.PairwiseComparison;

/**
 * @author lmarini
 *
 */
public class JobStatusPresenter implements Presenter {

	private final HandlerManager eventBus;
	private final Display display;
	private final Job job;
	private final ExecutionServiceAsync executionService = GWT.create(ExecutionService.class);

	public interface Display {
		Widget asWidget();
		
		void setStatus(String status);
		
		void setStart(Date date);
		
		void setComparisons(List<PairwiseComparison> datasets);
		
		void setMeasure(String measure);
		
		void setExtractor(String extractor);
	}
	
	public JobStatusPresenter(HandlerManager eventBus, Display display, Job job) {
		this.eventBus = eventBus;
		this.display = display;
		this.job = job;
		display.setStart(job.getStarted());
		display.setComparisons(job.getComparison().getComparisons());
		display.setStatus("Started");
		display.setExtractor(job.getComparison().getExtractionID());
		display.setMeasure(job.getComparison().getMeasureID());
		pollStatus();
	}
	
	private void pollStatus() {
		
		Timer timer = new Timer() {
			
			@Override
			public void run() {
				executionService.getStatus(job.getUri(), new AsyncCallback<String>() {
					
					@Override
					public void onSuccess(String result) {
						display.setStatus(result);
						if (result.equals("Done")) {
							cancel();
						}
					}
					
					@Override
					public void onFailure(Throwable caught) {
						GWT.log("Error getting status of job ", caught);
						cancel();
					}
				});
			}
		};
		timer.scheduleRepeating(5000);

	}

	@Override
	public void go(HasWidgets container) {
		bind();
		container.add(display.asWidget());
	}

	private void bind() {
		// TODO Auto-generated method stub
		
	}

}
