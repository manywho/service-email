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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.Response;

import javax.inject.Inject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EngineClient {

    private RunClient runClient;
    private AuthorizationEncoder authorizationEncoder;
    private ObjectMapper objectMapper;

    private static final Logger LOGGER = LoggerFactory.getLogger(EngineClient.class);

    @Inject
    public EngineClient(RunClient runClient, AuthorizationEncoder authorizationEncoder, ObjectMapper objectMapper) {
        this.runClient = runClient;
        this.objectMapper = objectMapper;
        this.authorizationEncoder = authorizationEncoder;
    }

    public String sendResponseToEngine(EmailDecisionRequest emailRequest, String stringResponse) {
        try {
            Response<EngineInvokeResponse> response = runClient.join(emailRequest.getTenantId(), emailRequest.getStateId())
                    .execute();

            if (response.isSuccessful() == false) {
                LOGGER.error("The response from engine is not successful", objectMapper.writeValueAsString(response.message()));
                throw new RuntimeException(response.message());
            }

            EngineInvokeResponse engineInvokeResponseCall = response.body();

            Response<InvokeType> callbackResponse = runClient.callback(authorizationEncoder.encode(emailRequest.getAuthenticatedWho()), emailRequest.getTenantId(),
                    engineValuesToSend(emailRequest, stringResponse)).execute();

            LOGGER.info("callback response", objectMapper.writeValueAsString(callbackResponse.body()));

            FlowState flowState = new FlowState(runClient, emailRequest.getTenantId(), engineInvokeResponseCall);

            if (engineInvokeResponseCall != null) {
                flowState.sync();
                LOGGER.info("flow state", flowState);
            }

            EngineInvokeResponse invokeResponse = runClient.join(emailRequest.getTenantId().toString(), flowState.getState().toString()).execute().body();
            LOGGER.info("engine join response", invokeResponse);

            return invokeResponse.getJoinFlowUri();
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
