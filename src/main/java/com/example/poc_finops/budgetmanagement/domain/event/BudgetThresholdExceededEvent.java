package com.example.poc_finops.budgetmanagement.domain.event;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
public class BudgetThresholdExceededEvent {
    private UUID budgetId;
    private UUID thresholdRuleId;
    private String budgetName;
    private BigDecimal budgetAmount;
    private BigDecimal currentSpend;
    private BigDecimal thresholdAmount;
    private BigDecimal thresholdPercentage;
    private String triggerType;
    private UUID organizationId;
    private OffsetDateTime triggeredAt;
}