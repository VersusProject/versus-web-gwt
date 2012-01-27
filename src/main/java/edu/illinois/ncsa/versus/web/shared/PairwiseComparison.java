/**
 * 
 */
package edu.illinois.ncsa.versus.web.shared;

import edu.illinois.ncsa.versus.measure.Similarity;
import edu.uiuc.ncsa.cet.bean.CETBean;
import edu.uiuc.ncsa.cet.bean.DatasetBean;

/**
 * @author lmarini
 *
 */
@SuppressWarnings("serial")
public class PairwiseComparison extends CETBean {
	
	private DatasetBean firstDataset;

	private DatasetBean secondDataset;
	
	private String adapterId;
	
	private String extractorId;
	
	private String measureId;
	
	private Double similarity;
	
	public PairwiseComparison() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * @return the firstDataset
	 */
	public DatasetBean getFirstDataset() {
		return firstDataset;
	}

	/**
	 * @param firstDataset the firstDataset to set
	 */
	public void setFirstDataset(DatasetBean firstDataset) {
		this.firstDataset = firstDataset;
	}

	/**
	 * @return the secondDataset
	 */
	public DatasetBean getSecondDataset() {
		return secondDataset;
	}

	/**
	 * @param secondDataset the secondDataset to set
	 */
	public void setSecondDataset(DatasetBean secondDataset) {
		this.secondDataset = secondDataset;
	}

	/**
	 * @return the similarity
	 */
	public Double getSimilarity() {
		return similarity;
	}

	/**
	 * @param similarity the similarity to set
	 */
	public void setSimilarity(Double similarity) {
		this.similarity = similarity;
	}

	public void setAdapterId(String adapterId) {
		this.adapterId = adapterId;
	}

	public String getAdapterId() {
		return adapterId;
	}

	public void setExtractorId(String extractorId) {
		this.extractorId = extractorId;
	}

	public String getExtractorId() {
		return extractorId;
	}

	public void setMeasureId(String measureId) {
		this.measureId = measureId;
	}

	public String getMeasureId() {
		return measureId;
	}
}
