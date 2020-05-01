package com.manywho.services.email.email;

import com.manywho.services.email.ApplicationConfiguration;
import org.simplejavamail.MailException;
import org.simplejavamail.api.email.Email;
import org.simplejavamail.api.mailer.AsyncResponse;
import org.simplejavamail.api.mailer.Mailer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.inject.Inject;

public class EmailManager {
    private final MailerFactory mailerFactory;
    final static Logger logger = LoggerFactory.getLogger(EmailManager.class);

    @Inject
    public EmailManager(MailerFactory mailerFactory) {
        this.mailerFactory = mailerFactory;
    }

    public void send(ApplicationConfiguration configuration, Email email, boolean isDebugMode) {
        boolean sendAsynchronously = true;

        // If the flow is being run in debug mode, then we want to send the email synchronously
        if (isDebugMode) {
            sendAsynchronously = false;
        }

        Mailer mailer = mailerFactory.createMailer(configuration);

        try {
            AsyncResponse asyncTestConnection = mailer.testConnection(sendAsynchronously);
            if (asyncTestConnection != null) {
                asyncTestConnection.onException((e) -> {
                    logger.error("Connection error: {}", e.getMessage(), e);
                });
            }

            AsyncResponse asyncSendMail = mailer.sendMail(email, sendAsynchronously);
            if (asyncSendMail != null) {
                asyncSendMail.onException((e) -> {
                    logger.error("Send mail error: {}", e.getMessage(), e);
                });
            }

        } catch (MailException e) {
            throw new RuntimeException("Unable to send email: " + e.getMessage(), e);
        }
    }
}