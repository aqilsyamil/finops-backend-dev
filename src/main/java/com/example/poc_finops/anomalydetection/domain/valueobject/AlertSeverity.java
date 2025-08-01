package com.example.poc_finops.anomalydetection.domain.valueobject;

public enum AlertSeverity {
    LOW("Low"),
    MEDIUM("Medium"),
    HIGH("High"),
    CRITICAL("Critical");
    
    private final String displayName;
    
    AlertSeverity(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public static AlertSeverity fromDisplayName(String displayName) {
        for (AlertSeverity severity : AlertSeverity.values()) {
            if (severity.displayName.equalsIgnoreCase(displayName)) {
                return severity;
            }
        }
        throw new IllegalArgumentException("Unknown alert severity: " + displayName);
    }
}