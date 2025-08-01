package com.example.poc_finops.cloudconnections.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sts.StsClient;
import software.amazon.awssdk.services.sts.model.GetCallerIdentityRequest;
import software.amazon.awssdk.services.sts.model.GetCallerIdentityResponse;
import software.amazon.awssdk.services.sts.model.StsException;

@Service
@Slf4j
public class AwsCredentialValidationService {

    public ValidationResult validateCredentials(String accessKeyId, String secretAccessKey, String regionName) {
        try {
            log.info("Validating AWS credentials for access key: {}", maskAccessKey(accessKeyId));
            
            // Create AWS credentials
            AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKeyId, secretAccessKey);
            
            // Parse region
            Region region;
            try {
                region = Region.of(regionName);
            } catch (Exception e) {
                log.warn("Invalid region specified: {}, using default us-east-1", regionName);
                region = Region.US_EAST_1;
            }
            
            // Create STS client
            try (StsClient stsClient = StsClient.builder()
                    .region(region)
                    .credentialsProvider(StaticCredentialsProvider.create(credentials))
                    .build()) {
                
                // Test credentials by calling GetCallerIdentity
                GetCallerIdentityResponse response = stsClient.getCallerIdentity(
                    GetCallerIdentityRequest.builder().build()
                );
                
                log.info("AWS credentials validated successfully. Account: {}, UserId: {}", 
                        response.account(), maskUserId(response.userId()));
                
                return ValidationResult.success(response.account(), response.arn(), response.userId());
                
            }
        } catch (StsException e) {
            log.error("AWS credential validation failed: {}", e.awsErrorDetails().errorMessage());
            return ValidationResult.failure("AWS credential validation failed: " + e.awsErrorDetails().errorMessage());
        } catch (Exception e) {
            log.error("Unexpected error during AWS credential validation", e);
            return ValidationResult.failure("Unexpected error during credential validation: " + e.getMessage());
        }
    }
    
    private String maskAccessKey(String accessKey) {
        if (accessKey == null || accessKey.length() < 8) {
            return "****";
        }
        return accessKey.substring(0, 4) + "****" + accessKey.substring(accessKey.length() - 4);
    }
    
    private String maskUserId(String userId) {
        if (userId == null || userId.length() < 8) {
            return "****";
        }
        return userId.substring(0, 4) + "****" + userId.substring(userId.length() - 4);
    }
    
    public static class ValidationResult {
        private final boolean success;
        private final String errorMessage;
        private final String accountId;
        private final String arn;
        private final String userId;
        
        private ValidationResult(boolean success, String errorMessage, String accountId, String arn, String userId) {
            this.success = success;
            this.errorMessage = errorMessage;
            this.accountId = accountId;
            this.arn = arn;
            this.userId = userId;
        }
        
        public static ValidationResult success(String accountId, String arn, String userId) {
            return new ValidationResult(true, null, accountId, arn, userId);
        }
        
        public static ValidationResult failure(String errorMessage) {
            return new ValidationResult(false, errorMessage, null, null, null);
        }
        
        public boolean isSuccess() {
            return success;
        }
        
        public String getErrorMessage() {
            return errorMessage;
        }
        
        public String getAccountId() {
            return accountId;
        }
        
        public String getArn() {
            return arn;
        }
        
        public String getUserId() {
            return userId;
        }
    }
}