package com.manywho.services.email.guice;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.google.inject.Provider;

public class S3ClientProvider implements Provider<AmazonS3> {
    @Override
    public AmazonS3 get() {
        return new AmazonS3Client(new BasicAWSCredentials(
                "",
                ""
        ));
    }
}
