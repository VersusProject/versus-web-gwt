package edu.illinois.ncsa.versus.web.server;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.restlet.resource.ResourceException;
import org.tupeloproject.kernel.BeanSession;
import org.tupeloproject.kernel.OperatorException;
import org.tupeloproject.rdf.Resource;

import edu.illinois.ncsa.mmdb.web.server.TupeloStore;
import edu.illinois.ncsa.versus.core.RetryService;
import edu.illinois.ncsa.versus.core.comparison.Comparison;
import edu.illinois.ncsa.versus.core.comparison.ComparisonClient;
import edu.illinois.ncsa.versus.web.shared.Job;
import edu.illinois.ncsa.versus.web.shared.Job.ComparisonStatus;
import edu.illinois.ncsa.versus.web.shared.JobNotFoundException;
import edu.illinois.ncsa.versus.web.shared.JobSubmissionException;
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

        private volatile boolean previousCallEnded = true;

        public ComparisonsStatusUpdater(Job job) {
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
                Map<String, ComparisonStatus> comparisonStatus =
                        job.getComparisonStatus();
                for (String id : comparisonStatus.keySet()) {
                    if (comparisonStatus.get(id) == Job.ComparisonStatus.STARTED) {
                        Comparison comparison;
                        try {
                            comparison = client.getComparison(id);
                        } catch (RuntimeException e) {
                            job.updateError(id, "Cannot get result: " + e);
                            continue;
                        }

                        Comparison.ComparisonStatus status = comparison.getStatus();
                        if (status != null) {
                            switch (status) {
                                case DONE:
                                    job.updateSimilarityValue(id, new Double(comparison.getValue()));
                                    break;
                                case FAILED:
                                    job.updateError(id, comparison.getError());
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
            } finally {
                previousCallEnded = true;
            }
        }
    }

    public void submit(Job job) throws JobSubmissionException {
        int maxRetry = 3;
        for (int i = 0; i < maxRetry; i++) {
            try {
                trySubmit(job);
                log.debug("Job submitted");
                return;
            } catch (Exception e) {
                log.error("Error submitting the comparisons job", e);
            }
        }
        throw new JobSubmissionException("Error submitting the comparisons job");
    }

    public void trySubmit(Job job) throws OperatorException, IOException {
        BeanSession beanSession = TupeloStore.getInstance().getBeanSession();
        DatasetBean referenceDataset = job.getReferenceDataset();
        List<DatasetBean> datasets = new ArrayList<DatasetBean>(job.getDatasets());
        final ArrayList<String> datasetsNames = new ArrayList<String>(datasets.size());
        final ArrayList<InputStream> datasetsStreams = new ArrayList<InputStream>(datasets.size());
        boolean refDatasetFound = false;
        for (DatasetBean db : datasets) {
            String dbUri = db.getUri();
            String filename = db.getFilename();
            InputStream stream = beanSession.fetchBlob(Resource.uriRef(dbUri));
            if (referenceDataset != null
                    && referenceDataset.getUri().equals(dbUri)) {
                datasetsNames.add(0, filename);
                datasetsStreams.add(0, stream);
                refDatasetFound = true;
            } else {
                datasetsNames.add(filename);
                datasetsStreams.add(stream);
            }
        }
        final String adapterId = job.getAdapterId();
        final String extractorId = job.getExtractorId();
        final String measureId = job.getMeasureId();
        final List<Integer> referenceDatasets = refDatasetFound ? Arrays.asList(0) : null;
        RetryService<List<String>> retryService =
                new RetryService<List<String>>(new Callable<List<String>>() {
            @Override
            public List<String> call() throws Exception {
                return client.submit(
                        adapterId, extractorId, measureId,
                        datasetsNames, datasetsStreams, referenceDatasets);
            }
        }, 30, 3);
                                                  
        List<String> comparisonsIds = retryService.run();

        HashSet<PairwiseComparison> comparisons =
                new HashSet<PairwiseComparison>(comparisonsIds.size());

        Iterator<String> iterator = comparisonsIds.iterator();
        if (!refDatasetFound) {
            for (int i = 0; i < datasets.size(); i++) {
                for (int j = i + 1; j < datasets.size(); j++) {
                    PairwiseComparison comparison = new PairwiseComparison();
                    comparison.setId(iterator.next());
                    comparison.setFirstDataset(datasets.get(i));
                    comparison.setSecondDataset(datasets.get(j));
                    comparison.setAdapterId(adapterId);
                    comparison.setExtractorId(extractorId);
                    comparison.setMeasureId(measureId);
                    comparisons.add(comparison);
                }
            }
        } else {
            for (DatasetBean ds : datasets) {
                if (!ds.getUri().equals(referenceDataset.getUri())) {
                    PairwiseComparison comparison = new PairwiseComparison();
                    comparison.setId(iterator.next());
                    comparison.setFirstDataset(referenceDataset);
                    comparison.setSecondDataset(ds);
                    comparison.setAdapterId(adapterId);
                    comparison.setExtractorId(extractorId);
                    comparison.setMeasureId(measureId);
                    comparisons.add(comparison);
                }
            }
        }

        job.setComparisons(comparisons);
        for (String id : comparisonsIds) {
            job.setStatus(id, ComparisonStatus.STARTED);
        }

        jobs.add(job);
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(new ComparisonsStatusUpdater(job), 5000, 5000);
    }

    public Job getJob(String jobId) throws JobNotFoundException {
        for (Job job : jobs) {
            if (job.getId().equals(jobId)) {
                return job;
            }
        }
        throw new JobNotFoundException("Job not found. Id = " + jobId);
    }
}
