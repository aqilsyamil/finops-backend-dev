package com.example.poc_finops.recommendations.repository;

import com.example.poc_finops.recommendations.domain.entity.FocusRecommendations;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FocusRecommendationsRepository extends JpaRepository<FocusRecommendations, UUID> {
    
    List<FocusRecommendations> findByFocusLogId(UUID focusLogId);
    
    Optional<FocusRecommendations> findByRecommendationId(String recommendationId);
    
    List<FocusRecommendations> findByActionType(String actionType);
    
    List<FocusRecommendations> findByImplementationEffort(String implementationEffort);
    
    List<FocusRecommendations> findByRegion(String region);
    
    List<FocusRecommendations> findByRecommendationSource(String recommendationSource);
    
    @Query("SELECT fr FROM FocusRecommendations fr WHERE fr.focusLog.organization.id = :organizationId")
    List<FocusRecommendations> findByOrganizationId(@Param("organizationId") UUID organizationId);
    
    @Query("SELECT fr FROM FocusRecommendations fr WHERE fr.estimatedMonthlySavingsAfterDiscount >= :minSavings")
    List<FocusRecommendations> findByMinimumSavings(@Param("minSavings") BigDecimal minSavings);
    
    @Query("SELECT fr FROM FocusRecommendations fr WHERE fr.estimatedSavingsPercentageAfterDiscount >= :minPercentage")
    List<FocusRecommendations> findByMinimumSavingsPercentage(@Param("minPercentage") BigDecimal minPercentage);
    
    @Query("SELECT fr FROM FocusRecommendations fr WHERE fr.restartNeeded = :restartNeeded")
    List<FocusRecommendations> findByRestartNeeded(@Param("restartNeeded") Boolean restartNeeded);
    
    @Query("SELECT fr FROM FocusRecommendations fr WHERE fr.rollbackPossible = :rollbackPossible")
    List<FocusRecommendations> findByRollbackPossible(@Param("rollbackPossible") Boolean rollbackPossible);
    
    @Query("SELECT fr FROM FocusRecommendations fr WHERE fr.currentResourceType = :resourceType OR fr.recommendedResourceType = :resourceType")
    List<FocusRecommendations> findByResourceType(@Param("resourceType") String resourceType);
    
    @Query("SELECT fr FROM FocusRecommendations fr WHERE fr.focusLog.id = :focusLogId AND fr.actionType = :actionType")
    List<FocusRecommendations> findByFocusLogIdAndActionType(@Param("focusLogId") UUID focusLogId, 
                                                             @Param("actionType") String actionType);
    
    @Query("SELECT fr FROM FocusRecommendations fr WHERE fr.focusLog.organization.id = :organizationId ORDER BY fr.estimatedMonthlySavingsAfterDiscount DESC")
    List<FocusRecommendations> findTopSavingsRecommendationsByOrganization(@Param("organizationId") UUID organizationId);
    
    @Query("SELECT SUM(fr.estimatedMonthlySavingsAfterDiscount) FROM FocusRecommendations fr WHERE fr.focusLog.organization.id = :organizationId")
    BigDecimal sumPotentialSavingsByOrganization(@Param("organizationId") UUID organizationId);
    
    @Query("SELECT COUNT(fr) FROM FocusRecommendations fr WHERE fr.focusLog.organization.id = :organizationId")
    Long countRecommendationsByOrganization(@Param("organizationId") UUID organizationId);
    
    @Query("SELECT fr FROM FocusRecommendations fr WHERE fr.focusLog.cspConnection.id = :connectionId")
    List<FocusRecommendations> findByCspConnectionId(@Param("connectionId") UUID connectionId);
}