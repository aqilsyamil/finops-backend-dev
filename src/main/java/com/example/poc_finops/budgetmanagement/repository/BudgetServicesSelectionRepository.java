package com.example.poc_finops.budgetmanagement.repository;

import com.example.poc_finops.budgetmanagement.domain.entity.BudgetServicesSelection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BudgetServicesSelectionRepository extends JpaRepository<BudgetServicesSelection, UUID> {
    
    List<BudgetServicesSelection> findByBudgetId(UUID budgetId);
    
    List<BudgetServicesSelection> findByServiceId(UUID serviceId);
    
    List<BudgetServicesSelection> findByBudgetIdAndSelected(UUID budgetId, Boolean selected);
    
    @Query("SELECT bss FROM BudgetServicesSelection bss WHERE bss.budget.id = :budgetId AND bss.service.id = :serviceId")
    List<BudgetServicesSelection> findByBudgetIdAndServiceId(@Param("budgetId") UUID budgetId, 
                                                             @Param("serviceId") UUID serviceId);
}