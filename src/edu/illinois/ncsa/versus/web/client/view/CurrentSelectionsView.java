/**
 * 
 */
package edu.illinois.ncsa.versus.web.client.view;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import edu.illinois.ncsa.versus.web.client.presenter.CurrentSelectionsPresenter.Display;

/**
 * @author lmarini
 *
 */
public class CurrentSelectionsView extends Composite implements Display {
	
	private final VerticalPanel mainPanel;
	private final SimplePanel selectedExecutorPanel;
	private final SimplePanel selectedMeasurePanel;
	private final Button executeButton;
	private HorizontalPanel selectionsPanel;

	public CurrentSelectionsView() {
		mainPanel = new VerticalPanel();
		mainPanel.addStyleName("currentSelectionsView");
		mainPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		selectionsPanel = new HorizontalPanel();
		selectionsPanel.setSpacing(20);
		mainPanel.add(selectionsPanel);
		selectedMeasurePanel = new SimplePanel();
		selectedMeasurePanel.setWidget(new Label("No measure selected"));
		selectionsPanel.add(selectedMeasurePanel);		
		selectedExecutorPanel = new SimplePanel();
		selectedExecutorPanel.setWidget(new Label("No executor selected"));
		selectionsPanel.add(selectedExecutorPanel);
		executeButton = new Button("Launch");
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
