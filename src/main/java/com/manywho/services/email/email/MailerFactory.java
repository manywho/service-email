package com.manywho.services.email.email;

import com.manywho.services.email.ApplicationConfiguration;
import org.simplejavamail.mailer.Mailer;
import org.simplejavamail.mailer.config.TransportStrategy;

public class MailerFactory {

    public Mailer createMailer(ApplicationConfiguration configuration) {
        TransportStrategy transportStrategy;

        switch (configuration.getTransport()) {
            case "plain":
                transportStrategy = TransportStrategy.SMTP_PLAIN;
                break;
            case "ssl":
                transportStrategy = TransportStrategy.SMTP_SSL;
                break;
            case "tls":
            default:
                transportStrategy = TransportStrategy.SMTP_TLS;
                break;
        }

        return new Mailer(
                configuration.getHost(),
                configuration.getPort(),
                configuration.getUsername(),
                configuration.getPassword(),
                transportStrategy
        );
    }
}