package edu.illinois.ncsa.versus.web.client.presenter;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;

import edu.illinois.ncsa.versus.web.client.RegistryServiceAsync;
import edu.illinois.ncsa.versus.web.client.event.AdapterSelectedEvent;
import edu.illinois.ncsa.versus.web.client.event.AdapterUnselectedEvent;
import edu.illinois.ncsa.versus.web.client.event.AddAdapterEvent;
import edu.illinois.ncsa.versus.web.client.event.AddAdapterEventHandler;
import edu.illinois.ncsa.versus.web.shared.ComponentMetadata;

/**
 *
 * @author lmarini
 *
 */
public class SelectAdapterPresenter implements Presenter {

    private final RegistryServiceAsync registryService;
    private final HandlerManager eventBus;
    private final Display display;
    private String selectedAdapterId;

    public interface Display {

        int addAdapter(String adapter, String category, String helpLink);

        HasClickHandlers getAdapterAnchor(int index);

        void selectAdapter(int index);

        void unselectAdapter();

        Widget asWidget();
    }

    public SelectAdapterPresenter(RegistryServiceAsync registryService,
            HandlerManager eventBus, Display display) {
        this.registryService = registryService;
        this.eventBus = eventBus;
        this.display = display;
    }

    @Override
    public void go(HasWidgets container) {
        bind();
        container.add(display.asWidget());
    }

    @SuppressWarnings("deprecation")
    private void bind() {
        eventBus.addHandler(AddAdapterEvent.TYPE, new AddAdapterEventHandler() {

            @Override
            public void onAddAdapter(final AddAdapterEvent addAdapterEvent) {
                ComponentMetadata adapterMetadata = addAdapterEvent.getAdapterMetadata();
                final int index = display.addAdapter(adapterMetadata.getName(),
                        adapterMetadata.getCategory(),
                        adapterMetadata.getHelpLink());
                display.getAdapterAnchor(index).addClickHandler(
                        new ClickHandler() {

                            @Override
                            public void onClick(ClickEvent event) {
                                ComponentMetadata adapterMetadata = addAdapterEvent.getAdapterMetadata();
                                if (selectedAdapterId == adapterMetadata.getId()) {
                                    selectedAdapterId = null;
                                    eventBus.fireEvent(new AdapterUnselectedEvent(
                                            adapterMetadata));
                                    display.unselectAdapter();
                                } else {
                                    selectedAdapterId = adapterMetadata.getId();
                                    eventBus.fireEvent(new AdapterSelectedEvent(
                                            adapterMetadata));
                                    display.selectAdapter(index);
                                }
                            }
                        });
            }
        });
    }
}
