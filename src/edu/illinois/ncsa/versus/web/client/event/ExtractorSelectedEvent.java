package edu.illinois.ncsa.versus.web.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class ExtractorSelectedEvent extends GwtEvent<ExtractorSelectedHandler> {
	public static Type<ExtractorSelectedHandler> TYPE = new Type<ExtractorSelectedHandler>();
	private final String name;

	public ExtractorSelectedEvent(String name) {
		this.name = name;
	}

	@Override
	public Type<ExtractorSelectedHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(ExtractorSelectedHandler handler) {
		handler.onExtractorSelected(this);
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
}
