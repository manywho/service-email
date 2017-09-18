package com.manywho.services.email.controllers;

import com.manywho.sdk.utils.AuthorizationUtils;
import com.manywho.services.email.dtos.FileDownload;
import com.manywho.services.email.entities.Configuration;
import com.manywho.services.email.test.EmailServiceFunctionalTest;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.simplejavamail.MailException;
import org.simplejavamail.email.Email;

import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.io.ByteArrayInputStream;

import static junit.framework.TestCase.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class SendEmailControllerTest extends EmailServiceFunctionalTest {
    @Test
    public void testSendEmailWithoutAttachments() throws Exception {
        MultivaluedMap<String,Object> headers = new MultivaluedHashMap<>();
        headers.add("Authorization", AuthorizationUtils.serialize(getDefaultAuthenticatedWho()));

        ArgumentCaptor<Configuration> argumentCaptorConfiguration = ArgumentCaptor.forClass(Configuration.class);
        ArgumentCaptor<Email> argumentCaptorEmail = ArgumentCaptor.forClass(Email.class);

        when(mailerFactory.createMailer(argumentCaptorConfiguration.capture())).thenReturn(mailer);

        Response responseMsg = target("/actions/email").request()
                .headers(headers)
                .post(getServerRequestFromFile("SendEmailController/basic/request-send-email.json"));

        //check the response is right
        assertJsonSame(
                getJsonFormatFileContent("SendEmailController/basic/response-send-email.json"),
                getJsonFormatResponse(responseMsg)
        );

        verify(mailer).sendMail(argumentCaptorEmail.capture(), false);

        Configuration capturedConfiguration = argumentCaptorConfiguration.getValue();
        assertEquals(587, Math.toIntExact(capturedConfiguration.getPort()));
        assertEquals("tls", capturedConfiguration.getTransport());
        assertEquals("smtp.example.com", capturedConfiguration.getHost());
        assertEquals("test@mailaccount.com", capturedConfiguration.getUsername());

        Email capturedEmail = argumentCaptorEmail.getValue();

        assertEquals("email text body", capturedEmail.getText());
        assertEquals(
                "<html><title><body>Hello World!</body></title></html>",
                argumentCaptorEmail.getValue().getTextHTML()
        );
        assertEquals("Test Subject", capturedEmail.getSubject());

        assertEquals("Test ManyWho", capturedEmail.getFromRecipient().getName());
        assertEquals("test@manywho.com", capturedEmail.getFromRecipient().getAddress());

        assertEquals("Test To", capturedEmail.getRecipients().get(0).getName());
        assertEquals("to@manywho.com", capturedEmail.getRecipients().get(0).getAddress());

        assertEquals("Cc Manywho", capturedEmail.getRecipients().get(1).getName());
        assertEquals("cc@manywho.com", capturedEmail.getRecipients().get(1).getAddress());

        assertEquals("Bcc ManyWho", capturedEmail.getRecipients().get(2).getName());
        assertEquals("bcc@manywho.com", capturedEmail.getRecipients().get(2).getAddress());

        assertEquals(3, capturedEmail.getRecipients().size());

    }

    @Test
    public void testSendEmailWithNullsInCcAndBcc() throws Exception {
        MultivaluedMap<String,Object> headers = new MultivaluedHashMap<>();
        headers.add("Authorization", AuthorizationUtils.serialize(getDefaultAuthenticatedWho()));

        ArgumentCaptor<Configuration> argumentCaptorConfiguration = ArgumentCaptor.forClass(Configuration.class);
        ArgumentCaptor<Email> argumentCaptorEmail = ArgumentCaptor.forClass(Email.class);

        when(mailerFactory.createMailer(argumentCaptorConfiguration.capture())).thenReturn(mailer);
        FileDownload fileDownload = mock(FileDownload.class);
        when(fileManagerMock.downloadFile(any(Configuration.class), anyString())).thenReturn(fileDownload);
        when(fileDownload.getFileInput()).thenReturn(new ByteArrayInputStream("this is a pdf file".getBytes()));

        Response responseMsg = target("/actions/email").request()
                .headers(headers)
                .post(getServerRequestFromFile("SendEmailController/supportnull/request-send-email.json"));

        //check the response is right
        assertJsonSame(
                getJsonFormatFileContent("SendEmailController/supportnull/response-send-email.json"),
                getJsonFormatResponse(responseMsg)
        );

        verify(mailer).sendMail(argumentCaptorEmail.capture(), false);

        Configuration capturedConfiguration = argumentCaptorConfiguration.getValue();
        assertEquals(587, Math.toIntExact(capturedConfiguration.getPort()));
        assertEquals("tls", capturedConfiguration.getTransport());
        assertEquals("smtp.example.com", capturedConfiguration.getHost());
        assertEquals("test@mailaccount.com", capturedConfiguration.getUsername());

        Email capturedEmail = argumentCaptorEmail.getValue();


        assertEquals("email text body", argumentCaptorEmail.getValue().getText());
        assertEquals(
                "<html><title><body>Hello World!</body></title></html>",
                argumentCaptorEmail.getValue().getTextHTML()
        );
        assertEquals("Test Subject", capturedEmail.getSubject());

        assertEquals("Test ManyWho", capturedEmail.getFromRecipient().getName());
        assertEquals("test@manywho.com", capturedEmail.getFromRecipient().getAddress());

        assertEquals("Test To", capturedEmail.getRecipients().get(0).getName());
        assertEquals("to@manywho.com", capturedEmail.getRecipients().get(0).getAddress());

        assertEquals(1, capturedEmail.getRecipients().size());

    }

    @Test
    public void testSendEmailSimple() throws Exception {
        MultivaluedMap<String,Object> headers = new MultivaluedHashMap<>();
        headers.add("Authorization", AuthorizationUtils.serialize(getDefaultAuthenticatedWho()));

        ArgumentCaptor<Configuration> argumentCaptorConfiguration = ArgumentCaptor.forClass(Configuration.class);
        ArgumentCaptor<Email> argumentCaptorEmail = ArgumentCaptor.forClass(Email.class);

        when(mailerFactory.createMailer(argumentCaptorConfiguration.capture())).thenReturn(mailer);

        Response responseMsg = target("/actions/email-simple").request()
                .headers(headers)
                .post(getServerRequestFromFile("SendEmailController/simple/request-send-simple-email.json"));

        //check the response is right
        assertJsonSame(
                getJsonFormatFileContent("SendEmailController/simple/response-send-simple-email.json"),
                getJsonFormatResponse(responseMsg)
        );


        verify(mailer).sendMail(argumentCaptorEmail.capture(), false);

        Configuration capturedConfiguration = argumentCaptorConfiguration.getValue();
        assertEquals(587, Math.toIntExact(capturedConfiguration.getPort()));
        assertEquals("tls", capturedConfiguration.getTransport());
        assertEquals("smtp.gmail.com", capturedConfiguration.getHost());
        assertEquals("test@mailaccount.com", capturedConfiguration.getUsername());

        Email capturedEmail = argumentCaptorEmail.getValue();

        assertEquals("Email body", capturedEmail.getTextHTML());
        assertEquals("Email subject", capturedEmail.getSubject());

        assertEquals("test@mailaccount.com", capturedEmail.getFromRecipient().getName());
        assertEquals("test@mailaccount.com", capturedEmail.getFromRecipient().getAddress());

        assertEquals("test1@mailaccount.com;test2@mailaccount.com", capturedEmail.getRecipients().get(0).getName());
        assertEquals("test1@mailaccount.com", capturedEmail.getRecipients().get(0).getAddress());
        assertEquals("test2@mailaccount.com", capturedEmail.getRecipients().get(1).getAddress());

        assertEquals(2, argumentCaptorEmail.getValue().getRecipients().size());
    }

    @Test
    public void testSendEmailSimpleDebug() throws Exception {
        MultivaluedMap<String,Object> headers = new MultivaluedHashMap<>();
        headers.add("Authorization", AuthorizationUtils.serialize(getDefaultAuthenticatedWho()));

        ArgumentCaptor<Configuration> argumentCaptorConfiguration = ArgumentCaptor.forClass(Configuration.class);
        ArgumentCaptor<Email> argumentCaptorEmail = ArgumentCaptor.forClass(Email.class);

        when(mailerFactory.createMailer(argumentCaptorConfiguration.capture())).thenReturn(mailer);

        Response responseMsg = target("/actions/email-simple").request()
                .headers(headers)
                .post(getServerRequestFromFile("SendEmailController/simple-debug/request-send-simple-email.json"));

        //check the response is right
        assertJsonSame(
                getJsonFormatFileContent("SendEmailController/simple-debug/response-send-simple-email.json"),
                getJsonFormatResponse(responseMsg)
        );

        verify(mailer).sendMail(argumentCaptorEmail.capture(), true);

        Configuration capturedConfiguration = argumentCaptorConfiguration.getValue();
        assertEquals(587, Math.toIntExact(capturedConfiguration.getPort()));
        assertEquals("tls", capturedConfiguration.getTransport());
        assertEquals("smtp.gmail.com", capturedConfiguration.getHost());
        assertEquals("test@mailaccount.com", capturedConfiguration.getUsername());

        Email capturedEmail = argumentCaptorEmail.getValue();

        assertEquals("Email body", capturedEmail.getTextHTML());
        assertEquals("Email subject", capturedEmail.getSubject());

        assertEquals("test@mailaccount.com", capturedEmail.getFromRecipient().getName());
        assertEquals("test@mailaccount.com", capturedEmail.getFromRecipient().getAddress());

        assertEquals("test1@mailaccount.com;test2@mailaccount.com", capturedEmail.getRecipients().get(0).getName());
        assertEquals("test1@mailaccount.com", capturedEmail.getRecipients().get(0).getAddress());
        assertEquals("test2@mailaccount.com", capturedEmail.getRecipients().get(1).getAddress());

        assertEquals(2, capturedEmail.getRecipients().size());
    }

    @Test
    public void testSendEmailSimpleDebugWithException() throws Exception {
        MultivaluedMap<String,Object> headers = new MultivaluedHashMap<>();
        headers.add("Authorization", AuthorizationUtils.serialize(getDefaultAuthenticatedWho()));

        when(mailerFactory.createMailer(any())).thenReturn(mailer);

        //force an exception
        MailException mailException = new MailException("Third party error", new Exception("error")) {
            @Override
            public String getMessage() {
                return super.getMessage();
            }

            @Override
            public Exception getCause(){
                return new Exception("specific error");
            }
        };

        doThrow(mailException)
                .when(mailer).sendMail(any(), false);

        Response responseMsg = target("/actions/email-simple").request()
                .headers(headers)
                .post(getServerRequestFromFile("SendEmailController/simple-debug-with-errors/request-send-simple-email.json"));

        //check the response have the descriptive error
        assertJsonSame(
                getJsonFormatFileContent("SendEmailController/simple-debug-with-errors/response-send-simple-email.json"),
                getJsonFormatResponse(responseMsg)
        );
    }
}
