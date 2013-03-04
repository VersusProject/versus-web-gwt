/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.illinois.ncsa.versus.web.client.view;

import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 *
 * @author pkhoury
 */
public class CategoriesWidget {
    
        private DisclosurePanel disclosurePanel;
        private VerticalPanel verticalPanel;

        public CategoriesWidget(DisclosurePanel disclosurePanel, VerticalPanel verticalPanel) {
            this.disclosurePanel = disclosurePanel;
            this.verticalPanel = verticalPanel;
        }

        public DisclosurePanel getDisclosurePanel() {
            return disclosurePanel;
        }

        public VerticalPanel getVerticalPanel() {
            return verticalPanel;
        }
}
