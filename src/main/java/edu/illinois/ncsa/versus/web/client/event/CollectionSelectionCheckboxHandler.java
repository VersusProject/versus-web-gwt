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
package edu.illinois.ncsa.versus.web.client.event;

import java.util.HashSet;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.illinois.ncsa.mmdb.web.client.dispatch.GetDatasetsInCollection;
import edu.illinois.ncsa.mmdb.web.client.dispatch.GetDatasetsInCollectionResult;
import edu.illinois.ncsa.mmdb.web.client.event.DatasetSelectedEvent;
import edu.illinois.ncsa.mmdb.web.client.event.DatasetUnselectedEvent;
import net.customware.gwt.dispatch.client.DispatchAsync;

/**
 *
 * @author antoinev
 */
public class CollectionSelectionCheckboxHandler implements ValueChangeHandler<Boolean> {

    private final String collectionUri;

    private final HandlerManager eventBus;

    private final DispatchAsync dispatchAsync;

    public CollectionSelectionCheckboxHandler(String collectionUri,
            HandlerManager eventBus, DispatchAsync dispatchAsync) {
        this.collectionUri = collectionUri;
        this.eventBus = eventBus;
        this.dispatchAsync = dispatchAsync;
    }

    @Override
    public void onValueChange(final ValueChangeEvent<Boolean> event) {
        dispatchAsync.execute(new GetDatasetsInCollection(collectionUri),
                new AsyncCallback<GetDatasetsInCollectionResult>() {

                    @Override
                    public void onFailure(Throwable caught) {
                        GWT.log("Cannot get datasets of collection " + collectionUri, caught);
                    }

                    @Override
                    public void onSuccess(GetDatasetsInCollectionResult result) {
                        HashSet<String> datasets = result.getDatasets();
                        if (event.getValue()) {
                            for (String dataset : datasets) {
                                DatasetSelectedEvent dse = new DatasetSelectedEvent();
                                dse.setUri(dataset);
                                eventBus.fireEvent(dse);
                            }
                        } else {
                            for (String dataset : datasets) {
                                DatasetUnselectedEvent due = new DatasetUnselectedEvent();
                                due.setUri(dataset);
                                eventBus.fireEvent(due);
                            }
                        }
                    }
                });
    }
}
