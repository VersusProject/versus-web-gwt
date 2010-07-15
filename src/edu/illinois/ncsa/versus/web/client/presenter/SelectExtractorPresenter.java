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
import edu.illinois.ncsa.versus.web.client.event.ExtractorSelectedEvent;
import edu.illinois.ncsa.versus.web.client.event.AddExtractorEvent;
import edu.illinois.ncsa.versus.web.client.event.AddExtractorEventHandler;

/**
 * @author lmarini
 *
 */
public class SelectExtractorPresenter implements Presenter {

	private final RegistryServiceAsync registryService;
	private final HandlerManager eventBus;
	private final Display display;

	public interface Display {
		int addExtractor(String extractor);
		HasClickHandlers getExtractorAnchor(int index);
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
				int index = display.addExtractor(addExtractorEvent.getExtractorMetadata().getName());
				display.getExtractorAnchor(index).addClickHandler(new ClickHandler() {
					
					@Override
					public void onClick(ClickEvent event) {
						eventBus.fireEvent(new ExtractorSelectedEvent(addExtractorEvent.getExtractorMetadata()));
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
