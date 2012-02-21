/**
 * 
 */
package edu.illinois.ncsa.versus.web.client.event;

import com.google.gwt.event.shared.EventHandler;

/**
 * @author Luigi Marini <lmarini@ncsa.illinois.edu>
 * 
 */
public interface LoggedOutHandler extends EventHandler {

    void onLoggedOut(LoggedOutEvent loggedOutEvent);

}

