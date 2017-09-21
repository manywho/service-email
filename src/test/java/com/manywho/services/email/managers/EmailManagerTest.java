package com.manywho.services.email.managers;

import com.manywho.services.email.actions.SendEmail;
import com.manywho.services.email.actions.SendEmailSimple;
import com.manywho.services.email.entities.Configuration;
import com.manywho.services.email.factories.EmailFactory;
import com.manywho.services.email.service.EmailService;
import com.manywho.services.email.types.Contact;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import com.manywho.sdk.services.types.system.$File;
import org.simplejavamail.email.Email;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class EmailManagerTest {

    @Mock EmailService emailService;
    @Mock FileManager fileManager;
    @Mock Configuration configuration;
    @Mock SendEmail sendEmail;
    @Mock SendEmailSimple sendEmailSimple;
    @Mock Email email;
    @Mock Contact contact;
    EmailFactory emailFactory;
    EmailManager emailManager;

    @Before
    public void setup() throws IOException {
        emailFactory = spy(new EmailFactory(fileManager));
        emailManager = spy(new EmailManager(emailService, emailFactory, fileManager));
        when(sendEmail.getFrom()).thenReturn(contact);
        doReturn(email).when(emailFactory).createEmailSimple(any(SendEmailSimple.class), anyString());
    }

    @Test
    public void sendEmailUsingBoxAttachment() throws Exception {
        $File file = Mockito.mock($File.class);
        List<$File> files = Arrays.asList(file, file);
        when(sendEmail.getFiles()).thenReturn(files);
        when(configuration.getUseBoxForAttachment()).thenReturn(true);
        emailManager.sendEmail(configuration, sendEmail, false);
        verify(emailFactory).createEmail(configuration, sendEmail);
        verify(emailService).sendMail(anyBoolean(), any(Configuration.class), any(Email.class));
        verify(fileManager, times(2)).downloadFile(any(configuration.getClass()), anyString());
        verify(fileManager, times(0)).deleteFiles(any(Configuration.class), any(List.class));
    }

    @Test
    public void sendEmailUsingS3Attachment() throws Exception {
        $File file = Mockito.mock($File.class);
        List<$File> files = Arrays.asList(file, file);
        when(sendEmail.getFiles()).thenReturn(files);
        when(configuration.getUseBoxForAttachment()).thenReturn(false);
        emailManager.sendEmail(configuration, sendEmail, false);
        verify(emailFactory).createEmail(configuration, sendEmail);
        verify(emailService).sendMail(anyBoolean(), any(Configuration.class), any(Email.class));
        verify(fileManager, times(2)).downloadFile(any(configuration.getClass()), anyString());
        verify(fileManager).deleteFiles(any(Configuration.class), any(List.class));
    }

    @Test
    public void sendEmailSimple() throws Exception {
        emailManager.sendEmailSimple(configuration, sendEmailSimple, false);
        verify(emailService).sendMail(false, configuration, email);
    }

}