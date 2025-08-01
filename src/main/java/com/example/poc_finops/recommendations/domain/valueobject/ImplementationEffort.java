package com.example.poc_finops.recommendations.domain.valueobject;

public enum ImplementationEffort {
    LOW("Low"),
    MEDIUM("Medium"),
    HIGH("High"),
    VERY_HIGH("Very High");
    
    private final String displayName;
    
    ImplementationEffort(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public static ImplementationEffort fromDisplayName(String displayName) {
        for (ImplementationEffort effort : ImplementationEffort.values()) {
            if (effort.displayName.equalsIgnoreCase(displayName)) {
                return effort;
            }
        }
        throw new IllegalArgumentException("Unknown implementation effort: " + displayName);
    }
}