package com.example.poc_finops.anomalydetection.api.dto;

import com.example.poc_finops.anomalydetection.domain.entity.AlertThreshold;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlertThresholdDto {
    private UUID id;
    private UUID alertId;
    private Double thresholdAmount1;
    private Double thresholdAmount2;
    private String thresholdType1;
    private String thresholdType2;
    private String relation;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
    
    public static AlertThresholdDto fromEntity(AlertThreshold threshold) {
        AlertThresholdDto dto = new AlertThresholdDto();
        dto.setId(threshold.getId());
        dto.setAlertId(threshold.getAlert().getId());
        dto.setThresholdAmount1(threshold.getThresholdAmount1());
        dto.setThresholdAmount2(threshold.getThresholdAmount2());
        dto.setThresholdType1(threshold.getThresholdType1());
        dto.setThresholdType2(threshold.getThresholdType2());
        dto.setRelation(threshold.getRelation());
        dto.setCreatedAt(threshold.getCreatedAt());
        dto.setUpdatedAt(threshold.getUpdatedAt());
        dto.setCreatedBy(threshold.getCreatedBy() != null ? threshold.getCreatedBy().getUsername() : null);
        dto.setUpdatedBy(threshold.getUpdatedBy() != null ? threshold.getUpdatedBy().getUsername() : null);
        return dto;
    }
}