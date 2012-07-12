/**
 *
 */
package edu.illinois.ncsa.versus.web.client;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import edu.illinois.ncsa.versus.web.shared.ComponentMetadata;

/**
 * @author lmarini
 *
 */
@RemoteServiceRelativePath("registry")
public interface RegistryService extends RemoteService {

    List<ComponentMetadata> getMeasures();

    List<ComponentMetadata> getExtractors();

    List<ComponentMetadata> getAdapters();
    
    List<ComponentMetadata> getSamplers();
}
