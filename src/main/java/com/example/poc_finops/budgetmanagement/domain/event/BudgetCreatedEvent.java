package com.example.poc_finops.budgetmanagement.domain.event;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
public class BudgetCreatedEvent {
    private UUID budgetId;
    private UUID organizationId;
    private UUID cspConnectionId;
    private String budgetName;
    private BigDecimal amount;
    private String timeRange;
    private UUID createdBy;
    private OffsetDateTime createdAt;
}