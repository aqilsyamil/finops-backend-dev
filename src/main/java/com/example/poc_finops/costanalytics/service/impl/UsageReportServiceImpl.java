package com.example.poc_finops.costanalytics.service.impl;

import com.example.poc_finops.costanalytics.api.dto.CostBreakdownDto;
import com.example.poc_finops.costanalytics.api.dto.FocusUsageDto;
import com.example.poc_finops.costanalytics.api.dto.UsageReportRequest;
import com.example.poc_finops.costanalytics.service.CostAnalyticsService;
import com.example.poc_finops.costanalytics.service.FocusDataService;
import com.example.poc_finops.costanalytics.service.UsageReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UsageReportServiceImpl implements UsageReportService {

    private final CostAnalyticsService costAnalyticsService;
    private final FocusDataService focusDataService;


    @Override
    public void generateScheduledReport(UsageReportRequest request, String reportFrequency) {
        log.info("Scheduled report generation for frequency: {} - not yet implemented", reportFrequency);
        // Scheduled reporting logic would go here
    }

    @Override
    public boolean validateReportRequest(UsageReportRequest request) {
        if (request.getOrganizationId() == null) {
            throw new IllegalArgumentException("Organization ID is required");
        }
        
        if (request.getStartDate() == null || request.getEndDate() == null) {
            throw new IllegalArgumentException("Start date and end date are required");
        }
        
        if (request.getStartDate().isAfter(request.getEndDate())) {
            throw new IllegalArgumentException("Start date must be before end date");
        }
        
        if (request.getLimit() != null && request.getLimit() <= 0) {
            throw new IllegalArgumentException("Limit must be positive");
        }
        
        return true;
    }

    private boolean filterByService(FocusUsageDto usage, List<String> serviceNames) {
        return serviceNames == null || serviceNames.isEmpty() || 
               serviceNames.contains(usage.getServiceName());
    }

    private boolean filterByRegion(FocusUsageDto usage, List<String> regions) {
        return regions == null || regions.isEmpty() || 
               regions.contains(usage.getRegionName());
    }

    private boolean filterByChargeCategory(FocusUsageDto usage, List<String> chargeCategories) {
        return chargeCategories == null || chargeCategories.isEmpty() || 
               chargeCategories.contains(usage.getChargeCategory());
    }

    private boolean filterByResourceType(FocusUsageDto usage, List<String> resourceTypes) {
        return resourceTypes == null || resourceTypes.isEmpty() || 
               resourceTypes.contains(usage.getResourceType());
    }
}