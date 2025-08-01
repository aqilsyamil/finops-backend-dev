package com.example.poc_finops.costanalytics.service;

import com.example.poc_finops.costanalytics.api.dto.CostBreakdownDto;
import com.example.poc_finops.costanalytics.api.dto.FocusUsageDto;
import com.example.poc_finops.costanalytics.api.dto.UsageReportRequest;

import java.util.List;

public interface UsageReportService {

    
    // Scheduled reports
    void generateScheduledReport(UsageReportRequest request, String reportFrequency);
    
    // Report validation
    boolean validateReportRequest(UsageReportRequest request);
}