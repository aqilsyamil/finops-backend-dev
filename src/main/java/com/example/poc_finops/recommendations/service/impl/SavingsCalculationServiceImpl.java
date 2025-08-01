package com.example.poc_finops.recommendations.service.impl;

import com.example.poc_finops.recommendations.domain.entity.FocusRecommendations;
import com.example.poc_finops.recommendations.domain.valueobject.SavingsMetrics;
import com.example.poc_finops.recommendations.repository.FocusRecommendationsRepository;
import com.example.poc_finops.recommendations.service.SavingsCalculationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class SavingsCalculationServiceImpl implements SavingsCalculationService {

    private final FocusRecommendationsRepository recommendationsRepository;

    @Override
    public SavingsMetrics calculateSavings(BigDecimal currentCost, BigDecimal recommendedCost, String currencyCode) {
        return new SavingsMetrics(currentCost, recommendedCost, currencyCode);
    }

    @Override
    public BigDecimal calculateSavingsPercentage(BigDecimal currentCost, BigDecimal recommendedCost) {
        if (currentCost == null || currentCost.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal savings = currentCost.subtract(recommendedCost != null ? recommendedCost : BigDecimal.ZERO);
        return savings.divide(currentCost, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));
    }

    @Override
    public BigDecimal calculateAnnualSavings(BigDecimal monthlySavings) {
        return monthlySavings != null ? monthlySavings.multiply(BigDecimal.valueOf(12)) : BigDecimal.ZERO;
    }

    private SavingsMetrics aggregateRecommendations(List<FocusRecommendations> recommendations) {
        double totalCurrentCost = 0.0;
        double totalRecommendedCost = 0.0;
        String currencyCode = "USD"; // Default currency
        
        for (FocusRecommendations rec : recommendations) {
            if (rec.getEstimatedMonthlyCostBeforeDiscount() != null) {
                totalCurrentCost += rec.getEstimatedMonthlyCostBeforeDiscount();
            }
            if (rec.getEstimatedMonthlyCostAfterDiscount() != null) {
                totalRecommendedCost += rec.getEstimatedMonthlyCostAfterDiscount();
            }
            if (rec.getCurrencyCode() != null && !rec.getCurrencyCode().isEmpty()) {
                currencyCode = rec.getCurrencyCode();
            }
        }
        
        return new SavingsMetrics(BigDecimal.valueOf(totalCurrentCost), BigDecimal.valueOf(totalRecommendedCost), currencyCode);
    }
}