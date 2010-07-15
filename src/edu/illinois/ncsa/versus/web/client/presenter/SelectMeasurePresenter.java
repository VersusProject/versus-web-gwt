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
import edu.illinois.ncsa.versus.web.client.event.AddMeasureEvent;
import edu.illinois.ncsa.versus.web.client.event.AddMeasureEventHandler;
import edu.illinois.ncsa.versus.web.client.event.MeasureSelectedEvent;

/**
 * @author lmarini
 *
 */
public class SelectMeasurePresenter implements Presenter {

	private final RegistryServiceAsync registryService;
	private final HandlerManager eventBus;
	private final Display display;

	public interface Display {
		int addMeasure(String measure);
		int getNumMeasures();
		HasClickHandlers getMeasureAnchor(int index);
		Widget asWidget();
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
				int index = display.addMeasure(addMeasureEvent.getMeasureMetadata().getName());
				display.getMeasureAnchor(index).addClickHandler(new ClickHandler() {
					
					@Override
					public void onClick(ClickEvent event) {
						eventBus.fireEvent(new MeasureSelectedEvent(addMeasureEvent.getMeasureMetadata()));
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
