/**
 * 
 */
package edu.illinois.ncsa.versus.web.client.view;

import java.util.HashMap;
import java.util.Map;

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;

import edu.illinois.ncsa.mmdb.web.client.dispatch.GetPreviews;
import edu.illinois.ncsa.mmdb.web.client.event.DatasetSelectedEvent;
import edu.illinois.ncsa.mmdb.web.client.event.DatasetSelectedHandler;
import edu.illinois.ncsa.mmdb.web.client.event.DatasetUnselectedEvent;
import edu.illinois.ncsa.mmdb.web.client.event.DatasetUnselectedHandler;
import edu.illinois.ncsa.mmdb.web.client.ui.PreviewWidget;

/**
 * @author lmarini
 *
 */
public class ListThumbails extends Composite {

	FlowPanel mainPanel;
	
	Map<String, PreviewWidget> previews = new HashMap<String, PreviewWidget>();

	private SimplePanel outerPanel;
	
	public ListThumbails(HandlerManager eventBus, final PickupDragController dragController) {
		outerPanel = new SimplePanel();
		outerPanel.addStyleName("listThumbnailsPanel");
		mainPanel = new FlowPanel();
		ScrollPanel scrollPanel = new ScrollPanel(mainPanel);
		outerPanel.add(scrollPanel);
		initWidget(outerPanel);
		
		eventBus.addHandler(DatasetSelectedEvent.TYPE, new DatasetSelectedHandler() {
			
			@Override
			public void onDatasetSelected(DatasetSelectedEvent event) {
				GWT.log("Adding selected dataset to list thumbnails view");
				PreviewWidget previewWidget = new PreviewWidget(event.getUri(), GetPreviews.SMALL, null);
				previewWidget.addStyleName("thumbnail");
				mainPanel.add(previewWidget);
				previews.put(event.getUri(), previewWidget);
				dragController.makeDraggable(previewWidget);
			}
		});
		
		eventBus.addHandler(DatasetUnselectedEvent.TYPE, new DatasetUnselectedHandler() {
			
			@Override
			public void onDatasetUnselected(
					DatasetUnselectedEvent datasetUnselectedEvent) {
				PreviewWidget previewWidget = previews.get(datasetUnselectedEvent.getUri());
				mainPanel.remove(previewWidget);
				previews.remove(datasetUnselectedEvent.getUri());
				
			}
		});
		
	}
	
}
