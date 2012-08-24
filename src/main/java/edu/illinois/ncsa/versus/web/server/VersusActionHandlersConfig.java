/*
 * This software was developed at the National Institute of Standards and
 * Technology by employees of the Federal Government in the course of
 * their official duties. Pursuant to title 17 Section 105 of the United
 * States Code this software is not subject to copyright protection and is
 * in the public domain. This software is an experimental system. NIST assumes
 * no responsibility whatsoever for its use by other parties, and makes no
 * guarantees, expressed or implied, about its quality, reliability, or
 * any other characteristic. We would appreciate acknowledgement if the
 * software is used.
 */
package edu.illinois.ncsa.versus.web.server;

import java.util.logging.Level;
import java.util.logging.Logger;

import edu.illinois.ncsa.mmdb.web.server.DispatchUtil;
import edu.illinois.ncsa.versus.web.server.dispatch.ListQueryCollectionsHandler;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 *
 * @author antoinev
 */
public class VersusActionHandlersConfig implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        Logger.getLogger(VersusActionHandlersConfig.class.getName())
                .log(Level.INFO, "Registering handlers.");
        DispatchUtil.registerHandler(new ListQueryCollectionsHandler());
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }
    
}
