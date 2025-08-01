package com.example.poc_finops.recommendations.domain.event;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
public class RecommendationAppliedEvent {
    private UUID recommendationId;
    private String actionType;
    private String resourceArn;
    private String region;
    private BigDecimal actualSavings;
    private BigDecimal estimatedSavings;
    private String implementationStatus;
    private Boolean restartRequired;
    private Boolean rollbackPossible;
    private UUID appliedBy;
    private OffsetDateTime appliedAt;
}