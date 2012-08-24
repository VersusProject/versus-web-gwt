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
package edu.illinois.ncsa.versus.web.client.presenter;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;

import edu.illinois.ncsa.mmdb.web.client.MMDB;
import edu.illinois.ncsa.versus.web.client.ExecutionService;
import edu.illinois.ncsa.versus.web.client.ExecutionServiceAsync;
import edu.illinois.ncsa.versus.web.client.event.NewSamplingJobEvent;
import edu.illinois.ncsa.versus.web.client.event.NewSubmissionEvent;
import edu.illinois.ncsa.versus.web.client.event.SamplerSelectedEvent;
import edu.illinois.ncsa.versus.web.client.event.SamplerSelectedHandler;
import edu.illinois.ncsa.versus.web.client.event.SamplerUnselectedEvent;
import edu.illinois.ncsa.versus.web.client.event.SamplerUnselectedHandler;
import edu.illinois.ncsa.versus.web.client.event.SubmissionFailureEvent;
import edu.illinois.ncsa.versus.web.shared.ComponentMetadata;
import edu.illinois.ncsa.versus.web.shared.SamplingJob;
import edu.illinois.ncsa.versus.web.shared.SamplingSubmission;

/**
 *
 * @author antoinev
 */
public class SamplingCurrentSelectionsPresenter implements Presenter {

    private final HandlerManager eventBus;

    private final Display display;

    private ComponentMetadata individual = new ComponentMetadata(
            "gov.nist.itl.ssd.sampling.impl.BasicIndividual",
            "Basic individual", "", "", "");

    private ComponentMetadata sampler;

    private final ExecutionServiceAsync executionService =
            GWT.create(ExecutionService.class);

    public interface Display {

        Widget asWidget();

        void setSampler(String name);

        HasClickHandlers getExecuteButton();

        HasValueChangeHandlers<String> getSampleSizeChangeHandler();

        void setSamplingSizeErrorMessage(String message);

        String getSampleSize();
    }

    public SamplingCurrentSelectionsPresenter(HandlerManager eventBus,
            Display currentSelectionsView) {
        this.eventBus = eventBus;
        this.display = currentSelectionsView;
    }

    @Override
    public void go(HasWidgets container) {
        bind();
        container.add(display.asWidget());
    }

    private void bind() {
        eventBus.addHandler(SamplerSelectedEvent.TYPE, new SamplerSelectedHandler() {

            @Override
            public void onSamplerSelected(SamplerSelectedEvent event) {
                sampler = event.getSamplerMetadata();
                display.setSampler(sampler.getName());
            }
        });

        eventBus.addHandler(SamplerUnselectedEvent.TYPE, new SamplerUnselectedHandler() {

            @Override
            public void onSamplerUnselected(SamplerUnselectedEvent event) {
                sampler = null;
                display.setSampler("");
            }
        });

        display.getSampleSizeChangeHandler().addValueChangeHandler(
                new ValueChangeHandler<String>() {

                    @Override
                    public void onValueChange(ValueChangeEvent<String> event) {
                        checkSampleSize(event.getValue());
                    }
                });

        display.getExecuteButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                submitExecution();
            }
        });
    }

    private boolean checkSampleSize(String value) {
        boolean validSampleSize = isValidSampleSize(value);
        String error = validSampleSize ? "" : getSampleSizeErrorMessage();
        display.setSamplingSizeErrorMessage(error);
        return validSampleSize;
    }

    private boolean isValidSampleSize(String value) {
        int size;
        try {
            size = Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return false;
        }
        if (size <= 0) {
            return false;
        }
        if (size > MMDB.getSessionState().getSelectedDatasets().size()) {
            return false;
        }
        return true;
    }

    private String getSampleSizeErrorMessage() {
        int maxSize = MMDB.getSessionState().getSelectedDatasets().size();
        if (maxSize == 0) {
            return "Select data before setting the sample size.";
        }
        return "The sample size must be an integer between 1 and " + maxSize + ".";
    }

    private void submitExecution() {
        String sampleSizeString = display.getSampleSize();
        if (!checkSampleSize(sampleSizeString)) {
            return;
        }
        int sampleSize = Integer.parseInt(sampleSizeString);

        final SamplingSubmission submission = new SamplingSubmission();
        submission.setDatasetsURI(MMDB.getSessionState().getSelectedDatasets());
        submission.setIndividual(individual);
        submission.setSampler(sampler);
        submission.setSampleSize(sampleSize);

        eventBus.fireEvent(new NewSubmissionEvent(submission));
        
        executionService.submit(submission, new AsyncCallback<SamplingJob>() {

            @Override
            public void onFailure(Throwable caught) {
                GWT.log("Error submitting execution", caught);
                eventBus.fireEvent(new SubmissionFailureEvent(submission, caught));
            }

            @Override
            public void onSuccess(SamplingJob result) {
                GWT.log("Execution successfully submitted");
                eventBus.fireEvent(new NewSamplingJobEvent(result, submission));
            }
        });
    }
}
