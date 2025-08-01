package com.example.poc_finops.recommendations.api;

import com.example.poc_finops.recommendations.api.dto.RecommendationDto;
import com.example.poc_finops.recommendations.api.dto.RecommendationFilterRequest;
import com.example.poc_finops.recommendations.api.dto.SavingsRecommendationDto;
import com.example.poc_finops.recommendations.domain.valueobject.SavingsMetrics;
import com.example.poc_finops.recommendations.service.RecommendationService;
import com.example.poc_finops.recommendations.service.SavingsCalculationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/recommendations")
@RequiredArgsConstructor
@Tag(name = "Recommendations Management", description = "APIs for managing cost optimization recommendations")
public class RecommendationController {

    private final RecommendationService recommendationService;
    private final SavingsCalculationService savingsCalculationService;

    @GetMapping
    @Operation(summary = "Get all recommendations", description = "Retrieve a list of all recommendations")
    public ResponseEntity<List<RecommendationDto>> getAllRecommendations() {
        List<RecommendationDto> recommendations = recommendationService.getAllRecommendations();
        return ResponseEntity.ok(recommendations);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get recommendation by ID", description = "Retrieve a recommendation by its ID")
    public ResponseEntity<RecommendationDto> getRecommendationById(@PathVariable UUID id) {
        RecommendationDto recommendation = recommendationService.getRecommendationById(id);
        return ResponseEntity.ok(recommendation);
    }

    @GetMapping("/recommendation-id/{recommendationId}")
    @Operation(summary = "Get recommendation by recommendation ID", description = "Retrieve a recommendation by its recommendation ID")
    public ResponseEntity<RecommendationDto> getRecommendationByRecommendationId(@PathVariable String recommendationId) {
        RecommendationDto recommendation = recommendationService.getRecommendationByRecommendationId(recommendationId);
        return ResponseEntity.ok(recommendation);
    }


    @GetMapping("/connection/{connectionId}")
    @Operation(summary = "Get recommendations by connection", description = "Retrieve recommendations for a specific CSP connection")
    public ResponseEntity<List<RecommendationDto>> getRecommendationsByConnectionId(@PathVariable UUID connectionId) {
        List<RecommendationDto> recommendations = recommendationService.getRecommendationsByConnectionId(connectionId);
        return ResponseEntity.ok(recommendations);
    }

    @PostMapping("/filter")
    @Operation(summary = "Filter recommendations", description = "Filter recommendations based on various criteria")
    public ResponseEntity<List<RecommendationDto>> filterRecommendations(@RequestBody RecommendationFilterRequest filter) {
        List<RecommendationDto> recommendations = recommendationService.filterRecommendations(filter);
        return ResponseEntity.ok(recommendations);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete recommendation", description = "Delete a recommendation by its ID")
    public ResponseEntity<Void> deleteRecommendation(@PathVariable UUID id) {
        recommendationService.deleteRecommendation(id);
        return ResponseEntity.noContent().build();
    }
}