package com.example.poc_finops.budgetmanagement.service.impl;

import com.example.poc_finops.budgetmanagement.api.dto.BudgetThresholdDto;
import com.example.poc_finops.budgetmanagement.domain.entity.BudgetThresholdRules;
import com.example.poc_finops.budgetmanagement.repository.BudgetThresholdRulesRepository;
import com.example.poc_finops.budgetmanagement.repository.BudgetsRepository;
import com.example.poc_finops.budgetmanagement.service.BudgetThresholdService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BudgetThresholdServiceImpl implements BudgetThresholdService {

    private final BudgetThresholdRulesRepository thresholdRulesRepository;
    private final BudgetsRepository budgetsRepository;

    @Override
    @Transactional(readOnly = true)
    public List<BudgetThresholdDto> getThresholdsByBudget(UUID budgetId) {
        return thresholdRulesRepository.findByBudgetId(budgetId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public BudgetThresholdDto getThresholdById(UUID id) {
        BudgetThresholdRules threshold = thresholdRulesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Threshold rule not found with id: " + id));
        return convertToDto(threshold);
    }

    @Override
    public BudgetThresholdDto createThreshold(UUID budgetId, Double percentage, Double amount, String triggerType) {
        BudgetThresholdRules threshold = new BudgetThresholdRules();
        threshold.setBudget(budgetsRepository.findById(budgetId)
                .orElseThrow(() -> new RuntimeException("Budget not found with id: " + budgetId)));
        threshold.setPercentage(percentage);
        threshold.setAmount(amount);
        threshold.setTriggerType(triggerType);

        BudgetThresholdRules savedThreshold = thresholdRulesRepository.save(threshold);
        log.info("Created threshold rule for budget: {} with trigger type: {}", budgetId, triggerType);
        return convertToDto(savedThreshold);
    }

    @Override
    public BudgetThresholdDto updateThreshold(UUID id, Double percentage, Double amount, String triggerType) {
        BudgetThresholdRules threshold = thresholdRulesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Threshold rule not found with id: " + id));

        threshold.setPercentage(percentage);
        threshold.setAmount(amount);
        threshold.setTriggerType(triggerType);

        BudgetThresholdRules updatedThreshold = thresholdRulesRepository.save(threshold);
        log.info("Updated threshold rule: {}", id);
        return convertToDto(updatedThreshold);
    }

    @Override
    public void deleteThreshold(UUID id) {
        thresholdRulesRepository.deleteById(id);
        log.info("Deleted threshold rule: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BudgetThresholdDto> getThresholdsByTriggerType(String triggerType) {
        return thresholdRulesRepository.findByTriggerType(triggerType).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private BudgetThresholdDto convertToDto(BudgetThresholdRules threshold) {
        BudgetThresholdDto dto = new BudgetThresholdDto();
        dto.setId(threshold.getId());
        dto.setBudgetId(threshold.getBudget().getId());
        dto.setPercentage(threshold.getPercentage());
        dto.setAmount(threshold.getAmount());
        dto.setTriggerType(threshold.getTriggerType());
        dto.setCreatedAt(threshold.getCreatedAt());
        dto.setUpdatedAt(threshold.getUpdatedAt());
        dto.setCreatedBy(threshold.getCreatedBy() != null ? threshold.getCreatedBy().getId() : null);
        dto.setUpdatedBy(threshold.getUpdatedBy() != null ? threshold.getUpdatedBy().getId() : null);
        return dto;
    }
}