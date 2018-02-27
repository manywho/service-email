package com.manywho.services.email.email.decisions.persistence.entities;

import com.manywho.sdk.api.run.elements.map.OutcomeAvailable;
import com.manywho.sdk.api.security.AuthenticatedWho;

import java.util.List;
import java.util.UUID;

public class EmailDecisionRequest {
    private UUID tenantId;
    private String token;
    private UUID stateId;
    private String email;
    private AuthenticatedWho authenticatedWho;
    private List<OutcomeAvailable> outcomeAvailables;

    public EmailDecisionRequest() {
    }

    public EmailDecisionRequest(UUID tenantId, String token, String email, UUID stateId, List<OutcomeAvailable> outcomeAvailables,
                                AuthenticatedWho authenticatedWho) {
        this.tenantId = tenantId;
        this.token = token;
        this.email = email;
        this.stateId = stateId;
        this.outcomeAvailables = outcomeAvailables;
        this.authenticatedWho = authenticatedWho;
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

    public AuthenticatedWho getAuthenticatedWho() {
        return authenticatedWho;
    }
}
