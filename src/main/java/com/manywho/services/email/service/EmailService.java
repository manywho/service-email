package com.manywho.services.email.service;

import com.manywho.sdk.services.types.system.$File;
import com.manywho.services.email.actions.SendEmail;
import com.manywho.services.email.managers.FileManager;
import com.manywho.services.email.types.Contact;
import org.codemonkey.simplejavamail.Email;
import org.codemonkey.simplejavamail.Mailer;
import javax.inject.Inject;
import javax.mail.Message;
import javax.mail.util.ByteArrayDataSource;
import java.io.IOException;

public class EmailService {

    private FileManager fileManager;

    @Inject
    public EmailService(FileManager fileManager){
        this.fileManager = fileManager;
    }

    public Email createEmail(SendEmail sendEmail) throws IOException {
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
                email.addAttachment(file.getName(), new ByteArrayDataSource(fileManager.downloadFile(file.getId()), file.getMimeType()));
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
    
    public void sendEmail(Mailer mailer, Email email) {
        mailer.sendMail(email);
    }
}
