package com.example.poc_finops.anomalydetection.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to create a new monitor with alert configuration")
public class CreateMonitorRequest {
    
    @NotNull(message = "CSP connection ID is required")
    @Schema(description = "CSP connection ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private UUID cspConnectionId;
    
    @NotBlank(message = "Monitor name is required")
    @Schema(description = "Monitor name", requiredMode = Schema.RequiredMode.REQUIRED, example = "EC2 Cost Monitor")
    private String name;
    
    @Schema(description = "Monitor type", example = "COST_ANOMALY")
    private String type;
    
    @Schema(description = "Monitored dimensions (JSON format)", example = "{\"SERVICE\": [\"EC2\"], \"REGION\": [\"us-east-1\"]}")
    private String monitoredDimensions;
    
    @Schema(description = "Tags (JSON format)", example = "{\"Environment\": \"Production\", \"Team\": \"FinOps\"}")
    private String tags;
    
    @Valid
    @Schema(description = "Alert configuration")
    private AlertConfiguration alertConfiguration;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AlertConfiguration {
        
        @NotBlank(message = "Alert name is required")
        @Schema(description = "Alert name", requiredMode = Schema.RequiredMode.REQUIRED, example = "High Cost Anomaly Alert")
        private String alertName;
        
        @NotEmpty(message = "At least one recipient is required")
        @Schema(description = "List of alert recipients", requiredMode = Schema.RequiredMode.REQUIRED)
        private List<String> recipientEmails;
    }
}