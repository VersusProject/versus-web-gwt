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
package edu.illinois.ncsa.versus.web.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import edu.illinois.ncsa.mmdb.web.client.UploadWidget;
import edu.illinois.ncsa.mmdb.web.client.dispatch.GetDataset;
import edu.illinois.ncsa.mmdb.web.client.dispatch.GetDatasetResult;
import edu.illinois.ncsa.mmdb.web.client.event.DatasetUploadedEvent;
import edu.illinois.ncsa.mmdb.web.client.event.DatasetUploadedHandler;
import edu.illinois.ncsa.mmdb.web.client.ui.TitlePanel;
import net.customware.gwt.dispatch.client.DispatchAsync;

/**
 *
 * @author antoinev
 */
public class UploadPage extends Composite {

    public UploadPage(final DispatchAsync dispatchAsync) {
        final VerticalPanel globalPanel = new VerticalPanel();
        globalPanel.add(new TitlePanel("Upload"));

        HorizontalPanel panel = new HorizontalPanel();
        UploadWidget uploadWidget = new UploadWidget(false);
        uploadWidget.addDatasetUploadedHandler(new DatasetUploadedHandler() {

            @Override
            public void onDatasetUploaded(DatasetUploadedEvent event) {
                dispatchAsync.execute(new GetDataset(event.getDatasetUri()), new AsyncCallback<GetDatasetResult>() {

                    @Override
                    public void onFailure(Throwable caught) {
                        GWT.log("Error getting dataset", caught);
                    }

                    @Override
                    public void onSuccess(GetDatasetResult result) {
                        globalPanel.add(new edu.illinois.ncsa.mmdb.web.client.ui.DatasetInfoWidget(result.getDataset(), dispatchAsync));
                    }
                });
            }
        });
        panel.add(uploadWidget);


        globalPanel.add(panel);

        initWidget(globalPanel);
    }
}
