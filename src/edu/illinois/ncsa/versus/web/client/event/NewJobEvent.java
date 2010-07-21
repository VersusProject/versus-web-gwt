/**
 * 
 */
package edu.illinois.ncsa.versus.web.client.event;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.GwtEvent.Type;

import edu.illinois.ncsa.versus.web.shared.Job;
import edu.illinois.ncsa.versus.web.shared.Submission;

/**
 * @author lmarini
 *
 */
public class NewJobEvent extends GwtEvent<NewJobHandler>{

	public static Type<NewJobHandler> TYPE = new Type<NewJobHandler>();
	private Job job;
	private Submission submission;
	
	public NewJobEvent() {
	}
	
	public NewJobEvent(Job job, Submission submission) {
		this.job = job;
		this.submission = submission;
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

	public Submission getSubmission() {
		return submission;
	}

}
