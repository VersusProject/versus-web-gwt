/**
 * 
 */
package edu.illinois.ncsa.versus.web.client.view;

import java.util.ArrayList;
import java.util.HashMap;
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
import com.google.gwt.user.client.ui.DisclosurePanel;
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
	private final VerticalPanel listCategoriesPanel;
	private final List<Anchor> measureAnchors = new ArrayList<Anchor>();
	
	private List<HandlerRegistration> clickHandlers  = new ArrayList<HandlerRegistration>();
	private List<HandlerRegistration> mouseOverHandlers  = new ArrayList<HandlerRegistration>();
	private List<HandlerRegistration> mouseOutHandlers  = new ArrayList<HandlerRegistration>();
	private HashMap<String, CategoriesWidgets> categories = new HashMap<String, CategoriesWidgets>();
        
        private class CategoriesWidgets {

        private DisclosurePanel disclosurePanel;
        private VerticalPanel verticalPanel;

        public CategoriesWidgets(DisclosurePanel disclosurePanel, VerticalPanel verticalPanel) {
            this.disclosurePanel = disclosurePanel;
            this.verticalPanel = verticalPanel;
        }

        public DisclosurePanel getDisclosurePanel() {
            return disclosurePanel;
        }

        public VerticalPanel getVerticalPanel() {
            return verticalPanel;
        }
    }

        
	public SelectMeasureView() {
		mainPanel = new FlowPanel();
		mainPanel.addStyleName("selectMeasurePanel");
		Label titleLabel = new Label("Measures");
		titleLabel.addStyleName("titleLabel");
		mainPanel.add(titleLabel);
		listCategoriesPanel = new VerticalPanel();
		listCategoriesPanel.setSpacing(10);
				
		mainPanel.add(listCategoriesPanel);
		initWidget(mainPanel);
	}
	
	@Override
	public Widget asWidget() {
		return this;
	}

	@Override
	public int addMeasure(String measure, String category) {
		final Anchor measureAnchor = new Anchor(measure);
		measureAnchors.add(measureAnchor);
		listCategoriesPanel.add(measureAnchor);

		VerticalPanel categoryPanel = null;
		if(categories.containsKey(category)) {
			categoryPanel = categories.get(category).getVerticalPanel();
		} else {
			categoryPanel = new VerticalPanel();
			categoryPanel.addStyleName("selectMeasurePanel");
			DisclosurePanel disclosurePanel = new DisclosurePanel(category);
			disclosurePanel.add(categoryPanel);
			listCategoriesPanel.add(disclosurePanel);
			categories.put(category, new CategoriesWidgets(disclosurePanel, categoryPanel));		
                }
		categoryPanel.add(measureAnchor);
		
		int index = measureAnchors.indexOf(measureAnchor);
		addStyleAndHandlers(index);
		return index;
	}
	
	private void addStyleAndHandlers(int index) {
		GWT.log("SelectedMeasureView: Enabling measure " + index);
		final Anchor measureAnchor = measureAnchors.get(index);
		measureAnchor.addStyleName("listAnchor");
		final InfoPopup popup = new InfoPopup(measureAnchor.getText());
		HandlerRegistration addClickHandler = measureAnchor.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				clearSelection();
				measureAnchor.addStyleName("selectedLabel");
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
		HandlerRegistration addMouseOverHandler = measureAnchor.addMouseOverHandler(new MouseOverHandler() {
			
			@Override
			public void onMouseOver(final MouseOverEvent event) {
				GWT.log("ON MOUSE OVER");
				measureAnchor.addStyleName("highlightLabel");
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
		HandlerRegistration addMouseOutHandler = measureAnchor.addMouseOutHandler(new MouseOutHandler() {
			
			@Override
			public void onMouseOut(MouseOutEvent event) {
				GWT.log("ON MOUSE OUT");
				measureAnchor.removeStyleName("highlightLabel");
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
			GWT.log("SelectMeasureView: Removed click handler from " + index);
		}
		if (mouseOverHandlers.get(index) != null) {
			mouseOverHandlers.get(index).removeHandler();
			mouseOverHandlers.set(index, null);
			GWT.log("SelectMeasureView: Removed mouse over from " + index);
		}
		if (mouseOutHandlers.get(index) != null) {
			mouseOutHandlers.get(index).removeHandler();
			mouseOutHandlers.set(index, null);
			GWT.log("SelectMeasureView: Removed mouse out from " + index);
		}
	}
	
	
	
	private void clearSelection() {
		for (Anchor anchor : measureAnchors) {
			anchor.removeStyleName("selectedLabel");
		}
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

	@Override
	public HasClickHandlers getMeasureAnchor(int index) {
		return measureAnchors.get(index);
	}

	@Override
	public void enableMeasures() {
		for (int i=0; i<measureAnchors.size(); i++) {
			addStyleAndHandlers(i);
		}
		for (Anchor anchor : measureAnchors) {
			anchor.removeStyleName("hideMeasure");
		}
                
                for (CategoriesWidgets widgets : categories.values()) {
                        widgets.getDisclosurePanel().getHeader().removeStyleName("applyOpacityToCategory");
        }

	}

	@Override
	public void disableMeasures(Set<Integer> measures) {
		for (Integer index : measures) {
			measureAnchors.get(index).addStyleName("hideMeasure");
			removeStyleAndHandlers(index);
		}
                
                for (CategoriesWidgets widgets : categories.values()) {
                        Boolean allHidden = true;
                        VerticalPanel panel = widgets.getVerticalPanel();
                        for (int i = 0; i < panel.getWidgetCount(); i++) {
                            Anchor measure = (Anchor) panel.getWidget(i);
                            if (!measure.getStyleName().contains("hideMeasure")) {
                                allHidden = false;
                                break;
                            }
                        }
                        if (allHidden) {
                            widgets.getDisclosurePanel().getHeader().addStyleName("applyOpacityToCategory");
                        }
                }

          }
}
