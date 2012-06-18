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

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasValue;

import edu.illinois.ncsa.mmdb.web.client.MMDB;
import edu.illinois.ncsa.mmdb.web.client.PermissionUtil;
import edu.illinois.ncsa.mmdb.web.client.UserSessionState;
import edu.illinois.ncsa.mmdb.web.client.dispatch.GetDatasetsInCollection;
import edu.illinois.ncsa.mmdb.web.client.dispatch.GetDatasetsInCollectionResult;
import edu.illinois.ncsa.mmdb.web.client.event.ClearDatasetsEvent;
import edu.illinois.ncsa.mmdb.web.client.event.ClearDatasetsHandler;
import edu.illinois.ncsa.mmdb.web.client.event.RefreshEvent;
import edu.illinois.ncsa.mmdb.web.client.event.ShowItemEvent;
import edu.illinois.ncsa.mmdb.web.client.event.ShowItemEventHandler;
import edu.illinois.ncsa.mmdb.web.client.mvp.BasePresenter;
import edu.illinois.ncsa.versus.web.client.event.CollectionCreatedEvent;
import edu.illinois.ncsa.versus.web.client.event.CollectionCreatedHandler;
import edu.illinois.ncsa.versus.web.client.event.CollectionDeletedEvent;
import edu.illinois.ncsa.versus.web.client.event.CollectionDeletedHandler;
import edu.illinois.ncsa.versus.web.client.event.CollectionSelectionCheckboxHandler;
import edu.uiuc.ncsa.cet.bean.rbac.medici.Permission;
import net.customware.gwt.dispatch.client.DispatchAsync;

/**
 *
 * @author antoinev
 */
public class DynamicListCollectionPresenter
        extends BasePresenter<DynamicListCollectionPresenter.Display> {

    /**
     * Map from uri to location in view *
     */
    private final Map<String, Integer> items;

    public interface Display {

        HasValue<Boolean> getSelected(int location);

        int insertItem(final String uri);

        void setTitle(int row, String title, String uri);

        void setDate(int row, Date date);

        void setAuthor(int row, String author);

        void setSize(int row, String size);

        void setDownload(int row, String uri, String name);

        void setDelete(int row, String uri);

        void removeAllRows();
    }

    public DynamicListCollectionPresenter(Display display,
            DispatchAsync dispatch, HandlerManager eventBus) {
        super(display, dispatch, eventBus);
        items = new HashMap<String, Integer>();
    }

    @Override
    public void bind() {
        addHandler(ShowItemEvent.TYPE, new ShowItemEventHandler() {

            @Override
            public void onShowItem(final ShowItemEvent showItemEvent) {
                final int row = addItem(showItemEvent.getId());
                display.setTitle(row, showItemEvent.getTitle(), showItemEvent.getId());
                if (showItemEvent.getAuthor() != null) {
                    display.setAuthor(row, showItemEvent.getAuthor());
                }
                if (showItemEvent.getDate() != null) {
                    display.setDate(row, showItemEvent.getDate());
                }
                if (showItemEvent.getSize() != null) {
                    display.setSize(row, showItemEvent.getSize());
                }
                PermissionUtil rbac = new PermissionUtil(service);
                rbac.doIfAllowed(Permission.DOWNLOAD,
                        new PermissionUtil.PermissionCallback() {

                            @Override
                            public void onAllowed() {
                                display.setDownload(row, showItemEvent.getId(),
                                        showItemEvent.getTitle());
                            }
                        });
                rbac.doIfAllowed(Permission.DELETE_COLLECTION,
                        new PermissionUtil.PermissionCallback() {

                            @Override
                            public void onAllowed() {
                                display.setDelete(row, showItemEvent.getId());
                            }
                        });
            }
        });

        addHandler(ClearDatasetsEvent.TYPE, new ClearDatasetsHandler() {

            @Override
            public void onClearDatasets(ClearDatasetsEvent event) {
                display.removeAllRows();
                items.clear();
            }
        });

        addHandler(CollectionDeletedEvent.TYPE, new CollectionDeletedHandler() {

            @Override
            public void onDeleteCollection(CollectionDeletedEvent event) {
                if (items.containsKey(event.getCollectionUri())) {
                    eventBus.fireEvent(new RefreshEvent());
                }
            }
        });

        addHandler(CollectionCreatedEvent.TYPE, new CollectionCreatedHandler() {

            @Override
            public void onCreateCollection(CollectionCreatedEvent event) {
                eventBus.fireEvent(new RefreshEvent());
            }
        });
    }

    private int addItem(final String id) {
        int location = display.insertItem(id);
        items.put(id, location);
        final HasValue<Boolean> selected = display.getSelected(location);
        selected.addValueChangeHandler(
                new CollectionSelectionCheckboxHandler(id, eventBus, service));

        service.execute(new GetDatasetsInCollection(id),
                new AsyncCallback<GetDatasetsInCollectionResult>() {

                    @Override
                    public void onFailure(Throwable caught) {
                        GWT.log("Cannot get datasets of collection " + id, caught);
                        selected.setValue(false);
                    }

                    @Override
                    public void onSuccess(GetDatasetsInCollectionResult result) {
                        HashSet<String> datasets = result.getDatasets();
                        if (datasets.isEmpty()) {
                            selected.setValue(false);
                        } else {
                            UserSessionState sessionState = MMDB.getSessionState();
                            Set<String> selectedDatasets = sessionState.getSelectedDatasets();
                            for (String dataset : datasets) {
                                if (!selectedDatasets.contains(dataset)) {
                                    selected.setValue(false);
                                    return;
                                }
                            }
                            selected.setValue(true);
                        }
                    }
                });

        return location;
    }
}
