/**
 * 
 */
package edu.illinois.ncsa.versus.web.server;

import java.util.Date;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.illinois.ncsa.mmdb.web.server.TupeloStore;
import edu.illinois.ncsa.versus.web.client.ExecutionService;
import edu.illinois.ncsa.versus.web.shared.Job;
import edu.illinois.ncsa.versus.web.shared.SetComparison;

/**
 * @author lmarini
 *
 */
@SuppressWarnings("serial")
public class ExecutionServiceImpl extends RemoteServiceServlet implements
		ExecutionService {
	
	private static ExecutionEngine executionEngine = new ExecutionEngine(TupeloStore.getInstance().getBeanSession());
	
    /** Commons logging **/
    private static Log log = LogFactory.getLog(ExecutionServiceImpl.class);
	
	@Override
	public Job submit(SetComparison set) {
		// submit job for execution
		Job job = new Job();
		job.setStarted(new Date());
		job.setComparison(set);
//		executionEngine.submit(job);
		// timer submission for debugging
		executionEngine.getJobStatus().put(job.getUri(), "Started");
		Timer timer = new Timer();
		TimerTask task = new RandomJobTask(job.getUri(), executionEngine.getJobStatus());
		Random randomGenerator = new Random();
		timer.schedule(task, randomGenerator.nextInt(60)*1000);
		return job;
	}

	@Override
	public String getStatus(String jobId) {
		return executionEngine.getJobStatus().get(jobId);
	}
	
	/**
	 * For testing purposes only. Add job uri to map as done without doing anything.
	 * 
	 * @author lmarini
	 *
	 */
	class RandomJobTask extends TimerTask {

		private final String uri;
		private final Map<String, String> jobStatus2;

		public RandomJobTask(String uri, Map<String, String> jobStatus) {
			this.uri = uri;
			jobStatus2 = jobStatus;
		}

		@Override
		public void run() {
			jobStatus2.put(uri, "Done");
			log.debug("Job " + uri + " done");
		}
	
	}

}
