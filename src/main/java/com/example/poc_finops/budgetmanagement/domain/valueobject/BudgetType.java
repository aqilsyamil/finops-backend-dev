package com.example.poc_finops.budgetmanagement.domain.valueobject;

public enum BudgetType {
    FIXED("fixed");
    
    private final String value;
    
    BudgetType(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
    
    public static BudgetType fromValue(String value) {
        for (BudgetType type : BudgetType.values()) {
            if (type.value.equals(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown budget type: " + value);
    }
}