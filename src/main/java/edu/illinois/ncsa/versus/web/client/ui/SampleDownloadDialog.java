package edu.illinois.ncsa.versus.web.client.ui;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitHandler;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;


/**
 * @author David Nimorwicz
 *
 */
public class SampleDownloadDialog extends DialogBox {

    public SampleDownloadDialog(String title, ArrayList<String> uris) {
        this(title, uris, "sample");
    }

    public SampleDownloadDialog(String title, ArrayList<String> uris, String zipName) {
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

        //Compile string of URIs to send through invisible text field
        StringBuffer uri_post_data = new StringBuffer();
        for (String uri : uris ) {
            uri_post_data.append(URL.encode(uri));
            uri_post_data.append("&");
        }
        
        final FormPanel form = new FormPanel();
        form.setAction("SampleBatchDownload");
        form.setEncoding(FormPanel.ENCODING_URLENCODED);
        form.setMethod(FormPanel.METHOD_POST);

        //Set file name
        Label titleLabel = new Label("Filename:");
        final TextBox titleText = new TextBox();
        titleText.setName("filename");
        titleText.setText(zipName);
        titleText.setHeight("24px");
        titleText.setMaxLength(32);

        Label extLabel = new Label(".zip");

        //Invisible list of URIs
        TextArea image_uri_list = new TextArea();
        image_uri_list.setName("uri");
        image_uri_list.setVisible(false);
        image_uri_list.setText(uri_post_data.toString());
        
        horizontal.add(titleLabel);
        horizontal.add(titleText);
        horizontal.add(extLabel);
        horizontal.add(image_uri_list);

        //Submit Form
        Button submit = new Button("Download");
        submit.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                form.submit();
            }
            
        });
        buttons.add(submit);

        Button cancel = new Button("Cancel");
        cancel.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                SampleDownloadDialog.this.hide();
            }
        });
        buttons.add(cancel);
        cancel.addStyleName("confirmDialogButtonNo");

        //Default filename
        form.addSubmitHandler(new SubmitHandler() {

            @Override
            public void onSubmit(SubmitEvent event) {
                if (titleText.getText().isEmpty()) {
                    titleText.setText("sample");
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
