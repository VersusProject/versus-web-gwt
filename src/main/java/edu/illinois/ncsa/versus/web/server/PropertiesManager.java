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
import javax.naming.ConfigurationException;

/**
 *
 * @author antoinev
 */
public class PropertiesManager {

    private static final Properties props;

    private static final Logger log;

    static {
        log = Logger.getLogger(PropertiesManager.class.getName());
        props = new Properties();
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
    }

    public static String getWebServicesUrl() {
        if (props.containsKey("webservices.url")) {
            return props.getProperty("webservices.url");
        }
        final String defaultUrl = "http://localhost:8182/versus/api";
        log.log(Level.WARNING, "No Web Services URL specified, using default one: {0}", defaultUrl);
        return defaultUrl;
    }
}
