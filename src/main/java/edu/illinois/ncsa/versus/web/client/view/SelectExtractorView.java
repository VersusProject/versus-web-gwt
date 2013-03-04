/**
 *
 */
package edu.illinois.ncsa.versus.web.client.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.*;

import edu.illinois.ncsa.versus.web.client.InfoPopup;
import edu.illinois.ncsa.versus.web.client.presenter.SelectExtractorPresenter.Display;

/**
 * @author lmarini
 *
 */
public class SelectExtractorView extends Composite implements Display {

    private final FlowPanel mainPanel;

    private final VerticalPanel listCategoriesPanel;

    private final List<Anchor> extractorAnchors = new ArrayList<Anchor>();

    private List<HandlerRegistration> clickHandlers = new ArrayList<HandlerRegistration>();

    private List<HandlerRegistration> mouseOverHandlers = new ArrayList<HandlerRegistration>();

    private List<HandlerRegistration> mouseOutHandlers = new ArrayList<HandlerRegistration>();

    private HashMap<String, CategoriesWidget> categories = new HashMap<String, CategoriesWidget>();

    public SelectExtractorView() {
        mainPanel = new FlowPanel();
        mainPanel.addStyleName("selectExtractorPanel");
        Label titleLabel = new Label("Characterize (extractor)");
        titleLabel.addStyleName("titleLabel");
        mainPanel.add(titleLabel);
        listCategoriesPanel = new VerticalPanel();
        listCategoriesPanel.setSpacing(10);
        mainPanel.add(listCategoriesPanel);
        initWidget(mainPanel);
    }

    @Override
    public Widget asWidget() {
        return this;
    }

    @Override
    public int addExtractor(String extractor, String category, String helpLink) {
        final Anchor extractorAnchor = new Anchor(extractor);
        FlowPanel extractorPanel = new FlowPanel();
        extractorPanel.add(extractorAnchor);
        extractorAnchors.add(extractorAnchor);

        if (helpLink != null && !helpLink.isEmpty()) {
            extractorPanel.add(new InlineHTML("&nbsp;&nbsp;"));
            Anchor helpAnchor = new Anchor("<img src=\"images/help-browser.png\" alt=\"help\">", true, helpLink, "_blank");
            extractorPanel.add(helpAnchor);
        }

        VerticalPanel categoryPanel;
        if (categories.containsKey(category)) {
            categoryPanel = categories.get(category).getVerticalPanel();
        } else {
            categoryPanel = new VerticalPanel();
            categoryPanel.addStyleName("selectExtractorPanel");
            DisclosurePanel disclosurePanel = new DisclosurePanel(category);
            disclosurePanel.add(categoryPanel);
            listCategoriesPanel.add(disclosurePanel);
            categories.put(category, new CategoriesWidget(disclosurePanel, categoryPanel));
        }
        categoryPanel.add(extractorPanel);

        int index = extractorAnchors.indexOf(extractorAnchor);
        addStyleAndHandlers(index);
        removeStyleAndHandlers(index);
        extractorAnchor.addStyleName("hideExtractor");
        enableDisableCategories();
        return index;
    }

    private void addStyleAndHandlers(int index) {
        GWT.log("SelectedExtractorView: Enabling extractor " + index);
        final Anchor extractorAnchor = extractorAnchors.get(index);
        extractorAnchor.addStyleName("listAnchor");
        final InfoPopup popup = new InfoPopup(extractorAnchor.getText());
        HandlerRegistration addClickHandler = extractorAnchor.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                clearSelection();
                extractorAnchor.addStyleName("selectedLabel");
            }
        });
        if (index > clickHandlers.size() - 1) {
            clickHandlers.add(addClickHandler);
        } else {
            if (clickHandlers.get(index) != null) {
                clickHandlers.get(index).removeHandler();
            }
            clickHandlers.set(index, addClickHandler);
        }
        HandlerRegistration addMouseOverHandler = extractorAnchor.addMouseOverHandler(new MouseOverHandler() {

            @Override
            public void onMouseOver(final MouseOverEvent event) {
                GWT.log("ON MOUSE OVER");
                extractorAnchor.addStyleName("highlightLabel");
                popup.setPopupPositionAndShow(new PopupPanel.PositionCallback() {

                    @Override
                    public void setPosition(int offsetWidth, int offsetHeight) {
                        popup.setPopupPosition(event.getClientX() + 50, event.getClientY());
                    }
                });
            }
        });
        if (index > mouseOverHandlers.size() - 1) {
            mouseOverHandlers.add(addMouseOverHandler);
        } else {
            if (mouseOutHandlers.get(index) != null) {
                mouseOverHandlers.get(index).removeHandler();
            }
            mouseOverHandlers.set(index, addMouseOverHandler);
        }
        HandlerRegistration addMouseOutHandler = extractorAnchor.addMouseOutHandler(new MouseOutHandler() {

            @Override
            public void onMouseOut(MouseOutEvent event) {
                GWT.log("ON MOUSE OUT");
                extractorAnchor.removeStyleName("highlightLabel");
                popup.hide();
            }
        });
        if (index > mouseOutHandlers.size() - 1) {
            mouseOutHandlers.add(addMouseOutHandler);
        } else {
            if (mouseOutHandlers.get(index) != null) {
                mouseOutHandlers.get(index).removeHandler();
            }
            mouseOutHandlers.set(index, addMouseOutHandler);
        }
    }

    private void removeStyleAndHandlers(int index) {
        if (clickHandlers.get(index) != null) {
            clickHandlers.get(index).removeHandler();
            clickHandlers.set(index, null);
            GWT.log("SelectExtractorView: Removed click handler from " + index);
        }
        if (mouseOverHandlers.get(index) != null) {
            mouseOverHandlers.get(index).removeHandler();
            mouseOverHandlers.set(index, null);
            GWT.log("SelectExtractorView: Removed mouse over from " + index);
        }
        if (mouseOutHandlers.get(index) != null) {
            mouseOutHandlers.get(index).removeHandler();
            mouseOutHandlers.set(index, null);
            GWT.log("SelectExtractorView: Removed mouse out from " + index);
        }
    }

    private void clearSelection() {
        for (Anchor anchor : extractorAnchors) {
            anchor.removeStyleName("selectedLabel");
        }
    }

    @Override
    public HasClickHandlers getExtractorAnchor(int index) {
        return extractorAnchors.get(index);
    }

    @Override
    public void selectExtractor(int index) {
        clearSelection();
        extractorAnchors.get(index).addStyleName("selectedLabel");
    }

    @Override
    public void unselectExtractor() {
        clearSelection();
    }

    @Override
    public void enableExtractors(Set<Integer> extractors) {
        for (Integer index : extractors) {
            addStyleAndHandlers(index);
            extractorAnchors.get(index).removeStyleName("hideExtractor");
        }
        enableDisableCategories();
    }

    @Override
    public void disableExtractors() {
        for (int i = 0; i < extractorAnchors.size(); i++) {
            extractorAnchors.get(i).addStyleName("hideExtractor");
            removeStyleAndHandlers(i);
        }

        for (CategoriesWidget widgets : categories.values()) {
            setCategoryEnabled(widgets, false);
        }
    }
    private void enableDisableCategories() {
        for (CategoriesWidget widgets : categories.values()) {
            enableDisableCategory(widgets);
        }
    }
    
    private void enableDisableCategory(CategoriesWidget widget) {
        Boolean allHidden = true;
        VerticalPanel panel = widget.getVerticalPanel();
        for (int i = 0; i < panel.getWidgetCount(); i++) {
            FlowPanel flowPanel = (FlowPanel) panel.getWidget(i);
            Anchor measure = (Anchor) flowPanel.getWidget(0);
            if (!measure.getStyleName().contains("hideExtractor")) {
                allHidden = false;
                break;
            }
        }
        setCategoryEnabled(widget, !allHidden);
    }

    private void setCategoryEnabled(CategoriesWidget widget, boolean enabled) {
        if (enabled) {
            widget.getDisclosurePanel().getHeader().removeStyleName("applyOpacityToCategory");
        } else {
            widget.getDisclosurePanel().getHeader().addStyleName("applyOpacityToCategory");
        }
    }
}
