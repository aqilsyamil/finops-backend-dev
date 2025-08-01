# **Cost Allocation module - Technical Specification Document**

# **Overview**

The Cost Allocation module serves as the central metadata management system for the FinOps platform, providing unified resource tagging, key-value metadata storage, and comprehensive resource organization capabilities across multiple cloud service providers with complete audit trails and multi-tenant support.

**Purpose:** Enable comprehensive resource tagging and metadata management across multiple CSPs, provide key-value metadata storage for flexible resource categorization, support cost attribution through resource tagging, and maintain complete audit trails for compliance and governance.

**Scope:** CSP resource tagging, key-value metadata management, tag status tracking, compliance, service-level resource organization, audit trail maintenance, event-driven integration, and support for cost analytics and resource optimization through standardized tagging.

## **API Endpoints**

### **Tag Management Operations**

| Endpoint | Method | Description | Response Codes |
|----------|--------|-------------|----------------|
| `/api/tags/{id}` | GET, POST, PUT, DELETE | Manage tag entities and configurations | 200: Success, 400: Bad Request, 401: Unauthorized, 404: Tag not found, 409: Duplicate tag |

### **Connection-based Tag Operations**

| Endpoint | Method | Description | Response Codes |
|----------|--------|-------------|----------------|
| `/api/tags/csp-connection/{cspConnectionId}` | GET | Retrieve all tags for specific CSP connection | 200: Success, 401: Unauthorized, 404: Connection not found |

### **Service-based Tag Operations**

| Endpoint | Method | Description | Response Codes |
|----------|--------|-------------|----------------|
| `/api/tags/service/{serviceName}` | GET | Get tags by cloud service name | 200: Success, 401: Unauthorized, 404: Service not found |
| `/api/tags/service-id/{serviceId}` | GET | Get tags by service identifier | 200: Success, 401: Unauthorized, 404: Service not found |

### **Tag Status Operations**

| Endpoint | Method | Description | Response Codes |
|----------|--------|-------------|----------------|
| `/api/tags/{tagId}/status` | GET, POST, PUT, DELETE | Manage tag key-value status configurations | 200: Success, 400: Bad Request, 401: Unauthorized, 404: Tag not found |

---

## **Tech Stack**

- **Backend:** Java 21 with Spring Boot 3.5.3 (RESTful APIs)
- **Build Tool:** Maven
- **Database (Development):** H2 in-memory database
- **Database (Production):** Amazon RDS PostgreSQL
- **Authentication:** JWT tokens with JJWT 0.12.3
- **Security:** Spring Security with role-based access control
- **ORM:** Spring Data JPA with Hibernate
- **Schema Version:** finops_v2 across all environments
- **Data Types:** Text for metadata, UUID for relationships

**Database Tables (Schema: finops_v2):**

| Table Name | Description | Key Columns |
| --- | --- | --- |
| `t_tags` | Primary tag entities linking resources to CSP connections | `id` UUID PRIMARY KEY, `csp_connection_id` UUID NOT NULL, `service_id` UUID NULL, `created_at` TIMESTAMPTZ DEFAULT now(), `updated_at` TIMESTAMPTZ, `created_by` UUID, `updated_by` UUID, `deleted_at` TIMESTAMPTZ |
| `t_tag_status` | Key-value pairs for flexible metadata storage | `id` UUID PRIMARY KEY, `tags_id` UUID NOT NULL, `name` VARCHAR(25), `value` VARCHAR(255), `created_at` TIMESTAMPTZ DEFAULT now(), `updated_at` TIMESTAMPTZ, `created_by` UUID, `updated_by` UUID, `deleted_at` TIMESTAMPTZ |

**Key Relationships:**

- `t_tags.csp_connection_id` → `t_csp_connection.id` (Required foreign key)
- `t_tags.service_id` → `t_service.id` (Optional foreign key)
- `t_tags.created_by|updated_by` → `t_mt_users.id` (Audit trail)
- `t_tag_status.tags_id` → `t_tags.id` (Required foreign key)

**Constraints and Features:**

- UUID primary keys with `gen_random_uuid()` defaults
- Soft delete support with `deleted_at` timestamps
- Complete audit trail with created/updated by and at timestamps
- Multi-tenant isolation through CSP connection relationships

---

## **Request Body Examples**

### **Create Tag Request**

```json
{
  "cspConnectionId": "550e8400-e29b-41d4-a716-446655440000",
  "serviceName": "Amazon EC2",
  "userId": "660e8400-e29b-41d4-a716-446655440001",
  "metadata": {
    "resourceName": "web-server-production",
    "description": "Production web server resource tagging"
  },
  "tagStatuses": [
    {
      "name": "Environment",
      "value": "Production"
    },
    {
      "name": "Application",
      "value": "WebApp"
    },
    {
      "name": "Owner",
      "value": "Platform Team"
    },
    {
      "name": "CostCenter",
      "value": "Engineering"
    },
    {
      "name": "Project",
      "value": "CustomerPortal"
    }
  ]
}
```

### **Update Tag Request**

```json
{
  "serviceName": "Amazon EC2",
  "userId": "660e8400-e29b-41d4-a716-446655440001",
  "metadata": {
    "resourceName": "web-server-production-v2",
    "description": "Updated production web server resource tagging"
  },
  "updatedTagStatuses": [
    {
      "name": "Version",
      "value": "2.1.0"
    },
    {
      "name": "LastUpdated",
      "value": "2024-01-15"
    }
  ]
}
```

### **Create Tag Status Request**

```json
{
  "tagId": "770e8400-e29b-41d4-a716-446655440002",
  "name": "Compliance",
  "value": "PCI-DSS",
  "userId": "660e8400-e29b-41d4-a716-446655440001",
  "metadata": {
    "validationDate": "2024-01-15",
    "validatedBy": "Security Team"
  }
}
```

### **Bulk Tag Management Request**

```json
{
  "operation": "bulk_create",
  "cspConnectionId": "550e8400-e29b-41d4-a716-446655440000",
  "userId": "660e8400-e29b-41d4-a716-446655440001",
  "tags": [
    {
      "serviceName": "Amazon EC2",
      "tagStatuses": [
        { "name": "Environment", "value": "Production" },
        { "name": "Team", "value": "Platform" }
      ]
    },
    {
      "serviceName": "Amazon RDS",
      "tagStatuses": [
        { "name": "Environment", "value": "Production" },
        { "name": "Database", "value": "Primary" }
      ]
    }
  ]
}
```

### **Request Body Field Tables**

#### **Create Tag Request Fields**

| Field                 | Type        | Required | Description                                            |
| --------------------- | ----------- | -------- | ------------------------------------------------------ |
| cspConnectionId       | UUID        | Yes      | CSP connection identifier for resource association     |
| serviceName           | String      | No       | Cloud service name (resolved to serviceId internally)  |
| serviceId             | UUID        | No       | Direct service identifier (alternative to serviceName) |
| userId                | UUID        | Yes      | User identifier for audit trail                        |
| metadata.resourceName | String      | No       | Descriptive resource name for identification           |
| metadata.description  | String      | No       | Detailed description of tagging purpose                |
| tagStatuses           | Array       | No       | Initial key-value pairs for the tag                    |
| tagStatuses[].name    | String(25)  | Yes      | Tag key name                                           |
| tagStatuses[].value   | String(255) | Yes      | Tag value                                              |

---

## **Response Body Examples**

### **Comprehensive Tag Response**

```json
{
  "id": "880e8400-e29b-41d4-a716-446655440003",
  "cspConnectionId": "550e8400-e29b-41d4-a716-446655440000",
  "cspConnectionName": "Production AWS Account",
  "serviceId": "990e8400-e29b-41d4-a716-446655440004",
  "serviceName": "Amazon EC2",
  "serviceCategory": "Compute",
  "resourceContext": {
    "resourceType": "EC2-Instance",
    "resourceArn": "arn:aws:ec2:us-east-1:123456789012:instance/i-1234567890abcdef0",
    "region": "us-east-1",
    "accountId": "123456789012"
  },
  "tagStatuses": [
    {
      "id": "aa0e8400-e29b-41d4-a716-446655440005",
      "tagId": "880e8400-e29b-41d4-a716-446655440003",
      "name": "Environment",
      "value": "Production",
      "createdAt": "2024-01-15T10:30:00Z",
      "updatedAt": "2024-01-15T10:30:00Z",
      "createdBy": "platform-admin",
      "updatedBy": "platform-admin"
    },
    {
      "id": "bb0e8400-e29b-41d4-a716-446655440006",
      "tagId": "880e8400-e29b-41d4-a716-446655440003",
      "name": "Application",
      "value": "WebApp",
      "createdAt": "2024-01-15T10:30:00Z",
      "updatedAt": "2024-01-15T10:30:00Z",
      "createdBy": "platform-admin",
      "updatedBy": "platform-admin"
    },
    {
      "id": "cc0e8400-e29b-41d4-a716-446655440007",
      "tagId": "880e8400-e29b-41d4-a716-446655440003",
      "name": "Owner",
      "value": "Platform Team",
      "createdAt": "2024-01-15T10:30:00Z",
      "updatedAt": "2024-01-15T10:30:00Z",
      "createdBy": "platform-admin",
      "updatedBy": "platform-admin"
    },
    {
      "id": "dd0e8400-e29b-41d4-a716-446655440008",
      "tagId": "880e8400-e29b-41d4-a716-446655440003",
      "name": "CostCenter",
      "value": "Engineering",
      "createdAt": "2024-01-15T10:30:00Z",
      "updatedAt": "2024-01-15T10:30:00Z",
      "createdBy": "platform-admin",
      "updatedBy": "platform-admin"
    }
  ],
  "organizationContext": {
    "organizationId": "ee0e8400-e29b-41d4-a716-446655440009",
    "organizationName": "Acme Corporation"
  },
  "auditTrail": {
    "createdAt": "2024-01-15T10:30:00Z",
    "updatedAt": "2024-01-15T10:30:00Z",
    "createdBy": "platform-admin",
    "updatedBy": "platform-admin",
    "version": 1,
    "changeHistory": [
      {
        "timestamp": "2024-01-15T10:30:00Z",
        "action": "CREATED",
        "userId": "platform-admin",
        "changes": "Initial tag creation with 4 tag statuses"
      }
    ]
  },
  "integrationStatus": {
    "usedInBudgets": true,
    "usedInRecommendations": true,
    "usedInAnomalyDetection": false
  }
}
```

### **Tag Collection Response (Service-Level)**

```json
{
  "serviceName": "Amazon EC2",
  "serviceId": "990e8400-e29b-41d4-a716-446655440004",
  "cspConnectionId": "550e8400-e29b-41d4-a716-446655440000",
  "tags": [
    {
      "id": "880e8400-e29b-41d4-a716-446655440003",
      "tagStatusCount": 4,
      "commonTags": {
        "Environment": "Production",
        "Owner": "Platform Team"
      },
      "createdAt": "2024-01-15T10:30:00Z"
    }
  ],
  "aggregatedStatistics": {
    "totalTags": 15,
    "uniqueTagKeys": 8,
    "mostCommonTagKeys": [
      { "key": "Environment", "count": 15 },
      { "key": "Owner", "count": 14 },
      { "key": "CostCenter", "count": 12 }
    ],
    "mostCommonTagValues": [
      { "value": "Production", "count": 12 },
      { "value": "Platform Team", "count": 8 },
      { "value": "Engineering", "count": 10 }
    ]
  },
  "complianceStatus": {
    "missingRequiredTags": ["Compliance", "DataClassification"]
  }
}
```

### **Tag Status Response**

```json
{
  "id": "aa0e8400-e29b-41d4-a716-446655440005",
  "tagId": "880e8400-e29b-41d4-a716-446655440003",
  "name": "Environment",
  "value": "Production",
  "validation": {
    "keyValidation": {
      "isValid": true,
      "maxLength": 25,
      "currentLength": 11
    },
    "valueValidation": {
      "isValid": true,
      "maxLength": 255,
      "currentLength": 10
    }
  },
  "metadata": {
    "dataType": "String",
    "category": "Environment",
    "required": true,
    "allowedValues": ["Development", "Staging", "Production"]
  },
  "usage": {
    "linkedToBudgets": true,
    "linkedToRecommendations": false,
    "linkedToAnomalyDetection": true
  },
  "createdAt": "2024-01-15T10:30:00Z",
  "updatedAt": "2024-01-15T10:30:00Z",
  "createdBy": "platform-admin",
  "updatedBy": "platform-admin"
}
```

### **Response Field Tables**

#### **Tag Response Fields**

| Field               | Type   | Description                           |
| ------------------- | ------ | ------------------------------------- |
| id                  | UUID   | Unique tag identifier                 |
| cspConnectionId     | UUID   | Associated CSP connection             |
| cspConnectionName   | String | CSP connection display name           |
| serviceId           | UUID   | Associated service identifier         |
| serviceName         | String | Cloud service name                    |
| tagStatuses         | Array  | Key-value pairs associated with tag   |
| resourceContext     | Object | Cloud resource context information    |
| organizationContext | Object | Multi-tenant organization information |
| auditTrail          | Object | Complete audit and change history     |
| integrationStatus   | Object | Integration status with other modules |

---

## **Business Logic Explanation**

### **Core Functionality**

The Cost Allocation module implements comprehensive metadata management with the following core capabilities:

1. **CSP Resource Tagging** - Unified tagging interface across AWS, Azure, GCP, and other cloud providers
2. **Flexible Key-Value Metadata** - Two-tier architecture supporting complex metadata structures and relationships
3. **Service-Level Resource Organization** - Integration with cloud services for granular resource categorization
4. **Complete Audit Trail Management** - Comprehensive tracking of all tagging operations with user attribution
5. **Multi-Tenant Data Isolation** - Organization-based data separation through CSP connection relationships
6. **Event-Driven Integration** - Domain events for seamless integration with cost analytics and optimization modules

### **Two-Tier Tagging Architecture**

The system implements a sophisticated two-tier architecture for maximum flexibility:

1. **Primary Tags Layer** - Top-level resource identification and CSP connection linkage
2. **Tag Status Layer** - Flexible key-value pairs enabling complex metadata structures
3. **Relationship Management** - Clean separation of concerns with clear entity relationships
4. **Scalability** - Architecture supports high-volume tagging scenarios with efficient querying
5. **Extensibility** - Framework supports future enhancements without schema changes

### **Service Integration and Resolution**

#### **Service Mapping Strategy**

- **Name-Based Resolution** - Current implementation uses service name lookup for user convenience
- **Direct ID Resolution** - Alternative service ID resolution for programmatic access
- **Fallback Handling** - Graceful handling of service resolution failures
- **Multi-Match Resolution** - Takes first match when multiple services share names

#### **CSP Connection Integration**

- **Required Association** - All tags must be linked to valid CSP connections
- **Organization Context** - Inherits multi-tenant context through CSP connection relationships
- **Connection Validation** - Validates CSP connection existence and accessibility
- **Data Isolation** - Ensures proper data separation across organizations

### **Metadata Validation and Management**

#### **Tag Key Validation**

- **Length Constraints** - Maximum 25 characters for tag keys with validation
- **Character Validation** - Supports alphanumeric and common special characters
- **Uniqueness Enforcement** - Prevents duplicate tag keys within tag scope
- **Immutable Design** - TagKey value object ensures consistency and validation

#### **Tag Value Management**

- **Extended Length Support** - Maximum 255 characters for comprehensive metadata storage
- **Flexible Content** - Supports various data types and formats in string representation
- **Validation Framework** - Extensible validation system for custom business rules
- **Immutable Design** - TagValue value object ensures data integrity

### **Audit Trail and Compliance**

#### **Comprehensive Audit Logging**

- **User Attribution** - Complete tracking of who created and modified tags
- **Temporal Tracking** - Precise timestamps for all tagging operations
- **Change History** - Full audit trail of tag modifications and deletions
- **Soft Delete Support** - Logical deletion preserves audit history and enables recovery

#### **Compliance Features**

- **Data Retention** - Configurable data retention policies for regulatory compliance
- **Access Logging** - Complete logging of tag access for security auditing
- **Version Control** - Tag versioning support for change management
- **Data Privacy** - GDPR and privacy regulation compliance through proper data handling

### **Event-Driven Architecture**

#### **Domain Events**

- **TagCreatedEvent** - Published when new tags are created with complete context
- **TagUpdatedEvent** - Published when tag modifications occur with change details
- **Integration Events** - Supports integration with external systems and workflows

#### **Event Processing**

- **Asynchronous Processing** - Non-blocking event processing for performance
- **Event Ordering** - Proper event sequencing for consistency
- **Error Handling** - Robust error handling and retry mechanisms
- **External Integration** - Framework for external system notifications and webhooks

---

## **API Call Flow Description**

```
Tag Creation and Management Flow
DevOps Engineer
 │
 │ 1. Submit create tag request (POST /api/tags)
 ▼
TagController.createTag
 │
 │ 2. Extract tag creation parameters (cspConnectionId, serviceName, userId)
 │
 │ 3. Validate request parameters and user permissions:
 │      ├─ Validate CSP connection exists and is accessible
 │      ├─ Validate user exists and has tagging permissions
 │      ├─ Validate service name if provided
 │      └─ If validation fails:
 │            ├─ Return 400 Bad Request
 │            └─ Include detailed validation errors
 │
 │ 4. Call TagService.createTag with comprehensive entity building:
 │      ├─ Resolve CspConnection entity from repository
 │      ├─ Resolve Services entity by name (if provided)
 │      │    └─ Uses first match from servicesRepository.findByServiceName()
 │      ├─ Resolve User entity for audit trail
 │      ├─ Create Tags entity with all relationships
 │      └─ If entity resolution fails:
 │            ├─ Return 404 Not Found for missing entities
 │            └─ Include specific entity resolution errors
 │
 │ 5. Persist tag entity and create initial tag statuses:
 │      ├─ Persist Tags entity to t_tags table with audit fields
 │      ├─ Create TagStatus entities for provided key-value pairs
 │      ├─ Validate tag key/value constraints using value objects
 │      ├─ Persist TagStatus entities to t_tag_status table
 │      └─ If persistence fails:
 │            ├─ Rollback transaction
 │            ├─ Return 500 Internal Server Error
 │            └─ Include constraint violation details
 │
 │ 6. Post-creation processing and event publishing:
 │      ├─ Publish TagCreatedEvent with complete tag context
 │      ├─ Log tag creation for audit trail
 │      ├─ Trigger integration with cost analytics module
 │      ├─ Update organization tagging statistics
 │      └─ Return comprehensive TagDto response
 │
 ▼
Tag successfully created and ready for cost attribution

Tag Status Management Flow
Platform Administrator
 │
 │ 1. Add new key-value pair (POST /api/tags/{tagId}/status)
 ▼
TagController.createTagStatus
 │
 │ 2. Extract tag status parameters (tagId, name, value, userId)
 │
 │ 3. Validate tag status creation parameters:
 │      ├─ Validate parent tag exists and is accessible
 │      ├─ Validate user permissions for tag modification
 │      ├─ Validate tag key constraints using TagKey value object
 │      ├─ Validate tag value constraints using TagValue value object
 │      ├─ Check for duplicate key within tag scope
 │      └─ If validation fails:
 │            ├─ Return 400 Bad Request for constraint violations
 │            ├─ Return 404 Not Found for missing parent tag
 │            └─ Return 409 Conflict for duplicate keys
 │
 │ 4. Create and persist tag status entity:
 │      ├─ Create TagStatus entity with validated key-value pair
 │      ├─ Link to parent Tags entity via tags_id foreign key
 │      ├─ Set audit fields (created_by, created_at)
 │      ├─ Persist to t_tag_status table
 │      └─ If persistence fails:
 │            ├─ Return 500 Internal Server Error
 │            └─ Include database constraint details
 │
 │ 5. Integration and event processing:
 │      ├─ Update parent tag's updated_at timestamp
 │      ├─ Publish TagUpdatedEvent with modification details
 │      ├─ Trigger cost analytics data refresh
 │      ├─ Update tag usage statistics
 │      └─ Return TagStatusDto with complete context
 │
 ▼
Tag status successfully added and available for cost analysis

Service-Level Tag Aggregation Flow
Financial Analyst
 │
 │ 1. Request service tags (GET /api/tags/service/{serviceName})
 ▼
TagController.getTagsByServiceName
 │
 │ 2. Extract service name and validate access permissions
 │
 │ 3. Execute service-level tag aggregation:
 │      ├─ Query tags by service name using repository method
 │      ├─ Apply organization-based filtering for multi-tenancy
 │      ├─ Load associated tag statuses for each tag
 │      ├─ Calculate aggregated statistics (tag counts, common keys/values)
 │      └─ If no tags found:
 │            ├─ Return empty collection with metadata
 │            └─ Include service context information
 │
 │ 4. Enhanced response processing:
 │      ├─ Convert Tag entities to comprehensive TagDto objects
 │      ├─ Include all associated TagStatus entities
 │      ├─ Calculate tag usage statistics and compliance metrics
 │      ├─ Include integration status with other modules
 │      ├─ Add organization context and permissions
 │      └─ Format response with pagination and metadata
 │
 │ 5. Response enhancement:
 │      ├─ Identify missing required tags for compliance
 │      ├─ Include cost attribution context where available
 │      └─ Add resource usage context
 │
 ▼
Comprehensive service-level tagging analysis delivered
```

### **1. Tag Entity Management**

├── **Entity Resolution** - Multi-entity validation and relationship building with comprehensive error handling \
├── **Service Resolution** - Flexible service identification via name or ID with fallback strategies\
├── **Audit Trail Integration** - Complete user attribution and temporal tracking for compliance\
└── **Transaction Management** - Atomic operations ensuring data consistency across entity relationships

### **2. Key-Value Metadata Processing**

├── **Value Object Validation** - Immutable value objects ensuring tag key and value constraints \
├── **Flexible Schema** - Two-tier architecture supporting complex metadata structures \
├── **Constraint Enforcement** - Database and application-level constraint validation \
└── **Duplicate Prevention** - Business logic preventing duplicate keys within tag scope

### **3. Multi-Tenant Data Management**

├── **Organization Isolation** - Automatic data separation through CSP connection relationships \
├── **Permission Validation** - User permission validation for tag creation and modification \
├── **Access Control** - Role-based access control integration for secure tag management \
└── **Data Privacy** - GDPR-compliant data handling with soft delete and audit preservation

### **4. Service Integration Layer**

├── **CSP Connection Validation** - Ensures valid CSP connections for all tagging operations\
├── **Service Mapping** - Flexible service resolution supporting name-based and ID-based lookup\
├── **Cross-Module Integration** - Event-driven integration with cost analytics and optimization\
└── **Resource Context** - Rich resource context for enhanced cost attribution and analysis

### **5. Event-Driven Integration**

├── **Domain Event Processing** - Comprehensive event publishing for tag lifecycle operations \
├── **Asynchronous Processing** - Non-blocking event handling for system performance \
└── **External Integration** - Framework for webhook and external system notifications \

### **6. Compliance and Audit Systems**

├── **Comprehensive Audit Trail** - Complete tracking of all tagging operations with user attribution \
├── **Change History** - Detailed change logging with rollback capabilities \
├── **Compliance Tracking** - Required tag coverage analysis \
└── **Data Retention** - Configurable retention policies with regulatory compliance support

---
