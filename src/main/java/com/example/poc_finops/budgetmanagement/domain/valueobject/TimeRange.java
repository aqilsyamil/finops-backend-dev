package com.example.poc_finops.budgetmanagement.domain.valueobject;

public enum TimeRange {
    MONTHLY("monthly"),
    QUARTERLY("quarterly"),
    YEARLY("yearly");
    
    private final String value;
    
    TimeRange(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
    
    public static TimeRange fromValue(String value) {
        for (TimeRange range : TimeRange.values()) {
            if (range.value.equals(value)) {
                return range;
            }
        }
        throw new IllegalArgumentException("Unknown time range: " + value);
    }
}