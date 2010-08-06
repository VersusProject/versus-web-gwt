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

	public interface Display {
		int addMeasure(String measure);
		int getNumMeasures();
		HasClickHandlers getMeasureAnchor(int index);
		void selectMeasure(int index);
		void unselectMeasure(int index);
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
				final int index = display.addMeasure(addMeasureEvent.getMeasureMetadata().getName());
				display.getMeasureAnchor(index).addClickHandler(new ClickHandler() {
					
					@Override
					public void onClick(ClickEvent event) {
						ComponentMetadata adapterMetadata = addMeasureEvent.getMeasureMetadata();
						if (selectedMeasureId == adapterMetadata.getId()) {
							selectedMeasureId = null;
							eventBus.fireEvent(new MeasureUnselectedEvent(adapterMetadata));
							display.unselectMeasure(index);
						} else {
							selectedMeasureId = adapterMetadata.getId();
							eventBus.fireEvent(new MeasureSelectedEvent(adapterMetadata));
							display.selectMeasure(index);
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
