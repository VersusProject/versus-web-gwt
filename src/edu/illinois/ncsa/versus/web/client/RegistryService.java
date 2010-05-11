/**
 * 
 */
package edu.illinois.ncsa.versus.web.client;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * @author lmarini
 *
 */
@RemoteServiceRelativePath("registry")
public interface RegistryService extends RemoteService {
	List<String> getMeasures();
	List<String> getExtractors();
}
