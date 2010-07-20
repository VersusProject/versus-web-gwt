/**
 * 
 */
package edu.illinois.ncsa.versus.web.client;

import java.util.Set;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import edu.illinois.ncsa.versus.web.shared.Job;
import edu.illinois.ncsa.versus.web.shared.PairwiseComparison;
import edu.illinois.ncsa.versus.web.shared.Submission;

/**
 * @author lmarini
 *
 */
@RemoteServiceRelativePath("execution")
public interface ExecutionService extends RemoteService {
	
	Job submit(Submission submission);
	
	String getStatus(String jobId);
	
	Set<PairwiseComparison> getAllComparisons();
}
