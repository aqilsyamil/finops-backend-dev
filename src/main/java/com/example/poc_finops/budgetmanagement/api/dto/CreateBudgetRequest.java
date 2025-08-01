package com.example.poc_finops.budgetmanagement.api.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
public class CreateBudgetRequest {
    
    @NotNull(message = "Organization ID is required")
    private UUID organizationId;
    
    @NotNull(message = "CSP Connection ID is required")
    private UUID cspConnectionId;
    
    @NotNull(message = "Budget name is required")
    private String name;
    
    @NotNull(message = "Time range is required")
    private String timeRange; // monthly, quarterly, yearly
    
    private String renewalType = "recurring";
    
    @NotNull(message = "Start month is required")
    private LocalDate startMonth;
    
    private String budgetType = "fixed";
    
    @NotNull(message = "Budget amount is required")
    @Positive(message = "Budget amount must be positive")
    private Double amount;
    
    // Service Selection
    private List<UUID> selectedServiceIds;
    
    // Alert Threshold Rules
    private List<ThresholdRuleRequest> thresholdRules;
    
    // Alert Recipients
    private List<String> alertRecipients;
}