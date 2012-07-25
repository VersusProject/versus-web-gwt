/**
 *
 */
package edu.illinois.ncsa.versus.web.client.view;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Set;

import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import edu.illinois.ncsa.mmdb.web.client.dispatch.GetPreviews;
import edu.illinois.ncsa.mmdb.web.client.ui.PreviewWidget;
import edu.illinois.ncsa.versus.web.client.InfoPopup;
import edu.illinois.ncsa.versus.web.client.presenter.JobStatusPresenter.Display;
import edu.illinois.ncsa.versus.web.shared.Job;
import edu.illinois.ncsa.versus.web.shared.Job.ComparisonStatus;
import edu.illinois.ncsa.versus.web.shared.PairwiseComparison;
import net.customware.gwt.dispatch.client.DispatchAsync;

/**
 * @author lmarini
 *
 */
public class JobStatusView extends Composite implements Display {

    private FlowPanel mainPanel;

    private final SimplePanel startPanel;

    private final SimplePanel statusPanel;

    private final FlowPanel comparisonsPanel;

    private HorizontalPanel selectionsPanel;

    private SimplePanel selectedAdapterPanel;

    private SimplePanel selectedExtractorPanel;

    private SimplePanel selectedMeasurePanel;

    private DisclosurePanel disclosurePanel;

    private final DispatchAsync dispatchAsync;

    private final List<InfoPopup> popups;
    
    public JobStatusView(DispatchAsync dispatchAsync) {
        this.dispatchAsync = dispatchAsync;
        popups = new ArrayList<InfoPopup>();
        mainPanel = new FlowPanel();
        mainPanel.addStyleName("jobStatusPanel");

        selectionsPanel = new HorizontalPanel();
        selectionsPanel.setSpacing(20);
        mainPanel.add(selectionsPanel);
        selectedAdapterPanel = new SimplePanel();
        selectedAdapterPanel.setWidget(new Label("*"));
        selectionsPanel.add(selectedAdapterPanel);
        selectionsPanel.add(new HTML("&#8594;"));
        selectedExtractorPanel = new SimplePanel();
        selectedExtractorPanel.setWidget(new Label("*"));
        selectionsPanel.add(selectedExtractorPanel);
        selectionsPanel.add(new HTML("&#8594;"));
        selectedMeasurePanel = new SimplePanel();
        selectedMeasurePanel.setWidget(new Label("*"));
        selectionsPanel.add(selectedMeasurePanel);
        mainPanel.add(selectionsPanel);

        startPanel = new SimplePanel();
        startPanel.addStyleName("jobStatusSubPanel");
        mainPanel.add(startPanel);
        statusPanel = new SimplePanel();
        statusPanel.addStyleName("jobStatusSubPanel");
        mainPanel.add(statusPanel);
        comparisonsPanel = new FlowPanel();
        mainPanel.add(comparisonsPanel);

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
    public void setStart(Date date) {
        String dateString = DateTimeFormat.getMediumDateTimeFormat().format(date);
        disclosurePanel.getHeaderTextAccessor().setText(dateString);
        startPanel.setWidget(new Label(dateString));
    }

    @Override
    public void setStatus(String status) {
        FlowPanel panel = new FlowPanel();
        panel.add(new Label(status));
        statusPanel.setWidget(panel);
    }

    @Override
    public void setAdapter(String adapter) {
        selectedAdapterPanel.setWidget(new Label(adapter));
    }

    @Override
    public void setExtractor(String extractor) {
        selectedExtractorPanel.setWidget(new Label(extractor));
    }

    @Override
    public void setMeasure(String measure) {
        selectedMeasurePanel.setWidget(new Label(measure));
    }

    @Override
    public void showResults(Job job) {
        Set<PairwiseComparison> comparisons = job.getComparisons();
        comparisonsPanel.clear();
        clearPopups();
        List<PairwiseComparison> ordered = new ArrayList<PairwiseComparison>(
                comparisons);
        Collections.sort(ordered, new Comparator<PairwiseComparison>() {

            @Override
            public int compare(PairwiseComparison o1, PairwiseComparison o2) {
                if (o1.getSimilarity() != null & o2.getSimilarity() != null) {
                    double parseDouble = o1.getSimilarity();
                    double parseDouble2 = o2.getSimilarity();
                    if (parseDouble > parseDouble2) {
                        return -1;
                    } else if (parseDouble < parseDouble2) {
                        return 1;
                    } else {
                        return 0;
                    }
                } else {
                    return 0;
                }
            }
        });
        for (PairwiseComparison comparison : ordered) {
            HorizontalPanel pairwiseComparisonPanel = new HorizontalPanel();
            String firstId = comparison.getFirstDataset().getUri();
            PreviewWidget previewWidget = new PreviewWidget(firstId, GetPreviews.SMALL, "dataset?id=" + firstId, dispatchAsync);
            previewWidget.addStyleName("thumbnail");
            final InfoPopup popup1 = new InfoPopup(comparison.getFirstDataset().getTitle());
            previewWidget.addMouseOverHandler(new MouseOverHandler() {

                @Override
                public void onMouseOver(final MouseOverEvent event) {
                    popup1.setPopupPosition(event.getClientX(), event.getClientY() + 25);
                    popup1.show();
                }
            });
            previewWidget.addMouseOutHandler(new MouseOutHandler() {

                @Override
                public void onMouseOut(MouseOutEvent event) {
                    popup1.hide();
                }
            });
            popups.add(popup1);
            pairwiseComparisonPanel.add(previewWidget);
            pairwiseComparisonPanel.setCellVerticalAlignment(previewWidget,
                    HasVerticalAlignment.ALIGN_MIDDLE);
            String secondId = comparison.getSecondDataset().getUri();
            PreviewWidget previewWidget2 = new PreviewWidget(secondId, GetPreviews.SMALL, "dataset?id=" + secondId, dispatchAsync);
            previewWidget2.addStyleName("thumbnail");
            final InfoPopup popup2 = new InfoPopup(comparison.getSecondDataset().getTitle());
            previewWidget2.addMouseOverHandler(new MouseOverHandler() {

                @Override
                public void onMouseOver(final MouseOverEvent event) {
                    popup2.setPopupPosition(event.getClientX(), event.getClientY() + 25);
                    popup2.show();
                }
            });
            previewWidget2.addMouseOutHandler(new MouseOutHandler() {

                @Override
                public void onMouseOut(MouseOutEvent event) {
                    popup2.hide();
                }
            });
            popups.add(popup2);
            pairwiseComparisonPanel.add(previewWidget2);
            pairwiseComparisonPanel.setCellVerticalAlignment(previewWidget2,
                    HasVerticalAlignment.ALIGN_MIDDLE);

            String text = " = ";

            ComparisonStatus comparisonStatus = job.getStatus(comparison);
            if (comparisonStatus == ComparisonStatus.ENDED) {
                text += "<b>" + comparison.getSimilarity() + "</b>";
            } else if (comparisonStatus == ComparisonStatus.FAILED) {
                text += "<b>" + comparisonStatus + "</b><br />" + comparison.getError();
            }
            HTML similarity = new HTML(text);
            pairwiseComparisonPanel.add(similarity);
            pairwiseComparisonPanel.setCellVerticalAlignment(similarity,
                    HasVerticalAlignment.ALIGN_MIDDLE);

            comparisonsPanel.add(pairwiseComparisonPanel);
        }
    }

    private void clearPopups() {
        for(InfoPopup popup : popups) {
            popup.hide();
        }
        popups.clear();
    }
}
