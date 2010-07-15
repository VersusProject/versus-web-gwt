/**
 * 
 */
package edu.illinois.ncsa.versus.web.shared;

import java.io.Serializable;
import java.util.Set;

/**
 * @author lmarini
 *
 */
@SuppressWarnings("serial")
public class Submission implements Serializable {

	private Set<String> datasetsURI;
	private String extractionID;
	private String measureID;
	private String adapterID;
	
	public Submission() {
	}

	public void setDatasetsURI(Set<String> datasetsURI) {
		this.datasetsURI = datasetsURI;
	}

	public Set<String> getDatasetsURI() {
		return datasetsURI;
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
