package com.example.poc_finops.recommendations.domain.valueobject;

public enum ActionType {
    RIGHTSIZE("Rightsize"),
    TERMINATE("Terminate"),
    MODIFY("Modify"),
    MIGRATE("Migrate"),
    RESERVE("Reserve"),
    SAVINGS_PLAN("Savings Plan"),
    SPOT_INSTANCE("Spot Instance"),
    UPGRADE("Upgrade"),
    DOWNGRADE("Downgrade");
    
    private final String displayName;
    
    ActionType(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public static ActionType fromDisplayName(String displayName) {
        for (ActionType type : ActionType.values()) {
            if (type.displayName.equalsIgnoreCase(displayName)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown action type: " + displayName);
    }
}