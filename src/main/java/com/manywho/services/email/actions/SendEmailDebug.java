package com.manywho.services.email.actions;

import com.manywho.sdk.enums.ContentType;
import com.manywho.sdk.services.annotations.Action;
import com.manywho.sdk.services.annotations.ActionInput;
import javax.validation.constraints.NotNull;

@Action(name = "Send Email Debug", summary = "Send an Email for Debug", uriPart = "actions/email-debug")
public class SendEmailDebug {

    @NotNull(message = "A From email is required when sending an email")
    @ActionInput(name = "From", contentType = ContentType.String, required = true)
    private String from;

    @NotNull()
    @ActionInput(name = "To", contentType = ContentType.String, required = true)
    private String to;

    @ActionInput(name = "Body", contentType = ContentType.String, required = false)
    private String body;

    @ActionInput(name = "Subject", contentType = ContentType.String, required = false)
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
