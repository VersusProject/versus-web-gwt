/**
 * 
 */
package edu.illinois.ncsa.versus.web.client.presenter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;

import edu.illinois.ncsa.versus.web.client.RegistryServiceAsync;
import edu.illinois.ncsa.versus.web.client.event.AddMeasureEvent;
import edu.illinois.ncsa.versus.web.client.event.AddMeasureEventHandler;
import edu.illinois.ncsa.versus.web.client.event.ExtractorSelectedEvent;
import edu.illinois.ncsa.versus.web.client.event.ExtractorSelectedHandler;
import edu.illinois.ncsa.versus.web.client.event.ExtractorUnselectedEvent;
import edu.illinois.ncsa.versus.web.client.event.ExtractorUnselectedHandler;
import edu.illinois.ncsa.versus.web.client.event.MeasureSelectedEvent;
import edu.illinois.ncsa.versus.web.client.event.MeasureUnselectedEvent;
import edu.illinois.ncsa.versus.web.shared.ComponentMetadata;

/**
 * @author lmarini
 *
 */
public class SelectMeasurePresenter implements Presenter {

	private final RegistryServiceAsync registryService;
	private final HandlerManager eventBus;
	private final Display display;
	private String selectedMeasureId;
	
	private Map<ComponentMetadata, Integer> measureToIndex = new HashMap<ComponentMetadata, Integer>();
	private Map<Integer, ComponentMetadata> indexToMeasure = new HashMap<Integer, ComponentMetadata>();
	private List<HandlerRegistration> clickHandlers  = new ArrayList<HandlerRegistration>();

	private Set<Integer> hiddenByExtractor = new HashSet<Integer>();
	
	public interface Display {
		int addMeasure(String measure, String category);
		int getNumMeasures();
		void enableMeasures();
		void selectMeasure(int index);
		void unselectMeasure(int index);
		void disableMeasures(Set<Integer> measures);
		Widget asWidget();
		HasClickHandlers getMeasureAnchor(int index);
	}
	
	public SelectMeasurePresenter(RegistryServiceAsync registryService,
			HandlerManager eventBus, Display display) {
				this.registryService = registryService;
				this.eventBus = eventBus;
				this.display = display;
	}
	
	void bind() {
		eventBus.addHandler(AddMeasureEvent.TYPE, new AddMeasureEventHandler() {
			
			@Override
			public void onAddMeasure(final AddMeasureEvent addMeasureEvent) {			
				ComponentMetadata measureMetada = addMeasureEvent.getMeasureMetadata();
				final int index = display.addMeasure(measureMetada.getName(), measureMetada.getCategory());
				measureToIndex.put(measureMetada, index);
				indexToMeasure.put(index, measureMetada);
				HandlerRegistration handlerRegistration = display.getMeasureAnchor(index).addClickHandler(new SelectionHandler(index));
				clickHandlers.add(handlerRegistration);
			}
		});
		
		eventBus.addHandler(ExtractorSelectedEvent.TYPE, new ExtractorSelectedHandler() {

			@Override
			public void onExtractorSelected(ExtractorSelectedEvent event) {
				resetView();
				Set<String> supportedOutputs = event.getMeasureMetadata().getSupportedOutputs();
				for (ComponentMetadata extractor : measureToIndex.keySet()) {
					boolean found = false;
					for (String supportedOut : supportedOutputs) {
						if (extractor.getSupportedInputs().contains(supportedOut)) {
							found = true;
							break;
						}
					}
					if (!found) {
						hiddenByExtractor.add(measureToIndex.get(extractor));
					}
				}
				GWT.log("Hide " + hiddenByExtractor);
				for (Integer index : hiddenByExtractor) {
					removeClickHandler(index);
				}
				display.disableMeasures(hiddenByExtractor);
			}
		});
		
		eventBus.addHandler(ExtractorUnselectedEvent.TYPE, new ExtractorUnselectedHandler() {
			
			@Override
			public void onExtractorUnselected(ExtractorUnselectedEvent event) {
				resetView();
			}
		});
	}
	
	protected void resetView() {
		GWT.log("Refreshing all handlers");
		if (selectedMeasureId != null) {
			for (ComponentMetadata componentMetadata : measureToIndex.keySet()) {
				if (componentMetadata.getId().equals(selectedMeasureId)) {
					eventBus.fireEvent(new MeasureUnselectedEvent(componentMetadata));
				}
			}
		}
		hiddenByExtractor.clear();
		display.enableMeasures();
		for (Integer index : indexToMeasure.keySet()) {
			addClickHandler(index);
		}
	}
	
	
	/**
	 * 
	 * @param index
	 */
	protected void addClickHandler(final int index) {
		GWT.log("Adding handler for entry " + index);
		HandlerRegistration oldRegistration = clickHandlers.get(index);
		if (oldRegistration != null) {
			oldRegistration.removeHandler();
			clickHandlers.set(index, null);
		}
		HandlerRegistration handlerRegistration = 
			display.getMeasureAnchor(index).addClickHandler(new SelectionHandler(index));
		clickHandlers.set(index, handlerRegistration);
	}
	
	/**
	 * 
	 * @param index
	 */
	protected void removeClickHandler(int index) {
		HandlerRegistration handlerRegistration = clickHandlers.get(index);
		if (handlerRegistration != null) {
			handlerRegistration.removeHandler();
			clickHandlers.set(index, null);
			GWT.log("SelectMeasurePresenter: Removed click handler for entry " + index);
		}
	}
	
	

	/* (non-Javadoc)
	 * @see edu.illinois.ncsa.versus.web.client.presenter.Presenter#go(com.google.gwt.user.client.ui.HasWidgets)
	 */
	@Override
	public void go(HasWidgets container) {
		bind();
		container.add(display.asWidget());
	}
	
	class SelectionHandler implements ClickHandler {

		private final int index;
		
		public SelectionHandler(int index) {
			super();
			this.index = index;
			
		}
		
		@Override
		public void onClick(ClickEvent event) {
			GWT.log("Clicked on " + this + " / " + index);
			ComponentMetadata componentMetadata = indexToMeasure.get(index);
			if (selectedMeasureId == componentMetadata.getId()) {
				selectedMeasureId = null;
				eventBus.fireEvent(new MeasureUnselectedEvent(componentMetadata));
				display.unselectMeasure(index);
			} else {
				selectedMeasureId = componentMetadata.getId();
				eventBus.fireEvent(new MeasureSelectedEvent(componentMetadata));
				display.selectMeasure(index);
			}	
		}
	}
	

}
