/**
 *
 */
package edu.illinois.ncsa.versus.web.shared;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * @author lmarini
 *
 */
public class Job implements Serializable {

    private String id;
    
    private Set<PairwiseComparison> comparisons;

    private volatile Date started;

    private volatile Date ended;

    public enum ComparisonStatus {

        STARTED, ENDED, FAILED, ABORTED

    }
    private Map<String, ComparisonStatus> comparisonStatus;

    public Job() {
        id = UUID.randomUUID().toString();
        comparisonStatus = new HashMap<String, ComparisonStatus>();
    }
    
    public String getId() {
        return id;
    }

    public synchronized void setComparisons(Set<PairwiseComparison> comparison) {
        this.comparisons = new HashSet(comparison);
    }

    public synchronized Set<PairwiseComparison> getComparisons() {
        return new HashSet<PairwiseComparison>(comparisons);
    }
    
    public synchronized Map<String, ComparisonStatus> getComparisonStatus() {
        return new HashMap<String, ComparisonStatus>(comparisonStatus);
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

    public synchronized void setStatus(String comparisonId, ComparisonStatus status) {
        if (comparisonStatus.containsKey(comparisonId)) {
            comparisonStatus.remove(comparisonId);
        }
        comparisonStatus.put(comparisonId, status);
    }

    public synchronized ComparisonStatus getStatus(PairwiseComparison comparison) {
        return comparisonStatus.get(comparison.getId());
    }

    public synchronized void updateSimilarityValue(String comparisonId, Double similarity) {
        PairwiseComparison comparison = getComparison(comparisonId);
        if (comparison != null) {
            comparison.setSimilarity(similarity);
            setStatus(comparisonId, ComparisonStatus.ENDED);
        }
    }

    public synchronized void updateError(String comparisonId, String error) {
        PairwiseComparison comparison = getComparison(comparisonId);
        if (comparison != null) {
            comparison.setError(error);
            setStatus(comparisonId, ComparisonStatus.FAILED);
        }
    }

    private synchronized PairwiseComparison getComparison(String comparisonId) {
        for (PairwiseComparison comparison : comparisons) {
            if (comparisonId.equals(comparison.getId())) {
                return comparison;
            }
        }
        return null;
    }
}
