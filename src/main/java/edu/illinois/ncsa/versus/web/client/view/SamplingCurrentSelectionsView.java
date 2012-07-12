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

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

import edu.illinois.ncsa.versus.web.client.presenter.SamplingCurrentSelectionsPresenter.Display;

/**
 *
 * @author antoinev
 */
public class SamplingCurrentSelectionsView extends Composite implements Display {

    private static final String NO_SAMPLER_SELECTED = "No sampler selected";

    private final SimplePanel selectedSamplerPanel;

    private final Button executeButton;

    private final TextBox sampleSizeTextBox;

    private final Image errorImage;

    private final Label errorMessage;

    public SamplingCurrentSelectionsView() {
        HorizontalPanel mainPanel = new HorizontalPanel();
        mainPanel.addStyleName("currentSelectionsView");
        mainPanel.setSpacing(20);
        mainPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        mainPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);

        selectedSamplerPanel = new SimplePanel();
        selectedSamplerPanel.setWidget(new Label(NO_SAMPLER_SELECTED));
        mainPanel.add(selectedSamplerPanel);

        mainPanel.add(new HTML("&#8594;"));

        sampleSizeTextBox = new TextBox();
        sampleSizeTextBox.setVisibleLength(10);
        HorizontalPanel selectedSizePanel = new HorizontalPanel();
        selectedSizePanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
        selectedSizePanel.add(new Label("Sampling size:"));
        selectedSizePanel.add(sampleSizeTextBox);
        errorImage = new Image("images/dialog-error.png");
        errorImage.setVisible(false);
        final PopupPanel pp = new PopupPanel(true, false);
        errorMessage = new Label();
        pp.add(errorMessage);
        errorImage.addMouseOverHandler(new MouseOverHandler() {

            @Override
            public void onMouseOver(MouseOverEvent event) {
                pp.setPopupPositionAndShow(new PopupPanel.PositionCallback() {

                    @Override
                    public void setPosition(int offsetWidth, int offsetHeight) {
                        pp.setPopupPosition(
                                errorImage.getAbsoluteLeft() + errorImage.getWidth(),
                                errorImage.getAbsoluteTop() - offsetHeight);
                    }
                });
            }
        });
        errorImage.addMouseOutHandler(new MouseOutHandler() {

            @Override
            public void onMouseOut(MouseOutEvent event) {
                pp.hide();
            }
        });
        selectedSizePanel.add(errorImage);
        mainPanel.add(selectedSizePanel);

        executeButton = new Button("Launch");
        mainPanel.add(executeButton);
        initWidget(mainPanel);
    }

    @Override
    public Widget asWidget() {
        return this;
    }

    @Override
    public void setSampler(String name) {
        if (name.isEmpty()) {
            selectedSamplerPanel.setWidget(new Label(NO_SAMPLER_SELECTED));
        } else {
            selectedSamplerPanel.setWidget(new Label(name));
        }
    }

    @Override
    public HasValueChangeHandlers<String> getSampleSizeChangeHandler() {
        return sampleSizeTextBox;
    }

    @Override
    public void setSamplingSizeErrorMessage(String message) {
        errorMessage.setText(message);
        errorImage.setVisible(message != null && !message.isEmpty());
    }

    @Override
    public String getSampleSize() {
        return sampleSizeTextBox.getValue();
    }

    @Override
    public HasClickHandlers getExecuteButton() {
        return executeButton;
    }
}
