package com.example.poc_finops.recommendations.service;

import com.example.poc_finops.recommendations.api.dto.RecommendationDto;
import com.example.poc_finops.recommendations.api.dto.RecommendationFilterRequest;
import com.example.poc_finops.recommendations.api.dto.SavingsRecommendationDto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface RecommendationService {
    List<RecommendationDto> getAllRecommendations();
    RecommendationDto getRecommendationById(UUID id);
    RecommendationDto getRecommendationByRecommendationId(String recommendationId);
    

    List<RecommendationDto> getRecommendationsByConnectionId(UUID connectionId);
    
    List<RecommendationDto> filterRecommendations(RecommendationFilterRequest filter);
        
    void deleteRecommendation(UUID id);
}