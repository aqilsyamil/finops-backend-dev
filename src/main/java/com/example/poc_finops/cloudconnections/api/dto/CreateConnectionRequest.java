package com.example.poc_finops.cloudconnections.api.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class CreateConnectionRequest {
    private UUID organizationId;
    private UUID cspId;
    private UUID dataSourceId;
    private String name;
    private String description;
    private UUID planTypeId;
    private String accessKeyId;
    private String secretKeyId;
    private UUID regionId;
}