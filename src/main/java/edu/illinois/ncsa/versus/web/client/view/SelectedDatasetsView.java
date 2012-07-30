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
package edu.illinois.ncsa.versus.web.client.view;

import java.util.HashSet;
import java.util.Set;

import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import edu.illinois.ncsa.mmdb.web.client.ui.LabeledListBox;
import edu.illinois.ncsa.versus.web.client.presenter.SelectedDatasetsPresenter;

/**
 *
 * @author antoinev
 */
public class SelectedDatasetsView extends Composite implements
        SelectedDatasetsPresenter.Display {

    public static final String LIST_VIEW_TYPE = "list";
    public static final String GRID_VIEW_TYPE = "grid";
    private final FlowPanel mainPanel;
    private final HorizontalPanel topPagingPanel;
    private final VerticalPanel middlePanel;
    private final HorizontalPanel bottomPagingPanel;
    private final LabeledListBox viewOptionsTop;
    private String viewType;
    private final LabeledListBox viewOptionsBottom;

    public SelectedDatasetsView() {
        mainPanel = new FlowPanel();
        mainPanel.addStyleName("dynamicTable");
        initWidget(mainPanel);
        topPagingPanel = new HorizontalPanel();
        topPagingPanel.addStyleName("dynamicTableHeader");
        topPagingPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
        middlePanel = new VerticalPanel();
        middlePanel.addStyleName("content");
        bottomPagingPanel = new HorizontalPanel();
        bottomPagingPanel.addStyleName("dynamicTableHeader");
        bottomPagingPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
        mainPanel.add(topPagingPanel);
        mainPanel.add(middlePanel);
        mainPanel.add(bottomPagingPanel);
        topPagingPanel.add(new Hyperlink("Select the input datasets.", "listDatasets"));
        viewOptionsTop = createViewOptions();
        topPagingPanel.add(viewOptionsTop);
        topPagingPanel.setCellHorizontalAlignment(viewOptionsTop, 
                HasHorizontalAlignment.ALIGN_RIGHT);
        bottomPagingPanel.add(new Hyperlink("Select the input datasets.", "listDatasets"));
        viewOptionsBottom = createViewOptions();
        bottomPagingPanel.add(viewOptionsBottom);
        bottomPagingPanel.setCellHorizontalAlignment(viewOptionsBottom, 
                HasHorizontalAlignment.ALIGN_RIGHT);
    }

    private LabeledListBox createViewOptions() {
        LabeledListBox viewOptions = new LabeledListBox("View:");
        viewOptions.addStyleName("pagingLabel");
        viewOptions.addItem("List", LIST_VIEW_TYPE);
        viewOptions.addItem("Grid", GRID_VIEW_TYPE);
        viewOptions.setSelected(viewType);
        return viewOptions;
    }

    @Override
    public Widget asWidget() {
        return this;
    }

    @Override
    public Set<HasValueChangeHandlers<String>> getViewListBox() {
        Set<HasValueChangeHandlers<String>> set = new HashSet<HasValueChangeHandlers<String>>();
        set.add(viewOptionsTop);
        set.add(viewOptionsBottom);
        return set;
    }

    @Override
    public void setContentView(Widget contentView) {
        middlePanel.clear();
        middlePanel.add(contentView);

    }

    @Override
    public void setViewType(String viewType) {
        viewOptionsTop.setSelected(viewType);
        viewOptionsBottom.setSelected(viewType);
    }
}
