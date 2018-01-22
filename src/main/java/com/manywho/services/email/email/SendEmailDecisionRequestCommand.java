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
import com.manywho.services.email.persistence.CacheManager;
import com.manywho.services.email.persistence.entities.EmailDecisionRequest;
import com.manywho.services.email.types.Contact;
import org.simplejavamail.email.Email;
import javax.inject.Inject;
import java.util.UUID;

public class SendEmailDecisionRequestCommand implements ActionCommand<ApplicationConfiguration, SendEmailDecisionRequest,
        SendEmailDecisionRequest.Input, SendEmailDecisionRequest.Output> {

    private final EmailManager emailManager;
    private final EmailFactory emailFactory;
    private final AmazonS3 amazonS3;
    private final ServiceConfiguration serviceConfiguration;
    private CacheManager cacheManager;

    @Inject
    public SendEmailDecisionRequestCommand(EmailManager emailManager, EmailFactory emailFactory, AmazonS3 amazonS3,
                                           ServiceConfiguration serviceConfiguration, CacheManager cacheManager) {
        this.emailManager = emailManager;
        this.emailFactory = emailFactory;
        this.amazonS3 = amazonS3;
        this.serviceConfiguration = serviceConfiguration;
        this.cacheManager = cacheManager;
    }

    @Override
    public ActionResponse<SendEmailDecisionRequest.Output> execute(ApplicationConfiguration configuration, ServiceRequest request, SendEmailDecisionRequest.Input inputs) {
        for (Contact to:inputs.getTo()) {
            UUID uuid = UUID.randomUUID();
            String uuidString = uuid.toString();

            Email email = emailFactory.createEmailDecisionRequest(configuration, to, inputs, uuidString);
            emailManager.send(configuration, email, request.isDebugMode());

            EmailDecisionRequest emailRequest = new EmailDecisionRequest(request.getTenantId().toString(),
                    request.getToken(), to.getEmail(), inputs.getDecisionChoices());

            cacheManager.saveEmailDecisionRequest(uuidString, emailRequest);
        }

        // Clean up any attached files
        if (configuration.getAttachmentSource().equals(ApplicationConfiguration.AttachmentSource.Default)) {
            for ($File attachment : inputs.getFiles()) {
                amazonS3.deleteObject(new DeleteObjectRequest(
                        serviceConfiguration.get("s3.bucket"),
                        attachment.getId()
                ));
            }
        }

        if (inputs.getDecisionChoices().isEmpty() == false) {
            return new ActionResponse<>(InvokeType.Wait);
        }

        return new ActionResponse<>(InvokeType.Forward);
    }
}
