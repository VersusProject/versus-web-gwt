/**
 * 
 */
package edu.illinois.ncsa.versus.web.client.view;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import edu.illinois.ncsa.versus.web.client.InfoPopup;
import edu.illinois.ncsa.versus.web.client.presenter.SelectMeasurePresenter.Display;

/**
 * @author lmarini
 *
 */
public class SelectMeasureView extends Composite implements Display {

	private final FlowPanel mainPanel;
	private final VerticalPanel listMeasuresPanel;
	private final List<Anchor> measureAnchors = new ArrayList<Anchor>();
	
	public SelectMeasureView() {
		mainPanel = new FlowPanel();
		mainPanel.addStyleName("selectMeasurePanel");
		Label titleLabel = new Label("Measures");
		titleLabel.addStyleName("titleLabel");
		mainPanel.add(titleLabel);
		listMeasuresPanel = new VerticalPanel();
		listMeasuresPanel.setSpacing(10);
		mainPanel.add(listMeasuresPanel);
		initWidget(mainPanel);
	}
	
	@Override
	public Widget asWidget() {
		return this;
	}

	@Override
	public int addMeasure(String measure) {
		final Anchor measureAnchor = new Anchor(measure);
		measureAnchor.addStyleName("measureAnchor");
		measureAnchors.add(measureAnchor);
		listMeasuresPanel.add(measureAnchor);
		measureAnchor.addMouseOverHandler(new MouseOverHandler() {
			
			@Override
			public void onMouseOver(final MouseOverEvent event) {
				measureAnchor.addStyleName("highlightLabel");
			}
		});
		measureAnchor.addMouseOutHandler(new MouseOutHandler() {
			
			@Override
			public void onMouseOut(MouseOutEvent event) {
				measureAnchor.removeStyleName("highlightLabel");
			}
		});
		return measureAnchors.indexOf(measureAnchor);
	}
	
	private void clearSelection() {
		for (Anchor anchor : measureAnchors) {
			anchor.removeStyleName("selectedLabel");
		}
	}

	@Override
	public HasClickHandlers getClickAnchor(int index) {
		return measureAnchors.get(index);
	}

	@Override
	public int getNumMeasures() {
		return measureAnchors.size();
	}

	@Override
	public void selectMeasure(int index) {
		clearSelection();
		measureAnchors.get(index).addStyleName("selectedLabel");
	}

	@Override
	public void unselectMeasure(int index) {
		clearSelection();
	}
}
