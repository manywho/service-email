package com.manywho.services.email.service;

import com.manywho.services.email.entities.Configuration;
import com.manywho.services.email.factories.MailerFactory;
import org.simplejavamail.MailException;
import org.simplejavamail.email.Email;
import javax.inject.Inject;

public class EmailService {
    private MailerFactory mailerFactory;

    @Inject
    public EmailService(MailerFactory mailerFactory) {
        this.mailerFactory = mailerFactory;
    }

    public void sendMail(Boolean async, Configuration configuration, Email emailSimple) {
        try {
            mailerFactory.createMailer(configuration)
                    .sendMail(emailSimple, async);

        } catch (MailException e) {
            throw new RuntimeException(e.getCause().getMessage());
        }
    }
}
