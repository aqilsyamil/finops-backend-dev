package com.example.poc_finops.costanalytics.service;

import com.example.poc_finops.costanalytics.api.dto.CostBreakdownDto;
import com.example.poc_finops.costanalytics.domain.valueobject.CostMetrics;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface CostAnalyticsService {
    
    // Cost analysis operations
    BigDecimal getTotalCost(UUID organizationId, LocalDate startDate, LocalDate endDate);
    BigDecimal getTotalEffectiveCost(UUID organizationId, LocalDate startDate, LocalDate endDate);
    BigDecimal getTotalSavings(UUID organizationId, LocalDate startDate, LocalDate endDate);
    
    // Service-level analytics
    Map<String, BigDecimal> getCostByService(UUID organizationId, LocalDate startDate, LocalDate endDate);
    Map<String, BigDecimal> getCostByRegion(UUID organizationId, LocalDate startDate, LocalDate endDate);
    Map<String, BigDecimal> getCostByChargeCategory(UUID organizationId, LocalDate startDate, LocalDate endDate);
    
    // Resource analytics
    List<CostBreakdownDto.ResourceCostDto> getTopCostResources(UUID organizationId, LocalDate startDate, LocalDate endDate, Integer limit);
    List<CostBreakdownDto.ServiceCostDto> getTopCostServices(UUID organizationId, LocalDate startDate, LocalDate endDate, Integer limit);
    
    // Cost metrics
    CostMetrics getCostMetrics(UUID organizationId, LocalDate startDate, LocalDate endDate, String currency);
    CostMetrics getCostMetricsByService(UUID organizationId, String serviceName, LocalDate startDate, LocalDate endDate);
    
    // Trending analytics
    Map<LocalDate, BigDecimal> getDailyCostTrend(UUID organizationId, LocalDate startDate, LocalDate endDate);
    Map<String, Map<LocalDate, BigDecimal>> getServiceCostTrend(UUID organizationId, LocalDate startDate, LocalDate endDate);
    
    // Comparison analytics
    BigDecimal getCostGrowth(UUID organizationId, LocalDate currentPeriodStart, LocalDate currentPeriodEnd, 
                            LocalDate previousPeriodStart, LocalDate previousPeriodEnd);
}