/**
 * 
 */
package edu.illinois.ncsa.versus.web.client.view;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import edu.illinois.ncsa.versus.web.client.presenter.CurrentSelectionsPresenter.Display;

/**
 * @author lmarini
 *
 */
public class CurrentSelectionsView extends Composite implements Display {
	
	private final HorizontalPanel mainPanel;
	private final SimplePanel selectedExecutorPanel;
	private final SimplePanel selectedMeasurePanel;
	private final Button executeButton;

	public CurrentSelectionsView() {
		mainPanel = new HorizontalPanel();
		mainPanel.addStyleName("currentSelectionsView");
		selectedMeasurePanel = new SimplePanel();
		selectedMeasurePanel.setWidget(new Label("No measure selected"));
		mainPanel.add(selectedMeasurePanel);		
		selectedExecutorPanel = new SimplePanel();
		selectedExecutorPanel.setWidget(new Label("No executor selected"));
		mainPanel.add(selectedExecutorPanel);
		executeButton = new Button("Execute");
		mainPanel.add(executeButton);
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

	@Override
	public HasClickHandlers getExecuteButton() {
		return executeButton;
	}
}
