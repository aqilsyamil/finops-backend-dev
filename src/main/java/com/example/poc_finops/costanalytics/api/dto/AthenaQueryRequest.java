package com.example.poc_finops.costanalytics.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.UUID;

@Data
public class AthenaQueryRequest {
    
    @NotBlank(message = "Query is required")
    @Size(max = 10000, message = "Query length cannot exceed 10000 characters")
    private String query;
    
    @NotNull(message = "Organization ID is required")
    private UUID organizationId;
}