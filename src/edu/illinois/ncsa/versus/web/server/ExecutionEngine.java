package edu.illinois.ncsa.versus.web.server;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.tupeloproject.kernel.BeanSession;

import edu.illinois.ncsa.versus.web.shared.Job;
import edu.illinois.ncsa.versus.web.shared.PairwiseComparison;

public class ExecutionEngine {

	private static final int EXECUTION_THREADS = 1;
	private final ExecutorService newFixedThreadPool;
	private Map<String, String> jobStatus;
	private Set<PairwiseComparison> comparisons;
    private static Log log = LogFactory.getLog(ExecutionEngine.class);
	private final BeanSession beanSession;
	
	public ExecutionEngine(BeanSession beanSession) {
		this.beanSession = beanSession;
		newFixedThreadPool = Executors.newFixedThreadPool(EXECUTION_THREADS);
		jobStatus = Collections.synchronizedMap(new HashMap<String, String>());
		comparisons = new HashSet<PairwiseComparison>();
	}

	public void submit(Job job) {
		Set<PairwiseComparison> comparison = job.getComparison();
		for (PairwiseComparison pairwiseComparison : comparison) {
			comparisons.add(pairwiseComparison);
			ComputeThread computeThread = new ComputeThread(pairwiseComparison, comparison, comparisons, getJobStatus(), beanSession);
			newFixedThreadPool.execute(computeThread);
		}
		getJobStatus().put(job.getUri(), "Started");
		log.debug("Job submitted");
	}

	public Map<String, String> getJobStatus() {
		return jobStatus;
	}

	public Set<PairwiseComparison> getComparisons() {
		return comparisons;
	}
}
