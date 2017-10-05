package com.manywho.services.email.email.attachments;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.google.common.base.Strings;
import com.google.common.io.ByteStreams;
import com.manywho.services.email.ApplicationConfiguration;

import java.io.IOException;

public class S3Source implements Source {

    @Override
    public SourceFile download(ApplicationConfiguration configuration, String key) {
        if (Strings.isNullOrEmpty(configuration.getS3AccessKey())) {
            throw new RuntimeException("The service must be configured with an S3 Access Key to use S3 as the attachment source");
        }

        if (Strings.isNullOrEmpty(configuration.getS3SecretKey())) {
            throw new RuntimeException("The service must be configured with an S3 Secret Key to use S3 as the attachment source");
        }

        if (Strings.isNullOrEmpty(configuration.getS3Bucket())) {
            throw new RuntimeException("The service must be configured with an S3 Bucket to use S3 as the attachment source");
        }

        // Create the S3 client, based on the given credentials
        final AmazonS3 amazonS3 = AmazonS3Client.builder()
                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(configuration.getS3AccessKey(), configuration.getS3SecretKey())))
                .build();

        // Fetch the file info from S3
        S3Object object = amazonS3.getObject(new GetObjectRequest(
                configuration.getS3Bucket(),
                key
        ));

        if (object.getObjectMetadata().getContentLength() > SourceConstants.SIZE_LIMIT) {
            throw new RuntimeException(String.format("The file with ID %s is larger than %s, which is the maximum supported attachment size", key, SourceConstants.SIZE_LIMIT_HUMAN));
        }

        try {
            return new SourceFile(ByteStreams.toByteArray(object.getObjectContent()), object.getKey());
        } catch (IOException e) {
            throw new RuntimeException("An error occurred converting the source file to a byte array", e);
        }
    }
}