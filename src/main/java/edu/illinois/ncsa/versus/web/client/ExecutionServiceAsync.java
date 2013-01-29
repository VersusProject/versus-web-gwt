/**
 * 
 */
package edu.illinois.ncsa.versus.web.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.illinois.ncsa.versus.web.shared.ComparisonSubmission;
import edu.illinois.ncsa.versus.web.shared.Job;
import edu.illinois.ncsa.versus.web.shared.SamplingJob;
import edu.illinois.ncsa.versus.web.shared.SamplingSubmission;

/**
 * @author lmarini
 *
 */
public interface ExecutionServiceAsync {

	void submit(ComparisonSubmission submission, AsyncCallback<Job> callback);

	void getStatus(String jobId, AsyncCallback<Job> callback);
    
    void submit(SamplingSubmission submission, AsyncCallback<SamplingJob> callback);
    
    void getSamplingStatus(String samplingJobId, AsyncCallback<SamplingJob> callback);
}
