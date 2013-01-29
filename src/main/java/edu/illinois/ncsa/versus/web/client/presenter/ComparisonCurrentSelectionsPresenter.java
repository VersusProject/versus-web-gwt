/**
 *
 */
package edu.illinois.ncsa.versus.web.client.presenter;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

import edu.illinois.ncsa.mmdb.web.client.event.DatasetSelectedEvent;
import edu.illinois.ncsa.mmdb.web.client.event.DatasetSelectedHandler;
import edu.illinois.ncsa.mmdb.web.client.event.DatasetUnselectedEvent;
import edu.illinois.ncsa.mmdb.web.client.event.DatasetUnselectedHandler;
import edu.illinois.ncsa.mmdb.web.client.event.RefreshEvent;
import edu.illinois.ncsa.mmdb.web.client.event.RefreshHandler;
import edu.illinois.ncsa.versus.web.client.ExecutionService;
import edu.illinois.ncsa.versus.web.client.ExecutionServiceAsync;
import edu.illinois.ncsa.versus.web.client.RegistryServiceAsync;
import edu.illinois.ncsa.versus.web.client.Versus_web;
import edu.illinois.ncsa.versus.web.client.event.AdapterSelectedEvent;
import edu.illinois.ncsa.versus.web.client.event.AdapterSelectedHandler;
import edu.illinois.ncsa.versus.web.client.event.AdapterUnselectedEvent;
import edu.illinois.ncsa.versus.web.client.event.AdapterUnselectedHandler;
import edu.illinois.ncsa.versus.web.client.event.ExtractorSelectedEvent;
import edu.illinois.ncsa.versus.web.client.event.ExtractorSelectedHandler;
import edu.illinois.ncsa.versus.web.client.event.ExtractorUnselectedEvent;
import edu.illinois.ncsa.versus.web.client.event.ExtractorUnselectedHandler;
import edu.illinois.ncsa.versus.web.client.event.MeasureSelectedEvent;
import edu.illinois.ncsa.versus.web.client.event.MeasureSelectedHandler;
import edu.illinois.ncsa.versus.web.client.event.MeasureUnselectedEvent;
import edu.illinois.ncsa.versus.web.client.event.MeasureUnselectedHandler;
import edu.illinois.ncsa.versus.web.client.event.NewJobEvent;
import edu.illinois.ncsa.versus.web.client.event.NewSubmissionEvent;
import edu.illinois.ncsa.versus.web.client.event.SubmissionFailureEvent;
import edu.illinois.ncsa.versus.web.client.view.DatasetSelectionView;
import edu.illinois.ncsa.versus.web.shared.ComparisonSubmission;
import edu.illinois.ncsa.versus.web.shared.ComponentMetadata;
import edu.illinois.ncsa.versus.web.shared.Job;
import edu.uiuc.ncsa.cet.bean.DatasetBean;
import net.customware.gwt.dispatch.client.DispatchAsync;

/**
 * @author lmarini
 *
 */
public class ComparisonCurrentSelectionsPresenter implements Presenter {

    private final DispatchAsync dispatchAsync;

    private final RegistryServiceAsync registryService;

    private final HandlerManager eventBus;

    private final Display display;

    private ComponentMetadata adapter;

    private ComponentMetadata measure;

    private ComponentMetadata extractor;

    private DatasetBean referenceBean = null;

    private final DatasetSelectionPresenter datasetSelectionPresenter;

    private final PopupPanel popupPanel;

    private final ExecutionServiceAsync executionService = GWT.create(ExecutionService.class);

    public interface Display {

        Widget asWidget();

        void setAdapter(String name);

        void setMeasure(String name);

        void setExtractor(String name);

        void addSelectImageHandler(ClickHandler clickHandler);

        void setSelectedImage(DatasetBean datasetBean);

        HasClickHandlers getExecuteButton();
    }

    public ComparisonCurrentSelectionsPresenter(DispatchAsync dispatchAsync,
            RegistryServiceAsync registryService, HandlerManager eventBus,
            Display currentSelectionsView) {
        this.dispatchAsync = dispatchAsync;
        this.registryService = registryService;
        this.eventBus = eventBus;
        this.display = currentSelectionsView;

        DatasetSelectionView view = new DatasetSelectionView(dispatchAsync);
        datasetSelectionPresenter =
                new DatasetSelectionPresenter(view, dispatchAsync, eventBus,
                Versus_web.getSessionState().getSelectedDatasets());

        popupPanel = new PopupPanel(true);
        popupPanel.setGlassEnabled(true);
        popupPanel.setStyleName("datasetSelectionPopup");

        datasetSelectionPresenter.getCloseHandler().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                popupPanel.hide();
            }
        });

        popupPanel.addCloseHandler(new CloseHandler<PopupPanel>() {
            @Override
            public void onClose(CloseEvent<PopupPanel> event) {
                referenceBean = datasetSelectionPresenter.getSelectedDataset();
                display.setSelectedImage(referenceBean);
            }
        });
        ScrollPanel scrollPanel = new ScrollPanel();
        datasetSelectionPresenter.go(scrollPanel);
        popupPanel.add(scrollPanel);
    }

    @Override
    public void go(HasWidgets container) {
        bind();
        container.add(display.asWidget());
    }

    private void checkReferenceBean() {
        if (referenceBean == null) {
            return;
        }
        if (Versus_web.getSessionState().getSelectedDatasets().contains(
                referenceBean.getUri())) {
            return;
        }
        referenceBean = null;
        display.setSelectedImage(referenceBean);
    }

    private void bind() {
        eventBus.addHandler(AdapterSelectedEvent.TYPE, new AdapterSelectedHandler() {
            @Override
            public void onAdapterSelected(AdapterSelectedEvent event) {
                adapter = event.getAdapterMetadata();
                display.setAdapter(adapter.getName());
            }
        });

        eventBus.addHandler(AdapterUnselectedEvent.TYPE, new AdapterUnselectedHandler() {
            @Override
            public void onAdapterUnselected(AdapterUnselectedEvent event) {
                adapter = null;
                display.setAdapter("");
            }
        });

        eventBus.addHandler(MeasureSelectedEvent.TYPE, new MeasureSelectedHandler() {
            @Override
            public void onMeasureSelected(MeasureSelectedEvent event) {
                measure = event.getMeasureMetadata();
                display.setMeasure(measure.getName());
            }
        });

        eventBus.addHandler(MeasureUnselectedEvent.TYPE, new MeasureUnselectedHandler() {
            @Override
            public void onMeasureUnselected(MeasureUnselectedEvent event) {
                measure = null;
                display.setMeasure("");
            }
        });

        eventBus.addHandler(ExtractorSelectedEvent.TYPE, new ExtractorSelectedHandler() {
            @Override
            public void onExtractorSelected(ExtractorSelectedEvent event) {
                extractor = event.getExtractorMetadata();
                display.setExtractor(extractor.getName());
            }
        });

        eventBus.addHandler(ExtractorUnselectedEvent.TYPE, new ExtractorUnselectedHandler() {
            @Override
            public void onExtractorUnselected(ExtractorUnselectedEvent event) {
                extractor = null;
                display.setExtractor("");
            }
        });

        eventBus.addHandler(RefreshEvent.TYPE, new RefreshHandler() {
            @Override
            public void onRefresh(RefreshEvent event) {
                checkReferenceBean();
            }
        });

        eventBus.addHandler(DatasetUnselectedEvent.TYPE, new DatasetUnselectedHandler() {
            @Override
            public void onDatasetUnselected(DatasetUnselectedEvent datasetUnselectedEvent) {
                checkReferenceBean();
            }
        });

        eventBus.addHandler(DatasetSelectedEvent.TYPE, new DatasetSelectedHandler() {
            @Override
            public void onDatasetSelected(DatasetSelectedEvent datasetSelectedEvent) {
                checkReferenceBean();
            }
        });

        display.getExecuteButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                submitExecution();
            }
        });

        display.addSelectImageHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                datasetSelectionPresenter.setSelectedDataset(referenceBean);
                popupPanel.center();
                DOM.setStyleAttribute(popupPanel.getElement(), "overflow", "auto");
            }
        });
    }

    /**
     * Submit a new job to the server.
     */
    protected void submitExecution() {
        final ComparisonSubmission submission = new ComparisonSubmission();
        submission.setDatasetsURI(Versus_web.getSessionState().getSelectedDatasets());
        submission.setAdapter(adapter);
        submission.setExtraction(extractor);
        submission.setMeasure(measure);
        if (referenceBean != null) {
            submission.setReferenceDataset(referenceBean.getUri());
        }

        eventBus.fireEvent(new NewSubmissionEvent(submission));

        // submit execution
        executionService.submit(submission, new AsyncCallback<Job>() {
            @Override
            public void onSuccess(Job job) {
                GWT.log("Execution successfully submitted");
                eventBus.fireEvent(new NewJobEvent(job, submission));
            }

            @Override
            public void onFailure(Throwable caught) {
                GWT.log("Error submitting execution", caught);
                eventBus.fireEvent(new SubmissionFailureEvent(submission, caught));
            }
        });
    }
}
