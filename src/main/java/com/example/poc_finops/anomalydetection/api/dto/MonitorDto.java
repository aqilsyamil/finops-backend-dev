package com.example.poc_finops.anomalydetection.api.dto;

import com.example.poc_finops.anomalydetection.domain.entity.FocusAnomalyMonitors;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MonitorDto {
    private UUID id;
    private UUID serviceId;
    private String name;
    private String type;
    private LocalDate dateCreated;
    private LocalDate dateUpdated;
    private String monitoredDimensions;
    private String tags;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
    
    public static MonitorDto fromEntity(FocusAnomalyMonitors monitor) {
        MonitorDto dto = new MonitorDto();
        dto.setId(monitor.getId());
        dto.setServiceId(monitor.getService() != null ? monitor.getService().getId() : null);
        dto.setName(monitor.getName());
        dto.setType(monitor.getType());
        dto.setDateCreated(monitor.getDateCreated());
        dto.setDateUpdated(monitor.getDateUpdated());
        dto.setMonitoredDimensions(monitor.getMonitoredDimensions());
        dto.setTags(monitor.getTags());
        dto.setCreatedAt(monitor.getCreatedAt());
        dto.setUpdatedAt(monitor.getUpdatedAt());
        dto.setCreatedBy(monitor.getCreatedBy() != null ? monitor.getCreatedBy().getUsername() : null);
        dto.setUpdatedBy(monitor.getUpdatedBy() != null ? monitor.getUpdatedBy().getUsername() : null);
        return dto;
    }
}