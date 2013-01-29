/**
 * 
 */
package edu.illinois.ncsa.versus.web.client;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;

/**
 * @author lmarini
 *
 */
public class InfoPopup extends PopupPanel {

	public InfoPopup(String text) {
		super(true);
		setAnimationEnabled(true);
		setWidget(new Label(text));
	}
}
