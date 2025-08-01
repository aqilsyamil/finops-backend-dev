package com.example.poc_finops.recommendations.domain.event;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
public class RecommendationGeneratedEvent {
    private UUID recommendationId;
    private UUID focusLogId;
    private String actionType;
    private String recommendationSource;
    private BigDecimal estimatedMonthlySavings;
    private BigDecimal savingsPercentage;
    private String implementationEffort;
    private String region;
    private String resourceArn;
    private UUID createdBy;
    private OffsetDateTime generatedAt;
}