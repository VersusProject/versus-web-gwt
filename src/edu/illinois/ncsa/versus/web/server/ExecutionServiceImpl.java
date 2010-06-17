/**
 * 
 */
package edu.illinois.ncsa.versus.web.server;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

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

	private static Map<String, String> jobStatus = new HashMap<String, String>();
	
    /** Commons logging **/
    private static Log log = LogFactory.getLog(ExecutionServiceImpl.class);
	
	@Override
	public Job submit(SetComparison set) {
		Job job = new Job();
		job.setStarted(new Date());
		job.setComparison(set);
		jobStatus.put(job.getUri(), "Started");
		log.debug("Job submitted");
		Timer timer = new Timer();
		TimerTask task = new RandomJobTask(job.getUri(), jobStatus);
		Random randomGenerator = new Random();
		timer.schedule(task, randomGenerator.nextInt(60)*1000);
		return job;
	}

	@Override
	public String getStatus(String jobId) {
		return jobStatus.get(jobId);
	}
	
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
