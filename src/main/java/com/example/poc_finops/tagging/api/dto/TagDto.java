package com.example.poc_finops.tagging.api.dto;

import com.example.poc_finops.tagging.domain.entity.Tags;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TagDto {
    private UUID id;
    private UUID cspConnectionId;
    private UUID serviceId;
    private String serviceName;
    private List<TagStatusDto> tagStatuses;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
    
    public static TagDto fromEntity(Tags tags) {
        TagDto dto = new TagDto();
        dto.setId(tags.getId());
        dto.setCspConnectionId(tags.getCspConnection().getId());
        dto.setServiceId(tags.getService() != null ? tags.getService().getId() : null);
        dto.setServiceName(tags.getService() != null ? tags.getService().getServiceName() : null);
        dto.setCreatedAt(tags.getCreatedAt());
        dto.setUpdatedAt(tags.getUpdatedAt());
        dto.setCreatedBy(tags.getCreatedBy() != null ? tags.getCreatedBy().getUsername() : null);
        dto.setUpdatedBy(tags.getUpdatedBy() != null ? tags.getUpdatedBy().getUsername() : null);
        return dto;
    }
}