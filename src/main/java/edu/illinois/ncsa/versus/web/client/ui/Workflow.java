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
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import edu.illinois.ncsa.mmdb.web.client.presenter.DatasetTablePresenter;
import edu.illinois.ncsa.mmdb.web.client.view.DynamicTableView;
import edu.illinois.ncsa.versus.web.client.RegistryService;
import edu.illinois.ncsa.versus.web.client.RegistryServiceAsync;
import edu.illinois.ncsa.versus.web.client.event.AddAdapterEvent;
import edu.illinois.ncsa.versus.web.client.event.AddExtractorEvent;
import edu.illinois.ncsa.versus.web.client.event.AddMeasureEvent;
import edu.illinois.ncsa.versus.web.client.presenter.CurrentSelectionsPresenter;
import edu.illinois.ncsa.versus.web.client.presenter.SelectAdapterPresenter;
import edu.illinois.ncsa.versus.web.client.presenter.SelectExtractorPresenter;
import edu.illinois.ncsa.versus.web.client.presenter.SelectMeasurePresenter;
import edu.illinois.ncsa.versus.web.client.view.CurrentSelectionsView;
import edu.illinois.ncsa.versus.web.client.view.SelectAdapterView;
import edu.illinois.ncsa.versus.web.client.view.SelectExtractorView;
import edu.illinois.ncsa.versus.web.client.view.SelectMeasureView;
import edu.illinois.ncsa.versus.web.shared.ComponentMetadata;
import java.util.List;
import net.customware.gwt.dispatch.client.DispatchAsync;

/**
 *
 * @author antoinev
 */
public class Workflow extends Composite {

    private final RegistryServiceAsync registryService = GWT.create(RegistryService.class);
    private final HandlerManager eventBus;
    private final VerticalPanel previousDisclosureBody;

    public Workflow(final DispatchAsync dispatchAsync, final HandlerManager eventBus) {
        this.eventBus = eventBus;

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
        CurrentSelectionsView currentSelectionsView = new CurrentSelectionsView();
        CurrentSelectionsPresenter currentSelectionsPresenter = new CurrentSelectionsPresenter(
                registryService, eventBus, currentSelectionsView);
        currentSelectionsPresenter.go(currentSelectionPanel);

        DockLayoutPanel comparePanel = new DockLayoutPanel(Style.Unit.EM);
        comparePanel.addSouth(currentSelectionPanel, 8);
        comparePanel.add(new ScrollPanel(newExecutionPanel));

        // previous executions
        previousDisclosureBody = new VerticalPanel();
        previousDisclosureBody.setWidth("95%");
        ScrollPanel previousExecScroll = new ScrollPanel(previousDisclosureBody);

        // dataset selection
        DynamicTableView datasetTableView = new DynamicTableView();
        final DatasetTablePresenter datasetTablePresenter = new DatasetTablePresenter(dispatchAsync,
                eventBus, datasetTableView);
        datasetTablePresenter.bind();
        VerticalPanel selectionDisclosureBody = new VerticalPanel() {

            @Override
            protected void onDetach() {
                super.onDetach();
                datasetTablePresenter.unbind();
            }
        };
        selectionDisclosureBody.setWidth("100%");
        selectionDisclosureBody.add(datasetTableView.asWidget());
        ScrollPanel selectedDataScroll = new ScrollPanel(selectionDisclosureBody);

        // tab panel layout
        TabLayoutPanel tabPanel = new TabLayoutPanel(2, Style.Unit.EM);
        tabPanel.setWidth("100%");
        tabPanel.add(selectedDataScroll, "Selected Data");
        tabPanel.add(comparePanel, "Compare");
        tabPanel.add(previousExecScroll, "View Results");
        tabPanel.selectTab(0);

        initWidget(tabPanel);

        populate();
        datasetTablePresenter.refresh();
    }

    public HasWidgets getResultWidget() {
        return previousDisclosureBody;
    }

    private void populate() {

        registryService.getAdapters(new AsyncCallback<List<ComponentMetadata>>() {

            @Override
            public void onFailure(Throwable caught) {
                GWT.log("Error retrieving adapters", caught);
            }

            @Override
            public void onSuccess(List<ComponentMetadata> result) {
                for (ComponentMetadata metadata : result) {
                    eventBus.fireEvent(new AddAdapterEvent(metadata));
                }
            }
        });

        registryService.getExtractors(new AsyncCallback<List<ComponentMetadata>>() {

            @Override
            public void onSuccess(List<ComponentMetadata> result) {
                for (ComponentMetadata metadata : result) {
                    eventBus.fireEvent(new AddExtractorEvent(metadata));
                }
            }

            @Override
            public void onFailure(Throwable caught) {
                GWT.log("Error retrieving extractors", caught);
            }
        });

        registryService.getMeasures(new AsyncCallback<List<ComponentMetadata>>() {

            @Override
            public void onSuccess(List<ComponentMetadata> result) {
                for (ComponentMetadata metadata : result) {
                    eventBus.fireEvent(new AddMeasureEvent(metadata));
                }
            }

            @Override
            public void onFailure(Throwable caught) {
                GWT.log("Error retrieving measures", caught);
            }
        });
    }
}
