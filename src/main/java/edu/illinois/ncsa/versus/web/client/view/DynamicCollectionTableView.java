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
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import edu.illinois.ncsa.mmdb.web.client.ui.LabeledListBox;
import edu.illinois.ncsa.mmdb.web.client.ui.PagingWidget;
import edu.illinois.ncsa.versus.web.client.presenter.CollectionsTablePresenter;

/**
 *
 * @author antoinev
 */
public class DynamicCollectionTableView extends Composite
        implements CollectionsTablePresenter.Display {

    public static final String PAGE_SIZE_X1 = "default";

    public static final String PAGE_SIZE_X2 = "two";

    public static final String PAGE_SIZE_X4 = "four";

    private final FlowPanel mainPanel;

    private final HorizontalPanel topPagingPanel;

    private final VerticalPanel middlePanel;

    private final HorizontalPanel bottomPagingPanel;

    private final PagingWidget pagingWidgetTop;

    private final PagingWidget pagingWidgetBottom;

    private final LabeledListBox sortOptionsTop;

    private final LabeledListBox sizeOptionsTop;

    private String sortKey;

    private String sizeType;

    private final LabeledListBox sortOptionsBottom;

    private final LabeledListBox sizeOptionsBottom;

    public DynamicCollectionTableView() {
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
        pagingWidgetTop = new PagingWidget();
        topPagingPanel.add(pagingWidgetTop);
        topPagingPanel.setCellWidth(pagingWidgetTop, "50%");
        sortOptionsTop = createSortOptions();
        topPagingPanel.add(sortOptionsTop);
        sizeOptionsTop = createSizeOptions();
        topPagingPanel.add(sizeOptionsTop);
        pagingWidgetBottom = new PagingWidget();
        bottomPagingPanel.add(pagingWidgetBottom);
        bottomPagingPanel.setCellWidth(pagingWidgetBottom, "50%");
        sortOptionsBottom = createSortOptions();
        bottomPagingPanel.add(sortOptionsBottom);
        sizeOptionsBottom = createSizeOptions();
        bottomPagingPanel.add(sizeOptionsBottom);
    }

    private LabeledListBox createSortOptions() {
        LabeledListBox sortOptions = new LabeledListBox("Sort by: ");
        sortOptions.addStyleName("pagingLabel");
        sortOptions.addItem("Date: newest first", "date-desc");
        sortOptions.addItem("Date: oldest first", "date-asc");
        sortOptions.addItem("Title: A-Z", "title-asc");
        sortOptions.addItem("Title: Z-A", "title-desc");
        sortOptions.setSelected(sortKey);
        return sortOptions;
    }

    private LabeledListBox createSizeOptions() {
        LabeledListBox sizeOptions = new LabeledListBox("Page Size:");
        sizeOptions.addStyleName("pagingLabel");
        sizeOptions.addItem("5", PAGE_SIZE_X1);
        sizeOptions.addItem("10", PAGE_SIZE_X2);
        sizeOptions.addItem("20", PAGE_SIZE_X4);
        sizeOptions.setSelected(sizeType);
        return sizeOptions;
    }

    @Override
    public Widget asWidget() {
        return this;
    }

    @Override
    public void setCurrentPage(int num) {
        pagingWidgetTop.setPage(num);
        pagingWidgetBottom.setPage(num);
    }

    @Override
    public void setTotalNumPages(int num) {
        pagingWidgetTop.setNumberOfPages(num);
        pagingWidgetBottom.setNumberOfPages(num);
    }

    @Override
    public void addItem(String id, int position) {
    }

    @Override
    public void removeAllRows() {
    }

    @Override
    public void changeSizeNumbers() {

        sizeOptionsTop.removeItem(PAGE_SIZE_X4);
        sizeOptionsTop.removeItem(PAGE_SIZE_X2);
        sizeOptionsTop.removeItem(PAGE_SIZE_X1);
        sizeOptionsTop.addItem("5", PAGE_SIZE_X1);
        sizeOptionsTop.addItem("10", PAGE_SIZE_X2);
        sizeOptionsTop.addItem("20", PAGE_SIZE_X4);
        sizeOptionsBottom.removeItem(PAGE_SIZE_X4);
        sizeOptionsBottom.removeItem(PAGE_SIZE_X2);
        sizeOptionsBottom.removeItem(PAGE_SIZE_X1);
        sizeOptionsBottom.addItem("5", PAGE_SIZE_X1);
        sizeOptionsBottom.addItem("10", PAGE_SIZE_X2);
        sizeOptionsBottom.addItem("20", PAGE_SIZE_X4);
    }

    @Override
    public Set<HasValueChangeHandlers<String>> getSortListBox() {
        Set<HasValueChangeHandlers<String>> set = new HashSet<HasValueChangeHandlers<String>>();
        set.add(sortOptionsTop);
        set.add(sortOptionsBottom);
        return set;
    }

    @Override
    public Set<HasValueChangeHandlers<String>> getSizeListBox() {
        Set<HasValueChangeHandlers<String>> set = new HashSet<HasValueChangeHandlers<String>>();
        set.add(sizeOptionsTop);
        set.add(sizeOptionsBottom);
        return set;
    }

    @Override
    public Set<HasValueChangeHandlers<Integer>> getPagingWidget() {
        Set<HasValueChangeHandlers<Integer>> set = new HashSet<HasValueChangeHandlers<Integer>>();
        set.add(pagingWidgetTop);
        set.add(pagingWidgetBottom);
        return set;
    }

    @Override
    public void setPage(Integer value) {
        pagingWidgetTop.setPage(value, false);
        pagingWidgetBottom.setPage(value, false);
    }

    @Override
    public void setOrder(String order) {
        sortOptionsTop.setSelected(order);
        sortOptionsBottom.setSelected(order);
    }

    @Override
    public void setContentView(Widget contentView) {
        middlePanel.clear();
        middlePanel.add(contentView);

    }

    @Override
    public void setSizeType(String sizeType) {
        sizeOptionsTop.setSelected(sizeType);
        sizeOptionsBottom.setSelected(sizeType);
    }
}
