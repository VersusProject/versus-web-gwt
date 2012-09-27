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

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.illinois.ncsa.versus.web.client.RegistryServiceAsync;
import edu.illinois.ncsa.versus.web.client.presenter.ComparisonCurrentSelectionsPresenter;
import edu.illinois.ncsa.versus.web.client.presenter.SelectAdapterPresenter;
import edu.illinois.ncsa.versus.web.client.presenter.SelectExtractorPresenter;
import edu.illinois.ncsa.versus.web.client.presenter.SelectMeasurePresenter;
import edu.illinois.ncsa.versus.web.client.view.ComparisonCurrentSelectionsView;
import edu.illinois.ncsa.versus.web.client.view.SelectAdapterView;
import edu.illinois.ncsa.versus.web.client.view.SelectExtractorView;
import edu.illinois.ncsa.versus.web.client.view.SelectMeasureView;
import net.customware.gwt.dispatch.client.DispatchAsync;

/**
 *
 * @author antoinev
 */
public class ComparisonWorkflow extends Composite {

    public ComparisonWorkflow(final DispatchAsync dispatchAsync,
            final HandlerManager eventBus, final RegistryServiceAsync registryService) {
        VerticalPanel newExecutionPanel = new VerticalPanel();
        newExecutionPanel.setWidth("100%");

        HorizontalPanel selectionPanel = new HorizontalPanel();
        selectionPanel.setBorderWidth(0);
        selectionPanel.setWidth("100%");
        newExecutionPanel.add(selectionPanel);

        // adapters
        SelectAdapterView selectAdapterView = new SelectAdapterView();
        SelectAdapterPresenter selectAdapterPresenter = new SelectAdapterPresenter(
                registryService, eventBus, selectAdapterView);
        selectAdapterPresenter.go(selectionPanel);

        // extractors
        SelectExtractorView selectExtractorView = new SelectExtractorView();
        SelectExtractorPresenter selectExtractorPresenter = new SelectExtractorPresenter(
                registryService, eventBus, selectExtractorView);
        selectExtractorPresenter.go(selectionPanel);

        // measures
        SelectMeasureView selectMeasureView = new SelectMeasureView();
        SelectMeasurePresenter selectMeasurePresenter = new SelectMeasurePresenter(
                registryService, eventBus, selectMeasureView);
        selectMeasurePresenter.go(selectionPanel);

        // current selections
        SimplePanel currentSelectionPanel = new SimplePanel();
        ComparisonCurrentSelectionsView currentSelectionsView = 
                new ComparisonCurrentSelectionsView(dispatchAsync);
        ComparisonCurrentSelectionsPresenter currentSelectionsPresenter =
                new ComparisonCurrentSelectionsPresenter(dispatchAsync,
                registryService, eventBus, currentSelectionsView);
        currentSelectionsPresenter.go(currentSelectionPanel);

        DockLayoutPanel comparePanel = new DockLayoutPanel(Style.Unit.PX);
        comparePanel.addSouth(currentSelectionPanel, 200);
        comparePanel.add(new ScrollPanel(newExecutionPanel));

        initWidget(comparePanel);
    }
}
