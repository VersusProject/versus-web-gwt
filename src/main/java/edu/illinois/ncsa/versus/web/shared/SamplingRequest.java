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

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import edu.uiuc.ncsa.cet.bean.DatasetBean;

/**
 *
 * @author antoinev
 */
public class SamplingRequest implements Serializable {

    private String id;

    private String individualId;

    private String samplerId;

    private List<DatasetBean> datasets;

    private int sampleSize;

    private List<DatasetBean> sample;

    private String error;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIndividualId() {
        return individualId;
    }

    public void setIndividualId(String individualId) {
        this.individualId = individualId;
    }

    public String getSamplerId() {
        return samplerId;
    }

    public void setSamplerId(String samplerId) {
        this.samplerId = samplerId;
    }

    public List<DatasetBean> getDatasets() {
        return datasets;
    }

    public void setDatasets(List<DatasetBean> datasets) {
        this.datasets = datasets;
    }

    public int getSampleSize() {
        return sampleSize;
    }

    public void setSampleSize(int sampleSize) {
        this.sampleSize = sampleSize;
    }

    public List<DatasetBean> getSample() {
        return sample;
    }

    public void setSample(List<DatasetBean> sample) {
        this.sample = sample;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
