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
import org.simplejavamail.email.Email;

import javax.inject.Inject;

public class SendEmailCommand implements ActionCommand<ApplicationConfiguration, SendEmail, SendEmail, Void> {
    private final EmailManager emailManager;
    private final EmailFactory emailFactory;
    private final AmazonS3 amazonS3;
    private final ServiceConfiguration serviceConfiguration;

    @Inject
    public SendEmailCommand(EmailManager emailManager, EmailFactory emailFactory, AmazonS3 amazonS3, ServiceConfiguration serviceConfiguration) {
        this.emailManager = emailManager;
        this.emailFactory = emailFactory;
        this.amazonS3 = amazonS3;
        this.serviceConfiguration = serviceConfiguration;
    }

    @Override
    public ActionResponse<Void> execute(ApplicationConfiguration configuration, ServiceRequest request, SendEmail inputs) {
        Email email = emailFactory.createEmail(configuration, inputs);

        emailManager.send(configuration, email, request.isDebugMode());

        // Clean up any attached files
        if (configuration.getAttachmentSource().equals(ApplicationConfiguration.AttachmentSource.Default)) {
            for ($File attachment : inputs.getFiles()) {
                amazonS3.deleteObject(new DeleteObjectRequest(
                        serviceConfiguration.get("s3.bucket"),
                        attachment.getId()
                ));
            }
        }

        return new ActionResponse<>(InvokeType.Forward);
    }
}
