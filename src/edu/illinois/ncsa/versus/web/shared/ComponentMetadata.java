/**
 * 
 */
package edu.illinois.ncsa.versus.web.shared;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * @author lmarini
 *
 */
@SuppressWarnings("serial")
public class ComponentMetadata implements Serializable {
	private String id;
	private String name;
	private String description;
	private Set<String> supportedInputs = new HashSet<String>();
	private Set<String> supportedOutputs = new HashSet<String>();
	
	public ComponentMetadata() {}
	
	public ComponentMetadata(String id, String name, String description) {
		this.id = id;
		this.name = name;
		this.description = description;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public void setSupportedInputs(Set<String> supportedInputs) {
		this.supportedInputs = supportedInputs;
	}
	
	public void addSupportedInput(String supportedInput) {
		this.supportedInputs.add(supportedInput);
	}

	public Set<String> getSupportedInputs() {
		return supportedInputs;
	}

	public void setSupportedOutputs(Set<String> supportedOutputs) {
		this.supportedOutputs = supportedOutputs;
	}
	
	public void addSupportedOutputs(String supportedOutput) {
		this.supportedOutputs.add(supportedOutput);
	}

	public Set<String> getSupportedOutputs() {
		return supportedOutputs;
	}
}
