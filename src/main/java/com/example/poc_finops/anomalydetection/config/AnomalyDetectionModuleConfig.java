package com.example.poc_finops.anomalydetection.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@ComponentScan(basePackages = "com.example.poc_finops.anomalydetection")
@EntityScan(basePackages = "com.example.poc_finops.anomalydetection.domain.entity")
@EnableJpaRepositories(basePackages = "com.example.poc_finops.anomalydetection.repository")
public class AnomalyDetectionModuleConfig {
}