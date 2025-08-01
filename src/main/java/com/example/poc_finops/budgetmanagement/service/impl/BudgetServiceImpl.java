package com.example.poc_finops.budgetmanagement.service.impl;

import com.example.poc_finops.budgetmanagement.api.dto.BudgetDto;
import com.example.poc_finops.budgetmanagement.api.dto.BudgetAlertDto;
import com.example.poc_finops.budgetmanagement.api.dto.BudgetServiceDto;
import com.example.poc_finops.budgetmanagement.api.dto.BudgetThresholdDto;
import com.example.poc_finops.budgetmanagement.api.dto.CreateBudgetRequest;
import com.example.poc_finops.budgetmanagement.domain.entity.BudgetAlertsRecipients;
import com.example.poc_finops.budgetmanagement.domain.entity.BudgetServicesSelection;
import com.example.poc_finops.budgetmanagement.domain.entity.BudgetThresholdRules;
import com.example.poc_finops.budgetmanagement.domain.entity.Budgets;
import com.example.poc_finops.budgetmanagement.repository.BudgetAlertsRecipientsRepository;
import com.example.poc_finops.budgetmanagement.repository.BudgetServicesSelectionRepository;
import com.example.poc_finops.budgetmanagement.repository.BudgetThresholdRulesRepository;
import com.example.poc_finops.budgetmanagement.repository.BudgetsRepository;
import com.example.poc_finops.budgetmanagement.service.BudgetService;
import com.example.poc_finops.cloudconnections.repository.ServicesRepository;
import com.example.poc_finops.cloudconnections.service.ConnectionService;
import com.example.poc_finops.organization.service.OrganizationService;
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
public class BudgetServiceImpl implements BudgetService {

    private final BudgetsRepository budgetsRepository;
    private final BudgetServicesSelectionRepository budgetServicesSelectionRepository;
    private final BudgetThresholdRulesRepository budgetThresholdRulesRepository;
    private final BudgetAlertsRecipientsRepository budgetAlertsRecipientsRepository;
    private final ServicesRepository servicesRepository;
    private final OrganizationService organizationService;
    private final ConnectionService connectionService;

    @Override
    @Transactional(readOnly = true)
    public List<BudgetDto> getAllBudgets() {
        return budgetsRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BudgetDto> getBudgetsByOrganization(UUID organizationId) {
        return budgetsRepository.findByOrganizationId(organizationId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public BudgetDto getBudgetById(UUID id) {
        Budgets budget = budgetsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Budget not found with id: " + id));
        return convertToDto(budget);
    }

    @Override
    public BudgetDto createBudget(CreateBudgetRequest request) {
        // 1. Create budget entity
        Budgets budget = new Budgets();
        budget.setOrganization(organizationService.getOrganizationEntityById(request.getOrganizationId()));
        budget.setCspConnection(connectionService.getConnectionEntityById(request.getCspConnectionId()));
        budget.setName(request.getName());
        budget.setTimeRange(request.getTimeRange());
        budget.setRenewalType(request.getRenewalType());
        budget.setStartMonth(request.getStartMonth());
        budget.setBudgetType(request.getBudgetType());
        budget.setAmount(request.getAmount());

        Budgets savedBudget = budgetsRepository.save(budget);
        log.info("Created budget: {} for organization: {}", savedBudget.getName(), request.getOrganizationId());

        // 2. Save selected services
        if (request.getSelectedServiceIds() != null && !request.getSelectedServiceIds().isEmpty()) {
            for (UUID serviceId : request.getSelectedServiceIds()) {
                com.example.poc_finops.cloudconnections.domain.entity.Services service = servicesRepository.findById(serviceId)
                    .orElseThrow(() -> new RuntimeException("Service not found with id: " + serviceId));
                
                BudgetServicesSelection selection = new BudgetServicesSelection();
                selection.setBudget(savedBudget);
                selection.setService(service);
                selection.setSelected(true);
                budgetServicesSelectionRepository.save(selection);
            }
        }

        // 3. Create threshold rules
        if (request.getThresholdRules() != null && !request.getThresholdRules().isEmpty()) {
            for (var thresholdRequest : request.getThresholdRules()) {
                BudgetThresholdRules threshold = new BudgetThresholdRules();
                threshold.setBudget(savedBudget);
                threshold.setPercentage(thresholdRequest.getPercentage());
                threshold.setAmount(thresholdRequest.getAmount());
                threshold.setTriggerType(thresholdRequest.getTriggerType());
                budgetThresholdRulesRepository.save(threshold);
            }
        }

        // 4. Add alert recipients
        if (request.getAlertRecipients() != null && !request.getAlertRecipients().isEmpty()) {
            for (String email : request.getAlertRecipients()) {
                BudgetAlertsRecipients recipient = new BudgetAlertsRecipients();
                recipient.setBudget(savedBudget);
                recipient.setEmail(email);
                budgetAlertsRecipientsRepository.save(recipient);
            }
        }

        return convertToDto(savedBudget);
    }

    @Override
    public BudgetDto updateBudget(UUID id, CreateBudgetRequest request) {
        Budgets budget = budgetsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Budget not found with id: " + id));

        budget.setName(request.getName());
        budget.setTimeRange(request.getTimeRange());
        budget.setRenewalType(request.getRenewalType());
        budget.setStartMonth(request.getStartMonth());
        budget.setBudgetType(request.getBudgetType());
        budget.setAmount(request.getAmount());

        Budgets updatedBudget = budgetsRepository.save(budget);
        log.info("Updated budget: {}", id);
        return convertToDto(updatedBudget);
    }

    @Override
    public void deleteBudget(UUID id) {
        budgetsRepository.deleteById(id);
        log.info("Deleted budget: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BudgetDto> getBudgetsByOrganizationAndConnection(UUID organizationId, UUID cspConnectionId) {
        return budgetsRepository.findByOrganizationIdAndCspConnectionId(organizationId, cspConnectionId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private BudgetDto convertToDto(Budgets budget) {
        BudgetDto dto = new BudgetDto();
        dto.setId(budget.getId());
        dto.setOrganizationId(budget.getOrganization().getId());
        dto.setCspConnectionId(budget.getCspConnection().getId());
        dto.setName(budget.getName());
        dto.setTimeRange(budget.getTimeRange());
        dto.setRenewalType(budget.getRenewalType());
        dto.setStartMonth(budget.getStartMonth());
        dto.setBudgetType(budget.getBudgetType());
        dto.setAmount(budget.getAmount());
        dto.setCreatedAt(budget.getCreatedAt());
        dto.setUpdatedAt(budget.getUpdatedAt());
        dto.setCreatedBy(budget.getCreatedBy() != null ? budget.getCreatedBy().getId() : null);
        dto.setUpdatedBy(budget.getUpdatedBy() != null ? budget.getUpdatedBy().getId() : null);

        // Get selected services
        List<BudgetServicesSelection> selections = budgetServicesSelectionRepository.findByBudgetIdAndSelected(budget.getId(), true);
        List<BudgetServiceDto> selectedServices = selections.stream()
            .map(selection -> {
                BudgetServiceDto serviceDto = new BudgetServiceDto();
                serviceDto.setId(selection.getService().getId());
                serviceDto.setServiceName(selection.getService().getServiceName());
                serviceDto.setServiceId(selection.getService().getId().toString());
                serviceDto.setSelected(selection.getSelected());
                return serviceDto;
            })
            .collect(Collectors.toList());
        dto.setSelectedServices(selectedServices);

        // Get threshold rules
        List<BudgetThresholdRules> thresholds = budgetThresholdRulesRepository.findByBudgetId(budget.getId());
        List<BudgetThresholdDto> thresholdDtos = thresholds.stream()
            .map(threshold -> {
                BudgetThresholdDto thresholdDto = new BudgetThresholdDto();
                thresholdDto.setId(threshold.getId());
                thresholdDto.setBudgetId(threshold.getBudget().getId());
                thresholdDto.setPercentage(threshold.getPercentage());
                thresholdDto.setAmount(threshold.getAmount());
                thresholdDto.setTriggerType(threshold.getTriggerType());
                thresholdDto.setCreatedAt(threshold.getCreatedAt());
                thresholdDto.setUpdatedAt(threshold.getUpdatedAt());
                return thresholdDto;
            })
            .collect(Collectors.toList());
        dto.setThresholdRules(thresholdDtos);

        // Get alert recipients
        List<BudgetAlertsRecipients> recipients = budgetAlertsRecipientsRepository.findByBudgetId(budget.getId());
        List<BudgetAlertDto> alertDtos = recipients.stream()
            .map(recipient -> {
                BudgetAlertDto alertDto = new BudgetAlertDto();
                alertDto.setId(recipient.getId());
                alertDto.setBudgetId(recipient.getBudget().getId());
                alertDto.setEmail(recipient.getEmail());
                alertDto.setCreatedAt(recipient.getCreatedAt());
                alertDto.setUpdatedAt(recipient.getUpdatedAt());
                return alertDto;
            })
            .collect(Collectors.toList());
        dto.setAlertRecipients(alertDtos);

        return dto;
    }
}