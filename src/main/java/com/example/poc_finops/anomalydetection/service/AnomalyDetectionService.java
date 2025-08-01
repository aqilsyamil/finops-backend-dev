package com.example.poc_finops.anomalydetection.service;

import com.example.poc_finops.anomalydetection.api.dto.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface AnomalyDetectionService {
    
    // Existing methods
    List<AnomalyDto> getAllAnomalies();
    
    AnomalyDto getAnomalyById(UUID anomalyId);
    
    List<AnomalyDto> getAnomaliesByMonitor(UUID monitorId);
    
    
    List<AnomalyDto> getAnomaliesByDateRange(LocalDate startDate, LocalDate endDate);
    
    List<AnomalyDto> getAnomaliesExceedingThreshold(Double thresholdMultiplier);
    
    MonitorDto createMonitor(String name, String type, UUID serviceId, String monitoredDimensions, String tags, UUID userId);
    
    MonitorDto updateMonitor(UUID monitorId, String name, String type, UUID serviceId, String monitoredDimensions, String tags, UUID userId);
    
    void deleteMonitor(UUID monitorId);
    
    List<MonitorDto> getAllMonitors();
    
    MonitorDto getMonitorById(UUID monitorId);
    
    // New methods for revised APIs
    
    /**
     * Get detection history filtered by name
     */
    List<DetectionHistoryDto> getDetectionHistoryByName(String name);
    
    /**
     * Get all detection history
     */
    List<DetectionHistoryDto> getAllDetectionHistory();
    
    /**
     * Get distinct services with anomaly statistics
     */
    List<ServiceSummaryDto> getServiceSummaries();
    
    /**
     * Get detailed spend analysis for specific anomaly by ID
     */
    SpendAnalysisDetailDto getSpendAnalysisById(UUID anomalyId);
    
    /**
     * Get all monitors with alerts and recipients
     */
    List<MonitorWithAlertsDto> getAllMonitorsWithAlerts();
    
    /**
     * Create monitor with alert configuration and recipients
     */
    MonitorWithAlertsDto createMonitorWithAlerts(CreateMonitorRequest request, UUID userId);
    
    /**
     * Delete monitor with cascade deletion of alerts and recipients
     */
    void deleteMonitorWithAlerts(UUID monitorId);
}