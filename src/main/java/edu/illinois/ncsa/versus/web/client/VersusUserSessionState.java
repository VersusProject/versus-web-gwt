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
package edu.illinois.ncsa.versus.web.client;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 *
 * @author antoinev
 */
public class VersusUserSessionState {

    //LinkedHashSet preserve ordering
    private LinkedHashSet<String> selectedDatasets;

    public VersusUserSessionState() {
        selectedDatasets = new LinkedHashSet<String>();
    }

    public void datasetSelected(String uri) {
        selectedDatasets.add(uri);
    }

    public void datasetUnselected(String uri) {
        selectedDatasets.remove(uri);
    }

    public Set<String> getSelectedDatasets() {
        return selectedDatasets;
    }

    public void allDatasetsUnselected() {
        selectedDatasets.clear();
    }
}
