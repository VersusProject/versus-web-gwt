/**
 *
 */
package edu.illinois.ncsa.versus.web.server;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.tupeloproject.kernel.BeanSession;
import org.tupeloproject.kernel.OperatorException;
import org.tupeloproject.rdf.Resource;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.illinois.ncsa.mmdb.web.server.TupeloStore;
import edu.illinois.ncsa.versus.web.client.ExecutionService;
import edu.illinois.ncsa.versus.web.shared.Job;
import edu.illinois.ncsa.versus.web.shared.PairwiseComparison;
import edu.illinois.ncsa.versus.web.shared.SamplingJob;
import edu.illinois.ncsa.versus.web.shared.SamplingRequest;
import edu.illinois.ncsa.versus.web.shared.SamplingSubmission;
import edu.illinois.ncsa.versus.web.shared.ComparisonSubmission;
import edu.uiuc.ncsa.cet.bean.DatasetBean;
import edu.uiuc.ncsa.cet.bean.tupelo.DatasetBeanUtil;

/**
 * @author lmarini
 *
 */
@SuppressWarnings("serial")
public class ExecutionServiceImpl extends RemoteServiceServlet implements
        ExecutionService {

    private static final ExecutionEngine executionEngine = new ExecutionEngine();

    private static final SamplingExecutionEngine see = new SamplingExecutionEngine();

    /**
     * Commons logging *
     */
    private static final Log log = LogFactory.getLog(ExecutionServiceImpl.class);

    @Override
    public Job submit(ComparisonSubmission set) {
        BeanSession beanSession = TupeloStore.getInstance().getBeanSession();

        // create comparison
        Set<PairwiseComparison> comparisons = new HashSet<PairwiseComparison>();
        DatasetBeanUtil dbu = new DatasetBeanUtil(beanSession);

        final String adapterId = set.getAdapter().getId();
        final String extractorId = set.getExtraction().getId();
        final String measureId = set.getMeasure().getId();

        List<String> datasetsURI = new ArrayList<String>(set.getDatasetsURI());
        for (int i = 0; i < datasetsURI.size(); i++) {
            for (int j = i + 1; j < datasetsURI.size(); j++) {
                PairwiseComparison comparison = new PairwiseComparison();
                try {
                    comparison.setFirstDataset(dbu.get(Resource.uriRef(datasetsURI.get(i))));
                    comparison.setSecondDataset(dbu.get(Resource.uriRef(datasetsURI.get(j))));
                    comparison.setAdapterId(adapterId);
                    comparison.setExtractorId(extractorId);
                    comparison.setMeasureId(measureId);
                } catch (OperatorException e) {
                    log.error("Error setting up comparison", e);
                }
                comparisons.add(comparison);
            }
        }

        // submit job for execution
        Job job = new Job();
        job.setStarted(new Date());
        job.setComparisons(comparisons);
        executionEngine.submit(job);
        return job;
    }

    @Override
    public Job getStatus(String jobId) {
        return executionEngine.getJob(jobId);
    }

    @Override
    public SamplingJob submit(SamplingSubmission submission) {
        BeanSession beanSession = TupeloStore.getInstance().getBeanSession();
        DatasetBeanUtil dbu = new DatasetBeanUtil(beanSession);
        Set<String> datasetsURI = submission.getDatasetsURI();
        Set<DatasetBean> beans = new HashSet<DatasetBean>(datasetsURI.size());
        for (String uri : datasetsURI) {
            try {
                beans.add(dbu.get(Resource.uriRef(uri)));
            } catch (OperatorException ex) {
                log.error("Error setting up sampling", ex);
            }
        }
        SamplingRequest sr = new SamplingRequest();
        sr.setDatasets(beans);
        sr.setIndividualId(submission.getIndividual().getId());
        sr.setSamplerId(submission.getSampler().getId());
        sr.setSampleSize(submission.getSampleSize());
        Set<SamplingRequest> samplings = new HashSet<SamplingRequest>(1);
        samplings.add(sr);

        SamplingJob job = new SamplingJob();
        job.setStarted(new Date());
        job.setSamplings(samplings);
        see.submit(job);
        return job;
    }

    @Override
    public SamplingJob getSamplingStatus(String samplingJobId) {
        return see.getJob(samplingJobId);
    }
}
