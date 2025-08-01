package com.example.poc_finops.budgetmanagement.domain.valueobject;

public enum ThresholdType {
    ACTUAL_COST("actual cost"),
    ACTUAL_SPEND("actual spend");
    
    private final String value;
    
    ThresholdType(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
    
    public static ThresholdType fromValue(String value) {
        for (ThresholdType type : ThresholdType.values()) {
            if (type.value.equals(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown threshold type: " + value);
    }
}