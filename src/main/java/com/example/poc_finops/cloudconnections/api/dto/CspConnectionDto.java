package com.example.poc_finops.cloudconnections.api.dto;

import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
public class CspConnectionDto {
    private UUID id;
    private UUID organizationId;
    private String organizationName;
    private UUID cspId;
    private String cspName;
    private UUID dataSourceId;
    private String dataSourceName;
    private String name;
    private String description;
    private UUID planTypeId;
    private String planTypeName;
    private String accessKeyId;
    private String secretKeyId;
    private UUID regionId;
    private String regionName;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private UUID createdBy;
    private UUID updatedBy;
}