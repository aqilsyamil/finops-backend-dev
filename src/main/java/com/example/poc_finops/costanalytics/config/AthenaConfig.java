package com.example.poc_finops.costanalytics.config;

import com.example.poc_finops.cloudconnections.domain.entity.CspConnection;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.athena.AthenaClient;

import java.time.Duration;

@Configuration
public class AthenaConfig {

    @Value("${aws.athena.workgroup:primary}")
    private String defaultWorkgroup;

    @Value("${aws.athena.cost-per-tb-usd:5.00}")
    private Double costPerTbUsd;

    @Value("${aws.athena.api-call-timeout-seconds:600}")
    private Integer apiCallTimeoutSeconds;

    @Value("${aws.athena.api-attempt-timeout-seconds:120}")
    private Integer apiAttemptTimeoutSeconds;

    @Value("${aws.athena.query-execution-timeout-minutes:15}")
    private Integer queryExecutionTimeoutMinutes;

    public AthenaClient createAthenaClient(CspConnection cspConnection) {
        AwsBasicCredentials credentials = AwsBasicCredentials.create(
                cspConnection.getAccessKeyId(),
                cspConnection.getSecretKeyId()
        );

        ClientOverrideConfiguration clientConfig = ClientOverrideConfiguration.builder()
                .apiCallTimeout(Duration.ofSeconds(apiCallTimeoutSeconds))
                .apiCallAttemptTimeout(Duration.ofSeconds(apiAttemptTimeoutSeconds))
                .build();

        return AthenaClient.builder()
                .region(Region.of(cspConnection.getRegion().getName()))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .overrideConfiguration(clientConfig)
                .build();
    }

    public String getDefaultWorkgroup() {
        return defaultWorkgroup;
    }

    public Double getCostPerTbUsd() {
        return costPerTbUsd;
    }

    public Integer getQueryExecutionTimeoutMinutes() {
        return queryExecutionTimeoutMinutes;
    }
}