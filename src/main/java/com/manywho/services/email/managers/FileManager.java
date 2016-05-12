package com.manywho.services.email.managers;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.*;
import com.manywho.sdk.entities.run.elements.type.*;
import com.manywho.sdk.entities.run.elements.type.Object;
import com.manywho.sdk.services.config.ServiceConfiguration;
import com.manywho.sdk.services.types.system.$File;
import com.manywho.services.email.service.FileService;
import org.glassfish.jersey.media.multipart.BodyPart;
import org.glassfish.jersey.media.multipart.BodyPartEntity;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import javax.inject.Inject;
import java.io.*;
import java.util.List;
import java.util.UUID;

public class FileManager {
    private FileService fileService;
    private ServiceConfiguration configuration;
    private AmazonS3 s3client;

    @Inject
    public FileManager(FileService fileService, ServiceConfiguration configuration) {
        this.fileService = fileService;
        this.configuration = configuration;

        BasicAWSCredentials credentials = new BasicAWSCredentials(configuration.get("s3.aws_access_key_id"), configuration.get("s3.aws_secret_access_key"));
        this.s3client = new AmazonS3Client(credentials);
    }

    public ObjectDataResponse uploadFile( FormDataMultiPart formDataMultiPart) throws Exception {
        BodyPart bodyPart = fileService.getFilePart(formDataMultiPart);

        if (bodyPart != null) {
            InputStream inputStream = bodyPart.getEntityAs(BodyPartEntity.class).getInputStream();

            UUID id = UUID.randomUUID();
            Object object = fileService.getObjectFile(formDataMultiPart, bodyPart, id);
            s3client.putObject(new PutObjectRequest(configuration.get("s3.bucket_name"), id.toString(), inputStream, new ObjectMetadata()));

            return new ObjectDataResponse(object);
        }

        throw new Exception("A file was not provided to upload to Box");
    }

    public InputStream downloadFile(String key) {
        S3Object object = s3client.getObject(new GetObjectRequest(configuration.get("s3.bucket_name"), key));
        return object.getObjectContent();
    }

    public void deleteFiles(List<$File> files) {
        for($File attachmentExternalKey:files) {
            s3client.deleteObject(new DeleteObjectRequest(configuration.get("s3.bucket_name"), attachmentExternalKey.getId()));
        }
    }
}
