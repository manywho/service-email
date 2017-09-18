package com.manywho.services.email.handler;

import com.box.sdk.BoxFile;
import com.manywho.services.email.cache.ServiceCacheProvider;
import com.manywho.services.email.dtos.FileDownload;
import com.manywho.services.email.entities.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BoxFileHandlerTest {
    @Mock ServiceCacheProvider serviceCacheProvider;
    @Mock Configuration configuration;
    @Mock BoxFile boxFile;
    @Mock BoxFile.Info boxFileInfo;
    BoxFileHandler boxFileHandler;

    @Before
    public void setup() throws IOException {
        boxFileHandler = Mockito.spy(new BoxFileHandler(serviceCacheProvider));
    }
    @Test
    public void downloadFile() throws Exception {
        String fileId = "fileId";
        doReturn(boxFile).when(boxFileHandler).getBoxFile(configuration, fileId);
        when(boxFile.getInfo()).thenReturn(boxFileInfo);
        FileDownload fileDownload = boxFileHandler.downloadFile(configuration, fileId);
        assertNotNull(fileDownload);
    }

}