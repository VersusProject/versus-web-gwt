package edu.illinois.ncsa.versus.web.server;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.tupeloproject.kernel.BeanSession;
import org.tupeloproject.kernel.OperatorException;
import org.tupeloproject.rdf.Resource;

import edu.illinois.ncsa.mmdb.web.server.TupeloStore;
import edu.illinois.ncsa.versus.core.comparison.Comparison;
import edu.illinois.ncsa.versus.core.comparison.ComparisonClient;
import edu.illinois.ncsa.versus.web.shared.Job;
import edu.illinois.ncsa.versus.web.shared.Job.ComparisonStatus;
import edu.illinois.ncsa.versus.web.shared.PairwiseComparison;
import edu.uiuc.ncsa.cet.bean.DatasetBean;

public class ExecutionEngine {

    private final List<Job> jobs;

    private static final Log log = LogFactory.getLog(ExecutionEngine.class);

    private final ComparisonClient client = new ComparisonClient(PropertiesManager.getWebServicesUrl());

    public ExecutionEngine() {
        jobs = new CopyOnWriteArrayList<Job>();
    }

    private class ComparisonsStatusUpdater extends TimerTask {

        private final Job job;

        public ComparisonsStatusUpdater(Job job) {
            this.job = job;
        }

        @Override
        public void run() {
            try {
                boolean allFinished = true;
                Map<String, ComparisonStatus> comparisonStatus =
                        job.getComparisonStatus();
                for (String id : comparisonStatus.keySet()) {
                    if (comparisonStatus.get(id) == Job.ComparisonStatus.STARTED) {
                        Comparison comparison = client.getComparison(id);
                        Comparison.ComparisonStatus status = comparison.getStatus();
                        if (status != null) {
                            switch (status) {
                                case DONE:
                                    job.updateSimilarityValue(id, new Double(comparison.getValue()));
                                    break;
                                case FAILED:
                                    job.updateError(id, "Error");
                                    break;
                                case ABORTED:
                                    job.setStatus(id, ComparisonStatus.ABORTED);
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
            }
        }
    }

    public void submit(Job job) {
        Set<PairwiseComparison> comparisons = job.getComparisons();
        BeanSession beanSession = TupeloStore.getInstance().getBeanSession();
        for (PairwiseComparison pairwiseComparison : comparisons) {
            DatasetBean firstDataset = pairwiseComparison.getFirstDataset();
            DatasetBean secondDataset = pairwiseComparison.getSecondDataset();
            String dataset1 = firstDataset.getFilename();
            String dataset2 = secondDataset.getFilename();
            String adapterId = pairwiseComparison.getAdapterId();
            String extractorId = pairwiseComparison.getExtractorId();
            String measureId = pairwiseComparison.getMeasureId();
            Comparison comparison = new Comparison(dataset1, dataset2,
                    adapterId, extractorId, measureId);
            try {
                InputStream stream1 = beanSession.fetchBlob(Resource.uriRef(firstDataset.getUri()));
                InputStream stream2 = beanSession.fetchBlob(Resource.uriRef(secondDataset.getUri()));
                String id = client.submit(comparison, stream1, stream2);
                comparison.setId(id);
                pairwiseComparison.setId(id);
                job.setStatus(id, ComparisonStatus.STARTED);
            } catch (OperatorException ex) {
                Logger.getLogger(ExecutionEngine.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(ExecutionEngine.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        jobs.add(job);
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(new ComparisonsStatusUpdater(job), 5000, 5000);
        log.debug("Job submitted");
    }

    public Job getJob(String jobId) {
        for (Job job : jobs) {
            if (job.getId().equals(jobId)) {
                return job;
            }
        }
        log.error("Job not found. Id = " + jobId);
        return null;
    }
}
