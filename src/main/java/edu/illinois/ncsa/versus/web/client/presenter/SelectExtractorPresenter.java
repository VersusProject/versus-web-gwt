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
	private List<HandlerRegistration> clickHandlers = new ArrayList<HandlerRegistration>();
	private Set<Integer> hiddenByAdapter = new HashSet<Integer>();
	private Set<Integer> hiddenByMeasure = new HashSet<Integer>();

	public interface Display {
		int addExtractor(String extractor);

		HasClickHandlers getExtractorAnchor(int index);

		void selectExtractor(int index);

		void unselectExtractor(int index);

		void enableExtractors();

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
	@SuppressWarnings("deprecation")
	void bind() {
		eventBus.addHandler(AddExtractorEvent.TYPE,
				new AddExtractorEventHandler() {

					@Override
					public void onAddExtractor(
							final AddExtractorEvent addExtractorEvent) {
						final int index = display
								.addExtractor(addExtractorEvent
										.getExtractorMetadata().getName());
						extractorToIndex.put(
								addExtractorEvent.getExtractorMetadata(), index);
						indexToExtractor.put(index,
								addExtractorEvent.getExtractorMetadata());
						HandlerRegistration handlerRegistration = display
								.getExtractorAnchor(index).addClickHandler(
										new SelectionHandler(index));
						clickHandlers.add(handlerRegistration);
					}
				});

		eventBus.addHandler(AdapterSelectedEvent.TYPE,
				new AdapterSelectedHandler() {

					@Override
					public void onAdapterSelected(AdapterSelectedEvent event) {
						resetView();
						
						String selectedAdapter = event.getAdapterMetadata().getId();
						for (ComponentMetadata extractor : extractorToIndex.keySet()) {
							if(!extractor.getSupportedInputs().contains(selectedAdapter)) {
								hiddenByAdapter.add(extractorToIndex
										.get(extractor));
							}
						}
						
						GWT.log("Hide " + hiddenByAdapter);
						for (Integer index : hiddenByAdapter) {
							removeClickHandler(index);
						}
						display.disableExtractors(hiddenByAdapter);
					}
				});

		eventBus.addHandler(AdapterUnselectedEvent.TYPE,
				new AdapterUnselectedHandler() {

					@Override
					public void onAdapterUnselected(AdapterUnselectedEvent event) {
						resetView();
					}
				});
	}

	/**
	 * 
	 */
	protected void resetView() {
		GWT.log("Refreshing all handlers");
		if (selectedExtractorId != null) {
			for (ComponentMetadata componentMetadata : extractorToIndex
					.keySet()) {
				if (componentMetadata.getId().equals(selectedExtractorId)) {
					eventBus.fireEvent(new ExtractorUnselectedEvent(
							componentMetadata));
				}
			}
		}
		hiddenByAdapter.clear();
		display.enableExtractors();
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
		HandlerRegistration oldRegistration = clickHandlers.get(index);
		if (oldRegistration != null) {
			oldRegistration.removeHandler();
			clickHandlers.set(index, null);
		}
		HandlerRegistration handlerRegistration = display.getExtractorAnchor(
				index).addClickHandler(new SelectionHandler(index));
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
			GWT.log("SelectExtractorPresenter: Removed click handler for entry "
					+ index);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.illinois.ncsa.versus.web.client.presenter.Presenter#go(com.google
	 * .gwt.user.client.ui.HasWidgets)
	 */
	@Override
	public void go(HasWidgets container) {
		bind();
		hiddenByAdapter.clear();
		container.add(display.asWidget());
	}

	class SelectionHandler implements ClickHandler {

		private final int index;

		public SelectionHandler(int index) {
			super();
			this.index = index;

		}

		@SuppressWarnings("deprecation")
		@Override
		public void onClick(ClickEvent event) {
			GWT.log("Clicked on " + this + " / " + index);
			ComponentMetadata componentMetadata = indexToExtractor.get(index);
			if (selectedExtractorId == componentMetadata.getId()) {
				selectedExtractorId = null;
				eventBus.fireEvent(new ExtractorUnselectedEvent(
						componentMetadata));
				display.unselectExtractor(index);
			} else {
				selectedExtractorId = componentMetadata.getId();
				eventBus.fireEvent(new ExtractorSelectedEvent(componentMetadata));
				display.selectExtractor(index);
			}
		}
	}
}
