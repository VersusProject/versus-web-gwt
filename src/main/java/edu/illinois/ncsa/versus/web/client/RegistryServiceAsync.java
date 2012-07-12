/**
 * 
 */
package edu.illinois.ncsa.versus.web.client;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.illinois.ncsa.versus.web.shared.ComponentMetadata;

/**
 * @author lmarini
 *
 */
public interface RegistryServiceAsync {

	void getMeasures(AsyncCallback<List<ComponentMetadata>> callback);

	void getExtractors(AsyncCallback<List<ComponentMetadata>> callback);

	void getAdapters(AsyncCallback<List<ComponentMetadata>> callback);
    
    void getSamplers(AsyncCallback<List<ComponentMetadata>> callback);
}
