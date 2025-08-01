package com.example.poc_finops.anomalydetection.service.impl;

import com.example.poc_finops.anomalydetection.api.dto.*;
import com.example.poc_finops.anomalydetection.domain.entity.*;
import com.example.poc_finops.anomalydetection.repository.*;
import com.example.poc_finops.anomalydetection.service.AnomalyDetectionService;
import com.example.poc_finops.cloudconnections.domain.entity.CspConnection;
import com.example.poc_finops.cloudconnections.domain.entity.Services;
import com.example.poc_finops.cloudconnections.repository.CspConnectionRepository;
import com.example.poc_finops.cloudconnections.repository.ServicesRepository;
import com.example.poc_finops.organization.domain.entity.User;
import com.example.poc_finops.organization.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AnomalyDetectionServiceImpl implements AnomalyDetectionService {
    
    private final FocusAnomalyDetectionRepository anomalyDetectionRepository;
    private final FocusAnomalyMonitorsRepository anomalyMonitorsRepository;
    private final MonitorRepository monitorRepository;
    private final AlertRepository alertRepository;
    private final AlertRecipientsRepository alertRecipientsRepository;
    private final CspConnectionRepository cspConnectionRepository;
    private final ServicesRepository servicesRepository;
    private final UserRepository userRepository;
    
    @Override
    @Transactional(readOnly = true)
    public List<AnomalyDto> getAllAnomalies() {
        List<FocusAnomalyDetection> anomalies = anomalyDetectionRepository.findAll();
        return anomalies.stream().map(AnomalyDto::fromEntity).collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public AnomalyDto getAnomalyById(UUID anomalyId) {
        FocusAnomalyDetection anomaly = anomalyDetectionRepository.findById(anomalyId)
            .orElseThrow(() -> new IllegalArgumentException("Anomaly not found with ID: " + anomalyId));
        return AnomalyDto.fromEntity(anomaly);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<AnomalyDto> getAnomaliesByMonitor(UUID monitorId) {
        List<FocusAnomalyDetection> anomalies = anomalyDetectionRepository.findByAnomalyMonitorId(monitorId);
        return anomalies.stream().map(AnomalyDto::fromEntity).collect(Collectors.toList());
    }
    
    
    @Override
    @Transactional(readOnly = true)
    public List<AnomalyDto> getAnomaliesByDateRange(LocalDate startDate, LocalDate endDate) {
        List<FocusAnomalyDetection> anomalies = anomalyDetectionRepository.findByDateRange(startDate, endDate);
        return anomalies.stream().map(AnomalyDto::fromEntity).collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<AnomalyDto> getAnomaliesExceedingThreshold(Double thresholdMultiplier) {
        List<FocusAnomalyDetection> anomalies = anomalyDetectionRepository.findAnomaliesExceedingThreshold(thresholdMultiplier);
        return anomalies.stream().map(AnomalyDto::fromEntity).collect(Collectors.toList());
    }
    
    @Override
    public MonitorDto createMonitor(String name, String type, UUID serviceId, String monitoredDimensions, String tags, UUID userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
        
        FocusAnomalyMonitors monitor = new FocusAnomalyMonitors();
        monitor.setName(name);
        monitor.setType(type);
        if (serviceId != null) {
            Services service = servicesRepository.findById(serviceId)
                .orElseThrow(() -> new IllegalArgumentException("Service not found with ID: " + serviceId));
            monitor.setService(service);
        }
        monitor.setMonitoredDimensions(monitoredDimensions);
        monitor.setTags(tags);
        monitor.setDateCreated(LocalDate.now());
        monitor.setCreatedBy(user);
        
        FocusAnomalyMonitors savedMonitor = anomalyMonitorsRepository.save(monitor);
        return MonitorDto.fromEntity(savedMonitor);
    }
    
    @Override
    public MonitorDto updateMonitor(UUID monitorId, String name, String type, UUID serviceId, String monitoredDimensions, String tags, UUID userId) {
        FocusAnomalyMonitors monitor = anomalyMonitorsRepository.findById(monitorId)
            .orElseThrow(() -> new IllegalArgumentException("Monitor not found with ID: " + monitorId));
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
        
        monitor.setName(name);
        monitor.setType(type);
        if (serviceId != null) {
            Services service = servicesRepository.findById(serviceId)
                .orElseThrow(() -> new IllegalArgumentException("Service not found with ID: " + serviceId));
            monitor.setService(service);
        }
        monitor.setMonitoredDimensions(monitoredDimensions);
        monitor.setTags(tags);
        monitor.setDateUpdated(LocalDate.now());
        monitor.setUpdatedBy(user);
        
        FocusAnomalyMonitors savedMonitor = anomalyMonitorsRepository.save(monitor);
        return MonitorDto.fromEntity(savedMonitor);
    }
    
    @Override
    public void deleteMonitor(UUID monitorId) {
        // For backward compatibility, try both legacy and new monitor deletion
        // First try new Monitor entity deletion
        if (monitorRepository.existsById(monitorId)) {
            deleteMonitorWithAlerts(monitorId);
            return;
        }
        
        // Fall back to legacy FocusAnomalyMonitors deletion
        if (!anomalyMonitorsRepository.existsById(monitorId)) {
            throw new IllegalArgumentException("Monitor not found with ID: " + monitorId);
        }
        anomalyMonitorsRepository.deleteById(monitorId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<MonitorDto> getAllMonitors() {
        List<FocusAnomalyMonitors> monitors = anomalyMonitorsRepository.findAll();
        return monitors.stream().map(MonitorDto::fromEntity).collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public MonitorDto getMonitorById(UUID monitorId) {
        FocusAnomalyMonitors monitor = anomalyMonitorsRepository.findById(monitorId)
            .orElseThrow(() -> new IllegalArgumentException("Monitor not found with ID: " + monitorId));
        return MonitorDto.fromEntity(monitor);
    }
    
    // New methods for revised APIs
    
    @Override
    @Transactional(readOnly = true)
    public List<DetectionHistoryDto> getDetectionHistoryByName(String name) {
        List<FocusAnomalyDetection> anomalies = anomalyDetectionRepository.findByMonitorNameContainingIgnoreCase(name);
        return anomalies.stream()
                .map(DetectionHistoryDto::fromEntity)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<DetectionHistoryDto> getAllDetectionHistory() {
        List<FocusAnomalyDetection> anomalies = anomalyDetectionRepository.findAllDetectionHistoryOrderByDate();
        return anomalies.stream()
                .map(DetectionHistoryDto::fromEntity)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ServiceSummaryDto> getServiceSummaries() {
        List<Object[]> results = anomalyDetectionRepository.findServiceSummaries();
        List<ServiceSummaryDto> summaries = new ArrayList<>();
        
        for (Object[] result : results) {
            ServiceSummaryDto summary = new ServiceSummaryDto();
            summary.setServiceName((String) result[0]);
            summary.setAnomalyCount((Long) result[1]);
            summary.setTotalCostImpact(result[2] != null ? ((Number) result[2]).doubleValue() : null);
            summary.setAverageCostImpact(result[3] != null ? ((Number) result[3]).doubleValue() : null);
            summary.setTotalExpectedSpend(result[4] != null ? ((Number) result[4]).doubleValue() : null);
            summary.setTotalActualSpend(result[5] != null ? ((Number) result[5]).doubleValue() : null);
            
            // Calculate overall variance percentage
            if (summary.getTotalExpectedSpend() != null && 
                summary.getTotalExpectedSpend() != 0.0 &&
                summary.getTotalCostImpact() != null) {
                Double percentage = (summary.getTotalCostImpact() / summary.getTotalExpectedSpend()) * 100.0;
                summary.setOverallVariancePercentage(percentage);
            }
            
            summaries.add(summary);
        }
        
        return summaries;
    }
    
    @Override
    @Transactional(readOnly = true)
    public SpendAnalysisDetailDto getSpendAnalysisById(UUID anomalyId) {
        FocusAnomalyDetection anomaly = anomalyDetectionRepository.findById(anomalyId)
            .orElseThrow(() -> new IllegalArgumentException("Anomaly not found with ID: " + anomalyId));
        return SpendAnalysisDetailDto.fromEntity(anomaly);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<MonitorWithAlertsDto> getAllMonitorsWithAlerts() {
        List<Monitor> monitors = monitorRepository.findAllWithAlerts();
        List<MonitorWithAlertsDto> result = new ArrayList<>();
        
        for (Monitor monitor : monitors) {
            Alert alert = monitor.getAlert();
            List<AlertRecipients> recipients = null;
            
            if (alert != null) {
                recipients = alertRecipientsRepository.findByAlertId(alert.getId());
            }
            
            // Get anomaly counts
            Long totalAnomalies = monitorRepository.countAnomaliesByMonitorName(monitor.getName());
            LocalDate sevenDaysAgo = LocalDate.now().minusDays(7);
            Long activeAnomalies = monitorRepository.countActiveAnomaliesByMonitorName(monitor.getName(), sevenDaysAgo);
            
            MonitorWithAlertsDto dto = MonitorWithAlertsDto.fromEntity(
                monitor, alert, recipients, totalAnomalies, activeAnomalies);
            result.add(dto);
        }
        
        return result;
    }
    
    @Override
    public MonitorWithAlertsDto createMonitorWithAlerts(CreateMonitorRequest request, UUID userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
        
        CspConnection cspConnection = cspConnectionRepository.findById(request.getCspConnectionId())
            .orElseThrow(() -> new IllegalArgumentException("CSP connection not found with ID: " + request.getCspConnectionId()));
        
        // Create alert first
        Alert alert = new Alert();
        alert.setName(request.getAlertConfiguration().getAlertName());
        alert.setCreatedBy(user);
        Alert savedAlert = alertRepository.save(alert);
        
        // Create alert recipients
        List<AlertRecipients> recipients = new ArrayList<>();
        for (String email : request.getAlertConfiguration().getRecipientEmails()) {
            AlertRecipients recipient = new AlertRecipients();
            recipient.setAlert(savedAlert);
            recipient.setEmail(email);
            recipient.setCreatedBy(user);
            recipients.add(recipient);
        }
        alertRecipientsRepository.saveAll(recipients);
        
        // Create monitor
        Monitor monitor = new Monitor();
        monitor.setCspConnection(cspConnection);
        monitor.setName(request.getName());
        monitor.setAlert(savedAlert);
        monitor.setCreatedBy(user);
        Monitor savedMonitor = monitorRepository.save(monitor);
        
        // Return DTO with all information
        return MonitorWithAlertsDto.fromEntity(savedMonitor, savedAlert, recipients, 0L, 0L);
    }
    
    @Override
    public void deleteMonitorWithAlerts(UUID monitorId) {
        Monitor monitor = monitorRepository.findById(monitorId)
            .orElseThrow(() -> new IllegalArgumentException("Monitor not found with ID: " + monitorId));
        
        // Get the associated alert
        Alert alert = monitor.getAlert();
        
        if (alert != null) {
            // Delete alert recipients first (foreign key constraint)
            List<AlertRecipients> recipients = alertRecipientsRepository.findByAlertId(alert.getId());
            if (!recipients.isEmpty()) {
                alertRecipientsRepository.deleteAll(recipients);
            }
            
            // Delete the alert
            alertRepository.delete(alert);
        }
        
        // Finally delete the monitor
        monitorRepository.delete(monitor);
    }
}