package com.example.poc_finops.costanalytics.domain.event;

import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
public class CostDataUpdatedEvent {
    private UUID focusUsageId;
    private UUID focusLogId;
    private UUID organizationId;
    private String resourceId;
    private String serviceName;
    private BigDecimal previousCost;
    private BigDecimal newCost;
    private String currency;
    private OffsetDateTime updatedAt;
    private UUID updatedBy;

    public CostDataUpdatedEvent(UUID focusUsageId, UUID focusLogId, UUID organizationId,
                               String resourceId, String serviceName, BigDecimal previousCost,
                               BigDecimal newCost, String currency, OffsetDateTime updatedAt, UUID updatedBy) {
        this.focusUsageId = focusUsageId;
        this.focusLogId = focusLogId;
        this.organizationId = organizationId;
        this.resourceId = resourceId;
        this.serviceName = serviceName;
        this.previousCost = previousCost;
        this.newCost = newCost;
        this.currency = currency;
        this.updatedAt = updatedAt;
        this.updatedBy = updatedBy;
    }

    public BigDecimal getCostDifference() {
        return newCost.subtract(previousCost);
    }
}