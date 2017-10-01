package com.manywho.services.email;

import com.amazonaws.services.s3.AmazonS3;
import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.manywho.services.email.configuration.ServiceConfiguration;
import com.manywho.services.email.configuration.ServiceConfigurationDefault;
import com.manywho.services.email.guice.S3ClientProvider;

public class ApplicationModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(AmazonS3.class).toProvider(S3ClientProvider.class).in(Singleton.class);
        bind(ServiceConfiguration.class).to(ServiceConfigurationDefault.class);
    }
}
