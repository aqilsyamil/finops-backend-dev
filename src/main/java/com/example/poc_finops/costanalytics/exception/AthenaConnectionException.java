package com.example.poc_finops.costanalytics.exception;

public class AthenaConnectionException extends RuntimeException {
    
    public AthenaConnectionException(String message) {
        super(message);
    }
    
    public AthenaConnectionException(String message, Throwable cause) {
        super(message, cause);
    }
}