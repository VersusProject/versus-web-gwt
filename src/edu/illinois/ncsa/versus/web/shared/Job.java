/**
 * 
 */
package edu.illinois.ncsa.versus.web.shared;

import java.util.Date;

import edu.uiuc.ncsa.cet.bean.CETBean;

/**
 * @author lmarini
 *
 */
@SuppressWarnings("serial")
public class Job extends CETBean {

	private SetComparison comparison;
	
	private Date started;
	
	public Job() {
		// TODO Auto-generated constructor stub
	}

	public void setComparison(SetComparison comparison) {
		this.comparison = comparison;
	}

	public SetComparison getComparison() {
		return comparison;
	}

	public void setStarted(Date started) {
		this.started = started;
	}

	public Date getStarted() {
		return started;
	}
}
