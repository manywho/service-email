package com.manywho.services.email.email;

import com.manywho.services.email.ApplicationConfiguration;
import org.simplejavamail.MailException;
import org.simplejavamail.api.email.Email;

import javax.inject.Inject;

public class EmailManager {
    private final MailerFactory mailerFactory;

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

        try {
            mailerFactory.createMailer(configuration)
                    .sendMail(email, sendAsynchronously);
        } catch (MailException e) {
            throw new RuntimeException("Unable to send email: " + e.getMessage(), e);
        }
    }
}