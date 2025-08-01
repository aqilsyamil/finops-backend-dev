package com.example.poc_finops.costanalytics.service;

import com.example.poc_finops.costanalytics.api.dto.AthenaQueryRequest;
import com.example.poc_finops.costanalytics.api.dto.AthenaQueryResponse;
import com.example.poc_finops.costanalytics.api.dto.QueryExecutionResponse;

import java.util.UUID;

public interface AthenaService {
    
    QueryExecutionResponse executeQuery(UUID cspConnectionId, AthenaQueryRequest queryRequest);
    
    AthenaQueryResponse getQueryResults(String queryExecutionId, UUID cspConnectionId);
    
    String getQueryStatus(String queryExecutionId, UUID cspConnectionId);
    
    void cancelQuery(String queryExecutionId, UUID cspConnectionId);
}