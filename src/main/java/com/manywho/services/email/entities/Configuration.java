package com.manywho.services.email.entities;

import com.manywho.sdk.services.annotations.Property;
import org.hibernate.validator.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class Configuration {
    @NotBlank(message = "An SMTP Hostname is required to send an email")
    @Property("Host")
    private String host;

    @NotNull
    @Property("Port")
    private Integer port;

    @NotBlank(message = "An SMTP Username is required to send an email")
    @Property("Username")
    private String username;

    @NotBlank(message = "An SMTP Password is required to send an email")
    @Property("Password")
    private String password;

    @NotBlank(message = "The SMTP Transport is required to send an email")
    @Property("Transport")
    private String transport;

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
}
