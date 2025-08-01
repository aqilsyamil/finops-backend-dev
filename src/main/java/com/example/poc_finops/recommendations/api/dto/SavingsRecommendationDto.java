package com.example.poc_finops.recommendations.api.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class SavingsRecommendationDto {
    private UUID id;
    private String recommendationId;
    private String actionType;
    private String resourceArn;
    private String region;
    private String currencyCode;
    
    // Cost metrics
    private Double currentMonthlyCost;
    private Double recommendedMonthlyCost;
    private Double monthlySavings;
    private Double savingsPercentage;
    private Double annualSavings;
    
    // Implementation details
    private String implementationEffort;
    private Boolean restartNeeded;
    private Boolean rollbackPossible;
    private String recommendationSource;
    
    // Resource details
    private String currentResourceType;
    private String recommendedResourceType;
    private String currentResourceSummary;
    private String recommendedResourceSummary;
}