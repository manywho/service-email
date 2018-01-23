package com.manywho.services.email.email;

import com.manywho.services.email.clients.EngineClient;
import com.manywho.services.email.persistence.CacheManager;
import com.manywho.services.email.persistence.entities.EmailDecisionRequest;
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
        String joinUrl = engineClient.sendResponseToEngine(emailRequest, decodedResponse);

        return Response.seeOther(URI.create(joinUrl)).build();
    }
}
