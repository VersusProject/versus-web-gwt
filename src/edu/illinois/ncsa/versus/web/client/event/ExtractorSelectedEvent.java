package edu.illinois.ncsa.versus.web.client.event;

import com.google.gwt.event.shared.GwtEvent;

import edu.illinois.ncsa.versus.web.shared.ComponentMetadata;

public class ExtractorSelectedEvent extends GwtEvent<ExtractorSelectedHandler> {
	public static Type<ExtractorSelectedHandler> TYPE = new Type<ExtractorSelectedHandler>();
	private final ComponentMetadata extractorMetadata;

	public ExtractorSelectedEvent(ComponentMetadata measureMetadata) {
		this.extractorMetadata = measureMetadata;
	}

	@Override
	public Type<ExtractorSelectedHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(ExtractorSelectedHandler handler) {
		handler.onExtractorSelected(this);
	}

	public ComponentMetadata getMeasureMetadata() {
		return extractorMetadata;
	}
}
