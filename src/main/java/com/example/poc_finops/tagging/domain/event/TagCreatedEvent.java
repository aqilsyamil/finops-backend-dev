package com.example.poc_finops.tagging.domain.event;

import com.example.poc_finops.tagging.domain.entity.Tags;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
public class TagCreatedEvent {
    private UUID tagId;
    private UUID cspConnectionId;
    private UUID serviceId;
    private String serviceName;
    private UUID createdBy;
    private OffsetDateTime createdAt;
    
    public static TagCreatedEvent fromEntity(Tags tags) {
        return new TagCreatedEvent(
            tags.getId(),
            tags.getCspConnection().getId(),
            tags.getService() != null ? tags.getService().getId() : null,
            tags.getService() != null ? tags.getService().getServiceName() : null,
            tags.getCreatedBy() != null ? tags.getCreatedBy().getId() : null,
            tags.getCreatedAt()
        );
    }
}