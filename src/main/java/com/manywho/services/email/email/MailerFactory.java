package com.manywho.services.email.email;

import com.google.common.base.Strings;
import com.manywho.services.email.ApplicationConfiguration;
import org.simplejavamail.api.mailer.Mailer;
import org.simplejavamail.mailer.MailerBuilder;
import org.simplejavamail.api.mailer.config.TransportStrategy;

public class MailerFactory {

    public Mailer createMailer(ApplicationConfiguration configuration) {
        TransportStrategy transportStrategy;

        switch (configuration.getTransport()) {
            case "plain":
                transportStrategy = TransportStrategy.SMTP;
                break;
            case "ssl":
                transportStrategy = TransportStrategy.SMTPS;
                break;
            case "tls":
            default:
                transportStrategy = TransportStrategy.SMTP_TLS;
                break;
        }

        if (Strings.isNullOrEmpty(configuration.getUsername()) && Strings.isNullOrEmpty(configuration.getPassword())) {
            return MailerBuilder
                    .withSMTPServer(
                            configuration.getHost(),
                            configuration.getPort()
                    )
                    .withTransportStrategy(transportStrategy)
                    .buildMailer();
        }

        return MailerBuilder
                .withSMTPServer(
                        configuration.getHost(),
                        configuration.getPort(),
                        configuration.getUsername(),
                        configuration.getPassword()
                )
                .withTransportStrategy(transportStrategy)
                .buildMailer();
    }
}