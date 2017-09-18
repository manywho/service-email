package com.manywho.services.email.managers;

import com.manywho.services.email.actions.SendEmail;
import com.manywho.services.email.actions.SendEmailSimple;
import com.manywho.services.email.entities.Configuration;
import com.manywho.services.email.factories.EmailFactory;
import com.manywho.services.email.service.EmailService;

import javax.inject.Inject;
import java.io.IOException;

import static org.apache.commons.lang3.StringUtils.isEmpty;

public class EmailManager {
    private EmailService emailService;
    private EmailFactory emailFactory;
    private FileManager fileManager;

    @Inject
    public EmailManager(EmailService emailService, EmailFactory emailFactory, FileManager fileManager) {
        this.emailService = emailService;
        this.emailFactory = emailFactory;
        this.fileManager = fileManager;
    }

    public void sendEmail(Configuration configuration, SendEmail sendEmail, Boolean async) throws IOException {
        emailService.sendMail(async, configuration, emailFactory.createEmail(configuration, sendEmail));
        if (!configuration.getUseBoxForAttachment() && !configuration.getRetainFiles()) {
            fileManager.deleteFiles(configuration, sendEmail.getFiles());
        }
    }

    public void sendEmailSimple(Configuration configuration, SendEmailSimple sendEmail, Boolean async) {
        emailService.sendMail(async, configuration, emailFactory.createEmailSimple(sendEmail, configuration.getUsername()));
    }
}
