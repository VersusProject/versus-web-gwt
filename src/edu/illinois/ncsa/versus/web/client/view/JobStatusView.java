/**
 * 
 */
package edu.illinois.ncsa.versus.web.client.view;

import java.util.Date;
import java.util.List;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import edu.illinois.ncsa.versus.web.client.presenter.JobStatusPresenter.Display;
import edu.illinois.ncsa.versus.web.shared.PairwiseComparison;

/**
 * @author lmarini
 *
 */
public class JobStatusView extends Composite implements Display {

	FlowPanel mainPanel;
	private SimplePanel startPanel;
	private SimplePanel statusPanel;
	
	public JobStatusView() {
		mainPanel = new FlowPanel();
		startPanel = new SimplePanel();
		mainPanel.add(startPanel);
		statusPanel = new SimplePanel();
		mainPanel.add(statusPanel);
		initWidget(mainPanel);
	}
	
	@Override
	public Widget asWidget() {
		return this;
	}

	@Override
	public void setComparisons(List<PairwiseComparison> datasets) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setStart(Date date) {
		startPanel.setWidget(new Label(DateTimeFormat.getMediumDateTimeFormat().format(date)));
	}

	@Override
	public void setStatus(String status) {
		statusPanel.setWidget(new Label(status));
	}

}
