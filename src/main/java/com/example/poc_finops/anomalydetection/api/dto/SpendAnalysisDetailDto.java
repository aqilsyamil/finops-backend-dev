package com.example.poc_finops.anomalydetection.api.dto;

import com.example.poc_finops.anomalydetection.domain.entity.FocusAnomalyDetection;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Detailed spend analysis for individual anomaly detection")
public class SpendAnalysisDetailDto {
    
    @Schema(description = "Anomaly detection ID")
    private UUID id;
    
    @Schema(description = "Monitor information")
    private MonitorInfo monitor;
    
    @Schema(description = "Detection period information")
    private DetectionPeriod detectionPeriod;
    
    @Schema(description = "Spend comparison details")
    private SpendComparison spendComparison;
    
    @Schema(description = "Account information")
    private AccountInfo accountInfo;
    
    @Schema(description = "Historical context")
    private HistoricalContext historicalContext;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MonitorInfo {
        private UUID monitorId;
        private String monitorName;
        private String monitorType;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DetectionPeriod {
        private LocalDate startDate;
        private LocalDate lastDetectedDate;
        private Long durationDays;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SpendComparison {
        private Double expectedSpend;
        private Double actualSpend;
        private Double variance;
        private Double variancePercentage;
        private String impactSeverity; // LOW, MEDIUM, HIGH, CRITICAL
        private String impactDescription;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AccountInfo {
        private String linkedAccountId;
        private String linkedAccountName;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HistoricalContext {
        private OffsetDateTime firstDetected;
        private OffsetDateTime lastUpdated;
        private String createdBy;
        private String updatedBy;
    }
    
    public static SpendAnalysisDetailDto fromEntity(FocusAnomalyDetection anomaly) {
        SpendAnalysisDetailDto dto = new SpendAnalysisDetailDto();
        dto.setId(anomaly.getId());
        // Monitor information
        MonitorInfo monitorInfo = new MonitorInfo();
        monitorInfo.setMonitorId(anomaly.getAnomalyMonitor().getId());
        monitorInfo.setMonitorName(anomaly.getAnomalyMonitor().getName());
        monitorInfo.setMonitorType(anomaly.getAnomalyMonitor().getType());
        dto.setMonitor(monitorInfo);
        
        // Detection period
        DetectionPeriod period = new DetectionPeriod();
        period.setStartDate(anomaly.getStartDate());
        period.setLastDetectedDate(anomaly.getLastDetectedDate());
        if (anomaly.getStartDate() != null && anomaly.getLastDetectedDate() != null) {
            period.setDurationDays(anomaly.getLastDetectedDate().toEpochDay() - anomaly.getStartDate().toEpochDay());
        }
        dto.setDetectionPeriod(period);
        
        // Spend comparison
        SpendComparison comparison = new SpendComparison();
        comparison.setExpectedSpend(anomaly.getExpectedSpend());
        comparison.setActualSpend(anomaly.getActualSpend());
        
        if (anomaly.getActualSpend() != null && anomaly.getExpectedSpend() != null) {
            Double variance = anomaly.getActualSpend() - anomaly.getExpectedSpend();
            comparison.setVariance(variance);
            
            if (anomaly.getExpectedSpend() != 0.0) {
                Double percentage = (variance / anomaly.getExpectedSpend()) * 100.0;
                comparison.setVariancePercentage(percentage);
                
                // Determine impact severity
                comparison.setImpactSeverity(determineImpactSeverity(percentage));
                comparison.setImpactDescription(generateImpactDescription(variance, percentage));
            }
        }
        dto.setSpendComparison(comparison);
        
        // Account information
        AccountInfo accountInfo = new AccountInfo();
        accountInfo.setLinkedAccountId(anomaly.getLinkedAccountId());
        accountInfo.setLinkedAccountName(anomaly.getLinkedAccountName());
        dto.setAccountInfo(accountInfo);
        
        // Historical context
        HistoricalContext context = new HistoricalContext();
        context.setFirstDetected(anomaly.getCreatedAt());
        context.setLastUpdated(anomaly.getUpdatedAt());
        context.setCreatedBy(anomaly.getCreatedBy() != null ? anomaly.getCreatedBy().getUsername() : null);
        context.setUpdatedBy(anomaly.getUpdatedBy() != null ? anomaly.getUpdatedBy().getUsername() : null);
        dto.setHistoricalContext(context);
        
        return dto;
    }
    
    private static String determineImpactSeverity(Double percentage) {
        Double absPercentage = Math.abs(percentage);
        if (absPercentage >= 50.0) {
            return "CRITICAL";
        } else if (absPercentage >= 25.0) {
            return "HIGH";
        } else if (absPercentage >= 10.0) {
            return "MEDIUM";
        } else {
            return "LOW";
        }
    }
    
    private static String generateImpactDescription(Double variance, Double percentage) {
        String direction = variance > 0.0 ? "overspend" : "underspend";
        return String.format("%.2f%% %s from expected baseline", Math.abs(percentage), direction);
    }
}