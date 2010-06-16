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
	
	private String extractionID;
	
	private String measureID;
	
	private String adapterID;
	
	private String similarity;
	
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
	 * @return the extractionID
	 */
	public String getExtractionID() {
		return extractionID;
	}

	/**
	 * @param extractionID the extractionID to set
	 */
	public void setExtractionID(String extractionID) {
		this.extractionID = extractionID;
	}

	/**
	 * @return the measureID
	 */
	public String getMeasureID() {
		return measureID;
	}

	/**
	 * @param measureID the measureID to set
	 */
	public void setMeasureID(String measureID) {
		this.measureID = measureID;
	}

	/**
	 * @return the adapterID
	 */
	public String getAdapterID() {
		return adapterID;
	}

	/**
	 * @param adapterID the adapterID to set
	 */
	public void setAdapterID(String adapterID) {
		this.adapterID = adapterID;
	}

	/**
	 * @return the similarity
	 */
	public String getSimilarity() {
		return similarity;
	}

	/**
	 * @param similarity the similarity to set
	 */
	public void setSimilarity(String similarity) {
		this.similarity = similarity;
	}
}
