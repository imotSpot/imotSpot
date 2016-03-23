package com.imotspot.helper;

import com.vaadin.server.Page;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Upload;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

/**
 * Created by Angel Raev on 23-Mar-16
 */
public class ImageUploader implements Upload.Receiver, Upload.SucceededListener {

    public File file;

    @Override
    public OutputStream receiveUpload(String filename, String mimeType) {

        FileOutputStream fos = null; // Stream to write to
        try {
            // Open the file for writing.
            file = new File("../tmp/uploads/" + filename);
            fos = new FileOutputStream(file);
        } catch (final java.io.FileNotFoundException e) {
            new Notification("Could not open file",
                    e.getMessage(),
                    Notification.Type.ERROR_MESSAGE).show(Page.getCurrent());
            return null;
        }
        return fos; // Return the output stream to write to
    }

    @Override
    public void uploadSucceeded(Upload.SucceededEvent succeededEvent) {
//        image.setVisible(true);
//        image.setSource(new FileResource(file));
        new Notification("File uploaded successfull", Notification.Type.HUMANIZED_MESSAGE).show(Page.getCurrent());
    }
}
