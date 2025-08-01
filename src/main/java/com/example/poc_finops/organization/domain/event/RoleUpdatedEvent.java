package com.example.poc_finops.organization.domain.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoleUpdatedEvent {
    private UUID roleId;
    private String roleName;
    private String roleType;
    private String status;
    private OffsetDateTime updatedAt;
    private UUID updatedBy;
}