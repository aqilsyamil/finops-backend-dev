package com.example.poc_finops.costanalytics.service.impl;

import com.example.poc_finops.costanalytics.api.dto.CostBreakdownDto;
import com.example.poc_finops.costanalytics.domain.valueobject.CostMetrics;
import com.example.poc_finops.costanalytics.repository.FocusUsageRepository;
import com.example.poc_finops.costanalytics.service.CostAnalyticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CostAnalyticsServiceImpl implements CostAnalyticsService {

    private final FocusUsageRepository focusUsageRepository;

    

    @Override
    public BigDecimal getTotalCost(UUID organizationId, LocalDate startDate, LocalDate endDate) {
        BigDecimal result = focusUsageRepository.sumBilledCostByOrganizationAndDateRange(organizationId, startDate, endDate);
        return result != null ? result : BigDecimal.ZERO;
    }

    @Override
    public BigDecimal getTotalEffectiveCost(UUID organizationId, LocalDate startDate, LocalDate endDate) {
        BigDecimal result = focusUsageRepository.sumEffectiveCostByOrganizationAndDateRange(organizationId, startDate, endDate);
        return result != null ? result : BigDecimal.ZERO;
    }

    @Override
    public BigDecimal getTotalSavings(UUID organizationId, LocalDate startDate, LocalDate endDate) {
        BigDecimal totalCost = getTotalCost(organizationId, startDate, endDate);
        BigDecimal effectiveCost = getTotalEffectiveCost(organizationId, startDate, endDate);
        return totalCost.subtract(effectiveCost);
    }

    @Override
    public Map<String, BigDecimal> getCostByService(UUID organizationId, LocalDate startDate, LocalDate endDate) {
        List<Object[]> results = focusUsageRepository.getCostByServiceForOrganizationAndDateRange(organizationId, startDate, endDate);
        return results.stream()
                .collect(Collectors.toMap(
                    row -> (String) row[0],
                    row -> (BigDecimal) row[1],
                    (existing, replacement) -> existing.add(replacement)
                ));
    }

    @Override
    public Map<String, BigDecimal> getCostByRegion(UUID organizationId, LocalDate startDate, LocalDate endDate) {
        List<Object[]> results = focusUsageRepository.getCostByRegionForOrganizationAndDateRange(organizationId, startDate, endDate);
        return results.stream()
                .collect(Collectors.toMap(
                    row -> (String) row[0],
                    row -> (BigDecimal) row[1],
                    (existing, replacement) -> existing.add(replacement)
                ));
    }

    @Override
    public Map<String, BigDecimal> getCostByChargeCategory(UUID organizationId, LocalDate startDate, LocalDate endDate) {
        List<Object[]> results = focusUsageRepository.getCostByChargeCategoryForOrganizationAndDateRange(organizationId, startDate, endDate);
        return results.stream()
                .collect(Collectors.toMap(
                    row -> (String) row[0],
                    row -> (BigDecimal) row[1],
                    (existing, replacement) -> existing.add(replacement)
                ));
    }

    @Override
    public List<CostBreakdownDto.ResourceCostDto> getTopCostResources(UUID organizationId, LocalDate startDate, LocalDate endDate, Integer limit) {
        return focusUsageRepository.findTopCostResourcesByOrganizationAndDateRange(organizationId, startDate, endDate)
                .stream()
                .limit(limit)
                .map(usage -> {
                    CostBreakdownDto.ResourceCostDto resource = new CostBreakdownDto.ResourceCostDto();
                    resource.setResourceId(usage.getResourceId());
                    resource.setResourceName(usage.getResourceName());
                    resource.setServiceName(usage.getServiceName());
                    resource.setRegion(usage.getRegionName());
                    resource.setCost(usage.getBilledCost());
                    resource.setEffectiveCost(usage.getEffectiveCost());
                    resource.setChargeCategory(usage.getChargeCategory());
                    return resource;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<CostBreakdownDto.ServiceCostDto> getTopCostServices(UUID organizationId, LocalDate startDate, LocalDate endDate, Integer limit) {
        Map<String, BigDecimal> serviceCosts = getCostByService(organizationId, startDate, endDate);
        return serviceCosts.entrySet().stream()
                .sorted(Map.Entry.<String, BigDecimal>comparingByValue().reversed())
                .limit(limit)
                .map(entry -> {
                    CostBreakdownDto.ServiceCostDto service = new CostBreakdownDto.ServiceCostDto();
                    service.setServiceName(entry.getKey());
                    service.setCost(entry.getValue().doubleValue());
                    return service;
                })
                .collect(Collectors.toList());
    }

    @Override
    public CostMetrics getCostMetrics(UUID organizationId, LocalDate startDate, LocalDate endDate, String currency) {
        BigDecimal billedCost = getTotalCost(organizationId, startDate, endDate);
        BigDecimal effectiveCost = getTotalEffectiveCost(organizationId, startDate, endDate);
        
        return new CostMetrics(
            billedCost,
            effectiveCost,
            billedCost, // Assuming list cost equals billed cost for now
            effectiveCost, // Assuming contracted cost equals effective cost
            BigDecimal.ZERO, // Would need to sum consumed quantities
            currency
        );
    }

    @Override
    public CostMetrics getCostMetricsByService(UUID organizationId, String serviceName, LocalDate startDate, LocalDate endDate) {
        // Would need service-specific queries
        return new CostMetrics(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, "USD");
    }

    @Override
    public Map<LocalDate, BigDecimal> getDailyCostTrend(UUID organizationId, LocalDate startDate, LocalDate endDate) {
        // Would need a daily cost aggregation query
        return new HashMap<>();
    }

    @Override
    public Map<String, Map<LocalDate, BigDecimal>> getServiceCostTrend(UUID organizationId, LocalDate startDate, LocalDate endDate) {
        // Would need service-specific daily trend queries
        return new HashMap<>();
    }

    @Override
    public BigDecimal getCostGrowth(UUID organizationId, LocalDate currentPeriodStart, LocalDate currentPeriodEnd, 
                                   LocalDate previousPeriodStart, LocalDate previousPeriodEnd) {
        BigDecimal currentCost = getTotalCost(organizationId, currentPeriodStart, currentPeriodEnd);
        BigDecimal previousCost = getTotalCost(organizationId, previousPeriodStart, previousPeriodEnd);
        
        if (previousCost.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        
        return currentCost.subtract(previousCost)
                .divide(previousCost, 4, java.math.RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }

    private Map<String, CostBreakdownDto.ServiceCostDto> getServiceBreakdown(UUID organizationId, LocalDate startDate, LocalDate endDate) {
        Map<String, BigDecimal> serviceCosts = getCostByService(organizationId, startDate, endDate);
        return serviceCosts.entrySet().stream()
                .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    entry -> {
                        CostBreakdownDto.ServiceCostDto service = new CostBreakdownDto.ServiceCostDto();
                        service.setServiceName(entry.getKey());
                        service.setCost(entry.getValue().doubleValue());
                        return service;
                    }
                ));
    }
}