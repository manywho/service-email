package com.manywho.services.email.actions;

import com.manywho.sdk.enums.ContentType;
import com.manywho.sdk.services.annotations.Action;
import com.manywho.sdk.services.annotations.ActionInput;
import com.manywho.sdk.services.types.system.$File;
import com.manywho.services.email.types.Contact;
import javax.validation.constraints.NotNull;
import java.util.List;

@Action(name = "Send Email", summary = "Send an email with more advanced features (HTML, attachments, etc.)", uriPart = "actions/email")
public class SendEmail {

    @ActionInput(name="Attachments", contentType = ContentType.List, required = false)
    private List <$File> files;

    @NotNull(message = "A From contact is required when sending an email")
    @ActionInput(name = "From", contentType = ContentType.Object)
    private Contact from;

    @NotNull()
    @ActionInput(name = "To", contentType = ContentType.List, required = true)
    private List<Contact> to;

    @ActionInput(name = "To (CC)", contentType = ContentType.List, required = false)
    private List<Contact> cc;

    @ActionInput(name = "To (BCC)", contentType = ContentType.List, required = false)
    private List<Contact> bcc;

    @ActionInput(name = "Subject", contentType = ContentType.String, required = false)
    private String subject;

    @ActionInput(name = "Body", contentType = ContentType.String, required = false)
    private String body;

    @ActionInput(name="HTML Body", contentType = ContentType.String, required = false)
    private String htmlBody;

    public List<$File> getFiles() {
        return files;
    }

    public Contact getFrom() {
        return from;
    }

    public List<Contact> getTo() {
        return to;
    }

    public List<Contact> getCc() {
        return cc;
    }

    public List<Contact> getBcc() {
        return bcc;
    }

    public String getSubject() {
        return subject;
    }

    public String getBody() {
        return body;
    }

    public String getHtmlBody() {
        return htmlBody;
    }
}
