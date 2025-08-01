package com.example.poc_finops.budgetmanagement.api.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class BudgetServiceDto {
    private UUID id;
    private String serviceName;
    private String serviceId;
    private boolean selected;
}