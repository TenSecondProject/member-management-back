package org.colcum.admin.global.config;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

public class RedisTestContainerConfiguration implements BeforeAllCallback {

    private static final String REDIS_IMAGE = "redis:7.4.0-alpine";
    private static final int REDIS_INTERNAL_PORT = 6379;

    @Override
    public void beforeAll(ExtensionContext context) {
//        GenericContainer redis = new GenericContainer(DockerImageName.parse(REDIS_IMAGE))
//            .withExposedPorts(REDIS_INTERNAL_PORT);
//        redis.start();
//        System.setProperty("spring.data.redis.host", redis.getHost());
//        System.setProperty("spring.data.redis.port", String.valueOf(redis.getMappedPort(REDIS_INTERNAL_PORT
//        )));
    }

}
