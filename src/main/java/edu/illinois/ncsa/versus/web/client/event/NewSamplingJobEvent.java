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

import edu.illinois.ncsa.versus.web.shared.SamplingJob;
import edu.illinois.ncsa.versus.web.shared.SamplingSubmission;

/**
 *
 * @author antoinev
 */
public class NewSamplingJobEvent extends GwtEvent<NewSamplingJobHandler> {

    public static Type<NewSamplingJobHandler> TYPE =
            new Type<NewSamplingJobHandler>();

    private SamplingJob job;

    private SamplingSubmission submission;

    public NewSamplingJobEvent() {
    }

    public NewSamplingJobEvent(SamplingJob job, SamplingSubmission submission) {
        this.job = job;
        this.submission = submission;
    }

    @Override
    protected void dispatch(NewSamplingJobHandler handler) {
        handler.onNewJob(this);
    }

    @Override
    public Type<NewSamplingJobHandler> getAssociatedType() {
        return TYPE;
    }

    public SamplingJob getJob() {
        return job;
    }

    public SamplingSubmission getSubmission() {
        return submission;
    }
}
