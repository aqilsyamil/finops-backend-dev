package com.example.poc_finops.budgetmanagement.service;

import com.example.poc_finops.budgetmanagement.api.dto.BudgetThresholdDto;

import java.util.List;
import java.util.UUID;

public interface BudgetThresholdService {
    List<BudgetThresholdDto> getThresholdsByBudget(UUID budgetId);
    BudgetThresholdDto getThresholdById(UUID id);
    BudgetThresholdDto createThreshold(UUID budgetId, Double percentage, Double amount, String triggerType);
    BudgetThresholdDto updateThreshold(UUID id, Double percentage, Double amount, String triggerType);
    void deleteThreshold(UUID id);
    List<BudgetThresholdDto> getThresholdsByTriggerType(String triggerType);
}