package com.example.poc_finops.anomalydetection.api.dto;

import com.example.poc_finops.anomalydetection.domain.entity.Alert;
import com.example.poc_finops.anomalydetection.domain.entity.AlertRecipients;
import com.example.poc_finops.anomalydetection.domain.entity.Monitor;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Complete monitor information with associated alerts and recipients")
public class MonitorWithAlertsDto {
    
    @Schema(description = "Monitor ID")
    private UUID id;
    
    @Schema(description = "CSP connection ID")
    private UUID cspConnectionId;
    
    @Schema(description = "CSP connection name")
    private String cspConnectionName;
    
    @Schema(description = "Monitor name")
    private String name;
    
    @Schema(description = "Monitor status")
    private String status;
    
    @Schema(description = "Associated alert information")
    private AlertInfo alertInfo;
    
    @Schema(description = "Performance metrics")
    private PerformanceMetrics performanceMetrics;
    
    @Schema(description = "Created timestamp")
    private OffsetDateTime createdAt;
    
    @Schema(description = "Updated timestamp")
    private OffsetDateTime updatedAt;
    
    @Schema(description = "Created by user")
    private String createdBy;
    
    @Schema(description = "Updated by user")
    private String updatedBy;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AlertInfo {
        private UUID alertId;
        private String alertName;
        private List<String> recipientEmails;
        private OffsetDateTime alertCreatedAt;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PerformanceMetrics {
        private Long totalAnomaliesDetected;
        private Long activeAnomalies;
        private String lastAnomalyDate;
        private String monitorStatus; // ACTIVE, INACTIVE, ERROR
    }
    
    public static MonitorWithAlertsDto fromEntity(Monitor monitor, Alert alert, List<AlertRecipients> recipients, Long totalAnomalies, Long activeAnomalies) {
        MonitorWithAlertsDto dto = new MonitorWithAlertsDto();
        dto.setId(monitor.getId());
        dto.setCspConnectionId(monitor.getCspConnection() != null ? monitor.getCspConnection().getId() : null);
        dto.setCspConnectionName(monitor.getCspConnection() != null ? monitor.getCspConnection().getName() : null);
        dto.setName(monitor.getName());
        dto.setStatus("ACTIVE"); // Default status
        dto.setCreatedAt(monitor.getCreatedAt());
        dto.setUpdatedAt(monitor.getUpdatedAt());
        dto.setCreatedBy(monitor.getCreatedBy() != null ? monitor.getCreatedBy().getUsername() : null);
        dto.setUpdatedBy(monitor.getUpdatedBy() != null ? monitor.getUpdatedBy().getUsername() : null);
        
        // Alert information
        if (alert != null) {
            AlertInfo alertInfo = new AlertInfo();
            alertInfo.setAlertId(alert.getId());
            alertInfo.setAlertName(alert.getName());
            alertInfo.setAlertCreatedAt(alert.getCreatedAt());
            
            if (recipients != null) {
                alertInfo.setRecipientEmails(recipients.stream()
                    .map(AlertRecipients::getEmail)
                    .collect(Collectors.toList()));
            }
            
            dto.setAlertInfo(alertInfo);
        }
        
        // Performance metrics
        PerformanceMetrics metrics = new PerformanceMetrics();
        metrics.setTotalAnomaliesDetected(totalAnomalies != null ? totalAnomalies : 0L);
        metrics.setActiveAnomalies(activeAnomalies != null ? activeAnomalies : 0L);
        metrics.setMonitorStatus("ACTIVE");
        dto.setPerformanceMetrics(metrics);
        
        return dto;
    }
}