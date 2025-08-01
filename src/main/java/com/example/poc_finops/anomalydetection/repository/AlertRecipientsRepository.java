package com.example.poc_finops.anomalydetection.repository;

import com.example.poc_finops.anomalydetection.domain.entity.AlertRecipients;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AlertRecipientsRepository extends JpaRepository<AlertRecipients, UUID> {
    
    List<AlertRecipients> findByAlertId(UUID alertId);
    
    List<AlertRecipients> findByEmail(String email);
    
    void deleteByAlertId(UUID alertId);
}