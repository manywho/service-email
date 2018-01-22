package com.manywho.services.email.clients;

import com.manywho.sdk.api.ContentType;
import com.manywho.sdk.api.InvokeType;
import com.manywho.sdk.api.run.EngineValue;
import com.manywho.sdk.api.run.elements.config.ServiceResponse;
import com.manywho.sdk.client.run.RunClient;
import com.manywho.services.email.persistence.entities.EmailDecisionRequest;
import retrofit2.Call;

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

    public void sendResponseToEngine(EmailDecisionRequest emailRequest, String stringResponse) {
        List<EngineValue> values = new ArrayList<>();
        EngineValue valueResponse = new EngineValue("Response", ContentType.String, stringResponse);
        values.add(valueResponse);

        EngineValue valueRecipient = new EngineValue("Recipient", ContentType.String, emailRequest.getEmail());
        values.add(valueRecipient);

        ServiceResponse serviceResponse = new ServiceResponse(InvokeType.Forward, values, emailRequest.getToken());
        serviceResponse.setTenantId(UUID.fromString(emailRequest.getTenantId()));

        Call<InvokeType> invokeType = runClient.callback(null, UUID.fromString(emailRequest.getTenantId()), serviceResponse);

        try {
            invokeType.execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
