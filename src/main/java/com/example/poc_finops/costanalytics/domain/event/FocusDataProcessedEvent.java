package com.example.poc_finops.costanalytics.domain.event;

import lombok.Data;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
public class FocusDataProcessedEvent {
    private UUID focusLogId;
    private UUID organizationId;
    private UUID cspConnectionId;
    private LocalDate logDate;
    private Boolean processingStatus;
    private Integer recordsProcessed;
    private OffsetDateTime processedAt;
    private UUID processedBy;

    public FocusDataProcessedEvent(UUID focusLogId, UUID organizationId, UUID cspConnectionId,
                                  LocalDate logDate, Boolean processingStatus, Integer recordsProcessed,
                                  OffsetDateTime processedAt, UUID processedBy) {
        this.focusLogId = focusLogId;
        this.organizationId = organizationId;
        this.cspConnectionId = cspConnectionId;
        this.logDate = logDate;
        this.processingStatus = processingStatus;
        this.recordsProcessed = recordsProcessed;
        this.processedAt = processedAt;
        this.processedBy = processedBy;
    }
}