/**
 *
 */
package edu.illinois.ncsa.versus.web.client.presenter;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;
import edu.illinois.ncsa.versus.web.client.RegistryServiceAsync;
import edu.illinois.ncsa.versus.web.client.event.*;
import edu.illinois.ncsa.versus.web.shared.ComponentMetadata;
import java.util.*;

/**
 * @author lmarini
 *
 */
public class SelectMeasurePresenter implements Presenter {

    private final RegistryServiceAsync registryService;

    private final HandlerManager eventBus;

    private final Display display;

    private String selectedMeasureId;

    private Map<ComponentMetadata, Integer> measureToIndex = new HashMap<ComponentMetadata, Integer>();

    private Map<Integer, ComponentMetadata> indexToMeasure = new HashMap<Integer, ComponentMetadata>();

    private List<HandlerRegistration> clickHandlers = new ArrayList<HandlerRegistration>();

    private Set<Integer> activatedByExtractor = new HashSet<Integer>();

    public interface Display {

        int addMeasure(String measure, String category, String helpLink);

        HasClickHandlers getMeasureAnchor(int index);

        void selectMeasure(int index);

        void unselectMeasure();

        void enableMeasures(Set<Integer> measures);

        void disableMeasures();

        Widget asWidget();
    }

    public SelectMeasurePresenter(RegistryServiceAsync registryService,
            HandlerManager eventBus, Display display) {
        this.registryService = registryService;
        this.eventBus = eventBus;
        this.display = display;
    }

    void bind() {
        eventBus.addHandler(AddMeasureEvent.TYPE, new AddMeasureEventHandler() {

            @Override
            public void onAddMeasure(final AddMeasureEvent addMeasureEvent) {
                ComponentMetadata measureMetada = addMeasureEvent.getMeasureMetadata();
                final int index = display.addMeasure(measureMetada.getName(), measureMetada.getCategory(), measureMetada.getHelpLink());
                measureToIndex.put(measureMetada, index);
                indexToMeasure.put(index, measureMetada);
                HandlerRegistration handlerRegistration = display.getMeasureAnchor(index).addClickHandler(new SelectionHandler(index));
                clickHandlers.add(handlerRegistration);
            }
        });

        eventBus.addHandler(ExtractorSelectedEvent.TYPE, new ExtractorSelectedHandler() {

            @Override
            public void onExtractorSelected(ExtractorSelectedEvent event) {
                resetView();
                Set<String> supportedOutputs = event.getExtractorMetadata().getSupportedOutputs();
                for (ComponentMetadata measure : measureToIndex.keySet()) {
                    boolean found = false;
                    for (String supportedOut : supportedOutputs) {
                        if (measure.getSupportedInputs().contains(supportedOut)) {
                            found = true;
                            break;
                        }
                    }
                    if (found) {
                        activatedByExtractor.add(measureToIndex.get(measure));
                    }
                }
                for (Integer index : activatedByExtractor) {
                    addClickHandler(index);
                }
                display.enableMeasures(activatedByExtractor);
            }
        });

        eventBus.addHandler(ExtractorUnselectedEvent.TYPE, new ExtractorUnselectedHandler() {

            @Override
            public void onExtractorUnselected(ExtractorUnselectedEvent event) {
                resetView();
            }
        });
    }

    protected void resetView() {
        GWT.log("Refreshing all handlers");
        if (selectedMeasureId != null) {
            for (ComponentMetadata componentMetadata : measureToIndex.keySet()) {
                if (componentMetadata.getId().equals(selectedMeasureId)) {
                    selectedMeasureId = null;
                    eventBus.fireEvent(new MeasureUnselectedEvent(componentMetadata));
                    display.unselectMeasure();
                    break;
                }
            }
        }
        activatedByExtractor.clear();
        display.disableMeasures();
        for (Integer index : indexToMeasure.keySet()) {
            removeClickHandler(index);
        }
    }

    /**
     *
     * @param index
     */
    protected void addClickHandler(final int index) {
        GWT.log("Adding handler for entry " + index);
        HandlerRegistration oldRegistration = clickHandlers.get(index);
        if (oldRegistration != null) {
            oldRegistration.removeHandler();
            clickHandlers.set(index, null);
        }
        HandlerRegistration handlerRegistration =
                display.getMeasureAnchor(index).addClickHandler(new SelectionHandler(index));
        clickHandlers.set(index, handlerRegistration);
    }

    /**
     *
     * @param index
     */
    protected void removeClickHandler(int index) {
        HandlerRegistration handlerRegistration = clickHandlers.get(index);
        if (handlerRegistration != null) {
            handlerRegistration.removeHandler();
            clickHandlers.set(index, null);
            GWT.log("SelectMeasurePresenter: Removed click handler for entry " + index);
        }
    }

    /*
     * (non-Javadoc) @see
     * edu.illinois.ncsa.versus.web.client.presenter.Presenter#go(com.google.gwt.user.client.ui.HasWidgets)
     */
    @Override
    public void go(HasWidgets container) {
        bind();
        container.add(display.asWidget());
    }

    class SelectionHandler implements ClickHandler {

        private final int index;

        public SelectionHandler(int index) {
            super();
            this.index = index;

        }

        @Override
        public void onClick(ClickEvent event) {
            GWT.log("Clicked on " + this + " / " + index);
            ComponentMetadata componentMetadata = indexToMeasure.get(index);
            if (selectedMeasureId == componentMetadata.getId()) {
                selectedMeasureId = null;
                eventBus.fireEvent(new MeasureUnselectedEvent(componentMetadata));
                display.unselectMeasure();
            } else {
                selectedMeasureId = componentMetadata.getId();
                eventBus.fireEvent(new MeasureSelectedEvent(componentMetadata));
                display.selectMeasure(index);
            }
        }
    }
}
