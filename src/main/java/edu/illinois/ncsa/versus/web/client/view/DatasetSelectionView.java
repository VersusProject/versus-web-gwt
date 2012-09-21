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

import java.util.HashMap;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import edu.illinois.ncsa.mmdb.web.client.dispatch.GetPreviews;
import edu.illinois.ncsa.mmdb.web.client.ui.PreviewWidget;
import edu.illinois.ncsa.versus.web.client.InfoPopup;
import edu.illinois.ncsa.versus.web.client.presenter.DatasetSelectionPresenter.Display;
import edu.uiuc.ncsa.cet.bean.DatasetBean;
import net.customware.gwt.dispatch.client.DispatchAsync;

/**
 *
 * @author antoinev
 */
public class DatasetSelectionView extends Composite implements Display {

    private final FlowPanel filesPanel;

    private final Button button;

    private final DispatchAsync dispatchAsync;

    private final HashMap<CheckBox, DatasetBean> checkBoxes;

    public DatasetSelectionView(DispatchAsync dispatchAsync) {
        this.dispatchAsync = dispatchAsync;
        checkBoxes = new HashMap<CheckBox, DatasetBean>();
        VerticalPanel mainPanel = new VerticalPanel();
        mainPanel.setStyleName("datasetSelection");

        Label title = new Label("Select the reference file to use.");
        title.addStyleName("popupTitle");
        mainPanel.add(title);

        filesPanel = new FlowPanel();
        mainPanel.add(filesPanel);

        button = new Button("Done");
        button.addStyleName("buttonDone");
        mainPanel.add(button);

        initWidget(mainPanel);
    }

    @Override
    public void clear() {
        filesPanel.clear();
    }

    private void setCheckBoxSelection(CheckBox checkBox, boolean selected) {
        for (CheckBox cb : checkBoxes.keySet()) {
            cb.setValue(false);
        }
        if (checkBox != null) {
            checkBox.setValue(selected);
        }
    }

    @Override
    public void add(DatasetBean item) {

        final String fullTitle = item.getTitle();
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


        final CheckBox checkBox = new CheckBox();
        checkBox.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                setCheckBoxSelection(checkBox, checkBox.getValue());
            }
        });
        checkBoxes.put(checkBox, item);

        PreviewWidget pre = new PreviewWidget(
                item.getUri(), GetPreviews.SMALL, null, dispatchAsync);
        pre.setWidth("120px");
        pre.setMaxWidth(100);
        pre.sinkEvents(Event.ONCLICK);
        pre.addHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                setCheckBoxSelection(checkBox, !checkBox.getValue());
            }
        }, ClickEvent.getType());


        HorizontalPanel titlePanel = new HorizontalPanel();
        titlePanel.add(checkBox);
        titlePanel.add(label);
        final VerticalPanel layoutPanel = new VerticalPanel();

        layoutPanel.addStyleName("datasetSelectionThumbnail");
        layoutPanel.setHeight("130px");
        layoutPanel.add(pre);
        layoutPanel.add(titlePanel);
        layoutPanel.addStyleName("inline");

        filesPanel.add(layoutPanel);
    }

    private String shortenTitle(String title) {
        if (title != null && title.length() > 15) {
            return title.substring(0, 10) + "...";
        } else {
            return title;
        }
    }

    @Override
    public void setError(String error) {
        filesPanel.clear();
        filesPanel.add(new Label(error));
    }

    @Override
    public DatasetBean getSelectedDataset() {
        for (CheckBox cb : checkBoxes.keySet()) {
            if (cb.getValue()) {
                return checkBoxes.get(cb);
            }
        }
        return null;
    }

    @Override
    public void setSelectedDataset(DatasetBean datasetBean) {
        if (datasetBean == null) {
            setCheckBoxSelection(null, false);
        } else if (checkBoxes.containsValue(datasetBean)) {
            for (CheckBox cb : checkBoxes.keySet()) {
                if (datasetBean.getUri().equals(checkBoxes.get(cb).getUri())) {
                    setCheckBoxSelection(cb, true);
                }
            }
        }
    }

    @Override
    public Widget asWidget() {
        return this;
    }

    @Override
    public HasClickHandlers getCloseHandler() {
        return button;
    }
}
