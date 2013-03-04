package edu.illinois.ncsa.versus.web.client;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;

public class TestGraph extends Composite {

	public TestGraph(String id) {
		HTML test = new HTML("<div id='"+id+"'></div>");
		initWidget(test);
	}
}
