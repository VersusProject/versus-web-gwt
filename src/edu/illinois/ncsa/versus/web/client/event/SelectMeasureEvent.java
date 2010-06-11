package edu.illinois.ncsa.versus.web.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class SelectMeasureEvent extends GwtEvent<SelectMeasureEventHandler> {
	public static Type<SelectMeasureEventHandler> TYPE = new Type<SelectMeasureEventHandler>();
	private final String name;

	public SelectMeasureEvent(String name) {
		this.name = name;
	}

	@Override
	public Type<SelectMeasureEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(SelectMeasureEventHandler handler) {
		handler.onAddMeasure(this);
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
}
