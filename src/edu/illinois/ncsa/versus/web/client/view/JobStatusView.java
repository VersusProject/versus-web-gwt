/**
 * 
 */
package edu.illinois.ncsa.versus.web.client.view;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Set;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
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
	private final SimplePanel startPanel;
	private final SimplePanel statusPanel;
	private final SimplePanel measurePanel;
	private final SimplePanel extractorPanel;
	private final FlowPanel comparisonsPanel;
	
	public JobStatusView() {
		mainPanel = new FlowPanel();
		mainPanel.addStyleName("jobStatusPanel");
		measurePanel = new SimplePanel();
		mainPanel.add(measurePanel);
		extractorPanel = new SimplePanel();
		mainPanel.add(extractorPanel);
		startPanel = new SimplePanel();
		mainPanel.add(startPanel);
		statusPanel = new SimplePanel();
		mainPanel.add(statusPanel);
		comparisonsPanel = new FlowPanel();
		mainPanel.add(comparisonsPanel);
		initWidget(mainPanel);
	}
	
	@Override
	public Widget asWidget() {
		return this;
	}

	@Override
	public void setComparisons(Set<PairwiseComparison> datasets) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setStart(Date date) {
		startPanel.setWidget(new Label(DateTimeFormat.getMediumDateTimeFormat().format(date)));
	}

	@Override
	public void setStatus(String status) {
		FlowPanel panel = new FlowPanel();
		panel.add(new Label(status));
		statusPanel.setWidget(panel);
	}

	@Override
	public void setExtractor(String extractor) {
		extractorPanel.setWidget(new Label(extractor));
	}

	@Override
	public void setMeasure(String measure) {
		measurePanel.setWidget(new Label(measure));
	}

	@Override
	public void showResults(Set<PairwiseComparison> comparisons) {
		comparisonsPanel.clear();
		List<PairwiseComparison> ordered = new ArrayList<PairwiseComparison>(comparisons);
		Collections.sort(ordered, new Comparator<PairwiseComparison>() {

			@Override
			public int compare(PairwiseComparison o1, PairwiseComparison o2) {
				double parseDouble = o1.getSimilarity();
				double parseDouble2 = o2.getSimilarity();
				if (parseDouble > parseDouble2) {
					return -1;
				} else if (parseDouble < parseDouble2) {
					return 1;
				} else {
					return 0;
				}
			}
		});
		for (PairwiseComparison comparison: ordered) {
			comparisonsPanel.add(new HTML(comparison.getFirstDataset().getTitle() + 
					" vs " + comparison.getSecondDataset().getTitle() + 
					" = <b>" + comparison.getSimilarity() + "</b>"));
		}
	}

}
