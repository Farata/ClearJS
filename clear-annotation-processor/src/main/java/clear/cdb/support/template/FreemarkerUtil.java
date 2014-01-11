package clear.cdb.support.template;

import freemarker.ext.beans.BeansWrapper;
import freemarker.template.Configuration;
import freemarker.template.Template;

import java.io.StringWriter;
import java.util.Map;

public class FreemarkerUtil {

    private static Configuration config;
    static {
        config = new Configuration();
        config.setTemplateUpdateDelay(0);
        config.setDefaultEncoding("ISO-8859-1");
        config.setClassForTemplateLoading(FreemarkerUtil.class, "/");
        config.setOutputEncoding("UTF-8");
        config.setDateFormat("MM/dd/yyyy");
        config.setDateTimeFormat("MM/dd/yyyy h:mm a");
        ((BeansWrapper)config.getObjectWrapper()).setExposeFields(true);
    }

    public static String render(String templateName, Map<String, Object> params) throws Exception {
        Template template = config.getTemplate(templateName);
        StringWriter sw = new StringWriter();
        template.process(params, sw);
        return sw.toString();
    }

}
