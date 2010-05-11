package edu.illinois.ncsa.versus.web.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class AddExtractorEvent extends GwtEvent<AddExtractorEventHandler> {
	public static Type<AddExtractorEventHandler> TYPE = new Type<AddExtractorEventHandler>();
	private final String name;

	public AddExtractorEvent(String name) {
		this.name = name;
	}

	@Override
	public Type<AddExtractorEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(AddExtractorEventHandler handler) {
		handler.onAddExtractor(this);
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
}
