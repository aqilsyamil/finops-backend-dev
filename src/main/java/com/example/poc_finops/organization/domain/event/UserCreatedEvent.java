package com.example.poc_finops.organization.domain.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserCreatedEvent {
    private UUID userId;
    private String username;
    private String email;
    private UUID organizationId;
    private UUID roleId;
    private OffsetDateTime createdAt;
    private UUID createdBy;
}