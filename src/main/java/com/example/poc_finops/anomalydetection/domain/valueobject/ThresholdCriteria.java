package com.example.poc_finops.anomalydetection.domain.valueobject;

public enum ThresholdCriteria {
    GREATER_THAN("Greater Than"),
    LESS_THAN("Less Than"),
    EQUALS("Equals"),
    GREATER_THAN_OR_EQUAL("Greater Than or Equal"),
    LESS_THAN_OR_EQUAL("Less Than or Equal"),
    BETWEEN("Between"),
    OUTSIDE("Outside");
    
    private final String displayName;
    
    ThresholdCriteria(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public static ThresholdCriteria fromDisplayName(String displayName) {
        for (ThresholdCriteria criteria : ThresholdCriteria.values()) {
            if (criteria.displayName.equalsIgnoreCase(displayName)) {
                return criteria;
            }
        }
        throw new IllegalArgumentException("Unknown threshold criteria: " + displayName);
    }
}