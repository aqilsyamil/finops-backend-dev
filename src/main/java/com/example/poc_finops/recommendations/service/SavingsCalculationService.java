package com.example.poc_finops.recommendations.service;

import com.example.poc_finops.recommendations.domain.valueobject.SavingsMetrics;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface SavingsCalculationService {
    SavingsMetrics calculateSavings(BigDecimal currentCost, BigDecimal recommendedCost, String currencyCode);
    BigDecimal calculateSavingsPercentage(BigDecimal currentCost, BigDecimal recommendedCost);
    BigDecimal calculateAnnualSavings(BigDecimal monthlySavings);
        
}