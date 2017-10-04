package com.manywho.services.email.email.attachments;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.google.common.io.ByteStreams;
import com.manywho.services.email.ApplicationConfiguration;
import com.manywho.services.email.configuration.ServiceConfiguration;

import javax.inject.Inject;
import java.io.IOException;

public class DefaultSource implements Source {
    private final AmazonS3 amazonS3;
    private final ServiceConfiguration serviceConfiguration;

    @Inject
    public DefaultSource(AmazonS3 amazonS3, ServiceConfiguration serviceConfiguration) {
        this.amazonS3 = amazonS3;
        this.serviceConfiguration = serviceConfiguration;
    }

    @Override
    public SourceFile download(ApplicationConfiguration configuration, String key) {
        S3Object object = amazonS3.getObject(new GetObjectRequest(
                serviceConfiguration.get("s3.bucket"),
                key
        ));

        try {
            return new SourceFile(ByteStreams.toByteArray(object.getObjectContent()), object.getKey());
        } catch (IOException e) {
            throw new RuntimeException("An error occurred converting the source file to a byte array", e);
        }
    }
}