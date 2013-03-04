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
package edu.illinois.ncsa.versus.web.server;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.tupeloproject.kernel.BeanSession;
import org.tupeloproject.rdf.Resource;

import edu.illinois.ncsa.mmdb.web.server.TupeloStore;
import edu.illinois.ncsa.versus.web.shared.JobNotFoundException;
import edu.illinois.ncsa.versus.web.shared.SamplingJob;
import edu.illinois.ncsa.versus.web.shared.SamplingJob.SamplingStatus;
import edu.illinois.ncsa.versus.web.shared.SamplingRequest;
import edu.uiuc.ncsa.cet.bean.DatasetBean;
import gov.nist.itl.ssd.sampling.Sampling;
import gov.nist.itl.ssd.sampling.SamplingClient;

/**
 *
 * @author antoinev
 */
public class SamplingExecutionEngine {

    private final List<SamplingJob> jobs;

    private static final Log log = LogFactory.getLog(SamplingExecutionEngine.class);

    private final SamplingClient client = new SamplingClient(PropertiesManager.getWebServicesUrl());

    public SamplingExecutionEngine() {
        jobs = new CopyOnWriteArrayList<SamplingJob>();
    }

    private class SamplingStatusUpdater extends TimerTask {

        private final SamplingJob job;

        private volatile boolean previousCallEnded = true;
        
        public SamplingStatusUpdater(SamplingJob job) {
            this.job = job;
        }

        @Override
        public void run() {
            if (!previousCallEnded) {
                return;
            }
            previousCallEnded = false;
            try {
                boolean allFinished = true;
                Map<String, SamplingStatus> samplingsStatus =
                        job.getSamplingsStatus();
                for (String id : samplingsStatus.keySet()) {
                    if (samplingsStatus.get(id) == SamplingStatus.STARTED) {
                        Sampling sampling;
                        try {
                            sampling = client.getSampling(id);
                        } catch (RuntimeException e) {
                            job.updateError(id, "Cannot get result: " + e);
                            continue;
                        }

                        Sampling.SamplingStatus status = sampling.getStatus();
                        if (status != null) {
                            switch (status) {
                                case DONE:
                                    SamplingRequest sr = job.getSampling(id);
                                    List<DatasetBean> datasets = sr.getDatasets();
                                    List<DatasetBean> result =
                                            new ArrayList<DatasetBean>(
                                            sr.getSampleSize());
                                    
                                    for (String ind : sampling.getSample()) {
                                    	for (DatasetBean ds : datasets) {
                                    		if (ind.equals(ds.getUri())) {
                                    			result.add(ds);
                                    			break;
                                    		}
                                    	}
                                    }
                                    
                                    job.setEnded(new Date());
                                    job.updateSample(id, result);
                                    break;
                                case FAILED:
                                    job.updateError(id, sampling.getError());
                                    break;
                                case ABORTED:
                                    job.setStatus(id, SamplingStatus.ABORTED);
                                    break;
                                default:
                                    allFinished = false;
                            }
                        } else {
                            allFinished = false;
                        }
                    }
                }
                if (allFinished) {
                    cancel();
                }
            } catch (Exception ex) {
                log.info("Update error", ex);
            } finally {
                previousCallEnded = true;
            }
        }
    }

    public void submit(SamplingJob job) {
        Set<SamplingRequest> samplings = job.getSamplings();
        BeanSession beanSession = TupeloStore.getInstance().getBeanSession();
        for (SamplingRequest sr : samplings) {
            try {
                List<DatasetBean> datasets = sr.getDatasets();
                ArrayList<String> datasetsUrls =
                        new ArrayList<String>(datasets.size());
                ArrayList<InputStream> datasetsStreams =
                        new ArrayList<InputStream>(datasets.size());
                for (DatasetBean ds : datasets) {
                    datasetsUrls.add(ds.getUri());
                    InputStream stream = beanSession.fetchBlob(
                            Resource.uriRef(ds.getUri()));
                    datasetsStreams.add(stream);
                }
                String individualId = sr.getIndividualId();
                String samplerId = sr.getSamplerId();
                int sampleSize = sr.getSampleSize();
                Sampling sampling = new Sampling(individualId, samplerId,
                        sampleSize, datasetsUrls);
                String id = client.submit(sampling, datasetsStreams);
                sr.setId(id);
                job.setStatus(id, SamplingStatus.STARTED);
            } catch (Exception ex) {
                String id = UUID.randomUUID().toString();
                sr.setId(id);
                job.updateError(id, "Error submitting sampling: " + ex.getMessage());
                log.error("Error submitting sampling " + id, ex);
            }
        }
        jobs.add(job);
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(new SamplingStatusUpdater(job), 5000, 5000);
        log.debug("Job submitted");
    }

    public SamplingJob getJob(String jobId) throws JobNotFoundException {
        for (SamplingJob job : jobs) {
            if (job.getId().equals(jobId)) {
                return job;
            }
        }
        throw new JobNotFoundException("Sampling job " + jobId + " not found.");
    }
}
