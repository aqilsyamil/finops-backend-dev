package com.example.poc_finops.anomalydetection.repository;

import com.example.poc_finops.anomalydetection.domain.entity.FocusAnomalyDetection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface FocusAnomalyDetectionRepository extends JpaRepository<FocusAnomalyDetection, UUID> {
    
    // Existing methods
    List<FocusAnomalyDetection> findByAnomalyMonitorId(UUID anomalyMonitorId);
    
    
    List<FocusAnomalyDetection> findByLinkedAccountId(String linkedAccountId);
    
    @Query("SELECT d FROM FocusAnomalyDetection d WHERE d.lastDetectedDate >= :startDate AND d.lastDetectedDate <= :endDate")
    List<FocusAnomalyDetection> findByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    
    @Query("SELECT d FROM FocusAnomalyDetection d WHERE d.actualSpend > d.expectedSpend * :thresholdMultiplier")
    List<FocusAnomalyDetection> findAnomaliesExceedingThreshold(@Param("thresholdMultiplier") Double thresholdMultiplier);
    
    // New methods for revised APIs
    
    /**
     * Find detection history by monitor name (case-insensitive partial match)
     */
    @Query("SELECT d FROM FocusAnomalyDetection d WHERE LOWER(d.anomalyMonitor.name) LIKE LOWER(CONCAT('%', :name, '%')) AND d.deletedAt IS NULL ORDER BY d.lastDetectedDate DESC")
    List<FocusAnomalyDetection> findByMonitorNameContainingIgnoreCase(@Param("name") String name);
    
    /**
     * Get all detection history ordered by last detected date
     */
    @Query("SELECT d FROM FocusAnomalyDetection d WHERE d.deletedAt IS NULL ORDER BY d.lastDetectedDate DESC")
    List<FocusAnomalyDetection> findAllDetectionHistoryOrderByDate();
    
    /**
     * Get service summaries with anomaly statistics - modified to use linkedAccountName as grouping
     */
    @Query("SELECT d.linkedAccountName as serviceName, " +
           "COUNT(d) as anomalyCount, " +
           "SUM(d.actualSpend - d.expectedSpend) as totalCostImpact, " +
           "AVG(d.actualSpend - d.expectedSpend) as averageCostImpact, " +
           "SUM(d.expectedSpend) as totalExpectedSpend, " +
           "SUM(d.actualSpend) as totalActualSpend " +
           "FROM FocusAnomalyDetection d " +
           "WHERE d.deletedAt IS NULL " +
           "GROUP BY d.linkedAccountName " +
           "ORDER BY COUNT(d) DESC")
    List<Object[]> findServiceSummaries();
    
    /**
     * Get distinct linked account names (used as service grouping)
     */
    @Query("SELECT DISTINCT d.linkedAccountName FROM FocusAnomalyDetection d WHERE d.deletedAt IS NULL AND d.linkedAccountName IS NOT NULL ORDER BY d.linkedAccountName")
    List<String> findDistinctServices();
}