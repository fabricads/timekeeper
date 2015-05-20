package br.com.redhat.consulting.services;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.redhat.consulting.config.Resources;
import br.com.redhat.consulting.model.Person;
import br.com.redhat.consulting.util.FreeMarkerUtil;
import br.com.redhat.consulting.util.GeneralException;
import br.com.redhat.consulting.util.Util;
import freemarker.template.Configuration;
import freemarker.template.TemplateExceptionHandler;

public class ForgotPassword {

    private static int FACTOR = 10000;
    private static Logger LOG = LoggerFactory.getLogger(ForgotPassword.class);

    @Inject
    private EmailService emailService;
    
    private static Configuration freemarkerCfg;
    
    static {
        freemarkerCfg = new Configuration(Configuration.VERSION_2_3_21);
        freemarkerCfg.setClassForTemplateLoading(Resources.class, "/");
        freemarkerCfg.setDefaultEncoding("UTF-8");
        freemarkerCfg.setTemplateExceptionHandler(TemplateExceptionHandler.HTML_DEBUG_HANDLER);
        // cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
    }

    @Inject
    private PersonService personService;
    
    public void requestPasswordReset(String remoteAddress, Person person) {

        Map<String, String> root = new HashMap<>();
        root.put("name", person.getName());
        root.put("ip_address", remoteAddress);
        root.put("host_address", System.getProperty("timekeeper.host.address", "http://localhost:8080"));
        root.put("hash", buildToken(person));
        
        String text = FreeMarkerUtil.processTemplate("forgot_password.ftl", root);
        if(text != null){
        	emailService.sendPlain(person.getEmail(), "Password definition for timekeeper", text);
        }
        
    }

    public Person check(String hash)  {
        int offset = ("" + FACTOR).length();
        String strId = hash.substring(0, offset);
        Integer personId = Integer.parseInt(strId) - FACTOR;
        Person ps = null;
        try {
            ps = personService.findByIdEnabled(personId);
            if (ps != null) {
                String token = buildToken(ps);
                if (token.equals(hash)) {
                    LOG.debug("Person found = " + ps);
                } else {
                    LOG.warn("Person found " + ps + " but token differs");
                    LOG.warn("submitted hash: " + hash);
                    LOG.warn("expected      : " + token);
                    ps = null;
                }
            } else {
                LOG.debug("Person not found");
            }
        } catch (GeneralException e) {
            LOG.error("Error checking hash for password reset.", e);
        }
        return ps;
    }
    
    private String buildToken(Person person) {
        String clearText = person.getId() + person.getEmail() + person.getRegistered();
        String hash = Util.hash(clearText);
        hash = (FACTOR + person.getId()) + hash;
        return hash;
    }

}
