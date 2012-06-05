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

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.illinois.ncsa.mmdb.web.server.TupeloStore;
import edu.illinois.ncsa.versus.core.ClientResourceFactory;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 *
 * @author antoinev
 */
public class PropertiesManager implements ServletContextListener {

    private static final Logger log = Logger.getLogger(PropertiesManager.class.getName());

    private static String webServicesUrl;

    public static String getWebServicesUrl() {
        return webServicesUrl;
    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        Properties props = new Properties();
        String path = "/server.properties";
        log.log(Level.INFO, "Loading server property file: {0}", path);

        // load properties
        InputStream input = null;
        try {
            input = TupeloStore.findFile(path).openStream();
            props.load(input);
        } catch (IOException exc) {
            log.log(Level.WARNING, "Could not load server.properties.", exc);
        } finally {
            try {
                input.close();
            } catch (IOException exc) {
                log.log(Level.WARNING, "Could not close server.properties.", exc);
            }
        }

        if (props.containsKey("webservices.url")) {
            webServicesUrl = props.getProperty("webservices.url");
            log.log(Level.INFO, "Using Web Services URL {0}", webServicesUrl);
        } else {
            webServicesUrl = "http://localhost:8182/versus/api";
            log.log(Level.WARNING, "No Web Services URL specified, using default one: {0}", webServicesUrl);
        }

        if (props.containsKey("webservices.retrydelay")) {
            try {
                long retryDelay = new Long(props.getProperty("webservices.retrydelay"));
                ClientResourceFactory.setRetryDelay(retryDelay);
                log.log(Level.INFO, "Using retry delay: {0}", retryDelay);
            } catch (Exception e) {
                log.log(Level.WARNING, "Could not set the retrydelay property.", e);
            }
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }
}
