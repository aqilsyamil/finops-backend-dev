package com.example.poc_finops.budgetmanagement.repository;

import com.example.poc_finops.budgetmanagement.domain.entity.Budgets;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BudgetsRepository extends JpaRepository<Budgets, UUID> {
    
    List<Budgets> findByOrganizationId(UUID organizationId);
    
    List<Budgets> findByCspConnectionId(UUID cspConnectionId);
    
    @Query("SELECT b FROM Budgets b WHERE b.organization.id = :organizationId AND b.cspConnection.id = :cspConnectionId")
    List<Budgets> findByOrganizationIdAndCspConnectionId(@Param("organizationId") UUID organizationId, 
                                                         @Param("cspConnectionId") UUID cspConnectionId);
    
    List<Budgets> findByTimeRange(String timeRange);
    
    List<Budgets> findByBudgetType(String budgetType);
    
    @Query("SELECT b FROM Budgets b WHERE b.organization.id = :organizationId AND b.timeRange = :timeRange")
    List<Budgets> findByOrganizationIdAndTimeRange(@Param("organizationId") UUID organizationId, 
                                                   @Param("timeRange") String timeRange);
}