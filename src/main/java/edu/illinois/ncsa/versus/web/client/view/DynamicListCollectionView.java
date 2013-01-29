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

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;

import edu.illinois.ncsa.mmdb.web.client.dispatch.DeleteDataset;
import edu.illinois.ncsa.mmdb.web.client.dispatch.DeleteDatasetResult;
import edu.illinois.ncsa.mmdb.web.client.dispatch.GetDatasetsInCollection;
import edu.illinois.ncsa.mmdb.web.client.dispatch.GetDatasetsInCollectionResult;
import edu.illinois.ncsa.mmdb.web.client.ui.ConfirmDialog;
import edu.illinois.ncsa.mmdb.web.client.ui.DownloadDialog;
import edu.illinois.ncsa.mmdb.web.client.ui.PreviewWidget;
import edu.illinois.ncsa.versus.web.client.event.CollectionDeletedEvent;
import edu.illinois.ncsa.versus.web.client.presenter.DynamicListCollectionPresenter.Display;
import net.customware.gwt.dispatch.client.DispatchAsync;

/**
 *
 * @author antoinev
 */
public class DynamicListCollectionView extends FlexTable implements Display {

    private final static DateTimeFormat DATE_TIME_FORMAT =
            DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.DATE_TIME_MEDIUM);

    private final DispatchAsync dispatchAsync;

    private final HandlerManager eventBus;

    public DynamicListCollectionView(DispatchAsync dispatchAsync,
            HandlerManager eventBus) {
        this.dispatchAsync = dispatchAsync;
        this.eventBus = eventBus;
        addStyleName("dynamicTableList");
    }

    @Override
    public int insertItem(final String uri) {
        final int row = this.getRowCount();

        // selection checkbox
        CheckBox checkBox = new CheckBox();
        setWidget(row, 0, checkBox);

        //image thumbnail
        FlowPanel images = new FlowPanel();
        images.setStyleName("imageOverlayPanel");

        PreviewWidget pre = PreviewWidget.newCollectionBadge(uri,
                "collection?uri=" + uri, dispatchAsync);
        pre.setMaxWidth(100);
        images.add(pre);

        setWidget(row, 1, images);

        FlexTable informationPanel = new FlexTable();
        informationPanel.addStyleName("dynamicTableListInformation");
        informationPanel.getFlexCellFormatter().setColSpan(0, 0, 2);
        setWidget(row, 2, informationPanel);

        getFlexCellFormatter().addStyleName(row, 0, "dynamicTableListCheckbox");
        getFlexCellFormatter().addStyleName(row, 1, "dynamicTableListPreview");
        getFlexCellFormatter().addStyleName(row, 2, "dynamicTableListCell");

        return row;
    }

    @Override
    public HasValue<Boolean> getSelected(int location) {
        return (CheckBox) getWidget(location, 0);
    }

    @Override
    public void setTitle(int row, String title, String uri) {
        FlexTable panel = (FlexTable) getWidget(row, 2);
        HorizontalPanel anchorPanel = new HorizontalPanel();
        Hyperlink hyperlink = new Hyperlink(title, "collection?uri=" + uri);
        anchorPanel.add(hyperlink);
        anchorPanel.add(new Label("")); //FIXME hack so entire row won't be linked
        panel.setWidget(0, 0, anchorPanel);
    }

    @Override
    public void setDate(int row, Date date) {
        FlexTable panel = (FlexTable) getWidget(row, 2);
        panel.setWidget(2, 0, new Label(DATE_TIME_FORMAT.format(date)));
    }

    @Override
    public void setAuthor(int row, String author) {
        FlexTable panel = (FlexTable) getWidget(row, 2);
        panel.setWidget(1, 0, new Label(author));
    }

    @Override
    public void setSize(int row, String size) {
        FlexTable panel = (FlexTable) getWidget(row, 2);
        panel.setWidget(1, 1, new Label(size));
    }

    @Override
    public void setDownload(int row, final String uri, final String name) {
        Anchor download = new Anchor();
        download.setText("Download");
        download.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                dispatchAsync.execute(new GetDatasetsInCollection(uri),
                        new AsyncCallback<GetDatasetsInCollectionResult>() {

                            @Override
                            public void onFailure(Throwable caught) {
                                ConfirmDialog cd = new ConfirmDialog("Error",
                                        "Cannot get datasets in collection.", false);
                                cd.getOkText().setText("OK");
                                GWT.log("Failed retrieving datasets in collection");
                            }

                            @Override
                            public void onSuccess(GetDatasetsInCollectionResult result) {
                                if (result.getDatasets().size() > 0) {
                                    DownloadDialog dd = new DownloadDialog(
                                            "Download Collection",
                                            result.getDatasets(), name);
                                } else {
                                    ConfirmDialog okay = new ConfirmDialog(
                                            "Error", "No datasets in collection", false);
                                    okay.getOkText().setText("OK");
                                }
                            }
                        });
            }
        });
        FlexTable panel = (FlexTable) getWidget(row, 2);
        panel.setWidget(3, 0, download);
    }

    @Override
    public void setDelete(int row, final String uri) {
        Anchor deleteAnchor = new Anchor("Delete");
        deleteAnchor.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                dispatchAsync.execute(new DeleteDataset(uri),
                        new AsyncCallback<DeleteDatasetResult>() {

                            @Override
                            public void onFailure(Throwable caught) {
                                ConfirmDialog okay = new ConfirmDialog(
                                        "Error", "Cannot delete collection", false);
                                okay.getOkText().setText("OK");
                                GWT.log("Failed deleting collection", caught);
                            }

                            @Override
                            public void onSuccess(DeleteDatasetResult result) {
                                eventBus.fireEvent(new CollectionDeletedEvent(uri));
                            }
                        });
            }
        });
        FlexTable panel = (FlexTable) getWidget(row, 2);
        panel.setWidget(4, 0, deleteAnchor);
    }
}
