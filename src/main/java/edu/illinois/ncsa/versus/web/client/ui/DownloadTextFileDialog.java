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

import java.util.HashSet;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitHandler;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 *
 * @author antoinev
 */
public class DownloadTextFileDialog extends DialogBox {

    public DownloadTextFileDialog(String title,
            String fileContent, String fileName, String fileExtension) {
        super();

        setAnimationEnabled(true);
        setGlassEnabled(true);
        setText(title);

        VerticalPanel vertical = new VerticalPanel();
        vertical.setWidth("300px");
        vertical.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);

        HorizontalPanel horizontal = new HorizontalPanel();
        horizontal.addStyleName("downloadDialog");
        HorizontalPanel buttons = new HorizontalPanel();

        //Form options
        final FormPanel form = new FormPanel();
        form.setAction("TextFileDownload");
        form.setEncoding(FormPanel.ENCODING_URLENCODED);
        form.setMethod(FormPanel.METHOD_POST);

        //Set file name
        Label titleLabel = new Label("Filename:");
        final TextBox titleText = new TextBox();
        titleText.setName("fileName");
        titleText.setText(fileName);
        titleText.setHeight("24px");
        titleText.setMaxLength(32);

        Label extLabel = new Label('.' + fileExtension);
        
        //Invisible file extension
        TextArea feTextArea = new TextArea();
        feTextArea.setName("fileExtension");
        feTextArea.setVisible(false);
        feTextArea.setText(fileExtension);

        //Invisible content
        TextArea text = new TextArea();
        text.setName("fileContent");
        text.setVisible(false);
        text.setText(fileContent);

        horizontal.add(titleLabel);
        horizontal.add(titleText);
        horizontal.add(extLabel);
        horizontal.add(feTextArea);
        horizontal.add(text);

        //Submit Form
        Button submit = new Button("Download");
        submit.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                form.submit();
                DownloadTextFileDialog.this.hide();
            }
        });
        buttons.add(submit);

        Button cancel = new Button("Cancel");
        cancel.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                DownloadTextFileDialog.this.hide();
            }
        });
        buttons.add(cancel);
        cancel.addStyleName("confirmDialogButtonNo");

        //Default filename
        form.addSubmitHandler(new SubmitHandler() {
            @Override
            public void onSubmit(SubmitEvent event) {
                if (titleText.getText().isEmpty()) {
                    titleText.setText("comparisons-results");
                }

            }
        });

        form.add(horizontal);
        vertical.add(form);
        vertical.add(buttons);

        add(vertical);

        center();
    }
}
