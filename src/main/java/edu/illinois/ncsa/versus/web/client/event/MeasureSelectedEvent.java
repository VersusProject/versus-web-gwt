package edu.illinois.ncsa.versus.web.client.event;

import com.google.gwt.event.shared.GwtEvent;

import edu.illinois.ncsa.versus.web.shared.ComponentMetadata;

public class MeasureSelectedEvent extends GwtEvent<MeasureSelectedHandler> {
	public static Type<MeasureSelectedHandler> TYPE = new Type<MeasureSelectedHandler>();
	private final ComponentMetadata measureMetadata;

	public MeasureSelectedEvent(ComponentMetadata measureMetadata) {
		this.measureMetadata = measureMetadata;
	}

	@Override
	public Type<MeasureSelectedHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(MeasureSelectedHandler handler) {
		handler.onMeasureSelected(this);
	}

	public ComponentMetadata getMeasureMetadata() {
		return measureMetadata;
	}
}
