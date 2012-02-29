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
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
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
	
	private List<HandlerRegistration> clickHandlers  = new ArrayList<HandlerRegistration>();
	private List<HandlerRegistration> mouseOverHandlers  = new ArrayList<HandlerRegistration>();
	private List<HandlerRegistration> mouseOutHandlers  = new ArrayList<HandlerRegistration>();
	private List<TreeItem> treeItems = new ArrayList<TreeItem>();

	public SelectMeasureView() {
		mainPanel = new FlowPanel();
		mainPanel.addStyleName("selectMeasurePanel");
		Label titleLabel = new Label("Measures");
		titleLabel.addStyleName("titleLabel");
		mainPanel.add(titleLabel);
		listMeasuresPanel = new VerticalPanel();
		listMeasuresPanel.setSpacing(10);
		
		treeItems.add(new TreeItem("Lp Minkowski family")); //index 0
		treeItems.add(new TreeItem("L1 family")); //index 1
		treeItems.add(new TreeItem("Intersection family")); //index 2
		treeItems.add(new TreeItem("Inner Product family")); //index 3
		treeItems.add(new TreeItem("Fidelity family or Squared-chord family")); //index 4
		treeItems.add(new TreeItem("Squared L2 family or X2 family")); //index 5
		treeItems.add(new TreeItem("Shannon's entropy family")); //index 6
		treeItems.add(new TreeItem("Combinations")); //index 7
		treeItems.add(new TreeItem("Other")); //index 8
		
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
		measureAnchors.add(measureAnchor);
		listMeasuresPanel.add(measureAnchor);
		
		if ((measureAnchor.getText().contains("EuclideanL2")) || (measureAnchor.getText().contains("CityBlock"))) {
			treeItems.get(0).addItem(measureAnchor); //index 0 = Lp Minkowski family
		} else if ((measureAnchor.getText().contains("Sorensen")) || (measureAnchor.getText().contains("Soergel")) || (measureAnchor.getText().equals("KulczynskiMeasure")) || (measureAnchor.getText().contains("Canberra")) || (measureAnchor.getText().contains("Lorentzian"))) {
			treeItems.get(1).addItem(measureAnchor); //index 1 = L1 family
		} else if ((measureAnchor.getText().contains("Czekanowski") || (measureAnchor.getText().contains("Motyka")) || (measureAnchor.getText().equals("KulczynskiSMeasure")) || (measureAnchor.getText().contains("Ruzicka")) || (measureAnchor.getText().contains("Tanimoto"))) || measureAnchor.getText().contains("Hedges") || (measureAnchor.getText().equals("IntersectionMeasure"))) {
			treeItems.get(2).addItem(measureAnchor); //index 2 = Intersection family
		} else if ((measureAnchor.getText().contains("InnerProduct")) || (measureAnchor.getText().contains("Harmonic")) || (measureAnchor.getText().contains("Cosine")) || (measureAnchor.getText().contains("KumarHassebrook")) || (measureAnchor.getText().contains("Jaccard")) || (measureAnchor.getText().contains("Dice"))) {
			treeItems.get(3).addItem(measureAnchor); //index 3 = Inner Product family
		} else if ((measureAnchor.getText().contains("Bhattacharyya")) || (measureAnchor.getText().contains("Fidelity")) || (measureAnchor.getText().contains("Hellinger")) || (measureAnchor.getText().contains("Matusita")) || (measureAnchor.getText().contains("SquaredChord"))){
			treeItems.get(4).addItem(measureAnchor); //index 4 = Fidelity family or Squared-chord family
		} else if ((measureAnchor.getText().contains("SquaredEuclidean")) || (measureAnchor.getText().contains("Pearson")) || (measureAnchor.getText().contains("Neyman")) || (measureAnchor.getText().contains("ProbSym")) || (measureAnchor.getText().equals("DivergenceMeasure")) || (measureAnchor.getText().contains("Clark")) || (measureAnchor.getText().contains("Additive"))) {
			treeItems.get(5).addItem(measureAnchor); //index 5 = Squared L2 family or X2 family
		} else if ((measureAnchor.getText().contains("Kullback")) || (measureAnchor.getText().contains("Jeffrey")) || ((measureAnchor.getText().contains("KDivergenceMeasure"))) || ((measureAnchor.getText().contains("Topsoe"))) || ((measureAnchor.getText().contains("Jensen")))) {
			treeItems.get(6).addItem(measureAnchor); //index 6 = Shannon's entropy family
		} else if ((measureAnchor.getText().contains("Taneja")) || (measureAnchor.getText().contains("Taneja")) || ((measureAnchor.getText().contains("KumarJohnson"))) || ((measureAnchor.getText().contains("AvgDifference")))) {
			treeItems.get(7).addItem(measureAnchor); //index 7 = Combinations
		} else {
			treeItems.get(8).addItem(measureAnchor); //index 8 = other
		}
		
//		for (Anchor anchor : measureAnchors) {
//			GWT.log("################");
//			GWT.log("Measure Text1 from List: "+ anchor.getText());
//			GWT.log("Measure Text2: "+ measureAnchor.getText());
//			GWT.log("Measure Anchors List Size: "+measureAnchors.size());
//			GWT.log("################");
////			if (anchor.getClass().getInterfaces().equals("HasCategory")) {
////				if (anchor.getText().equals(measureAnchor.getText())) {
////					
////				}
////			}
//		}
//		
		Tree t = new Tree();
		t.addItem(treeItems.get(0));
		t.addItem(treeItems.get(1));
		t.addItem(treeItems.get(2));
		t.addItem(treeItems.get(3));
		t.addItem(treeItems.get(4));
		t.addItem(treeItems.get(5));
		t.addItem(treeItems.get(6));
		t.addItem(treeItems.get(7));
		t.addItem(treeItems.get(8));
		
		mainPanel.add(t);
		
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
	}

	@Override
	public void disableMeasures(Set<Integer> measures) {
		for (Integer index : measures) {
			measureAnchors.get(index).addStyleName("hideMeasure");
			removeStyleAndHandlers(index);
		}
	}

	@Override
	public void hideTreeItemsChildren() {
		//When the view is reset or no measure is highlighted  
		for (int i = 0; i < treeItems.size(); i++) {
			treeItems.get(i).setState(false);
		}
		
	}

	@Override
	public void displayTreeItemsChildren(String measure) {
		//Display tree children if measures of these trees items are highlighted
				GWT.log("measure in select measure view:"+ measure);
				
				if (measure.contains("Array")) {
					treeItems.get(8).setState(true); //index 8
					
					treeItems.get(0).setState(false);
					treeItems.get(1).setState(false);
					treeItems.get(2).setState(false);
					treeItems.get(3).setState(false);
					treeItems.get(4).setState(false);
					treeItems.get(5).setState(false);
					treeItems.get(6).setState(false);
					treeItems.get(7).setState(false);
				} else if ((measure.contains("MD5")) || (measure.contains("Dummy"))) {
					treeItems.get(8).setState(true); //index 8
					
					treeItems.get(0).setState(false);
					treeItems.get(1).setState(false);
					treeItems.get(2).setState(false);
					treeItems.get(3).setState(false);
					treeItems.get(4).setState(false);
					treeItems.get(5).setState(false);
					treeItems.get(6).setState(false);
					treeItems.get(7).setState(false);
				} else if (measure.contains("LabeledArray")) {
					treeItems.get(8).setState(true); //index 8
					
					treeItems.get(0).setState(false);
					treeItems.get(1).setState(false);
					treeItems.get(2).setState(false);
					treeItems.get(3).setState(false);
					treeItems.get(4).setState(false);
					treeItems.get(5).setState(false);
					treeItems.get(6).setState(false);
					treeItems.get(7).setState(false);
				} else if (measure.contains("Grayscale Histogram")) {
					treeItems.get(0).setState(true); //index 0
					treeItems.get(1).setState(true); //index 1
					treeItems.get(2).setState(true); //index 2
					treeItems.get(3).setState(true); //index 3
					treeItems.get(4).setState(true); //index 4
					treeItems.get(5).setState(true); //index 5
					treeItems.get(6).setState(true); //index 6
					treeItems.get(7).setState(true); //index 7
					treeItems.get(8).setState(true); //index 8
				} else if (measure.contains("RGB Histogram")) {
					treeItems.get(0).setState(true); //index 0
					treeItems.get(1).setState(true); //index 1
					treeItems.get(2).setState(true); //index 2
					treeItems.get(3).setState(true); //index 3
					treeItems.get(4).setState(true); //index 4
					treeItems.get(5).setState(true); //index 5
					treeItems.get(6).setState(true); //index 6
					treeItems.get(7).setState(true); //index 7
					treeItems.get(8).setState(true); //index 8
				} else if (measure.contains("Signature Vector")) {
					this.hideTreeItemsChildren(); // no measures
				} else if (measure.contains("Pixel Histogram")) {
					treeItems.get(5).setState(true); //index 7
					treeItems.get(8).setState(true); //index 8
					
					treeItems.get(0).setState(false);
					treeItems.get(1).setState(false);
					treeItems.get(2).setState(false);
					treeItems.get(3).setState(false);
					treeItems.get(4).setState(false);
					treeItems.get(6).setState(false);
					treeItems.get(7).setState(false);
				} else if (measure.contains("Vector")) {
					this.hideTreeItemsChildren(); // no measures
				}
		
	}

	@Override
	public DialogBox createDialogBox() {
		// TODO Auto-generated method stub
		return null;
	}
}
