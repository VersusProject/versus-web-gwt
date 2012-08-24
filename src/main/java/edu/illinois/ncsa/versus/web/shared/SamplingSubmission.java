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
package edu.illinois.ncsa.versus.web.shared;

import java.util.Set;

/**
 *
 * @author antoinev
 */
public class SamplingSubmission implements Submission {

    private Set<String> datasetsURI;

    private ComponentMetadata individual;

    private ComponentMetadata sampler;

    private int sampleSize;

    public SamplingSubmission() {
    }

    public Set<String> getDatasetsURI() {
        return datasetsURI;
    }

    public void setDatasetsURI(Set<String> datasetsURI) {
        this.datasetsURI = datasetsURI;
    }

    public ComponentMetadata getIndividual() {
        return individual;
    }

    public void setIndividual(ComponentMetadata individual) {
        this.individual = individual;
    }

    public ComponentMetadata getSampler() {
        return sampler;
    }

    public void setSampler(ComponentMetadata sampler) {
        this.sampler = sampler;
    }

    public int getSampleSize() {
        return sampleSize;
    }

    public void setSampleSize(int sampleSize) {
        this.sampleSize = sampleSize;
    }
}
