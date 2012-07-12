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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;

import edu.illinois.ncsa.mmdb.web.client.MMDB;
import edu.illinois.ncsa.versus.web.client.event.SamplerSelectedEvent;
import edu.illinois.ncsa.versus.web.client.event.SamplerSelectedHandler;
import edu.illinois.ncsa.versus.web.client.event.SamplerUnselectedEvent;
import edu.illinois.ncsa.versus.web.client.event.SamplerUnselectedHandler;
import edu.illinois.ncsa.versus.web.shared.ComponentMetadata;

/**
 *
 * @author antoinev
 */
public class SamplingCurrentSelectionsPresenter implements Presenter {

    private final HandlerManager eventBus;

    private final Display display;

    private ComponentMetadata sampler;

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
                        boolean validSampleSize = isValidSampleSize(event.getValue());
                        String error = validSampleSize ? "" : getSampleSizeErrorMessage();
                        display.setSamplingSizeErrorMessage(error);
                    }
                });

        display.getExecuteButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                submitExecution();
            }
        });
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
        if(maxSize == 0) {
            return "Select data before setting the sample size.";
        }
        return "The sample size must be an integer between 1 and " + maxSize + ".";
    }

    private void submitExecution() {
    }
}
