/**
 *
 */
package edu.illinois.ncsa.versus.web.client.view;

import java.util.HashMap;
import java.util.Map;

import net.customware.gwt.dispatch.client.DispatchAsync;

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ScrollPanel;

import edu.illinois.ncsa.mmdb.web.client.dispatch.GetPreviews;
import edu.illinois.ncsa.mmdb.web.client.ui.PreviewWidget;
import edu.illinois.ncsa.versus.web.client.presenter.ListThumbnailsPresenter.Display;

/**
 * @author lmarini
 *
 */
public class ListThumbnailsView extends Composite implements Display {

    private HorizontalPanel mainPanel;
    private Map<String, PreviewWidget> previews = new HashMap<String, PreviewWidget>(16);
    private DispatchAsync service;

    public ListThumbnailsView(DispatchAsync dispatchAsync, HandlerManager eventBus, final PickupDragController dragController) {
        this.service = dispatchAsync;
        mainPanel = new HorizontalPanel();
        ScrollPanel scrollPanel = new ScrollPanel(mainPanel);
        scrollPanel.addStyleName("listThumbnailsPanel");
        initWidget(scrollPanel);
    }

    @Override
    public void addThumbnail(String uri) {
        if (!previews.containsKey(uri)) {
            PreviewWidget previewWidget = new PreviewWidget(uri,
                    GetPreviews.SMALL, "dataset?id=" + uri, service);
            previewWidget.addStyleName("thumbnail");
            mainPanel.add(previewWidget);
            previews.put(uri, previewWidget);
        } else {
            GWT.log("Ignoring already added dataset: " + uri);
        }
    }

    @Override
    public void removeThumbnail(String uri) {
        if (previews.containsKey(uri)) {
            PreviewWidget previewWidget = previews.get(uri);
            mainPanel.remove(previewWidget);
            previews.remove(uri);
        } else {
            GWT.log("Ignoring missing dataset: " + uri);
        }
    }
}
