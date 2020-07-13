package com.manywho.services.email.email.decisions;

import com.manywho.services.email.email.decisions.clients.EngineClient;
import com.manywho.services.email.email.decisions.persistence.EmailDecisionRepository;
import com.manywho.services.email.email.decisions.persistence.entities.EmailDecisionRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;

@Path("/callback")
public class ReceiveDecision {
    private EngineClient engineClient;
    private EmailDecisionRepository emailDecisionRepository;
    private static final Logger LOGGER = LoggerFactory.getLogger(ReceiveDecision.class);

    @Inject
    public ReceiveDecision(EmailDecisionRepository emailDecisionRepository, EngineClient engineClient) {
        this.engineClient = engineClient;
        this.emailDecisionRepository = emailDecisionRepository;
    }

    @GET
    @Path("/response/{code}/{response}")
    @Produces(MediaType.TEXT_HTML)
    public Response receivedDecisionResponse(@PathParam("code") String code, @PathParam("response") String response) {
        EmailDecisionRequest emailRequest = emailDecisionRepository.getEmailDecisionRequest(code);
        String joinUrl = engineClient.sendResponseToEngine(emailRequest, response);

        return Response.temporaryRedirect(URI.create(joinUrl)).build();
    }
}
