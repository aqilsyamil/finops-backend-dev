package com.example.poc_finops.costanalytics.domain.valueobject;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CostMetrics {
    private final BigDecimal billedCost;
    private final BigDecimal effectiveCost;
    private final BigDecimal listCost;
    private final BigDecimal contractedCost;
    private final BigDecimal consumedQuantity;
    private final String currency;

    public CostMetrics(BigDecimal billedCost, BigDecimal effectiveCost, BigDecimal listCost, 
                      BigDecimal contractedCost, BigDecimal consumedQuantity, String currency) {
        this.billedCost = billedCost != null ? billedCost : BigDecimal.ZERO;
        this.effectiveCost = effectiveCost != null ? effectiveCost : BigDecimal.ZERO;
        this.listCost = listCost != null ? listCost : BigDecimal.ZERO;
        this.contractedCost = contractedCost != null ? contractedCost : BigDecimal.ZERO;
        this.consumedQuantity = consumedQuantity != null ? consumedQuantity : BigDecimal.ZERO;
        this.currency = currency;
    }

    public BigDecimal getTotalSavings() {
        return listCost.subtract(effectiveCost);
    }

    public BigDecimal getSavingsPercentage() {
        if (listCost.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return getTotalSavings().divide(listCost, 4, java.math.RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }

    public BigDecimal getDiscountAmount() {
        return listCost.subtract(billedCost);
    }
}