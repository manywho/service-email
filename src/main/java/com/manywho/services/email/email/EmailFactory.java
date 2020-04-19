package com.manywho.services.email.email;

import com.google.common.base.Strings;
import com.google.common.net.UrlEscapers;
import com.manywho.sdk.api.run.elements.map.OutcomeAvailable;
import com.manywho.sdk.api.run.elements.map.OutcomeResponse;
import com.manywho.services.email.ApplicationConfiguration;
import com.manywho.services.email.email.attachments.EmailAttachmentManager;
import com.manywho.services.email.email.decisions.DecisionBodyBuilder;
import com.manywho.services.email.email.decisions.SendEmailDecisionRequest;
import com.manywho.services.email.types.Contact;
import org.simplejavamail.email.Email;

import javax.inject.Inject;
import javax.mail.Message;
import javax.mail.util.ByteArrayDataSource;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class EmailFactory {

    private final EmailAttachmentManager attachmentManager;
    private final DecisionBodyBuilder decisionBodyBuilder;


    @Inject
    public EmailFactory(EmailAttachmentManager attachmentManager, DecisionBodyBuilder decisionBodyBuilder) {
        this.attachmentManager = attachmentManager;
        this.decisionBodyBuilder = decisionBodyBuilder;
    }

    Email createEmail(ApplicationConfiguration configuration, SendEmail sendEmail) {
        final Email email = new Email();

        for (Contact contact : sendEmail.getTo()) {
            email.addRecipient(contact.getName(), contact.getEmail(), Message.RecipientType.TO);
        }

        if (sendEmail.getCc() != null) {
            for (Contact contact : sendEmail.getCc()) {
                email.addRecipient(contact.getName(), contact.getEmail(), Message.RecipientType.CC);
            }
        }

        if (sendEmail.getBcc() != null) {
            for (Contact contact : sendEmail.getBcc()) {
                email.addRecipient(contact.getName(), contact.getEmail(), Message.RecipientType.BCC);
            }
        }

        if (sendEmail.getFiles() != null) {
            // If we're given any files, we retrieve each of them from the attachment store and attach them to the email
            sendEmail.getFiles().stream()
                    .map(file -> attachmentManager.fetchAttachment(configuration, file))
                    .forEach(file -> email.addAttachment(file.getName(), new ByteArrayDataSource(file.getFileData(), file.getMimeType())));
        }

        if (sendEmail.getHtmlBody() != null) {
            email.setTextHTML(sendEmail.getHtmlBody());
        }

        email.setFromAddress(sendEmail.getFrom().getName(), sendEmail.getFrom().getEmail());
        email.setSubject(sendEmail.getSubject());
        email.setText(sendEmail.getBody());

        return email;
    }

    public Email createEmailDecisionRequest(ApplicationConfiguration configuration, Contact contact, SendEmailDecisionRequest.Input sendEmail, UUID token, List<OutcomeResponse> outcomes) {
        final Email email = new Email();

        email.addRecipient(contact.getName(), contact.getEmail(), Message.RecipientType.TO);

        if (sendEmail.getFiles() != null) {
            // If we're given any files, we retrieve each of them from the attachment store and attach them to the email
            sendEmail.getFiles().stream()
                    .map(file -> attachmentManager.fetchAttachment(configuration, file))
                    .forEach(file -> email.addAttachment(file.getName(), new ByteArrayDataSource(file.getFileData(), file.getMimeType())));
        }

        String bodyHtml = decisionBodyBuilder.bodyBuilderHtml(sendEmail.getHtmlBody(), outcomes, token);

        if (bodyHtml != null) {
            email.setTextHTML(bodyHtml);
        }

        String bodyText = decisionBodyBuilder.bodyBuilderText(sendEmail.getBody(), outcomes, token);

        if (bodyText != null) {
            email.setText(bodyText);
        }

        email.setFromAddress(sendEmail.getFrom().getName(), sendEmail.getFrom().getEmail());
        email.setSubject(sendEmail.getSubject());

        return email;
    }


    Email createEmailSimple(SendEmailSimple sendEmail, String defaultFrom) {
        final Email email = new Email();

        List<String> toList = new ArrayList<>(Arrays.asList(sendEmail.getTo().split(";")));

        for (String to : toList) {
            email.addRecipient(sendEmail.getTo(), to, Message.RecipientType.TO);
        }

        if (Strings.isNullOrEmpty(sendEmail.getFrom())) {
            email.setFromAddress(sendEmail.getFrom(), defaultFrom);
        } else {
            email.setFromAddress(sendEmail.getFrom(), sendEmail.getFrom());
        }

        email.setSubject(sendEmail.getSubject());
        email.setTextHTML(sendEmail.getBody());

        return email;
    }
}
