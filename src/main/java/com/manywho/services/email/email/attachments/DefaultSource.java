package com.manywho.services.email.email.attachments;

import com.manywho.services.email.ApplicationConfiguration;
import com.manywho.services.email.configuration.ServiceConfiguration;

import javax.inject.Inject;

public class DefaultSource implements Source {
    private final S3Source s3Source;
    private final ServiceConfiguration serviceConfiguration;

    @Inject
    public DefaultSource(S3Source s3Source, ServiceConfiguration serviceConfiguration) {
        this.s3Source = s3Source;
        this.serviceConfiguration = serviceConfiguration;
    }

    @Override
    public SourceFile download(ApplicationConfiguration configuration, String key) {
        // We're going to reuse S3Source, so we need to set the required configuration values for it
        configuration.setS3AccessKey(serviceConfiguration.get("s3.accessKey"));
        configuration.setS3SecretKey(serviceConfiguration.get("s3.secretKey"));
        configuration.setS3Bucket(serviceConfiguration.get("s3.bucket"));
        configuration.setS3Region(serviceConfiguration.get("s3.region"));

        return s3Source.download(configuration, key);
    }
}