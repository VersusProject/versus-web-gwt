/**
 * 
 */
package edu.illinois.ncsa.versus.web.client.presenter;

import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;

import edu.illinois.ncsa.versus.web.client.RegistryServiceAsync;

/**
 * @author lmarini
 *
 */
public class DropBoxPresenter implements Presenter {

	private final RegistryServiceAsync registryService;
	private final SimpleEventBus eventBus;
	private final Display display;

	public interface Display {

		Widget asWidget();
		
	}
	
	public DropBoxPresenter(RegistryServiceAsync registryService,
			SimpleEventBus eventBus, Display display) {
				this.registryService = registryService;
				this.eventBus = eventBus;
				this.display = display;
	}
	
	@Override
	public void go(HasWidgets container) {
		container.add(display.asWidget());
	}

}
