package edu.illinois.ncsa.versus.web.client.event;

import com.google.gwt.event.shared.GwtEvent;

import edu.illinois.ncsa.versus.web.shared.ComponentMetadata;

public class MeasureUnselectedEvent extends GwtEvent<MeasureUnselectedHandler> {
	public static Type<MeasureUnselectedHandler> TYPE = new Type<MeasureUnselectedHandler>();
	private final ComponentMetadata measureMetadata;

	public MeasureUnselectedEvent(ComponentMetadata measureMetadata) {
		this.measureMetadata = measureMetadata;
	}

	@Override
	public Type<MeasureUnselectedHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(MeasureUnselectedHandler handler) {
		handler.onMeasureUnselected(this);
	}

	public ComponentMetadata getMeasureMetadata() {
		return measureMetadata;
	}
}
