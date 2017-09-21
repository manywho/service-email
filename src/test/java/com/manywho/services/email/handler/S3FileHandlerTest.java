package com.manywho.services.email.handler;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.manywho.sdk.entities.run.elements.type.ObjectDataResponse;
import com.manywho.sdk.services.types.system.$File;
import com.manywho.services.email.cache.ServiceCacheProvider;
import com.manywho.services.email.dtos.FileDownload;
import com.manywho.services.email.entities.Configuration;
import com.manywho.services.email.service.FileService;
import org.glassfish.jersey.media.multipart.BodyPart;
import org.glassfish.jersey.media.multipart.BodyPartEntity;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class S3FileHandlerTest {

    @Mock Configuration configuration;
    @Mock FileService fileService;
    @Mock AmazonS3 amazonS3;
    @Mock S3Object s3Object;
    @Mock S3ObjectInputStream s3ObjectInputStream;
    @Mock ServiceCacheProvider serviceCacheProvider;
    S3FileHandler s3FileHandler;

    @Before
    public void setup() throws IOException {
        s3FileHandler = spy(new S3FileHandler(fileService, serviceCacheProvider));
        when(serviceCacheProvider.getS3Client(configuration)).thenReturn(amazonS3);
        when(amazonS3.getObject(any(GetObjectRequest.class))).thenReturn(s3Object);
        when(s3ObjectInputStream.read(any(byte[].class))).thenReturn(5,-1);
    }

    @Test
    public void testDownloadFile() throws IOException {
        String fileId = "fileId";
        when(s3Object.getObjectContent()).thenReturn(s3ObjectInputStream);
        FileDownload fileDownload = s3FileHandler.downloadFile(configuration, fileId);
        assertNotNull(fileDownload);
    }

    @Test
    public void testUploadFile() throws Exception {
        FormDataMultiPart formDataMultiPart = mock(FormDataMultiPart.class);
        BodyPart bodyPart = mock(BodyPart.class);
        BodyPartEntity bodyPartEntity = mock(BodyPartEntity.class);

        when(fileService.getFilePart(formDataMultiPart)).thenReturn(bodyPart);
        when(bodyPart.getEntityAs(BodyPartEntity.class)).thenReturn(bodyPartEntity);
        ObjectDataResponse objectDataResponse = s3FileHandler.uploadFile(configuration, formDataMultiPart);
        verify(amazonS3).putObject(any(PutObjectRequest.class));
        assertNotNull(objectDataResponse);
    }

    @Test
    public void testDeleteFiles() throws IOException {
        $File file = mock($File.class);
        List<$File> files = Arrays.asList(file, file);
        s3FileHandler.deleteFiles(configuration, files);
        verify(amazonS3, times(2)).deleteObject(any(DeleteObjectRequest.class));
    }
}