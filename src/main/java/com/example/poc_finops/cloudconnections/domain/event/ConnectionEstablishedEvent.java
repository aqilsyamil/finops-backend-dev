package com.example.poc_finops.cloudconnections.domain.event;

import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
public class ConnectionEstablishedEvent {
    private UUID connectionId;
    private String connectionName;
    private UUID organizationId;
    private UUID cspId;
    private String cspType;
    private OffsetDateTime establishedAt;
    private UUID establishedBy;

    public ConnectionEstablishedEvent(UUID connectionId, String connectionName, 
                                    UUID organizationId, UUID cspId, String cspType, 
                                    OffsetDateTime establishedAt, UUID establishedBy) {
        this.connectionId = connectionId;
        this.connectionName = connectionName;
        this.organizationId = organizationId;
        this.cspId = cspId;
        this.cspType = cspType;
        this.establishedAt = establishedAt;
        this.establishedBy = establishedBy;
    }
}