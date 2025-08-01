package com.example.poc_finops.anomalydetection.repository;

import com.example.poc_finops.anomalydetection.domain.entity.FocusAnomalyMonitors;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FocusAnomalyMonitorsRepository extends JpaRepository<FocusAnomalyMonitors, UUID> {
    
    List<FocusAnomalyMonitors> findByName(String name);
    
    List<FocusAnomalyMonitors> findByType(String type);
    
    Optional<FocusAnomalyMonitors> findByServiceId(UUID serviceId);
    
    List<FocusAnomalyMonitors> findByService(com.example.poc_finops.cloudconnections.domain.entity.Services service);
    
    @Query("SELECT m FROM FocusAnomalyMonitors m WHERE m.name = :name AND m.type = :type")
    List<FocusAnomalyMonitors> findByNameAndType(@Param("name") String name, @Param("type") String type);
    
    @Query("SELECT m FROM FocusAnomalyMonitors m WHERE m.monitoredDimensions LIKE %:dimension%")
    List<FocusAnomalyMonitors> findByMonitoredDimension(@Param("dimension") String dimension);
}