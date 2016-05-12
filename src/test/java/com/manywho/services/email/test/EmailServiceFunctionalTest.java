package com.manywho.services.email.test;

import com.manywho.sdk.test.FunctionalTest;
import com.manywho.sdk.test.MockFactory;
import com.manywho.services.email.managers.EmailManager;
import com.manywho.services.email.managers.FileManager;
import com.manywho.services.email.service.EmailService;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import javax.ws.rs.core.Application;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

public class EmailServiceFunctionalTest extends FunctionalTest {

    protected EmailService emailServiceMock;
    protected FileManager fileManagerMock;

    @Override
    protected Application configure(){
        emailServiceMock = mock(EmailService.class);
        fileManagerMock = mock(FileManager.class);

        return new com.manywho.services.email.Application().register(new AbstractBinder() {
            @Override
            protected void configure() {
                bindFactory(new MockFactory<EmailService>(emailServiceMock)).to(EmailService.class).ranked(1);
                bindFactory(new MockFactory<FileManager>(fileManagerMock)).to(FileManager.class).ranked(1);
            }
        });
    }
}
