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

import java.util.Date;
import java.util.Set;

import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import edu.illinois.ncsa.mmdb.web.client.dispatch.GetPreviews;
import edu.illinois.ncsa.mmdb.web.client.ui.PreviewWidget;
import edu.illinois.ncsa.versus.web.client.InfoPopup;
import edu.illinois.ncsa.versus.web.client.presenter.SamplingJobStatusPresenter.Display;
import edu.uiuc.ncsa.cet.bean.DatasetBean;
import net.customware.gwt.dispatch.client.DispatchAsync;

/**
 *
 * @author antoinev
 */
public class SamplingJobStatusView extends Composite implements Display {

    private final DispatchAsync dispatchAsync;

    private FlowPanel mainPanel;

    private final SimplePanel startPanel;

    private final SimplePanel endPanel;

    private final SimplePanel statusPanel;

    private HorizontalPanel selectionsPanel;

    private SimplePanel selectedIndividualPanel;

    private SimplePanel selectedSamplerPanel;

    private SimplePanel selectedSampleSizePanel;

    private final FlowPanel selectedDatasetsPanel;

    private final FlowPanel samplePanel;

    private DisclosurePanel disclosurePanel;

    public SamplingJobStatusView(DispatchAsync dispatchAsync) {
        this.dispatchAsync = dispatchAsync;

        mainPanel = new FlowPanel();
        mainPanel.addStyleName("jobStatusPanel");

        selectionsPanel = new HorizontalPanel();
        selectionsPanel.setSpacing(20);
        mainPanel.add(selectionsPanel);
        selectedIndividualPanel = new SimplePanel();
        selectedIndividualPanel.setWidget(new Label("*"));
        selectionsPanel.add(selectedIndividualPanel);
        selectionsPanel.add(new HTML("&#8594;"));
        selectedSamplerPanel = new SimplePanel();
        selectedSamplerPanel.setWidget(new Label("*"));
        selectionsPanel.add(selectedSamplerPanel);
        selectionsPanel.add(new HTML("&#8594;"));
        selectedSampleSizePanel = new SimplePanel();
        selectedSampleSizePanel.setWidget(new Label("*"));
        selectionsPanel.add(selectedSampleSizePanel);
        mainPanel.add(selectionsPanel);

        startPanel = new SimplePanel();
        startPanel.addStyleName("jobStatusSubPanel");
        mainPanel.add(startPanel);
        endPanel = new SimplePanel();
        endPanel.addStyleName("jobStatusSubPanel");
        mainPanel.add(endPanel);
        statusPanel = new SimplePanel();
        statusPanel.addStyleName("jobStatusSubPanel");
        mainPanel.add(statusPanel);
        selectedDatasetsPanel = new FlowPanel();
        mainPanel.add(selectedDatasetsPanel);
        samplePanel = new FlowPanel();
        mainPanel.add(samplePanel);

        disclosurePanel = new DisclosurePanel("Result");
        disclosurePanel.addStyleName("resultDisclosurePanel");
        disclosurePanel.setWidth("100%");
        disclosurePanel.setAnimationEnabled(true);
        disclosurePanel.setContent(mainPanel);
        disclosurePanel.setOpen(true);

        initWidget(disclosurePanel);
    }

    @Override
    public Widget asWidget() {
        return this;
    }

    @Override
    public void setStatus(String status) {
        FlowPanel panel = new FlowPanel();
        panel.add(new Label("Status: " + status));
        statusPanel.setWidget(panel);
    }

    @Override
    public void setStart(Date date) {
        String dateString = DateTimeFormat.getFormat(
                PredefinedFormat.DATE_TIME_MEDIUM).format(date);
        disclosurePanel.getHeaderTextAccessor().setText(dateString);
        startPanel.setWidget(new Label("Start: " + dateString));
    }

    @Override
    public void setEnd(Date date) {
        if (date != null) {
            String dateString = DateTimeFormat.getFormat(
                    PredefinedFormat.DATE_TIME_MEDIUM).format(date);
            disclosurePanel.getHeaderTextAccessor().setText(dateString);
            endPanel.setWidget(new Label("End: " + dateString));
        }
    }

    @Override
    public void setDatasets(Set<DatasetBean> datasets) {
        selectedDatasetsPanel.clear();
        selectedDatasetsPanel.add(new Label("Datasets:"));
        for (DatasetBean bean : datasets) {
            PreviewWidget previewWidget = new PreviewWidget(bean.getUri(),
                    GetPreviews.SMALL, "dataset?id=" + bean.getUri(), dispatchAsync);
            previewWidget.addStyleName("inlinethumbnail");
            selectedDatasetsPanel.add(previewWidget);
            addPopup(previewWidget, bean);
        }
    }

    @Override
    public void setIndividual(String individual) {
        selectedIndividualPanel.setWidget(new Label(individual));
    }

    @Override
    public void setSampler(String sampler) {
        selectedSamplerPanel.setWidget(new Label(sampler));
    }

    @Override
    public void setSampleSize(int sampleSize) {
        selectedSampleSizePanel.setWidget(new Label(Integer.toString(sampleSize)));
    }

    @Override
    public void setSample(Set<DatasetBean> sample) {
        samplePanel.clear();
        samplePanel.add(new Label("Sample:"));
        if (sample != null) {
            for (DatasetBean bean : sample) {
                PreviewWidget previewWidget = new PreviewWidget(bean.getUri(),
                        GetPreviews.SMALL, "dataset?id=" + bean.getUri(), dispatchAsync);
                previewWidget.addStyleName("inlinethumbnail");
                samplePanel.add(previewWidget);
                addPopup(previewWidget, bean);
            }
        }
    }

    private void addPopup(PreviewWidget previewWidget, DatasetBean bean) {
        final InfoPopup popup = new InfoPopup(bean.getTitle());
        previewWidget.addMouseOverHandler(new MouseOverHandler() {

            @Override
            public void onMouseOver(final MouseOverEvent event) {
                popup.setPopupPosition(event.getClientX(),
                        event.getClientY() + 25);
                popup.show();
            }
        });
        previewWidget.addMouseOutHandler(new MouseOutHandler() {

            @Override
            public void onMouseOut(MouseOutEvent event) {
                popup.hide();
            }
        });
    }
}
