package com.example.poc_finops.costanalytics.repository;

import com.example.poc_finops.costanalytics.domain.entity.FocusUsage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface FocusUsageRepository extends JpaRepository<FocusUsage, UUID> {
    
    List<FocusUsage> findByFocusLogId(UUID focusLogId);
    
    @Query("SELECT fu FROM FocusUsage fu WHERE fu.focusLog.organization.id = :organizationId")
    List<FocusUsage> findByOrganizationId(@Param("organizationId") UUID organizationId);
    
    @Query("SELECT fu FROM FocusUsage fu WHERE fu.focusLog.organization.id = :organizationId AND fu.focusLog.logDate BETWEEN :startDate AND :endDate")
    List<FocusUsage> findByOrganizationIdAndDateRange(@Param("organizationId") UUID organizationId, 
                                                      @Param("startDate") LocalDate startDate, 
                                                      @Param("endDate") LocalDate endDate);
    
    List<FocusUsage> findByServiceName(String serviceName);
    
    List<FocusUsage> findByChargeCategory(String chargeCategory);
    
    List<FocusUsage> findByRegionName(String regionName);
    
    List<FocusUsage> findByResourceType(String resourceType);
    
    @Query("SELECT SUM(fu.billedCost) FROM FocusUsage fu WHERE fu.focusLog.organization.id = :organizationId AND fu.focusLog.logDate BETWEEN :startDate AND :endDate")
    BigDecimal sumBilledCostByOrganizationAndDateRange(@Param("organizationId") UUID organizationId, 
                                                       @Param("startDate") LocalDate startDate, 
                                                       @Param("endDate") LocalDate endDate);
    
    @Query("SELECT SUM(fu.effectiveCost) FROM FocusUsage fu WHERE fu.focusLog.organization.id = :organizationId AND fu.focusLog.logDate BETWEEN :startDate AND :endDate")
    BigDecimal sumEffectiveCostByOrganizationAndDateRange(@Param("organizationId") UUID organizationId, 
                                                          @Param("startDate") LocalDate startDate, 
                                                          @Param("endDate") LocalDate endDate);
    
    @Query("SELECT fu.serviceName, SUM(fu.billedCost) FROM FocusUsage fu WHERE fu.focusLog.organization.id = :organizationId AND fu.focusLog.logDate BETWEEN :startDate AND :endDate GROUP BY fu.serviceName ORDER BY SUM(fu.billedCost) DESC")
    List<Object[]> getCostByServiceForOrganizationAndDateRange(@Param("organizationId") UUID organizationId, 
                                                               @Param("startDate") LocalDate startDate, 
                                                               @Param("endDate") LocalDate endDate);
    
    @Query("SELECT fu.regionName, SUM(fu.billedCost) FROM FocusUsage fu WHERE fu.focusLog.organization.id = :organizationId AND fu.focusLog.logDate BETWEEN :startDate AND :endDate GROUP BY fu.regionName ORDER BY SUM(fu.billedCost) DESC")
    List<Object[]> getCostByRegionForOrganizationAndDateRange(@Param("organizationId") UUID organizationId, 
                                                              @Param("startDate") LocalDate startDate, 
                                                              @Param("endDate") LocalDate endDate);
    
    @Query("SELECT fu FROM FocusUsage fu WHERE fu.focusLog.organization.id = :organizationId AND fu.focusLog.logDate BETWEEN :startDate AND :endDate ORDER BY fu.billedCost DESC")
    List<FocusUsage> findTopCostResourcesByOrganizationAndDateRange(@Param("organizationId") UUID organizationId, 
                                                                    @Param("startDate") LocalDate startDate, 
                                                                    @Param("endDate") LocalDate endDate);
    
    @Query("SELECT fu.chargeCategory, SUM(fu.billedCost) FROM FocusUsage fu WHERE fu.focusLog.organization.id = :organizationId AND fu.focusLog.logDate BETWEEN :startDate AND :endDate GROUP BY fu.chargeCategory ORDER BY SUM(fu.billedCost) DESC")
    List<Object[]> getCostByChargeCategoryForOrganizationAndDateRange(@Param("organizationId") UUID organizationId, 
                                                                      @Param("startDate") LocalDate startDate, 
                                                                      @Param("endDate") LocalDate endDate);
}