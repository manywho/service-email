package com.manywho.services.email.factories;

import com.manywho.services.email.entities.Configuration;
import org.codemonkey.simplejavamail.Mailer;
import org.codemonkey.simplejavamail.TransportStrategy;

public class MailerFactory {

    public Mailer createMailer(Configuration configuration) {
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