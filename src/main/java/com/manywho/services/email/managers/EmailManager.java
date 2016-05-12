package com.manywho.services.email.managers;

import com.manywho.sdk.utils.ThreadUtils;
import com.manywho.services.email.actions.SendEmail;
import com.manywho.services.email.entities.Configuration;
import com.manywho.services.email.factories.MailerFactory;
import com.manywho.services.email.service.EmailService;
import org.codemonkey.simplejavamail.Mailer;
import javax.inject.Inject;

public class EmailManager {
    private EmailService emailService;
    private FileManager fileManager;
    private MailerFactory mailerFactory;

    @Inject
    public EmailManager(FileManager fileManager, EmailService emailService, MailerFactory mailerFactory) {
        this.fileManager = fileManager;
        this.emailService = emailService;
        this.mailerFactory = mailerFactory;
    }

    public void sendEmailSimple(Configuration configuration, SendEmail sendEmail) {

        Mailer mailer = mailerFactory.createMailer(configuration);

        ThreadUtils.runInBackground(()-> {
            // create email (it will download the attachments from persistence server)
            emailService.sendEmail(mailer, emailService.createEmail(sendEmail));

            // after the email is sent I can remove the attachments from persistence server
            fileManager.deleteFiles(sendEmail.getFiles());
        });
    }
}
