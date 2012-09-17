/**
 *
 */
package edu.illinois.ncsa.versus.web.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import edu.illinois.ncsa.versus.web.shared.ComparisonSubmission;
import edu.illinois.ncsa.versus.web.shared.Job;
import edu.illinois.ncsa.versus.web.shared.JobNotFoundException;
import edu.illinois.ncsa.versus.web.shared.JobSubmissionException;
import edu.illinois.ncsa.versus.web.shared.SamplingJob;
import edu.illinois.ncsa.versus.web.shared.SamplingSubmission;

/**
 * @author lmarini
 *
 */
@RemoteServiceRelativePath("execution")
public interface ExecutionService extends RemoteService {

    Job submit(ComparisonSubmission submission) throws JobSubmissionException;

    Job getStatus(String jobId) throws JobNotFoundException;

    SamplingJob submit(SamplingSubmission submission) throws JobSubmissionException;

    SamplingJob getSamplingStatus(String samplingJobId) throws JobNotFoundException;
}
