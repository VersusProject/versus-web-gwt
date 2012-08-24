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

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;

import edu.illinois.ncsa.versus.web.client.RegistryService;
import edu.illinois.ncsa.versus.web.client.RegistryServiceAsync;
import edu.illinois.ncsa.versus.web.client.event.AddAdapterEvent;
import edu.illinois.ncsa.versus.web.client.event.AddExtractorEvent;
import edu.illinois.ncsa.versus.web.client.event.AddMeasureEvent;
import edu.illinois.ncsa.versus.web.client.event.AddSamplerEvent;
import edu.illinois.ncsa.versus.web.client.presenter.ResultPresenter;
import edu.illinois.ncsa.versus.web.client.presenter.SelectedDatasetsPresenter;
import edu.illinois.ncsa.versus.web.client.view.ResultView;
import edu.illinois.ncsa.versus.web.client.view.SelectedDatasetsView;
import edu.illinois.ncsa.versus.web.shared.ComponentMetadata;
import net.customware.gwt.dispatch.client.DispatchAsync;

/**
 *
 * @author antoinev
 */
public class Workflow extends Composite {

    private final RegistryServiceAsync registryService = GWT.create(RegistryService.class);

    private final HandlerManager eventBus;

    public Workflow(final DispatchAsync dispatchAsync, 
            final HandlerManager eventBus) {
        this.eventBus = eventBus;

        // Workflow selection
        StackLayoutPanel workflowSelection = new StackLayoutPanel(Style.Unit.EM);
        HTML comparisonHeader = new HTML("Comparison");
        workflowSelection.add(new ComparisonWorkflow(dispatchAsync, eventBus, registryService), comparisonHeader, 2);
        HTML samplingHeader = new HTML("Sampling");
        workflowSelection.add(new SamplingWorkflow(dispatchAsync, eventBus, registryService), samplingHeader, 2);
        comparisonHeader.addStyleName("workflowHeader");
        samplingHeader.addStyleName("workflowHeader");

        // previous executions
        ResultView resultView = new ResultView();
        final ResultPresenter resultPresenter = new ResultPresenter(resultView, dispatchAsync, eventBus);
        resultPresenter.bind();
        ScrollPanel previousExecScroll = new ScrollPanel(resultView) {
            @Override
            protected void onDetach() {
                super.onDetach();
                resultPresenter.unbind();
            }
        };

        // dataset selection
        SelectedDatasetsView selectedDatasetsView = new SelectedDatasetsView();
        final SelectedDatasetsPresenter selectedDatasetsPresenter = 
                new SelectedDatasetsPresenter(dispatchAsync,
                eventBus, selectedDatasetsView);
        selectedDatasetsPresenter.bind();
        VerticalPanel selectionDisclosureBody = new VerticalPanel() {

            @Override
            protected void onDetach() {
                super.onDetach();
                selectedDatasetsPresenter.unbind();
            }
        };
        selectionDisclosureBody.setWidth("100%");
        selectionDisclosureBody.add(selectedDatasetsView.asWidget());
        ScrollPanel selectedDataScroll = new ScrollPanel(selectionDisclosureBody);

        // tab panel layout
        TabLayoutPanel tabPanel = new TabLayoutPanel(2, Style.Unit.EM);
        tabPanel.setWidth("100%");
        tabPanel.add(selectedDataScroll, "Selected Data");
        tabPanel.add(workflowSelection, "Worflow selection");
        tabPanel.add(previousExecScroll, "View Results");
        tabPanel.selectTab(0);

        initWidget(tabPanel);

        populate();
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

        registryService.getSamplers(new AsyncCallback<List<ComponentMetadata>>() {

            @Override
            public void onFailure(Throwable caught) {
                GWT.log("Error retrieving samplers", caught);
            }

            @Override
            public void onSuccess(List<ComponentMetadata> result) {
                for (ComponentMetadata metadata : result) {
                    eventBus.fireEvent(new AddSamplerEvent(metadata));
                }
            }
        });
    }
}
