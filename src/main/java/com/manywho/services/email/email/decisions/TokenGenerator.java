package com.manywho.services.email.email.decisions;

import com.google.inject.Inject;

import java.util.UUID;

public class TokenGenerator {
    @Inject
    public TokenGenerator(){}

    public UUID generateRandomUUID() {
        return UUID.randomUUID();
    }
}
