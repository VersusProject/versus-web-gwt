package edu.illinois.ncsa.versus.web.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class SelectExtractorEvent extends GwtEvent<SelectExtractorEventHandler> {
	public static Type<SelectExtractorEventHandler> TYPE = new Type<SelectExtractorEventHandler>();
	private final String id;
	private final String name;

	public SelectExtractorEvent(String id, String name) {
		this.id = id;
		this.name = name;
	}

	@Override
	public Type<SelectExtractorEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(SelectExtractorEventHandler handler) {
		handler.onAddExtractor(this);
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
}
