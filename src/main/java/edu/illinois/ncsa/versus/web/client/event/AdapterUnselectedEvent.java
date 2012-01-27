package edu.illinois.ncsa.versus.web.client.event;

import com.google.gwt.event.shared.GwtEvent;

import edu.illinois.ncsa.versus.web.shared.ComponentMetadata;

public class AdapterUnselectedEvent extends GwtEvent<AdapterUnselectedHandler> {
	public static Type<AdapterUnselectedHandler> TYPE = new Type<AdapterUnselectedHandler>();
	private final ComponentMetadata adapterMetadata;

	public AdapterUnselectedEvent(ComponentMetadata adapterMetadata) {
		this.adapterMetadata = adapterMetadata;
	}

	@Override
	public Type<AdapterUnselectedHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(AdapterUnselectedHandler handler) {
		handler.onAdapterUnselected(this);
	}

	public ComponentMetadata getAdapterMetadata() {
		return adapterMetadata;
	}
}
