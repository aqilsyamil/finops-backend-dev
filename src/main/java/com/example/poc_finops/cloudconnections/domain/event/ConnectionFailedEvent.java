package com.example.poc_finops.cloudconnections.domain.event;

import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
public class ConnectionFailedEvent {
    private UUID connectionId;
    private String connectionName;
    private UUID organizationId;
    private UUID cspId;
    private String cspType;
    private String errorMessage;
    private OffsetDateTime failedAt;
    private UUID attemptedBy;

    public ConnectionFailedEvent(UUID connectionId, String connectionName, 
                               UUID organizationId, UUID cspId, String cspType, 
                               String errorMessage, OffsetDateTime failedAt, UUID attemptedBy) {
        this.connectionId = connectionId;
        this.connectionName = connectionName;
        this.organizationId = organizationId;
        this.cspId = cspId;
        this.cspType = cspType;
        this.errorMessage = errorMessage;
        this.failedAt = failedAt;
        this.attemptedBy = attemptedBy;
    }
}