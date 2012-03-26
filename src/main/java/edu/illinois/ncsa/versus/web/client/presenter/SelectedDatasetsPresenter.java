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

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.rpc.AsyncCallback;
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
import edu.illinois.ncsa.mmdb.web.client.presenter.DynamicTablePresenter;
import edu.illinois.ncsa.mmdb.web.client.ui.ContentCategory;
import edu.illinois.ncsa.mmdb.web.client.view.DynamicGridView;
import edu.illinois.ncsa.mmdb.web.client.view.DynamicListView;
import edu.illinois.ncsa.mmdb.web.client.view.DynamicTableView;
import edu.uiuc.ncsa.cet.bean.DatasetBean;
import edu.uiuc.ncsa.cet.bean.PersonBean;
import java.util.HashSet;
import net.customware.gwt.dispatch.client.DispatchAsync;

/**
 *
 * @author antoinev
 */
public class SelectedDatasetsPresenter extends BasePresenter<DynamicTablePresenter.Display> {

    private int pageSize = DynamicListView.DEFAULT_PAGE_SIZE;
    protected String sortKey = "date-desc";
    protected String viewTypePreference;
    protected String viewType = DynamicTableView.LIST_VIEW_TYPE;
    protected String sizeType = DynamicTableView.PAGE_SIZE_X1;
    protected int numberOfPages;
    protected int currentPage = 1;
    protected BasePresenter<?> viewTypePresenter;

    public SelectedDatasetsPresenter(DispatchAsync dispatch, HandlerManager eventBus, DynamicTablePresenter.Display display) {
        this(dispatch, eventBus, display, DynamicTableView.LIST_VIEW_TYPE, DynamicTableView.PAGE_SIZE_X1);
    }

    public SelectedDatasetsPresenter(DispatchAsync dispatch, HandlerManager eventBus, DynamicTablePresenter.Display display, String defaultview, String defaultsize) {
        super(display, dispatch, eventBus);

        sortKey = MMDB.getSessionPreference(getViewSortPreference(), "date-desc");
        display.setOrder(sortKey);
        setViewType(MMDB.getSessionPreference(getViewTypePreference(), defaultview), MMDB.getSessionPreference(getViewSizeTypePreference(), defaultsize));

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
        final HashSet<String> selectedDatasets = new HashSet<String>(MMDB.getSessionState().getSelectedDatasets());

        service.execute(new GetDatasetsBySet(selectedDatasets), new AsyncCallback<GetDatasetsBySetResult>() {

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
                final int np = (int) Math.ceil((double) selectedDatasets.size() / getPageSize());
                setNumberOfPages(np);
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

    /**
     * Set the total number of pages.
     *
     * @param numberOfPages
     */
    protected void setNumberOfPages(int numberOfPages) {
        this.numberOfPages = numberOfPages;
        display.setTotalNumPages(numberOfPages);
    }

    protected boolean rememberPageNumber() {
        return false;
    }

    protected String getPageKey() {
        return this.getClass().getName();
    }

    void setPage(int page) {
        currentPage = page;
        display.setPage(page);
        if (rememberPageNumber()) {
            GWT.log("Remembering that we're on page " + page);
            MMDB.getSessionState().setPage(getPageKey(), page); // remember page number in session
        }
    }

    @Override
    public void bind() {
        for (HasValueChangeHandlers<Integer> handler : display.getPagingWidget()) {
            handler.addValueChangeHandler(new ValueChangeHandler<Integer>() {

                @Override
                public void onValueChange(ValueChangeEvent<Integer> event) {
                    int page = event.getValue();
                    GWT.log("User changed page to " + page);
                    setPage(page);
                    getContent();
                }
            });
        }

        for (HasValueChangeHandlers<String> handler : display.getSortListBox()) {
            handler.addValueChangeHandler(new ValueChangeHandler<String>() {

                @Override
                public void onValueChange(ValueChangeEvent<String> event) {
                    GWT.log("Sort list box clicked " + event.getValue());
                    sortKey = event.getValue();
                    display.setOrder(sortKey);
                    setPage(1); // FIXME stay on same page?
                    MMDB.setSessionPreference(getViewSortPreference(), sortKey);
                    getContent();
                }
            });
        }

        for (HasValueChangeHandlers<String> handler : display.getViewListBox()) {
            handler.addValueChangeHandler(new ValueChangeHandler<String>() {

                @Override
                public void onValueChange(ValueChangeEvent<String> event) {
                    GWT.log("View list box clicked " + event.getValue());
                    changeViewType(event.getValue(), sizeType);
                    getContent();
                }
            });
        }

        for (HasValueChangeHandlers<String> handler : display.getSizeListBox()) {
            handler.addValueChangeHandler(new ValueChangeHandler<String>() {

                @Override
                public void onValueChange(ValueChangeEvent<String> event) {
                    GWT.log("View page size box clicked " + event.getValue());
                    changeViewType(viewType, event.getValue());
                    getContent();
                }
            });
        }

        if (rememberPageNumber()) {
            int rememberedPage = MMDB.getSessionState().getPage(getPageKey());
            GWT.log("Setting page to remembered page " + rememberedPage);
            setPage(rememberedPage);
        }

        getContent();
    }

    int computeNewPage(int oldPage, int oldPageSize) {
        return (((oldPage - 1) * oldPageSize) / pageSize) + 1;
    }

    /**
     * Change the view type. Adjust the page according to the current page on
     * the previous view type.
     *
     * @param viewType
     * @param sizeType
     */
    protected void changeViewType(String viewType, String sizeType) {
        int oldPage = currentPage;
        int oldPageSize = pageSize;
        setViewType(viewType, sizeType);
        setPage(computeNewPage(oldPage, oldPageSize));
    }

    /**
     * Set the view type. Do not adjust the page according to any existing
     * currentPage value.
     *
     * @param viewType
     */
    private void setViewType(String viewType, String sizeType) {
        this.viewType = viewType;
        display.setViewType(viewType);
        MMDB.setSessionPreference(getViewTypePreference(), viewType);
        MMDB.setSessionPreference(getViewSizeTypePreference(), sizeType);
        // unbind the existing presenter if any
        if (viewTypePresenter != null) {
            viewTypePresenter.unbind();
        }
        if (viewType.equals(DynamicTableView.LIST_VIEW_TYPE)) {
            DynamicListView listView = new DynamicListView(service);
            display.changeListSizeNumbers();
            display.setSizeType(sizeType);
            if (sizeType.equals(DynamicTableView.PAGE_SIZE_X2)) {
                setPageSize(DynamicListView.PAGE_SIZE_X2);
            } else if (sizeType.equals(DynamicTableView.PAGE_SIZE_X4)) {
                setPageSize(DynamicListView.PAGE_SIZE_X4);
            } else {
                setPageSize(DynamicListView.DEFAULT_PAGE_SIZE);
            }
            DynamicListPresenter listPresenter = new DynamicListPresenter(service, eventBus, listView);
            listPresenter.bind();
            viewTypePresenter = listPresenter;
            display.setContentView(listView);
        } else if (viewType.equals(DynamicTableView.GRID_VIEW_TYPE)) {
            display.changeGridSizeNumbers();
            display.setSizeType(sizeType);
            DynamicGridView gridView = new DynamicGridView(service);

            if (sizeType.equals(DynamicTableView.PAGE_SIZE_X2)) {
                setPageSize(DynamicGridView.PAGE_SIZE_X2);
            } else if (sizeType.equals(DynamicTableView.PAGE_SIZE_X4)) {
                setPageSize(DynamicGridView.PAGE_SIZE_X4);
            } else {
                setPageSize(DynamicGridView.DEFAULT_PAGE_SIZE);
            }

            DynamicGridPresenter gridPresenter = new DynamicGridPresenter(service, eventBus, gridView);
            gridPresenter.bind();
            viewTypePresenter = gridPresenter;
            display.setContentView(gridView);
        }
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
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

    private String getViewSizeTypePreference() {
        return MMDB.DATASET_VIEWSIZE_TYPE_PREFERENCE;
    }

    private String getViewSortPreference() {
        return MMDB.DATASET_VIEW_SORT_PREFERENCE;
    }
}
