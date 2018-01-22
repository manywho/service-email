package com.manywho.services.email.email;

import com.google.common.base.Strings;
import com.manywho.services.email.clients.EngineClient;
import com.manywho.services.email.persistence.CacheManager;
import com.manywho.services.email.persistence.entities.EmailDecisionRequest;
import com.manywho.services.email.types.DecisionChoice;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.*;


@Path("/callback")
public class ReceiveDecision {
    private Base64.Decoder decoder;
    private EngineClient engineClient;
    private CacheManager cacheManager;

    @Inject
    public ReceiveDecision(CacheManager cacheManager, EngineClient engineClient) {
        decoder = Base64.getUrlDecoder();
        this.engineClient = engineClient;
        this.cacheManager = cacheManager;
    }

    @GET
    @Path("/response/{code}/{response}")
    @Produces(MediaType.TEXT_HTML)
    public Response receivedDecisionResponse(@PathParam("code") String code, @PathParam("response") String response) {
        String decodedResponse = new String(decoder.decode(response));
        EmailDecisionRequest emailRequest = cacheManager.getEmailDecisionRequest(code);

        engineClient.sendResponseToEngine(emailRequest, decodedResponse);

        for (DecisionChoice choice:emailRequest.getDecisionChoices()) {
            if (Objects.equals(choice.getLabel(), decodedResponse) && Strings.isNullOrEmpty(choice.getRedirect()) == false) {
                return Response.seeOther(URI.create(choice.getRedirect())).build();
            }
        }

        // only executed if there is not redirect configured for that choice
        String body = "<!doctype html> <html lang=\"en\"><head><meta charset=\"utf-8\"><title>Response Sent</title></head>" +
                "<body><p>Thank you</p><p>Your response have been sent.</p></body></html>";

        return Response.status(200).entity(body).build();
    }
}
