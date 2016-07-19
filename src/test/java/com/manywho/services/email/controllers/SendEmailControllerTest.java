package com.manywho.services.email.controllers;

import com.manywho.sdk.utils.AuthorizationUtils;
import com.manywho.services.email.test.EmailServiceFunctionalTest;
import org.codemonkey.simplejavamail.Email;
import org.codemonkey.simplejavamail.Mailer;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import javax.mail.Session;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import static java.lang.Thread.sleep;
import static junit.framework.TestCase.assertEquals;
import static org.mockito.Mockito.*;

public class SendEmailControllerTest extends EmailServiceFunctionalTest {
    @Test
    public void testSendEmailWithoutAttachments() throws Exception {
        MultivaluedMap<String,Object> headers = new MultivaluedHashMap<>();
        headers.add("Authorization", AuthorizationUtils.serialize(getDefaultAuthenticatedWho()));

        when(emailServiceMock.createEmail(any())).thenCallRealMethod();

        Response responseMsg = target("/actions/email").request()
                .headers(headers)
                .post(getServerRequestFromFile("SendEmailController/basic/request-send-email.json"));

        //check the response is right
        assertJsonSame(
                getJsonFormatFileContent("SendEmailController/basic/response-send-email.json"),
                getJsonFormatResponse(responseMsg)
        );

        ArgumentCaptor<Email> argumentCaptorEmail = ArgumentCaptor.forClass(Email.class);
        ArgumentCaptor<Mailer> argumentCaptorMailer = ArgumentCaptor.forClass(Mailer.class);

        // before verify the call to send email, we need to give some time to the threat in background to finish the task
        sleepUntilThreadFinish();

        // check mailer configuration and email parameters, but don't send the email
        verify(emailServiceMock).sendEmail(argumentCaptorMailer.capture(), argumentCaptorEmail.capture());
        Session session = argumentCaptorMailer.getValue().getSession();
        assertEquals("587", session.getProperty("mail.smtp.port"));
        assertEquals("smtp", session.getProperty("mail.transport.protocol"));
        assertEquals("smtp.example.com", session.getProperty("mail.smtp.host"));
        assertEquals("test@mailaccount.com", session.getProperty("mail.smtp.username"));

        assertEquals("email text body", argumentCaptorEmail.getValue().getText());
        assertEquals(
                "<html><title><body>Hello World!</body></title></html>",
                argumentCaptorEmail.getValue().getTextHTML()
        );
        assertEquals("Test Subject", argumentCaptorEmail.getValue().getSubject());

        assertEquals("Test ManyWho", argumentCaptorEmail.getValue().getFromRecipient().getName());
        assertEquals("test@manywho.com", argumentCaptorEmail.getValue().getFromRecipient().getAddress());

        assertEquals("Test To", argumentCaptorEmail.getValue().getRecipients().get(0).getName());
        assertEquals("to@manywho.com", argumentCaptorEmail.getValue().getRecipients().get(0).getAddress());

        assertEquals("Cc Manywho", argumentCaptorEmail.getValue().getRecipients().get(1).getName());
        assertEquals("cc@manywho.com", argumentCaptorEmail.getValue().getRecipients().get(1).getAddress());

        assertEquals("Bcc ManyWho", argumentCaptorEmail.getValue().getRecipients().get(2).getName());
        assertEquals("bcc@manywho.com", argumentCaptorEmail.getValue().getRecipients().get(2).getAddress());

        assertEquals(3, argumentCaptorEmail.getValue().getRecipients().size());

        verify(fileManagerMock).deleteFiles(any());
    }

    @Test
    public void testSendEmailWithNullsInCcAndBcc() throws Exception {
        MultivaluedMap<String,Object> headers = new MultivaluedHashMap<>();
        headers.add("Authorization", AuthorizationUtils.serialize(getDefaultAuthenticatedWho()));

        when(emailServiceMock.createEmail(any())).thenCallRealMethod();

        Response responseMsg = target("/actions/email").request()
                .headers(headers)
                .post(getServerRequestFromFile("SendEmailController/supportnull/request-send-email.json"));

        //check the response is right
        assertJsonSame(
                getJsonFormatFileContent("SendEmailController/supportnull/response-send-email.json"),
                getJsonFormatResponse(responseMsg)
        );

        ArgumentCaptor<Email> argumentCaptorEmail = ArgumentCaptor.forClass(Email.class);
        ArgumentCaptor<Mailer> argumentCaptorMailer = ArgumentCaptor.forClass(Mailer.class);

        // before verify the call to send email, we need to give some time to the threat in background to finish the task
        sleepUntilThreadFinish();

        // check mailer configuration and email parameters, but don't send the email
        verify(emailServiceMock).sendEmail(argumentCaptorMailer.capture(), argumentCaptorEmail.capture());
        Session session = argumentCaptorMailer.getValue().getSession();
        assertEquals("587", session.getProperty("mail.smtp.port"));
        assertEquals("smtp", session.getProperty("mail.transport.protocol"));
        assertEquals("smtp.example.com", session.getProperty("mail.smtp.host"));
        assertEquals("test@mailaccount.com", session.getProperty("mail.smtp.username"));

        assertEquals("email text body", argumentCaptorEmail.getValue().getText());
        assertEquals(
                "<html><title><body>Hello World!</body></title></html>",
                argumentCaptorEmail.getValue().getTextHTML()
        );
        assertEquals("Test Subject", argumentCaptorEmail.getValue().getSubject());

        assertEquals("Test ManyWho", argumentCaptorEmail.getValue().getFromRecipient().getName());
        assertEquals("test@manywho.com", argumentCaptorEmail.getValue().getFromRecipient().getAddress());

        assertEquals("Test To", argumentCaptorEmail.getValue().getRecipients().get(0).getName());
        assertEquals("to@manywho.com", argumentCaptorEmail.getValue().getRecipients().get(0).getAddress());

        assertEquals(1, argumentCaptorEmail.getValue().getRecipients().size());

        verify(fileManagerMock).deleteFiles(any());
    }

    // todo we should find a way to wait until the thread finish without use sleep
    private void sleepUntilThreadFinish() throws InterruptedException {
        sleep(1000);
    }

    @Test
    public void testSendEmailSimple() throws Exception {
        MultivaluedMap<String,Object> headers = new MultivaluedHashMap<>();
        headers.add("Authorization", AuthorizationUtils.serialize(getDefaultAuthenticatedWho()));

        when(emailServiceMock.createEmailSimple(any(), any())).thenCallRealMethod();

        Response responseMsg = target("/actions/email-simple").request()
                .headers(headers)
                .post(getServerRequestFromFile("SendEmailController/simple/request-send-simple-email.json"));

        //check the response is right
        assertJsonSame(
                getJsonFormatFileContent("SendEmailController/simple/response-send-simple-email.json"),
                getJsonFormatResponse(responseMsg)
        );

        ArgumentCaptor<Email> argumentCaptorEmail = ArgumentCaptor.forClass(Email.class);
        ArgumentCaptor<Mailer> argumentCaptorMailer = ArgumentCaptor.forClass(Mailer.class);

        // before verify the call to send email, we need to give some time to the threat in background to finish the task
        sleepUntilThreadFinish();

        // check mailer configuration and email parameters, but don't send the email
        verify(emailServiceMock).sendEmail(argumentCaptorMailer.capture(), argumentCaptorEmail.capture());
        Session session = argumentCaptorMailer.getValue().getSession();
        assertEquals("587", session.getProperty("mail.smtp.port"));
        assertEquals("smtp", session.getProperty("mail.transport.protocol"));
        assertEquals("smtp.gmail.com", session.getProperty("mail.smtp.host"));
        assertEquals("test@mailaccount.com", session.getProperty("mail.smtp.username"));

        assertEquals("Email body", argumentCaptorEmail.getValue().getText());
        assertEquals("Email subject", argumentCaptorEmail.getValue().getSubject());

        assertEquals("test@mailaccount.com", argumentCaptorEmail.getValue().getFromRecipient().getName());
        assertEquals("test@mailaccount.com", argumentCaptorEmail.getValue().getFromRecipient().getAddress());

        assertEquals("test1@mailaccount.com;test2@mailaccount.com", argumentCaptorEmail.getValue().getRecipients().get(0).getName());
        assertEquals("test1@mailaccount.com", argumentCaptorEmail.getValue().getRecipients().get(0).getAddress());
        assertEquals("test2@mailaccount.com", argumentCaptorEmail.getValue().getRecipients().get(1).getAddress());

        assertEquals(2, argumentCaptorEmail.getValue().getRecipients().size());
    }


    @Test
    public void testSendEmailSimpleDebug() throws Exception {
        MultivaluedMap<String,Object> headers = new MultivaluedHashMap<>();
        headers.add("Authorization", AuthorizationUtils.serialize(getDefaultAuthenticatedWho()));

        when(emailServiceMock.createEmailSimple(any(), any())).thenCallRealMethod();

        Response responseMsg = target("/actions/email-simple").request()
                .headers(headers)
                .post(getServerRequestFromFile("SendEmailController/simple-debug/request-send-simple-email.json"));

        //check the response is right
        assertJsonSame(
                getJsonFormatFileContent("SendEmailController/simple-debug/response-send-simple-email.json"),
                getJsonFormatResponse(responseMsg)
        );

        ArgumentCaptor<Email> argumentCaptorEmail = ArgumentCaptor.forClass(Email.class);
        ArgumentCaptor<Mailer> argumentCaptorMailer = ArgumentCaptor.forClass(Mailer.class);

        // before verify the call to send email, we DO NOT need to give some time to the threat in background to finish the task

        // check mailer configuration and email parameters, but don't send the email
        verify(emailServiceMock).sendEmail(argumentCaptorMailer.capture(), argumentCaptorEmail.capture());
        Session session = argumentCaptorMailer.getValue().getSession();
        assertEquals("587", session.getProperty("mail.smtp.port"));
        assertEquals("smtp", session.getProperty("mail.transport.protocol"));
        assertEquals("smtp.gmail.com", session.getProperty("mail.smtp.host"));
        assertEquals("test@mailaccount.com", session.getProperty("mail.smtp.username"));

        assertEquals("Email body", argumentCaptorEmail.getValue().getText());
        assertEquals("Email subject", argumentCaptorEmail.getValue().getSubject());

        assertEquals("test@mailaccount.com", argumentCaptorEmail.getValue().getFromRecipient().getName());
        assertEquals("test@mailaccount.com", argumentCaptorEmail.getValue().getFromRecipient().getAddress());

        assertEquals("test1@mailaccount.com;test2@mailaccount.com", argumentCaptorEmail.getValue().getRecipients().get(0).getName());
        assertEquals("test1@mailaccount.com", argumentCaptorEmail.getValue().getRecipients().get(0).getAddress());
        assertEquals("test2@mailaccount.com", argumentCaptorEmail.getValue().getRecipients().get(1).getAddress());

        assertEquals(2, argumentCaptorEmail.getValue().getRecipients().size());
    }
}
