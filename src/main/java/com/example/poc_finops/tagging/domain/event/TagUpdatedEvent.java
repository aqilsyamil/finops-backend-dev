package com.example.poc_finops.tagging.domain.event;

import com.example.poc_finops.tagging.domain.entity.Tags;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
public class TagUpdatedEvent {
    private UUID tagId;
    private UUID cspConnectionId;
    private UUID serviceId;
    private String serviceName;
    private UUID updatedBy;
    private OffsetDateTime updatedAt;
    
    public static TagUpdatedEvent fromEntity(Tags tags) {
        return new TagUpdatedEvent(
            tags.getId(),
            tags.getCspConnection().getId(),
            tags.getService() != null ? tags.getService().getId() : null,
            tags.getService() != null ? tags.getService().getServiceName() : null,
            tags.getUpdatedBy() != null ? tags.getUpdatedBy().getId() : null,
            tags.getUpdatedAt()
        );
    }
}