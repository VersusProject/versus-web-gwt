package edu.illinois.ncsa.versus.web.client.presenter;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;

import edu.illinois.ncsa.versus.web.client.RegistryServiceAsync;
import edu.illinois.ncsa.versus.web.client.event.AdapterSelectedEvent;
import edu.illinois.ncsa.versus.web.client.event.AddAdapterEvent;
import edu.illinois.ncsa.versus.web.client.event.AddAdapterEventHandler;

/**
 * 
 * @author lmarini
 *
 */
public class SelectAdapterPresenter implements Presenter {

	private final RegistryServiceAsync registryService;
	private final HandlerManager eventBus;
	private final Display display;

	public interface Display {
		int addAdapter(String adapter);
		int getNumAdapters();
		HasClickHandlers getAdapterAnchor(int index);
		Widget asWidget();
	}
	
	public SelectAdapterPresenter(RegistryServiceAsync registryService,
			HandlerManager eventBus, Display display) {
				this.registryService = registryService;
				this.eventBus = eventBus;
				this.display = display;
	}
	
	@Override
	public void go(HasWidgets container) {
		bind();
		container.add(display.asWidget());
	}

	private void bind() {
		eventBus.addHandler(AddAdapterEvent.TYPE, new AddAdapterEventHandler() {
			
			@Override
			public void onAddAdapter(final AddAdapterEvent addAdapterEvent) {
				int index = display.addAdapter(addAdapterEvent.getAdapterMetadata().getName());
				display.getAdapterAnchor(index).addClickHandler(new ClickHandler() {
					
					@Override
					public void onClick(ClickEvent event) {
						eventBus.fireEvent(new AdapterSelectedEvent(addAdapterEvent.getAdapterMetadata()));
					}
				});
			}
		});
	}

}
