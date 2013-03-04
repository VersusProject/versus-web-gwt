package edu.illinois.ncsa.versus.web.client.event;

import com.google.gwt.event.shared.GwtEvent;
import edu.illinois.ncsa.versus.web.shared.ComponentMetadata;

public class AddMeasureEvent extends GwtEvent<AddMeasureEventHandler> {
	public static Type<AddMeasureEventHandler> TYPE = new Type<AddMeasureEventHandler>();
	private final ComponentMetadata measureMetadata;

	public AddMeasureEvent(ComponentMetadata measureMetadata) {
		this.measureMetadata = measureMetadata;
	}

	@Override
	public Type<AddMeasureEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(AddMeasureEventHandler handler) {
		handler.onAddMeasure(this);
	}

	public ComponentMetadata getMeasureMetadata() {
		return measureMetadata;
	}
}
