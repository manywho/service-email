package com.manywho.services.email.test;

import com.manywho.sdk.test.FunctionalTest;
import com.manywho.sdk.test.MockFactory;
import com.manywho.services.email.factories.MailerFactory;
import com.manywho.services.email.managers.FileManager;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.simplejavamail.mailer.Mailer;

import javax.ws.rs.core.Application;

import static org.mockito.Mockito.mock;

public class EmailServiceFunctionalTest extends FunctionalTest {

    protected MailerFactory mailerFactory;
    protected FileManager fileManagerMock;
    protected Mailer mailer;

    @Override
    protected Application configure(){
        fileManagerMock = mock(FileManager.class);
        mailerFactory = mock(MailerFactory.class);
        mailer = mock(Mailer.class);

        return new com.manywho.services.email.Application().register(new AbstractBinder() {
            @Override
            protected void configure() {
                bindFactory(new MockFactory<MailerFactory>(mailerFactory)).to(MailerFactory.class).ranked(1);
                bindFactory(new MockFactory<FileManager>(fileManagerMock)).to(FileManager.class).ranked(1);
            }
        });
    }
}
