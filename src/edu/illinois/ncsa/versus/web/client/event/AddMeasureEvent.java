package edu.illinois.ncsa.versus.web.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class AddMeasureEvent extends GwtEvent<AddMeasureEventHandler> {
	public static Type<AddMeasureEventHandler> TYPE = new Type<AddMeasureEventHandler>();
	private final String name;

	public AddMeasureEvent(String name) {
		this.name = name;
	}

	@Override
	public Type<AddMeasureEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(AddMeasureEventHandler handler) {
		handler.onAddMeasure(this);
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
}
