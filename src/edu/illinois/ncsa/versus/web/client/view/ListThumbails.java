/**
 * 
 */
package edu.illinois.ncsa.versus.web.client.view;

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.allen_sauer.gwt.dnd.client.drop.AbsolutePositionDropController;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author lmarini
 *
 */
public class ListThumbails extends Composite {

	FlowPanel mainPanel;
	
	public ListThumbails(HandlerManager eventBus, PickupDragController dragController) {
		mainPanel = new FlowPanel();
		mainPanel.addStyleName("listThumbnailsPanel");
		for (int i=0; i<10; i++) {
			Image image = new Image("images/thumbnail.jpg");
			image.addStyleName("thumbnail");
			mainPanel.add(image);
			dragController.makeDraggable(image);
		}
		ScrollPanel scrollPanel = new ScrollPanel(mainPanel);
		initWidget(scrollPanel);
	}
	
}
