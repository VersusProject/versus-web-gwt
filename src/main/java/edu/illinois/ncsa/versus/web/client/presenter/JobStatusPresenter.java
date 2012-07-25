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
import edu.illinois.ncsa.versus.web.shared.Job;
import edu.illinois.ncsa.versus.web.shared.Job.ComparisonStatus;
import edu.illinois.ncsa.versus.web.shared.Submission;

/**
 * @author lmarini
 *
 */
public class JobStatusPresenter implements Presenter {

    private static final int WAIT_INTERVAL = 1000;

    private final Display display;

    private final Job job;

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

    public JobStatusPresenter(Display display, Job job, Submission submission) {
        this.display = display;
        this.job = job;
        display.setStart(job.getStarted());
        display.setAdapter(submission.getAdapter().getName());
        display.setExtractor(submission.getExtraction().getName());
        display.setMeasure(submission.getMeasure().getName());
        pollStatus();
    }

    private void pollStatus() {

        Timer timer = new Timer() {

            @Override
            public void run() {
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
                        cancel();
                    }
                });
            }
        };
        timer.scheduleRepeating(WAIT_INTERVAL);

    }

    @Override
    public void go(HasWidgets container) {
        container.add(display.asWidget());
    }
}
