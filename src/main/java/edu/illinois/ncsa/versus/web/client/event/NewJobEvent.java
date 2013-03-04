/**
 * 
 */
package edu.illinois.ncsa.versus.web.client.event;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.GwtEvent.Type;

import edu.illinois.ncsa.versus.web.shared.ComparisonSubmission;
import edu.illinois.ncsa.versus.web.shared.Job;

/**
 * @author lmarini
 *
 */
public class NewJobEvent extends GwtEvent<NewJobHandler>{

	public static Type<NewJobHandler> TYPE = new Type<NewJobHandler>();
	private Job job;
	private ComparisonSubmission submission;
	
	public NewJobEvent() {
	}
	
	public NewJobEvent(Job job, ComparisonSubmission submission) {
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

	public ComparisonSubmission getSubmission() {
		return submission;
	}

}
