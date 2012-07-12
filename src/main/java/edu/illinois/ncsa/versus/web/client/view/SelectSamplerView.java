/*
 * This software was developed at the National Institute of Standards and
 * Technology by employees of the Federal Government in the course of
 * their official duties. Pursuant to title 17 Section 105 of the United
 * States Code this software is not subject to copyright protection and is
 * in the public domain. This software is an experimental system. NIST assumes
 * no responsibility whatsoever for its use by other parties, and makes no
 * guarantees, expressed or implied, about its quality, reliability, or
 * any other characteristic. We would appreciate acknowledgement if the
 * software is used.
 */
package edu.illinois.ncsa.versus.web.client.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import edu.illinois.ncsa.versus.web.client.presenter.SelectSamplerPresenter.Display;

/**
 *
 * @author antoinev
 */
public class SelectSamplerView extends Composite implements Display {

    private final FlowPanel mainPanel;

    private final VerticalPanel listCategoriesPanel;

    private final List<Anchor> samplerAnchors = new ArrayList<Anchor>();

    private final HashMap<String, CategoriesWidget> categories = new HashMap<String, CategoriesWidget>();

    public SelectSamplerView() {
        mainPanel = new FlowPanel();
        mainPanel.addStyleName("selectSamplerPanel");
        Label titleLabel = new Label("Samplers");
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
    public int addSampler(String sampler, String category, String helpLink) {
        final Anchor samplerAnchor = new Anchor(sampler);
        FlowPanel samplerPanel = new FlowPanel();
        samplerPanel.add(samplerAnchor);
        samplerAnchors.add(samplerAnchor);

        if (helpLink != null && !helpLink.isEmpty()) {
            samplerPanel.add(new InlineHTML("&nbsp;&nbsp;"));
            Anchor helpAnchor = new Anchor("<img src=\"images/help-browser.png\" alt=\"help\">", true, helpLink, "_blank");
            samplerPanel.add(helpAnchor);
        }

        VerticalPanel categoryPanel;
        if (categories.containsKey(category)) {
            categoryPanel = categories.get(category).getVerticalPanel();
        } else {
            categoryPanel = new VerticalPanel();
            categoryPanel.addStyleName("selectSamplerPanel");
            DisclosurePanel disclosurePanel = new DisclosurePanel(category);
            disclosurePanel.add(categoryPanel);
            listCategoriesPanel.add(disclosurePanel);
            categories.put(category, new CategoriesWidget(disclosurePanel, categoryPanel));
        }
        categoryPanel.add(samplerPanel);

        addStyleAndHandlers(samplerAnchor);
        return samplerAnchors.indexOf(samplerAnchor);
    }

    @Override
    public HasClickHandlers getSamplerAnchor(int index) {
        return samplerAnchors.get(index);
    }

    @Override
    public void selectSampler(int index) {
        clearSelection();
        samplerAnchors.get(index).addStyleName("selectedLabel");
    }

    @Override
    public void unselectSampler() {
        clearSelection();
    }

    private void clearSelection() {
        for (Anchor anchor : samplerAnchors) {
            anchor.removeStyleName("selectedLabel");
        }
    }
    
    private void addStyleAndHandlers(final Anchor samplerAnchor) {
        samplerAnchor.addStyleName("measureAnchor");
        
        samplerAnchor.addMouseOverHandler(new MouseOverHandler() {

            @Override
            public void onMouseOver(final MouseOverEvent event) {
                samplerAnchor.addStyleName("highlightLabel");
            }
        });
        samplerAnchor.addMouseOutHandler(new MouseOutHandler() {

            @Override
            public void onMouseOut(MouseOutEvent event) {
                samplerAnchor.removeStyleName("highlightLabel");
            }
        });
    }
}
