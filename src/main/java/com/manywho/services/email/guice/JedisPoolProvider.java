package com.manywho.services.email.guice;

import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.manywho.services.email.configuration.ServiceConfigurationDefault;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class JedisPoolProvider implements Provider<JedisPool> {

    private ServiceConfigurationDefault serviceConfigurationDefault;

    @Inject
    public JedisPoolProvider(ServiceConfigurationDefault serviceConfigurationDefault) {
        this.serviceConfigurationDefault = serviceConfigurationDefault;
    }

    @Override
    public JedisPool get() {
        int port = 6379;

        if (Strings.isNullOrEmpty(serviceConfigurationDefault.get("redis.port")) == false) {
            port = Integer.parseInt(serviceConfigurationDefault.get("redis.port"));
        }

        JedisPool pool = new JedisPool(new JedisPoolConfig(), serviceConfigurationDefault.get("redis.url"), port, 2000);

        pool.addObjects(JedisPoolConfig.DEFAULT_MAX_IDLE);

        return pool;
    }
}
