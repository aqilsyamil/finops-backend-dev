package com.example.poc_finops.recommendations.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@ComponentScan(basePackages = "com.example.poc_finops.recommendations")
@EntityScan(basePackages = "com.example.poc_finops.recommendations.domain.entity")
@EnableJpaRepositories(basePackages = "com.example.poc_finops.recommendations.repository")
@EnableTransactionManagement
@Slf4j
public class RecommendationsModuleConfig {

    public RecommendationsModuleConfig() {
        log.info("Initializing Recommendations Module");
    }
}