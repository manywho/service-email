package com.manywho.services.email.email;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.manywho.sdk.api.InvokeType;
import com.manywho.sdk.api.run.elements.config.ServiceRequest;
import com.manywho.sdk.services.actions.ActionCommand;
import com.manywho.sdk.services.actions.ActionResponse;
import com.manywho.sdk.services.types.system.$File;
import com.manywho.services.email.ApplicationConfiguration;
import com.manywho.services.email.configuration.ServiceConfiguration;
import org.simplejavamail.MailException;
import org.simplejavamail.email.Email;

import javax.inject.Inject;

public class SendEmailCommand implements ActionCommand<ApplicationConfiguration, SendEmail, SendEmail, Void> {
    private final EmailFactory emailFactory;
    private final MailerFactory mailerFactory;
    private final AmazonS3 amazonS3;
    private final ServiceConfiguration serviceConfiguration;

    @Inject
    public SendEmailCommand(EmailFactory emailFactory, MailerFactory mailerFactory, AmazonS3 amazonS3, ServiceConfiguration serviceConfiguration) {
        this.emailFactory = emailFactory;
        this.mailerFactory = mailerFactory;
        this.amazonS3 = amazonS3;
        this.serviceConfiguration = serviceConfiguration;
    }

    @Override
    public ActionResponse<Void> execute(ApplicationConfiguration configuration, ServiceRequest request, SendEmail inputs) {
        boolean isAsync = true;

        // If the flow is being run in debug mode, then we want to send the email synchronously
        if (request.isDebugMode()) {
            isAsync = false;
        }

        Email email = emailFactory.createEmail(inputs);

        try {
            mailerFactory.createMailer(configuration)
                    .sendMail(email, isAsync);
        } catch (MailException e) {
            throw new RuntimeException("Unable to send email: " + e.getCause().getMessage(), e);
        }

        // Clean up any attached files
        for ($File attachment : inputs.getFiles()) {
            amazonS3.deleteObject(new DeleteObjectRequest(
                    serviceConfiguration.get("s3.bucket_name"),
                    attachment.getId()
            ));
        }

        return new ActionResponse<>(InvokeType.Forward);
    }
}
