package com.manywho.services.email.configuration;

public interface ServiceConfiguration {
    String get(String key);
    boolean has(String key);
}