package com.manywho.services.email.email;

import com.google.common.base.Strings;
import com.manywho.services.email.ApplicationConfiguration;
import com.manywho.services.email.email.attachments.EmailAttachmentManager;
import com.manywho.services.email.types.Contact;
import com.manywho.services.email.types.DecisionChoice;
import org.simplejavamail.email.Email;

import javax.inject.Inject;
import javax.mail.Message;
import javax.mail.util.ByteArrayDataSource;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

public class EmailFactory {

    private UriInfo uriInfo;
    private final EmailAttachmentManager attachmentManager;
    private Base64.Encoder encoder;


    @Inject
    public EmailFactory(EmailAttachmentManager attachmentManager, @Context UriInfo uriInfo) {
        this.attachmentManager = attachmentManager;
        this.uriInfo = uriInfo;
        encoder = Base64.getUrlEncoder().withoutPadding();
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

    Email createEmailDecisionRequest(ApplicationConfiguration configuration, Contact contact, SendEmailDecisionRequest.Input sendEmail, String token) {
        final Email email = new Email();

        email.addRecipient(contact.getName(), contact.getEmail(), Message.RecipientType.TO);

        if (sendEmail.getFiles() != null) {
            // If we're given any files, we retrieve each of them from the attachment store and attach them to the email
            sendEmail.getFiles().stream()
                    .map(file -> attachmentManager.fetchAttachment(configuration, file))
                    .forEach(file -> email.addAttachment(file.getName(), new ByteArrayDataSource(file.getFileData(), file.getMimeType())));
        }

        StringBuilder bodyHtml = new StringBuilder();

        if (sendEmail.getHtmlBody() != null) {
            bodyHtml.append(sendEmail.getBody());
        }

        addLinkForChoices(bodyHtml, sendEmail.getDecisionChoices(), token);

        if (bodyHtml.toString().isEmpty() == false) {
            email.setTextHTML(bodyHtml.toString());
        }

        email.setFromAddress(sendEmail.getFrom().getName(), sendEmail.getFrom().getEmail());
        email.setSubject(sendEmail.getSubject());
        email.setText(sendEmail.getBody());

        return email;
    }

    private void addLinkForChoices(StringBuilder stringBuilder, List<DecisionChoice> decisionChoices, String token) {
        URI uri = uriInfo.getBaseUri();

        if (decisionChoices.isEmpty() == false) {
            stringBuilder.append("<br/><br/>");
        }

        for (DecisionChoice response: decisionChoices) {

            String encodeTag = encoder.encodeToString(response.getLabel().getBytes());

            String link = String.format("<a href=\"%scallback/response/%s/%s\"> %s </a> &nbsp;", uri.toString(),
                    token, encodeTag, response.getLabel());

            stringBuilder.append(link);
        }
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
