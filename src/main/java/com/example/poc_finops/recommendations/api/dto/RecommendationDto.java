package com.example.poc_finops.recommendations.api.dto;

import lombok.Data;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

@Data
public class RecommendationDto {
    private UUID id;
    private UUID focusLogId;
    private String accountId;
    private String actionType;
    private String currencyCode;
    private String currentResourceDetails;
    private String currentResourceSummary;
    private String currentResourceType;
    private Double estimatedMonthlyCostAfterDiscount;
    private Double estimatedMonthlyCostBeforeDiscount;
    private Double estimatedMonthlySavingsAfterDiscount;
    private Double estimatedSavingsPercentageAfterDiscount;
    private Double estimatedSavingsPercentageBeforeDiscount;
    private Double estimatedMonthlySavingsBeforeDiscount;
    private String implementationEffort;
    private OffsetDateTime lastRefreshTimestamp;
    private String recommendationId;
    private Integer recommendationLookbackPeriodInDays;
    private String recommendationSource;
    private String recommendedResourceDetails;
    private String recommendedResourceSummary;
    private String recommendedResourceType;
    private String region;
    private String resourceArn;
    private Boolean restartNeeded;
    private Boolean rollbackPossible;
    private Map<String, Object> tags;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private UUID createdBy;
    private UUID updatedBy;
}