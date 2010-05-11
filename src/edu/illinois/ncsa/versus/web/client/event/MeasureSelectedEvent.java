package edu.illinois.ncsa.versus.web.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class MeasureSelectedEvent extends GwtEvent<MeasureSelectedHandler> {
	public static Type<MeasureSelectedHandler> TYPE = new Type<MeasureSelectedHandler>();
	private final String name;

	public MeasureSelectedEvent(String name) {
		this.name = name;
	}

	@Override
	public Type<MeasureSelectedHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(MeasureSelectedHandler handler) {
		handler.onMeasureSelected(this);
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
}
