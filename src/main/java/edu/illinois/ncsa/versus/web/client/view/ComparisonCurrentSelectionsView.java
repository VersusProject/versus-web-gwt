/**
 *
 */
package edu.illinois.ncsa.versus.web.client.view;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import edu.illinois.ncsa.mmdb.web.client.dispatch.GetPreviews;
import edu.illinois.ncsa.mmdb.web.client.ui.PreviewWidget;
import edu.illinois.ncsa.versus.web.client.InfoPopup;
import edu.illinois.ncsa.versus.web.client.presenter.ComparisonCurrentSelectionsPresenter.Display;
import edu.uiuc.ncsa.cet.bean.DatasetBean;
import net.customware.gwt.dispatch.client.DispatchAsync;

/**
 * @author lmarini
 *
 */
public class ComparisonCurrentSelectionsView extends Composite implements Display {

    private static final String NO_ADAPTER_SELECTED = "No adapter selected";

    private static final String NO_EXTRACTOR_SELECTED = "No extractor selected";

    private static final String NO_MEASURE_SELECTED = "No measure selected";

    private static final String SELECT_REFERENCE_IMAGE = "Select reference image";

    private final DispatchAsync dispatchAsync;

    private final SimplePanel selectedAdapterPanel;

    private final SimplePanel selectedExtractorPanel;

    private final SimplePanel selectedMeasurePanel;

    private final SimplePanel referenceImagePanel;

    private final Button executeButton;

    public ComparisonCurrentSelectionsView(DispatchAsync dispatchAsync) {
        this.dispatchAsync = dispatchAsync;

        VerticalPanel mainPanel = new VerticalPanel();
        mainPanel.addStyleName("currentSelectionsView");
        mainPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        HorizontalPanel selectionsPanel = new HorizontalPanel();
        selectionsPanel.setSpacing(20);
        mainPanel.add(selectionsPanel);
        selectedAdapterPanel = new SimplePanel();
        selectedAdapterPanel.setWidget(new Label(NO_ADAPTER_SELECTED));
        selectionsPanel.add(selectedAdapterPanel);
        selectionsPanel.add(new HTML("&#8594;"));
        selectedExtractorPanel = new SimplePanel();
        selectedExtractorPanel.setWidget(new Label(NO_EXTRACTOR_SELECTED));
        selectionsPanel.add(selectedExtractorPanel);
        selectionsPanel.add(new HTML("&#8594;"));
        selectedMeasurePanel = new SimplePanel();
        selectedMeasurePanel.setWidget(new Label(NO_MEASURE_SELECTED));
        selectionsPanel.add(selectedMeasurePanel);
        HorizontalPanel launchPanel = new HorizontalPanel();
        referenceImagePanel = new SimplePanel();
        referenceImagePanel.setWidget(new Label(SELECT_REFERENCE_IMAGE));
        referenceImagePanel.sinkEvents(Event.ONCLICK);
        referenceImagePanel.setSize("120px", "120px");
        launchPanel.add(referenceImagePanel);
        executeButton = new Button("Launch");
        launchPanel.add(executeButton);
        mainPanel.add(launchPanel);
        initWidget(mainPanel);
    }

    @Override
    public Widget asWidget() {
        return this;
    }

    @Override
    public void setAdapter(String name) {
        if (name.equals("")) {
            selectedAdapterPanel.setWidget(new Label(NO_ADAPTER_SELECTED));
        } else {
            selectedAdapterPanel.setWidget(new Label(name));
        }
    }

    @Override
    public void setMeasure(String name) {
        if (name.equals("")) {
            selectedMeasurePanel.setWidget(new Label(NO_MEASURE_SELECTED));
        } else {
            selectedMeasurePanel.setWidget(new Label(name));
        }
    }

    @Override
    public void setExtractor(String name) {
        if (name.equals("")) {
            selectedExtractorPanel.setWidget(new Label(NO_EXTRACTOR_SELECTED));
        } else {
            selectedExtractorPanel.setWidget(new Label(name));
        }
    }

    @Override
    public HasClickHandlers getExecuteButton() {
        return executeButton;
    }

    @Override
    public void addSelectImageHandler(ClickHandler clickHandler) {
        referenceImagePanel.addHandler(clickHandler, ClickEvent.getType());
    }

    private String shortenTitle(String title) {
        if (title != null && title.length() > 15) {
            return title.substring(0, 10) + "...";
        } else {
            return title;
        }
    }

    @Override
    public void setSelectedImage(DatasetBean datasetBean) {
        referenceImagePanel.clear();
        if (datasetBean == null) {
            referenceImagePanel.setWidget(new Label(SELECT_REFERENCE_IMAGE));
        } else {

            final String fullTitle = datasetBean.getTitle();
            final String shortTitle = shortenTitle(fullTitle);
            final Label label = new Label(shortTitle);
            label.addStyleName("smallText");
            label.setWidth("100px");
            final InfoPopup popup = new InfoPopup(fullTitle);
            label.addMouseOverHandler(new MouseOverHandler() {

                @Override
                public void onMouseOver(final MouseOverEvent event) {
                    popup.setPopupPosition(event.getClientX(), event.getClientY() + 25);
                    popup.show();
                }
            });
            label.addMouseOutHandler(new MouseOutHandler() {

                @Override
                public void onMouseOut(MouseOutEvent event) {
                    popup.hide();
                }
            });

            PreviewWidget pre = new PreviewWidget(
                    datasetBean.getUri(), GetPreviews.SMALL, null, dispatchAsync);
            pre.setWidth("120px");
            pre.setMaxWidth(100);

            HorizontalPanel titlePanel = new HorizontalPanel();
            titlePanel.add(label);
            final VerticalPanel layoutPanel = new VerticalPanel();

            layoutPanel.addStyleName("datasetSelectionThumbnail");
            layoutPanel.setHeight("130px");
            layoutPanel.add(pre);
            layoutPanel.add(titlePanel);
            layoutPanel.addStyleName("inline");
            referenceImagePanel.add(layoutPanel);
        }
    }
}
