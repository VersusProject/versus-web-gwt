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
import edu.illinois.ncsa.versus.web.client.event.AdapterSelectedEvent;
import edu.illinois.ncsa.versus.web.client.event.AdapterSelectedHandler;
import edu.illinois.ncsa.versus.web.client.event.AdapterUnselectedEvent;
import edu.illinois.ncsa.versus.web.client.event.AdapterUnselectedHandler;
import edu.illinois.ncsa.versus.web.client.event.AddExtractorEvent;
import edu.illinois.ncsa.versus.web.client.event.AddExtractorEventHandler;
import edu.illinois.ncsa.versus.web.client.event.ExtractorSelectedEvent;
import edu.illinois.ncsa.versus.web.client.event.ExtractorUnselectedEvent;
import edu.illinois.ncsa.versus.web.shared.ComponentMetadata;

/**
 * @author lmarini
 *
 */
public class SelectExtractorPresenter implements Presenter {

	private final RegistryServiceAsync registryService;
	private final HandlerManager eventBus;
	private final Display display;
	private String selectedExtractorId;
	private Map<ComponentMetadata, Integer> extractorToIndex = new HashMap<ComponentMetadata, Integer>();
	private Map<Integer, ComponentMetadata> indexToExtractor = new HashMap<Integer, ComponentMetadata>();
	private List<Set<HandlerRegistration>> clickHandlers  = new ArrayList<Set<HandlerRegistration>>();
	private Set<Integer> hiddenByAdapter = new HashSet<Integer>();
	private Set<Integer> hiddenByMeasure = new HashSet<Integer>();

	public interface Display {
		int addExtractor(String extractor);
		HasClickHandlers getExtractorAnchor(int index);
		void selectExtractor(int index);
		void unselectExtractor(int index);
		void enableExtractors(Set<Integer> extractors);
		void disableExtractors();
		void disableExtractors(Set<Integer> extractors);
		Widget asWidget();
	}
	
	/**
	 * 
	 * @param registryService
	 * @param eventBus
	 * @param display
	 */
	public SelectExtractorPresenter(RegistryServiceAsync registryService,
			HandlerManager eventBus, Display display) {
				this.registryService = registryService;
				this.eventBus = eventBus;
				this.display = display;
	}
	
	/**
	 * 
	 */
	void bind() {
		eventBus.addHandler(AddExtractorEvent.TYPE, new AddExtractorEventHandler() {
			
			@Override
			public void onAddExtractor(final AddExtractorEvent addExtractorEvent) {
				final int index = display.addExtractor(addExtractorEvent.getExtractorMetadata().getName());
				extractorToIndex.put(addExtractorEvent.getExtractorMetadata(), index);
				indexToExtractor.put(index, addExtractorEvent.getExtractorMetadata());
				addClickHandler(index);
			}
		});
		
		eventBus.addHandler(AdapterSelectedEvent.TYPE, new AdapterSelectedHandler() {

			@Override
			public void onAdapterSelected(AdapterSelectedEvent event) {
				enableAll();
				Set<String> supportedOutputs = event.getAdapterMetadata().getSupportedOutputs();
				for (ComponentMetadata extractor : extractorToIndex.keySet()) {
					boolean found = false;
					for (String supportedOut : supportedOutputs) {
						if (extractor.getSupportedInputs().contains(supportedOut)) {
							found = true;
							break;
						}
					}
					if (!found) {
						hiddenByAdapter.add(extractorToIndex.get(extractor));
					}
				}
				GWT.log("Hide " + hiddenByAdapter);
				for (Integer index : hiddenByAdapter) {
					removeClickHandler(index);
				}
				display.disableExtractors(hiddenByAdapter);
			}
		});
		
		eventBus.addHandler(AdapterUnselectedEvent.TYPE, new AdapterUnselectedHandler() {
			
			@Override
			public void onAdapterUnselected(AdapterUnselectedEvent event) {
				hiddenByAdapter.clear();
				display.disableExtractors();
			}
		});
	}

	protected void enableAll() {
		hiddenByAdapter.clear();
		display.disableExtractors();
		clickHandlers.clear();
		for (Integer index : indexToExtractor.keySet()) {
			addClickHandler(index);
		}
	}
	
	/**
	 * 
	 * @param index
	 */
	protected void addClickHandler(final int index) {
		GWT.log("Adding handler for entry " + index);
		HandlerRegistration handlerRegistration = display.getExtractorAnchor(index).addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				GWT.log("Clicked on " + this + " / " + index);
				ComponentMetadata componentMetadata = indexToExtractor.get(index);
				if (selectedExtractorId == componentMetadata.getId()) {
					selectedExtractorId = null;
					eventBus.fireEvent(new ExtractorUnselectedEvent(componentMetadata));
					display.unselectExtractor(index);
				} else {
					selectedExtractorId = componentMetadata.getId();
					eventBus.fireEvent(new ExtractorSelectedEvent(componentMetadata));
					display.selectExtractor(index);
				}
			}
		});
		getHandlerRegistrations(index).add(handlerRegistration);
		for (HandlerRegistration registration : clickHandlers.get(index)) {
			GWT.log(registration + " registered with " + index);
		}
	}
	
	/**
	 * 
	 */
	protected Set<HandlerRegistration> getHandlerRegistrations(int index) {
		int delta = index - clickHandlers.size() + 1;
		if (delta > 0) {
			GWT.log("Growing handler registration array by " + delta);
			for (int i=0; i<delta; i++) {
				clickHandlers.add(new HashSet<HandlerRegistration>());
			}
		}
		return clickHandlers.get(index);
	}
	
	/**
	 * 
	 * @param index
	 */
	protected void removeClickHandler(int index) {
		GWT.log("Removing handler for entry " + index);
		if (clickHandlers.get(index) != null) {
			for (HandlerRegistration registration : clickHandlers.get(index)) {
				GWT.log("Unregistering " + registration + " / " + index);
				registration.removeHandler();
			}
			clickHandlers.get(index).clear();
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

}
