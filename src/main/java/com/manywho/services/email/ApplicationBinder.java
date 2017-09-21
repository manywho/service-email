package com.manywho.services.email;

import com.manywho.services.email.cache.ServiceCacheProvider;
import com.manywho.services.email.factories.EmailFactory;
import com.manywho.services.email.factories.MailerFactory;
import com.manywho.services.email.handler.BoxFileHandler;
import com.manywho.services.email.handler.S3FileHandler;
import com.manywho.services.email.managers.EmailManager;
import com.manywho.services.email.managers.FileManager;
import com.manywho.services.email.service.EmailService;
import com.manywho.services.email.service.FileService;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

import javax.inject.Singleton;

public class ApplicationBinder extends AbstractBinder {
    @Override
    protected void configure() {
        bind(EmailFactory.class).to(EmailFactory.class);
        bind(MailerFactory.class).to(MailerFactory.class).in(Singleton.class);
        bind(FileManager.class).to(FileManager.class);
        bind(S3FileHandler.class).to(S3FileHandler.class);
        bind(BoxFileHandler.class).to(BoxFileHandler.class);
        bind(FileService.class).to(FileService.class);
        bind(EmailManager.class).to(EmailManager.class);
        bind(EmailService.class).to(EmailService.class);
        bind(ServiceCacheProvider.class).to(ServiceCacheProvider.class).in(Singleton.class);
    }
}
