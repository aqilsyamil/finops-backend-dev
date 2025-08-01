package com.example.poc_finops.cloudconnections.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@ComponentScan(basePackages = "com.example.poc_finops.cloudconnections")
@EntityScan(basePackages = "com.example.poc_finops.cloudconnections.domain.entity")
@EnableJpaRepositories(basePackages = "com.example.poc_finops.cloudconnections.repository")
public class CloudConnectionsModuleConfig {
}