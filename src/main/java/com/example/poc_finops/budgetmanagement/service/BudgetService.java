package com.example.poc_finops.budgetmanagement.service;

import com.example.poc_finops.budgetmanagement.api.dto.BudgetDto;
import com.example.poc_finops.budgetmanagement.api.dto.CreateBudgetRequest;

import java.util.List;
import java.util.UUID;

public interface BudgetService {
    List<BudgetDto> getAllBudgets();
    List<BudgetDto> getBudgetsByOrganization(UUID organizationId);
    BudgetDto getBudgetById(UUID id);
    BudgetDto createBudget(CreateBudgetRequest request);
    BudgetDto updateBudget(UUID id, CreateBudgetRequest request);
    void deleteBudget(UUID id);
    List<BudgetDto> getBudgetsByOrganizationAndConnection(UUID organizationId, UUID cspConnectionId);
}