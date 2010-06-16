/**
 * 
 */
package edu.illinois.ncsa.versus.web.client.presenter;

import java.util.HashSet;
import java.util.Set;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;

import edu.illinois.ncsa.mmdb.web.client.event.DatasetSelectedEvent;
import edu.illinois.ncsa.mmdb.web.client.event.DatasetSelectedHandler;
import edu.illinois.ncsa.mmdb.web.client.event.DatasetUnselectedEvent;
import edu.illinois.ncsa.mmdb.web.client.event.DatasetUnselectedHandler;
import edu.illinois.ncsa.versus.web.client.RegistryServiceAsync;
import edu.illinois.ncsa.versus.web.client.event.ExtractorSelectedEvent;
import edu.illinois.ncsa.versus.web.client.event.ExtractorSelectedHandler;
import edu.illinois.ncsa.versus.web.client.event.MeasureSelectedEvent;
import edu.illinois.ncsa.versus.web.client.event.MeasureSelectedHandler;
import edu.illinois.ncsa.versus.web.shared.PairwiseComparison;
import edu.illinois.ncsa.versus.web.shared.SetComparison;

/**
 * @author lmarini
 *
 */
public class CurrentSelectionsPresenter implements Presenter {

	private final RegistryServiceAsync registryService;
	private final HandlerManager eventBus;
	private final Display display;
	private final Set<String> datasets;
	protected String measure;
	protected String extractor;
	
	public interface Display {
		Widget asWidget();

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
		eventBus.addHandler(MeasureSelectedEvent.TYPE, new MeasureSelectedHandler() {
			
			@Override
			public void onMeasureSelected(MeasureSelectedEvent event) {
				measure = event.getName();
				display.setMeasure(event.getName());
			}
		});
		
		eventBus.addHandler(ExtractorSelectedEvent.TYPE, new ExtractorSelectedHandler() {
			
			@Override
			public void onExtractorSelected(ExtractorSelectedEvent event) {
				extractor = event.getName();
				display.setExtractor(event.getName());
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

	protected void submitExecution() {
		SetComparison comparisons = new SetComparison();
		for (String datasetOne : datasets) {
			for (String datasetTwo : datasets) {
				if (!datasetOne.equals(datasetTwo)) {
					PairwiseComparison pairwiseComparison = new PairwiseComparison();
					comparisons.getComparisons().add(pairwiseComparison);
				}
			}
		}
	}
}
