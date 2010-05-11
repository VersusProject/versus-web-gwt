/**
 * 
 */
package edu.illinois.ncsa.versus.web.client.presenter;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;

import edu.illinois.ncsa.versus.web.client.RegistryServiceAsync;
import edu.illinois.ncsa.versus.web.client.event.ExtractorSelectedEvent;
import edu.illinois.ncsa.versus.web.client.event.ExtractorSelectedHandler;
import edu.illinois.ncsa.versus.web.client.event.MeasureSelectedEvent;
import edu.illinois.ncsa.versus.web.client.event.MeasureSelectedHandler;

/**
 * @author lmarini
 *
 */
public class CurrentSelectionsPresenter implements Presenter {

	private final RegistryServiceAsync registryService;
	private final HandlerManager eventBus;
	private final Display display;
	
	public interface Display {
		Widget asWidget();

		void setMeasure(String name);
		
		void setExtractor(String name);
	}
	

	public CurrentSelectionsPresenter(RegistryServiceAsync registryService,
			HandlerManager eventBus, Display currentSelectionsView) {
				this.registryService = registryService;
				this.eventBus = eventBus;
				this.display = currentSelectionsView;
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
				display.setMeasure(event.getName());
			}
		});
		eventBus.addHandler(ExtractorSelectedEvent.TYPE, new ExtractorSelectedHandler() {
			
			@Override
			public void onExtractorSelected(ExtractorSelectedEvent event) {
				display.setExtractor(event.getName());
			}
		});
	}

}
