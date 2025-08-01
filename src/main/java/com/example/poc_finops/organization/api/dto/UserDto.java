package com.example.poc_finops.organization.api.dto;

import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
public class UserDto {
    private UUID id;
    private String username;
    private String email;
    private String status;
    private OffsetDateTime lastAccessAt;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private OrganizationDto organization;
    private RoleDto role;
}