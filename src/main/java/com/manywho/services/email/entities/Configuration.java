package com.manywho.services.email.entities;

import com.manywho.sdk.services.annotations.Property;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.validator.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import static java.lang.Boolean.valueOf;
import static java.util.Optional.ofNullable;

public class Configuration {

    public static final String HOST = "Host";
    public static final String PORT = "Port";
    public static final String USERNAME = "Username";
    public static final String PASSWORD = "Password";
    public static final String TRANSPORT = "Transport";
    public static final String BOX_APP_USER_ID = "BoxAppUserId";
    public static final String BOX_ENTERPRISE_ID = "BoxEnterpriseId";
    public static final String BOX_PUBLIC_KEY_ID = "BoxPublicKeyId";
    public static final String USE_BOX_FOR_ATTACHMENT = "UseBoxForAttachment";
    public static final String S3ACCESSKEYID = "S3AccessKeyId";
    public static final String S3ACCESSSECRET = "s3AccessSecret";
    public static final String S3BUCKETNAME = "S3BucketName";
    public static final String S3REGION = "S3Region";
    public static final String RETAIN_FILE = "RetainFiles";

    @NotBlank(message = "An SMTP Hostname is required to send an email")
    @Property(HOST)
    private String host;

    @NotNull
    @Property(PORT)
    private Integer port;

    @NotBlank(message = "An SMTP Username is required to send an email")
    @Property(USERNAME)
    private String username;

    @NotBlank(message = "An SMTP Password is required to send an email")
    @Property(PASSWORD)
    private String password;

    @NotBlank(message = "The SMTP Transport is required to send an email")
    @Property(TRANSPORT)
    private String transport;

    @Property(BOX_APP_USER_ID)
    private String boxUserId;

    @Property(BOX_ENTERPRISE_ID)
    private String boxEnterpriseId;

    @Property(BOX_PUBLIC_KEY_ID)
    private String boxPublicKeyId;

    @Property(USE_BOX_FOR_ATTACHMENT)
    private String useBoxForAttachment;

    @Property(S3ACCESSKEYID)
    private String s3AccessKeyId;

    @Property(S3ACCESSSECRET)
    private String s3AccessSecret;

    @Property(S3BUCKETNAME)
    private String s3BucketName;

    @Property(S3REGION)
    private String s3Region;

    @Property(RETAIN_FILE)
    private String retainFiles;

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

    public String getBoxUserId() {
        return boxUserId;
    }

    public String getS3AccessKeyId() {
        return s3AccessKeyId;
    }

    public String getS3AccessSecret() {
        return s3AccessSecret;
    }

    public String getS3BucketName() {
        return s3BucketName;
    }

    public boolean getRetainFiles() {
        return valueOf(ofNullable(retainFiles).orElse("false"));
    }

    public String getS3Region() {
        return ofNullable(s3Region).orElse("us-east-1");
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("Host",this.host)
                .append("Port", this.port)
                .append("Username",this.username)
                .append("Transport",this.transport)
                .append("BoxUserId",this.boxUserId)
                .append("BoxPublicKeyId",this.boxPublicKeyId)
                .append("UseBoxForAttachment",this.useBoxForAttachment)
                .append("S3AccessKeyId",this.s3AccessKeyId)
                .append("s3AccessSecret",this.s3AccessSecret)
                .append("S3BucketName",this.s3BucketName)
                .append("S3Region",this.s3Region)
                .append("RetainS3File",this.getRetainFiles())
                .build();
    }

    public String getBoxPublicKeyId() {
        return boxPublicKeyId;
    }

    public boolean getUseBoxForAttachment() {
        return valueOf(ofNullable(useBoxForAttachment).orElse("false"));
    }

    public String getBoxEnterpriseId() {
        return boxEnterpriseId;
    }
}
