package com.example.poc_finops.anomalydetection.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Service summary with anomaly statistics")
public class ServiceSummaryDto {
    
    @Schema(description = "Service name")
    private String serviceName;
    
    @Schema(description = "Total number of anomalies detected for this service")
    private Long anomalyCount;
    
    @Schema(description = "Total cost impact (sum of all variances)")
    private Double totalCostImpact;
    
    @Schema(description = "Average cost impact per anomaly")
    private Double averageCostImpact;
    
    @Schema(description = "Total expected spend for this service")
    private Double totalExpectedSpend;
    
    @Schema(description = "Total actual spend for this service")
    private Double totalActualSpend;
    
    @Schema(description = "Overall variance percentage for this service")
    private Double overallVariancePercentage;
}