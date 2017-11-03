package com.manywho.services.email.guice;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.manywho.services.email.configuration.ServiceConfigurationDefault;

public class S3ClientProvider implements Provider<AmazonS3> {
    private ServiceConfigurationDefault serviceConfigurationDefault;

    @Inject
    public S3ClientProvider(ServiceConfigurationDefault serviceConfigurationDefault) {
        this.serviceConfigurationDefault = serviceConfigurationDefault;
    }

    @Override
    public AmazonS3 get() {
        return AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(new AWSCredentials() {
            @Override
            public String getAWSAccessKeyId() {
                return serviceConfigurationDefault.get("s3.accessKey");
            }

            @Override
            public String getAWSSecretKey() {
                return serviceConfigurationDefault.get("s3.secretKey");
            }
        }))
         .withRegion(serviceConfigurationDefault.get("s3.region"))
         .build();
    }
}
