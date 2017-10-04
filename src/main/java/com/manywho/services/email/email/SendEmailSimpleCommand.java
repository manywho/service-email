package com.manywho.services.email.email;

import com.manywho.sdk.api.InvokeType;
import com.manywho.sdk.api.run.elements.config.ServiceRequest;
import com.manywho.sdk.services.actions.ActionCommand;
import com.manywho.sdk.services.actions.ActionResponse;
import com.manywho.services.email.ApplicationConfiguration;
import org.simplejavamail.email.Email;

import javax.inject.Inject;

public class SendEmailSimpleCommand implements ActionCommand<ApplicationConfiguration, SendEmailSimple, SendEmailSimple, Void> {
    private final EmailFactory emailFactory;
    private final EmailManager emailManager;

    @Inject
    public SendEmailSimpleCommand(EmailFactory emailFactory, EmailManager emailManager) {
        this.emailFactory = emailFactory;
        this.emailManager = emailManager;
    }

    @Override
    public ActionResponse<Void> execute(ApplicationConfiguration configuration, ServiceRequest request, SendEmailSimple inputs) {
        Email email = emailFactory.createEmailSimple(inputs, configuration.getUsername());

        emailManager.send(configuration, email, request.isDebugMode());

        return new ActionResponse<>(InvokeType.Forward);
    }
}
