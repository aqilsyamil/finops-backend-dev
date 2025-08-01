package com.example.poc_finops.anomalydetection.repository;

import com.example.poc_finops.anomalydetection.domain.entity.AlertThreshold;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AlertThresholdRepository extends JpaRepository<AlertThreshold, UUID> {
    
    List<AlertThreshold> findByAlertId(UUID alertId);
    
    List<AlertThreshold> findByThresholdType1(String thresholdType1);
    
    void deleteByAlertId(UUID alertId);
}