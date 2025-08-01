# **Cloud Connections Module - Technical Specification Document**

# **Overview**

The Cloud Connections module serves as the integration layer between the FinOps system and multiple cloud service providers (CSPs), enabling secure connection management, credential validation, and multi-tenant cost analytics data ingestion across AWS, Azure, GCP, and other cloud platforms.

**Purpose:** Provide secure multi-tenant CSP integration with automated credential validation, connection grouping, and batch-based architecture for downstream cost analytics processing.

**Scope:** CSP connection management, credential validation, data source configuration, connection grouping, regional and service mappings, and batch-based cost data ingestion.

## **API Endpoints**

### **Cloud Connection Operations**

| Endpoint | Method | Description | Response Codes |
|----------|--------|-------------|----------------|
| `/api/connections` | GET, POST, PUT, DELETE | Manage cloud service provider connections | 200: Success, 400: Bad Request, 401: Unauthorized, 404: Connection not found, 409: Duplicate connection |
| `/api/connections/{id}/test` | POST | Test cloud connection credentials and connectivity | 200: Connection valid, 400: Invalid credentials, 401: Unauthorized, 404: Connection not found, 503: Service unavailable |

### **CSP Management Operations**

| Endpoint | Method | Description | Response Codes |
|----------|--------|-------------|----------------|
| `/api/csps` | GET, POST, PUT, DELETE | Manage cloud service provider configurations | 200: Success, 400: Bad Request, 401: Unauthorized, 404: CSP not found, 409: Duplicate CSP |

### **Connection Group Operations**

| Endpoint | Method | Description | Response Codes |
|----------|--------|-------------|----------------|
| `/api/group-connections` | GET, POST, PUT, DELETE | Manage connection groupings and relationships | 200: Success, 400: Bad Request, 401: Unauthorized, 404: Group not found, 409: Duplicate group |

---

## **Tech Stack**

- **Backend:** Java 21 with Spring Boot 3.5.3 (RESTful APIs)
- **Build Tool:** Maven
- **Database (Development):** H2 in-memory database
- **Database (Production):** PostgreSQL/Supabase with connection pooling
- **Authentication:** JWT tokens with JJWT 0.12.3
- **Security:** Spring Security with role-based access control
- **Cloud Integration:** AWS SDK v2 for credential validation
- **ORM:** Spring Data JPA with Hibernate
- **Schema Version:** finops_v2 across all environments
- **Data Types:** Double for financial amounts, JSONB for metadata

**Database Tables (Schema: finops_v2):**

| Table Name                   | Description                                              | Key Columns                                                                                                                                                                                                                                                             |
| ---------------------------- | -------------------------------------------------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `t_csp_connection`           | Primary CSP connection configurations with credentials   | `id` UUID PRIMARY KEY, `organization_id` UUID, `csp_id` UUID, `data_source` UUID, `name` VARCHAR(25), `description` VARCHAR(2000), `plan_type` UUID, `access_key_id` VARCHAR(255) NOT NULL, `secret_key_id` VARCHAR(255) NOT NULL, `region` UUID                        |
| `t_csp`                      | Cloud service provider definitions (AWS, Azure, GCP)     | `id` UUID PRIMARY KEY, `name` VARCHAR(25)                                                                                                                                                                                                                               |
| `t_group_connection`         | Logical grouping of multiple CSP connections             | `id` UUID PRIMARY KEY, `organization_id` UUID, `name` VARCHAR(25)                                                                                                                                                                                                       |
| `t_groups_info`              | Many-to-many mapping between groups and connections      | `id` UUID PRIMARY KEY, `group_connection_id` UUID, `csp_connection_id` UUID                                                                                                                                                                                             |
| `t_data_source`              | Data source configurations for billing data ingestion    | `id` UUID PRIMARY KEY, `csp_connection_id` UUID, `name` TEXT, `output_bucket_url` VARCHAR(255)                                                                                                                                                                         |
| `t_plan_type`                | CSP-specific pricing plans (Reserved, On-Demand, Spot)   | `id` UUID PRIMARY KEY, `csp_connection_id` UUID, `name` VARCHAR(25)                                                                                                                                                                                                     |
| `t_region`                   | CSP regions for geographical data location               | `id` UUID PRIMARY KEY, `csp_connection_id` UUID, `name` VARCHAR(25)                                                                                                                                                                                                     |
| `t_service`                  | Cloud service mappings for resource identification       | `id` UUID PRIMARY KEY, `csp_connection_id` UUID, `partition_name` VARCHAR(255) NOT NULL, `service_name` VARCHAR(255) NOT NULL, `region_name` VARCHAR(255) NOT NULL, `account_id` VARCHAR(255) NOT NULL, `resource_type` VARCHAR(255) NOT NULL, `resource_id` VARCHAR(255) NOT NULL |
| `t_billing_table`            | Billing table definitions for cost data sources          | `id` UUID PRIMARY KEY, `csp_connection_id` UUID, `name` TEXT                                                                                                                                                                                                            |
| `t_data_catalog`             | Data catalog configurations for metadata management      | `id` UUID PRIMARY KEY, `csp_connection_id` UUID, `name` VARCHAR(50)                                                                                                                                                                                                     |
| `t_data_base`                | Database connection configurations                       | `id` UUID PRIMARY KEY, `csp_connection_id` UUID, `name` VARCHAR(50)                                                                                                                                                                                                     |
| `t_environment_type_uploads` | File upload configurations for environment-specific data | `id` UUID PRIMARY KEY, `csp_connection_id` UUID, `file_name` TEXT, `file_url` TEXT                                                                                                                                                                                      |

**Key Relationships:**

- `t_csp_connection.organization_id` → `t_mt_organization.id` (Multi-tenancy)
- `t_csp_connection.csp_id` → `t_csp.id` (CSP type linkage)
- `t_group_connection.organization_id` → `t_mt_organization.id` (Multi-tenancy)
- `t_groups_info.group_connection_id` → `t_group_connection.id`
- `t_groups_info.csp_connection_id` → `t_csp_connection.id`

**Constraints and Features:**

- UUID primary keys with `gen_random_uuid()` defaults
- All major tables include audit fields: `created_at`, `updated_at`, `created_by`, `updated_by`, `deleted_at`
- Multi-tenant isolation through organization_id foreign keys
- Encrypted credential storage for CSP access keys and secrets

---

## **Request Body Examples**

### **Create CSP Connection Request**

```json
{
  "organizationId": "550e8400-e29b-41d4-a716-446655440000",
  "cspId": "660e8400-e29b-41d4-a716-446655440001",
  "dataSourceId": "770e8400-e29b-41d4-a716-446655440002",
  "name": "Production AWS Account",
  "description": "Main production AWS account for cost analytics",
  "planTypeId": "880e8400-e29b-41d4-a716-446655440003",
  "accessKeyId": "KIYAKAPITANTAOBIN",
  "secretKeyId": "ALIMUTHUKUNYITBAWAHTANGGAANAMOLY",
  "regionId": "990e8400-e29b-41d4-a716-446655440004"
}
```

### **Create Connection Group Request**

```json
{
  "organizationId": "550e8400-e29b-41d4-a716-446655440000",
  "name": "Production Environment Group",
  "connectionIds": [
    "aa0e8400-e29b-41d4-a716-446655440005",
    "bb0e8400-e29b-41d4-a716-446655440006"
  ]
}
```

### **Test Connection Request**

```json
{
  "connectionId": "aa0e8400-e29b-41d4-a716-446655440005",
  "validateCredentials": true,
  "testDataAccess": true
}
```

### **Request Body Field Tables**

#### **Create CSP Connection Request Fields**

| Field          | Type         | Required | Description                                   |
| -------------- | ------------ | -------- | --------------------------------------------- |
| organizationId | UUID         | Yes      | Organization ID for multi-tenant isolation    |
| cspId          | UUID         | Yes      | Cloud service provider ID (AWS, Azure, GCP)   |
| dataSourceId   | UUID         | Yes      | Data source configuration for billing data    |
| name           | String(25)   | Yes      | Descriptive name for the connection           |
| description    | String(2000) | No       | Detailed description of connection purpose    |
| planTypeId     | UUID         | Yes      | Pricing plan type (Reserved, On-Demand, etc.) |
| accessKeyId    | String(255)  | Yes      | CSP access key or client ID                   |
| secretKeyId    | String(255)  | Yes      | CSP secret key or client secret               |
| regionId       | UUID         | Yes      | Default region for connection operations      |

---

## **Response Body Examples**

### **CSP Connection Success Response**

```json
{
  "id": "aa0e8400-e29b-41d4-a716-446655440005",
  "organizationId": "550e8400-e29b-41d4-a716-446655440000",
  "organizationName": "Acme Corporation",
  "cspId": "660e8400-e29b-41d4-a716-446655440001",
  "cspName": "AWS",
  "dataSourceId": "770e8400-e29b-41d4-a716-446655440002",
  "dataSourceName": "S3 Cost Reports",
  "name": "Production AWS Account",
  "description": "Main production AWS account for cost analytics",
  "planTypeId": "880e8400-e29b-41d4-a716-446655440003",
  "planTypeName": "On-Demand",
  "accessKeyId": "AKIA****MPLE",
  "secretKeyId": "****",
  "regionId": "990e8400-e29b-41d4-a716-446655440004",
  "regionName": "us-east-1",
  "connectionStatus": "active",
  "lastValidated": "2024-01-15T10:30:00Z",
  "createdAt": "2024-01-15T10:30:00Z",
  "updatedAt": "2024-01-15T10:30:00Z",
  "createdBy": "cc0e8400-e29b-41d4-a716-446655440007",
  "updatedBy": "cc0e8400-e29b-41d4-a716-446655440007"
}
```

### **Connection Test Response**

```json
{
  "connectionId": "aa0e8400-e29b-41d4-a716-446655440005",
  "validationResult": {
    "success": true,
    "accountId": "123456789012",
    "accountArn": "arn:aws:iam::123456789012:user/finops-user",
    "userId": "AIDACKCEVSQ6C2EXAMPLE",
    "region": "us-east-1",
    "validatedAt": "2024-01-15T10:30:00Z"
  },
  "dataAccessTest": {
    "canReadBillingData": true,
    "dataSourceAccessible": true,
    "lastDataUpdate": "2024-01-15T09:00:00Z"
  },
  "errors": []
}
```

### **Connection Group Response**

```json
{
  "id": "dd0e8400-e29b-41d4-a716-446655440008",
  "name": "Production Environment Group",
  "organization": {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "organizationName": "Acme Corporation",
    "createdAt": "2024-01-01T00:00:00Z",
    "updatedAt": "2024-01-01T00:00:00Z"
  },
  "connectionCount": 2,
  "connections": [
    {
      "id": "aa0e8400-e29b-41d4-a716-446655440005",
      "name": "Production AWS Account",
      "cspName": "AWS",
      "connectionStatus": "active"
    },
    {
      "id": "bb0e8400-e29b-41d4-a716-446655440006",
      "name": "Production Azure Subscription",
      "cspName": "Azure",
      "connectionStatus": "active"
    }
  ],
  "createdAt": "2024-01-15T10:30:00Z",
  "updatedAt": "2024-01-15T10:30:00Z",
  "createdBy": "cc0e8400-e29b-41d4-a716-446655440007",
  "updatedBy": "cc0e8400-e29b-41d4-a716-446655440007"
}
```

### **Response Field Tables**

#### **CSP Connection Response Fields**

| Field            | Type     | Description                                        |
| ---------------- | -------- | -------------------------------------------------- |
| id               | UUID     | Unique connection identifier                       |
| organizationId   | UUID     | Organization ID for multi-tenant isolation         |
| organizationName | String   | Organization display name                          |
| cspName          | String   | Cloud service provider name (AWS, Azure, GCP)      |
| dataSourceName   | String   | Data source configuration name                     |
| accessKeyId      | String   | Masked access key (first 4 + \*\*\*\* + last 4)    |
| secretKeyId      | String   | Fully masked secret key (\*\*\*\*)                 |
| connectionStatus | String   | Connection health status (active/inactive/error)   |
| lastValidated    | DateTime | Timestamp of last successful credential validation |

---

## **Business Logic Explanation**

### **Core Functionality**

The Cloud Connections module implements secure multi-CSP integration with the following core capabilities:

1. **Multi-Tenant CSP Management** - Organization-scoped connection isolation for secure multi-tenancy
2. **Automated Credential Validation** - Validation using CSP APIs (AWS STS, Azure ARM, GCP IAM)
3. **Connection Grouping** - Logical organization of multiple CSP connections for unified management
4. **Service Resource Mapping** - Detailed resource identification for granular cost allocation

### **Connection Management Flow**

The system implements comprehensive connection lifecycle management with security and validation:

1. **Connection Creation** - Validates organization membership, assembles entity relationships, and persists connection
2. **Credential Validation** - Uses CSP-specific APIs to verify access keys and permissions

### **Multi-CSP Integration Architecture**

#### **AWS Integration**

- **Credential Validation** - Uses AWS STS GetCallerIdentity API for validation
- **Account Information** - Retrieves account ID, ARN, and user details
- **Region Support** - Validates region accessibility and service availability
- **Security Features** - Credential masking in logs, secure storage, API best practices

#### **Azure Integration** (Future Development)

- **Authentication** - Azure Service Principal and Managed Identity validation
- **Subscription Management** - Azure subscription and resource group mapping
- **Regional Configuration** - Azure region and availability zone support

#### **GCP Integration** (Future Development)

- **Service Account Validation** - Framework for GCP service account key validation
- **Project Management** - GCP project and billing account association

### **Connection Grouping Strategy**

#### **Group Management**

- **Logical Organization** - Groups enable unified management of related connections
- **Bulk Operations** - Single operations across multiple cloud accounts simultaneously
- **Cost Aggregation** - Combined cost analytics across grouped connections
- **Access Control** - Organization-scoped groups maintain multi-tenant security

### **Data Source Integration**

#### **FOCUS-Compliant Data Sources**

- **Billing Data Sources** - S3 buckets, Azure Storage, GCP Cloud Storage for cost reports
- **Output Configuration** - Configurable output bucket URLs for processed cost data

#### **Service Resource Mapping**

- **Granular Identification** - Partition, service, region, account, resource type, and resource ID mapping
- **Cost Allocation** - Enables detailed cost attribution and chargeback capabilities
- **Resource Optimization** - Supports resource-level cost optimization recommendations
- **Compliance Tracking** - Resource tagging and metadata for governance compliance

### **Security & Data Protection**

#### **Credential Security**

- **Encrypted Storage** - CSP credentials encrypted at rest in database
- **Masked Logging** - Access keys and secrets masked in application logs
- **Validation-Only Access** - Credentials used only for validation, not stored in memory
- **Rotation Support** - Framework for credential rotation and expiration handling

#### **Multi-Tenant Isolation**

- **Organization Boundaries** - All connections scoped to specific organizations
- **API-Level Filtering** - Automatic organization-based query filtering
- **Access Control** - Role-based permissions for connection management
- **Audit Compliance** - Complete audit trail for all connection operations

---

## **API Call Flow Description**

```
CSP Connection Creation Flow
Admin User
 │
 │ 1. Submit create connection request (POST /api/connections)
 ▼
ConnectionController.createConnection
 │
 │ 2. Extract CreateConnectionRequest with CSP details
 │
 │ 3. Validate required fields and organization membership
 │      └─ If validation fails:
 │            ├─ Return 400 Bad Request
 │            └─ Include field-specific validation errors
 │
 │ 4. Call ConnectionService.createConnection
 │      ├─ Validate organization and CSP references
 │      ├─ Assemble CspConnection entity with relationships
 │      ├─ Persist connection to t_csp_connection table
 │      └─ If persistence fails:
 │            ├─ Return 500 Internal Server Error
 │            └─ Include database constraint errors
 │
 │ 5. AWS Credential Validation (if CSP is AWS):
 │      ├─ Call AwsCredentialValidationService.validateCredentials
 │      ├─ Create AWS STS client with provided credentials
 │      ├─ Call GetCallerIdentity API to verify access
 │      ├─ Extract account ID, ARN, and user details
 │      └─ If validation fails:
 │            ├─ Log connection failure details
 │            ├─ Update connection status to 'error'
 │            └─ Return connection with error status
 │
 │ 6. Successful connection creation:
 │      ├─ Log connection success details
 │      ├─ Update connection status to 'active'
 │      ├─ Set lastValidated timestamp
 │      ├─ Log connection success for audit trail
 │      └─ Return CspConnectionDto with complete details
 │
 ▼
Connection ready for cost data ingestion

Connection Test Flow
User
 │
 │ 1. Request connection test (POST /api/connections/{id}/test)
 ▼
ConnectionController.testConnection
 │
 │ 2. Retrieve existing connection by ID
 │      └─ If not found or access denied:
 │            ├─ Return 404 Not Found or 403 Forbidden
 │            └─ Include appropriate error message
 │
 │ 3. Validate current user has access to connection's organization
 │
 │ 4. Perform credential validation:
 │      ├─ Call appropriate CSP validation service
 │      ├─ Test API connectivity and permissions
 │      ├─ Verify data source accessibility
 │      └─ Check billing data availability
 │
 │ 5. Generate comprehensive test results:
 │      ├─ Credential validation results
 │      ├─ Data access test results
 │      ├─ Last data update timestamps
 │      ├─ Any detected errors or warnings
 │      └─ Connection health status
 │
 │ 6. Update connection metadata:
 │      ├─ Set lastValidated timestamp
 │      ├─ Update connection status based on test results
 │      └─ Log test results for monitoring
 │
 ▼
Test results returned to user for connection health assessment
```

### **1. Connection Management Controller Flow**

├── **Request Processing** - Extract and validate connection parameters\
├── **ConnectionService.createConnection()** - Core connection creation orchestration\
├── **Entity Assembly** - Build CspConnection with all required relationships\
└── **Credential Validation** - Trigger CSP-specific validation services

### **2. CSP Credential Validation**

├── **AwsCredentialValidationService** - AWS STS-based credential verification\
├── **Credential Security** - Masked logging and secure handling of secrets\
├── **Account Information Extraction** - Retrieve account details and permissions\
└── **Status Update** - Update connection status based on validation results

### **3. Batch-Based Integration**

├── **Connection Status Updates** - Update connection status for downstream batch processing\
├── **Connection Failure Logging** - Log validation failures for monitoring systems\
├── **Batch Processing** - Scheduled cost data processing integration\
└── **Audit Trail** - Complete activity history for compliance and troubleshooting

### **5. Connection Group Management**

├── **Group Creation** - Create logical groups of related connections\
├── **Bulk Operations** - Perform operations across multiple connections simultaneously\
├── **Cost Aggregation** - Enable unified cost analytics across grouped connections\
└── **Organization Scoping** - Maintain multi-tenant boundaries for groups

### **6. Resource Mapping Integration**

├── **Service Entity Management** - Detailed resource identification and mapping\
├── **Regional Configuration** - Geographic data location and compliance\
├── **Plan Type Assignment** - Pricing plan association for cost optimization\
└── **Data Source Configuration** - Billing data ingestion endpoint setup

### **7. Monitoring & Health Checks**

├── **Connection Status Tracking** - Monitor connection health and availability\
├── **Validation Scheduling** - Periodic re-validation of stored credentials\
├── **Error Detection** - Identify and alert on connection failures\
└── **Performance Metrics** - Track validation response times and success rates

---
