package edu.illinois.ncsa.versus.web.server;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExecutionEngine {

	private static final int EXECUTION_THREADS = 1;
	private final ExecutorService newFixedThreadPool;
	
	public ExecutionEngine() {
		newFixedThreadPool = Executors.newFixedThreadPool(EXECUTION_THREADS);
	}
	
	public void submit() {


	}
	
	public String getStatus(String jobId) {
		return "";
	}
}
