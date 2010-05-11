/**
 * 
 */
package edu.illinois.ncsa.versus.web.client.view;

import com.allen_sauer.gwt.dnd.client.drop.AbsolutePositionDropController;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

import edu.illinois.ncsa.versus.web.client.presenter.DropBoxPresenter.Display;

/**
 * @author lmarini
 *
 */
public class DropBoxView extends Composite implements Display {

	private AbsolutePanel mainPanel;
	private AbsolutePositionDropController dropController;
	
	public DropBoxView() {
		mainPanel = new AbsolutePanel();
		mainPanel.addStyleName("dropBoxPanel");
		initWidget(mainPanel);
		mainPanel.add(new Label("Drop selection here"));
		setDropController(new AbsolutePositionDropController(mainPanel));
	}

	@Override
	public Widget asWidget() {
		return this;
	}

	public void setDropController(AbsolutePositionDropController dropController) {
		this.dropController = dropController;
	}

	public AbsolutePositionDropController getDropController() {
		return dropController;
	}
	
	
	
}
