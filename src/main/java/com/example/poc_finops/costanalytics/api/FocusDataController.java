package com.example.poc_finops.costanalytics.api;

import com.example.poc_finops.costanalytics.api.dto.FocusUsageDto;
import com.example.poc_finops.costanalytics.domain.entity.FocusLog;
import com.example.poc_finops.costanalytics.service.FocusDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/focus-data")
@RequiredArgsConstructor
@Tag(name = "Focus Data Management", description = "APIs for managing FOCUS log and usage data")
public class FocusDataController {

    private final FocusDataService focusDataService;

    @GetMapping("/logs")
    @Operation(summary = "Get all focus logs", description = "Retrieve a list of all focus logs")
    public ResponseEntity<List<FocusLog>> getAllFocusLogs() {
        List<FocusLog> logs = focusDataService.getAllFocusLogs();
        return ResponseEntity.ok(logs);
    }

    @GetMapping("/logs/organization/{organizationId}")
    @Operation(summary = "Get focus logs by organization", description = "Retrieve focus logs for a specific organization")
    public ResponseEntity<List<FocusLog>> getFocusLogsByOrganization(@PathVariable UUID organizationId) {
        List<FocusLog> logs = focusDataService.getFocusLogsByOrganization(organizationId);
        return ResponseEntity.ok(logs);
    }

    @GetMapping("/logs/{id}")
    @Operation(summary = "Get focus log by ID", description = "Retrieve a focus log by its ID")
    public ResponseEntity<FocusLog> getFocusLogById(@PathVariable UUID id) {
        FocusLog log = focusDataService.getFocusLogById(id);
        return ResponseEntity.ok(log);
    }

    @DeleteMapping("/logs/{id}")
    @Operation(summary = "Delete focus log", description = "Delete a focus log by its ID")
    public ResponseEntity<Void> deleteFocusLog(@PathVariable UUID id) {
        focusDataService.deleteFocusLog(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/usage/log/{focusLogId}")
    @Operation(summary = "Get usage by focus log", description = "Retrieve usage records for a specific focus log")
    public ResponseEntity<List<FocusUsageDto>> getFocusUsageByLogId(@PathVariable UUID focusLogId) {
        List<FocusUsageDto> usage = focusDataService.getFocusUsageByLogId(focusLogId);
        return ResponseEntity.ok(usage);
    }

    @GetMapping("/usage/organization/{organizationId}")
    @Operation(summary = "Get usage by organization", description = "Retrieve usage records for a specific organization")
    public ResponseEntity<List<FocusUsageDto>> getFocusUsageByOrganization(@PathVariable UUID organizationId) {
        List<FocusUsageDto> usage = focusDataService.getFocusUsageByOrganization(organizationId);
        return ResponseEntity.ok(usage);
    }

    @GetMapping("/usage/{id}")
    @Operation(summary = "Get usage by ID", description = "Retrieve a usage record by its ID")
    public ResponseEntity<FocusUsageDto> getFocusUsageById(@PathVariable UUID id) {
        FocusUsageDto usage = focusDataService.getFocusUsageById(id);
        return ResponseEntity.ok(usage);
    }

    @DeleteMapping("/usage/{id}")
    @Operation(summary = "Delete usage record", description = "Delete a usage record by its ID")
    public ResponseEntity<Void> deleteFocusUsage(@PathVariable UUID id) {
        focusDataService.deleteFocusUsage(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/usage/organization/{organizationId}/date-range")
    @Operation(summary = "Get usage by organization and date range", description = "Retrieve usage records for an organization within a date range")
    public ResponseEntity<List<FocusUsageDto>> getFocusUsageByDateRange(
            @PathVariable UUID organizationId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<FocusUsageDto> usage = focusDataService.getFocusUsageByDateRange(organizationId, startDate, endDate);
        return ResponseEntity.ok(usage);
    }
}