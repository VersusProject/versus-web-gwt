/*
 * This software was developed at the National Institute of Standards and
 * Technology by employees of the Federal Government in the course of
 * their official duties. Pursuant to title 17 Section 105 of the United
 * States Code this software is not subject to copyright protection and is
 * in the public domain. This software is an experimental system. NIST assumes
 * no responsibility whatsoever for its use by other parties, and makes no
 * guarantees, expressed or implied, about its quality, reliability, or
 * any other characteristic. We would appreciate acknowledgement if the
 * software is used.
 */
package edu.illinois.ncsa.versus.web.client.event;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.GwtEvent.Type;

import edu.illinois.ncsa.versus.web.shared.Submission;

/**
 *
 * @author antoinev
 */
public class NewSubmissionEvent extends GwtEvent<NewSubmissionHandler> {

    public static final Type<NewSubmissionHandler> TYPE =
            new GwtEvent.Type<NewSubmissionHandler>();
    
    private final Submission submission;
        
    public NewSubmissionEvent(Submission submission) {
        this.submission = submission;
    }
    
    @Override
    public Type<NewSubmissionHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(NewSubmissionHandler handler) {
        handler.onNewSubmission(this);
    }
    
    public Submission getSubmission() {
        return submission;
    }
}
