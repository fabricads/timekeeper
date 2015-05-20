package br.com.redhat.consulting.services;

import javax.annotation.Resource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmailService {
    
    private static Logger LOG = LoggerFactory.getLogger(EmailService.class);
    
    @Resource(mappedName = "java:jboss/mail/Default")
    private Session mailSession;

    public void sendPlain(String to, String subject, String body) {
        String from = "no-reply@redhat.com";
        try {
            MimeMessage message = new MimeMessage(mailSession);
            message.setFrom(new InternetAddress(from));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            message.setSubject(subject);
            message.setContent(body, "text/plain");
            Transport.send(message);
            LOG.debug("Sent message successfully to " + to);
        } catch (MessagingException mex) {
            mex.printStackTrace();
        }
    }

}
