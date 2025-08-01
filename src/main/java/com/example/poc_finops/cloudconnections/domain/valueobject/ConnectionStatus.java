package com.example.poc_finops.cloudconnections.domain.valueobject;

public enum ConnectionStatus {
    PENDING("Connection not yet tested"),
    CONNECTED("Connection successfully established"),
    FAILED("Connection failed during validation"),
    DISCONNECTED("Connection was previously established but is now disconnected");
    
    private final String description;
    
    ConnectionStatus(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
}