package com.example.poc_finops.costanalytics.api;

import com.example.poc_finops.costanalytics.api.dto.CostBreakdownDto;
import com.example.poc_finops.costanalytics.api.dto.FocusUsageDto;
import com.example.poc_finops.costanalytics.api.dto.UsageReportRequest;
import com.example.poc_finops.costanalytics.service.UsageReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/usage-reports")
@RequiredArgsConstructor
@Tag(name = "Usage Reports (To Be Completed)", description = "APIs for generating and exporting usage reports")
public class UsageController {

    private final UsageReportService usageReportService;

    @PostMapping("/batch")
    @Operation(summary = "To Be Completed", description = "Schedule a recurring report generation")
    public ResponseEntity<String> scheduleReport(
            @RequestBody UsageReportRequest request,
            @RequestParam String frequency) {
        
        usageReportService.generateScheduledReport(request, frequency);
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body("Report scheduled successfully with frequency: " + frequency);
    }

    // @PostMapping("/display")
    // @Operation(summary = "Display report", description = "Validate a report request without generating the report")
    // public ResponseEntity<String> validateReportRequest(@RequestBody UsageReportRequest request) {
    //     boolean isValid = usageReportService.validateReportRequest(request);
    //     if (isValid) {
    //         return ResponseEntity.ok("Report request is valid");
    //     } else {
    //         return ResponseEntity.badRequest().body("Report request is invalid");
    //     }
    // }
}