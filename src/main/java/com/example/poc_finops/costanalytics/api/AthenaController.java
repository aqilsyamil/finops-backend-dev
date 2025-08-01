package com.example.poc_finops.costanalytics.api;

import com.example.poc_finops.costanalytics.api.dto.AthenaQueryRequest;
import com.example.poc_finops.costanalytics.api.dto.AthenaQueryResponse;
import com.example.poc_finops.costanalytics.api.dto.QueryExecutionResponse;
import com.example.poc_finops.costanalytics.service.AthenaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/athena")
@RequiredArgsConstructor
@Tag(name = "Athena", description = "AWS Athena query operations")
@Validated
public class AthenaController {

    private final AthenaService athenaService;

    @PostMapping("/connections/{cspConnectionId}/execute")
    @Operation(summary = "Execute Athena query", description = "Execute a SQL query on AWS Athena using the specified CSP connection")
    public ResponseEntity<QueryExecutionResponse> executeQuery(
            @PathVariable UUID cspConnectionId,
            @RequestBody @Validated AthenaQueryRequest queryRequest) {
        
        log.info("Executing Athena query for connection: {}", cspConnectionId);
        
        QueryExecutionResponse response = athenaService.executeQuery(cspConnectionId, queryRequest);
        
        if ("FAILED".equals(response.getStatus())) {
            return ResponseEntity.badRequest().body(response);
        }
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/executions/{queryExecutionId}/results")
    @Operation(summary = "Get query results", 
               description = "Retrieve results for a completed Athena query execution. " +
                           "Returns 408 (Request Timeout) if query exceeds configured timeout limit.")
    public ResponseEntity<AthenaQueryResponse> getQueryResults(
            @PathVariable String queryExecutionId,
            @RequestParam UUID cspConnectionId) {
        
        log.info("Getting results for query execution: {}", queryExecutionId);
        
        AthenaQueryResponse response = athenaService.getQueryResults(queryExecutionId, cspConnectionId);
        
        if ("ERROR".equals(response.getStatus())) {
            return ResponseEntity.badRequest().body(response);
        }
        
        if ("TIMEOUT".equals(response.getStatus())) {
            log.warn("Query execution {} timed out", queryExecutionId);
            return ResponseEntity.status(408).body(response); // 408 Request Timeout
        }
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/executions/{queryExecutionId}/status")
    @Operation(summary = "Get query status", 
               description = "Get the current status of an Athena query execution. " +
                           "Possible statuses: QUEUED, RUNNING, SUCCEEDED, FAILED, CANCELLED, TIMEOUT, ERROR. " +
                           "Returns 408 (Request Timeout) for TIMEOUT status.")
    public ResponseEntity<String> getQueryStatus(
            @PathVariable String queryExecutionId,
            @RequestParam UUID cspConnectionId) {
        
        log.info("Getting status for query execution: {}", queryExecutionId);
        
        String status = athenaService.getQueryStatus(queryExecutionId, cspConnectionId);
        
        if ("TIMEOUT".equals(status)) {
            log.warn("Query execution {} timed out", queryExecutionId);
            return ResponseEntity.status(408).body(status); // 408 Request Timeout
        }
        
        if ("ERROR".equals(status)) {
            return ResponseEntity.badRequest().body(status);
        }
        
        return ResponseEntity.ok(status);
    }

    @DeleteMapping("/executions/{queryExecutionId}")
    @Operation(summary = "Cancel query", description = "Cancel a running Athena query execution")
    public ResponseEntity<Void> cancelQuery(
            @PathVariable String queryExecutionId,
            @RequestParam UUID cspConnectionId) {
        
        log.info("Cancelling query execution: {}", queryExecutionId);
        
        try {
            athenaService.cancelQuery(queryExecutionId, cspConnectionId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Failed to cancel query: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
}