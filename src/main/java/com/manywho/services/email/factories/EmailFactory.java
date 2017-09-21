package com.manywho.services.email.factories;

import com.manywho.sdk.services.types.system.$File;
import com.manywho.services.email.actions.SendEmail;
import com.manywho.services.email.actions.SendEmailSimple;
import com.manywho.services.email.dtos.FileDownload;
import com.manywho.services.email.entities.Configuration;
import com.manywho.services.email.managers.FileManager;
import com.manywho.services.email.types.Contact;
import org.apache.commons.lang3.StringUtils;
import org.simplejavamail.email.Email;
import javax.inject.Inject;
import javax.mail.Message;
import javax.mail.util.ByteArrayDataSource;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class EmailFactory {

    private FileManager fileManager;

    @Inject
    public EmailFactory(FileManager fileManager){
        this.fileManager = fileManager;
    }

    public Email createEmail(Configuration configuration, SendEmail sendEmail) throws IOException {
        final Email email = new Email();

        for (Contact c : sendEmail.getTo()) {
            email.addRecipient(c.getName(), c.getEmail(), Message.RecipientType.TO);
        }

        if(sendEmail.getCc() != null) {
            for (Contact c : sendEmail.getCc()) {
                email.addRecipient(c.getName(), c.getEmail(), Message.RecipientType.CC);
            }
        }

        if(sendEmail.getBcc() != null) {
            for (Contact c : sendEmail.getBcc()) {
                email.addRecipient(c.getName(), c.getEmail(), Message.RecipientType.BCC);
            }
        }

        if(sendEmail.getFiles() != null) {
            for ($File file : sendEmail.getFiles()) {
                FileDownload fileDownload = fileManager.downloadFile(configuration, file.getId());
                if (Objects.nonNull(fileDownload)) {
                    try (InputStream attachment = fileDownload.getFileInput()) {
                        email.addAttachment(file.getName(), new ByteArrayDataSource(attachment, fileDownload.getMimeType()));
                    }
                }
            }
        }

        if(sendEmail.getHtmlBody() != null) {
            email.setTextHTML(sendEmail.getHtmlBody());
        }

        email.setFromAddress(sendEmail.getFrom().getName(), sendEmail.getFrom().getEmail());
        email.setSubject(sendEmail.getSubject());
        email.setText(sendEmail.getBody());

        return email;
    }

    public Email createEmailSimple(SendEmailSimple sendEmail, String defaultFrom) {
        final Email email = new Email();
        List<String> toList = new ArrayList<>(Arrays.asList(sendEmail.getTo().split(";")));

        for (String to:toList) {
            email.addRecipient(sendEmail.getTo(), to, Message.RecipientType.TO);
        }

        if(StringUtils.isEmpty(sendEmail.getFrom())) {
            email.setFromAddress(sendEmail.getFrom(), defaultFrom);
        }else {
            email.setFromAddress(sendEmail.getFrom(), sendEmail.getFrom());
        }

        email.setSubject(sendEmail.getSubject());
        email.setTextHTML(sendEmail.getBody());

        return email;
    }
}
