package com.manywho.services.email.email;

import com.google.common.collect.Lists;
import com.manywho.sdk.api.ContentType;
import com.manywho.sdk.services.actions.Action;
import com.manywho.sdk.services.types.system.$File;
import com.manywho.services.email.types.Contact;

import javax.validation.constraints.NotNull;
import java.util.List;

@Action.Metadata(name = "Send Email", summary = "Send an email with more advanced features (HTML, attachments, etc.)", uri = "email")
public class SendEmail implements Action {
    @Action.Input(name = "Attachments", contentType = ContentType.List, required = false)
    private List<$File> files = Lists.newArrayList();

    @NotNull(message = "A From contact is required when sending an email")
    @Action.Input(name = "From", contentType = ContentType.Object)
    private Contact from;

    @NotNull()
    @Action.Input(name = "To", contentType = ContentType.List)
    private List<Contact> to;

    @Action.Input(name = "To (CC)", contentType = ContentType.List, required = false)
    private List<Contact> cc;

    @Action.Input(name = "To (BCC)", contentType = ContentType.List, required = false)
    private List<Contact> bcc;

    @Action.Input(name = "Subject", contentType = ContentType.String, required = false)
    private String subject;

    @Action.Input(name = "Body", contentType = ContentType.String, required = false)
    private String body;

    @Action.Input(name = "HTML Body", contentType = ContentType.Content, required = false)
    private String htmlBody;

    public List<Contact> getBcc() {
        return bcc;
    }

    public String getBody() {
        return body;
    }

    public List<Contact> getCc() {
        return cc;
    }

    public List<$File> getFiles() {
        return files;
    }

    public Contact getFrom() {
        return from;
    }

    public String getHtmlBody() {
        return htmlBody;
    }

    public String getSubject() {
        return subject;
    }

    public List<Contact> getTo() {
        return to;
    }
}
