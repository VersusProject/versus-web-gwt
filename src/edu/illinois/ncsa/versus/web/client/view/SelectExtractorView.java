/**
 * 
 */
package edu.illinois.ncsa.versus.web.client.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import edu.illinois.ncsa.versus.web.client.InfoPopup;
import edu.illinois.ncsa.versus.web.client.presenter.SelectExtractorPresenter.Display;

/**
 * @author lmarini
 *
 */
public class SelectExtractorView extends Composite implements Display {

	private final FlowPanel mainPanel;
	private final VerticalPanel listExtractorPanel;
	private final List<Anchor> extractorAnchors = new ArrayList<Anchor>();
	private List<HandlerRegistration> clickHandlers  = new ArrayList<HandlerRegistration>();
	private List<HandlerRegistration> mouseOverHandlers  = new ArrayList<HandlerRegistration>();
	private List<HandlerRegistration> mouseOutHandlers  = new ArrayList<HandlerRegistration>();
	
	public SelectExtractorView() {
		mainPanel = new FlowPanel();
		mainPanel.addStyleName("selectExtractorPanel");
		Label titleLabel = new Label("Extractors");
		titleLabel.addStyleName("titleLabel");
		mainPanel.add(titleLabel);
		listExtractorPanel = new VerticalPanel();
		listExtractorPanel.setSpacing(10);
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
		extractorAnchors.add(extractorAnchor);
		listExtractorPanel.add(extractorAnchor);
		int index = extractorAnchors.indexOf(extractorAnchor);
		addStyleAndHandlers(index);
		return index;
	}
	
	private void addStyleAndHandlers(int index) {
		GWT.log("SelectedExtractorView: Enabling extractor " + index);
		final Anchor extractorAnchor = extractorAnchors.get(index);
		extractorAnchor.addStyleName("measureAnchor");
		final InfoPopup popup = new InfoPopup(extractorAnchor.getText());
		HandlerRegistration addClickHandler = extractorAnchor.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				clearSelection();
				extractorAnchor.addStyleName("selectedLabel");
			}
		});
		if (index > clickHandlers.size() -1) {
			clickHandlers.add(addClickHandler);
		} else {
			if (clickHandlers.get(index) != null) {
				clickHandlers.get(index).removeHandler();
			}
			clickHandlers.set(index, addClickHandler);
		}
		HandlerRegistration addMouseOverHandler = extractorAnchor.addMouseOverHandler(new MouseOverHandler() {
			
			@Override
			public void onMouseOver(final MouseOverEvent event) {
				GWT.log("ON MOUSE OVER");
				extractorAnchor.addStyleName("highlightLabel");
				popup.setPopupPositionAndShow(new PopupPanel.PositionCallback() {
			          public void setPosition(int offsetWidth, int offsetHeight) {
			            popup.setPopupPosition(event.getClientX() + 50, event.getClientY());
			          }
			        });
			}
		});
		if (index > mouseOverHandlers.size() -1) {
			mouseOverHandlers.add(addMouseOverHandler);
		} else {
			if (mouseOutHandlers.get(index) != null) {
				mouseOverHandlers.get(index).removeHandler();
			}
			mouseOverHandlers.set(index, addMouseOverHandler);
		}
		HandlerRegistration addMouseOutHandler = extractorAnchor.addMouseOutHandler(new MouseOutHandler() {
			
			@Override
			public void onMouseOut(MouseOutEvent event) {
				GWT.log("ON MOUSE OUT");
				extractorAnchor.removeStyleName("highlightLabel");
				popup.hide();
			}
		});
		if (index > mouseOutHandlers.size() -1) {
			mouseOutHandlers.add(addMouseOutHandler);
		} else {
			if (mouseOutHandlers.get(index) != null) {
				mouseOutHandlers.get(index).removeHandler();
			}
			mouseOutHandlers.set(index, addMouseOutHandler);
		}
	}
	
	private void removeStyleAndHandlers(int index) {
		if (clickHandlers.get(index) != null) {
			clickHandlers.get(index).removeHandler();
			clickHandlers.set(index, null);
			GWT.log("SelectExtractorView: Removed click handler from " + index);
		}
		if (mouseOverHandlers.get(index) != null) {
			mouseOverHandlers.get(index).removeHandler();
			mouseOverHandlers.set(index, null);
			GWT.log("SelectExtractorView: Removed mouse over from " + index);
		}
		if (mouseOutHandlers.get(index) != null) {
			mouseOutHandlers.get(index).removeHandler();
			mouseOutHandlers.set(index, null);
			GWT.log("SelectExtractorView: Removed mouse out from " + index);
		}
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

	@Override
	public void selectExtractor(int index) {
		clearSelection();
		extractorAnchors.get(index).addStyleName("selectedLabel");
	}

	@Override
	public void unselectExtractor(int index) {
		clearSelection();
	}
	
	@Override
	public void enableExtractors() {
		for (int i=0; i<extractorAnchors.size(); i++) {
			addStyleAndHandlers(i);
		}
		for (Anchor anchor : extractorAnchors) {
			anchor.removeStyleName("hideExtractor");
		}
	}

	@Override
	public void disableExtractors(Set<Integer> extractors) {
		for (Integer index : extractors) {
			extractorAnchors.get(index).addStyleName("hideExtractor");
			removeStyleAndHandlers(index);
		}
	}

}
