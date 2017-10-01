package com.manywho.services.email.files;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.google.common.collect.Lists;
import com.manywho.sdk.api.run.elements.type.FileListFilter;
import com.manywho.sdk.services.files.FileHandler;
import com.manywho.sdk.services.files.FileUpload;
import com.manywho.sdk.services.types.system.$File;
import com.manywho.services.email.ApplicationConfiguration;
import com.manywho.services.email.configuration.ServiceConfiguration;

import javax.inject.Inject;
import java.util.List;
import java.util.UUID;

public class EmailFileHandler implements FileHandler<ApplicationConfiguration> {
    private final AmazonS3 amazonS3;
    private final ServiceConfiguration serviceConfiguration;

    @Inject
    public EmailFileHandler(AmazonS3 amazonS3, ServiceConfiguration serviceConfiguration) {
        this.amazonS3 = amazonS3;
        this.serviceConfiguration = serviceConfiguration;
    }

    @Override
    public List<$File> findAll(ApplicationConfiguration configuration, FileListFilter listFilter, String path) {
        return Lists.newArrayList();
    }

    @Override
    public $File upload(ApplicationConfiguration configuration, String path, FileUpload fileUpload) {
        if (fileUpload == null || fileUpload.getContent() == null) {
            throw new RuntimeException("No file was uploaded");
        }

        UUID id = UUID.randomUUID();

        amazonS3.putObject(new PutObjectRequest(
                serviceConfiguration.get("s3.bucket_name"),
                id.toString(),
                fileUpload.getContent(),
                new ObjectMetadata()
        ));

        return new $File(id.toString(), fileUpload.getName());
    }
}
