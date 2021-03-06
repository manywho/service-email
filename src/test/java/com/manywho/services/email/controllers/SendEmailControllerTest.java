package com.manywho.services.email.controllers;

import com.manywho.sdk.api.security.AuthenticatedWho;
import com.manywho.sdk.services.identity.AuthorizationEncoder;
import com.manywho.services.email.ApplicationConfiguration;
import com.manywho.services.email.test.EmailServiceFunctionalTest;
import org.jboss.resteasy.mock.MockHttpRequest;
import org.jboss.resteasy.mock.MockHttpResponse;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.simplejavamail.MailException;
import org.simplejavamail.api.email.Email;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.Collections;
import java.util.UUID;

import static junit.framework.TestCase.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


public class SendEmailControllerTest extends EmailServiceFunctionalTest {
    @Test
    public void testSendEmailWithoutAttachments() throws Exception {
        ArgumentCaptor<ApplicationConfiguration> argumentCaptorConfiguration = ArgumentCaptor.forClass(ApplicationConfiguration.class);
        ArgumentCaptor<Email> argumentCaptorEmail = ArgumentCaptor.forClass(Email.class);

        when(mailerFactory.createMailer(argumentCaptorConfiguration.capture())).thenReturn(mailer);

        MockHttpRequest request = MockHttpRequest.post("/actions/email")
                .content(getFile("SendEmailController/basic/request-send-email.json"))
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", injector.getInstance(AuthorizationEncoder.class).encode(AuthenticatedWho.createPublicUser(UUID.fromString("67204d5c-6022-474d-8f80-0d576b43d02d"))));

        MockHttpResponse response = new MockHttpResponse();

        dispatcher.invoke(request, response);

        //check the response is right
        assertJsonSame(
                getJsonFormatFileContent("SendEmailController/basic/response-send-email.json"),
                response.getContentAsString()
        );

        verify(mailer).sendMail(argumentCaptorEmail.capture(), eq(true));

        ApplicationConfiguration capturedConfiguration = argumentCaptorConfiguration.getValue();
        assertEquals(587, Math.toIntExact(capturedConfiguration.getPort()));
        assertEquals("tls", capturedConfiguration.getTransport());
        assertEquals("smtp.example.com", capturedConfiguration.getHost());
        assertEquals("test@mailaccount.com", capturedConfiguration.getUsername());

        Email capturedEmail = argumentCaptorEmail.getValue();

        assertEquals("email text body", capturedEmail.getPlainText());
        assertEquals(
                "<html><title><body>Hello World!</body></title></html>",
                argumentCaptorEmail.getValue().getHTMLText()
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
    public void testSendEmailWithDecision() throws Exception {
        ArgumentCaptor<ApplicationConfiguration> argumentCaptorConfiguration = ArgumentCaptor.forClass(ApplicationConfiguration.class);
        ArgumentCaptor<Email> argumentCaptorEmail = ArgumentCaptor.forClass(Email.class);

        when(mailerFactory.createMailer(argumentCaptorConfiguration.capture())).thenReturn(mailer);

        when(tokenGenerator.generateRandomUUID()).thenReturn(UUID.fromString("67204d5c-6022-474d-8f80-0d576b43d02d"));

        String authorizationSerialized = injector.getInstance(AuthorizationEncoder.class).encode(AuthenticatedWho.createPublicUser(UUID.fromString("67204d5c-6022-474d-8f80-0d576b43d02d")));

        MultivaluedMap<String, String> headers = new MultivaluedHashMap<>();
        headers.put("Authorization", Collections.singletonList(authorizationSerialized));

        when(httpHeadersTest.getRequestHeaders()).thenReturn(headers);
        UriBuilder mockUribuilder = Mockito.mock(UriBuilder.class);
        URI uri = new URI("https://test.com/api/");
        when(mockUribuilder.build()).thenReturn(uri);

        when(uriInfo.getBaseUri()).thenReturn(new URI("https://test.com/api/"));

        MockHttpRequest request = MockHttpRequest.post("/actions/email-choices")
                .content(getFile("SendEmailController/decision/request-send-email.json"))
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", authorizationSerialized);

        MockHttpResponse response = new MockHttpResponse();

        dispatcher.invoke(request, response);

        String result = mockJedisPool.getResource().get("service:email:requests:67204d5c-6022-474d-8f80-0d576b43d02d");
        assertJsonSame(getJsonFormatFileContent("SendEmailController/decision/persistence.json"), result);

        //check the response is right
        assertJsonSame(
                getJsonFormatFileContent("SendEmailController/decision/response-send-email.json"),
                response.getContentAsString()
        );

        verify(mailer).sendMail(argumentCaptorEmail.capture(), eq(true));

        ApplicationConfiguration capturedConfiguration = argumentCaptorConfiguration.getValue();
        assertEquals(587, Math.toIntExact(capturedConfiguration.getPort()));
        assertEquals("tls", capturedConfiguration.getTransport());
        assertEquals("smtp.example.com", capturedConfiguration.getHost());
        assertEquals("test@mailaccount.com", capturedConfiguration.getUsername());

        Email capturedEmail = argumentCaptorEmail.getValue();

        assertEquals(
                "email text body\r\n\r\ngo - https://test.com/api/callback/response/67204d5c-6022-474d-8f80-0d576b43d02d/go \r\n\r\n",
                capturedEmail.getPlainText()
        );

        assertEquals(
                "<p> Hello World HTML!</p><br/><br/><a href=\"https://test.com/api/callback/response/67204d5c-6022-474d-8f80-0d576b43d02d/go\"> go </a> &nbsp;",
                argumentCaptorEmail.getValue().getHTMLText()
        );
        assertEquals("Test Subject", capturedEmail.getSubject());

        assertEquals("Test ManyWho", capturedEmail.getFromRecipient().getName());
        assertEquals("test@manywho.com", capturedEmail.getFromRecipient().getAddress());

        assertEquals("Test To", capturedEmail.getRecipients().get(0).getName());
        assertEquals("to@manywho.com", capturedEmail.getRecipients().get(0).getAddress());

        assertEquals(1, capturedEmail.getRecipients().size());
    }

    @Test
    public void testSendEmailWithNullsInCcAndBcc() throws Exception {
        ArgumentCaptor<ApplicationConfiguration> argumentCaptorConfiguration = ArgumentCaptor.forClass(ApplicationConfiguration.class);
        ArgumentCaptor<Email> argumentCaptorEmail = ArgumentCaptor.forClass(Email.class);

        when(mailerFactory.createMailer(argumentCaptorConfiguration.capture())).thenReturn(mailer);

        MockHttpRequest request = MockHttpRequest.post("/actions/email")
                .content(getFile("SendEmailController/supportnull/request-send-email.json"))
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", injector.getInstance(AuthorizationEncoder.class).encode(AuthenticatedWho.createPublicUser(UUID.fromString("67204d5c-6022-474d-8f80-0d576b43d02d"))));

        MockHttpResponse response = new MockHttpResponse();

        dispatcher.invoke(request, response);

        //check the response is right
        assertJsonSame(
                getJsonFormatFileContent("SendEmailController/supportnull/response-send-email.json"),
                response.getContentAsString()
        );

        verify(mailer).sendMail(argumentCaptorEmail.capture(), eq(true));

        ApplicationConfiguration capturedConfiguration = argumentCaptorConfiguration.getValue();
        assertEquals(587, Math.toIntExact(capturedConfiguration.getPort()));
        assertEquals("tls", capturedConfiguration.getTransport());
        assertEquals("smtp.example.com", capturedConfiguration.getHost());
        assertEquals("test@mailaccount.com", capturedConfiguration.getUsername());

        Email capturedEmail = argumentCaptorEmail.getValue();

        assertEquals("email text body", argumentCaptorEmail.getValue().getPlainText());
        assertEquals(
                "<html><title><body>Hello World!</body></title></html>",
                argumentCaptorEmail.getValue().getHTMLText()
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
        ArgumentCaptor<ApplicationConfiguration> argumentCaptorConfiguration = ArgumentCaptor.forClass(ApplicationConfiguration.class);
        ArgumentCaptor<Email> argumentCaptorEmail = ArgumentCaptor.forClass(Email.class);

        when(mailerFactory.createMailer(argumentCaptorConfiguration.capture())).thenReturn(mailer);

        MockHttpRequest request = MockHttpRequest.post("/actions/email-simple")
                .content(getFile("SendEmailController/simple/request-send-simple-email.json"))
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", injector.getInstance(AuthorizationEncoder.class).encode(AuthenticatedWho.createPublicUser(UUID.fromString("67204d5c-6022-474d-8f80-0d576b43d02d"))));

        MockHttpResponse response = new MockHttpResponse();

        dispatcher.invoke(request, response);

        //check the response is right
        assertJsonSame(
                getJsonFormatFileContent("SendEmailController/simple/response-send-simple-email.json"),
                response.getContentAsString()
        );

        verify(mailer).sendMail(argumentCaptorEmail.capture(), eq(true));

        ApplicationConfiguration capturedConfiguration = argumentCaptorConfiguration.getValue();
        assertEquals(587, Math.toIntExact(capturedConfiguration.getPort()));
        assertEquals("tls", capturedConfiguration.getTransport());
        assertEquals("smtp.gmail.com", capturedConfiguration.getHost());
        assertEquals("test@mailaccount.com", capturedConfiguration.getUsername());

        Email capturedEmail = argumentCaptorEmail.getValue();

        assertEquals("Email body", capturedEmail.getHTMLText());
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
        ArgumentCaptor<ApplicationConfiguration> argumentCaptorConfiguration = ArgumentCaptor.forClass(ApplicationConfiguration.class);
        ArgumentCaptor<Email> argumentCaptorEmail = ArgumentCaptor.forClass(Email.class);

        when(mailerFactory.createMailer(argumentCaptorConfiguration.capture())).thenReturn(mailer);

        MockHttpRequest request = MockHttpRequest.post("/actions/email-simple")
                .content(getFile("SendEmailController/simple-debug/request-send-simple-email.json"))
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", injector.getInstance(AuthorizationEncoder.class).encode(AuthenticatedWho.createPublicUser(UUID.fromString("67204d5c-6022-474d-8f80-0d576b43d02d"))));

        MockHttpResponse response = new MockHttpResponse();

        dispatcher.invoke(request, response);

        //check the response is right
        assertJsonSame(
                getJsonFormatFileContent("SendEmailController/simple-debug/response-send-simple-email.json"),
                response.getContentAsString()
        );

        verify(mailer).sendMail(argumentCaptorEmail.capture(), eq(false));

        ApplicationConfiguration capturedConfiguration = argumentCaptorConfiguration.getValue();
        assertEquals(587, Math.toIntExact(capturedConfiguration.getPort()));
        assertEquals("tls", capturedConfiguration.getTransport());
        assertEquals("smtp.gmail.com", capturedConfiguration.getHost());
        assertEquals("test@mailaccount.com", capturedConfiguration.getUsername());

        Email capturedEmail = argumentCaptorEmail.getValue();

        assertEquals("Email body", capturedEmail.getHTMLText());
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
        when(mailerFactory.createMailer(any())).thenReturn(mailer);

        //force an exception
        MailException mailException = new MailException("Third party error", new Exception("error")) {
            @Override
            public String getMessage() {
                return super.getMessage();
            }
        };

        doThrow(mailException)
                .when(mailer).sendMail(any(), eq(false));

        MockHttpRequest request = MockHttpRequest.post("/actions/email-simple")
                .content(getFile("SendEmailController/simple-debug-with-errors/request-send-simple-email.json"))
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", injector.getInstance(AuthorizationEncoder.class).encode(AuthenticatedWho.createPublicUser(UUID.fromString("67204d5c-6022-474d-8f80-0d576b43d02d"))));

        MockHttpResponse response = new MockHttpResponse();

        dispatcher.invoke(request, response);

        //check the response have the descriptive error
        assertJsonSame(
                getJsonFormatFileContent("SendEmailController/simple-debug-with-errors/response-send-simple-email.json"),
                response.getContentAsString()
        );
    }
}
