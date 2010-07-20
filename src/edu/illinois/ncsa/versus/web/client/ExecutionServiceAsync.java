/**
 * 
 */
package edu.illinois.ncsa.versus.web.client;

import java.util.Set;

import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.illinois.ncsa.versus.web.shared.Job;
import edu.illinois.ncsa.versus.web.shared.PairwiseComparison;
import edu.illinois.ncsa.versus.web.shared.Submission;

/**
 * @author lmarini
 *
 */
public interface ExecutionServiceAsync {

	void submit(Submission submission, AsyncCallback<Job> callback);

	void getStatus(String jobId, AsyncCallback<String> callback);

	void getAllComparisons(AsyncCallback<Set<PairwiseComparison>> callback);
}
