package com.example.poc_finops.budgetmanagement.service.impl;

import com.example.poc_finops.budgetmanagement.api.dto.BudgetAlertDto;
import com.example.poc_finops.budgetmanagement.domain.entity.BudgetAlertsRecipients;
import com.example.poc_finops.budgetmanagement.repository.BudgetAlertsRecipientsRepository;
import com.example.poc_finops.budgetmanagement.repository.BudgetsRepository;
import com.example.poc_finops.budgetmanagement.service.BudgetAlertService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BudgetAlertServiceImpl implements BudgetAlertService {

    private final BudgetAlertsRecipientsRepository alertsRecipientsRepository;
    private final BudgetsRepository budgetsRepository;

    @Override
    @Transactional(readOnly = true)
    public List<BudgetAlertDto> getAlertRecipientsByBudget(UUID budgetId) {
        return alertsRecipientsRepository.findByBudgetId(budgetId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public BudgetAlertDto getAlertRecipientById(UUID id) {
        BudgetAlertsRecipients recipient = alertsRecipientsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Alert recipient not found with id: " + id));
        return convertToDto(recipient);
    }

    @Override
    public BudgetAlertDto addAlertRecipient(UUID budgetId, String email) {
        BudgetAlertsRecipients recipient = new BudgetAlertsRecipients();
        recipient.setBudget(budgetsRepository.findById(budgetId)
                .orElseThrow(() -> new RuntimeException("Budget not found with id: " + budgetId)));
        recipient.setEmail(email);

        BudgetAlertsRecipients savedRecipient = alertsRecipientsRepository.save(recipient);
        log.info("Added alert recipient: {} for budget: {}", email, budgetId);
        return convertToDto(savedRecipient);
    }

    @Override
    public void removeAlertRecipient(UUID id) {
        alertsRecipientsRepository.deleteById(id);
        log.info("Removed alert recipient: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BudgetAlertDto> getAlertRecipientsByEmail(String email) {
        return alertsRecipientsRepository.findByEmail(email).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private BudgetAlertDto convertToDto(BudgetAlertsRecipients recipient) {
        BudgetAlertDto dto = new BudgetAlertDto();
        dto.setId(recipient.getId());
        dto.setBudgetId(recipient.getBudget().getId());
        dto.setEmail(recipient.getEmail());
        dto.setCreatedAt(recipient.getCreatedAt());
        dto.setUpdatedAt(recipient.getUpdatedAt());
        dto.setCreatedBy(recipient.getCreatedBy() != null ? recipient.getCreatedBy().getId() : null);
        dto.setUpdatedBy(recipient.getUpdatedBy() != null ? recipient.getUpdatedBy().getId() : null);
        return dto;
    }
}