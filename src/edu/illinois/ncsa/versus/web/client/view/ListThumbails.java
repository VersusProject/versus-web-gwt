/**
 * 
 */
package edu.illinois.ncsa.versus.web.client.view;

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.allen_sauer.gwt.dnd.client.drop.AbsolutePositionDropController;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

import edu.illinois.ncsa.mmdb.web.client.dispatch.GetPreviews;
import edu.illinois.ncsa.mmdb.web.client.event.DatasetSelectedEvent;
import edu.illinois.ncsa.mmdb.web.client.event.DatasetSelectedHandler;
import edu.illinois.ncsa.mmdb.web.client.ui.PreviewWidget;

/**
 * @author lmarini
 *
 */
public class ListThumbails extends Composite {

	FlowPanel mainPanel;
	
	public ListThumbails(HandlerManager eventBus, final PickupDragController dragController) {
		mainPanel = new FlowPanel();
		mainPanel.addStyleName("listThumbnailsPanel");
		for (int i=0; i<1; i++) {
			Image image = new Image("images/thumbnail.jpg");
			image.addStyleName("thumbnail");
			mainPanel.add(image);
			dragController.makeDraggable(image);
		}
		ScrollPanel scrollPanel = new ScrollPanel(mainPanel);
		initWidget(scrollPanel);
		
		eventBus.addHandler(DatasetSelectedEvent.TYPE, new DatasetSelectedHandler() {
			
			@Override
			public void onDatasetSelected(DatasetSelectedEvent event) {
				GWT.log("Adding selected dataset to list thumbnails view");
				PreviewWidget previewWidget = new PreviewWidget(event.getUri(), GetPreviews.SMALL, null);
				mainPanel.add(previewWidget);
//				dragController.makeDraggable(previewWidget);
			}
		});
		
	}
	
}
