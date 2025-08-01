package com.example.poc_finops.budgetmanagement.api.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class BudgetDto {
    private UUID id;
    private UUID organizationId;
    private UUID cspConnectionId;
    private String name;
    private String timeRange;
    private String renewalType;
    private LocalDate startMonth;
    private String budgetType;
    private Double amount;
    
    // Service Selection Information
    private List<BudgetServiceDto> selectedServices;
    
    // Alert Threshold Rules
    private List<BudgetThresholdDto> thresholdRules;
    
    // Alert Recipients
    private List<BudgetAlertDto> alertRecipients;
    
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private UUID createdBy;
    private UUID updatedBy;
}