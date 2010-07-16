/**
 * 
 */
package edu.illinois.ncsa.versus.web.server;

import java.util.Date;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.tupeloproject.kernel.OperatorException;
import org.tupeloproject.rdf.Resource;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.illinois.ncsa.mmdb.web.server.TupeloStore;
import edu.illinois.ncsa.versus.web.client.ExecutionService;
import edu.illinois.ncsa.versus.web.shared.Job;
import edu.illinois.ncsa.versus.web.shared.PairwiseComparison;
import edu.illinois.ncsa.versus.web.shared.SetComparison;
import edu.illinois.ncsa.versus.web.shared.Submission;
import edu.uiuc.ncsa.cet.bean.tupelo.DatasetBeanUtil;

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
	public Job submit(Submission set) {
		
		// create comparison
		SetComparison comparisons = new SetComparison();
		comparisons.setAdapterID("edu.illinois.ncsa.versus.adapter.impl.BufferedImageAdapter");
		comparisons.setMeasureID(set.getMeasureID());
		comparisons.setExtractionID(set.getExtractionID());
		DatasetBeanUtil dbu = new DatasetBeanUtil(TupeloStore.getInstance().getBeanSession());
		for (String datasetOne : set.getDatasetsURI()) {
			for (String datasetTwo : set.getDatasetsURI()) {
				if (!datasetOne.equals(datasetTwo)) {
					PairwiseComparison pairwiseComparison = new PairwiseComparison();
					try {
						pairwiseComparison.setFirstDataset(dbu.get(Resource.uriRef(datasetOne)));
						pairwiseComparison.setSecondDataset(dbu.get(Resource.uriRef(datasetTwo)));
					} catch (OperatorException e) {
						e.printStackTrace();
					}
					comparisons.getComparisons().add(pairwiseComparison);
				}
			}
		}
		
		// submit job for execution
		Job job = new Job();
		job.setStarted(new Date());
		job.setComparison(comparisons);
		executionEngine.submit(job);
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

	@Override
	public Set<PairwiseComparison> getAllComparisons() {
		return executionEngine.getComparisons();
	}

}
