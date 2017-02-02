package com.manywho.services.email.managers;

import com.manywho.services.email.actions.SendEmail;
import com.manywho.services.email.actions.SendEmailSimple;
import com.manywho.services.email.entities.Configuration;
import com.manywho.services.email.factories.EmailFactory;
import com.manywho.services.email.service.EmailService;

import javax.inject.Inject;
import java.io.IOException;

public class EmailManager {
    private FileManager fileManager;
    private EmailService emailService;
    private EmailFactory emailFactory;

    @Inject
    public EmailManager(FileManager fileManager, EmailService emailService, EmailFactory emailFactory) {
        this.fileManager = fileManager;
        this.emailService = emailService;
        this.emailFactory = emailFactory;
    }

    public void sendEmail(Configuration configuration, SendEmail sendEmail, Boolean async) throws IOException {
        emailService.sendMail(async, configuration, emailFactory.createEmail(sendEmail));
        fileManager.deleteFiles(sendEmail.getFiles());
    }

    public void sendEmailSimple(Configuration configuration, SendEmailSimple sendEmail, Boolean async) {
        emailService.sendMail(async, configuration, emailFactory.createEmailSimple(sendEmail, configuration.getUsername()));
    }
}
