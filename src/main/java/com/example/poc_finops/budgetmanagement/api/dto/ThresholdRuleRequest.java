package com.example.poc_finops.budgetmanagement.api.dto;

import lombok.Data;


@Data
public class ThresholdRuleRequest {
    private Double percentage;
    private Double amount;
    private String triggerType = "actual cost";
}