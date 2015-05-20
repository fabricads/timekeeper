package br.com.redhat.consulting.util;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.redhat.consulting.config.Resources;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;

public class FreeMarkerUtil {
	
	private static Configuration freemarkerCfg;
	
	private static Logger LOG = LoggerFactory.getLogger(FreeMarkerUtil.class);
	
	static {
        freemarkerCfg = new Configuration(Configuration.VERSION_2_3_21);
        freemarkerCfg.setClassForTemplateLoading(Resources.class, "/");
        freemarkerCfg.setDefaultEncoding("UTF-8");
        freemarkerCfg.setTemplateExceptionHandler(TemplateExceptionHandler.HTML_DEBUG_HANDLER);
        // cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
    }
	
	public static String processTemplate(String template, Map<String, ?> dataModel){
        Template temp;
        try {
            temp = freemarkerCfg.getTemplate(template);
            Writer out = new StringWriter();
            temp.process(dataModel, out);
            String text = out.toString();
            LOG.debug(text);
            return text;
        } catch (IOException | TemplateException e) {
            LOG.error("Error processing freemarker template", e);
            return null;
        }  
	}

}
