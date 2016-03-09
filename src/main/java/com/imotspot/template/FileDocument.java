package com.imotspot.template;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;

public class FileDocument {
    private final String filename;
    private File file;
    private URL url;

    public FileDocument(String filename) {
        this.filename = filename;
    }

    public FileDocument loadFile() throws URISyntaxException {
        file = searchFile();
        return this;
    }

    public File file() throws URISyntaxException {
        return file;
    }

    public InputStream asStream() throws URISyntaxException, IOException {
        return url.openStream();
    }

    private File searchFile() throws URISyntaxException {
        url = ClassLoader.getSystemResource(filename);

        if (url != null) {
            return new File(url.toURI());
        }

        File file = new File(filename);
        if (file == null || !file.exists()) {
            return new File("src/main/resources/" + filename);
        }

        return new File(filename);
    }


}
