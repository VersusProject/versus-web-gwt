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
	private ComponentMetadata extraction;
	private ComponentMetadata measure;
	private ComponentMetadata adapter;
	
	public Submission() {
	}

	public void setDatasetsURI(Set<String> datasetsURI) {
		this.datasetsURI = datasetsURI;
	}

	public Set<String> getDatasetsURI() {
		return datasetsURI;
	}

	public void setExtraction(ComponentMetadata extraction) {
		this.extraction = extraction;
	}

	public ComponentMetadata getExtraction() {
		return extraction;
	}

	public void setMeasure(ComponentMetadata measure) {
		this.measure = measure;
	}

	public ComponentMetadata getMeasure() {
		return measure;
	}

	public void setAdapter(ComponentMetadata adapter) {
		this.adapter = adapter;
	}

	public ComponentMetadata getAdapter() {
		return adapter;
	}
}
