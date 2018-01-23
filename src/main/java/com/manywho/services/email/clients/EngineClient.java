package com.manywho.services.email.clients;

import com.manywho.sdk.api.ContentType;
import com.manywho.sdk.api.InvokeType;
import com.manywho.sdk.api.run.EngineInvokeResponse;
import com.manywho.sdk.api.run.EngineValue;
import com.manywho.sdk.api.run.elements.config.ServiceResponse;
import com.manywho.sdk.api.run.elements.map.OutcomeAvailable;
import com.manywho.sdk.client.flow.FlowState;
import com.manywho.sdk.client.run.RunClient;
import com.manywho.services.email.persistence.entities.EmailDecisionRequest;
import javax.inject.Inject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class EngineClient {

    private RunClient runClient;

    @Inject
    public EngineClient(RunClient runClient) {
        this.runClient = runClient;
    }

    public String sendResponseToEngine(EmailDecisionRequest emailRequest, String stringResponse) {
        try {
            EngineInvokeResponse engineInvokeResponseCall = runClient.join(emailRequest.getTenantId(), emailRequest.getStateId())
                    .execute().body();

            runClient.callback(emailRequest.getAuthorization(), emailRequest.getTenantId(), engineValuesToSend(emailRequest, stringResponse))
                    .execute();

            FlowState flowState = new FlowState(runClient, emailRequest.getTenantId(), engineInvokeResponseCall);
            flowState.sync();

            return runClient.join(emailRequest.getTenantId().toString(), flowState.getState().toString()).execute().body().getJoinFlowUri();
        } catch (IOException e) {
            throw new RuntimeException("Error while sending response to engine", e);
        }
    }

    private ServiceResponse engineValuesToSend(EmailDecisionRequest emailDecisionRequest, String stringResponse) {
        List<EngineValue> values = new ArrayList<>();
        values.add(new EngineValue("Recipient", ContentType.String, emailDecisionRequest.getEmail()));

        ServiceResponse serviceResponse = new ServiceResponse(InvokeType.Forward, values, emailDecisionRequest.getToken());
        serviceResponse.setTenantId(emailDecisionRequest.getTenantId());
        UUID selectedOutcomeId = getOutcomeId(stringResponse,emailDecisionRequest.getOutcomeAvailables()).getId();
        serviceResponse.setSelectedOutcomeId(selectedOutcomeId);

        return serviceResponse;
    }

    private OutcomeAvailable getOutcomeId(String stringResponse, List<OutcomeAvailable> outcomeAvailables) {
        for (OutcomeAvailable outcome:outcomeAvailables) {
            if (outcome.getDeveloperName().equals(stringResponse)) {
                return outcome;
            }
        }

        throw new RuntimeException("outcome not found");
    }
}
