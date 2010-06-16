/**
 * 
 */
package edu.illinois.ncsa.versus.web.server;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.illinois.ncsa.mmdb.web.server.dispatch.AddCollectionHandler;
import edu.illinois.ncsa.versus.web.client.ExecutionService;
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
	public void submit(SetComparison job) {
		log.debug("Job submitted");
	}

}
