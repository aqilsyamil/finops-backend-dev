package com.example.poc_finops.cloudconnections.service.impl;

import com.example.poc_finops.cloudconnections.api.dto.CreateConnectionRequest;
import com.example.poc_finops.cloudconnections.api.dto.CspConnectionDto;
import com.example.poc_finops.cloudconnections.domain.entity.CspConnection;
import com.example.poc_finops.cloudconnections.domain.entity.DataSource;
import com.example.poc_finops.cloudconnections.domain.entity.PlanType;
import com.example.poc_finops.cloudconnections.domain.entity.Region;
import com.example.poc_finops.cloudconnections.domain.event.ConnectionEstablishedEvent;
import com.example.poc_finops.cloudconnections.domain.event.ConnectionFailedEvent;
import com.example.poc_finops.cloudconnections.repository.CspConnectionRepository;
import com.example.poc_finops.cloudconnections.repository.DataSourceRepository;
import com.example.poc_finops.cloudconnections.repository.PlanTypeRepository;
import com.example.poc_finops.cloudconnections.repository.RegionRepository;
import com.example.poc_finops.cloudconnections.service.AwsCredentialValidationService;
import com.example.poc_finops.cloudconnections.service.ConnectionService;
import com.example.poc_finops.cloudconnections.service.CspService;
import com.example.poc_finops.organization.service.OrganizationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ConnectionServiceImpl implements ConnectionService {

    private final CspConnectionRepository connectionRepository;
    private final CspService cspService;
    private final OrganizationService organizationService;
    private final DataSourceRepository dataSourceRepository;
    private final PlanTypeRepository planTypeRepository;
    private final RegionRepository regionRepository;
    private final AwsCredentialValidationService awsCredentialValidationService;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional(readOnly = true)
    public List<CspConnectionDto> getAllConnections() {
        return connectionRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CspConnectionDto> getConnectionsByOrganization(UUID organizationId) {
        return connectionRepository.findByOrganizationId(organizationId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CspConnectionDto getConnectionById(UUID id) {
        CspConnection connection = connectionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Connection not found with id: " + id));
        return convertToDto(connection);
    }

    @Override
    @Transactional(readOnly = true)
    public CspConnection getConnectionEntityById(UUID id) {
        return connectionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Connection not found with id: " + id));
    }

    @Override
    public CspConnectionDto createConnection(CreateConnectionRequest request) {
        CspConnection connection = new CspConnection();
        connection.setOrganization(organizationService.getOrganizationEntityById(request.getOrganizationId()));
        connection.setCsp(cspService.getCspById(request.getCspId()));
        connection.setDataSource(getDataSourceById(request.getDataSourceId()));
        connection.setName(request.getName());
        connection.setDescription(request.getDescription());
        connection.setPlanType(getPlanTypeById(request.getPlanTypeId()));
        connection.setAccessKeyId(request.getAccessKeyId());
        connection.setSecretKeyId(request.getSecretKeyId());
        connection.setRegion(getRegionById(request.getRegionId()));

        // Save connection first
        CspConnection savedConnection = connectionRepository.save(connection);
        log.info("Created connection: {} for organization: {}", request.getName(), request.getOrganizationId());
        
        // Validate AWS credentials if this is an AWS connection
        if (isAwsConnection(savedConnection)) {
            validateAwsConnection(savedConnection);
        }

        return convertToDto(savedConnection);
    }

    @Override
    public CspConnectionDto updateConnection(UUID id, CreateConnectionRequest request) {
        CspConnection connection = connectionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Connection not found with id: " + id));

        connection.setName(request.getName());
        connection.setDescription(request.getDescription());
        connection.setAccessKeyId(request.getAccessKeyId());
        connection.setSecretKeyId(request.getSecretKeyId());
        if (request.getDataSourceId() != null) {
            connection.setDataSource(getDataSourceById(request.getDataSourceId()));
        }
        if (request.getPlanTypeId() != null) {
            connection.setPlanType(getPlanTypeById(request.getPlanTypeId()));
        }
        if (request.getRegionId() != null) {
            connection.setRegion(getRegionById(request.getRegionId()));
        }

        CspConnection updatedConnection = connectionRepository.save(connection);
        log.info("Updated connection: {}", id);
        return convertToDto(updatedConnection);
    }

    @Override
    public void deleteConnection(UUID id) {
        connectionRepository.deleteById(id);
        log.info("Deleted connection: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CspConnectionDto> getConnectionsByCsp(UUID cspId) {
        return connectionRepository.findByCspId(cspId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CspConnectionDto> getConnectionsByDataSource(UUID dataSourceId) {
        return connectionRepository.findByDataSourceId(dataSourceId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private DataSource getDataSourceById(UUID id) {
        return dataSourceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("DataSource not found with id: " + id));
    }

    private PlanType getPlanTypeById(UUID id) {
        return planTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("PlanType not found with id: " + id));
    }

    private Region getRegionById(UUID id) {
        return regionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Region not found with id: " + id));
    }

    private CspConnectionDto convertToDto(CspConnection connection) {
        CspConnectionDto dto = new CspConnectionDto();
        dto.setId(connection.getId());
        dto.setOrganizationId(connection.getOrganization().getId());
        dto.setOrganizationName(connection.getOrganization().getOrganizationName());
        dto.setCspId(connection.getCsp().getId());
        dto.setCspName(connection.getCsp().getName());
        dto.setDataSourceId(connection.getDataSource().getId());
        dto.setDataSourceName(connection.getDataSource().getName());
        dto.setName(connection.getName());
        dto.setDescription(connection.getDescription());
        dto.setPlanTypeId(connection.getPlanType().getId());
        dto.setPlanTypeName(connection.getPlanType().getName());
        dto.setAccessKeyId(connection.getAccessKeyId());
        dto.setSecretKeyId(connection.getSecretKeyId());
        dto.setRegionId(connection.getRegion().getId());
        dto.setRegionName(connection.getRegion().getName());
        dto.setCreatedAt(connection.getCreatedAt());
        dto.setUpdatedAt(connection.getUpdatedAt());
        dto.setCreatedBy(connection.getCreatedBy());
        dto.setUpdatedBy(connection.getUpdatedBy());
        return dto;
    }
    
    public CspConnectionDto testConnection(UUID connectionId) {
        CspConnection connection = getConnectionEntityById(connectionId);
        
        if (isAwsConnection(connection)) {
            validateAwsConnection(connection);
        } else {
            log.warn("Connection testing not supported for CSP: {}", connection.getCsp().getName());
        }
        
        return convertToDto(connection);
    }
    
    private boolean isAwsConnection(CspConnection connection) {
        return "AWS".equalsIgnoreCase(connection.getCsp().getName());
    }
    
    private void validateAwsConnection(CspConnection connection) {
        try {
            log.info("Validating AWS connection: {}", connection.getName());
            
            AwsCredentialValidationService.ValidationResult result = awsCredentialValidationService
                    .validateCredentials(
                            connection.getAccessKeyId(),
                            connection.getSecretKeyId(),
                            connection.getRegion().getName()
                    );
            
            if (result.isSuccess()) {
                
                // Publish success event
                ConnectionEstablishedEvent event = new ConnectionEstablishedEvent(
                        connection.getId(),
                        connection.getName(),
                        connection.getOrganization().getId(),
                        connection.getCsp().getId(),
                        connection.getCsp().getName(),
                        OffsetDateTime.now(),
                        connection.getCreatedBy()
                );
                eventPublisher.publishEvent(event);
                
                log.info("AWS connection validated successfully: {}", connection.getName());
            } else {
                log.error("AWS connection validation failed for {}: {}", connection.getName(), result.getErrorMessage());
                
                // Publish failure event
                ConnectionFailedEvent event = new ConnectionFailedEvent(
                        connection.getId(),
                        connection.getName(),
                        connection.getOrganization().getId(),
                        connection.getCsp().getId(),
                        connection.getCsp().getName(),
                        result.getErrorMessage(),
                        OffsetDateTime.now(),
                        connection.getCreatedBy()
                );
                eventPublisher.publishEvent(event);
            }
            
            connectionRepository.save(connection);
            
        } catch (Exception e) {
            log.error("Unexpected error during AWS connection validation for {}", connection.getName(), e);
            
            // Publish failure event
            ConnectionFailedEvent event = new ConnectionFailedEvent(
                    connection.getId(),
                    connection.getName(),
                    connection.getOrganization().getId(),
                    connection.getCsp().getId(),
                    connection.getCsp().getName(),
                    "Unexpected error: " + e.getMessage(),
                    OffsetDateTime.now(),
                    connection.getCreatedBy()
            );
            eventPublisher.publishEvent(event);
        }
    }
}