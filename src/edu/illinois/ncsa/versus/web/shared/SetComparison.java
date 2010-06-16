/**
 * 
 */
package edu.illinois.ncsa.versus.web.shared;

import java.util.ArrayList;
import java.util.List;

import edu.uiuc.ncsa.cet.bean.CETBean;

/**
 * @author lmarini
 *
 */
@SuppressWarnings("serial")
public class SetComparison extends CETBean {

	private List<PairwiseComparison> comparisons;
	
	public SetComparison() {
		comparisons = new ArrayList<PairwiseComparison>();
	}

	/**
	 * @param comparisons the comparisons to set
	 */
	public void setComparisons(List<PairwiseComparison> comparisons) {
		this.comparisons = comparisons;
	}

	/**
	 * @return the comparisons
	 */
	public List<PairwiseComparison> getComparisons() {
		return comparisons;
	}
}
