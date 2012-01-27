package edu.illinois.ncsa.versus.web.client.event;

import com.google.gwt.event.shared.EventHandler;

public interface NewJobHandler extends EventHandler {

	void onNewJob(NewJobEvent newJobEvent);
}
