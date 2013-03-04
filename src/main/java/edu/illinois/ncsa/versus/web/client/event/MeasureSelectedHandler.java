package edu.illinois.ncsa.versus.web.client.event;

import com.google.gwt.event.shared.EventHandler;

public interface MeasureSelectedHandler extends EventHandler {
  void onMeasureSelected(MeasureSelectedEvent event);
}
