package edu.illinois.ncsa.versus.web.shared;

import edu.uiuc.ncsa.cet.bean.CETBean;

@SuppressWarnings("serial")
public class ComponentInfo extends CETBean {

	private Job name;
	private Job description;
	private Job id;
	
	public ComponentInfo() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * @return the name
	 */
	public Job getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(Job name) {
		this.name = name;
	}

	/**
	 * @return the description
	 */
	public Job getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(Job description) {
		this.description = description;
	}

	/**
	 * @return the id
	 */
	public Job getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Job id) {
		this.id = id;
	}
}
