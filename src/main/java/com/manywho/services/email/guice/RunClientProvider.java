package com.manywho.services.email.guice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.manywho.sdk.client.run.RunClient;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class RunClientProvider implements Provider<RunClient>{
    private ObjectMapper objectMapper;

    @Inject
    public RunClientProvider(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public RunClient get() {
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(JacksonConverterFactory.create(objectMapper))
                .baseUrl("https://flow.boomi.com")
                .build();

        return retrofit.create(RunClient.class);
    }
}
