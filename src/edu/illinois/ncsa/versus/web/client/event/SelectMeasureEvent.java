package edu.illinois.ncsa.versus.web.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class SelectMeasureEvent extends GwtEvent<SelectMeasureEventHandler> {
	public static Type<SelectMeasureEventHandler> TYPE = new Type<SelectMeasureEventHandler>();
	private final String id;
	private final String name;

	public SelectMeasureEvent(String id, String name) {
		this.id = id;
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

	public String getId() {
		return id;
	}
}
