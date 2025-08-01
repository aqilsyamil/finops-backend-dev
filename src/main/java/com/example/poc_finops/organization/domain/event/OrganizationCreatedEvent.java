package com.example.poc_finops.organization.domain.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrganizationCreatedEvent {
    private UUID organizationId;
    private String organizationName;
    private OffsetDateTime createdAt;
}