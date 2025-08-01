# **Anomaly Detection Module - Technical Specification Document**

# **Overview**

The Anomaly Detection module serves as the central system for cost monitoring in the FinOps platform, providing threshold-based anomaly detection, configurable monitoring rules, alerting, and comprehensive detection history tracking across multiple cloud service providers.

**Purpose:** Enable cost anomaly detection through rule-based monitoring, configurable thresholds, and alerting to prevent cost overruns and identify unusual spending patterns.

**Scope:** Rule-based anomaly detection, monitor configuration and management, alert threshold management, recipient notification systems (email), detection history analysis, service-level anomaly aggregation, and integration with cost analytics and budget management modules.

## **API Endpoints**

### **Anomaly Detection Analytics**

| Endpoint | Method | Description | Response Codes |
|----------|--------|-------------|----------------|
| `/api/anomaly-detection/history` | GET | Retrieve historical anomaly detection results | 200: Success, 401: Unauthorized, 404: No history found |
| `/api/anomaly-detection/services` | GET | Get service-level anomaly analytics and insights | 200: Success, 401: Unauthorized, 404: No service data found |
| `/api/anomaly-detection/spend-analysis/{id}` | GET | Detailed spend analysis for specific anomaly | 200: Success, 401: Unauthorized, 404: Analysis not found |

### **Monitor Management Operations**

| Endpoint | Method | Description | Response Codes |
|----------|--------|-------------|----------------|
| `/api/anomaly-detection/monitors` | GET, POST, DELETE | Manage anomaly detection monitors and configurations | 200: Success, 400: Bad Request, 401: Unauthorized, 404: Monitor not found, 409: Duplicate monitor |

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

| Table Name | Description | Key Columns |
|------------|-------------|-------------|
| `t_anomaly_alert` | Alert definitions for anomaly notifications | `id` UUID, `name` VARCHAR(25), `organization_id` UUID |
| `t_anomaly_alert_threshold` | Configurable threshold rules for alerts | `id` UUID, `alert_id` UUID, `threshold_amount_1` FLOAT8, `threshold_amount_2` FLOAT8, `threshold_type_1` VARCHAR(255), `threshold_type_2` VARCHAR(255), `relation` VARCHAR(25) |
| `t_anomaly_alert_recipients` | Email recipients for alert notifications | `id` UUID, `alert_id` UUID, `email` VARCHAR(255) |
| `t_anomaly_monitor` | Monitor configurations linking CSPs to alerts | `id` UUID, `csp_connection_id` UUID, `name` VARCHAR(25), `alert_id` UUID |
| `t_focus_anomaly_monitors` | Service-based anomaly monitors | `id` UUID, `service_id` UUID, `name` VARCHAR(25), `type` VARCHAR(255), `monitored_dimensions` TEXT, `tags` TEXT, `organization_id` UUID |
| `t_focus_anomaly_detection` | Actual anomaly detection records with spend analysis | `id` UUID, `anomaly_monitor_id` UUID, `start_date` DATE, `last_detected_date` DATE, `expected_spend` FLOAT8, `actual_spend` FLOAT8, `linked_account_id` VARCHAR(25), `linked_account_name` VARCHAR(255) |

**Key Constraints:**
- All tables include standard audit fields: `created_at`, `updated_at`, `created_by`, `updated_by`, `deleted_at`
- Foreign key relationships ensure referential integrity between monitors, alerts, and detection records
- Multi-tenant isolation through `organization_id` in relevant tables

---

## **Request Body Examples**

### **Create Monitor Request**

```json
{
  "cspConnectionId": "550e8400-e29b-41d4-a716-446655440000",
  "name": "Production EC2 Cost Monitor",
  "type": "COST_ANOMALY",
  "monitoredDimensions": {
    "SERVICE": ["Amazon Elastic Compute Cloud"],
    "REGION": ["us-east-1", "us-west-2"],
    "RESOURCE_TYPE": ["Instance"]
  },
  "tags": {
    "Environment": "Production",
    "Team": "FinOps",
    "CostCenter": "Infrastructure",
    "AlertLevel": "Critical"
  },
  "alertConfiguration": {
    "alertName": "High EC2 Cost Anomaly Alert",
    "thresholds": [
      {
        "thresholdAmount1": 1000.0,
        "thresholdType1": "GREATER_THAN",
        "relation": "AND"
      },
      {
        "thresholdAmount1": 500.0,
        "thresholdAmount2": 2000.0,
        "thresholdType1": "BETWEEN",
        "relation": "OR"
      }
    ],
    "recipientEmails": [
      "finops@deloitte.com",
      "infrastructure@deloite.com",
      "alerts@deloitte.com"
    ]
  },
  "detectionCriteria": {
    "anomalyTypes": ["COST_ANOMALY", "USAGE_ANOMALY"],
    "alertSeverity": "HIGH",
    "lookbackPeriodDays": 30,
    "minimumThresholdAmount": 100.0
  }
}
```

### **Anomaly Detection Query Parameters**

```json
{
  "organizationId": "550e8400-e29b-41d4-a716-446655440000",
  "monitorName": "Production EC2 Cost Monitor",
  "startDate": "2024-01-01",
  "endDate": "2024-01-31",
  "anomalyTypes": ["COST_ANOMALY", "USAGE_ANOMALY"],
  "severityLevels": ["HIGH", "CRITICAL"],
  "serviceNames": ["Amazon EC2", "Amazon RDS"],
  "accountIds": ["123456789012", "234567890123"],
  "minVariancePercentage": 15.0,
  "minCostImpact": 500.0,
  "sortBy": "actualSpend",
  "sortOrder": "desc",
  "limit": 100
}
```

### **Request Body Field Tables**

#### **Create Monitor Request Fields**

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| cspConnectionId | UUID | Yes | CSP connection ID for monitor linkage |
| name | String(25) | Yes | Descriptive monitor name |
| type | String | Yes | Anomaly type: COST_ANOMALY, USAGE_ANOMALY, etc. |
| monitoredDimensions | Object | No | Dimensions to monitor (SERVICE, REGION, RESOURCE_TYPE) |
| tags | Object | No | Custom tags for monitor categorization |
| alertConfiguration | Object | Yes | Alert setup with thresholds and recipients |
| alertConfiguration.alertName | String(25) | Yes | Alert name for notifications |
| alertConfiguration.thresholds | Array | Yes | Threshold configurations for anomaly detection |
| alertConfiguration.recipientEmails | String[] | Yes | Email addresses for alert notifications |
| detectionCriteria | Object | No | Advanced detection configuration |
| detectionCriteria.anomalyTypes | String[] | No | Types of anomalies to detect |
| detectionCriteria.alertSeverity | String | No | Alert severity level (LOW, MEDIUM, HIGH, CRITICAL) |
| detectionCriteria.lookbackPeriodDays | Integer | No | Historical analysis period in days |

---

## **Response Body Examples**

### **Detection History Response**

```json
{
  "detectionHistory": [
    {
      "id": "990e8400-e29b-41d4-a716-446655440004",
      "anomalyMonitorId": "aa0e8400-e29b-41d4-a716-446655440005",
      "anomalyMonitorName": "Production EC2 Cost Monitor",
      "anomalyType": "COST_ANOMALY",
      "startDate": "2024-01-15",
      "lastDetectedDate": "2024-01-18",
      "detectionDurationDays": 3,
      "expectedSpend": 1200.00,
      "actualSpend": 1680.00,
      "spendVariance": 480.00,
      "spendVariancePercentage": 40.0,
      "costImpact": "HIGH",
      "linkedAccountId": "123456789012",
      "linkedAccountName": "Production Account",
      "affectedServices": ["Amazon EC2", "Amazon EBS"],
      "affectedRegions": ["us-east-1"],
      "alertsSent": 3,
      "lastAlertSent": "2024-01-18T14:30:00Z",
      "status": "ACTIVE",
      "createdAt": "2024-01-15T09:15:00Z",
      "updatedAt": "2024-01-18T14:30:00Z"
    }
  ],
  "totalDetections": 1,
  "totalCostImpact": 480.00,
  "averageVariancePercentage": 40.0,
  "criticalAnomalies": 0,
  "highSeverityAnomalies": 1,
  "mediumSeverityAnomalies": 0,
  "reportPeriod": {
    "startDate": "2024-01-01",
    "endDate": "2024-01-31"
  }
}
```

### **Service Summary Response**

```json
{
  "serviceSummaries": [
    {
      "serviceName": "Amazon Elastic Compute Cloud",
      "serviceCategory": "Compute",
      "anomalyCount": 12,
      "activeAnomalies": 3,
      "totalCostImpact": 4567.89,
      "averageCostImpact": 380.66,
      "maxCostImpact": 1200.00,
      "totalExpectedSpend": 28500.00,
      "totalActualSpend": 33067.89,
      "overallVariancePercentage": 16.02,
      "affectedAccounts": [
        {
          "accountId": "123456789012",
          "accountName": "Production Account",
          "anomalyCount": 8,
          "costImpact": 3200.45
        },
        {
          "accountId": "234567890123", 
          "accountName": "Staging Account",
          "anomalyCount": 4,
          "costImpact": 1367.44
        }
      ],
      "trendAnalysis": {
        "anomalyFrequencyTrend": "INCREASING",
        "costImpactTrend": "STABLE",
        "averageProcessingTime": "2.5 hours",
        "alertAccuracy": 94.8
      },
      "topAnomalyTriggers": [
        "Sudden instance count increase",
        "High CPU utilization pattern change",
        "Reserved instance expiration"
      ]
    }
  ],
  "organizationSummary": {
    "totalServices": 8,
    "totalAnomalies": 45,
    "totalCostImpact": 12456.78,
    "overallAccuracy": 94.8
  }
}
```

### **Spend Analysis Detail Response**

```json
{
  "id": "990e8400-e29b-41d4-a716-446655440004",
  "monitor": {
    "monitorId": "aa0e8400-e29b-41d4-a716-446655440005",
    "monitorName": "Production EC2 Cost Monitor",
    "monitorType": "COST_ANOMALY",
    "cspConnectionName": "AWS Production Account",
    "monitoredDimensions": {
      "SERVICE": ["Amazon EC2"],
      "REGION": ["us-east-1", "us-west-2"]
    }
  },
  "detectionPeriod": {
    "startDate": "2024-01-15",
    "lastDetectedDate": "2024-01-18",
    "durationDays": 3,
    "processingTime": "1.2 hours"
  },
  "spendComparison": {
    "expectedSpend": 1200.00,
    "actualSpend": 1680.00,
    "baselineSpend": 1150.00,
    "variance": 480.00,
    "variancePercentage": 40.0,
    "impactSeverity": "HIGH",
    "impactDescription": "40.00% overspend detected - exceeds HIGH threshold",
    "ruleMatchScore": 0.87,
    "thresholdScore": 2.3
  },
  "accountInfo": {
    "linkedAccountId": "123456789012",
    "linkedAccountName": "Production Account",
    "accountType": "Production",
    "costCenter": "Infrastructure"
  },
  "rootCauseAnalysis": {
    "primaryCause": "Unexpected instance scaling event",
    "contributingFactors": [
      "Auto-scaling policy triggered by traffic spike",
      "New m5.large instances launched without termination",
      "Reserved instance coverage gap"
    ],
    "affectedResources": [
      {
        "resourceId": "i-1234567890abcdef0",
        "resourceType": "Instance",
        "costImpact": 234.56,
        "anomalyContribution": 48.9
      }
    ]
  },
  "alertHistory": {
    "alertsSent": 3,
    "recipientsNotified": 5,
    "lastAlertSent": "2024-01-18T14:30:00Z",
    "alertEscalation": "NONE",
    "acknowledgmentStatus": "PENDING"
  },
  "recommendations": [
    {
      "type": "RIGHTSIZING",
      "description": "Consider rightsizing instances based on utilization",
      "potentialSavings": 180.45,
      "implementation": "Review CPU and memory utilization patterns"
    },
    {
      "type": "RESERVED_INSTANCES",
      "description": "Purchase reserved instances for consistent workloads",
      "potentialSavings": 125.30,
      "implementation": "Analyze 30-day usage patterns for RI recommendations"
    }
  ]
}
```

### **Response Field Tables**

#### **Detection History Response Fields**

| Field | Type | Description |
|-------|------|-------------|
| id | UUID | Unique detection record identifier |
| anomalyMonitorId | UUID | Associated monitor identifier |
| anomalyMonitorName | String | Monitor display name |
| spendVariance | Double | Absolute variance amount (actualSpend - expectedSpend) |
| spendVariancePercentage | Double | Variance as percentage of expected spend |
| costImpact | String | Impact severity (LOW, MEDIUM, HIGH, CRITICAL) |
| linkedAccountId | String | CSP account identifier |
| linkedAccountName | String | CSP account display name |
| affectedServices | String[] | Services involved in the anomaly |
| affectedRegions | String[] | Regions where anomaly was detected |

---

## **Business Logic Explanation**

### **Core Functionality**

The Anomaly Detection module implements monitoring with the following core capabilities:

1. **Multi-Dimensional Threshold Monitoring** - Monitoring cost, usage, and billing patterns across services, regions, accounts, and resources using configurable rules
2. **Configurable Threshold Rules** - Flexible threshold configuration with support for single thresholds, ranges, and complex relational operators
3. **Historical Analysis** - Comprehensive detection history with pattern analysis and root cause identification
4. **Service-Level Aggregation** - Service and account-level anomaly summaries with impact analysis

### **Rule-Based Anomaly Detection**

The system implements threshold-based anomaly detection with configurable monitoring:

1. **Baseline Establishment** - Establishes expected spending baselines using historical cost data patterns
2. **Variance Calculation** - Compares actual spending against expected baselines using configurable thresholds
3. **Threshold Evaluation** - Applies configurable thresholds with support for absolute amounts and percentage variances
4. **Pattern Analysis** - Analyzes spending patterns against configured rules to identify anomalies
5. **Multi-Dimensional Monitoring** - Monitors anomalies across multiple dimensions using predefined criteria

### **Monitor Configuration and Management**

#### **Threshold Configuration**
- **Multi-Threshold Support** - Multiple threshold rules per alert with complex relational operators
- **Threshold Types** - Support for GREATER_THAN, LESS_THAN, EQUALS, BETWEEN, and custom criteria
- **Dynamic Thresholds** - Percentage-based thresholds that adapt to spending patterns
- **Threshold Relationships** - AND/OR logic between multiple thresholds for sophisticated rules

### **Alert Processing and Notification**

#### **Alert Lifecycle Management**
- **Alert Creation** - Alert generation when thresholds are exceeded
- **Severity Classification** - Dynamic severity assignment based on variance magnitude and impact
- **Escalation Processing** - Configurable escalation policies based on alert duration and severity
- **Acknowledgment Tracking** - Alert acknowledgment and resolution tracking for audit compliance

#### **Notification System**
- **Multi-Recipient Support** - Email notifications to multiple recipients per alert
- **Customizable Messages** - Context-rich alert messages with detailed anomaly information
- **Delivery Tracking** - Complete audit trail of notification delivery and recipient responses

### **Detection History and Analytics**

#### **Historical Analysis**
- **Comprehensive Tracking** - Complete history of all detected anomalies with detailed metadata
- **Pattern Analysis** - Analysis of anomaly patterns, frequency, and cost impact over time

#### **Service-Level Aggregation**
- **Service Impact Analysis** - Aggregated anomaly statistics and cost impact by service
- **Account-Level Breakdown** - Multi-account anomaly analysis within service categories
- **Monitoring Metrics** - Service monitoring effectiveness and alert accuracy tracking

### **Integration Architecture**

#### **Cost Analytics Integration**
- **Data Processing** - FOCUS cost data for anomaly detection
- **Baseline Data Source** - Uses historical cost analytics data for baseline establishment
- **Variance Calculation** - Integrates with cost metrics for accurate variance and impact calculations
- **Trend Analysis** - Leverages cost trending data for pattern recognition and seasonal adjustment

#### **Budget Management Integration**
- **Budget Context** - Anomaly detection considers budget allocations and thresholds
- **Variance Correlation** - Correlates anomalies with budget variance for comprehensive financial oversight
- **Alert Coordination** - Coordinates with budget alerts to prevent notification redundancy
- **Cost Control** - Provides early warning system for budget threshold breaches

#### **Cloud Connections Integration**
- **Multi-CSP Support** - Monitors anomalies across AWS, Azure, GCP, and other cloud providers
- **Connection Health** - Monitors depend on healthy CSP connections for accurate data
- **Service Mapping** - Integrates with service definitions for granular monitoring capabilities
- **Account Context** - Uses CSP account information for detailed anomaly attribution

---

## **API Call Flow Description**

```
Threshold-Based Monitoring Processing Flow
Cost Analytics Engine
 │
 │ 1. New cost data processed (triggered by FOCUS data ingestion)
 ▼
AnomalyDetectionService.processNewCostData
 │
 │ 2. Retrieve active monitors for affected CSP connections:
 │      ├─ Query Monitor entities linked to CSP connections
 │      ├─ Query FocusAnomalyMonitors for service-specific monitoring
 │      └─ Filter monitors by organization and connection scope
 │
 │ 3. For each active monitor, perform anomaly detection:
 │      ├─ Calculate expected baseline from historical data
 │      ├─ Compare actual spending against baseline
 │      ├─ Calculate variance percentage and absolute difference
 │      ├─ Apply threshold-based evaluation
 │      └─ Generate anomaly score based on configured rules
 │
 │ 4. Threshold evaluation and anomaly classification:
 │      ├─ Retrieve alert thresholds for monitor
 │      ├─ Evaluate variance against threshold criteria
 │      ├─ Apply relational operators (AND/OR logic)
 │      ├─ Classify severity based on variance magnitude
 │      └─ If threshold exceeded:
 │            ├─ Create FocusAnomalyDetection record
 │            ├─ Calculate cost impact and severity
 │            ├─ Publish AnomalyDetectedEvent
 │            └─ Trigger alert processing
 │
 │ 5. Alert processing and notification:
 │      ├─ Retrieve alert recipients from AlertRecipients table
 │      ├─ Generate contextual alert message with anomaly details
 │      ├─ Send notifications to all recipients
 │      ├─ Create alert history record for audit trail
 │      ├─ Publish AlertTriggeredEvent
 │      └─ Update monitor performance metrics
 │
 ▼
Anomaly detection and alerting complete

Monitor Creation Flow
FinOps Administrator
 │
 │ 1. Submit create monitor request (POST /api/anomaly-detection/monitors)
 ▼
AnomalyController.createMonitor
 │
 │ 2. Extract CreateMonitorRequest with monitor configuration
 │
 │ 3. Validate monitor configuration and user permissions:
 │      ├─ Validate CSP connection exists and is accessible
 │      ├─ Validate threshold configuration syntax
 │      ├─ Validate recipient email formats
 │      └─ If validation fails:
 │            ├─ Return 400 Bad Request
 │            └─ Include detailed validation errors
 │
 │ 4. Create monitor entities (Atomic Transaction):
 │      ├─ Create Alert entity with organization linkage
 │      ├─ Persist alert to t_anomaly_alert table
 │      ├─ Create AlertThreshold entities for each threshold rule
 │      ├─ Persist thresholds to t_anomaly_alert_threshold table
 │      ├─ Create AlertRecipients entities for each email
 │      ├─ Persist recipients to t_anomaly_alert_recipients table
 │      ├─ Create Monitor entity linking CSP connection to alert
 │      ├─ Persist monitor to t_anomaly_monitor table
 │      └─ If any operation fails:
 │            ├─ Rollback entire transaction
 │            ├─ Return 500 Internal Server Error
 │            └─ Include constraint error details
 │
 │ 5. Initialize monitor for detection:
 │      ├─ Establish baseline using historical cost data
 │      ├─ Calculate initial monitoring metrics
 │      ├─ Configure threshold monitoring schedule
 │      ├─ Log monitor creation for audit trail
 │      └─ Return MonitorDto with complete configuration
 │
 ▼
Monitor ready for anomaly detection

Detection History Analysis Flow
Financial Analyst
 │
 │ 1. Request detection history (GET /api/anomaly-detection/history?monitorName=EC2)
 ▼
AnomalyController.getDetectionHistory
 │
 │ 2. Extract query parameters and validate access:
 │      ├─ Validate user access to organization data
 │      ├─ Parse optional filters (monitor name, date range, severity)
 │      └─ Apply organization-based data isolation
 │
 │ 3. Query detection history with advanced filtering:
 │      ├─ Query FocusAnomalyDetection with JOIN to monitors
 │      ├─ Apply date range filters on detection dates
 │      ├─ Apply monitor name filters if specified
 │      ├─ Calculate variance percentages and impact severity
 │      ├─ Order by detection date and cost impact
 │      └─ Apply pagination for large result sets
 │
 │ 4. Enhanced analytics processing:
 │      ├─ Calculate aggregate statistics (total impact, average variance)
 │      ├─ Generate trend analysis (frequency, severity distribution)
 │      ├─ Identify top cost impact anomalies
 │      ├─ Calculate monitoring effectiveness metrics
 │      └─ Format comprehensive response with insights
 │
 │ 5. Response enrichment:
 │      ├─ Convert entities to DetectionHistoryDto objects
 │      ├─ Include monitor context and configuration details
 │      ├─ Add root cause analysis where available
 │      ├─ Include recommendations for anomaly prevention
 │      └─ Return paginated results with metadata
 │
 ▼
Comprehensive detection history with analytics delivered to user
```

### **1. Anomaly Detection Engine**

├── **Baseline Calculation** - Historical data analysis to establish spending baselines\
├── **Variance Analysis** - Variance calculation with threshold-based evaluation\
└── **Classification Logic** - Rule-based severity classification and impact assessment

### **2. Monitor Management System**

├── **Configuration Management** - Complex monitor setup with multi-threshold and multi-recipient support\
└── **Lifecycle Management** - Complete monitor lifecycle from creation to deletion with cascade operations

### **3. Alert Processing Engine**

├── **Threshold Evaluation** - Complex threshold logic with AND/OR relationships and multiple criteria\
├── **Notification Orchestration** - Multi-recipient notification with delivery tracking and audit\
└── **Escalation Management** - Escalation based on severity and response times\

### **4. Historical Analytics System**

├── **Pattern Analysis** - Analysis of anomaly patterns and cost impact trends\
├── **Impact Analysis** - Analysis of contributing factors and affected resources\
└── **Comparative Analysis** - Cross-service and cross-account anomaly comparison

### **5. Multi-Tenant Data Management**

├── **Organization Isolation** - Complete data separation by organization with security enforcement\
├── **User Context** - Current user organization context for all data access operations\
├── **Audit Compliance** - Comprehensive audit trails for all detection and alert activities\
└── **Access Control** - Role-based access control for monitor management and detection history

### **6. Integration Event Processing**

├── **Cost Analytics Integration** - Integration with FOCUS cost data for detection\
├── **Budget Management Integration** - Coordination with budget alerts and threshold monitoring\
└── **External System Integration** - Event publishing for external monitoring and alerting systems


---