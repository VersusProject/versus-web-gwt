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
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;

import edu.illinois.ncsa.versus.web.client.event.AddSamplerEvent;
import edu.illinois.ncsa.versus.web.client.event.AddSamplerEventHandler;
import edu.illinois.ncsa.versus.web.client.event.SamplerSelectedEvent;
import edu.illinois.ncsa.versus.web.client.event.SamplerUnselectedEvent;
import edu.illinois.ncsa.versus.web.shared.ComponentMetadata;

/**
 *
 * @author antoinev
 */
public class SelectSamplerPresenter implements Presenter {

    private final HandlerManager eventBus;

    private final Display display;
    private String selectedSamplerId;

    public interface Display {

        int addSampler(String sampler, String category, String helpLink);

        HasClickHandlers getSamplerAnchor(int index);

        void selectSampler(int index);

        void unselectSampler();

        Widget asWidget();
    }

    public SelectSamplerPresenter(HandlerManager eventBus, Display display) {
        this.eventBus = eventBus;
        this.display = display;
    }

    @Override
    public void go(HasWidgets container) {
        bind();
        container.add(display.asWidget());
    }

    private void bind() {
        eventBus.addHandler(AddSamplerEvent.TYPE, new AddSamplerEventHandler() {

            @Override
            public void onAddSampler(AddSamplerEvent event) {
                final ComponentMetadata samplerMetadata = event.getSamplerMetadata();
                final int index = display.addSampler(samplerMetadata.getName(),
                        samplerMetadata.getCategory(),
                        samplerMetadata.getHelpLink());
                display.getSamplerAnchor(index).addClickHandler(new ClickHandler() {

                    @Override
                    public void onClick(ClickEvent event) {
                        if(samplerMetadata.getId().equals(selectedSamplerId)) {
                            selectedSamplerId = null;
                            eventBus.fireEvent(new SamplerUnselectedEvent(samplerMetadata));
                            display.unselectSampler();
                        } else {
                            selectedSamplerId = samplerMetadata.getId();
                            eventBus.fireEvent(new SamplerSelectedEvent(samplerMetadata));
                            display.selectSampler(index);
                        }
                    }
                });
            }
        });
    }
}
