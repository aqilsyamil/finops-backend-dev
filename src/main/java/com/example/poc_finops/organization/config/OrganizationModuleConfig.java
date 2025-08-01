package com.example.poc_finops.organization.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@ComponentScan(basePackages = "com.example.poc_finops.organization")
@EntityScan(basePackages = "com.example.poc_finops.organization.domain.entity")
@EnableJpaRepositories(basePackages = "com.example.poc_finops.organization.repository")
public class OrganizationModuleConfig {
}