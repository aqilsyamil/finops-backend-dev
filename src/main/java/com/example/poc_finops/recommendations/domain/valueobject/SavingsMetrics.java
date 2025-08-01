package com.example.poc_finops.recommendations.domain.valueobject;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Data
@AllArgsConstructor
public class SavingsMetrics {
    
    private BigDecimal monthlyCostBefore;
    private BigDecimal monthlyCostAfter;
    private BigDecimal monthlySavings;
    private BigDecimal savingsPercentage;
    private String currencyCode;
    
    public SavingsMetrics(BigDecimal monthlyCostBefore, BigDecimal monthlyCostAfter, String currencyCode) {
        this.monthlyCostBefore = monthlyCostBefore != null ? monthlyCostBefore : BigDecimal.ZERO;
        this.monthlyCostAfter = monthlyCostAfter != null ? monthlyCostAfter : BigDecimal.ZERO;
        this.currencyCode = currencyCode;
        this.monthlySavings = calculateSavings();
        this.savingsPercentage = calculateSavingsPercentage();
    }
    
    private BigDecimal calculateSavings() {
        return monthlyCostBefore.subtract(monthlyCostAfter);
    }
    
    private BigDecimal calculateSavingsPercentage() {
        if (monthlyCostBefore.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return monthlySavings.divide(monthlyCostBefore, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }
    
    public BigDecimal getAnnualSavings() {
        return monthlySavings.multiply(BigDecimal.valueOf(12));
    }
    
    public boolean hasSavings() {
        return monthlySavings.compareTo(BigDecimal.ZERO) > 0;
    }
}