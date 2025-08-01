package com.example.poc_finops.anomalydetection.api.dto;

import com.example.poc_finops.anomalydetection.domain.entity.FocusAnomalyDetection;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnomalyDto {
    private UUID id;
    private UUID anomalyMonitorId;
    private String anomalyMonitorName;
    private LocalDate startDate;
    private LocalDate lastDetectedDate;
    private Double expectedSpend;
    private Double actualSpend;
    private String linkedAccountId;
    private String linkedAccountName;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
    
    public static AnomalyDto fromEntity(FocusAnomalyDetection anomaly) {
        AnomalyDto dto = new AnomalyDto();
        dto.setId(anomaly.getId());
        dto.setAnomalyMonitorId(anomaly.getAnomalyMonitor().getId());
        dto.setAnomalyMonitorName(anomaly.getAnomalyMonitor().getName());
        dto.setStartDate(anomaly.getStartDate());
        dto.setLastDetectedDate(anomaly.getLastDetectedDate());
        dto.setExpectedSpend(anomaly.getExpectedSpend());
        dto.setActualSpend(anomaly.getActualSpend());
        dto.setLinkedAccountId(anomaly.getLinkedAccountId());
        dto.setLinkedAccountName(anomaly.getLinkedAccountName());
        dto.setCreatedAt(anomaly.getCreatedAt());
        dto.setUpdatedAt(anomaly.getUpdatedAt());
        dto.setCreatedBy(anomaly.getCreatedBy() != null ? anomaly.getCreatedBy().getUsername() : null);
        dto.setUpdatedBy(anomaly.getUpdatedBy() != null ? anomaly.getUpdatedBy().getUsername() : null);
        return dto;
    }
}