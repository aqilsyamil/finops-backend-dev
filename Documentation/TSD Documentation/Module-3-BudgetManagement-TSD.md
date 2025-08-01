# **Budget Management Module - Technical Specification Document**

# **Overview**

The Budget Management module is a critical component of the FinOps platform that handles budget creation, threshold monitoring, and alert management for financial oversight across cloud service providers (CSPs). It provides comprehensive budget lifecycle management with multi-tenant isolation and threshold monitoring.

**Purpose:** Enable organizations to create, manage, and monitor cloud spending budgets with automated threshold alerts, service-specific budget allocation, and comprehensive financial governance across multiple CSPs.

**Scope:** Budget creation and lifecycle management, threshold rule definition, alert recipient management, service-specific budget allocation, multi-tenant budget isolation, and event-driven alert processing.

## **API Endpoints**

### **Budget Management Operations**

| Endpoint | Method | Description | Response Codes |
|----------|--------|-------------|----------------|
| `/api/budgets` | GET, POST, PUT, DELETE | Manage budget entities and configurations | 200: Success, 400: Bad Request, 401: Unauthorized, 404: Budget not found, 409: Duplicate budget |
| `/api/budgets/organization/{organizationId}` | GET | Retrieve all budgets for specific organization | 200: Success, 401: Unauthorized, 404: Organization not found |
| `/api/budgets/organization/{organizationId}/connection/{cspConnectionId}` | GET | Get budgets for specific CSP connection within organization | 200: Success, 401: Unauthorized, 404: Connection not found |

---

## **Tech Stack**

- **Backend:** Java 21 with Spring Boot 3.5.3 (RESTful APIs)
- **Build Tool:** Maven
- **Database (Development):** H2 in-memory database / PostgreSQL
- **Database (Production):** Amazon RDS PostgreSQL
- **Authentication:** JWT tokens with JJWT 0.12.3
- **Security:** Spring Security with role-based access control
- **Documentation:** SpringDoc OpenAPI 2.2.0 (Swagger UI)
- **Cloud Integration:** AWS SDK v2 for credential validation
- **ORM:** Spring Data JPA with Hibernate
- **Utilities:** Lombok, Hypersistence Utils for Hibernate
- **Schema Version:** finops_v2 across all environments
- **Data Types:** Double for financial amounts, JSONB for metadata

**Database Tables (Schema: finops_v2):**

| Table Name                    | Description                                               | Key Columns                                                                                                                                                               |
| ----------------------------- | --------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `t_budgets`                   | Main budget definitions with organization and CSP linkage | `id` UUID, `organization_id` UUID, `csp_connection_id` UUID, `name` VARCHAR(25), `amount` FLOAT8, `time_range` VARCHAR(25), `renewal_type` VARCHAR(25), `budget_type` VARCHAR(25), `start_month` DATE |
| `t_budget_threshold_rules`    | Alert threshold definitions for budget monitoring         | `id` UUID, `budget_id` UUID, `percentage` FLOAT8, `amount` FLOAT8, `trigger_type` VARCHAR(25)                                                                             |
| `t_budget_alerts_recipients`  | Email recipients for budget alert notifications           | `id` UUID, `budget_id` UUID, `email` VARCHAR(255) NOT NULL                                                                                                                |
| `t_budget_services_selection` | Service-specific budget allocation and monitoring         | `id` UUID, `budget_id` UUID, `service_id` UUID, `selected` BOOLEAN NOT NULL                                                                                               |

**Table Constraints:**

- `t_budgets.budget_type` CHECK: Must be 'fixed'
- `t_budgets.renewal_type` CHECK: Must be 'recurring'
- `t_budgets.time_range` CHECK: Must be 'monthly', 'quarterly', or 'yearly'
- `t_budget_threshold_rules.trigger_type` CHECK: Must be 'actual cost' or 'actual spend'

---

## **Request Body Examples**

### **Create Budget Request**

```json
{
  "organizationId": "550e8400-e29b-41d4-a716-446655440000",
  "cspConnectionId": "660e8400-e29b-41d4-a716-446655440001",
  "name": "Q1 Production Budget",
  "timeRange": "quarterly",
  "renewalType": "recurring",
  "startMonth": "2024-01-01",
  "budgetType": "fixed",
  "amount": 50000.0,
  "selectedServiceIds": [
    "770e8400-e29b-41d4-a716-446655440002",
    "880e8400-e29b-41d4-a716-446655440003"
  ],
  "thresholdRules": [
    {
      "percentage": 75.0,
      "amount": 37500.0,
      "triggerType": "actual cost"
    },
    {
      "percentage": 90.0,
      "amount": 45000.0,
      "triggerType": "actual cost"
    }
  ],
  "alertRecipients": [
    "finance@deloitte.com",
    "operations@deloitte.com",
    "cto@deloitte.com"
  ]
}
```

### **Update Budget Request**

```json
{
  "name": "Q1 Production Budget - Updated",
  "amount": 60000.0,
  "thresholdRules": [
    {
      "percentage": 80.0,
      "amount": 48000.0,
      "triggerType": "actual spend"
    }
  ],
  "alertRecipients": ["finance@deloitte.com", "newadmin@deloitte.com"]
}
```

### **Request Body Field Tables**

#### **Create Budget Request Fields**

| Field                        | Type       | Required | Description                                      |
| ---------------------------- | ---------- | -------- | ------------------------------------------------ |
| organizationId               | UUID       | Yes      | Organization ID for multi-tenant isolation       |
| cspConnectionId              | UUID       | Yes      | CSP connection ID for cloud provider linkage     |
| name                         | String(25) | Yes      | Descriptive budget name                          |
| timeRange                    | String     | Yes      | Budget period: 'monthly', 'quarterly', 'yearly'  |
| renewalType                  | String     | Yes      | Budget renewal strategy: 'recurring'             |
| startMonth                   | Date       | Yes      | Budget cycle start date (YYYY-MM-DD)             |
| budgetType                   | String     | Yes      | Budget type: 'fixed'                             |
| amount                       | Double     | Yes      | Budget limit in monetary units                   |
| selectedServiceIds           | UUID[]     | No       | Array of service IDs for focused monitoring      |
| thresholdRules               | Object[]   | No       | Array of threshold rule definitions              |
| thresholdRules[].percentage  | Double     | No       | Threshold as percentage of budget (0-100)        |
| thresholdRules[].amount      | Double     | No       | Absolute threshold amount                        |
| thresholdRules[].triggerType | String     | Yes      | Trigger type: 'actual cost' or 'actual spend'    |
| alertRecipients              | String[]   | No       | Array of email addresses for alert notifications |

---

## **Response Body Examples**

### **Budget Creation Success Response**

```json
{
  "id": "990e8400-e29b-41d4-a716-446655440004",
  "organizationId": "550e8400-e29b-41d4-a716-446655440000",
  "organizationName": "Acme Corporation",
  "cspConnectionId": "660e8400-e29b-41d4-a716-446655440001",
  "cspConnectionName": "Production AWS Account",
  "name": "Q1 Production Budget",
  "timeRange": "quarterly",
  "renewalType": "recurring",
  "startMonth": "2024-01-01",
  "budgetType": "fixed",
  "amount": 50000.0,
  "currentSpend": 12500.75,
  "remainingAmount": 37499.25,
  "utilizationPercentage": 25.0,
  "status": "active",
  "selectedServices": [
    {
      "id": "770e8400-e29b-41d4-a716-446655440002",
      "serviceName": "Amazon EC2",
      "selected": true
    },
    {
      "id": "880e8400-e29b-41d4-a716-446655440003",
      "serviceName": "Amazon S3",
      "selected": true
    }
  ],
  "thresholdRules": [
    {
      "id": "aa0e8400-e29b-41d4-a716-446655440005",
      "percentage": 75.0,
      "amount": 37500.0,
      "triggerType": "actual cost",
      "status": "active"
    },
    {
      "id": "bb0e8400-e29b-41d4-a716-446655440006",
      "percentage": 90.0,
      "amount": 45000.0,
      "triggerType": "actual cost",
      "status": "active"
    }
  ],
  "alertRecipients": [
    {
      "id": "cc0e8400-e29b-41d4-a716-446655440007",
      "email": "finance@deloitte.com"
    },
    {
      "id": "dd0e8400-e29b-41d4-a716-446655440008",
      "email": "operations@deloitte.com"
    },
    {
      "id": "ee0e8400-e29b-41d4-a716-446655440009",
      "email": "cto@deloitte.com"
    }
  ],
  "createdAt": "2025-07-30T10:30:00Z",
  "updatedAt": "2025-07-30T10:30:00Z",
  "createdBy": "ff0e8400-e29b-41d4-a716-446655440010",
  "updatedBy": "ff0e8400-e29b-41d4-a716-446655440010"
}
```

### **Budget List Response (Organization-scoped)**

```json
{
  "budgets": [
    {
      "id": "990e8400-e29b-41d4-a716-446655440004",
      "name": "Q1 Production Budget",
      "amount": 50000.0,
      "currentSpend": 12500.75,
      "utilizationPercentage": 25.0,
      "status": "active",
      "timeRange": "quarterly",
      "cspConnectionName": "Production AWS Account",
      "alertCount": 3,
      "thresholdCount": 2,
      "nextThresholdPercentage": 75.0,
      "daysRemaining": 45
    }
  ],
  "totalBudgets": 1,
  "totalAllocatedAmount": 50000.0,
  "totalCurrentSpend": 12500.75,
  "organizationId": "550e8400-e29b-41d4-a716-446655440000"
}
```

### **Response Field Tables**

#### **Budget Response Fields**

| Field                 | Type   | Description                                |
| --------------------- | ------ | ------------------------------------------ |
| id                    | UUID   | Unique budget identifier                   |
| organizationId        | UUID   | Organization ID for multi-tenant isolation |
| organizationName      | String | Organization display name                  |
| cspConnectionId       | UUID   | CSP connection identifier                  |
| cspConnectionName     | String | CSP connection display name                |
| amount                | Double | Total budget allocation amount             |
| currentSpend          | Double | Current spending against budget            |
| remainingAmount       | Double | Remaining budget amount                    |
| utilizationPercentage | Double | Percentage of budget utilized (0-100)      |
| status                | String | Budget status (active, inactive, exceeded) |
| selectedServices      | Array  | Services included in budget monitoring     |
| thresholdRules        | Array  | Alert threshold configurations             |
| alertRecipients       | Array  | Email recipients for budget alerts         |

---

## **Business Logic Explanation**

### **Core Functionality**

The Budget Management module implements comprehensive financial governance with the following core capabilities:

1. **Multi-Tenant Budget Management** - Organization-scoped budget isolation with complete data separation
2. **CSP-Specific Budget Allocation** - Budgets tied to specific cloud provider connections for focused monitoring
3. **Service-Level Budget Granularity** - Service-specific budget allocation and tracking
4. **Multi-Threshold Alert System** - Support for multiple alert thresholds with percentage and absolute triggers
5. **Email-Based Alert Management** - Recipient management for budget notifications (email)

### **Budget Creation and Management Flow**

The system implements atomic budget creation with comprehensive relationship management:

1. **Budget Definition** - Creates budget with organization and CSP connection linkage
2. **Service Selection** - Linking to specific cloud services for focused monitoring
3. **Threshold Configuration** - Multiple alert thresholds with flexible trigger types
4. **Recipient Management** - Email-based alert recipient configuration
5. **Audit Trail** - Complete tracking of budget lifecycle with user attribution
6. **Event Publishing** - Domain events for downstream integration and monitoring

### **Multi-Tenant Budget Architecture**

#### **Organization-Based Isolation**

- **Data Boundaries** - Budgets scoped to specific organizations
- **Access Control** - Role-based budget management within organization context

#### **CSP Connection Integration**

- **Provider-Specific Budgets** - Budgets linked to specific cloud provider connections
- **Multi-Cloud Support** - Separate budgets per CSP connection within organization
- **Connection Health Evaluation** - Budget evaluation depends on healthy CSP connections
- **Cost Data Integration** - Budgets provide context for cost analytics evaluation

### **Threshold Evaluation Strategy**

#### **Multi-Level Alert System**

- **Percentage Thresholds** - Alerts based on percentage of budget consumed (e.g., 75%, 90%)
- **Absolute Amount Thresholds** - Alerts based on absolute spending amounts
- **Trigger Type Flexibility** - Support for 'actual cost' and 'actual spend' evaluation
- **Threshold Ordering** - Multiple thresholds enable escalating alert strategies

#### **Alert Processing Logic**

- **Event-Driven Alerts** - Domain events trigger alert processing
- **Email Notification** - Alert recipients receive notifications via email
- **Alert History** - Complete audit trail of alert events and responses

### **Service-Level Budget Allocation**

#### **Granular Cost Control**

- **Service Selection** - Budgets can monitor specific cloud services (EC2, S3, RDS, etc.)
- **Flexible Allocation** - Optional service selection enables focused or broad monitoring
- **Cross-Service Budgets** - Single budget can span multiple services within CSP connection
- **Service Mapping** - Integration with cloud connections module for service identification

#### **Budget Aggregation**

- **Service-Level Tracking** - Individual service spending tracked within budget context
- **Rollup Reporting** - Service spending aggregated to budget level
- **Variance Analysis** - Service-level variance reporting against allocations
- **Optimization Insights** - Service spending patterns inform optimization recommendations

### **Financial Governance Features**

#### **Budget Types and Flexibility**

- **Fixed Budgets** - Currently supported budget type with defined spending limits
- **Recurring Budgets** - Automatic budget renewal based on time range configuration
- **Time-Based Cycles** - Support for monthly, quarterly, and yearly budget periods
- **Extensible Framework** - Architecture supports future budget types (variable, rolling, etc.)

#### **Compliance and Audit**

- **Complete Audit Trail** - All budget operations tracked with user attribution
- **Change History** - Budget modifications tracked for compliance reporting
- **Access Logging** - User access to budget information logged
- **Data Retention** - Soft deletion preserves budget history for audit requirements

### **Integration Architecture**

#### **Cost Analytics Integration**

- **Spending Data Source** - Cost analytics module provides actual spending data
- **Variance Reporting** - Budget vs. actual spending analysis
- **Forecast Integration** - Budget planning informed by cost forecasting

#### **Alert and Notification Integration**

- **Event Publishing** - Budget events published for external system integration
- **Email Service Integration** - Alert recipients managed through email service
- **Dashboard Integration** - Budget status and alerts displayed in monitoring dashboards
- **API Integration** - RESTful APIs enable external budget management tools

---

## **API Call Flow Description**

```
Budget Creation Flow
Finance Admin
 │
 │ 1. Submit create budget request (POST /api/budgets)
 ▼
BudgetController.createBudget
 │
 │ 2. Extract CreateBudgetRequest with budget details
 │
 │ 3. Validate required fields and constraints
 │      └─ If validation fails:
 │            ├─ Return 400 Bad Request
 │            └─ Include field-specific validation errors
 │
 │ 4. Validate organization and CSP connection access
 │      └─ If access denied:
 │            ├─ Return 403 Forbidden
 │            └─ Include access control error message
 │
 │ 5. Call BudgetService.createBudget (Atomic Transaction):
 │      ├─ Create Budgets entity with organization and CSP linkage
 │      ├─ Persist budget to t_budgets table
 │      ├─ Create BudgetThresholdRules entities
 │      ├─ Persist threshold rules to t_budget_threshold_rules table
 │      ├─ Create BudgetAlertsRecipients entities
 │      ├─ Persist alert recipients to t_budget_alerts_recipients table
 │      ├─ Create BudgetServicesSelection entities (if services selected)
 │      ├─ Persist service selections to t_budget_services_selection table
 │      └─ If any operation fails:
 │            ├─ Rollback entire transaction
 │            ├─ Return 500 Internal Server Error
 │            └─ Include database constraint errors
 │
 │ 6. Successful budget creation:
 │      ├─ Publish BudgetCreatedEvent for downstream processing
 │      ├─ Log budget creation for audit trail
 │      ├─ Convert entities to BudgetDto with full relationships
 │      └─ Return 201 Created with complete budget details
 │
 ▼
Budget ready for cost monitoring and threshold evaluation

Budget Threshold Evaluation Flow (Scheduled Process)
Cost Analytics Module
 │
 │ 1. Cost data aggregated for CSP connection
 ▼
Budget Evaluation Service
 │
 │ 2. Retrieve all active budgets for affected CSP connection
 │
 │ 3. For each budget, evaluate spending against thresholds:
 │      ├─ Query cost analytics for spending data
 │      ├─ Calculate spending percentage against budget amount
 │      ├─ Check each threshold rule for breach conditions
 │      └─ If threshold exceeded:
 │            ├─ Create threshold breach record
 │            ├─ Publish BudgetThresholdExceededEvent
 │            └─ Schedule alert processing
 │
 │ 4. Alert Processing:
 │      ├─ Retrieve alert recipients for budget
 │      ├─ Generate alert message with budget details
 │      ├─ Send email to all recipients
 │      ├─ Publish BudgetAlertTriggeredEvent
 │      └─ Log alert event for audit trail
 │
 │ 5. Update budget status and metadata:
 │      ├─ Update budget utilization percentage
 │      ├─ Update remaining amount calculation
 │      ├─ Set budget status (active, warning, exceeded)
 │      └─ Update lastEvaluated timestamp
 │
 ▼
Budget evaluation and cost control
```

### **1. Budget Management Controller Flow**

├── **Request Processing** - Extract and validate budget creation parameters\
├── **BudgetService.createBudget()** - Atomic budget creation with all relationships\
├── **Entity Assembly** - Build budget entity graph with thresholds, recipients, and services\
└── **Event Publishing** - Trigger downstream budget monitoring processes

### **2. Multi-Entity Transaction Management**

├── **Primary Budget Creation** - Create main budget record with organization/CSP linkage\
├── **Threshold Rule Creation** - Create multiple threshold rules with validation\
├── **Recipient Management** - Create alert recipient records with email validation\
└── **Service Selection** - Optional service-specific budget allocation

### **3. Threshold Evaluation Engine**

├── **Cost Data Integration** - Retrieve spending data from cost analytics module\
├── **Threshold Evaluation** - Compare spending against all configured thresholds\
├── **Alert Triggering** - Publish events when thresholds are exceeded\
└── **Status Updates** - Maintain budget utilization metrics

### **4. Multi-Tenant Data Access**

├── **Organization Filtering** -  Organization-based budget filtering\
├── **CSP Connection Scoping** - Budget operations scoped to specific connections\
├── **Security Context** - Current user's organization context for data isolation\
└── **Access Control** - Role-based permissions for budget operations

### **5. Event-Driven Integration**

├── **BudgetCreatedEvent** - Notify evaluation systems of new budgets\
├── **BudgetThresholdExceededEvent** - Trigger alert processing and notifications\
├── **BudgetAlertTriggeredEvent** - Log alert events for audit and monitoring\
└── **Event Publishing** - Loose coupling with notification and evaluation systems

### **6. Alert and Notification Processing**

├── **Recipient Resolution** - Retrieve email recipients for budget alerts\
├── **Message Generation** - Create contextual alert messages with budget details\
├── **Notification Delivery** - Send alerts via email notification service\
└── **Audit Logging** - Complete audit trail of alert events and delivery

### **7. Budget Analytics and Reporting**

├── **Utilization Calculation** - Budget utilization percentage calculation\
├── **Variance Analysis** - Budget vs. actual spending variance reporting\
├── **Trend Analysis** - Historical budget performance and trending\
└── **Optimization Insights** - Budget efficiency and optimization recommendations

---
