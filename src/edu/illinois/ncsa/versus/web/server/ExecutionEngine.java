package edu.illinois.ncsa.versus.web.server;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.tupeloproject.kernel.BeanSession;

import edu.illinois.ncsa.versus.web.shared.Job;
import edu.illinois.ncsa.versus.web.shared.PairwiseComparison;
import edu.illinois.ncsa.versus.web.shared.SetComparison;

public class ExecutionEngine {

	private static final int EXECUTION_THREADS = 1;
	private final ExecutorService newFixedThreadPool;
	private  Map<String, String> jobStatus;
    private static Log log = LogFactory.getLog(ExecutionEngine.class);
	private final BeanSession beanSession;
	
	public ExecutionEngine(BeanSession beanSession) {
		this.beanSession = beanSession;
		newFixedThreadPool = Executors.newFixedThreadPool(EXECUTION_THREADS);
		jobStatus = Collections.synchronizedMap(new HashMap<String, String>());
	}

	public void submit(Job job) {
		SetComparison comparison = job.getComparison();
		for (PairwiseComparison pairwiseComparison : comparison.getComparisons()) {
			ComputeThread computeThread = new ComputeThread(pairwiseComparison, comparison, getJobStatus(), beanSession);
			newFixedThreadPool.execute(computeThread);
		}
		getJobStatus().put(job.getUri(), "Started");
		log.debug("Job submitted");
	}

	public Map<String, String> getJobStatus() {
		return jobStatus;
	}
}