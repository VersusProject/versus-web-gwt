/**
 * 
 */
package edu.illinois.ncsa.versus.web.client.event;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.GwtEvent.Type;

import edu.illinois.ncsa.versus.web.shared.Job;

/**
 * @author lmarini
 *
 */
public class NewJobEvent extends GwtEvent<NewJobHandler>{

	public static Type<NewJobHandler> TYPE = new Type<NewJobHandler>();
	private Job job;
	
	public NewJobEvent() {
	}
	
	public NewJobEvent(Job job) {
		this.job = job;
	}

	@Override
	protected void dispatch(NewJobHandler handler) {
		handler.onNewJob(this);
	}

	@Override
	public Type<NewJobHandler> getAssociatedType() {
		return TYPE;
	}

	public Job getJob() {
		return job;
	}

}
