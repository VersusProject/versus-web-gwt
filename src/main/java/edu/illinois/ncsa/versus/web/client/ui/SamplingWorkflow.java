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
package edu.illinois.ncsa.versus.web.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.illinois.ncsa.versus.web.client.RegistryService;
import edu.illinois.ncsa.versus.web.client.RegistryServiceAsync;
import edu.illinois.ncsa.versus.web.client.presenter.SamplingCurrentSelectionsPresenter;
import edu.illinois.ncsa.versus.web.client.presenter.SelectSamplerPresenter;
import edu.illinois.ncsa.versus.web.client.view.SamplingCurrentSelectionsView;
import edu.illinois.ncsa.versus.web.client.view.SelectSamplerView;
import net.customware.gwt.dispatch.client.DispatchAsync;

/**
 *
 * @author antoinev
 */
public class SamplingWorkflow extends Composite {

    public SamplingWorkflow(final DispatchAsync dispatchAsync,
            final HandlerManager eventBus, final RegistryServiceAsync registryService) {

        VerticalPanel newExecutionPanel = new VerticalPanel();
        newExecutionPanel.setWidth("100%");

        HorizontalPanel selectionPanel = new HorizontalPanel();
        selectionPanel.setBorderWidth(0);
        selectionPanel.setWidth("100%");
        newExecutionPanel.add(selectionPanel);

        // Sampler
        SelectSamplerView selectSamplerView = new SelectSamplerView();
        SelectSamplerPresenter selectSamplerPresenter =
                new SelectSamplerPresenter(eventBus, selectSamplerView);
        selectSamplerPresenter.go(selectionPanel);

        // Sample size

        // Current selection
        SimplePanel currentSelectionPanel = new SimplePanel();
        SamplingCurrentSelectionsView currentSelectionsView = 
                new SamplingCurrentSelectionsView();
        SamplingCurrentSelectionsPresenter currentSelectionsPresenter =
                new SamplingCurrentSelectionsPresenter(eventBus, currentSelectionsView);
        currentSelectionsPresenter.go(currentSelectionPanel);

        DockLayoutPanel mainPanel = new DockLayoutPanel(Style.Unit.EM);
        mainPanel.addSouth(currentSelectionPanel, 4);
        mainPanel.add(new ScrollPanel(newExecutionPanel));

        initWidget(mainPanel);
    }
}
