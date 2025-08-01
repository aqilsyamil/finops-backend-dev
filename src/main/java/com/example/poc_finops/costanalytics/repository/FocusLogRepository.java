package com.example.poc_finops.costanalytics.repository;

import com.example.poc_finops.costanalytics.domain.entity.FocusLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FocusLogRepository extends JpaRepository<FocusLog, UUID> {
    
    List<FocusLog> findByOrganizationId(UUID organizationId);
    
    List<FocusLog> findByCspConnectionId(UUID cspConnectionId);
    
    List<FocusLog> findByOrganizationIdAndCspConnectionId(UUID organizationId, UUID cspConnectionId);
    
    List<FocusLog> findByLogDateBetween(LocalDate startDate, LocalDate endDate);
    
    List<FocusLog> findByOrganizationIdAndLogDateBetween(UUID organizationId, LocalDate startDate, LocalDate endDate);
    
    List<FocusLog> findByStatus(Boolean status);
    
    Optional<FocusLog> findByOrganizationIdAndCspConnectionIdAndLogDate(UUID organizationId, UUID cspConnectionId, LocalDate logDate);
    
    @Query("SELECT fl FROM FocusLog fl WHERE fl.organization.id = :organizationId AND fl.logDate = :logDate")
    List<FocusLog> findByOrganizationAndDate(@Param("organizationId") UUID organizationId, @Param("logDate") LocalDate logDate);
    
    @Query("SELECT COUNT(fl) FROM FocusLog fl WHERE fl.organization.id = :organizationId AND fl.status = true")
    Long countSuccessfulLogsByOrganization(@Param("organizationId") UUID organizationId);
}