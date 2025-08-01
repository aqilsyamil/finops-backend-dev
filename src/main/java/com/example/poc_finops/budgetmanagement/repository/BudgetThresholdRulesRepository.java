package com.example.poc_finops.budgetmanagement.repository;

import com.example.poc_finops.budgetmanagement.domain.entity.BudgetThresholdRules;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BudgetThresholdRulesRepository extends JpaRepository<BudgetThresholdRules, UUID> {
    
    List<BudgetThresholdRules> findByBudgetId(UUID budgetId);
    
    List<BudgetThresholdRules> findByTriggerType(String triggerType);
    
    List<BudgetThresholdRules> findByBudgetIdAndTriggerType(UUID budgetId, String triggerType);
}