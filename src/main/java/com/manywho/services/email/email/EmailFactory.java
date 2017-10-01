package com.manywho.services.email.email;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.google.common.base.Strings;
import com.manywho.sdk.services.types.system.$File;
import com.manywho.services.email.configuration.ServiceConfiguration;
import com.manywho.services.email.types.Contact;
import org.simplejavamail.email.Email;

import javax.inject.Inject;
import javax.mail.Message;
import javax.mail.util.ByteArrayDataSource;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EmailFactory {

    private final AmazonS3 amazonS3;
    private final ServiceConfiguration serviceConfiguration;

    @Inject
    public EmailFactory(AmazonS3 amazonS3, ServiceConfiguration serviceConfiguration) {
        this.amazonS3 = amazonS3;
        this.serviceConfiguration = serviceConfiguration;
    }

    Email createEmail(SendEmail sendEmail) {
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
            for ($File file : sendEmail.getFiles()) {
                try {
                    email.addAttachment(file.getName(), new ByteArrayDataSource(downloadFile(file.getId()), file.getMimeType()));
                } catch (IOException e) {
                    throw new RuntimeException("Unable to add attachment", e);
                }
            }
        }

        if (sendEmail.getHtmlBody() != null) {
            email.setTextHTML(sendEmail.getHtmlBody());
        }

        email.setFromAddress(sendEmail.getFrom().getName(), sendEmail.getFrom().getEmail());
        email.setSubject(sendEmail.getSubject());
        email.setText(sendEmail.getBody());

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

    private InputStream downloadFile(String key) {
        S3Object object = amazonS3.getObject(new GetObjectRequest(
                serviceConfiguration.get("s3.bucket_name"),
                key
        ));

        return object.getObjectContent();
    }
}
