package com.imotspot.template;

import freemarker.cache.FileTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.IOException;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.util.Map;

public class FileTemplate {
    FileDocument fileDocument;

    public FileTemplate(FileDocument fileDocument) {
        this.fileDocument = fileDocument;
    }

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
