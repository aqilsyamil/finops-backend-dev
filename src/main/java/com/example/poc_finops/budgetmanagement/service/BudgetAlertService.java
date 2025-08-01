package com.example.poc_finops.budgetmanagement.service;

import com.example.poc_finops.budgetmanagement.api.dto.BudgetAlertDto;

import java.util.List;
import java.util.UUID;

public interface BudgetAlertService {
    List<BudgetAlertDto> getAlertRecipientsByBudget(UUID budgetId);
    BudgetAlertDto getAlertRecipientById(UUID id);
    BudgetAlertDto addAlertRecipient(UUID budgetId, String email);
    void removeAlertRecipient(UUID id);
    List<BudgetAlertDto> getAlertRecipientsByEmail(String email);
}