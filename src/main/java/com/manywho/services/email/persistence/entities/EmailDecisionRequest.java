package com.manywho.services.email.persistence.entities;

import com.manywho.services.email.types.DecisionChoice;
import java.util.List;

public class EmailDecisionRequest {
    private String tenantId;
    private String token;
    private String email;
    private List<DecisionChoice> decisionChoices;

    public EmailDecisionRequest() {
    }

    public EmailDecisionRequest(String tenantId, String token, String email, List<DecisionChoice> decisionChoices) {
        this.tenantId = tenantId;
        this.token = token;
        this.email = email;
        this.decisionChoices = decisionChoices;
    }

    public String getTenantId() {
        return tenantId;
    }

    public String getToken() {
        return token;
    }

    public String getEmail() {
        return email;
    }

    public List<DecisionChoice> getDecisionChoices() {
        return decisionChoices;
    }

    public void setDecisionChoices(List<DecisionChoice> decisionChoices) {
        this.decisionChoices = decisionChoices;
    }
}
