/**
 * 
 */
package edu.illinois.ncsa.versus.web.client.view;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import edu.illinois.ncsa.versus.web.client.presenter.CurrentSelectionsPresenter.Display;

/**
 * @author lmarini
 *
 */
public class CurrentSelectionsView extends Composite implements Display {
	
	private FlowPanel mainPanel;
	private SimplePanel selectedExecutorPanel;
	private SimplePanel selectedMeasurePanel;

	public CurrentSelectionsView() {
		mainPanel = new FlowPanel();
		mainPanel.addStyleName("currentSelectionsView");
		selectedExecutorPanel = new SimplePanel();
		selectedExecutorPanel.setWidget(new Label("No executor selected"));
		mainPanel.add(selectedExecutorPanel);
		selectedMeasurePanel = new SimplePanel();
		selectedMeasurePanel.setWidget(new Label("No measure selected"));
		mainPanel.add(selectedMeasurePanel);
		initWidget(mainPanel);
	}

	@Override
	public Widget asWidget() {
		return this;
	}

	@Override
	public void setMeasure(String name) {
		selectedExecutorPanel.setWidget(new Label(name));
	}

	@Override
	public void setExtractor(String name) {
		selectedMeasurePanel.setWidget(new Label(name));
	}

}
