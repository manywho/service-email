package com.manywho.services.email;

import com.manywho.sdk.api.ContentType;
import com.manywho.sdk.services.configuration.Configuration;

public class ApplicationConfiguration implements Configuration {
    @Configuration.Setting(name = "Host", contentType = ContentType.String)
    private String host;

    @Configuration.Setting(name = "Port", contentType = ContentType.Number)
    private Integer port;

    @Configuration.Setting(name = "Username", contentType = ContentType.String)
    private String username;

    @Configuration.Setting(name = "Password", contentType = ContentType.Password)
    private String password;

    @Configuration.Setting(name = "Transport", contentType = ContentType.String)
    private String transport;

    @Configuration.Setting(name = "Attachments: Source", contentType = ContentType.String, required = false)
    private AttachmentSource attachmentSource = AttachmentSource.Default;

    @Configuration.Setting(name = "Attachments: Box Enterprise ID", contentType = ContentType.String, required = false)
    private String boxEnterprise;

    @Configuration.Setting(name = "Attachments: S3 Bucket", contentType = ContentType.String, required = false)
    private String s3Bucket;

    @Configuration.Setting(name = "Attachments: S3 Access Key", contentType = ContentType.String, required = false)
    private String s3AccessKey;

    @Configuration.Setting(name = "Attachments: S3 Secret Key", contentType = ContentType.Password, required = false)
    private String s3SecretKey;

    @Configuration.Setting(name = "Attachments: S3 Region", contentType = ContentType.String, required = false)
    private String s3Region;

    public String getHost() {
        return host;
    }

    public Integer getPort() {
        return port;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getTransport() {
        return transport;
    }

    public AttachmentSource getAttachmentSource() {
        return attachmentSource;
    }

    public String getBoxEnterprise() {
        return boxEnterprise;
    }

    public String getS3Bucket() {
        return s3Bucket;
    }

    public void setS3Bucket(String s3Bucket) {
        this.s3Bucket = s3Bucket;
    }

    public String getS3AccessKey() {
        return s3AccessKey;
    }

    public void setS3AccessKey(String s3AccessKey) {
        this.s3AccessKey = s3AccessKey;
    }

    public String getS3SecretKey() {
        return s3SecretKey;
    }

    public void setS3SecretKey(String s3SecretKey) {
        this.s3SecretKey = s3SecretKey;
    }


    public String getS3Region() {
        return s3Region;
    }

    public void setS3Region(String s3Region) {
        this.s3Region = s3Region;
    }

    public enum AttachmentSource {
        Box("box"),
        Default(""),
        S3("s3");

        private final String source;

        AttachmentSource(String source) {
            this.source = source;
        }

        public static AttachmentSource forValue(String value) {
            for (AttachmentSource source : values()) {
                if (value.equalsIgnoreCase(source.source)) {
                    return source;
                }
            }

            throw new IllegalArgumentException("The attachment source value \"" + value + "\" is not valid");
        }
    }
}
