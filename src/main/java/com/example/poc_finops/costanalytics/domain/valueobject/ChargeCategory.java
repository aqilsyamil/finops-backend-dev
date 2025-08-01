package com.example.poc_finops.costanalytics.domain.valueobject;

public enum ChargeCategory {
    USAGE("Usage"),
    PURCHASE("Purchase"),
    TAX("Tax"),
    CREDIT("Credit"),
    ADJUSTMENT("Adjustment"),
    ROUNDING("Rounding");

    private final String value;

    ChargeCategory(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static ChargeCategory fromValue(String value) {
        for (ChargeCategory category : ChargeCategory.values()) {
            if (category.value.equalsIgnoreCase(value)) {
                return category;
            }
        }
        throw new IllegalArgumentException("Invalid charge category: " + value);
    }
}