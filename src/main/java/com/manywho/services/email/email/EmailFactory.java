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
import org.simplejavamail.api.email.Email;
import org.simplejavamail.api.email.EmailPopulatingBuilder;
import org.simplejavamail.email.EmailBuilder;

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
        final EmailPopulatingBuilder email = EmailBuilder.startingBlank();

        for (Contact contact : sendEmail.getTo()) {
            email.to(contact.getName(), contact.getEmail());
        }

        if (sendEmail.getCc() != null) {
            for (Contact contact : sendEmail.getCc()) {
                email.cc(contact.getName(), contact.getEmail());
            }
        }

        if (sendEmail.getBcc() != null) {
            for (Contact contact : sendEmail.getBcc()) {
                email.bcc(contact.getName(), contact.getEmail());
            }
        }

        if (sendEmail.getFiles() != null) {
            // If we're given any files, we retrieve each of them from the attachment store and attach them to the email
            sendEmail.getFiles().stream()
                    .map(file -> attachmentManager.fetchAttachment(configuration, file))
                    .forEach(file -> email.withAttachment(file.getName(), new ByteArrayDataSource(file.getFileData(), file.getMimeType())));
        }

        if (sendEmail.getHtmlBody() != null) {
            email.withHTMLText(sendEmail.getHtmlBody());
        }

        email.from(sendEmail.getFrom().getName(), sendEmail.getFrom().getEmail());
        email.withSubject(sendEmail.getSubject());
        email.withPlainText(sendEmail.getBody());

        return email.buildEmail();
    }

    public Email createEmailDecisionRequest(ApplicationConfiguration configuration, Contact contact, SendEmailDecisionRequest.Input sendEmail, UUID token, List<OutcomeResponse> outcomes) {
        final EmailPopulatingBuilder email = EmailBuilder.startingBlank();

        email.to(contact.getName(), contact.getEmail());

        if (sendEmail.getFiles() != null) {
            // If we're given any files, we retrieve each of them from the attachment store and attach them to the email
            sendEmail.getFiles().stream()
                    .map(file -> attachmentManager.fetchAttachment(configuration, file))
                    .forEach(file -> email.withAttachment(file.getName(), new ByteArrayDataSource(file.getFileData(), file.getMimeType())));
        }

        String bodyHtml = decisionBodyBuilder.bodyBuilderHtml(sendEmail.getHtmlBody(), outcomes, token);

        if (bodyHtml != null) {
            email.withHTMLText(bodyHtml);
        }

        String bodyText = decisionBodyBuilder.bodyBuilderText(sendEmail.getBody(), outcomes, token);

        if (bodyText != null) {
            email.withPlainText(bodyText);
        }

        email.from(sendEmail.getFrom().getName(), sendEmail.getFrom().getEmail());
        email.withSubject(sendEmail.getSubject());

        return email.buildEmail();
    }


    Email createEmailSimple(SendEmailSimple sendEmail, String defaultFrom) {
        final EmailPopulatingBuilder email = EmailBuilder.startingBlank();

        List<String> toList = new ArrayList<>(Arrays.asList(sendEmail.getTo().split(";")));

        for (String to : toList) {
            email.to(sendEmail.getTo(), to);
        }

        if (Strings.isNullOrEmpty(sendEmail.getFrom())) {
            email.from(sendEmail.getFrom(), defaultFrom);
        } else {
            email.from(sendEmail.getFrom(), sendEmail.getFrom());
        }

        email.withSubject(sendEmail.getSubject());
        email.withHTMLText(sendEmail.getBody());

        return email.buildEmail();
    }
}
