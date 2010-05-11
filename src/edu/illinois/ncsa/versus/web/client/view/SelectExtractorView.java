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
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import edu.illinois.ncsa.versus.web.client.presenter.SelectExtractorPresenter.Display;

/**
 * @author lmarini
 *
 */
public class SelectExtractorView extends Composite implements Display {

	private final FlowPanel mainPanel;
	private final VerticalPanel listExtractorPanel;
	private final List<Anchor> extractorAnchors = new ArrayList<Anchor>();
	
	public SelectExtractorView() {
		mainPanel = new FlowPanel();
		mainPanel.addStyleName("selectExtractorPanel");
		Label titleLabel = new Label("Extractors");
		titleLabel.addStyleName("titleLabel");
		mainPanel.add(titleLabel);
		listExtractorPanel = new VerticalPanel();
		mainPanel.add(listExtractorPanel);
		initWidget(mainPanel);
	}
	
	@Override
	public Widget asWidget() {
		return this;
	}

	@Override
	public int addExtractor(String extractor) {
		final Anchor extractorAnchor = new Anchor(extractor);
		extractorAnchor.addStyleName("measureAnchor");
		extractorAnchors.add(extractorAnchor);
		extractorAnchor.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				clearSelection();
				extractorAnchor.addStyleName("selectedLabel");
			}
		});
		extractorAnchor.addMouseOverHandler(new MouseOverHandler() {
			
			@Override
			public void onMouseOver(MouseOverEvent event) {
				extractorAnchor.addStyleName("highlightLabel");
			}
		});
		extractorAnchor.addMouseOutHandler(new MouseOutHandler() {
			
			@Override
			public void onMouseOut(MouseOutEvent event) {
				extractorAnchor.removeStyleName("highlightLabel");
			}
		});
		listExtractorPanel.add(extractorAnchor);
		return extractorAnchors.indexOf(extractorAnchor);
	}
	
	private void clearSelection() {
		for (Anchor anchor : extractorAnchors) {
			anchor.removeStyleName("selectedLabel");
		}
	}

	@Override
	public HasClickHandlers getExtractorAnchor(int index) {
		return extractorAnchors.get(index);
	}

}
