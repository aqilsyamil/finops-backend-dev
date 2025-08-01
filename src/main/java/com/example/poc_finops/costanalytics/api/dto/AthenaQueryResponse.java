package com.example.poc_finops.costanalytics.api.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class AthenaQueryResponse {
    
    private String queryExecutionId;
    private String status;
    private List<String> columnNames;
    private List<Map<String, Object>> rows;
    private Long totalRows;
    private Long dataScannedInBytes;
    private Long executionTimeInMillis;
    private String resultLocation;
    private String errorMessage;
}