package com.example.poc_finops.costanalytics.service.impl;

import com.example.poc_finops.cloudconnections.domain.entity.CspConnection;
import com.example.poc_finops.cloudconnections.domain.entity.DataBase;
import com.example.poc_finops.cloudconnections.domain.entity.DataCatalog;
import com.example.poc_finops.cloudconnections.repository.CspConnectionRepository;
import com.example.poc_finops.cloudconnections.repository.DataBaseRepository;
import com.example.poc_finops.cloudconnections.repository.DataCatalogRepository;
import com.example.poc_finops.costanalytics.domain.entity.FocusLog;
import com.example.poc_finops.costanalytics.domain.entity.FocusUsage;
import com.example.poc_finops.costanalytics.repository.FocusLogRepository;
import com.example.poc_finops.costanalytics.repository.FocusUsageRepository;
import com.example.poc_finops.costanalytics.api.dto.AthenaQueryRequest;
import com.example.poc_finops.costanalytics.api.dto.AthenaQueryResponse;
import com.example.poc_finops.costanalytics.api.dto.QueryExecutionResponse;
import com.example.poc_finops.costanalytics.config.AthenaConfig;
import com.example.poc_finops.costanalytics.exception.AthenaQueryTimeoutException;
import com.example.poc_finops.costanalytics.service.AthenaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.services.athena.AthenaClient;
import software.amazon.awssdk.services.athena.model.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AthenaServiceImpl implements AthenaService {

    private final AthenaConfig athenaConfig;
    private final CspConnectionRepository cspConnectionRepository;
    private final DataBaseRepository dataBaseRepository;
    private final DataCatalogRepository dataCatalogRepository;
    private final FocusLogRepository focusLogRepository;
    private final FocusUsageRepository focusUsageRepository;

    @Override
    public QueryExecutionResponse executeQuery(UUID cspConnectionId, AthenaQueryRequest queryRequest) {
        log.info("Executing Athena query for CSP connection: {}", cspConnectionId);

        CspConnection cspConnection = getCspConnection(cspConnectionId);

        try (AthenaClient athenaClient = athenaConfig.createAthenaClient(cspConnection)) {

            // Get database from the CSP connection
            String database = getDatabaseForCsp(cspConnectionId);

            // Get catalog from the CSP connection  
            String catalog = getCatalogForCsp(cspConnectionId);

            // Use default workgroup
            String workgroup = athenaConfig.getDefaultWorkgroup();

            // Generate automatic query name with timestamp
            String queryName = "query_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String outputLocation = cspConnection.getDataSource().getOutputBucketUrl() + "/" + queryName + "/";

            StartQueryExecutionRequest request = StartQueryExecutionRequest.builder()
                    .queryString(queryRequest.getQuery())
                    .queryExecutionContext(QueryExecutionContext.builder()
                            .database(database)
                            .catalog(catalog)
                            .build())
                    .resultConfiguration(ResultConfiguration.builder()
                            .outputLocation(outputLocation)
                            .build())
                    .workGroup(workgroup)
                    .build();

            StartQueryExecutionResponse response = athenaClient.startQueryExecution(request);

            QueryExecutionResponse result = new QueryExecutionResponse();
            result.setQueryExecutionId(response.queryExecutionId());
            result.setStatus("QUEUED");
            result.setMessage("Query execution started successfully");
            result.setS3ResultLocation(outputLocation);

            log.info("Query executed successfully with execution ID: {}", response.queryExecutionId());
            return result;

        } catch (Exception e) {
            log.error("Error executing Athena query: {}", e.getMessage(), e);

            QueryExecutionResponse errorResponse = new QueryExecutionResponse();
            errorResponse.setStatus("FAILED");
            errorResponse.setMessage("Query execution failed: " + e.getMessage());
            return errorResponse;
        }
    }

    @Override
    public AthenaQueryResponse getQueryResults(String queryExecutionId, UUID cspConnectionId) {
        log.info("Getting results for query execution: {}", queryExecutionId);

        CspConnection cspConnection = getCspConnection(cspConnectionId);

        try (AthenaClient athenaClient = athenaConfig.createAthenaClient(cspConnection)) {

            GetQueryExecutionResponse executionDetails = athenaClient.getQueryExecution(
                    GetQueryExecutionRequest.builder()
                            .queryExecutionId(queryExecutionId)
                            .build());

            QueryExecution execution = executionDetails.queryExecution();
            AthenaQueryResponse response = new AthenaQueryResponse();
            response.setQueryExecutionId(queryExecutionId);
            response.setStatus(execution.status().state().toString());

            // Check if query has exceeded timeout
            if (isQueryTimedOut(execution)) {
                log.warn("Query execution {} has timed out, attempting to cancel", queryExecutionId);
                try {
                    cancelQuery(queryExecutionId, cspConnectionId);
                } catch (Exception e) {
                    log.error("Failed to cancel timed-out query {}: {}", queryExecutionId, e.getMessage());
                }
                
                AthenaQueryTimeoutException timeoutException = new AthenaQueryTimeoutException(
                        queryExecutionId, athenaConfig.getQueryExecutionTimeoutMinutes());
                
                response.setStatus("TIMEOUT");
                response.setErrorMessage(timeoutException.getMessage());
                return response;
            }

            if (execution.status().state() == QueryExecutionState.SUCCEEDED) {
                GetQueryResultsResponse results = athenaClient.getQueryResults(
                        GetQueryResultsRequest.builder()
                                .queryExecutionId(queryExecutionId)
                                .maxResults(1000)
                                .build());

                ResultSet resultSet = results.resultSet();

                if (!resultSet.rows().isEmpty()) {
                    Row headerRow = resultSet.rows().get(0);
                    List<String> columnNames = headerRow.data().stream()
                            .map(Datum::varCharValue)
                            .collect(Collectors.toList());
                    response.setColumnNames(columnNames);

                    List<Map<String, Object>> rows = new ArrayList<>();
                    for (int i = 1; i < resultSet.rows().size(); i++) {
                        Row row = resultSet.rows().get(i);
                        Map<String, Object> rowData = new HashMap<>();

                        for (int j = 0; j < columnNames.size() && j < row.data().size(); j++) {
                            String columnName = columnNames.get(j);
                            String value = row.data().get(j).varCharValue();
                            rowData.put(columnName, value);
                        }
                        rows.add(rowData);
                    }
                    response.setRows(rows);
                    response.setTotalRows((long) rows.size());
                    
                    // Save data to FOCUS tables
                    try {
                        saveFocusData(cspConnectionId, columnNames, rows);
                        log.info("Successfully saved {} rows to FOCUS tables", rows.size());
                    } catch (Exception e) {
                        log.error("Failed to save data to FOCUS tables, but query was successful: {}", e.getMessage(), e);
                        // Don't fail the query response - just log the error
                    }
                }

                if (execution.statistics() != null) {
                    response.setDataScannedInBytes(execution.statistics().dataScannedInBytes());
                    response.setExecutionTimeInMillis(execution.statistics().engineExecutionTimeInMillis());
                }

                if (execution.resultConfiguration() != null) {
                    response.setResultLocation(execution.resultConfiguration().outputLocation());
                }

            } else if (execution.status().state() == QueryExecutionState.FAILED) {
                response.setErrorMessage(execution.status().stateChangeReason());
            }

            log.info("Retrieved results for query execution: {}", queryExecutionId);
            return response;

        } catch (Exception e) {
            log.error("Error getting query results: {}", e.getMessage(), e);

            AthenaQueryResponse errorResponse = new AthenaQueryResponse();
            errorResponse.setQueryExecutionId(queryExecutionId);
            errorResponse.setStatus("ERROR");
            errorResponse.setErrorMessage("Failed to retrieve results: " + e.getMessage());
            return errorResponse;
        }
    }

    @Override
    public String getQueryStatus(String queryExecutionId, UUID cspConnectionId) {
        log.info("Getting status for query execution: {}", queryExecutionId);

        CspConnection cspConnection = getCspConnection(cspConnectionId);

        try (AthenaClient athenaClient = athenaConfig.createAthenaClient(cspConnection)) {

            GetQueryExecutionResponse response = athenaClient.getQueryExecution(
                    GetQueryExecutionRequest.builder()
                            .queryExecutionId(queryExecutionId)
                            .build());

            QueryExecution execution = response.queryExecution();

            // Check if query has exceeded timeout
            if (isQueryTimedOut(execution)) {
                log.warn("Query execution {} has timed out, attempting to cancel", queryExecutionId);
                try {
                    cancelQuery(queryExecutionId, cspConnectionId);
                } catch (Exception e) {
                    log.error("Failed to cancel timed-out query {}: {}", queryExecutionId, e.getMessage());
                }
                
                // Log timeout exception for monitoring purposes
                AthenaQueryTimeoutException timeoutException = new AthenaQueryTimeoutException(
                        queryExecutionId, athenaConfig.getQueryExecutionTimeoutMinutes());
                log.error("Query timeout detected: {}", timeoutException.getMessage());
                
                return "TIMEOUT";
            }

            String status = execution.status().state().toString();
            log.info("Query execution {} status: {}", queryExecutionId, status);
            return status;

        } catch (Exception e) {
            log.error("Error getting query status: {}", e.getMessage(), e);
            return "ERROR";
        }
    }

    @Override
    public void cancelQuery(String queryExecutionId, UUID cspConnectionId) {
        log.info("Cancelling query execution: {}", queryExecutionId);

        CspConnection cspConnection = getCspConnection(cspConnectionId);

        try (AthenaClient athenaClient = athenaConfig.createAthenaClient(cspConnection)) {

            athenaClient.stopQueryExecution(StopQueryExecutionRequest.builder()
                    .queryExecutionId(queryExecutionId)
                    .build());

            log.info("Query execution cancelled: {}", queryExecutionId);

        } catch (Exception e) {
            log.error("Error cancelling query: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to cancel query: " + e.getMessage(), e);
        }
    }

    private CspConnection getCspConnection(UUID cspConnectionId) {
        return cspConnectionRepository.findById(cspConnectionId)
                .orElseThrow(() -> new IllegalArgumentException("CSP Connection not found: " + cspConnectionId));
    }

    private String getDatabaseForCsp(UUID cspId) {
        List<DataBase> databases = dataBaseRepository.findByCspConnectionId(cspId);
        if (databases.isEmpty()) {
            throw new IllegalArgumentException("No database found for CSP ID: " + cspId
                    + ". Please configure a database for this CSP connection.");
        }
        return databases.get(0).getName(); // Use first database found
    }

    private String getCatalogForCsp(UUID cspId) {
        List<DataCatalog> catalogs = dataCatalogRepository.findByCspConnectionId(cspId);
        if (catalogs.isEmpty()) {
            throw new IllegalArgumentException("No data catalog found for CSP ID: " + cspId
                    + ". Please configure a data catalog for this CSP connection.");
        }
        return catalogs.get(0).getName(); // Use first catalog found
    }

    private boolean isQueryTimedOut(QueryExecution execution) {
        if (execution.status().submissionDateTime() == null) {
            return false;
        }

        LocalDateTime submissionTime = LocalDateTime.ofInstant(
                execution.status().submissionDateTime(),
                java.time.ZoneOffset.UTC);
        LocalDateTime now = LocalDateTime.now(java.time.ZoneOffset.UTC);
        long minutesElapsed = java.time.Duration.between(submissionTime, now).toMinutes();

        return minutesElapsed > athenaConfig.getQueryExecutionTimeoutMinutes();
    }

    @Transactional
    private void saveFocusData(UUID cspConnectionId, List<String> columnNames, List<Map<String, Object>> rowData) {
        try {
            log.info("Saving FOCUS data for CSP connection: {}", cspConnectionId);
            
            CspConnection cspConnection = getCspConnection(cspConnectionId);
            
            // Create FocusLog entry
            FocusLog focusLog = new FocusLog();
            focusLog.setOrganization(cspConnection.getOrganization());
            focusLog.setCspConnection(cspConnection);
            focusLog.setLogDate(LocalDate.now());
            focusLog.setStatus(true); // Success status
            
            FocusLog savedFocusLog = focusLogRepository.save(focusLog);
            log.info("Created FocusLog entry with ID: {}", savedFocusLog.getId());
            
            // Process and save FocusUsage records
            List<FocusUsage> focusUsageList = new ArrayList<>();
            
            for (Map<String, Object> row : rowData) {
                FocusUsage focusUsage = mapRowToFocusUsage(row, savedFocusLog);
                if (focusUsage != null) {
                    focusUsageList.add(focusUsage);
                }
            }
            
            if (!focusUsageList.isEmpty()) {
                focusUsageRepository.saveAll(focusUsageList);
                log.info("Saved {} FocusUsage records to t_focus_report", focusUsageList.size());
            }
            
        } catch (Exception e) {
            log.error("Error saving FOCUS data: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to save FOCUS data", e);
        }
    }
    
    private FocusUsage mapRowToFocusUsage(Map<String, Object> row, FocusLog focusLog) {
        try {
            FocusUsage focusUsage = new FocusUsage();
            focusUsage.setFocusLog(focusLog);
            
            // Map common FOCUS fields using exact column name matching
            mapStringField(row, "availabilityzone", focusUsage::setAvailabilityZone);
            mapDoubleField(row, "billedcost", focusUsage::setBilledCost);
            mapStringField(row, "billingaccountid", focusUsage::setBillingAccountId);
            mapStringField(row, "billingaccountname", focusUsage::setBillingAccountName);
            mapStringField(row, "billingcurrency", focusUsage::setBillingCurrency);
            mapTimestampField(row, "billingperiodend", focusUsage::setBillingPeriodEnd);
            mapTimestampField(row, "billingperiodstart", focusUsage::setBillingPeriodStart);
            mapStringField(row, "chargecategory", focusUsage::setChargeCategory);
            mapStringField(row, "chargeclass", focusUsage::setChargeClass);
            mapStringField(row, "chargedescription", focusUsage::setChargeDescription);
            mapStringField(row, "chargefrequency", focusUsage::setChargeFrequency);
            mapTimestampField(row, "chargeperiodend", focusUsage::setChargePeriodEnd);
            mapTimestampField(row, "chargeperiodstart", focusUsage::setChargePeriodStart);
            mapStringField(row, "commitmentdiscountcategory", focusUsage::setCommitmentDiscountCategory);
            mapStringField(row, "commitmentdiscountid", focusUsage::setCommitmentDiscountId);
            mapStringField(row, "commitmentdiscountname", focusUsage::setCommitmentDiscountName);
            mapStringField(row, "commitmentdiscountstatus", focusUsage::setCommitmentDiscountStatus);
            mapStringField(row, "commitmentdiscounttype", focusUsage::setCommitmentDiscountType);
            mapDoubleField(row, "consumedquantity", focusUsage::setConsumedQuantity);
            mapStringField(row, "consumedunit", focusUsage::setConsumedUnit);
            mapDoubleField(row, "contractedcost", focusUsage::setContractedCost);
            mapDoubleField(row, "contractedunitprice", focusUsage::setContractedUnitPrice);
            mapDoubleField(row, "effectivecost", focusUsage::setEffectiveCost);
            mapStringField(row, "invoiceissuername", focusUsage::setInvoiceIssuerName);
            mapDoubleField(row, "listcost", focusUsage::setListCost);
            mapDoubleField(row, "listunitprice", focusUsage::setListUnitPrice);
            mapStringField(row, "pricingcategory", focusUsage::setPricingCategory);
            mapDoubleField(row, "pricingquantity", focusUsage::setPricingQuantity);
            mapStringField(row, "pricingunit", focusUsage::setPricingUnit);
            mapStringField(row, "providername", focusUsage::setProviderName);
            mapStringField(row, "publishername", focusUsage::setPublisherName);
            mapStringField(row, "regionid", focusUsage::setRegionId);
            mapStringField(row, "regionname", focusUsage::setRegionName);
            mapStringField(row, "resourceid", focusUsage::setResourceId);
            mapStringField(row, "resourcename", focusUsage::setResourceName);
            mapStringField(row, "resourcetype", focusUsage::setResourceType);
            mapStringField(row, "servicecategory", focusUsage::setServiceCategory);  
            mapStringField(row, "servicename", focusUsage::setServiceName);
            mapStringField(row, "skuid", focusUsage::setSkuId);
            mapStringField(row, "skupriceid", focusUsage::setSkuPriceId);
            mapStringField(row, "subaccountid", focusUsage::setSubAccountId);
            mapStringField(row, "subaccountname", focusUsage::setSubAccountName);
            mapStringField(row, "x_operation", focusUsage::setXOperation);
            mapStringField(row, "x_servicecode", focusUsage::setXServiceCode);
            mapStringField(row, "x_usagetype", focusUsage::setXUsageType);
            
            // Handle JSONB fields - for now set as empty maps if not present
            focusUsage.setTags(new HashMap<>());
            focusUsage.setXCostCategories(new HashMap<>());
            focusUsage.setXDiscounts(new HashMap<>());
            
            return focusUsage;
            
        } catch (Exception e) {
            log.error("Error mapping row to FocusUsage: {}", e.getMessage(), e);
            return null;
        }
    }
    
    private void mapStringField(Map<String, Object> row, String columnName, java.util.function.Consumer<String> setter) {
        Object value = row.get(columnName);
        if (value != null && !value.toString().trim().isEmpty()) {
            setter.accept(value.toString());
        }
    }
    
    private void mapDoubleField(Map<String, Object> row, String columnName, java.util.function.Consumer<Double> setter) {
        Object value = row.get(columnName);
        if (value != null && !value.toString().trim().isEmpty()) {
            try {
                setter.accept(Double.parseDouble(value.toString()));
            } catch (NumberFormatException e) {
                log.warn("Invalid number format for column {}: {}", columnName, value);
            }
        }
    }
    
    private void mapTimestampField(Map<String, Object> row, String columnName, java.util.function.Consumer<OffsetDateTime> setter) {
        Object value = row.get(columnName);
        if (value != null && !value.toString().trim().isEmpty()) {
            try {
                // Try parsing as OffsetDateTime - adjust format as needed
                setter.accept(OffsetDateTime.parse(value.toString()));
            } catch (Exception e) {
                log.warn("Invalid timestamp format for column {}: {}", columnName, value);
            }
        }
    }
}