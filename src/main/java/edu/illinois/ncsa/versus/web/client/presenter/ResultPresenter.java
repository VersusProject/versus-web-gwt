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
package edu.illinois.ncsa.versus.web.client.presenter;

import java.util.HashMap;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;

import edu.illinois.ncsa.mmdb.web.client.mvp.BasePresenter;
import edu.illinois.ncsa.versus.web.client.event.NewJobEvent;
import edu.illinois.ncsa.versus.web.client.event.NewJobHandler;
import edu.illinois.ncsa.versus.web.client.event.NewSamplingJobEvent;
import edu.illinois.ncsa.versus.web.client.event.NewSamplingJobHandler;
import edu.illinois.ncsa.versus.web.client.event.NewSubmissionEvent;
import edu.illinois.ncsa.versus.web.client.event.NewSubmissionHandler;
import edu.illinois.ncsa.versus.web.client.event.SubmissionFailureEvent;
import edu.illinois.ncsa.versus.web.client.event.SubmissionFailureHandler;
import edu.illinois.ncsa.versus.web.client.presenter.ResultPresenter.Display;
import edu.illinois.ncsa.versus.web.client.view.JobStatusView;
import edu.illinois.ncsa.versus.web.client.view.SamplingJobStatusView;
import edu.illinois.ncsa.versus.web.shared.ComparisonSubmission;
import edu.illinois.ncsa.versus.web.shared.Job;
import edu.illinois.ncsa.versus.web.shared.SamplingJob;
import edu.illinois.ncsa.versus.web.shared.SamplingSubmission;
import edu.illinois.ncsa.versus.web.shared.Submission;
import net.customware.gwt.dispatch.client.DispatchAsync;

/**
 *
 * @author antoinev
 */
public class ResultPresenter extends BasePresenter<Display> {

    public interface Display {

        Widget asWidget();

        HasWidgets getWidgetsContainer();
    }
    private final HashMap<Submission, Presenter> presenters =
            new HashMap<Submission, Presenter>();

    public ResultPresenter(Display display, DispatchAsync service,
            HandlerManager eventBus) {
        super(display, service, eventBus);
    }

    private Presenter getPresenter(Submission submission) {
        if (presenters.containsKey(submission)) {
            return presenters.get(submission);
        }

        Presenter presenter = null;
        if (submission instanceof ComparisonSubmission) {
            JobStatusView view = new JobStatusView(service);
            presenter = new ComparisonJobStatusPresenter(
                    view, (ComparisonSubmission) submission);
        } else if (submission instanceof SamplingSubmission) {
            SamplingJobStatusView view = new SamplingJobStatusView(service);
            presenter = new SamplingJobStatusPresenter(
                    view, ((SamplingSubmission) submission));
        } else {
            GWT.log("Unknown submission type");
        }
        presenter.go(display.getWidgetsContainer());
        presenters.put(submission, presenter);
        return presenter;
    }

    @Override
    public void bind() {
        addHandler(NewSubmissionEvent.TYPE, new NewSubmissionHandler() {

            @Override
            public void onNewSubmission(NewSubmissionEvent newSubmissionEvent) {
                Submission submission = newSubmissionEvent.getSubmission();
                getPresenter(submission);
            }
        });

        addHandler(NewJobEvent.TYPE, new NewJobHandler() {

            @Override
            public void onNewJob(NewJobEvent newJobEvent) {
                Job job = newJobEvent.getJob();
                ComparisonSubmission submission = newJobEvent.getSubmission();
                ComparisonJobStatusPresenter presenter =
                        (ComparisonJobStatusPresenter) getPresenter(submission);
                presenter.setJob(job);
            }
        });

        addHandler(SubmissionFailureEvent.TYPE, new SubmissionFailureHandler() {

            @Override
            public void onSubmissionFailure(
                    SubmissionFailureEvent submissionFailureEvent) {
                Submission submission = submissionFailureEvent.getSubmission();
                if (submission instanceof ComparisonSubmission) {
                    ComparisonJobStatusPresenter presenter =
                            (ComparisonJobStatusPresenter) getPresenter(submission);
                    Throwable caught = submissionFailureEvent.getCaught();
                    presenter.setError("Cannot submit computation: "
                            + caught.getMessage());
                } else if (submission instanceof SamplingSubmission) {
                    SamplingJobStatusPresenter presenter =
                            (SamplingJobStatusPresenter) getPresenter(submission);
                    Throwable caught = submissionFailureEvent.getCaught();
                    presenter.setError("Cannot submit computation: "
                            + caught.getMessage());
                }
            }
        });

        addHandler(NewSamplingJobEvent.TYPE, new NewSamplingJobHandler() {

            @Override
            public void onNewJob(NewSamplingJobEvent newSamplingJobEvent) {
                SamplingJob job = newSamplingJobEvent.getJob();
                SamplingSubmission submission = newSamplingJobEvent.getSubmission();
                SamplingJobStatusPresenter presenter =
                        (SamplingJobStatusPresenter) getPresenter(submission);
                presenter.setJob(job);
            }
        });
    }
}
