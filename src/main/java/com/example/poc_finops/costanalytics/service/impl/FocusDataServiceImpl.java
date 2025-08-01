package com.example.poc_finops.costanalytics.service.impl;

import com.example.poc_finops.costanalytics.api.dto.FocusUsageDto;
import com.example.poc_finops.costanalytics.domain.entity.FocusLog;
import com.example.poc_finops.costanalytics.domain.entity.FocusUsage;
import com.example.poc_finops.costanalytics.repository.FocusLogRepository;
import com.example.poc_finops.costanalytics.repository.FocusUsageRepository;
import com.example.poc_finops.costanalytics.service.FocusDataService;
import com.example.poc_finops.cloudconnections.service.ConnectionService;
import com.example.poc_finops.organization.service.OrganizationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class FocusDataServiceImpl implements FocusDataService {

    private final FocusLogRepository focusLogRepository;
    private final FocusUsageRepository focusUsageRepository;
    private final OrganizationService organizationService;
    private final ConnectionService connectionService;

    @Override
    @Transactional(readOnly = true)
    public List<FocusLog> getAllFocusLogs() {
        return focusLogRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<FocusLog> getFocusLogsByOrganization(UUID organizationId) {
        return focusLogRepository.findByOrganizationId(organizationId);
    }

    @Override
    @Transactional(readOnly = true)
    public FocusLog getFocusLogById(UUID id) {
        return focusLogRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Focus log not found with id: " + id));
    }

    @Override
    public void deleteFocusLog(UUID id) {
        focusLogRepository.deleteById(id);
        log.info("Deleted focus log: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FocusUsageDto> getFocusUsageByLogId(UUID focusLogId) {
        return focusUsageRepository.findByFocusLogId(focusLogId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<FocusUsageDto> getFocusUsageByOrganization(UUID organizationId) {
        return focusUsageRepository.findByOrganizationId(organizationId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public FocusUsageDto getFocusUsageById(UUID id) {
        FocusUsage focusUsage = focusUsageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Focus usage not found with id: " + id));
        return convertToDto(focusUsage);
    }

    @Override
    public FocusUsage createFocusUsage(FocusUsage focusUsage) {
        FocusUsage savedUsage = focusUsageRepository.save(focusUsage);
        log.info("Created focus usage record for log: {}", focusUsage.getFocusLog().getId());
        return savedUsage;
    }

    @Override
    public FocusUsage updateFocusUsage(UUID id, FocusUsage focusUsage) {
        FocusUsage existingUsage = focusUsageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Focus usage not found with id: " + id));
        
        // Update fields
        existingUsage.setBilledCost(focusUsage.getBilledCost());
        existingUsage.setEffectiveCost(focusUsage.getEffectiveCost());
        existingUsage.setListCost(focusUsage.getListCost());
        existingUsage.setConsumedQuantity(focusUsage.getConsumedQuantity());
        
        FocusUsage updatedUsage = focusUsageRepository.save(existingUsage);
        log.info("Updated focus usage: {}", id);
        return updatedUsage;
    }

    @Override
    public void deleteFocusUsage(UUID id) {
        focusUsageRepository.deleteById(id);
        log.info("Deleted focus usage: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FocusLog> getFocusLogsByOrganizationAndDateRange(UUID organizationId, LocalDate startDate, LocalDate endDate) {
        return focusLogRepository.findByOrganizationIdAndLogDateBetween(organizationId, startDate, endDate);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FocusUsageDto> getFocusUsageByDateRange(UUID organizationId, LocalDate startDate, LocalDate endDate) {
        return focusUsageRepository.findByOrganizationIdAndDateRange(organizationId, startDate, endDate).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private FocusUsageDto convertToDto(FocusUsage focusUsage) {
        FocusUsageDto dto = new FocusUsageDto();
        dto.setId(focusUsage.getId());
        dto.setFocusLogId(focusUsage.getFocusLog().getId());
        dto.setAvailabilityZone(focusUsage.getAvailabilityZone());
        dto.setBilledCost(focusUsage.getBilledCost());
        dto.setBillingAccountId(focusUsage.getBillingAccountId());
        dto.setBillingAccountName(focusUsage.getBillingAccountName());
        dto.setBillingCurrency(focusUsage.getBillingCurrency());
        dto.setBillingPeriodEnd(focusUsage.getBillingPeriodEnd());
        dto.setBillingPeriodStart(focusUsage.getBillingPeriodStart());
        dto.setChargeCategory(focusUsage.getChargeCategory());
        dto.setChargeClass(focusUsage.getChargeClass());
        dto.setChargeDescription(focusUsage.getChargeDescription());
        dto.setChargeFrequency(focusUsage.getChargeFrequency());
        dto.setChargePeriodEnd(focusUsage.getChargePeriodEnd());
        dto.setChargePeriodStart(focusUsage.getChargePeriodStart());
        dto.setCommitmentDiscountCategory(focusUsage.getCommitmentDiscountCategory());
        dto.setCommitmentDiscountId(focusUsage.getCommitmentDiscountId());
        dto.setCommitmentDiscountName(focusUsage.getCommitmentDiscountName());
        dto.setCommitmentDiscountStatus(focusUsage.getCommitmentDiscountStatus());
        dto.setCommitmentDiscountType(focusUsage.getCommitmentDiscountType());
        dto.setConsumedQuantity(focusUsage.getConsumedQuantity());
        dto.setConsumedUnit(focusUsage.getConsumedUnit());
        dto.setContractedCost(focusUsage.getContractedCost());
        dto.setContractedUnitPrice(focusUsage.getContractedUnitPrice());
        dto.setEffectiveCost(focusUsage.getEffectiveCost());
        dto.setInvoiceIssuerName(focusUsage.getInvoiceIssuerName());
        dto.setListCost(focusUsage.getListCost());
        dto.setListUnitPrice(focusUsage.getListUnitPrice());
        dto.setPricingCategory(focusUsage.getPricingCategory());
        dto.setPricingQuantity(focusUsage.getPricingQuantity());
        dto.setPricingUnit(focusUsage.getPricingUnit());
        dto.setProviderName(focusUsage.getProviderName());
        dto.setPublisherName(focusUsage.getPublisherName());
        dto.setRegionId(focusUsage.getRegionId());
        dto.setRegionName(focusUsage.getRegionName());
        dto.setResourceId(focusUsage.getResourceId());
        dto.setResourceName(focusUsage.getResourceName());
        dto.setResourceType(focusUsage.getResourceType());
        dto.setServiceCategory(focusUsage.getServiceCategory());
        dto.setServiceName(focusUsage.getServiceName());
        dto.setSkuId(focusUsage.getSkuId());
        dto.setSkuPriceId(focusUsage.getSkuPriceId());
        dto.setSubAccountId(focusUsage.getSubAccountId());
        dto.setSubAccountName(focusUsage.getSubAccountName());
        dto.setTags(focusUsage.getTags());
        dto.setXCostCategories(focusUsage.getXCostCategories());
        dto.setXDiscounts(focusUsage.getXDiscounts());
        dto.setXOperation(focusUsage.getXOperation());
        dto.setXServiceCode(focusUsage.getXServiceCode());
        dto.setXUsageType(focusUsage.getXUsageType());
        dto.setCreatedAt(focusUsage.getCreatedAt());
        dto.setUpdatedAt(focusUsage.getUpdatedAt());
        dto.setCreatedBy(focusUsage.getCreatedBy());
        dto.setUpdatedBy(focusUsage.getUpdatedBy());
        return dto;
    }
}