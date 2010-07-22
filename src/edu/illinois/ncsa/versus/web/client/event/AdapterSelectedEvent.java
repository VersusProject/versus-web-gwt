package edu.illinois.ncsa.versus.web.client.event;

import com.google.gwt.event.shared.GwtEvent;

import edu.illinois.ncsa.versus.web.shared.ComponentMetadata;

public class AdapterSelectedEvent extends GwtEvent<AdapterSelectedHandler> {
	public static Type<AdapterSelectedHandler> TYPE = new Type<AdapterSelectedHandler>();
	private final ComponentMetadata adapterMetadata;

	public AdapterSelectedEvent(ComponentMetadata adapterMetadata) {
		this.adapterMetadata = adapterMetadata;
	}

	@Override
	public Type<AdapterSelectedHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(AdapterSelectedHandler handler) {
		handler.onAdapterSelected(this);
	}

	public ComponentMetadata getAdapterMetadata() {
		return adapterMetadata;
	}
}
