package com.example.poc_finops.costanalytics.service;

import com.example.poc_finops.costanalytics.api.dto.FocusUsageDto;
import com.example.poc_finops.costanalytics.domain.entity.FocusLog;
import com.example.poc_finops.costanalytics.domain.entity.FocusUsage;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface FocusDataService {
    
    // Focus Log operations
    List<FocusLog> getAllFocusLogs();
    List<FocusLog> getFocusLogsByOrganization(UUID organizationId);
    FocusLog getFocusLogById(UUID id);
    void deleteFocusLog(UUID id);
    
    // Focus Usage operations
    List<FocusUsageDto> getFocusUsageByLogId(UUID focusLogId);
    List<FocusUsageDto> getFocusUsageByOrganization(UUID organizationId);
    FocusUsageDto getFocusUsageById(UUID id);
    FocusUsage createFocusUsage(FocusUsage focusUsage);
    FocusUsage updateFocusUsage(UUID id, FocusUsage focusUsage);
    void deleteFocusUsage(UUID id);
    
    // Query operations
    List<FocusLog> getFocusLogsByOrganizationAndDateRange(UUID organizationId, LocalDate startDate, LocalDate endDate);
    List<FocusUsageDto> getFocusUsageByDateRange(UUID organizationId, LocalDate startDate, LocalDate endDate);
}