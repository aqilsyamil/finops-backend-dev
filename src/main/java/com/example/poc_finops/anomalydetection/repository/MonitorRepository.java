package com.example.poc_finops.anomalydetection.repository;

import com.example.poc_finops.anomalydetection.domain.entity.Monitor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MonitorRepository extends JpaRepository<Monitor, UUID> {
    
    // Existing methods
    
    List<Monitor> findByCspConnectionId(UUID cspConnectionId);
    
    List<Monitor> findByAlertId(UUID alertId);
    
    List<Monitor> findByName(String name);
    
    
    // New methods for revised APIs
    
    /**
     * Find all monitors with their alerts (excluding soft deleted)
     */
    @Query("SELECT m FROM Monitor m LEFT JOIN FETCH m.alert WHERE m.deletedAt IS NULL ORDER BY m.createdAt DESC")
    List<Monitor> findAllWithAlerts();
    
    /**
     * Get anomaly count for a specific monitor
     */
    @Query("SELECT COUNT(d) FROM FocusAnomalyDetection d WHERE d.anomalyMonitor.id IN " +
           "(SELECT am.id FROM FocusAnomalyMonitors am WHERE am.name = :monitorName) " +
           "AND d.deletedAt IS NULL")
    Long countAnomaliesByMonitorName(@Param("monitorName") String monitorName);
    
    /**
     * Get active anomaly count for a specific monitor (last 7 days)
     */
    @Query("SELECT COUNT(d) FROM FocusAnomalyDetection d WHERE d.anomalyMonitor.id IN " +
           "(SELECT am.id FROM FocusAnomalyMonitors am WHERE am.name = :monitorName) " +
           "AND d.deletedAt IS NULL AND d.lastDetectedDate >= :sevenDaysAgo")
    Long countActiveAnomaliesByMonitorName(@Param("monitorName") String monitorName, @Param("sevenDaysAgo") java.time.LocalDate sevenDaysAgo);
}