package com.example.poc_finops.organization.api.dto;

import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
public class OrganizationDto {
    private UUID id;
    private String organizationName;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}