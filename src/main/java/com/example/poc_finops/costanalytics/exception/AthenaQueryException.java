package com.example.poc_finops.costanalytics.exception;

public class AthenaQueryException extends RuntimeException {
    
    public AthenaQueryException(String message) {
        super(message);
    }
    
    public AthenaQueryException(String message, Throwable cause) {
        super(message, cause);
    }
}