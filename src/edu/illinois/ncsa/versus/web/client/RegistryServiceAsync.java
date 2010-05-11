/**
 * 
 */
package edu.illinois.ncsa.versus.web.client;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author lmarini
 *
 */
public interface RegistryServiceAsync {

	void getMeasures(AsyncCallback<List<String>> callback);

	void getExtractors(AsyncCallback<List<String>> callback);

}
