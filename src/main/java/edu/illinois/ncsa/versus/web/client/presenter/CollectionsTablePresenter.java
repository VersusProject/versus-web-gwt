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

import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

import edu.illinois.ncsa.mmdb.web.client.MMDB;
import edu.illinois.ncsa.mmdb.web.client.dispatch.ListQueryResult;
import edu.illinois.ncsa.mmdb.web.client.event.ClearDatasetsEvent;
import edu.illinois.ncsa.mmdb.web.client.event.NoMoreItemsEvent;
import edu.illinois.ncsa.mmdb.web.client.event.RefreshEvent;
import edu.illinois.ncsa.mmdb.web.client.event.RefreshHandler;
import edu.illinois.ncsa.mmdb.web.client.event.ShowItemEvent;
import edu.illinois.ncsa.mmdb.web.client.mvp.BasePresenter;
import edu.illinois.ncsa.mmdb.web.client.view.DynamicListView;
import edu.illinois.ncsa.mmdb.web.client.view.DynamicTableView;
import edu.illinois.ncsa.versus.web.client.dispatch.ListQueryCollections;
import edu.illinois.ncsa.versus.web.client.view.DynamicCollectionTableView;
import edu.illinois.ncsa.versus.web.client.view.DynamicListCollectionView;
import edu.uiuc.ncsa.cet.bean.CollectionBean;
import edu.uiuc.ncsa.cet.bean.PersonBean;
import net.customware.gwt.dispatch.client.DispatchAsync;

/**
 *
 * @author antoinev
 */
public class CollectionsTablePresenter extends BasePresenter<CollectionsTablePresenter.Display> {

    private int pageSize = DynamicListView.DEFAULT_PAGE_SIZE;

    protected String sortKey = "date-desc";

    protected String viewTypePreference;

    protected String sizeType = DynamicTableView.PAGE_SIZE_X1;

    protected int numberOfPages;

    protected int currentPage = 1;

    protected BasePresenter<?> viewTypePresenter;

    public interface Display {

        void setCurrentPage(int num);

        void setTotalNumPages(int num);

        void addItem(String id, int position);

        Set<HasValueChangeHandlers<Integer>> getPagingWidget();

        Set<HasValueChangeHandlers<String>> getSortListBox();

        Set<HasValueChangeHandlers<String>> getSizeListBox();

        Widget asWidget();

        void removeAllRows();

        void setPage(Integer value);

        void setOrder(String order);

        void setContentView(Widget contentView);

        void setSizeType(String sizeType);

        void changeSizeNumbers();
    }

    public CollectionsTablePresenter(DispatchAsync dispatch,
            HandlerManager eventBus, Display display) {
        super(display, dispatch, eventBus);
        sortKey = MMDB.getSessionPreference(getViewSortPreference(), "date-desc");
        display.setOrder(sortKey);
        setViewSize(MMDB.getSessionPreference(getViewSizeTypePreference(),
                DynamicCollectionTableView.PAGE_SIZE_X1));

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

    /**
     * Retrieve server side items using {@link getQuery} and adding them to
     * display using {@link addItem}.
     */
    private void getContent() {
        ListQueryCollections query = getQuery();
        service.execute(query,
                new AsyncCallback<ListQueryResult<CollectionBean>>() {

                    @Override
                    public void onFailure(Throwable caught) {
                        GWT.log("Error retrieving items to show in table", caught);
                        DialogBox dialogBox = new DialogBox();
                        dialogBox.setText("Error retrieving items");
                        dialogBox.add(new Label(MMDB.SERVER_ERROR));
                        dialogBox.setAnimationEnabled(true);
                        dialogBox.center();
                        dialogBox.show();
                    }

                    @Override
                    public void onSuccess(final ListQueryResult<CollectionBean> result) {
                        eventBus.fireEvent(new ClearDatasetsEvent());
                        int index = 0;
                        for (CollectionBean item : result.getResults()) {
                            ShowItemEvent event = new ShowItemEvent();
                            event.setPosition(index);
                            addItem(event, item);
                            index++;
                            eventBus.fireEvent(event);
                        }
                        eventBus.fireEvent(new NoMoreItemsEvent());
                        final int np = (int) Math.ceil((double) result.getTotalCount() / pageSize);
                        setNumberOfPages(np);
                    }
                });
    }

    protected ListQueryCollections getQuery() {
        int offset = (currentPage - 1) * pageSize;
        ListQueryCollections query = new ListQueryCollections();
        query.setSortKey(uriForSortKey());
        query.setDesc(descForSortKey());
        query.setLimit(pageSize);
        query.setOffset(offset);
        return query;
    }

    private String uriForSortKey() {
        if (sortKey.startsWith("title-")) {
            return "http://purl.org/dc/elements/1.1/title";
        } else {
            return "http://purl.org/dc/terms/created";
        }
    }

    private boolean descForSortKey() {
        return !sortKey.endsWith("-asc"); // default is descending
    }

    protected void addItem(ShowItemEvent event, CollectionBean item) {
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
        event.setDate(item.getCreationDate());
    }

    private void setViewSize(String sizeType) {
        MMDB.setSessionPreference(getViewSizeTypePreference(), sizeType);
        // unbind the existing presenter if any
        if (viewTypePresenter != null) {
            viewTypePresenter.unbind();
        }
        DynamicListCollectionView listView = new DynamicListCollectionView(
                service, eventBus);
        display.changeSizeNumbers();
        display.setSizeType(sizeType);
        if (sizeType.equals(DynamicTableView.PAGE_SIZE_X2)) {
            pageSize = DynamicListView.PAGE_SIZE_X2;
        } else if (sizeType.equals(DynamicTableView.PAGE_SIZE_X4)) {
            pageSize = DynamicListView.PAGE_SIZE_X4;
        } else {
            pageSize = DynamicListView.DEFAULT_PAGE_SIZE;
        }
        DynamicListCollectionPresenter listPresenter =
                new DynamicListCollectionPresenter(listView, service, eventBus);
        listPresenter.bind();
        viewTypePresenter = listPresenter;
        display.setContentView(listView);
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
        return true;
    }

    protected String getPageKey() {
        return this.getClass().getName();
    }

    protected void setPage(int page) {
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

        for (HasValueChangeHandlers<String> handler : display.getSizeListBox()) {
            handler.addValueChangeHandler(new ValueChangeHandler<String>() {

                @Override
                public void onValueChange(ValueChangeEvent<String> event) {
                    GWT.log("View page size box clicked " + event.getValue());
                    changeViewSize(event.getValue());
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

    /**
     * Change the view size. Adjust the page according to the current page on
     * the previous view type.
     *
     * @param viewType
     * @param sizeType
     */
    protected void changeViewSize(String sizeType) {
        int oldPage = currentPage;
        int oldPageSize = pageSize;
        setViewSize(sizeType);
        setPage(computeNewPage(oldPage, oldPageSize));
    }

    protected int computeNewPage(int oldPage, int oldPageSize) {
        return (((oldPage - 1) * oldPageSize) / pageSize) + 1;
    }

    @Override
    public void unbind() {
        super.unbind();
        if (viewTypePresenter != null) {
            viewTypePresenter.unbind();
        }
    }

    protected String getViewTypePreference() {
        return "collectionsTableViewType";
    }

    protected String getViewSizeTypePreference() {
        return "collectionsTableViewSizeType";
    }

    protected String getViewSortPreference() {
        return "collectionsTableViewSort";
    }
}
