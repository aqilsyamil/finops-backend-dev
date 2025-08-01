package com.example.poc_finops.anomalydetection.domain.event;

import com.example.poc_finops.anomalydetection.domain.entity.FocusAnomalyDetection;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
public class AnomalyDetectedEvent {
    private UUID anomalyId;
    private UUID monitorId;
    private Double expectedSpend;
    private Double actualSpend;
    private LocalDate detectedDate;
    private String linkedAccountId;
    private UUID detectedBy;
    private OffsetDateTime detectedAt;
    
    public static AnomalyDetectedEvent fromEntity(FocusAnomalyDetection anomaly) {
        return new AnomalyDetectedEvent(
            anomaly.getId(),
            anomaly.getAnomalyMonitor().getId(),
            anomaly.getExpectedSpend(),
            anomaly.getActualSpend(),
            anomaly.getLastDetectedDate(),
            anomaly.getLinkedAccountId(),
            anomaly.getCreatedBy() != null ? anomaly.getCreatedBy().getId() : null,
            anomaly.getCreatedAt()
        );
    }
}