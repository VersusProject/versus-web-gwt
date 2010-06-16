/**
 * 
 */
package edu.illinois.ncsa.versus.web.server;

import java.util.Date;

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

    /** Commons logging **/
    private static Log log = LogFactory.getLog(ExecutionServiceImpl.class);
	
	@Override
	public Job submit(SetComparison set) {
		Job job = new Job();
		job.setStarted(new Date());
		job.setComparison(set);
		log.debug("Job submitted");
		return job;
	}

}
