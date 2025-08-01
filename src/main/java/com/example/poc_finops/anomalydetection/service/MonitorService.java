package com.example.poc_finops.anomalydetection.service;

import com.example.poc_finops.anomalydetection.api.dto.AnomalyDto;

import java.util.List;
import java.util.UUID;

public interface MonitorService {
    
    AnomalyDto createMonitorConfiguration(UUID cspId, UUID cspConnectionId, String name, UUID alertId, UUID userId);
    
    AnomalyDto updateMonitorConfiguration(UUID monitorId, String name, UUID alertId, UUID userId);
    
    void deleteMonitorConfiguration(UUID monitorId);
    
    List<AnomalyDto> getMonitorsByCsp(UUID cspId);
    
    List<AnomalyDto> getMonitorsByCspConnection(UUID cspConnectionId);
    
    List<AnomalyDto> getMonitorsByAlert(UUID alertId);
    
    AnomalyDto getMonitorById(UUID monitorId);
}