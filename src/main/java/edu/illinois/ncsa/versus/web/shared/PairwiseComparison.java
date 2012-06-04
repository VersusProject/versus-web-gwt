/**
 * 
 */
package edu.illinois.ncsa.versus.web.shared;

import java.io.Serializable;

import edu.uiuc.ncsa.cet.bean.DatasetBean;

/**
 * @author lmarini
 *
 */
public class PairwiseComparison implements Serializable {
	
    private String id;
    
	private DatasetBean firstDataset;

	private DatasetBean secondDataset;
	
	private String adapterId;
	
	private String extractorId;
	
	private String measureId;
	
	private Double similarity;
    
    private String error;
	
	public PairwiseComparison() {
	}
	
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
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
    
    public void setError(String error) {
        this.error = error;
    }
    
    public String getError() {
        return error;
    }
}
