package com.imotspot.template;

import freemarker.cache.FileTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.IOException;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.util.Map;

/**
 * Freemaker implementation to transform {@link FileTemplate}
 */
public class FileTemplate {
    FileDocument fileDocument;

    public FileTemplate(FileDocument fileDocument) {
        this.fileDocument = fileDocument;
    }

    /**
     * Fills the template with the data and returns the result.
     *
     * @param data key of the map is the template string to search in file.
     * @return the replaced template.
     * @throws IOException
     * @throws TemplateException
     * @throws URISyntaxException
     */
    public String transform(Map<String, Object> data) throws IOException, TemplateException, URISyntaxException {
        Configuration cfg = new Configuration();
        cfg.setTemplateLoader(new FileTemplateLoader(fileDocument.loadFile().file().getParentFile()));
        Template template = cfg.getTemplate(fileDocument.loadFile().file().getName());

        StringWriter out = new StringWriter();
        template.process(data, out);
        out.flush();
        out.close();

        return out.toString();
    }
}
