package com.example.poc_finops.costanalytics.api.dto;

import lombok.Data;

@Data
public class QueryExecutionResponse {
    
    private String queryExecutionId;
    private String status;
    private String message;
    private String s3ResultLocation;
}