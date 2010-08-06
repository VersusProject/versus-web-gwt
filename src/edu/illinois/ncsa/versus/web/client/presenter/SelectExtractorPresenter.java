/**
 * 
 */
package edu.illinois.ncsa.versus.web.client.presenter;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;

import edu.illinois.ncsa.versus.web.client.RegistryServiceAsync;
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

	public interface Display {
		int addExtractor(String extractor);
		HasClickHandlers getExtractorAnchor(int index);
		void selectExtractor(int index);
		void unselectExtractor(int index);
		Widget asWidget();
	}
	
	public SelectExtractorPresenter(RegistryServiceAsync registryService,
			HandlerManager eventBus, Display display) {
				this.registryService = registryService;
				this.eventBus = eventBus;
				this.display = display;
	}
	
	void bind() {
		eventBus.addHandler(AddExtractorEvent.TYPE, new AddExtractorEventHandler() {
			
			@Override
			public void onAddExtractor(final AddExtractorEvent addExtractorEvent) {
				final int index = display.addExtractor(addExtractorEvent.getExtractorMetadata().getName());
				display.getExtractorAnchor(index).addClickHandler(new ClickHandler() {
					
					@Override
					public void onClick(ClickEvent event) {
						ComponentMetadata adapterMetadata = addExtractorEvent.getExtractorMetadata();
						if (selectedExtractorId == adapterMetadata.getId()) {
							selectedExtractorId = null;
							eventBus.fireEvent(new ExtractorUnselectedEvent(adapterMetadata));
							display.unselectExtractor(index);
						} else {
							selectedExtractorId = adapterMetadata.getId();
							eventBus.fireEvent(new ExtractorSelectedEvent(adapterMetadata));
							display.selectExtractor(index);
						}
					}
				});
			}
		});
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
