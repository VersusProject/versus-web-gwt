/**
 *
 */
package edu.illinois.ncsa.versus.web.client.presenter;

import java.util.Date;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;

import edu.illinois.ncsa.versus.web.client.ExecutionService;
import edu.illinois.ncsa.versus.web.client.ExecutionServiceAsync;
import edu.illinois.ncsa.versus.web.shared.ComparisonSubmission;
import edu.illinois.ncsa.versus.web.shared.Job;
import edu.illinois.ncsa.versus.web.shared.Job.ComparisonStatus;

/**
 * @author lmarini
 *
 */
public class ComparisonJobStatusPresenter implements Presenter {

    private static final int WAIT_INTERVAL = 1000;

    private final Display display;

    private Job job;

    private final ExecutionServiceAsync executionService = GWT.create(ExecutionService.class);

    public interface Display {

        Widget asWidget();

        void setStatus(String status);

        void setStart(Date date);

        void setAdapter(String adapter);

        void setMeasure(String measure);

        void setExtractor(String extractor);

        void showResults(Job job);
    }

    public ComparisonJobStatusPresenter(Display display, ComparisonSubmission submission) {
        this.display = display;
        display.setAdapter(submission.getAdapter().getName());
        display.setExtractor(submission.getExtraction().getName());
        display.setMeasure(submission.getMeasure().getName());
    }

    public void setJob(Job job) {
        this.job = job;
        display.setStart(job.getStarted());
        pollStatus();
    }

    public void setError(String error) {
        display.setStatus(error);
    }

    private void pollStatus() {

        Timer timer = new Timer() {
            private volatile boolean previousCallFinished = true;

            @Override
            public void run() {
                if (!previousCallFinished) {
                    return;
                }
                previousCallFinished = false;
                try {
                    executionService.getStatus(job.getId(), new AsyncCallback<Job>() {
                        @Override
                        public void onSuccess(Job job) {
                            int done = 0;
                            int failed = 0;
                            Map<String, ComparisonStatus> comparisonStatus =
                                    job.getComparisonStatus();
                            for (ComparisonStatus status : comparisonStatus.values()) {
                                if (status == ComparisonStatus.ENDED) {
                                    done++;
                                }
                                if (status == ComparisonStatus.FAILED) {
                                    failed++;
                                }
                            }
                            display.setStatus("Done: " + done + " / " + comparisonStatus.size()
                                    + " Failed: " + failed + " / " + comparisonStatus.size());
                            display.showResults(job);
                            if (done + failed == comparisonStatus.size()) {
                                cancel();
                            }
                        }

                        @Override
                        public void onFailure(Throwable caught) {
                            GWT.log("Error getting status of job ", caught);
                            display.setStatus("Cannot get job status: "
                                    + caught.getMessage());
                            cancel();
                        }
                    });
                } finally {
                    previousCallFinished = true;
                }
            }
        };
        timer.scheduleRepeating(WAIT_INTERVAL);

    }

    @Override
    public void go(HasWidgets container) {
        container.add(display.asWidget());
    }
}
