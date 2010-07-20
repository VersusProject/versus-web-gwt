/**
 * 
 */
package edu.illinois.ncsa.versus.web.shared;

import java.util.Date;
import java.util.Set;

import edu.uiuc.ncsa.cet.bean.CETBean;

/**
 * @author lmarini
 *
 */
@SuppressWarnings("serial")
public class Job extends CETBean {

	private Set<PairwiseComparison> comparison;
	
	private Date started;
	
	public Job() {
		// TODO Auto-generated constructor stub
	}

	public void setComparison(Set<PairwiseComparison> comparison) {
		this.comparison = comparison;
	}

	public Set<PairwiseComparison> getComparison() {
		return comparison;
	}

	public void setStarted(Date started) {
		this.started = started;
	}

	public Date getStarted() {
		return started;
	}
}
