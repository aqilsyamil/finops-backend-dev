package com.example.poc_finops.costanalytics.api.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
public class CostBreakdownDto {
    private LocalDate reportDate;
    private String organizationName;
    private String currency;
    private Double totalCost;
    private Double totalEffectiveCost;
    private Double totalSavings;
    private Double savingsPercentage;
    
    // Breakdown by service
    private Map<String, ServiceCostDto> serviceBreakdown;
    
    // Breakdown by region
    private Map<String, Double> regionBreakdown;
    
    // Breakdown by charge category
    private Map<String, Double> categoryBreakdown;
    
    // Top cost resources
    private List<ResourceCostDto> topResources;

    @Data
    public static class ServiceCostDto {
        private String serviceName;
        private Double cost;
        private Double effectiveCost;
        private Double consumedQuantity;
        private String unit;
        private Integer resourceCount;
    }

    @Data
    public static class ResourceCostDto {
        private String resourceId;
        private String resourceName;
        private String serviceName;
        private String region;
        private Double cost;
        private Double effectiveCost;
        private String chargeCategory;
    }
}