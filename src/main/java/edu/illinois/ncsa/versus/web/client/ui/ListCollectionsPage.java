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
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.illinois.ncsa.mmdb.web.client.MMDB;
import edu.illinois.ncsa.mmdb.web.client.PermissionUtil;
import edu.illinois.ncsa.mmdb.web.client.dispatch.AddCollection;
import edu.illinois.ncsa.mmdb.web.client.dispatch.AddCollectionResult;
import edu.illinois.ncsa.mmdb.web.client.ui.ConfirmDialog;
import edu.illinois.ncsa.mmdb.web.client.ui.Page;
import edu.illinois.ncsa.mmdb.web.client.ui.WatermarkTextBox;
import edu.illinois.ncsa.versus.web.client.event.CollectionCreatedEvent;
import edu.illinois.ncsa.versus.web.client.presenter.CollectionsTablePresenter;
import edu.illinois.ncsa.versus.web.client.view.DynamicCollectionTableView;
import edu.uiuc.ncsa.cet.bean.CollectionBean;
import edu.uiuc.ncsa.cet.bean.rbac.medici.Permission;
import net.customware.gwt.dispatch.client.DispatchAsync;

/**
 *
 * @author antoinev
 */
public class ListCollectionsPage extends Page {
    
    public ListCollectionsPage(DispatchAsync dispatch, HandlerManager eventBus) {
        super("Collections", dispatch, eventBus);
        
        HorizontalPanel rightHeader = new HorizontalPanel();
        pageTitle.addEast(rightHeader);

        // add collection widget
        FlowPanel addCollectionPanel = createAddCollectionWidget();
        mainLayoutPanel.add(addCollectionPanel);
        
        DynamicCollectionTableView dynamicTableView = new DynamicCollectionTableView();
        final CollectionsTablePresenter dynamicTablePresenter =
                new CollectionsTablePresenter(dispatch, eventBus, dynamicTableView);
        dynamicTablePresenter.bind();
        
        VerticalPanel vp = new VerticalPanel() {
            
            @Override
            protected void onDetach() {
                dynamicTablePresenter.unbind();
            }
        };
        vp.add(dynamicTableView.asWidget());
        vp.addStyleName("tableCenter");
        mainLayoutPanel.add(vp);
    }
    
    private FlowPanel createAddCollectionWidget() {
        final FlowPanel addCollectionPanel = new FlowPanel();
        PermissionUtil rbac = new PermissionUtil(dispatchAsync);
        rbac.doIfAllowed(Permission.ADD_COLLECTION,
                new PermissionUtil.PermissionCallback() {
                    
                    @Override
                    public void onAllowed() {
                        Label createLabel = new Label("Create new collection: ");
                        createLabel.addStyleName("inline");
                        addCollectionPanel.add(createLabel);
                        final WatermarkTextBox addCollectionBox = new WatermarkTextBox("",
                                "Collection name");
                        addCollectionBox.addStyleName("inline");
                        addCollectionPanel.add(addCollectionBox);
                        Button addButton = new Button("Add", new ClickHandler() {
                            
                            @Override
                            public void onClick(ClickEvent arg0) {
                                createNewCollection(addCollectionBox.getText());
                            }
                        });
                        addButton.addStyleName("inline");
                        addCollectionPanel.add(addButton);
                        SimplePanel clearBoth = new SimplePanel();
                        clearBoth.addStyleName("clearBoth");
                        addCollectionPanel.add(clearBoth);
                    }
                });
        return addCollectionPanel;
    }

    /**
     * Create new collection on the server.
     *
     * @param text name of collection
     */
    private void createNewCollection(String text) {
        
        CollectionBean collection = new CollectionBean();
        collection.setTitle(text);
        
        dispatchAsync.execute(new AddCollection(collection, MMDB.getUsername()),
                new AsyncCallback<AddCollectionResult>() {
                    
                    @Override
                    public void onFailure(Throwable arg0) {
                        GWT.log("Failed creating new collection", arg0);
                        ConfirmDialog d = new ConfirmDialog("Error", 
                                "Cannot create collection.", false);
                        d.getOkText().setText("OK");
                    }
                    
                    @Override
                    public void onSuccess(AddCollectionResult arg0) {
                        eventBus.fireEvent(new CollectionCreatedEvent());
                    }
                });
    }
    
    @Override
    public void layout() {
    }
}
