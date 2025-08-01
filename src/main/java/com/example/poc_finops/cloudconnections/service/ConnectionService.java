package com.example.poc_finops.cloudconnections.service;

import com.example.poc_finops.cloudconnections.api.dto.CreateConnectionRequest;
import com.example.poc_finops.cloudconnections.api.dto.CspConnectionDto;
import com.example.poc_finops.cloudconnections.domain.entity.CspConnection;

import java.util.List;
import java.util.UUID;

public interface ConnectionService {
    List<CspConnectionDto> getAllConnections();
    List<CspConnectionDto> getConnectionsByOrganization(UUID organizationId);
    CspConnectionDto getConnectionById(UUID id);
    CspConnection getConnectionEntityById(UUID id);
    CspConnectionDto createConnection(CreateConnectionRequest request);
    CspConnectionDto updateConnection(UUID id, CreateConnectionRequest request);
    void deleteConnection(UUID id);
    List<CspConnectionDto> getConnectionsByCsp(UUID cspId);
    List<CspConnectionDto> getConnectionsByDataSource(UUID dataSourceId);
    CspConnectionDto testConnection(UUID connectionId);
}