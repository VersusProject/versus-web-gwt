/**
 * 
 */
package edu.illinois.ncsa.versus.web.shared;

import java.io.Serializable;

/**
 * @author lmarini
 *
 */
@SuppressWarnings("serial")
public class ComponentMetadata implements Serializable {
	private String id;
	private String name;
	private String description;
	
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
}
