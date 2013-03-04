/*
 * This software was developed at the National Institute of Standards and
 * Technology by employees of the Federal Government in the course of
 * their official duties. Pursuant to title 17 Section 105 of the United
 * States Code this software is not subject to copyright protection and is
 * in the public domain. This software is an experimental system. NIST assumes
 * no responsibility whatsoever for its use by other parties, and makes no
 * guarantees, expressed or implied, about its quality, reliability, or
 * any other characteristic. We would appreciate acknowledgement if the
 * software is used.
 */
package edu.illinois.ncsa.versus.web.client.ui;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Hyperlink;

/**
 *
 * @author antoinev
 */
public class MainMenu extends Composite {

    public MainMenu() {
        HorizontalPanel navMenu = new HorizontalPanel();
        navMenu.addStyleName("navMenu");


        Hyperlink logoLink = new Hyperlink("Versus", "");
        logoLink.addStyleName("logo");
        navMenu.add(logoLink);

        // workflow
        Hyperlink workflowLink = new Hyperlink("Workflow", "");
        workflowLink.addStyleName("navMenuLink");
        navMenu.add(workflowLink);
        HTML bullet = new HTML("&bull;");
        bullet.addStyleName("navMenuText");
        navMenu.add(bullet);

        // data
        Hyperlink dataLink = new Hyperlink("Data", "listDatasets");
        dataLink.addStyleName("navMenuLink");
        navMenu.add(dataLink);
        bullet = new HTML("&bull;");
        bullet.addStyleName("navMenuText");
        navMenu.add(bullet);

        // collections
        Hyperlink collectionsLink = new Hyperlink("Collections",
                "listCollections");
        collectionsLink.addStyleName("navMenuLink");
        navMenu.add(collectionsLink);
        bullet = new HTML("&bull;");
        bullet.addStyleName("navMenuText");
        navMenu.add(bullet);

        // upload link
        Hyperlink uploadLink = new Hyperlink("Upload", "upload");
        uploadLink.addStyleName("navMenuLink");
        navMenu.add(uploadLink);

        initWidget(navMenu);
    }
}
