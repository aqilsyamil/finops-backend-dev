package com.example.poc_finops.budgetmanagement.repository;

import com.example.poc_finops.budgetmanagement.domain.entity.BudgetAlertsRecipients;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BudgetAlertsRecipientsRepository extends JpaRepository<BudgetAlertsRecipients, UUID> {
    
    List<BudgetAlertsRecipients> findByBudgetId(UUID budgetId);
    
    List<BudgetAlertsRecipients> findByEmail(String email);
    
    List<BudgetAlertsRecipients> findByBudgetIdAndEmail(UUID budgetId, String email);
}