package edu.illinois.ncsa.versus.web.client.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.ui.*;

import edu.illinois.ncsa.versus.web.client.presenter.SelectAdapterPresenter.Display;

public class SelectAdapterView extends Composite implements Display {

    private FlowPanel mainPanel;
    private VerticalPanel listCategoriesPanel;
    private final List<Anchor> adapterAnchors = new ArrayList<Anchor>();
    private HashMap<String, CategoriesWidget> categories = new HashMap<String, CategoriesWidget>();

    public SelectAdapterView() {
        mainPanel = new FlowPanel();
        mainPanel.addStyleName("selectAdapterPanel");
        Label titleLabel = new Label("Adapters");
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
    public int addAdapter(String adapter, String category, String helpLink) {
        final Anchor adapterAnchor = new Anchor(adapter);
        FlowPanel adapterPanel = new FlowPanel();
        adapterPanel.add(adapterAnchor);
        adapterAnchors.add(adapterAnchor);

        if (helpLink != null && !helpLink.isEmpty()) {
            adapterPanel.add(new InlineHTML("&nbsp;&nbsp;"));
            Anchor helpAnchor = new Anchor("<img src=\"images/help-browser.png\" alt=\"help\">", true, helpLink, "_blank");
            adapterPanel.add(helpAnchor);
        }

        VerticalPanel categoryPanel;
        if (categories.containsKey(category)) {
            categoryPanel = categories.get(category).getVerticalPanel();
        } else {
            categoryPanel = new VerticalPanel();
            categoryPanel.addStyleName("selectAdapterPanel");
            DisclosurePanel disclosurePanel = new DisclosurePanel(category);
            disclosurePanel.add(categoryPanel);
            listCategoriesPanel.add(disclosurePanel);
            categories.put(category, new CategoriesWidget(disclosurePanel, categoryPanel));
        }
        categoryPanel.add(adapterPanel);

        addStyleAndHandlers(adapterAnchor);
        return adapterAnchors.indexOf(adapterAnchor);
    }
    
    private void addStyleAndHandlers(final Anchor adapterAnchor) {
        adapterAnchor.addStyleName("measureAnchor");
        
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

    @Override
    public void selectAdapter(int index) {
        clearSelection();
        adapterAnchors.get(index).addStyleName("selectedLabel");
    }

    @Override
    public void unselectAdapter(int index) {
        clearSelection();
    }
}
