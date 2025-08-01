package com.example.poc_finops.costanalytics.api;

import com.example.poc_finops.costanalytics.api.dto.CostBreakdownDto;
import com.example.poc_finops.costanalytics.domain.valueobject.CostMetrics;
import com.example.poc_finops.costanalytics.service.CostAnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/cost-analytics")
@RequiredArgsConstructor
@Tag(name = "Cost Analytics", description = "APIs for cost analysis and metrics")
public class CostReportController {

    private final CostAnalyticsService costAnalyticsService;


    // @GetMapping("/total-cost")
    // @Operation(summary = "Get total cost", description = "Get total billed cost for an organization in a date range")
    // public ResponseEntity<BigDecimal> getTotalCost(
    //         @RequestParam UUID organizationId,
    //         @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
    //         @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
    //     BigDecimal totalCost = costAnalyticsService.getTotalCost(organizationId, startDate, endDate);
    //     return ResponseEntity.ok(totalCost);
    // }

    // @GetMapping("/effective-cost")
    // @Operation(summary = "Get effective cost", description = "Get total effective cost for an organization in a date range")
    // public ResponseEntity<BigDecimal> getTotalEffectiveCost(
    //         @RequestParam UUID organizationId,
    //         @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
    //         @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
    //     BigDecimal effectiveCost = costAnalyticsService.getTotalEffectiveCost(organizationId, startDate, endDate);
    //     return ResponseEntity.ok(effectiveCost);
    // }

    // @GetMapping("/savings")
    // @Operation(summary = "Get total savings", description = "Get total cost savings for an organization in a date range")
    // public ResponseEntity<BigDecimal> getTotalSavings(
    //         @RequestParam UUID organizationId,
    //         @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
    //         @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
    //     BigDecimal savings = costAnalyticsService.getTotalSavings(organizationId, startDate, endDate);
    //     return ResponseEntity.ok(savings);
    // }

    // @GetMapping("/cost-by-service")
    // @Operation(summary = "Get cost by service", description = "Get cost breakdown by service for an organization")
    // public ResponseEntity<Map<String, BigDecimal>> getCostByService(
    //         @RequestParam UUID organizationId,
    //         @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
    //         @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
    //     Map<String, BigDecimal> costByService = costAnalyticsService.getCostByService(organizationId, startDate, endDate);
    //     return ResponseEntity.ok(costByService);
    // }

    // @GetMapping("/cost-by-region")
    // @Operation(summary = "Get cost by region", description = "Get cost breakdown by region for an organization")
    // public ResponseEntity<Map<String, BigDecimal>> getCostByRegion(
    //         @RequestParam UUID organizationId,
    //         @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
    //         @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
    //     Map<String, BigDecimal> costByRegion = costAnalyticsService.getCostByRegion(organizationId, startDate, endDate);
    //     return ResponseEntity.ok(costByRegion);
    // }

    // @GetMapping("/cost-by-charge-category")
    // @Operation(summary = "Get cost by charge category", description = "Get cost breakdown by charge category for an organization")
    // public ResponseEntity<Map<String, BigDecimal>> getCostByChargeCategory(
    //         @RequestParam UUID organizationId,
    //         @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
    //         @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
    //     Map<String, BigDecimal> costByCategory = costAnalyticsService.getCostByChargeCategory(organizationId, startDate, endDate);
    //     return ResponseEntity.ok(costByCategory);
    // }

    // @GetMapping("/top-cost-resources")
    // @Operation(summary = "Get top cost resources", description = "Get the highest cost resources for an organization")
    // public ResponseEntity<List<CostBreakdownDto.ResourceCostDto>> getTopCostResources(
    //         @RequestParam UUID organizationId,
    //         @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
    //         @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
    //         @RequestParam(defaultValue = "10") Integer limit) {
        
    //     List<CostBreakdownDto.ResourceCostDto> topResources = 
    //         costAnalyticsService.getTopCostResources(organizationId, startDate, endDate, limit);
    //     return ResponseEntity.ok(topResources);
    // }

    // @GetMapping("/top-cost-services")
    // @Operation(summary = "Get top cost services", description = "Get the highest cost services for an organization")
    // public ResponseEntity<List<CostBreakdownDto.ServiceCostDto>> getTopCostServices(
    //         @RequestParam UUID organizationId,
    //         @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
    //         @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
    //         @RequestParam(defaultValue = "10") Integer limit) {
        
    //     List<CostBreakdownDto.ServiceCostDto> topServices = 
    //         costAnalyticsService.getTopCostServices(organizationId, startDate, endDate, limit);
    //     return ResponseEntity.ok(topServices);
    // }

    // @GetMapping("/metrics")
    // @Operation(summary = "Get cost metrics", description = "Get comprehensive cost metrics for an organization")
    // public ResponseEntity<CostMetrics> getCostMetrics(
    //         @RequestParam UUID organizationId,
    //         @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
    //         @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
    //         @RequestParam(defaultValue = "USD") String currency) {
        
    //     CostMetrics metrics = costAnalyticsService.getCostMetrics(organizationId, startDate, endDate, currency);
    //     return ResponseEntity.ok(metrics);
    // }

    // @GetMapping("/metrics/service/{serviceName}")
    // @Operation(summary = "Get service-specific cost metrics", description = "Get cost metrics for a specific service")
    // public ResponseEntity<CostMetrics> getCostMetricsByService(
    //         @RequestParam UUID organizationId,
    //         @PathVariable String serviceName,
    //         @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
    //         @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
    //     CostMetrics metrics = costAnalyticsService.getCostMetricsByService(organizationId, serviceName, startDate, endDate);
    //     return ResponseEntity.ok(metrics);
    // }

    // @GetMapping("/trend/daily")
    // @Operation(summary = "Get daily cost trend", description = "Get daily cost trend for an organization")
    // public ResponseEntity<Map<LocalDate, BigDecimal>> getDailyCostTrend(
    //         @RequestParam UUID organizationId,
    //         @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
    //         @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
    //     Map<LocalDate, BigDecimal> trend = costAnalyticsService.getDailyCostTrend(organizationId, startDate, endDate);
    //     return ResponseEntity.ok(trend);
    // }

    // @GetMapping("/trend/service")
    // @Operation(summary = "Get service cost trend", description = "Get daily cost trend by service for an organization")
    // public ResponseEntity<Map<String, Map<LocalDate, BigDecimal>>> getServiceCostTrend(
    //         @RequestParam UUID organizationId,
    //         @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
    //         @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
    //     Map<String, Map<LocalDate, BigDecimal>> trend = 
    //         costAnalyticsService.getServiceCostTrend(organizationId, startDate, endDate);
    //     return ResponseEntity.ok(trend);
    // }

    // @GetMapping("/growth")
    // @Operation(summary = "Get cost growth", description = "Calculate cost growth percentage between two periods")
    // public ResponseEntity<BigDecimal> getCostGrowth(
    //         @RequestParam UUID organizationId,
    //         @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate currentPeriodStart,
    //         @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate currentPeriodEnd,
    //         @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate previousPeriodStart,
    //         @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate previousPeriodEnd) {
        
    //     BigDecimal growth = costAnalyticsService.getCostGrowth(
    //         organizationId, currentPeriodStart, currentPeriodEnd, previousPeriodStart, previousPeriodEnd);
    //     return ResponseEntity.ok(growth);
    // }
}