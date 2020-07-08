package com.manywho.services.email.test;

import com.manywho.services.email.ApplicationConfiguration;
import com.manywho.services.email.email.EmailManager;
import com.manywho.services.email.email.MailerFactory;
import org.junit.Test;
import org.simplejavamail.api.email.Email;
import org.simplejavamail.email.EmailBuilder;

import static org.junit.Assert.assertTrue;

public class EmailManagerTest {

    @Test
    public void testThatThreadsDoNotStayAroundAfterSendingEmailAsynchronously() throws InterruptedException {
        ApplicationConfiguration applicationConfiguration = new ApplicationConfiguration();
        applicationConfiguration.setHost("example");
        applicationConfiguration.setTransport("tls");

        Email email = EmailBuilder.startingBlank()
                .from("Test User", "test@example.com")
                .to("Test User", "test@example.com")
                .withPlainText("Some email content")
                .withSubject("A test email")
                .buildEmail();

        EmailManager emailManager = new EmailManager(new MailerFactory());
        for (int i = 0; i < 20; i++) {
            emailManager
                    .send(applicationConfiguration, email, false);
        }

        // Wait for the email sending threads to (probably) complete
        Thread.sleep(1000);

        // Ensure all the threads actually shut down
        assertTrue("There are still threads running when there shouldn't be", Thread.activeCount() < 5);
    }
}
