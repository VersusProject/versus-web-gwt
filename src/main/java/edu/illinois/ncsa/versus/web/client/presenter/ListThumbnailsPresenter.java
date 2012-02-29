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

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;
import edu.illinois.ncsa.mmdb.web.client.event.DatasetSelectedEvent;
import edu.illinois.ncsa.mmdb.web.client.event.DatasetSelectedHandler;
import edu.illinois.ncsa.mmdb.web.client.event.DatasetUnselectedEvent;
import edu.illinois.ncsa.mmdb.web.client.event.DatasetUnselectedHandler;

/**
 *
 * @author antoinev
 */
public class ListThumbnailsPresenter implements Presenter {

    private final HandlerManager eventBus;
    private final ListThumbnailsPresenter.Display display;

    public interface Display {

        void addThumbnail(String uri);

        void removeThumbnail(String uri);

        Widget asWidget();
    }

    public ListThumbnailsPresenter(HandlerManager eventBus, ListThumbnailsPresenter.Display display) {
        this.eventBus = eventBus;
        this.display = display;
        addHandlers();
    }

    private void addHandlers() {

        eventBus.addHandler(DatasetSelectedEvent.TYPE, new DatasetSelectedHandler() {

            @Override
            public void onDatasetSelected(DatasetSelectedEvent event) {
                display.addThumbnail(event.getUri());
            }
        });

        eventBus.addHandler(DatasetUnselectedEvent.TYPE, new DatasetUnselectedHandler() {

            @Override
            public void onDatasetUnselected(DatasetUnselectedEvent event) {
                display.removeThumbnail(event.getUri());
            }
        });
    }
    
    public Widget getWidget() {
        return display.asWidget();
    }

    @Override
    public void go(HasWidgets container) {
        container.add(display.asWidget());
    }
}
