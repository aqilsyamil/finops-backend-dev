package com.example.poc_finops.recommendations.service.impl;

import com.example.poc_finops.recommendations.api.dto.RecommendationDto;
import com.example.poc_finops.recommendations.api.dto.RecommendationFilterRequest;
import com.example.poc_finops.recommendations.api.dto.SavingsRecommendationDto;
import com.example.poc_finops.recommendations.domain.entity.FocusRecommendations;
import com.example.poc_finops.recommendations.repository.FocusRecommendationsRepository;
import com.example.poc_finops.recommendations.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class RecommendationServiceImpl implements RecommendationService {

    private final FocusRecommendationsRepository recommendationsRepository;

    @Override
    @Transactional(readOnly = true)
    public List<RecommendationDto> getAllRecommendations() {
        return recommendationsRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public RecommendationDto getRecommendationById(UUID id) {
        FocusRecommendations recommendation = recommendationsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Recommendation not found with id: " + id));
        return convertToDto(recommendation);
    }

    @Override
    @Transactional(readOnly = true)
    public RecommendationDto getRecommendationByRecommendationId(String recommendationId) {
        FocusRecommendations recommendation = recommendationsRepository.findByRecommendationId(recommendationId)
                .orElseThrow(() -> new RuntimeException("Recommendation not found with recommendation id: " + recommendationId));
        return convertToDto(recommendation);
    }


    @Override
    @Transactional(readOnly = true)
    public List<RecommendationDto> getRecommendationsByConnectionId(UUID connectionId) {
        return recommendationsRepository.findByCspConnectionId(connectionId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RecommendationDto> filterRecommendations(RecommendationFilterRequest filter) {
        List<FocusRecommendations> recommendations = recommendationsRepository.findAll();
        
        return recommendations.stream()
                .filter(rec -> filterByFocusLog(rec, filter.getFocusLogId()))
                .filter(rec -> filterByActionTypes(rec, filter.getActionTypes()))
                .filter(rec -> filterByImplementationEfforts(rec, filter.getImplementationEfforts()))
                .filter(rec -> filterByRegions(rec, filter.getRegions()))
                .filter(rec -> filterByRecommendationSource(rec, filter.getRecommendationSource()))
                .filter(rec -> filterByMinSavings(rec, filter.getMinSavings()))
                .filter(rec -> filterByMaxSavings(rec, filter.getMaxSavings()))
                .filter(rec -> filterByMinSavingsPercentage(rec, filter.getMinSavingsPercentage()))
                .filter(rec -> filterByRestartNeeded(rec, filter.getRestartNeeded()))
                .filter(rec -> filterByRollbackPossible(rec, filter.getRollbackPossible()))
                .limit(filter.getLimit())
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteRecommendation(UUID id) {
        recommendationsRepository.deleteById(id);
        log.info("Deleted recommendation: {}", id);
    }

    private RecommendationDto convertToDto(FocusRecommendations recommendation) {
        RecommendationDto dto = new RecommendationDto();
        dto.setId(recommendation.getId());
        dto.setFocusLogId(recommendation.getFocusLog().getId());
        dto.setAccountId(recommendation.getAccountId());
        dto.setActionType(recommendation.getActionType());
        dto.setCurrencyCode(recommendation.getCurrencyCode());
        dto.setCurrentResourceDetails(recommendation.getCurrentResourceDetails());
        dto.setCurrentResourceSummary(recommendation.getCurrentResourceSummary());
        dto.setCurrentResourceType(recommendation.getCurrentResourceType());
        dto.setEstimatedMonthlyCostAfterDiscount(recommendation.getEstimatedMonthlyCostAfterDiscount());
        dto.setEstimatedMonthlyCostBeforeDiscount(recommendation.getEstimatedMonthlyCostBeforeDiscount());
        dto.setEstimatedMonthlySavingsAfterDiscount(recommendation.getEstimatedMonthlySavingsAfterDiscount());
        dto.setEstimatedSavingsPercentageAfterDiscount(recommendation.getEstimatedSavingsPercentageAfterDiscount());
        dto.setEstimatedSavingsPercentageBeforeDiscount(recommendation.getEstimatedSavingsPercentageBeforeDiscount());
        dto.setImplementationEffort(recommendation.getImplementationEffort());
        dto.setLastRefreshTimestamp(recommendation.getLastRefreshTimestamp());
        dto.setRecommendationId(recommendation.getRecommendationId());
        dto.setRecommendationLookbackPeriodInDays(recommendation.getRecommendationLookbackPeriodInDays());
        dto.setRecommendationSource(recommendation.getRecommendationSource());
        dto.setRecommendedResourceDetails(recommendation.getRecommendedResourceDetails());
        dto.setRecommendedResourceSummary(recommendation.getRecommendedResourceSummary());
        dto.setRecommendedResourceType(recommendation.getRecommendedResourceType());
        dto.setRegion(recommendation.getRegion());
        dto.setResourceArn(recommendation.getResourceArn());
        dto.setRestartNeeded(recommendation.getRestartNeeded());
        dto.setRollbackPossible(recommendation.getRollbackPossible());
        dto.setTags(recommendation.getTags());
        dto.setCreatedAt(recommendation.getCreatedAt());
        dto.setUpdatedAt(recommendation.getUpdatedAt());
        dto.setCreatedBy(recommendation.getCreatedBy() != null ? recommendation.getCreatedBy().getId() : null);
        dto.setUpdatedBy(recommendation.getUpdatedBy() != null ? recommendation.getUpdatedBy().getId() : null);
        return dto;
    }

    private SavingsRecommendationDto convertToSavingsDto(FocusRecommendations recommendation) {
        SavingsRecommendationDto dto = new SavingsRecommendationDto();
        dto.setId(recommendation.getId());
        dto.setRecommendationId(recommendation.getRecommendationId());
        dto.setActionType(recommendation.getActionType());
        dto.setResourceArn(recommendation.getResourceArn());
        dto.setRegion(recommendation.getRegion());
        dto.setCurrencyCode(recommendation.getCurrencyCode());
        dto.setCurrentMonthlyCost(recommendation.getEstimatedMonthlyCostBeforeDiscount());
        dto.setRecommendedMonthlyCost(recommendation.getEstimatedMonthlyCostAfterDiscount());
        dto.setMonthlySavings(recommendation.getEstimatedMonthlySavingsAfterDiscount());
        dto.setSavingsPercentage(recommendation.getEstimatedSavingsPercentageAfterDiscount());
        dto.setAnnualSavings(recommendation.getEstimatedMonthlySavingsAfterDiscount() != null ? 
            recommendation.getEstimatedMonthlySavingsAfterDiscount() * 12 : 0.0);
        dto.setImplementationEffort(recommendation.getImplementationEffort());
        dto.setRestartNeeded(recommendation.getRestartNeeded());
        dto.setRollbackPossible(recommendation.getRollbackPossible());
        dto.setRecommendationSource(recommendation.getRecommendationSource());
        dto.setCurrentResourceType(recommendation.getCurrentResourceType());
        dto.setRecommendedResourceType(recommendation.getRecommendedResourceType());
        dto.setCurrentResourceSummary(recommendation.getCurrentResourceSummary());
        dto.setRecommendedResourceSummary(recommendation.getRecommendedResourceSummary());
        return dto;
    }

    // Filter helper methods
    private boolean filterByFocusLog(FocusRecommendations rec, UUID focusLogId) {
        return focusLogId == null || rec.getFocusLog().getId().equals(focusLogId);
    }

    private boolean filterByActionTypes(FocusRecommendations rec, List<String> actionTypes) {
        return actionTypes == null || actionTypes.isEmpty() || actionTypes.contains(rec.getActionType());
    }

    private boolean filterByImplementationEfforts(FocusRecommendations rec, List<String> efforts) {
        return efforts == null || efforts.isEmpty() || efforts.contains(rec.getImplementationEffort());
    }

    private boolean filterByRegions(FocusRecommendations rec, List<String> regions) {
        return regions == null || regions.isEmpty() || regions.contains(rec.getRegion());
    }

    private boolean filterByRecommendationSource(FocusRecommendations rec, String source) {
        return source == null || source.equals(rec.getRecommendationSource());
    }

    private boolean filterByMinSavings(FocusRecommendations rec, BigDecimal minSavings) {
        return minSavings == null || (rec.getEstimatedMonthlySavingsAfterDiscount() != null && 
                rec.getEstimatedMonthlySavingsAfterDiscount() >= minSavings.doubleValue());
    }

    private boolean filterByMaxSavings(FocusRecommendations rec, BigDecimal maxSavings) {
        return maxSavings == null || (rec.getEstimatedMonthlySavingsAfterDiscount() != null && 
                rec.getEstimatedMonthlySavingsAfterDiscount() <= maxSavings.doubleValue());
    }

    private boolean filterByMinSavingsPercentage(FocusRecommendations rec, BigDecimal minPercentage) {
        return minPercentage == null || (rec.getEstimatedSavingsPercentageAfterDiscount() != null && 
                rec.getEstimatedSavingsPercentageAfterDiscount() >= minPercentage.doubleValue());
    }

    private boolean filterByRestartNeeded(FocusRecommendations rec, Boolean restartNeeded) {
        return restartNeeded == null || restartNeeded.equals(rec.getRestartNeeded());
    }

    private boolean filterByRollbackPossible(FocusRecommendations rec, Boolean rollbackPossible) {
        return rollbackPossible == null || rollbackPossible.equals(rec.getRollbackPossible());
    }
}