package com.manywho.services.email;

import com.amazonaws.services.s3.AmazonS3;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.manywho.sdk.client.run.RunClient;
import com.manywho.services.email.configuration.ServiceConfiguration;
import com.manywho.services.email.configuration.ServiceConfigurationDefault;
import com.manywho.services.email.guice.JedisPoolProvider;
import com.manywho.services.email.guice.ObjectMapperProvider;
import com.manywho.services.email.guice.RunClientProvider;
import com.manywho.services.email.guice.S3ClientProvider;
import redis.clients.jedis.JedisPool;

public class ApplicationModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(AmazonS3.class).toProvider(S3ClientProvider.class).in(Singleton.class);
        bind(ServiceConfiguration.class).to(ServiceConfigurationDefault.class);
        bind(JedisPool.class).toProvider(JedisPoolProvider.class).in(Singleton.class);
        bind(RunClient.class).toProvider(RunClientProvider.class).in(Singleton.class);
    }
}
