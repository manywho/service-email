package com.manywho.services.email.guice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Provider;
import com.manywho.sdk.api.jackson.ObjectMapperFactory;

public class ObjectMapperProvider implements Provider<ObjectMapper>{
    @Override
    public ObjectMapper get() {
        return ObjectMapperFactory.create();
    }
}
