package com.manywho.services.email.cache;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.box.sdk.BoxAPIConnection;
import com.box.sdk.BoxConfig;
import com.manywho.sdk.services.config.ServiceConfiguration;
import com.manywho.services.email.dtos.FileDownload;
import com.manywho.services.email.entities.Configuration;
import com.manywho.services.email.handler.S3FileHandler;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ServiceCacheProviderTest {
    @Mock ServiceConfiguration serviceConfiguration;
    @Mock S3FileHandler s3FileHandler;
    @Mock Configuration configuration;
    @Mock FileDownload fileDownload;
    @Mock AmazonS3 amazonS3;
    @Mock S3Object s3Object;
    @Mock BoxAPIConnection boxAPIConnection;
    ServiceCacheProvider serviceCacheProvider;

    @Before
    public void setup() throws IOException {
        serviceCacheProvider = Mockito.spy(new ServiceCacheProvider(serviceConfiguration));
        doReturn(amazonS3).when(serviceCacheProvider).getS3Client(configuration);
        doReturn(s3FileHandler).when(serviceCacheProvider).getS3FileHandler();
        doReturn("bucket").when(serviceCacheProvider).getS3Bucket(configuration);
        when(amazonS3.getObject(any(GetObjectRequest.class))).thenReturn(s3Object);
        when(configuration.getBoxEnterpriseId()).thenReturn("app");
        when(configuration.getBoxPublicKeyId()).thenReturn("public");
        when(s3FileHandler.downloadFile(amazonS3, "bucket", "app_public_config.json")).thenReturn(fileDownload);
        when(fileDownload.getFileInput()).thenReturn(this.getClass().getClassLoader().getResourceAsStream("box-app-settings.json"));
    }

    @Test
    public void testGetBoxConfigFromS3() throws IOException {
        BoxConfig boxConfig = serviceCacheProvider.getBoxConfigFromS3(configuration);
        assertEquals("clientID", boxConfig.getClientId());
        assertEquals("clientSecret", boxConfig.getClientSecret());
        assertEquals("012345", boxConfig.getEnterpriseId());
        assertEquals("12345678", boxConfig.getJWTEncryptionPreferences().getPublicKeyID());
    }

    @Test
    public void testGetBoxConfig() throws IOException {
        when(fileDownload.getFileInput()).thenReturn(this.getClass().getClassLoader().getResourceAsStream("box-app-settings.json"));
        serviceCacheProvider.getBoxConfig(configuration);
        verify(serviceCacheProvider, times(1)).getBoxConfigFromS3(configuration);
        //calling the second time should read from cache
        Mockito.reset(serviceCacheProvider);
        serviceCacheProvider.getBoxConfig(configuration);
        verify(serviceCacheProvider, times(0)).getBoxConfigFromS3(configuration);
    }

    @Test
    public void testGetS3Client() {
        when(serviceCacheProvider.getS3Client(configuration)).thenCallRealMethod();
        doReturn(amazonS3).when(serviceCacheProvider).getAmazonS3(anyString(), anyString(), any(Regions.class));
        assertEquals(amazonS3, serviceCacheProvider.getS3Client(configuration));
        verify(serviceCacheProvider).getAmazonS3(anyString(), anyString(), any(Regions.class));
        //calling the second time should read from cache
        assertEquals(amazonS3, serviceCacheProvider.getS3Client(configuration));
        verify(serviceCacheProvider, times(1)).getAmazonS3(anyString(), anyString(), any(Regions.class));
    }

    @Test
    public void testGetBoxApiConnection() throws IOException {
        doReturn(boxAPIConnection).when(serviceCacheProvider).createBoxApiConnection(any(Configuration.class), any(BoxConfig.class));
        BoxAPIConnection boxAPIConnection = serviceCacheProvider.getBoxApiConnection(configuration);
        verify(serviceCacheProvider).getBoxConfig(configuration);
        assertEquals(this.boxAPIConnection, boxAPIConnection);
        //calling the second time should read from cache
        serviceCacheProvider.getBoxApiConnection(configuration);
        assertEquals(this.boxAPIConnection, boxAPIConnection);
        verify(serviceCacheProvider, times(2)).getBoxConfig(configuration);
    }

    @Test(expected = com.eclipsesource.json.ParseException.class)
    public void testInvalidBoxConfig() throws IOException {
        when(configuration.getBoxUserId()).thenReturn("app");
        when(configuration.getBoxPublicKeyId()).thenReturn("public");
        when(fileDownload.getFileInput()).thenReturn(new ByteArrayInputStream("box-app-settings.json".getBytes()));
        serviceCacheProvider.getBoxConfigFromS3(configuration);
    }
}