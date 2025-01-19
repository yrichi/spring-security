package com.kata.springsecurity.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.PostgreSQLContainer;

@TestConfiguration
public class TestContainersConfig {

    @Bean
    public PostgreSQLContainer<?> postgresContainer() {
        PostgreSQLContainer<?> container = new PostgreSQLContainer<>("postgres:15")
                .withDatabaseName("testdb")
                .withUsername("testuser")
                .withPassword("testpassword");
        container.start();
        return container;
    }
    }