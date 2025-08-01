package com.example.poc_finops.anomalydetection.repository;

import com.example.poc_finops.anomalydetection.domain.entity.Alert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AlertRepository extends JpaRepository<Alert, UUID> {
    
    Optional<Alert> findByName(String name);
    
    List<Alert> findByNameContainingIgnoreCase(String name);
    
    @Query("SELECT a FROM Alert a WHERE a.createdBy.id = :userId")
    List<Alert> findByCreatedBy(@Param("userId") UUID userId);
}