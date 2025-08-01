package com.example.poc_finops.anomalydetection.domain.event;

import com.example.poc_finops.anomalydetection.domain.entity.Alert;
import com.example.poc_finops.anomalydetection.domain.valueobject.AlertSeverity;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
public class AlertTriggeredEvent {
    private UUID alertId;
    private String alertName;
    private AlertSeverity severity;
    private String message;
    private UUID triggeredBy;
    private OffsetDateTime triggeredAt;
    
    public static AlertTriggeredEvent fromEntity(Alert alert, AlertSeverity severity, String message) {
        return new AlertTriggeredEvent(
            alert.getId(),
            alert.getName(),
            severity,
            message,
            alert.getCreatedBy() != null ? alert.getCreatedBy().getId() : null,
            OffsetDateTime.now()
        );
    }
}