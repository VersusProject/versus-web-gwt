/**
 * 
 */
package edu.illinois.ncsa.versus.web.shared;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import edu.uiuc.ncsa.cet.bean.CETBean;

/**
 * @author lmarini
 *
 */
@SuppressWarnings("serial")
public class Job extends CETBean {

	private Set<PairwiseComparison> comparisons;
	
	private Date started;
	
	private Date ended;
	
	public enum ComparisonStatus {STARTED, ENDED, FAILED, ABORTED}
	
	private Map<String, ComparisonStatus> comparisonStatus;
	
	public Job() {
		comparisonStatus = new HashMap<String, ComparisonStatus>();
	}

	public void setComparison(Set<PairwiseComparison> comparison) {
		this.comparisons = comparison;
	}

	public Set<PairwiseComparison> getComparison() {
		return comparisons;
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

	public Map<String, ComparisonStatus> getComparisonStatus() {
		return comparisonStatus;
	}
	
	public synchronized void setStatus(String comparisonId, ComparisonStatus status) {
		if (getComparisonStatus().containsKey(comparisonId)) {
			getComparisonStatus().remove(comparisonId);	
		}
		getComparisonStatus().put(comparisonId, status);
	}
	
	public ComparisonStatus getStatus(PairwiseComparison comparison) {
		return getComparisonStatus().get(comparison);
	}
	
	public synchronized void updateSimilarityValue(String comparisonId, Double similarity) {
		Iterator<PairwiseComparison> iterator = comparisons.iterator();
		while (iterator.hasNext()) {
			PairwiseComparison comparison = iterator.next();
			if (comparison.getUri().equals(comparisonId)) {
				comparison.setSimilarity(similarity);
				setStatus(comparisonId, ComparisonStatus.ENDED);
			}
		}
	}
}
