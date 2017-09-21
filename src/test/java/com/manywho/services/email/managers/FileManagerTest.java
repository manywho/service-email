package com.manywho.services.email.managers;

import com.manywho.services.email.entities.Configuration;
import com.manywho.services.email.handler.BoxFileHandler;
import com.manywho.services.email.handler.S3FileHandler;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FileManagerTest {

    @Mock S3FileHandler s3FileHandler;
    @Mock BoxFileHandler boxFileHandler;
    @Mock Configuration configuration;
    @InjectMocks FileManager fileManager;

    @Test
    public void testS3FileDownload() throws IOException {
        String fileId = "fileId";
        when(configuration.getUseBoxForAttachment()).thenReturn(false);
        fileManager.downloadFile(configuration, fileId);
        verify(s3FileHandler).downloadFile(configuration, fileId);
    }

    @Test
    public void testBoxFileDownload() throws IOException {
        String fileId = "fileId";
        when(configuration.getUseBoxForAttachment()).thenReturn(true);
        fileManager.downloadFile(configuration, fileId);
        verify(boxFileHandler).downloadFile(configuration, fileId);
    }

}