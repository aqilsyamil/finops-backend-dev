package com.example.poc_finops.anomalydetection.domain.valueobject;

public enum AnomalyType {
    COST_ANOMALY("Cost Anomaly"),
    USAGE_ANOMALY("Usage Anomaly"),
    BILLING_ANOMALY("Billing Anomaly"),
    SERVICE_ANOMALY("Service Anomaly"),
    REGION_ANOMALY("Region Anomaly");
    
    private final String displayName;
    
    AnomalyType(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public static AnomalyType fromDisplayName(String displayName) {
        for (AnomalyType type : AnomalyType.values()) {
            if (type.displayName.equalsIgnoreCase(displayName)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown anomaly type: " + displayName);
    }
}