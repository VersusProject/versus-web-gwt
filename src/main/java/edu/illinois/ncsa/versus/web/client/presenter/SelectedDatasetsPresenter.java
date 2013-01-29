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
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;

import edu.illinois.ncsa.mmdb.web.client.MMDB;
import edu.illinois.ncsa.mmdb.web.client.TextFormatter;
import edu.illinois.ncsa.mmdb.web.client.dispatch.GetDatasetsBySet;
import edu.illinois.ncsa.mmdb.web.client.dispatch.GetDatasetsBySetResult;
import edu.illinois.ncsa.mmdb.web.client.event.ClearDatasetsEvent;
import edu.illinois.ncsa.mmdb.web.client.event.NoMoreItemsEvent;
import edu.illinois.ncsa.mmdb.web.client.event.RefreshEvent;
import edu.illinois.ncsa.mmdb.web.client.event.RefreshHandler;
import edu.illinois.ncsa.mmdb.web.client.event.ShowItemEvent;
import edu.illinois.ncsa.mmdb.web.client.mvp.BasePresenter;
import edu.illinois.ncsa.mmdb.web.client.presenter.DynamicGridPresenter;
import edu.illinois.ncsa.mmdb.web.client.presenter.DynamicListPresenter;
import edu.illinois.ncsa.mmdb.web.client.ui.ContentCategory;
import edu.illinois.ncsa.mmdb.web.client.view.DynamicGridView;
import edu.illinois.ncsa.mmdb.web.client.view.DynamicListView;
import edu.illinois.ncsa.versus.web.client.Versus_web;
import edu.illinois.ncsa.versus.web.client.presenter.SelectedDatasetsPresenter.Display;
import edu.illinois.ncsa.versus.web.client.view.SelectedDatasetsView;
import edu.uiuc.ncsa.cet.bean.DatasetBean;
import edu.uiuc.ncsa.cet.bean.PersonBean;
import net.customware.gwt.dispatch.client.DispatchAsync;

/**
 *
 * @author antoinev
 */
public class SelectedDatasetsPresenter extends BasePresenter<Display> {

    protected String viewTypePreference;

    protected String viewType = SelectedDatasetsView.LIST_VIEW_TYPE;

    protected BasePresenter<?> viewTypePresenter;

    public interface Display {

        Set<HasValueChangeHandlers<String>> getViewListBox();

        Widget asWidget();

        void setContentView(Widget contentView);

        void setViewType(String viewType);
    }

    public SelectedDatasetsPresenter(DispatchAsync dispatch,
            HandlerManager eventBus, Display display) {
        this(dispatch, eventBus, display, SelectedDatasetsView.LIST_VIEW_TYPE);
    }

    public SelectedDatasetsPresenter(DispatchAsync dispatch,
            HandlerManager eventBus, Display display,
            String defaultview) {
        super(display, dispatch, eventBus);

        setViewType(MMDB.getSessionPreference(getViewTypePreference(), defaultview));

        addHandler(RefreshEvent.TYPE, new RefreshHandler() {

            @Override
            public void onRefresh(RefreshEvent event) {
                refresh();
            }
        });
    }

    public void refresh() {
        getContent();
    }

    private void getContent() {
        final HashSet<String> selectedDatasets = new HashSet<String>(
                Versus_web.getSessionState().getSelectedDatasets());

        service.execute(new GetDatasetsBySet(selectedDatasets),
                new AsyncCallback<GetDatasetsBySetResult>() {

                    @Override
                    public void onFailure(Throwable caught) {
                        GWT.log("Error getting datasetbean from uri", caught);
                    }

                    @Override
                    public void onSuccess(GetDatasetsBySetResult result) {
                        eventBus.fireEvent(new ClearDatasetsEvent());

                        int index = 0;
                        for (DatasetBean item : result.getDatasets()) {
                            ShowItemEvent event = new ShowItemEvent();
                            event.setPosition(index);
                            addItem(event, item);
                            index++;
                            eventBus.fireEvent(event);
                        }
                        eventBus.fireEvent(new NoMoreItemsEvent());
                    }
                });
    }

    private void addItem(ShowItemEvent event, DatasetBean item) {
        event.setId(item.getUri());
        if (item.getTitle() != null) {
            event.setTitle(item.getTitle());
        } else {
            event.setTitle(item.getUri());
        }
        PersonBean creator = item.getCreator();
        if (creator != null) {
            event.setAuthor(item.getCreator().getName());
        }
        event.setDate(item.getDate());
        event.setSize(TextFormatter.humanBytes(item.getSize()));
        event.setType(ContentCategory.getCategory(item.getMimeType(), service));
    }

    protected String getPageKey() {
        return this.getClass().getName();
    }

    @Override
    public void bind() {
        for (HasValueChangeHandlers<String> handler : display.getViewListBox()) {
            handler.addValueChangeHandler(new ValueChangeHandler<String>() {

                @Override
                public void onValueChange(ValueChangeEvent<String> event) {
                    GWT.log("View list box clicked " + event.getValue());
                    setViewType(event.getValue());
                    getContent();
                }
            });
        }

        getContent();
    }

    /**
     * Set the view type.
     *
     * @param viewType
     */
    private void setViewType(String viewType) {
        this.viewType = viewType;
        display.setViewType(viewType);
        MMDB.setSessionPreference(getViewTypePreference(), viewType);
        // unbind the existing presenter if any
        if (viewTypePresenter != null) {
            viewTypePresenter.unbind();
        }
        if (viewType.equals(SelectedDatasetsView.LIST_VIEW_TYPE)) {
            DynamicListView listView = new DynamicListView(service);
            DynamicListPresenter listPresenter = new DynamicListPresenter(
                    service, eventBus, listView);
            listPresenter.bind();
            viewTypePresenter = listPresenter;
            display.setContentView(listView);
        } else if (viewType.equals(SelectedDatasetsView.GRID_VIEW_TYPE)) {
            DynamicGridView gridView = new DynamicGridView(service);
            DynamicGridPresenter gridPresenter = new DynamicGridPresenter(
                    service, eventBus, gridView);
            gridPresenter.bind();
            viewTypePresenter = gridPresenter;
            display.setContentView(gridView);
        }
    }

    @Override
    public void unbind() {
        super.unbind();
        if (viewTypePresenter != null) {
            viewTypePresenter.unbind();
        }
    }

    private String getViewTypePreference() {
        return MMDB.DATASET_VIEW_TYPE_PREFERENCE;
    }
}
