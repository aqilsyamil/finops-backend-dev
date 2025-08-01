package com.example.poc_finops.tagging.api.dto;

import com.example.poc_finops.tagging.domain.entity.TagStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TagStatusDto {
    private UUID id;
    private UUID tagId;
    private String name;
    private String value;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
    
    public static TagStatusDto fromEntity(TagStatus tagStatus) {
        TagStatusDto dto = new TagStatusDto();
        dto.setId(tagStatus.getId());
        dto.setTagId(tagStatus.getTags().getId());
        dto.setName(tagStatus.getName());
        dto.setValue(tagStatus.getValue());
        dto.setCreatedAt(tagStatus.getCreatedAt());
        dto.setUpdatedAt(tagStatus.getUpdatedAt());
        dto.setCreatedBy(tagStatus.getCreatedBy() != null ? tagStatus.getCreatedBy().getUsername() : null);
        dto.setUpdatedBy(tagStatus.getUpdatedBy() != null ? tagStatus.getUpdatedBy().getUsername() : null);
        return dto;
    }
}