package com.example.poc_finops.costanalytics.domain.valueobject;

public enum ChargeClass {
    COMMITTED("Committed"),
    ON_DEMAND("OnDemand"),
    SPOT("Spot");

    private final String value;

    ChargeClass(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static ChargeClass fromValue(String value) {
        for (ChargeClass chargeClass : ChargeClass.values()) {
            if (chargeClass.value.equalsIgnoreCase(value)) {
                return chargeClass;
            }
        }
        throw new IllegalArgumentException("Invalid charge class: " + value);
    }
}