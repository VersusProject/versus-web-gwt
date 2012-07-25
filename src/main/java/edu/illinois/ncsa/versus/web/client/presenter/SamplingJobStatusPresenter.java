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

import java.util.Date;
import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;

import edu.illinois.ncsa.versus.web.client.ExecutionService;
import edu.illinois.ncsa.versus.web.client.ExecutionServiceAsync;
import edu.illinois.ncsa.versus.web.shared.SamplingJob;
import edu.illinois.ncsa.versus.web.shared.SamplingJob.SamplingStatus;
import edu.illinois.ncsa.versus.web.shared.SamplingRequest;
import edu.uiuc.ncsa.cet.bean.DatasetBean;

/**
 *
 * @author antoinev
 */
public class SamplingJobStatusPresenter implements Presenter {

    private static final int WAIT_INTERVAL = 1000;

    private final Display display;

    private final ExecutionServiceAsync executionService = GWT.create(ExecutionService.class);

    private final SamplingJob job;

    public interface Display {

        Widget asWidget();

        void setStatus(String status);

        void setStart(Date date);

        void setEnd(Date date);

        void setDatasets(Set<DatasetBean> datasets);

        void setIndividual(String individual);

        void setSampler(String sampler);

        void setSampleSize(int sampleSize);

        void setSample(Set<DatasetBean> sample);
    }

    public SamplingJobStatusPresenter(Display display, SamplingJob job) {
        this.display = display;
        this.job = job;
        SamplingRequest sampling = job.getSamplings().iterator().next();
        display.setStatus(job.getStatus(sampling).toString());
        display.setStart(job.getStarted());
        display.setEnd(job.getEnded());
        display.setDatasets(sampling.getDatasets());
        display.setIndividual(sampling.getIndividualId());
        display.setSampler(sampling.getSamplerId());
        display.setSampleSize(sampling.getSampleSize());
        display.setSample(sampling.getSample());
        pollStatus();
    }

    private void pollStatus() {

        Timer timer = new Timer() {

            @Override
            public void run() {
                executionService.getSamplingStatus(job.getId(),
                        new AsyncCallback<SamplingJob>() {

                            @Override
                            public void onFailure(Throwable caught) {
                                GWT.log("Error getting status of sampling job",
                                        caught);
                            }

                            @Override
                            public void onSuccess(SamplingJob result) {
                                SamplingRequest sr = result.getSamplings().
                                        iterator().next();
                                SamplingStatus status = result.getStatus(sr);
                                if (status != null) {
                                    display.setEnd(result.getEnded());
                                    display.setStatus(status.toString());
                                    display.setSample(sr.getSample());
                                }
                                if (status != null
                                        && status != SamplingStatus.STARTED) {
                                    cancel();
                                }
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
