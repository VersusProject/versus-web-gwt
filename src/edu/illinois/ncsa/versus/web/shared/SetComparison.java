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
	
	private String extractionID;
	
	private String measureID;
	
	private String adapterID;
	
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

	public void setExtractionID(String extractionID) {
		this.extractionID = extractionID;
	}

	public String getExtractionID() {
		return extractionID;
	}

	public void setMeasureID(String measureID) {
		this.measureID = measureID;
	}

	public String getMeasureID() {
		return measureID;
	}

	public void setAdapterID(String adapterID) {
		this.adapterID = adapterID;
	}

	public String getAdapterID() {
		return adapterID;
	}
}
