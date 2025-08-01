package com.example.poc_finops.budgetmanagement.domain.event;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
public class BudgetAlertTriggeredEvent {
    private UUID budgetId;
    private String budgetName;
    private List<String> recipientEmails;
    private String alertMessage;
    private String triggerReason;
    private UUID organizationId;
    private OffsetDateTime triggeredAt;
}