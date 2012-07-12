/**
 * 
 */
package edu.illinois.ncsa.versus.web.client.view;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import edu.illinois.ncsa.versus.web.client.presenter.ComparisonCurrentSelectionsPresenter.Display;

/**
 * @author lmarini
 *
 */
public class ComparisonCurrentSelectionsView extends Composite implements Display {
	
	private static final String NO_EXTRACTOR_SELECTED = "No extractor selected";
	private static final String NO_MEASURE_SELECTED = "No measure selected";
	private static final String NO_ADAPTER_SELECTED = "No adapter selected";
	private final VerticalPanel mainPanel;
	private final SimplePanel selectedExtractorPanel;
	private final SimplePanel selectedMeasurePanel;
	private final Button executeButton;
	private HorizontalPanel selectionsPanel;
	private SimplePanel selectedAdapterPanel;

	public ComparisonCurrentSelectionsView() {
		mainPanel = new VerticalPanel();
		mainPanel.addStyleName("currentSelectionsView");
		mainPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		selectionsPanel = new HorizontalPanel();
		selectionsPanel.setSpacing(20);
		mainPanel.add(selectionsPanel);
		selectedAdapterPanel = new SimplePanel();
		selectedAdapterPanel.setWidget(new Label(NO_ADAPTER_SELECTED));
		selectionsPanel.add(selectedAdapterPanel);
		selectionsPanel.add(new HTML("&#8594;"));
		selectedExtractorPanel = new SimplePanel();
		selectedExtractorPanel.setWidget(new Label(NO_EXTRACTOR_SELECTED));
		selectionsPanel.add(selectedExtractorPanel);
		selectionsPanel.add(new HTML("&#8594;"));
		selectedMeasurePanel = new SimplePanel();
		selectedMeasurePanel.setWidget(new Label(NO_MEASURE_SELECTED));
		selectionsPanel.add(selectedMeasurePanel);		
		executeButton = new Button("Launch");
		mainPanel.add(executeButton);
		initWidget(mainPanel);
	}

	@Override
	public Widget asWidget() {
		return this;
	}

	@Override
	public void setAdapter(String name) {
		if (name.equals("")) {
			selectedAdapterPanel.setWidget(new Label(NO_ADAPTER_SELECTED));
		} else {
			selectedAdapterPanel.setWidget(new Label(name));
		}
	}

	@Override
	public void setMeasure(String name) {
		if (name.equals("")) {
			selectedMeasurePanel.setWidget(new Label(NO_MEASURE_SELECTED));
		} else {
			selectedMeasurePanel.setWidget(new Label(name));
		}
	}

	@Override
	public void setExtractor(String name) {
		if (name.equals("")) {
			selectedExtractorPanel.setWidget(new Label(NO_EXTRACTOR_SELECTED));
		} else {
			selectedExtractorPanel.setWidget(new Label(name));
		}
	}

	@Override
	public HasClickHandlers getExecuteButton() {
		return executeButton;
	}

}
