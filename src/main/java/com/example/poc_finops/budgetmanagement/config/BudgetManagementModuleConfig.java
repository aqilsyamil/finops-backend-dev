package com.example.poc_finops.budgetmanagement.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@ComponentScan(basePackages = "com.example.poc_finops.budgetmanagement")
@EntityScan(basePackages = "com.example.poc_finops.budgetmanagement.domain.entity")
@EnableJpaRepositories(basePackages = "com.example.poc_finops.budgetmanagement.repository")
@EnableTransactionManagement
@Slf4j
public class BudgetManagementModuleConfig {

    public BudgetManagementModuleConfig() {
        log.info("Initializing Budget Management Module");
    }
}