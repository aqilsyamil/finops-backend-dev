package com.example.poc_finops.costanalytics.service;

import com.example.poc_finops.cloudconnections.domain.entity.CspConnection;
import com.example.poc_finops.cloudconnections.domain.entity.DataSource;
import com.example.poc_finops.cloudconnections.domain.entity.Region;
import com.example.poc_finops.cloudconnections.domain.entity.Csp;
import com.example.poc_finops.cloudconnections.domain.entity.DataBase;
import com.example.poc_finops.cloudconnections.domain.entity.DataCatalog;
import com.example.poc_finops.cloudconnections.repository.CspConnectionRepository;
import com.example.poc_finops.cloudconnections.repository.DataBaseRepository;
import com.example.poc_finops.cloudconnections.repository.DataCatalogRepository;
import com.example.poc_finops.costanalytics.repository.FocusLogRepository;
import com.example.poc_finops.costanalytics.repository.FocusUsageRepository;
import com.example.poc_finops.costanalytics.api.dto.AthenaQueryRequest;
import com.example.poc_finops.costanalytics.api.dto.QueryExecutionResponse;
import com.example.poc_finops.costanalytics.config.AthenaConfig;
import com.example.poc_finops.costanalytics.service.impl.AthenaServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("dev") 
class AthenaServiceTest {

    @Mock
    private AthenaConfig athenaConfig;
    
    @Mock
    private CspConnectionRepository cspConnectionRepository;
    
    @Mock
    private DataBaseRepository dataBaseRepository;
    
    @Mock
    private DataCatalogRepository dataCatalogRepository;
    
    @Mock
    private FocusLogRepository focusLogRepository;
    
    @Mock
    private FocusUsageRepository focusUsageRepository;
    
    private AthenaServiceImpl athenaService;
    
    private UUID cspConnectionId;
    private CspConnection mockConnection;
    
    @BeforeEach
    void setUp() {
        athenaService = new AthenaServiceImpl(athenaConfig, cspConnectionRepository, dataBaseRepository, dataCatalogRepository, focusLogRepository, focusUsageRepository);
        cspConnectionId = UUID.randomUUID();
        
        mockConnection = new CspConnection();
        mockConnection.setId(cspConnectionId);
        mockConnection.setAccessKeyId("test-access-key");
        mockConnection.setSecretKeyId("test-secret-key");
        
        Region region = new Region();
        region.setName("us-east-1");
        mockConnection.setRegion(region);
        
        DataSource dataSource = new DataSource();
        dataSource.setOutputBucketUrl("s3://test-bucket/results");
        mockConnection.setDataSource(dataSource);
        
        Csp csp = new Csp();
        csp.setId(UUID.randomUUID());
        mockConnection.setCsp(csp);
    }
    
    @Test
    void testExecuteQuery_ConnectionNotFound() {
        when(cspConnectionRepository.findById(cspConnectionId))
                .thenReturn(Optional.empty());
        
        AthenaQueryRequest request = new AthenaQueryRequest();
        request.setQuery("SELECT * FROM test_table LIMIT 10");
        request.setOrganizationId(UUID.randomUUID());
        
        assertThrows(IllegalArgumentException.class, () -> 
                athenaService.executeQuery(cspConnectionId, request));
    }
    
    @Test
    void testExecuteQuery_MissingDatabase() {
        when(cspConnectionRepository.findById(cspConnectionId))
                .thenReturn(Optional.of(mockConnection));
        when(dataBaseRepository.findByCspConnectionId(cspConnectionId))
                .thenReturn(Collections.emptyList());
        
        AthenaQueryRequest request = new AthenaQueryRequest();
        request.setQuery("SELECT * FROM test_table LIMIT 10");
        request.setOrganizationId(UUID.randomUUID());
        
        // This should return a QueryExecutionResponse with FAILED status due to the catch block
        QueryExecutionResponse response = athenaService.executeQuery(cspConnectionId, request);
        assertEquals("FAILED", response.getStatus());
        assertTrue(response.getMessage().contains("database"));
    }
    
    @Test
    void testExecuteQuery_MissingCatalog() {
        when(cspConnectionRepository.findById(cspConnectionId))
                .thenReturn(Optional.of(mockConnection));
        
        DataBase database = new DataBase();
        database.setName("test_db");
        when(dataBaseRepository.findByCspConnectionId(cspConnectionId))
                .thenReturn(List.of(database));
        when(dataCatalogRepository.findByCspConnectionId(cspConnectionId))
                .thenReturn(Collections.emptyList());
        
        AthenaQueryRequest request = new AthenaQueryRequest();
        request.setQuery("SELECT * FROM test_table LIMIT 10");
        request.setOrganizationId(UUID.randomUUID());
        
        // This should return a QueryExecutionResponse with FAILED status due to the catch block
        QueryExecutionResponse response = athenaService.executeQuery(cspConnectionId, request);
        assertEquals("FAILED", response.getStatus());
        assertTrue(response.getMessage().contains("data catalog"));
    }
    
    @Test
    void testGetQueryStatus_ConnectionNotFound() {
        when(cspConnectionRepository.findById(cspConnectionId))
                .thenReturn(Optional.empty());
        
        assertThrows(IllegalArgumentException.class, () -> 
                athenaService.getQueryStatus("test-execution-id", cspConnectionId));
    }
}