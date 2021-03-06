package com.manywho.services.email.email.decisions.persistence;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.manywho.services.email.email.decisions.persistence.entities.EmailDecisionRequest;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.inject.Inject;
import java.io.IOException;
import java.util.UUID;

public class EmailDecisionRepository {

    public final static String REDIS_KEY_EMAIL_REQUEST = "service:email:requests:%s";
    private JedisPool jedisPool;
    private ObjectMapper objectMapper;

    @Inject
    public EmailDecisionRepository(JedisPool jedisPool, ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.jedisPool = jedisPool;
    }

    public void saveEmailDecisionRequest(UUID code, EmailDecisionRequest request) {
        try(Jedis jedis = jedisPool.getResource()) {
            jedis.set(String.format(REDIS_KEY_EMAIL_REQUEST, code), objectMapper.writeValueAsString(request));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("There was an error while serializing the request for caching", e);
        }
    }

    public EmailDecisionRequest getEmailDecisionRequest(String code) {
        try(Jedis jedis = jedisPool.getResource()) {
            String json = jedis.get(String.format(REDIS_KEY_EMAIL_REQUEST, code));

            if (Strings.isNullOrEmpty(json) == false) {
                try {
                    return objectMapper.readValue(json, EmailDecisionRequest.class);
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            }
        }

        throw new RuntimeException("Email request not found");
    }
}
