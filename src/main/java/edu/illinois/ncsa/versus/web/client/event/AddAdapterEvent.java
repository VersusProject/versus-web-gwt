package edu.illinois.ncsa.versus.web.client.event;

import com.google.gwt.event.shared.GwtEvent;

import edu.illinois.ncsa.versus.web.shared.ComponentMetadata;

public class AddAdapterEvent extends GwtEvent<AddAdapterEventHandler> {
	public static Type<AddAdapterEventHandler> TYPE = new Type<AddAdapterEventHandler>();
	private final ComponentMetadata adapterMetadata;

	public AddAdapterEvent(ComponentMetadata adapterMetadata) {
		this.adapterMetadata = adapterMetadata;
	}

	@Override
	public Type<AddAdapterEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(AddAdapterEventHandler handler) {
		handler.onAddAdapter(this);
	}

	public ComponentMetadata getAdapterMetadata() {
		return adapterMetadata;
	}
}
