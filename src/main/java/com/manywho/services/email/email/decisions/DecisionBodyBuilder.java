package com.manywho.services.email.email.decisions;

import com.google.common.base.Strings;
import com.google.common.net.UrlEscapers;
import com.manywho.sdk.api.run.elements.map.OutcomeAvailable;
import javax.inject.Inject;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import java.util.List;
import java.util.UUID;

public class DecisionBodyBuilder {
    private UriInfo uriInfo;

    @Inject
    public DecisionBodyBuilder(@Context UriInfo uriInfo) {
        this.uriInfo = uriInfo;
    }

    public String bodyBuilderHtml(String body, List<OutcomeAvailable> outcomes, UUID token) {

        return bodyBuilderInternal(body, outcomes, token, false);
    }

    public String bodyBuilderText(String body, List<OutcomeAvailable> outcomes, UUID token) {

        return bodyBuilderInternal(body, outcomes, token, true);
    }

    private String bodyBuilderInternal(String body, List<OutcomeAvailable> outcomes, UUID token, boolean flagText) {
        if (Strings.isNullOrEmpty(body)) {
            return null;
        }

        if (outcomes == null || outcomes.isEmpty()) {
            return body;
        }

        StringBuilder stringBuilder = new StringBuilder(body);

        if (flagText) {
            stringBuilder.append("\r\n\r\n");
        } else {
            stringBuilder.append("<br/><br/>");
        }

        for (OutcomeAvailable outcome: outcomes) {
            String decisionLinkName = outcome.getLabel();

            if (decisionLinkName.isEmpty()) {
                decisionLinkName = outcome.getDeveloperName();
            }

            if (flagText) {
                stringBuilder.append(String.format("%s - %scallback/response/%s/%s \r\n\r\n", decisionLinkName, uriInfo.getBaseUri(),
                        token, UrlEscapers.urlPathSegmentEscaper().escape(outcome.getDeveloperName())));
            } else {
                stringBuilder.append(String.format("<a href=\"%scallback/response/%s/%s\"> %s </a> &nbsp;", uriInfo.getBaseUri(),
                        token, UrlEscapers.urlPathSegmentEscaper().escape(outcome.getDeveloperName()), decisionLinkName));
            }
        }

        return stringBuilder.toString();
    }
}
