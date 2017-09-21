package com.manywho.services.email.cache;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.box.sdk.BoxAPIConnection;
import com.box.sdk.BoxConfig;
import com.box.sdk.BoxDeveloperEditionAPIConnection;
import com.box.sdk.IAccessTokenCache;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.manywho.sdk.services.config.ServiceConfiguration;
import com.manywho.services.email.dtos.FileDownload;
import com.manywho.services.email.entities.Configuration;
import com.manywho.services.email.handler.S3FileHandler;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.concurrent.TimeUnit;

import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

public class ServiceCacheProvider implements IAccessTokenCache {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(ServiceCacheProvider.class);
    private final ServiceConfiguration serviceConfiguration;
    private Cache<String, Object> serviceCache = null;

    @Inject
    public ServiceCacheProvider(ServiceConfiguration serviceConfiguration) {
        this.serviceConfiguration = serviceConfiguration;
        serviceCache = CacheBuilder.newBuilder()
                .maximumSize(1000)
                .expireAfterWrite(1, TimeUnit.HOURS)
                .build();
    }

    public BoxAPIConnection getBoxApiConnection(Configuration configuration) throws  IOException {
        final String boxApiConnectionKey = format("%s_%s_connection", configuration.getBoxUserId(), configuration.getBoxPublicKeyId());
        BoxAPIConnection boxAPIConnection = createBoxApiConnection(configuration, getBoxConfig(configuration));
        LOGGER.info(format("BoxConnection created for [%s] is [%s]",boxApiConnectionKey, boxAPIConnection));
        serviceCache.put(boxApiConnectionKey, boxAPIConnection);
        return boxAPIConnection;
    }

    protected BoxAPIConnection createBoxApiConnection(Configuration configuration, BoxConfig boxConfig) {
        return BoxDeveloperEditionAPIConnection.getAppUserConnection(configuration.getBoxUserId(), boxConfig, this);
    }

    protected BoxConfig getBoxConfig(Configuration configuration) throws IOException {
        String boxConfigFileKey = getBoxConfigFileKey(configuration);
        BoxConfig conf = (BoxConfig) serviceCache.getIfPresent(boxConfigFileKey);
        LOGGER.info(format("Cached BoxConfig for [%s] is [%s]",boxConfigFileKey, conf));
        return conf != null ? conf : getBoxConfigFromS3(configuration);
    }

    public BoxConfig getBoxConfigFromS3(Configuration configuration) throws IOException {
        String boxConfigFileKey = getBoxConfigFileKey(configuration);
        LOGGER.info(format("Getting BoxConfig json from S3 for [%s]",boxConfigFileKey));
        FileDownload fileDownload  = getS3FileHandler().downloadFile(getS3Client(configuration), getS3Bucket(configuration), boxConfigFileKey);
        try (Reader reader = new InputStreamReader(fileDownload.getFileInput())) {
            BoxConfig boxConfig = BoxConfig.readFrom(reader);
            serviceCache.put(boxConfigFileKey, boxConfig);
            return boxConfig;
        }
    }

    public AmazonS3 getS3Client(Configuration configuration) {
        boolean s3FromMW = isS3FromMW(configuration);
        final String s3Bucket = getS3Bucket(configuration);
        String s3ClientKey = format("%s_s3client", s3Bucket);
        AmazonS3 s3client = (AmazonS3) serviceCache.getIfPresent(s3ClientKey);
        LOGGER.info(format("Cached S3 Client for [%s] is [%s]",s3ClientKey, s3client));
        if (s3client == null) {
            s3client = getAmazonS3(
                    s3FromMW ? configuration.getS3AccessKeyId() : serviceConfiguration.get("s3.aws_access_key_id"),
                    s3FromMW ? configuration.getS3AccessSecret() : serviceConfiguration.get("s3.aws_secret_access_key"),
                    getRegion(s3FromMW ? configuration.getS3Region() : serviceConfiguration.get("s3.aws_access_region") == null
                                                                        ? "us-east-1" : serviceConfiguration.get("s3.aws_access_region")));
            serviceCache.put(s3ClientKey, s3client);
        }
        return s3client;
    }

    public String getS3Bucket(Configuration configuration) {
        boolean s3FromMW = isS3FromMW(configuration);
        return s3FromMW ? configuration.getS3BucketName() : serviceConfiguration.get("s3.bucket_name");
    }

    protected S3FileHandler getS3FileHandler() {
        return new S3FileHandler();
    }

    protected AmazonS3 getAmazonS3(String s3AccessKeyId, String s3AccessKeySecret, Regions s3Region) {
        BasicAWSCredentials credentials = new BasicAWSCredentials(s3AccessKeyId, s3AccessKeySecret);
        return AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(s3Region)
                .build();
    }

    private boolean isS3FromMW(Configuration configuration) {
        return isNotEmpty(configuration.getS3BucketName())
                && isNotEmpty(configuration.getS3AccessKeyId())
                && isNotEmpty(configuration.getS3AccessSecret());
    }

    private String getBoxConfigFileKey(Configuration configuration) {
        return format("%s_%s_config.json",configuration.getBoxEnterpriseId(), configuration.getBoxPublicKeyId());
    }

    private Regions getRegion(String s3Region) {
        return Regions.fromName(s3Region);
    }

    @Override
    public String get(String key) {
        return (String)serviceCache.getIfPresent(key);
    }

    @Override
    public void put(String key, String value) {
        serviceCache.put(key, value);
    }
}
