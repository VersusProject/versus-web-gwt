/**
 * 
 */
package edu.illinois.ncsa.versus.web.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.illinois.ncsa.versus.web.shared.Job;
import edu.illinois.ncsa.versus.web.shared.SetComparison;

/**
 * @author lmarini
 *
 */
public interface ExecutionServiceAsync {

	void submit(SetComparison job, AsyncCallback<Job> callback);
}
