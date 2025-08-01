package com.example.poc_finops.budgetmanagement.api.dto;

import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
public class BudgetAlertDto {
    private UUID id;
    private UUID budgetId;
    private String email;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private UUID createdBy;
    private UUID updatedBy;
}