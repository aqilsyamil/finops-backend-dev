package com.example.poc_finops.costanalytics.exception;

public class AthenaQueryTimeoutException extends AthenaQueryException {
    
    private final String queryExecutionId;
    private final int timeoutMinutes;
    
    public AthenaQueryTimeoutException(String queryExecutionId, int timeoutMinutes) {
        super(String.format("Query execution %s timed out after %d minutes", queryExecutionId, timeoutMinutes));
        this.queryExecutionId = queryExecutionId;
        this.timeoutMinutes = timeoutMinutes;
    }
    
    public AthenaQueryTimeoutException(String queryExecutionId, int timeoutMinutes, Throwable cause) {
        super(String.format("Query execution %s timed out after %d minutes", queryExecutionId, timeoutMinutes), cause);
        this.queryExecutionId = queryExecutionId;
        this.timeoutMinutes = timeoutMinutes;
    }
    
    public String getQueryExecutionId() {
        return queryExecutionId;
    }
    
    public int getTimeoutMinutes() {
        return timeoutMinutes;
    }
}