package com.example.poc_finops.anomalydetection.service;

import com.example.poc_finops.anomalydetection.api.dto.AnomalyAlertDto;
import com.example.poc_finops.anomalydetection.api.dto.AlertThresholdDto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface AlertService {
    
    List<AnomalyAlertDto> getAllAlerts();
    
    AnomalyAlertDto getAlertById(UUID alertId);
    
    AnomalyAlertDto createAlert(String name, UUID userId);
    
    AnomalyAlertDto updateAlert(UUID alertId, String name, UUID userId);
    
    void deleteAlert(UUID alertId);
    
    void addAlertRecipient(UUID alertId, String email, UUID userId);
    
    void removeAlertRecipient(UUID alertId, String email);
    
    List<String> getAlertRecipients(UUID alertId);
    
    AlertThresholdDto addAlertThreshold(UUID alertId, BigDecimal thresholdAmount1, BigDecimal thresholdAmount2, 
                                       String thresholdType1, String thresholdType2, String relation, UUID userId);
    
    AlertThresholdDto updateAlertThreshold(UUID thresholdId, BigDecimal thresholdAmount1, BigDecimal thresholdAmount2,
                                          String thresholdType1, String thresholdType2, String relation, UUID userId);
    
    void deleteAlertThreshold(UUID thresholdId);
    
    List<AlertThresholdDto> getAlertThresholds(UUID alertId);
}