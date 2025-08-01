package com.example.poc_finops.cloudconnections.api.dto;

import com.example.poc_finops.organization.api.dto.OrganizationDto;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
public class GroupConnectionDto {
    private UUID id;
    private String name;
    private OrganizationDto organization;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private UUID createdBy;
    private UUID updatedBy;
}