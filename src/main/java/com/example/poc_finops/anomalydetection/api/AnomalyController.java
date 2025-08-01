package com.example.poc_finops.anomalydetection.api;

import com.example.poc_finops.anomalydetection.api.dto.*;
import com.example.poc_finops.anomalydetection.service.AnomalyDetectionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/anomaly-detection")
@RequiredArgsConstructor
@Tag(name = "Anomaly Detection", description = "APIs for managing anomaly detection and monitoring")
@Slf4j
public class AnomalyController {
    
    private final AnomalyDetectionService anomalyDetectionService;
    
    // ============ API ENDPOINTS ============
    
    @GetMapping("/history")
    @Operation(summary = "Get detection history", 
               description = "Fetch anomaly detection history, optionally filtered by monitor name")
    public ResponseEntity<List<DetectionHistoryDto>> getDetectionHistory(
            @Parameter(description = "Filter by monitor name (optional)") 
            @RequestParam(required = false) String name) {
        
        List<DetectionHistoryDto> history;
        if (name != null && !name.trim().isEmpty()) {
            log.info("Getting detection history filtered by name: {}", name);
            history = anomalyDetectionService.getDetectionHistoryByName(name);
        } else {
            log.info("Getting all detection history");
            history = anomalyDetectionService.getAllDetectionHistory();
        }
        
        return ResponseEntity.ok(history);
    }
    
    @GetMapping("/services")
    @Operation(summary = "Get service selection", 
               description = "Get distinct list of services with anomaly statistics")
    public ResponseEntity<List<ServiceSummaryDto>> getServiceSummaries() {
        log.info("Getting service summaries with anomaly statistics");
        List<ServiceSummaryDto> services = anomalyDetectionService.getServiceSummaries();
        return ResponseEntity.ok(services);
    }
    
    @GetMapping("/spend-analysis/{id}")
    @Operation(summary = "Get actual & expected spend analysis", 
               description = "Get detailed spend analysis for specific anomaly detection by ID")
    public ResponseEntity<SpendAnalysisDetailDto> getSpendAnalysisById(
            @Parameter(description = "Anomaly detection ID", required = true) 
            @PathVariable UUID id) {
        
        log.info("Getting spend analysis for anomaly ID: {}", id);
        SpendAnalysisDetailDto analysis = anomalyDetectionService.getSpendAnalysisById(id);
        return ResponseEntity.ok(analysis);
    }
    
    @GetMapping("/monitors")
    @Operation(summary = "List monitors", 
               description = "Get all monitors with associated alerts and recipients")
    public ResponseEntity<List<MonitorWithAlertsDto>> getAllMonitorsWithAlerts() {
        log.info("Getting all monitors with alerts and recipients");
        List<MonitorWithAlertsDto> monitors = anomalyDetectionService.getAllMonitorsWithAlerts();
        return ResponseEntity.ok(monitors);
    }
    
    @PostMapping("/monitors")
    @Operation(summary = "Add monitor with alerts", 
               description = "Create a new monitor with alert configuration and recipients")
    public ResponseEntity<MonitorWithAlertsDto> createMonitorWithAlerts(
            @Valid @RequestBody CreateMonitorRequest request) {
        
        UUID userId = getCurrentUserId();
        log.info("Creating monitor with alerts for user: {} and CSP connection: {}", 
                 userId, request.getCspConnectionId());
        
        MonitorWithAlertsDto monitor = anomalyDetectionService.createMonitorWithAlerts(request, userId);
        return new ResponseEntity<>(monitor, HttpStatus.CREATED);
    }
    
    @DeleteMapping("/monitors/{id}")
    @Operation(summary = "Delete monitor", 
               description = "Delete a monitor with cascade delete of alerts and recipients")
    public ResponseEntity<Void> deleteMonitor(
            @Parameter(description = "Monitor ID", required = true) 
            @PathVariable UUID id) {
        
        log.info("Deleting monitor with ID: {}", id);
        anomalyDetectionService.deleteMonitor(id);
        return ResponseEntity.noContent().build();
    }
    
    
    // ============ UTILITY METHODS ============
    
    private UUID getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof UUID) {
                return (UUID) principal;
            }
            try {
                return UUID.fromString(principal.toString());
            } catch (Exception e) {
                log.warn("Could not extract user ID from authentication: {}", e.getMessage());
            }
        }
        
        log.warn("No authenticated user found, using default user ID");
        return UUID.randomUUID(); // This should be replaced with proper error handling
    }
}