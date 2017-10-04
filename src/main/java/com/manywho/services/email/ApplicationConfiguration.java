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

    @Configuration.Setting(name = "Attachments: Source", contentType = ContentType.String)
    private AttachmentSource attachmentSource;

    @Configuration.Setting(name = "Attachments: Box Enterprise ID", contentType = ContentType.String)
    private String boxEnterprise;

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
