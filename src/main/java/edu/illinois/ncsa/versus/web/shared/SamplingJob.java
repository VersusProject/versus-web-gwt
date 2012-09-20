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
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import edu.uiuc.ncsa.cet.bean.DatasetBean;

/**
 *
 * @author antoinev
 */
public class SamplingJob implements Serializable {

    private String id;

    private Set<SamplingRequest> samplings;

    private volatile Date started;

    private volatile Date ended;

    public enum SamplingStatus {

        STARTED, ENDED, FAILED, ABORTED

    }
    private Map<String, SamplingStatus> samplingStatus;

    public SamplingJob() {
        id = UUID.randomUUID().toString();
        samplingStatus = new HashMap<String, SamplingStatus>();
    }

    public String getId() {
        return id;
    }

    public synchronized void setSamplings(Set<SamplingRequest> sampling) {
        this.samplings = new HashSet(sampling);
    }

    public synchronized Set<SamplingRequest> getSamplings() {
        return new HashSet<SamplingRequest>(samplings);
    }

    public synchronized Map<String, SamplingStatus> getSamplingsStatus() {
        return new HashMap<String, SamplingStatus>(samplingStatus);
    }

    public void setStarted(Date started) {
        this.started = started;
    }

    public Date getStarted() {
        return started;
    }

    public void setEnded(Date ended) {
        this.ended = ended;
    }

    public Date getEnded() {
        return ended;
    }

    public synchronized void setStatus(String samplingId, SamplingStatus status) {
        if (samplingStatus.containsKey(samplingId)) {
            samplingStatus.remove(samplingId);
        }
        samplingStatus.put(samplingId, status);
    }

    public synchronized SamplingStatus getStatus(SamplingRequest sampling) {
        return samplingStatus.get(sampling.getId());
    }

    public synchronized void updateSample(String samplingId, List<DatasetBean> sample) {
        SamplingRequest sampling = getSampling(samplingId);
        if (sampling != null) {
            sampling.setSample(sample);
            setStatus(samplingId, SamplingStatus.ENDED);
        }
    }

    public synchronized void updateError(String samplingId, String error) {
        SamplingRequest sampling = getSampling(samplingId);
        if (sampling != null) {
            sampling.setError(error);
            setStatus(samplingId, SamplingStatus.FAILED);
        }
    }

    public synchronized SamplingRequest getSampling(String samplingId) {
        for (SamplingRequest sampling : samplings) {
            if (samplingId.equals(sampling.getId())) {
                return sampling;
            }
        }
        return null;
    }
}
