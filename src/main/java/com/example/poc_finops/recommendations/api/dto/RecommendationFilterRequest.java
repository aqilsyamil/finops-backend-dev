package com.example.poc_finops.recommendations.api.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
public class RecommendationFilterRequest {
    private UUID focusLogId;
    private List<String> actionTypes;
    private List<String> implementationEfforts;
    private List<String> regions;
    private String recommendationSource;
    private BigDecimal minSavings;
    private BigDecimal maxSavings;
    private BigDecimal minSavingsPercentage;
    private String currencyCode;
    private Boolean restartNeeded;
    private Boolean rollbackPossible;
    private List<String> resourceTypes;
    private Integer limit = 100; // Default limit
}