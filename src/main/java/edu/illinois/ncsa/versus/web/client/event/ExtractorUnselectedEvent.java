package edu.illinois.ncsa.versus.web.client.event;

import com.google.gwt.event.shared.GwtEvent;

import edu.illinois.ncsa.versus.web.shared.ComponentMetadata;

public class ExtractorUnselectedEvent extends GwtEvent<ExtractorUnselectedHandler> {
	public static Type<ExtractorUnselectedHandler> TYPE = new Type<ExtractorUnselectedHandler>();
	private final ComponentMetadata extractorMetadata;

	public ExtractorUnselectedEvent(ComponentMetadata measureMetadata) {
		this.extractorMetadata = measureMetadata;
	}

	@Override
	public Type<ExtractorUnselectedHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(ExtractorUnselectedHandler handler) {
		handler.onExtractorUnselected(this);
	}

	public ComponentMetadata getMeasureMetadata() {
		return extractorMetadata;
	}
}
