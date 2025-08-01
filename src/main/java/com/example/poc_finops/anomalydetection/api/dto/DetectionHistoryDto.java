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
@Schema(description = "Detection history with comprehensive anomaly details")
public class DetectionHistoryDto {
    
    @Schema(description = "Anomaly detection ID")
    private UUID id;
    
    @Schema(description = "Anomaly monitor ID")
    private UUID anomalyMonitorId;
    
    @Schema(description = "Anomaly monitor name")
    private String anomalyMonitorName;
    
    @Schema(description = "Start date of anomaly detection")
    private LocalDate startDate;
    
    @Schema(description = "Last detected date")
    private LocalDate lastDetectedDate;
    
    @Schema(description = "Expected spend amount")
    private Double expectedSpend;
    
    @Schema(description = "Actual spend amount")
    private Double actualSpend;
    
    @Schema(description = "Spend variance (actual - expected)")
    private Double spendVariance;
    
    @Schema(description = "Spend variance percentage")
    private Double spendVariancePercentage;
    
    @Schema(description = "Linked account ID")
    private String linkedAccountId;
    
    @Schema(description = "Linked account name")
    private String linkedAccountName;
    
    @Schema(description = "Created timestamp")
    private OffsetDateTime createdAt;
    
    @Schema(description = "Updated timestamp")
    private OffsetDateTime updatedAt;
    
    @Schema(description = "Created by user")
    private String createdBy;
    
    @Schema(description = "Updated by user")
    private String updatedBy;
    
    public static DetectionHistoryDto fromEntity(FocusAnomalyDetection anomaly) {
        DetectionHistoryDto dto = new DetectionHistoryDto();
        dto.setId(anomaly.getId());
        dto.setAnomalyMonitorId(anomaly.getAnomalyMonitor().getId());
        dto.setAnomalyMonitorName(anomaly.getAnomalyMonitor().getName());
        dto.setStartDate(anomaly.getStartDate());
        dto.setLastDetectedDate(anomaly.getLastDetectedDate());
        dto.setExpectedSpend(anomaly.getExpectedSpend());
        dto.setActualSpend(anomaly.getActualSpend());
        
        // Calculate variance
        if (anomaly.getActualSpend() != null && anomaly.getExpectedSpend() != null) {
            dto.setSpendVariance(anomaly.getActualSpend() - anomaly.getExpectedSpend());
            
            // Calculate percentage variance
            if (anomaly.getExpectedSpend() != 0.0) {
                double percentage = (dto.getSpendVariance() / anomaly.getExpectedSpend()) * 100;
                dto.setSpendVariancePercentage(percentage);
            }
        }
        
        dto.setLinkedAccountId(anomaly.getLinkedAccountId());
        dto.setLinkedAccountName(anomaly.getLinkedAccountName());
        dto.setCreatedAt(anomaly.getCreatedAt());
        dto.setUpdatedAt(anomaly.getUpdatedAt());
        dto.setCreatedBy(anomaly.getCreatedBy() != null ? anomaly.getCreatedBy().getUsername() : null);
        dto.setUpdatedBy(anomaly.getUpdatedBy() != null ? anomaly.getUpdatedBy().getUsername() : null);
        
        return dto;
    }
}