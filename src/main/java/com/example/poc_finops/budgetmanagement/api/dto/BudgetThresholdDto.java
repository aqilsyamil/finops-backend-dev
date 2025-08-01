package com.example.poc_finops.budgetmanagement.api.dto;

import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
public class BudgetThresholdDto {
    private UUID id;
    private UUID budgetId;
    private Double percentage;
    private Double amount;
    private String triggerType;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private UUID createdBy;
    private UUID updatedBy;
}