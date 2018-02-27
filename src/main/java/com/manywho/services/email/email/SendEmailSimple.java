package com.manywho.services.email.email;

import com.manywho.sdk.api.ContentType;
import com.manywho.sdk.services.actions.Action;

import javax.validation.constraints.NotNull;

@Action.Metadata(name = "Send Email (Simple)", summary = "Send a basic email using simple inputs", uri = "email-simple")
public class SendEmailSimple {
    @NotNull(message = "A From email is required when sending an email")
    @Action.Input(name = "From", contentType = ContentType.String, required = false)
    private String from;

    @NotNull()
    @Action.Input(name = "To", contentType = ContentType.String)
    private String to;

    @Action.Input(name = "Body", contentType = ContentType.Content, required = false)
    private String body;

    @Action.Input(name = "Subject", contentType = ContentType.String, required = false)
    private String subject;

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public String getBody() {
        return body;
    }

    public String getSubject() {
        return subject;
    }
}
