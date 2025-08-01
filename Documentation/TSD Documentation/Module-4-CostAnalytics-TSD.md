# **Cost Analytics Module - Technical Specification Document**

# **Overview**

The FinOps system is a Spring Boot application designed for Financial Operations (FinOps) across multiple cloud service providers (CSPs). The system provides a billing dashboard for cloud cost management, financial optimization, and multi-tenant cost analytics with FOCUS standard compliance.

**Purpose:** Enable organizations to manage, monitor, and optimize cloud spending across multiple CSPs through automated cost analytics, budget management, anomaly detection, and recommendations.

**Scope:** Multi-tenant cloud cost management platform supporting AWS, Azure, and GCP with comprehensive financial analytics, budgeting, alerting, and cost optimization features.

**Architecture:** Domain-Driven Design (DDD) with 7 business modules organized around distinct financial operations domains.


**Key Endpoints:**
- FOCUS Data: `GET|DELETE /api/focus-data/logs`, `GET|DELETE /api/focus-data/reports`
- Cost Reports: `GET /api/cost-analytics/*` (comprehensive cost analytics - implementation ready)
- Usage Reports: `POST /api/usage-reports/batch` (scheduled report generation)
- Athena Queries: `POST /api/athena/connections/{cspConnectionId}/execute`, `GET /api/athena/executions/{queryExecutionId}/results`, `GET /api/athena/executions/{queryExecutionId}/status`, `DELETE /api/athena/executions/{queryExecutionId}`


## **Tech Stack**

- **Backend:** Java 21 with Spring Boot 3.5.3 (RESTful APIs)
- **Build Tool:** Maven
- **Database (Development):** H2 in-memory database / PostgreSQL
- **Database (Production):** Amazon RDS PostgreSQL
- **Authentication:** JWT tokens with JJWT 0.12.3
- **Security:** Spring Security with role-based access control
- **Documentation:** SpringDoc OpenAPI 2.2.0 (Swagger UI)
- **Cloud Integration:** AWS SDK v2 for credential validation and Athena queries
- **Analytics Engine:** Amazon Athena for SQL-based cost analytics on data lakes
- **ORM:** Spring Data JPA with Hibernate
- **Utilities:** Lombok, Hypersistence Utils for Hibernate
- **Schema Version:** finops_v2 across all environments
- **Data Types:** Double for financial amounts, JSONB for metadata

**Athena Integration Configuration:**
- **Query Timeout:** Configurable (default: 15 minutes)
- **API Timeouts:** API call timeout 600s, attempt timeout 120s
- **Cost Estimation:** $5.00 per TB scanned (configurable)
- **Workgroup:** Uses `primary` workgroup (configurable)
- **Result Storage:** S3 output location from CSP connection configuration

**Database Tables (Schema: finops_v2):**

| Table Name | Description | Key Columns |
|------------|-------------|-------------|
| `t_focus_report` | FOCUS-compliant cost and usage data from all CSPs | `id` UUID, `focus_log_id` UUID, `billedcost` FLOAT8, `effectivecost` FLOAT8, `servicename` TEXT, `resourceid` TEXT, `regionname` TEXT, `chargecategory` TEXT, `chargeclass` TEXT, `tags` JSONB, `x_costcategories` JSONB, `created_at` TIMESTAMPTZ, `updated_at` TIMESTAMPTZ, `created_by` UUID, `updated_by` UUID, `deleted_at` TIMESTAMPTZ |
| `t_focus_log` | FOCUS data processing session tracking | `id` UUID, `organization_id` UUID, `csp_connection_id` UUID, `log_date` DATE NOT NULL, `status` BOOLEAN NOT NULL |

**FOCUS Standard Fields in t_focus_report:**

| Field Category | Columns | Data Types |
|----------------|---------|------------|
| **Financial Core** | `billedcost`, `effectivecost`, `listcost`, `contractedcost` | FLOAT8 |
| **Unit Pricing** | `listunitprice`, `contractedunitprice` | FLOAT8 |
| **Usage Metrics** | `consumedquantity`, `pricingquantity` | FLOAT8 |
| **Billing Context** | `billingaccountid`, `billingaccountname`, `billingcurrency` | TEXT |
| **Billing Periods** | `billingperiodstart`, `billingperiodend`, `chargeperiodstart`, `chargeperiodend` | TIMESTAMPTZ |
| **Resource Identity** | `resourceid`, `resourcename`, `resourcetype` | TEXT |
| **Service Context** | `servicename`, `servicecategory`, `skuid`, `skupriceid` | TEXT |
| **Geographic** | `regionid`, `regionname`, `availabilityzone` | TEXT |
| **Account Structure** | `subaccountid`, `subaccountname` | TEXT |
| **Charge Classification** | `chargecategory`, `chargeclass`, `chargedescription`, `chargefrequency` | TEXT |
| **Pricing Details** | `pricingcategory`, `pricingunit`, `consumedunit` | TEXT |
| **Provider Context** | `providername`, `publishername`, `invoiceissuername` | TEXT |
| **Commitment Discounts** | `commitmentdiscountcategory`, `commitmentdiscountid`, `commitmentdiscountname`, `commitmentdiscountstatus`, `commitmentdiscounttype` | TEXT |
| **Extension Fields** | `x_operation`, `x_servicecode`, `x_usagetype` | TEXT |
| **Metadata** | `tags`, `x_costcategories`, `x_discounts` | JSONB |

---

## **API Endpoints**

### **Athena Query Operations**

| Endpoint | Method | Description | Response Codes |
|----------|--------|-------------|----------------|
| `/api/athena/connections/{cspConnectionId}/execute` | POST | Execute SQL query on AWS Athena using specified CSP connection | 200: Success, 400: Query failed, 401: Unauthorized, 404: Connection not found |
| `/api/athena/executions/{queryExecutionId}/results` | GET | Retrieve results for completed Athena query execution | 200: Success, 400: Query error, 408: Timeout, 404: Query not found |
| `/api/athena/executions/{queryExecutionId}/status` | GET | Get current status of Athena query execution | 200: Success, 400: Query error, 408: Timeout, 404: Query not found |
| `/api/athena/executions/{queryExecutionId}` | DELETE | Cancel running Athena query execution | 200: Success, 400: Cancel failed, 404: Query not found |

**Query Status Values:**
- `QUEUED`: Query is waiting to be executed
- `RUNNING`: Query is currently executing
- `SUCCEEDED`: Query completed successfully
- `FAILED`: Query execution failed
- `CANCELLED`: Query was cancelled by user
- `TIMEOUT`: Query exceeded configured timeout limit
- `ERROR`: System error occurred during processing

---

## **Request Body Examples**

### **Usage Report Request**

```json
{
  "organizationId": "550e8400-e29b-41d4-a716-446655440000",
  "cspConnectionIds": [
    "660e8400-e29b-41d4-a716-446655440001",
    "770e8400-e29b-41d4-a716-446655440002"
  ],
  "startDate": "2024-01-01",
  "endDate": "2024-01-31",
  "groupBy": "service",
  "serviceNames": [
    "Amazon EC2",
    "Amazon S3",
    "Amazon RDS"
  ],
  "regionNames": [
    "us-east-1",
    "us-west-2"
  ],
  "chargeCategories": [
    "Usage",
    "Purchase"
  ],
  "resourceTypes": [
    "Instance",
    "Volume"
  ],
  "currency": "USD",
  "includeCredits": false,
  "includeTags": true,
  "limit": 1000,
  "sortBy": "effectiveCost",
  "sortOrder": "desc"
}
```

### **Cost Analytics Query Parameters**

```json
{
  "organizationId": "550e8400-e29b-41d4-a716-446655440000",
  "cspConnectionId": "660e8400-e29b-41d4-a716-446655440001",
  "startDate": "2024-01-01T00:00:00Z",
  "endDate": "2024-01-31T23:59:59Z",
  "granularity": "daily",
  "dimension": "service",
  "metrics": [
    "billedCost",
    "effectiveCost",
    "totalSavings"
  ],
  "filters": {
    "serviceName": ["Amazon EC2", "Amazon S3"],
    "regionName": ["us-east-1"],
    "chargeClass": ["OnDemand", "Reserved"]
  }
}
```

### **Athena Query Request**

```json
{
  "query": "SELECT servicename, regionname, SUM(billedcost) as total_cost, COUNT(*) as resource_count FROM focus_cost_data WHERE billingperiodstart >= DATE('2024-01-01') AND billingperiodend <= DATE('2024-01-31') AND chargecategory = 'Usage' GROUP BY servicename, regionname ORDER BY total_cost DESC LIMIT 100",
  "organizationId": "550e8400-e29b-41d4-a716-446655440000"
}
```

### **Request Body Field Tables**

#### **Usage Report Request Fields**

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| organizationId | UUID | Yes | Organization ID for multi-tenant isolation |
| cspConnectionIds | UUID[] | No | Array of CSP connection IDs to include |
| startDate | Date | Yes | Report start date (YYYY-MM-DD) |
| endDate | Date | Yes | Report end date (YYYY-MM-DD) |
| groupBy | String | No | Grouping dimension: 'service', 'region', 'resource', 'category' |
| serviceNames | String[] | No | Filter by specific service names |
| regionNames | String[] | No | Filter by specific regions |
| chargeCategories | String[] | No | Filter by charge categories (Usage, Purchase, Tax, etc.) |
| resourceTypes | String[] | No | Filter by resource types |
| currency | String | No | Currency for cost display (default: USD) |
| includeCredits | Boolean | No | Include credit adjustments in calculations |
| includeTags | Boolean | No | Include resource tags in response |
| limit | Integer | No | Maximum number of records to return |
| sortBy | String | No | Sort field: 'billedCost', 'effectiveCost', 'resourceName' |
| sortOrder | String | No | Sort order: 'asc' or 'desc' |

#### **Athena Query Request Fields**

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| query | String | Yes | SQL query to execute on Athena (max 10,000 characters) |
| organizationId | UUID | Yes | Organization ID for access control and data isolation |

---

## **Response Body Examples**

### **FOCUS Usage Data Response**

```json
{
  "id": "990e8400-e29b-41d4-a716-446655440004",
  "focusLogId": "aa0e8400-e29b-41d4-a716-446655440005",
  "billedCost": 156.78,
  "effectiveCost": 125.42,
  "listCost": 195.98,
  "contractedCost": 140.60,
  "listUnitPrice": 0.10,
  "contractedUnitPrice": 0.08,
  "consumedQuantity": 1567.8,
  "consumedUnit": "GB-Hours",
  "pricingQuantity": 1567.8,
  "pricingUnit": "GB-Hours",
  "billingAccountId": "123456789012",
  "billingAccountName": "Production Account",
  "billingCurrency": "USD",
  "billingPeriodStart": "2024-01-01T00:00:00Z",
  "billingPeriodEnd": "2024-01-31T23:59:59Z",
  "chargeCategory": "Usage",
  "chargeClass": "OnDemand",
  "chargeDescription": "EC2-Instance running Linux/UNIX",
  "chargeFrequency": "Hourly",
  "resourceId": "i-1234567890abcdef0",
  "resourceName": "web-server-prod-01",
  "resourceType": "Instance",
  "serviceName": "Amazon Elastic Compute Cloud",
  "serviceCategory": "Compute",
  "skuId": "6YS6EN2CT7",
  "skuPriceId": "6YS6EN2CT7.JRTCKXETXF",
  "regionId": "us-east-1",
  "regionName": "US East (N. Virginia)",
  "availabilityZone": "us-east-1a",
  "subAccountId": "123456789012",
  "subAccountName": "Production",
  "providername": "AWS",
  "publishername": "Amazon Web Services",
  "invoiceissuername": "Amazon Web Services, Inc.",
  "commitmentDiscountCategory": "Reserved Instance",
  "commitmentDiscountId": "ri-1234567890abcdef0",
  "commitmentDiscountName": "EC2 Reserved Instance",
  "commitmentDiscountStatus": "Used",
  "commitmentDiscountType": "Instance",
  "pricingCategory": "Standard",
  "xOperation": "RunInstances",
  "xServiceCode": "AmazonEC2",
  "xUsageType": "USW2-BoxUsage:m5.large",
  "tags": {
    "Environment": "Production",
    "Project": "WebApp",
    "Owner": "Platform Team",
    "CostCenter": "Engineering"
  },
  "xCostCategories": {
    "Department": "Engineering",
    "Application": "Web Frontend"
  },
  "xDiscounts": {
    "EnterpriseDiscount": "5%",
    "VolumeDiscount": "2%"
  },
  "createdAt": "2024-01-15T10:30:00Z",
  "updatedAt": "2024-01-15T10:30:00Z"
}
```

### **Cost Analytics Summary Response**

```json
{
  "organizationId": "550e8400-e29b-41d4-a716-446655440000",
  "reportPeriod": {
    "startDate": "2024-01-01",
    "endDate": "2024-01-31"
  },
  "totalMetrics": {
    "totalBilledCost": 45678.90,
    "totalEffectiveCost": 42356.78,
    "totalListCost": 52340.12,
    "totalSavings": 9983.34,
    "savingsPercentage": 19.08,
    "recordCount": 15847
  },
  "costByService": [
    {
      "serviceName": "Amazon Elastic Compute Cloud",
      "billedCost": 18456.78,
      "effectiveCost": 16234.56,
      "resourceCount": 145,
      "utilizationPercentage": 87.9
    },
    {
      "serviceName": "Amazon Simple Storage Service",
      "billedCost": 8934.23,
      "effectiveCost": 8123.45,
      "resourceCount": 267,
      "utilizationPercentage": 91.0
    }
  ],
  "costByRegion": [
    {
      "regionName": "US East (N. Virginia)",
      "billedCost": 28456.78,
      "effectiveCost": 25987.65,
      "resourceCount": 234
    }
  ],
  "costByChargeCategory": [
    {
      "chargeCategory": "Usage",
      "billedCost": 38456.78,
      "effectiveCost": 35123.45,
      "percentage": 84.2
    },
    {
      "chargeCategory": "Purchase",
      "billedCost": 5234.56,
      "effectiveCost": 5234.56,
      "percentage": 11.5
    }
  ],
  "topCostResources": [
    {
      "resourceId": "i-1234567890abcdef0",
      "resourceName": "web-server-prod-01",
      "resourceType": "Instance",
      "serviceName": "Amazon EC2",
      "effectiveCost": 1234.56,
      "utilizationPercentage": 78.5
    }
  ],
  "trendData": {
    "dailyCosts": [
      {
        "date": "2024-01-01",
        "billedCost": 1456.78,
        "effectiveCost": 1345.67
      }
    ],
    "growthPercentage": 12.5,
    "forecastNextMonth": 47892.34
  }
}
```

### **Athena Query Execution Response**

```json
{
  "queryExecutionId": "12345678-1234-1234-1234-123456789012",
  "status": "QUEUED",
  "message": "Query execution started successfully",
  "s3ResultLocation": "s3://your-results-bucket/query_20240115_143000/"
}
```

### **Athena Query Results Response**

```json
{
  "queryExecutionId": "12345678-1234-1234-1234-123456789012",
  "status": "SUCCEEDED",
  "columnNames": [
    "servicename",
    "regionname", 
    "total_cost",
    "resource_count"
  ],
  "rows": [
    {
      "servicename": "Amazon Elastic Compute Cloud",
      "regionname": "US East (N. Virginia)",
      "total_cost": "18456.78",
      "resource_count": "145"
    },
    {
      "servicename": "Amazon Simple Storage Service",
      "regionname": "US East (N. Virginia)", 
      "total_cost": "8934.23",
      "resource_count": "267"
    }
  ],
  "totalRows": 2,
  "dataScannedInBytes": 1048576000,
  "executionTimeInMillis": 15420,
  "resultLocation": "s3://your-results-bucket/query_20240115_143000/results.csv"
}
```

### **Athena Query Timeout Response**

```json
{
  "queryExecutionId": "12345678-1234-1234-1234-123456789012",
  "status": "TIMEOUT",
  "errorMessage": "Query execution 12345678-1234-1234-1234-123456789012 timed out after 15 minutes"
}
```

### **Athena Query Error Response**

```json
{
  "queryExecutionId": "12345678-1234-1234-1234-123456789012",
  "status": "FAILED",
  "errorMessage": "SYNTAX_ERROR: line 1:8: Column 'invalid_column' cannot be resolved"
}
```

### **Response Field Tables**

#### **FOCUS Usage Data Response Fields**

| Field | Type | Description |
|-------|------|-------------|
| id | UUID | Unique usage record identifier |
| focusLogId | UUID | Reference to processing session |
| billedCost | Double | Actual amount billed by CSP |
| effectiveCost | Double | Cost after discounts and credits |
| listCost | Double | List price before any discounts |
| resourceId | String | Unique resource identifier from CSP |
| serviceName | String | Cloud service name (standardized) |
| regionName | String | Geographic region name |
| chargeCategory | String | FOCUS charge category (Usage, Purchase, Tax, etc.) |
| chargeClass | String | FOCUS charge class (OnDemand, Reserved, Spot) |
| tags | JSONB | Resource tags and metadata |
| xCostCategories | JSONB | Extended cost categorization |

#### **Athena Query Response Fields**

| Field | Type | Description |
|-------|------|-------------|
| queryExecutionId | String | Unique identifier for the query execution |
| status | String | Query execution status (QUEUED, RUNNING, SUCCEEDED, FAILED, CANCELLED, TIMEOUT, ERROR) |
| columnNames | String[] | Column names from the query result set |
| rows | Object[] | Array of result rows with column name-value pairs |
| totalRows | Long | Total number of rows returned by the query |
| dataScannedInBytes | Long | Amount of data scanned by Athena (for cost calculation) |
| executionTimeInMillis | Long | Query execution time in milliseconds |
| resultLocation | String | S3 location where query results are stored |
| errorMessage | String | Error description if query failed or timed out |
| message | String | Status message for query execution (execution response only) |
| s3ResultLocation | String | S3 output location for query results (execution response only) |

---

## **Business Logic Explanation**

### **FOCUS Data Processing Architecture**

The system implements comprehensive FOCUS data lifecycle management:

1. **Data Ingestion** - FOCUS-compliant data ingestion from multiple CSPs with standardization
2. **Data Validation** - Schema validation and data quality checks for financial accuracy
3. **Multi-CSP Normalization** - Standardizes cost data across AWS, Azure, GCP using FOCUS schema
4. **Metadata Processing** - Handles JSONB fields for tags, cost categories, and CSP-specific extensions
5. **Audit Trail** - Complete tracking of data processing sessions and modifications
6. **Event Publishing** - Domain events for downstream integration and monitoring

### **Athena Query Processing Architecture**

The system integrates Amazon Athena for advanced SQL-based analytics on cloud cost data lakes:

1. **Query Execution** - Execute complex SQL queries on large-scale cost datasets stored in S3 data lakes
2. **Connection Management** - Dynamic Athena client creation using CSP connection credentials and configurations
3. **Data Catalog Integration** - Utilizes configured data catalogs and databases for query context
4. **Result Processing** - Automatic retrieval and parsing of query results with FOCUS schema mapping
5. **Timeout Management** - Configurable query timeouts with automatic cancellation of long-running queries
6. **FOCUS Integration** - Seamless mapping of Athena query results to FOCUS data structures
7. **Cost Optimization** - Data scanning metrics for query cost estimation and optimization
8. **Error Handling** - Comprehensive error handling for query failures, timeouts, and connection issues

### **FOCUS Standard Compliance**

#### **Core FOCUS Dimensions**
- **Billing Context** - Complete billing account, period, and currency information
- **Resource Identity** - Comprehensive resource identification and classification
- **Service Classification** - Standardized service and SKU categorization
- **Charge Classification** - FOCUS-compliant charge categories and classes
- **Geographic Dimensions** - Region, availability zone, and provider location data

#### **Extension and Flexibility**
- **CSP-Specific Extensions** - X-fields for provider-specific data (AWS, Azure, GCP)
- **Custom Cost Categories** - Flexible cost categorization via JSONB fields
- **Tag Management** - Comprehensive resource tagging and metadata handling
- **Discount Tracking** - Detailed commitment discount and enterprise agreement tracking

### **Data Quality and Validation**

#### **Schema Validation**
- **FOCUS Compliance Checking** - Validates data against FOCUS standard requirements
- **Data Type Validation** - Ensures proper data types for financial calculations
- **Date Range Validation** - Validates billing periods and charge periods

#### **Data Integrity**
- **Duplicate Detection** - Identifies and handles duplicate usage records
- **Error Handling** - Comprehensive error logging and data quality reporting
- **Audit Trail** - Complete tracking of data modifications and processing

### **Integration Architecture**

#### **Budget Management Integration**
- **Cost Baseline Data** - Provides actual spending data for budget variance analysis
- **Threshold Monitoring** - Cost data for budget alert processing
- **Variance Reporting** - Budget vs. actual cost analysis and reporting
- **Forecast Integration** - Historical cost data for budget planning and forecasting

#### **Anomaly Detection Integration**
- **Cost Pattern Analysis** - Historical cost patterns for anomaly detection
- **Trend Analysis** - Cost trending data for anomaly detection
- **Alert Context** - Detailed cost context for anomaly alert processing

#### **Recommendations Integration**
- **Cost Optimization Data** - Detailed usage and cost data for optimization recommendations
- **Resource Utilization** - Utilization metrics for rightsizing recommendations
- **Commitment Analysis** - Reserved instance and committed use opportunity analysis
- **Savings Quantification** - Historical savings data for recommendation validation

#### **Athena Analytics Integration**  
- **Data Lake Queries** - Direct SQL queries on S3-stored cost and usage data
- **Advanced Analytics** - Complex aggregations, time-series analysis, and custom metrics
- **Real-time Insights** - On-demand query execution for immediate business insights
- **Historical Analysis** - Deep historical cost trend analysis across multiple time periods
- **Custom Reporting** - Flexible query-based reporting for specific business requirements

### **Athena Query Management**

#### **Query Lifecycle Management**
- **Execution Tracking** - Complete tracking of query execution status and progress
- **Result Caching** - S3-based result storage for query result persistence and sharing
- **Query Optimization** - Data scanning metrics for cost-effective query design
- **Resource Management** - Workgroup-based query organization and resource control

#### **Timeout and Error Handling**
- **Configurable Timeouts** - Customizable query execution timeouts (default: 15 minutes)
- **Automatic Cancellation** - Automatic cancellation of timed-out queries to prevent resource waste
- **Error Classification** - Detailed error categorization for troubleshooting and monitoring
- **Retry Logic** - Intelligent retry mechanisms for transient failures

#### **Data Integration Flow**
- **Result Mapping** - Automatic mapping of Athena query results to FOCUS schema
- **Batch Processing** - Efficient batch insertion of query results into FOCUS tables
- **Data Validation** - Schema validation and data quality checks for query results
- **Audit Logging** - Complete audit trail of query executions and data integrations

---

## **API Call Flow Description**

```
FOCUS Data Processing Flow
Data Ingestion Service
 │
 │ 1. CSP billing data received (AWS Cost & Usage Report, Azure billing export, etc.)
 ▼
FocusDataService.processBillingData
 │
 │ 2. Create FocusLog entity for processing session
 │      ├─ Link to Organization and CspConnection
 │      ├─ Set processing date and initial status
 │      └─ Persist to t_focus_log table
 │
 │ 3. Transform CSP-specific data to FOCUS standard:
 │      ├─ Map CSP billing fields to FOCUS schema
 │      ├─ Standardize service names and categories
 │      ├─ Process tags and metadata into JSONB
 │      └─ Validate data quality and completeness
 │
 │ 4. Batch insert FocusReport records:
 │      ├─ Create FocusReport entities with complete FOCUS data
 │      ├─ Link each record to FocusLog session
 │      ├─ Persist to t_focus_report table in batches
 │      └─ If processing fails:
 │            ├─ Mark FocusLog status as failed
 │            ├─ Log detailed error information
 │            └─ Publish FocusDataProcessedEvent with error details
 │
 │ 5. Successful processing completion:
 │      ├─ Mark FocusLog status as successful
 │      ├─ Update record counts and processing metrics
 │      ├─ Publish FocusDataProcessedEvent for downstream processing
 │      └─ Trigger cost analytics refresh
 │
 ▼
FOCUS data ready for cost analytics and reporting

Cost Analytics Query Flow
Finance User
 │
 │ 1. Request cost breakdown (GET /api/cost-analytics/cost-by-service)
 ▼
CostReportController.getCostByService
 │
 │ 2. Extract query parameters (organization, date range, filters)
 │
 │ 3. Validate user access to organization data
 │      └─ If access denied:
 │            ├─ Return 403 Forbidden
 │            └─ Include access control error message
 │
 │ 4. Call CostAnalyticsService.getCostByService:
 │      ├─ Apply date range filters on billing periods
 │      ├─ Apply service filters
 │      ├─ Group by service name 
 │      └─ Calculate billedCost, effectiveCost, savings
 │
 │ 5. Response generation:
 │      ├─ Convert service cost aggregations to DTOs
 │      ├─ Include metadata (record counts, processing times)
 │      ├─ Add trend analysis and growth indicators
 │      └─ Return comprehensive cost breakdown
 │
 ▼
Detailed service-level cost analytics delivered to user

Usage Report Generation Flow
Report Scheduler
 │
 │ 1. Trigger scheduled report generation (POST /api/usage-reports/batch)
 ▼
UsageController.generateBatchReports
 │
 │ 2. Extract UsageReportRequest parameters
 │
 │ 3. Validate report parameters and organization access
 │
 │ 4. Call UsageReportService.generateReport:
 │      ├─ Query FOCUS data with specified filters
 │      ├─ Group data by requested dimensions (service, region, resource)
 │      ├─ Apply date range filters
 │      ├─ Calculate aggregations and summaries
 │      └─ Format data for export (PDF, Excel)
 │
 │ 5. Report processing and delivery:
 │      ├─ Generate report files in requested formats
 │      ├─ Store reports in configured storage location
 │      ├─ Log report generation for audit trail
 │      └─ Schedule next report if recurring
 │
 ▼
Usage reports generated and delivered to stakeholders

Athena Query Execution Flow
Finance User/Analytics System
 │
 │ 1. Execute Athena query (POST /api/athena/connections/{cspConnectionId}/execute)
 ▼
AthenaController.executeQuery
 │
 │ 2. Extract AthenaQueryRequest (SQL query + organization ID)
 │
 │ 3. Validate user access to CSP connection
 │      └─ If access denied:
 │            ├─ return 403 Forbidden
 │            └─ Include access control error message
 │
 │ 4. Call AthenaService.executeQuery:
 │      ├─ Retrieve CSP connection credentials and configuration
 │      ├─ Create AthenaClient with connection credentials
 │      ├─ Get database and catalog from CSP connection
 │      ├─ Generate unique query name with timestamp
 │      ├─ Configure S3 output location from connection settings
 │      └─ Submit query to Athena with StartQueryExecutionRequest
 │
 │ 5. Query submission response:
 │      ├─ Return QueryExecutionResponse with execution ID
 │      ├─ Status: QUEUED
 │      ├─ Include S3 result location for output
 │      └─ If submission fails:
 │            ├─ Status: FAILED
 │            └─ Include error message and details
 │
 ▼
Query submitted to Athena, execution ID returned to user

Athena Query Results Retrieval Flow
Finance User/Analytics System  
 │
 │ 1. Check query status or get results (GET /api/athena/executions/{queryExecutionId}/results)
 ▼
AthenaController.getQueryResults
 │
 │ 2. Extract query execution ID and CSP connection ID
 │
 │ 3. Call AthenaService.getQueryResults:
 │      ├─ Create AthenaClient with CSP connection credentials
 │      ├─ Get query execution details from Athena
 │      ├─ Check if query has exceeded timeout threshold
 │      └─ If timed out:
 │            ├─ Attempt to cancel query automatically
 │            ├─ Return status: TIMEOUT
 │            └─ Include timeout exception message
 │
 │ 4. Process query execution status:
 │      ├─ QUEUED/RUNNING: Return current status
 │      ├─ FAILED: Return error message from Athena
 │      └─ SUCCEEDED: Proceed to result processing
 │
 │ 5. Result processing (if SUCCEEDED):
 │      ├─ Retrieve query results from Athena (max 1000 rows)
 │      ├─ Parse result set with column names and data rows
 │      ├─ Include execution statistics (data scanned, execution time)
 │      ├─ Add S3 result location for full dataset access
 │      └─ Call saveFocusData to persist results to FOCUS tables
 │
 │ 6. FOCUS data integration:
 │      ├─ Create FocusLog entry for audit trail
 │      ├─ Map query result columns to FOCUS schema fields
 │      ├─ Create FocusUsage entities with proper field mapping
 │      ├─ Batch insert FocusUsage records to t_focus_report
 │      └─ If integration fails:
 │            ├─ Log error but don't fail query response
 │            └─ Query results still returned to user
 │
 │ 7. Response generation:
 │      ├─ Return AthenaQueryResponse with results
 │      ├─ Include column names, data rows, and metadata
 │      ├─ Add execution metrics and result location
 │      └─ If error: Include error message and status
 │
 ▼
Query results delivered to user, FOCUS data optionally persisted

Athena Query Status Check Flow
Finance User/Analytics System
 │
 │ 1. Check query status (GET /api/athena/executions/{queryExecutionId}/status)
 ▼
AthenaController.getQueryStatus
 │
 │ 2. Extract query execution ID and CSP connection ID
 │
 │ 3. Call AthenaService.getQueryStatus:
 │      ├─ Create AthenaClient with CSP connection credentials
 │      ├─ Get query execution details from Athena
 │      ├─ Check if query has exceeded timeout threshold
 │      └─ If timed out:
 │            ├─ Attempt to cancel query automatically
 │            └─ Return status: TIMEOUT
 │
 │ 4. Status response:
 │      ├─ Return current execution status from Athena
 │      ├─ If TIMEOUT: Return 408 Request Timeout HTTP status
 │      ├─ If ERROR: Return 400 Bad Request HTTP status
 │      └─ Otherwise: Return 200 OK with status string
 │
 ▼
Current query execution status delivered to user

Athena Query Cancellation Flow
Finance User/Analytics System
 │
 │ 1. Cancel query (DELETE /api/athena/executions/{queryExecutionId})
 ▼
AthenaController.cancelQuery
 │
 │ 2. Extract query execution ID and CSP connection ID
 │
 │ 3. Call AthenaService.cancelQuery:
 │      ├─ Create AthenaClient with CSP connection credentials
 │      ├─ Submit StopQueryExecutionRequest to Athena
 │      └─ If cancellation fails:
 │            ├─ Log error details
 │            └─ Throw RuntimeException with error message
 │
 │ 4. Cancellation response:
 │      ├─ If successful: Return 200 OK
 │      └─ If failed: Return 400 Bad Request
 │
 ▼
Query execution cancelled or cancellation error returned
```

### **1. FOCUS Data Lifecycle Management**

├── **Data Ingestion** - Transform CSP-specific billing data to FOCUS standard\
├── **Schema Validation** - Ensure FOCUS compliance and data quality\
├── **Batch Processing** - Efficient bulk insert of usage records\
└── **Event Publishing** - Notify downstream systems of data availability

### **2. Cost Analytics Engine**

├── **Multi-Dimensional Queries** - Service, region, resource, category cost analysis\
├── **Financial Calculations** - Comprehensive cost metrics and savings analysis\
└── **Optimization Insights** - Utilization analysis and recommendation preparation

### **3. Cost Monitoring and Analytics**

├── **Threshold Evaluation** - Integration with budget management for alert processing\
├── **Anomaly Detection** - Cost pattern analysis for unusual spending detection\
└── **Dashboard Integration** - Cost metrics for monitoring dashboards

### **4. Multi-Tenant Data Access**

├── **Organization Filtering** - Organization-based data isolation\
├── **Connection Scoping** - Cost data scoped to specific CSP connections\
├── **Security Context** - User access validation for cost data queries\
└── **Audit Logging** - Complete audit trail of data access and modifications

### **5. Report Generation Engine**

├── **Flexible Filtering** - Multi-dimensional filtering and grouping options\
├── **Export Formats** - Support for PDF, Excel report formats\
├── **Scheduled Generation** - Automated recurring report processing\
└── **Delivery Management** - Email distribution and storage location management

### **6. Athena Query Processing Engine**

├── **Query Execution Management** - SQL query execution on AWS Athena data lakes\
├── **Connection Management** - Dynamic client creation with CSP credentials and configurations\
├── **Timeout Management** - Configurable query timeouts with automatic cancellation\
├── **Result Processing** - Comprehensive result parsing and FOCUS schema integration\
├── **Error Handling** - Detailed error classification and troubleshooting support\
└── **Cost Optimization** - Data scanning metrics for query cost estimation

### **7. Integration Event Processing**

├── **Budget Integration** - Cost data integration with budget management\
├── **Anomaly Detection** - Cost pattern data for anomaly detection\
└── **Recommendations** - Detailed usage data for optimization recommendations

### **8. Performance Optimization**

├── **Query Optimization** - Indexed queries for fast cost analytics\
├── **Batch Processing** - Efficient bulk data processing for large datasets\
└── **Caching Strategy** - Cached aggregations for frequently accessed metrics

---

## **Configuration Properties**

### **Athena Configuration Properties**

The following properties are configurable in `application.properties` for Athena integration:

| Property | Default Value | Description |
|----------|---------------|-------------|
| `aws.athena.workgroup` | `primary` | AWS Athena workgroup for query execution |
| `aws.athena.cost-per-tb-usd` | `5.00` | Cost estimation per TB of data scanned (USD) |
| `aws.athena.api-call-timeout-seconds` | `600` | Maximum timeout for Athena API calls (10 minutes) |
| `aws.athena.api-attempt-timeout-seconds` | `120` | Timeout for individual API call attempts (2 minutes) |
| `aws.athena.query-execution-timeout-minutes` | `15` | Maximum query execution time before automatic cancellation |

### **Configuration Examples**

#### **Development Environment**
```properties
# Athena Configuration for Development
aws.athena.workgroup=development
aws.athena.cost-per-tb-usd=5.00
aws.athena.api-call-timeout-seconds=300
aws.athena.api-attempt-timeout-seconds=60
aws.athena.query-execution-timeout-minutes=10
```

#### **Production Environment**
```properties
# Athena Configuration for Production
aws.athena.workgroup=production
aws.athena.cost-per-tb-usd=5.00
aws.athena.api-call-timeout-seconds=600
aws.athena.api-attempt-timeout-seconds=120
aws.athena.query-execution-timeout-minutes=15
```

### **Timeout Management Strategy**

#### **Query Execution Timeouts**
- **Configurable Timeout**: Default 15 minutes, configurable via `query-execution-timeout-minutes`
- **Automatic Cancellation**: Queries exceeding timeout are automatically cancelled
- **Timeout Detection**: Checked during result retrieval and status calls
- **Error Handling**: Timeout status returned as HTTP 408 Request Timeout

#### **API Call Timeouts**
- **API Call Timeout**: Overall timeout for complete API operations (default: 10 minutes)  
- **API Attempt Timeout**: Timeout for individual retry attempts (default: 2 minutes)
- **Connection Pooling**: Efficient connection reuse for multiple queries
- **Error Recovery**: Automatic retry logic for transient network failures

#### **Cost Management**
- **Data Scanning Metrics**: Tracks data scanned in bytes for cost estimation
- **Cost Per TB**: Configurable cost estimation (default: $5.00 USD per TB)
- **Query Optimization**: Provides scanning metrics to encourage efficient queries
- **Resource Management**: Workgroup-based resource control and billing separation

---
