package com.manywho.services.email.persistence.entities;

import com.manywho.sdk.api.run.elements.map.OutcomeAvailable;
import java.util.List;
import java.util.UUID;

public class EmailDecisionRequest {
    private UUID tenantId;
    private String token;
    private UUID stateId;
    private String email;
    private String authorization;

    private List<OutcomeAvailable> outcomeAvailables;

    public EmailDecisionRequest() {
    }

    public EmailDecisionRequest(UUID tenantId, String token, String email, UUID stateId,
                                List<OutcomeAvailable> outcomeAvailables, String authorization) {
        this.tenantId = tenantId;
        this.token = token;
        this.email = email;
        this.stateId = stateId;
        this.outcomeAvailables = outcomeAvailables;
        this.authorization = authorization;
    }

    public UUID getTenantId() {
        return tenantId;
    }

    public String getToken() {
        return token;
    }

    public String getEmail() {
        return email;
    }

    public UUID getStateId() {
        return stateId;
    }

    public List<OutcomeAvailable> getOutcomeAvailables() {
        return outcomeAvailables;
    }

    public String getAuthorization() {
        return authorization;
    }
}
