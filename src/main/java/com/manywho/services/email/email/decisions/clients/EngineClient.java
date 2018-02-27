package com.manywho.services.email.email.decisions.clients;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.manywho.sdk.api.ContentType;
import com.manywho.sdk.api.InvokeType;
import com.manywho.sdk.api.run.EngineInvokeResponse;
import com.manywho.sdk.api.run.EngineValue;
import com.manywho.sdk.api.run.elements.config.ServiceResponse;
import com.manywho.sdk.api.run.elements.map.OutcomeAvailable;
import com.manywho.sdk.client.flow.FlowState;
import com.manywho.sdk.client.run.RunClient;
import com.manywho.sdk.services.identity.AuthorizationEncoder;
import com.manywho.services.email.email.decisions.persistence.entities.EmailDecisionRequest;
import javax.inject.Inject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EngineClient {

    private RunClient runClient;
    private ObjectMapper objectMapper;
    private AuthorizationEncoder authorizationEncoder;

    @Inject
    public EngineClient(RunClient runClient, ObjectMapper objectMapper, AuthorizationEncoder authorizationEncoder) {
        this.runClient = runClient;
        this.objectMapper = objectMapper;
        this.authorizationEncoder = authorizationEncoder;
    }

    public String sendResponseToEngine(EmailDecisionRequest emailRequest, String stringResponse) {
        try {
            EngineInvokeResponse engineInvokeResponseCall = runClient.join(emailRequest.getTenantId(), emailRequest.getStateId())
                    .execute().body();

            runClient.callback(authorizationEncoder.encode(emailRequest.getAuthenticatedWho()), emailRequest.getTenantId(),
                    engineValuesToSend(emailRequest, stringResponse)).execute();

            FlowState flowState = new FlowState(runClient, emailRequest.getTenantId(), engineInvokeResponseCall);
            flowState.sync();

            return runClient.join(emailRequest.getTenantId().toString(), flowState.getState().toString()).execute().body().getJoinFlowUri();
        } catch (IOException e) {
            throw new RuntimeException("There was an unexpected error when re-joining the flow", e);
        }
    }

    private ServiceResponse engineValuesToSend(EmailDecisionRequest emailDecisionRequest, String stringResponse) {
        List<EngineValue> values = new ArrayList<>();
        values.add(new EngineValue("Recipient", ContentType.String, emailDecisionRequest.getEmail()));

        ServiceResponse serviceResponse = new ServiceResponse(InvokeType.Forward, values, emailDecisionRequest.getToken());
        serviceResponse.setTenantId(emailDecisionRequest.getTenantId());

        OutcomeAvailable selectedOutcome = emailDecisionRequest.getOutcomeAvailables().stream()
                .filter(outcome -> outcome.getDeveloperName().equals(stringResponse))
                .findFirst()
                .orElseThrow(()-> new RuntimeException("The map element must have at least one outcome when sending a decision email"));

        serviceResponse.setSelectedOutcomeId(selectedOutcome.getId());

        return serviceResponse;
    }
}
