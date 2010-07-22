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

import edu.illinois.ncsa.versus.web.client.presenter.SelectAdapterPresenter.Display;

public class SelectAdapterView extends Composite implements Display {

	private FlowPanel mainPanel;
	private VerticalPanel listAdaptersPanel;
	private final List<Anchor> adapterAnchors = new ArrayList<Anchor>();

	public SelectAdapterView() {
		mainPanel = new FlowPanel();
		mainPanel.addStyleName("selectAdapterPanel");
		Label titleLabel = new Label("Adapters");
		titleLabel.addStyleName("titleLabel");
		mainPanel.add(titleLabel);
		listAdaptersPanel = new VerticalPanel();
		listAdaptersPanel.setSpacing(10);
		mainPanel.add(listAdaptersPanel);
		initWidget(mainPanel);
	}
	
	@Override
	public Widget asWidget() {
		return this;
	}

	@Override
	public int addAdapter(String adapter) {
		final Anchor adapterAnchor = new Anchor(adapter);
		adapterAnchor.addStyleName("measureAnchor");
		adapterAnchors.add(adapterAnchor);
		adapterAnchor.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				clearSelection();
//				adapterAnchor.removeStyleName("measureAnchor");
				adapterAnchor.addStyleName("selectedLabel");
			}
		});
		adapterAnchor.addMouseOverHandler(new MouseOverHandler() {
			
			@Override
			public void onMouseOver(final MouseOverEvent event) {
				adapterAnchor.addStyleName("highlightLabel");
			}
		});
		adapterAnchor.addMouseOutHandler(new MouseOutHandler() {
			
			@Override
			public void onMouseOut(MouseOutEvent event) {
				adapterAnchor.removeStyleName("highlightLabel");
			}
		});
		adapterAnchors.add(adapterAnchor);
		listAdaptersPanel.add(adapterAnchor);
		return adapterAnchors.indexOf(adapterAnchor);
	}

	private void clearSelection() {
		for (Anchor anchor : adapterAnchors) {
			anchor.removeStyleName("selectedLabel");
		}
	}
	
	@Override
	public int getNumAdapters() {
		return adapterAnchors.size();
	}

	@Override
	public HasClickHandlers getAdapterAnchor(int index) {
		return adapterAnchors.get(index);
	}

}
