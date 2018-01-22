package com.manywho.services.email.email;

import com.google.common.collect.Lists;
import com.manywho.sdk.api.ContentType;
import com.manywho.sdk.services.actions.Action;
import com.manywho.sdk.services.types.system.$File;
import com.manywho.services.email.types.Contact;
import com.manywho.services.email.types.DecisionChoice;

import javax.validation.constraints.NotNull;
import java.util.List;

@Action.Metadata(name = "Send Email with Decision Request", summary = "Send an email with a decision request", uri = "email-choices")
public class SendEmailDecisionRequest implements Action {
    public static class Input {
        @Action.Input(name = "Attachments", contentType = ContentType.List, required = false)
        private List<$File> files = Lists.newArrayList();

        @NotNull(message = "A From contact is required when sending an email")
        @Action.Input(name = "From", contentType = ContentType.Object)
        private Contact from;

        @NotNull()
        @Action.Input(name = "To", contentType = ContentType.List)
        private List<Contact> to;

        @Action.Input(name = "Subject", contentType = ContentType.String, required = false)
        private String subject;

        @Action.Input(name = "Body", contentType = ContentType.String, required = false)
        private String body;

        @Action.Input(name = "HTML Body", contentType = ContentType.Content, required = false)
        private String htmlBody;

        @Action.Input(name = "Decision Choices", contentType = ContentType.List, required = false)
        private List<DecisionChoice> decisionChoices;

        public List<$File> getFiles() {
            return files;
        }

        public String getBody() {
            return body;
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

        public List<DecisionChoice> getDecisionChoices() {
            return decisionChoices;
        }
    }

    public static class Output {
        @Action.Output(name = "Response", contentType = ContentType.String)
        private String response;

        @Action.Output(name = "Recipient", contentType = ContentType.String)
        private String recipient;

        public Output(String response, String recipient) {
            this.response = response;
            this.recipient = recipient;
        }
    }
}

