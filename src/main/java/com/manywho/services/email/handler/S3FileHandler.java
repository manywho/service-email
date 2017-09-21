package com.manywho.services.email.handler;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.manywho.sdk.entities.run.elements.type.Object;
import com.manywho.sdk.entities.run.elements.type.ObjectDataResponse;
import com.manywho.sdk.services.types.system.$File;
import com.manywho.services.email.cache.ServiceCacheProvider;
import com.manywho.services.email.dtos.FileDownload;
import com.manywho.services.email.entities.Configuration;
import com.manywho.services.email.service.FileService;
import org.apache.commons.io.IOUtils;
import org.glassfish.jersey.media.multipart.BodyPart;
import org.glassfish.jersey.media.multipart.BodyPartEntity;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

public class S3FileHandler implements FileHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(S3FileHandler.class);
    private FileService fileService;
    private ServiceCacheProvider serviceCacheProvider;

    @Inject
    public S3FileHandler(FileService fileService, ServiceCacheProvider serviceCacheProvider) {
        this.fileService = fileService;
        this.serviceCacheProvider = serviceCacheProvider;
    }

    public S3FileHandler() {
        //default constructor
    }

    @Override
    public ObjectDataResponse uploadFile(Configuration configuration, FormDataMultiPart formDataMultiPart) throws Exception {
        BodyPart bodyPart = fileService.getFilePart(formDataMultiPart);

        if (bodyPart != null) {
            try (InputStream inputStream = bodyPart.getEntityAs(BodyPartEntity.class).getInputStream()) {
                UUID id = UUID.randomUUID();
                Object object = fileService.getObjectFile(formDataMultiPart, bodyPart, id);
                serviceCacheProvider.getS3Client(configuration)
                        .putObject(new PutObjectRequest(serviceCacheProvider.getS3Bucket(configuration),
                                    id.toString(), inputStream, new ObjectMetadata()));
                return new ObjectDataResponse(object);
            }
        }

        throw new Exception("A file was not provided to upload to S3");
    }

    @Override
    public FileDownload downloadFile(Configuration configuration, String key) throws IOException {
        return downloadFile(serviceCacheProvider.getS3Client(configuration), serviceCacheProvider.getS3Bucket(configuration), key);
    }

    public FileDownload downloadFile(AmazonS3 amazonS3, String bucketName, String key) throws  IOException {
        S3Object s3Object = amazonS3.getObject(new GetObjectRequest(bucketName, key));
        FileDownload fileDownload;
        try (InputStream s3Stream = s3Object.getObjectContent()) {
            fileDownload = new FileDownload(s3Stream, s3Object.getKey());
        }
        LOGGER.info(String.format("File downloaded from S3 is %s", fileDownload));
        return fileDownload;
    }

    @Override
    public void deleteFiles(Configuration configuration, List<$File> files) throws IOException {
        final AmazonS3 s3client = serviceCacheProvider.getS3Client(configuration);
        final String s3Bucket = serviceCacheProvider.getS3Bucket(configuration);
        files.stream().forEach(file -> s3client.deleteObject(new DeleteObjectRequest(s3Bucket, file.getId())));
    }
}
