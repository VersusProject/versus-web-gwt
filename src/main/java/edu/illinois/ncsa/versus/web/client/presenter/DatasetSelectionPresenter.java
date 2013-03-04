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

import java.util.HashSet;
import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;

import edu.illinois.ncsa.mmdb.web.client.dispatch.GetDatasetsBySet;
import edu.illinois.ncsa.mmdb.web.client.dispatch.GetDatasetsBySetResult;
import edu.illinois.ncsa.mmdb.web.client.event.DatasetSelectedEvent;
import edu.illinois.ncsa.mmdb.web.client.event.DatasetSelectedHandler;
import edu.illinois.ncsa.mmdb.web.client.event.DatasetUnselectedEvent;
import edu.illinois.ncsa.mmdb.web.client.event.DatasetUnselectedHandler;
import edu.illinois.ncsa.mmdb.web.client.event.RefreshEvent;
import edu.illinois.ncsa.mmdb.web.client.event.RefreshHandler;
import edu.illinois.ncsa.mmdb.web.client.mvp.BasePresenter;
import edu.illinois.ncsa.versus.web.client.presenter.DatasetSelectionPresenter.Display;
import edu.uiuc.ncsa.cet.bean.DatasetBean;
import net.customware.gwt.dispatch.client.DispatchAsync;

/**
 *
 * @author antoinev
 */
public class DatasetSelectionPresenter extends BasePresenter<Display> {

    private final Set<String> datasets;

    public interface Display {

        Widget asWidget();

        void clear();

        void add(DatasetBean item);

        void setError(String error);

        DatasetBean getSelectedDataset();

        HasClickHandlers getCloseHandler();

        void setSelectedDataset(DatasetBean datasetBean);
    }

    public DatasetSelectionPresenter(Display display, DispatchAsync service,
            HandlerManager eventBus, Set<String> datasets) {
        super(display, service, eventBus);
        this.datasets = datasets;
        getContent();
    }

    public void go(HasWidgets container) {
        addHandler(RefreshEvent.TYPE, new RefreshHandler() {

            @Override
            public void onRefresh(RefreshEvent event) {
                getContent();
            }
        });

        addHandler(DatasetUnselectedEvent.TYPE, new DatasetUnselectedHandler() {

            @Override
            public void onDatasetUnselected(DatasetUnselectedEvent datasetUnselectedEvent) {
                getContent();
            }
        });

        addHandler(DatasetSelectedEvent.TYPE, new DatasetSelectedHandler() {

            @Override
            public void onDatasetSelected(DatasetSelectedEvent datasetSelectedEvent) {
                getContent();
            }
        });

        container.add(display.asWidget());
    }

    private void getContent() {
        final HashSet<String> allDatasets = new HashSet<String>(datasets);
        service.execute(new GetDatasetsBySet(allDatasets), new AsyncCallback<GetDatasetsBySetResult>() {

            @Override
            public void onFailure(Throwable caught) {
                GWT.log("Error getting datasetbeans", caught);
                display.setError("Cannot get datasets: " + caught.getMessage());
            }

            @Override
            public void onSuccess(GetDatasetsBySetResult result) {
                display.clear();

                for (DatasetBean item : result.getDatasets()) {
                    display.add(item);
                }
            }
        });
    }

    public DatasetBean getSelectedDataset() {
        return display.getSelectedDataset();
    }

    public void setSelectedDataset(DatasetBean datasetBean) {
        display.setSelectedDataset(datasetBean);
    }

    public HasClickHandlers getCloseHandler() {
        return display.getCloseHandler();
    }
}
