/**
 * 
 */
package edu.illinois.ncsa.versus.web.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import edu.illinois.ncsa.versus.web.shared.Job;
import edu.illinois.ncsa.versus.web.shared.SetComparison;

/**
 * @author lmarini
 *
 */
@RemoteServiceRelativePath("execution")
public interface ExecutionService extends RemoteService {
	
	Job submit(SetComparison job);
}
