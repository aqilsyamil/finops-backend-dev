package com.example.poc_finops.anomalydetection.api.dto;

import com.example.poc_finops.anomalydetection.domain.entity.Alert;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnomalyAlertDto {
    private UUID id;
    private String name;
    private List<String> recipients;
    private List<AlertThresholdDto> thresholds;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
    
    public static AnomalyAlertDto fromEntity(Alert alert) {
        AnomalyAlertDto dto = new AnomalyAlertDto();
        dto.setId(alert.getId());
        dto.setName(alert.getName());
        dto.setCreatedAt(alert.getCreatedAt());
        dto.setUpdatedAt(alert.getUpdatedAt());
        dto.setCreatedBy(alert.getCreatedBy() != null ? alert.getCreatedBy().getUsername() : null);
        dto.setUpdatedBy(alert.getUpdatedBy() != null ? alert.getUpdatedBy().getUsername() : null);
        return dto;
    }
}