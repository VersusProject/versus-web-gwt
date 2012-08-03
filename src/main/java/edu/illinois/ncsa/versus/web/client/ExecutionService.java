/**
 *
 */
package edu.illinois.ncsa.versus.web.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import edu.illinois.ncsa.versus.web.shared.Job;
import edu.illinois.ncsa.versus.web.shared.SamplingJob;
import edu.illinois.ncsa.versus.web.shared.SamplingSubmission;
import edu.illinois.ncsa.versus.web.shared.ComparisonSubmission;

/**
 * @author lmarini
 *
 */
@RemoteServiceRelativePath("execution")
public interface ExecutionService extends RemoteService {

    Job submit(ComparisonSubmission submission);

    Job getStatus(String jobId);

    SamplingJob submit(SamplingSubmission submission);

    SamplingJob getSamplingStatus(String samplingJobId);
}
