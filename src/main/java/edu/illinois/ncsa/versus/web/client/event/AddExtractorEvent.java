package edu.illinois.ncsa.versus.web.client.event;

import com.google.gwt.event.shared.GwtEvent;

import edu.illinois.ncsa.versus.web.shared.ComponentMetadata;

public class AddExtractorEvent extends GwtEvent<AddExtractorEventHandler> {
	public static Type<AddExtractorEventHandler> TYPE = new Type<AddExtractorEventHandler>();
	private final ComponentMetadata extractorMetadata;

	public AddExtractorEvent(ComponentMetadata extractorMetadata) {
		this.extractorMetadata = extractorMetadata;
	}

	@Override
	public Type<AddExtractorEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(AddExtractorEventHandler handler) {
		handler.onAddExtractor(this);
	}

	public ComponentMetadata getExtractorMetadata() {
		return extractorMetadata;
	}
}
