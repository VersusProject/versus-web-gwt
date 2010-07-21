package edu.illinois.ncsa.versus.web.server;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.tupeloproject.kernel.BeanSession;

import edu.illinois.ncsa.versus.web.shared.Job;
import edu.illinois.ncsa.versus.web.shared.Job.ComparisonStatus;
import edu.illinois.ncsa.versus.web.shared.PairwiseComparison;

public class ExecutionEngine {

	private static final int EXECUTION_THREADS = 1;
	private final ExecutorService newFixedThreadPool;
	private List<Job> jobs;
    private static Log log = LogFactory.getLog(ExecutionEngine.class);
	private final BeanSession beanSession;
	
	public ExecutionEngine(BeanSession beanSession) {
		this.beanSession = beanSession;
		newFixedThreadPool = Executors.newFixedThreadPool(EXECUTION_THREADS);
		jobs = new ArrayList<Job>();
	}

	/**
	 * Execute each comparison in its own thread.
	 * 
	 * @param job
	 */
	public void submit(Job job) {
		jobs.add(job);
		Set<PairwiseComparison> comparison = job.getComparison();
		for (PairwiseComparison pairwiseComparison : comparison) {
			ComputeThread computeThread = new ComputeThread(pairwiseComparison, job, beanSession);
			newFixedThreadPool.execute(computeThread);
			job.setStatus(pairwiseComparison.getUri(), ComparisonStatus.STARTED);
		}
		log.debug("Job submitted");
	}

	public Job getJob(String jobId) {
		for (Job job : jobs) {
			if (job.getUri().equals(jobId)) {
				return job;
			}
		}
		log.error("Job not found. Id = " + jobId);
		return null;
	}
}
