package com.manywho.services.email.email;

import com.manywho.sdk.api.InvokeType;
import com.manywho.sdk.api.run.elements.config.ServiceRequest;
import com.manywho.sdk.services.actions.ActionCommand;
import com.manywho.sdk.services.actions.ActionResponse;
import com.manywho.services.email.ApplicationConfiguration;
import org.simplejavamail.MailException;
import org.simplejavamail.email.Email;

import javax.inject.Inject;

public class SendEmailSimpleCommand implements ActionCommand<ApplicationConfiguration, SendEmailSimple, SendEmailSimple, Void> {
    private final EmailFactory emailFactory;
    private final MailerFactory mailerFactory;

    @Inject
    public SendEmailSimpleCommand(EmailFactory emailFactory, MailerFactory mailerFactory) {
        this.emailFactory = emailFactory;
        this.mailerFactory = mailerFactory;
    }

    @Override
    public ActionResponse<Void> execute(ApplicationConfiguration configuration, ServiceRequest request, SendEmailSimple inputs) {
        boolean isAsync = true;

        // If the flow is being run in debug mode, then we want to send the email synchronously
        if (request.isDebugMode()) {
            isAsync = false;
        }

        Email email = emailFactory.createEmailSimple(inputs, configuration.getUsername());

        try {
            mailerFactory.createMailer(configuration)
                    .sendMail(email, isAsync);
        } catch (MailException e) {
            throw new RuntimeException("Unable to send email: " + e.getCause().getMessage(), e);
        }

        return new ActionResponse<>(InvokeType.Forward);
    }
}
