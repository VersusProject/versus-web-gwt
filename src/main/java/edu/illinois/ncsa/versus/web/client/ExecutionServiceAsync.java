/**
 * 
 */
package edu.illinois.ncsa.versus.web.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.illinois.ncsa.versus.web.shared.Job;
import edu.illinois.ncsa.versus.web.shared.SamplingJob;
import edu.illinois.ncsa.versus.web.shared.SamplingSubmission;
import edu.illinois.ncsa.versus.web.shared.Submission;

/**
 * @author lmarini
 *
 */
public interface ExecutionServiceAsync {

	void submit(Submission submission, AsyncCallback<Job> callback);

	void getStatus(String jobId, AsyncCallback<Job> callback);
    
    void submit(SamplingSubmission submission, AsyncCallback<SamplingJob> callback);
    
    void getSamplingStatus(String samplingJobId, AsyncCallback<SamplingJob> callback);
}
