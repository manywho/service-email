package com.manywho.services.email.email.decisions;

import com.google.common.base.Strings;
import com.google.common.net.UrlEscapers;
import com.manywho.sdk.api.run.elements.map.OutcomeAvailable;
import com.manywho.sdk.api.run.elements.map.OutcomeResponse;

import javax.inject.Inject;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.List;
import java.util.UUID;

public class DecisionBodyBuilder {
    private UriInfo uriInfo;
    private HttpHeaders headers;

    @Inject
    public DecisionBodyBuilder(@Context UriInfo uriInfo, @Context HttpHeaders headers) {
        this.uriInfo = uriInfo;
        this.headers = headers;
    }

    public String bodyBuilderHtml(String body, List<OutcomeResponse> outcomes, UUID token) {

        return bodyBuilderInternal(body, outcomes, token, false);
    }

    public String bodyBuilderText(String body, List<OutcomeResponse> outcomes, UUID token) {

        return bodyBuilderInternal(body, outcomes, token, true);
    }

    private String bodyBuilderInternal(String body, List<OutcomeResponse> outcomes, UUID token, boolean flagText) {
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

            if (decisionLinkName == null || decisionLinkName.isEmpty()) {
                decisionLinkName = outcome.getDeveloperName();
            }

            if (flagText) {
                stringBuilder.append(String.format("%s - %scallback/response/%s/%s \r\n\r\n", decisionLinkName, baseUri() ,
                        token, UrlEscapers.urlPathSegmentEscaper().escape(outcome.getDeveloperName())));
            } else {
                stringBuilder.append(String.format("<a href=\"%scallback/response/%s/%s\"> %s </a> &nbsp;", baseUri() ,
                        token, UrlEscapers.urlPathSegmentEscaper().escape(outcome.getDeveloperName()), decisionLinkName));
            }
        }

        return stringBuilder.toString();
    }

    /**
     * if there is a header with X-Forwarded-Proto will use the protocol indicated there, if there isn't then
     * it will use the one in uriInfo
     *
     * @return
     */
    private URI baseUri() {
        String protocol = headers.getRequestHeaders().getFirst("X-Forwarded-Proto");

        if (Strings.isNullOrEmpty(protocol) == false) {
            UriBuilder uri = uriInfo.getBaseUriBuilder();
            uri.scheme(protocol);

            return uri.build();
        } else {
            return uriInfo.getBaseUri();
        }
    }
}
