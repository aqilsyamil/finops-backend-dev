# **Recommendations Module - Technical Specification Document**

# **Overview**

The Recommendations module serves as the optimization engine of the FinOps platform, providing data-driven cost optimization recommendations, comprehensive savings analysis, implementation guidance, and portfolio-level optimization metrics across multiple cloud service providers.

**Purpose:** Generate actionable cost optimization recommendations through analysis of cost data, quantify potential savings, and enable systematic cloud cost optimization across multi-tenant environments.

**Scope:** Cost optimization recommendation generation, savings calculation and projection, portfolio-level optimization metrics, multi-dimensional filtering, and integration with cost analytics for data-driven optimization insights.

## **API Endpoints**

### **Recommendation Management Operations**

| Endpoint | Method | Description | Response Codes |
|----------|--------|-------------|----------------|
| `/api/recommendations/{id}` | GET, DELETE | Manage individual recommendation entities | 200: Success, 401: Unauthorized, 404: Recommendation not found |
| `/api/recommendations/recommendation-id/{recommendationId}` | GET | Lookup recommendation by business identifier | 200: Success, 401: Unauthorized, 404: Recommendation not found |
| `/api/recommendations/connection/{connectionId}` | GET | Get all recommendations for specific CSP connection | 200: Success, 401: Unauthorized, 404: Connection not found |

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
- **Data Types:** Double for financial amounts, JSONB for metadata

**Database Tables (Schema: finops_v2):**

| Table Name | Description | Key Columns |
| --- | --- | --- |
| `t_recommendations_report` | Cost optimization recommendations with savings analysis | `id` UUID, `focus_log_id` UUID, `account_id` TEXT, `action_type` TEXT, `currency_code` TEXT, `estimated_monthly_cost_before_discount` FLOAT8, `estimated_monthly_cost_after_discount` FLOAT8, `estimated_monthly_savings_before_discount` FLOAT8, `estimated_monthly_savings_after_discount` FLOAT8, `estimated_savings_percentage_before_discount` FLOAT8, `estimated_savings_percentage_after_discount` FLOAT8, `implementation_effort` TEXT, `resource_arn` TEXT, `region` TEXT, `tags` JSONB |

**Key Financial Fields:**

- Cost estimates: `estimated_monthly_cost_before_discount`, `estimated_monthly_cost_after_discount`
- Savings calculations: `estimated_monthly_savings_before_discount`, `estimated_monthly_savings_after_discount`
- Savings percentages: `estimated_savings_percentage_before_discount`, `estimated_savings_percentage_after_discount`

**Implementation Guidance Fields:**

- `implementation_effort`: Implementation difficulty (Low/Medium/High/Very High)
- `restart_needed`: BOOLEAN - Whether implementation requires resource restart

---

## **Request Body Examples**

### **Recommendation Filter Request**

```json
{
  "focusLogId": "550e8400-e29b-41d4-a716-446655440000",
  "actionTypes": ["RIGHTSIZE", "TERMINATE", "RESERVE", "SAVINGS_PLAN"],
  "implementationEfforts": ["LOW", "MEDIUM"],
  "regions": ["us-east-1", "us-west-2", "eu-west-1"],
  "resourceTypes": ["EC2-Instance", "RDS-DBInstance", "EBS-Volume"],
  "savingsCriteria": {
    "minMonthlySavings": 100.0,
    "maxMonthlySavings": 10000.0,
    "minSavingsPercentage": 15.0,
    "maxSavingsPercentage": 80.0,
    "currencyCode": "USD"
  },
  "organizationContext": {
    "organizationId": "660e8400-e29b-41d4-a716-446655440001",
    "cspConnectionIds": ["770e8400-e29b-41d4-a716-446655440002", "880e8400-e29b-41d4-a716-446655440003"]
  },
  "sorting": {
    "sortBy": "estimatedMonthlySavingsAfterDiscount",
    "sortOrder": "desc"
  },
  "pagination": {
    "limit": 50,
    "offset": 0
  }
}
```

### **Request Body Field Tables**

#### **Recommendation Filter Request Fields**

| Field                                      | Type     | Required | Description                                               |
| ------------------------------------------ | -------- | -------- | --------------------------------------------------------- |
| focusLogId                                 | UUID     | No       | Filter by specific cost analytics session                 |
| actionTypes                                | String[] | No       | Optimization actions: RIGHTSIZE, TERMINATE, RESERVE, etc. |
| implementationEfforts                      | String[] | No       | Effort levels: LOW, MEDIUM, HIGH, VERY_HIGH               |
| regions                                    | String[] | No       | Geographic regions to include                             |
| resourceTypes                              | String[] | No       | Resource types (EC2-Instance, RDS-DBInstance, etc.)       |
| savingsCriteria.minMonthlySavings          | Double   | No       | Minimum monthly savings amount                            |
| savingsCriteria.maxMonthlySavings          | Double   | No       | Maximum monthly savings amount                            |
| savingsCriteria.minSavingsPercentage       | Double   | No       | Minimum savings percentage (0-100)                        |
| savingsCriteria.maxSavingsPercentage       | Double   | No       | Maximum savings percentage (0-100)                        |
| implementationConstraints.restartNeeded    | Boolean  | No       | Filter by restart requirement                             |
| implementationConstraints.rollbackPossible | Boolean  | No       | Filter by rollback capability                             |
| sorting.sortBy                             | String   | No       | Sort field: savings amount, percentage, effort            |
| sorting.sortOrder                          | String   | No       | Sort order: 'asc' or 'desc'                               |
| pagination.limit                           | Integer  | No       | Maximum number of results (default: 50)                   |

---

## **Response Body Examples**

### **Comprehensive Recommendation Response**

```json
{
  "id": "990e8400-e29b-41d4-a716-446655440004",
  "recommendationId": "aws-rightsizing-i-1234567890abcdef0-20240115",
  "focusLogId": "aa0e8400-e29b-41d4-a716-446655440005",
  "accountId": "123456789012",
  "actionType": "RIGHTSIZE",
  "recommendationSource": "AWS Cost Explorer",
  "lastRefreshTimestamp": "2024-01-15T10:30:00Z",
  "lookbackPeriodDays": 14,
  "region": "us-east-1",
  "resourceArn": "arn:aws:ec2:us-east-1:123456789012:instance/i-1234567890abcdef0",
  "currentResource": {
    "resourceType": "EC2-Instance",
    "resourceDetails": "m5.xlarge, 4 vCPU, 16 GB RAM",
    "resourceSummary": "General Purpose instance with consistent moderate utilization",
    "currentConfiguration": {
      "instanceType": "m5.xlarge",
      "vcpus": 4,
      "memoryGb": 16,
      "networkPerformance": "Up to 10 Gbps",
      "storageType": "EBS-optimized"
    }
  },
  "recommendedResource": {
    "resourceType": "EC2-Instance",
    "resourceDetails": "m5.large, 2 vCPU, 8 GB RAM",
    "resourceSummary": "Right-sized instance matching actual utilization patterns",
    "recommendedConfiguration": {
      "instanceType": "m5.large",
      "vcpus": 2,
      "memoryGb": 8,
      "networkPerformance": "Up to 10 Gbps",
      "storageType": "EBS-optimized"
    }
  },
  "financialAnalysis": {
    "currencyCode": "USD",
    "monthlyEstimates": {
      "currentCostBeforeDiscount": 140.16,
      "currentCostAfterDiscount": 126.14,
      "recommendedCostBeforeDiscount": 70.08,
      "recommendedCostAfterDiscount": 63.07,
      "savingsBeforeDiscount": 70.08,
      "savingsAfterDiscount": 63.07,
      "savingsPercentageBeforeDiscount": 50.0,
      "savingsPercentageAfterDiscount": 50.0
    },
    "annualProjection": {
      "currentAnnualCost": 1513.68,
      "recommendedAnnualCost": 756.84,
      "annualSavings": 756.84,
      "annualSavingsPercentage": 50.0
    },
    "paybackPeriod": "Immediate",
    "roi": "100% within first month"
  },
  "implementationGuidance": {
    "implementationEffort": "MEDIUM",
    "estimatedImplementationTime": "2-4 hours",
    "restartNeeded": true,
    "rollbackPossible": true,
    "implementationSteps": [
      "1. Take snapshot of current instance for rollback",
      "2. Stop the instance during maintenance window",
      "3. Change instance type to m5.large",
      "4. Start instance and verify functionality",
      "5. Monitor performance for 24-48 hours"
    ],
    "prerequisites": ["Instance must be EBS-backed", "Coordinate with application team for downtime", "Ensure monitoring is in place"],
    "risks": ["Brief downtime during instance type change", "Potential performance impact if underestimated"],
    "rollbackProcedure": "Stop instance, change back to m5.xlarge, restart"
  },
  "utilizationAnalysis": {
    "analysisperiod": "14 days",
    "cpuUtilization": {
      "average": 35.2,
      "maximum": 68.5,
      "percentile95": 52.1
    },
    "memoryUtilization": {
      "average": 42.1,
      "maximum": 71.3,
      "percentile95": 58.7
    },
    "networkUtilization": {
      "average": 15.6,
      "maximum": 45.2
    },
    "rightsizingRationale": "CPU and memory utilization consistently below 50%, indicating over-provisioning"
  },
  "tags": {
    "Environment": "Production",
    "Application": "WebApp",
    "Owner": "Platform Team",
    "CostCenter": "Engineering",
    "RecommendationConfidence": "High"
  },
  "metadata": {
    "confidence": 0.92,
    "dataQuality": "Excellent",
    "dataQuality": "Excellent",
    "businessImpactAssessed": true
  },
  "createdAt": "2024-01-15T10:30:00Z",
  "updatedAt": "2024-01-15T10:30:00Z"
}
```

### **Savings Portfolio Summary Response**

```json
{
  "organizationId": "550e8400-e29b-41d4-a716-446655440000",
  "portfolioSummary": {
    "totalRecommendations": 127,
    "totalPotentialMonthlySavings": 45678.9,
    "totalPotentialAnnualSavings": 548146.8,
    "averageSavingsPercentage": 32.5,
    "implementationDistribution": {
      "LOW": { "count": 45, "savings": 15234.56 },
      "MEDIUM": { "count": 52, "savings": 23456.78 },
      "HIGH": { "count": 25, "savings": 6543.21 },
      "VERY_HIGH": { "count": 5, "savings": 444.35 }
    }
  },
  "actionTypeBreakdown": [
    {
      "actionType": "RIGHTSIZE",
      "recommendationCount": 68,
      "totalMonthlySavings": 23456.78,
      "averageSavingsPercentage": 35.2,
      "implementationComplexity": "MEDIUM",
      "priorityScore": 8.7
    },
    {
      "actionType": "TERMINATE",
      "recommendationCount": 23,
      "totalMonthlySavings": 12345.67,
      "averageSavingsPercentage": 100.0,
      "implementationComplexity": "LOW",
      "priorityScore": 9.5
    },
    {
      "actionType": "RESERVE",
      "recommendationCount": 18,
      "totalMonthlySavings": 6789.01,
      "averageSavingsPercentage": 25.8,
      "implementationComplexity": "LOW",
      "priorityScore": 7.8
    },
    {
      "actionType": "SAVINGS_PLAN",
      "recommendationCount": 12,
      "totalMonthlySavings": 2345.67,
      "averageSavingsPercentage": 22.3,
      "implementationComplexity": "LOW",
      "priorityScore": 7.2
    }
  ],
  "serviceBreakdown": [
    {
      "serviceName": "Amazon EC2",
      "recommendationCount": 78,
      "totalMonthlySavings": 28901.23,
      "topActionTypes": ["RIGHTSIZE", "TERMINATE", "RESERVE"]
    },
    {
      "serviceName": "Amazon RDS",
      "recommendationCount": 25,
      "totalMonthlySavings": 9876.54,
      "topActionTypes": ["RIGHTSIZE", "RESERVE"]
    }
  ]
}
```

## **Business Logic Explanation**

### **Core Functionality**

The Recommendations module implements comprehensive cost optimization with the following core capabilities:

1. **Data-Driven Recommendation Generation** - Analysis of cost and utilization data to identify optimization opportunities
2. **Financial Impact Modeling** - Comprehensive before/after cost modeling with discount considerations and ROI calculations
3. **Implementation Risk Assessment** - Detailed analysis of implementation effort, risks, and rollback capabilities
4. **Portfolio-Level Optimization** - Organization-wide optimization analysis with prioritization and roadmap planning
5. **Multi-Dimensional Filtering** - Advanced filtering capabilities across multiple criteria for targeted optimization
6. **Integration-Ready Architecture** - Event-driven design for integration with external optimization and deployment tools

### **Implementation Guidance System**

#### **Effort Assessment**

- **LOW Effort** - Configuration changes, policy updates, automated actions
- **MEDIUM Effort** - Resource modifications requiring coordination and brief downtime
- **HIGH Effort** - Architectural changes requiring significant planning and testing
- **VERY_HIGH Effort** - Complex migrations or major infrastructure changes

### **Portfolio-Level Optimization**

#### **Organization-Wide Analysis**

- **Total Opportunity Assessment** - Comprehensive view of optimization potential across all resources
- **Service-Level Aggregation** - Optimization opportunities grouped by cloud service type
- **Account-Level Breakdown** - Multi-account optimization analysis for complex organizations
- **Trend Analysis** - Historical optimization progress and remaining opportunity identification

---

## **API Call Flow Description**

```
Recommendation Generation Flow
Cost Analytics Data Processor
 │
 │ 1. New FOCUS cost data processed (initiated by data ingestion)
 ▼
RecommendationEngine.generateRecommendations
 │
 │ 2. Analyze cost and utilization patterns:
 │      ├─ Query FocusUsage data for resource utilization patterns
 │      ├─ Calculate baseline costs and utilization
 │      └─ Identify underutilized, overutilized, and unused resources
 │
 │ 3. Generate optimization recommendations:
 │      ├─ For each identified optimization opportunity:
 │      │    ├─ Determine appropriate action type (RIGHTSIZE, TERMINATE, etc.)
 │      │    ├─ Calculate current vs recommended resource configuration
 │      │    ├─ Model financial impact with before/after costs
 │      │    ├─ Assess implementation effort and risks
 │      │    └─ Generate implementation guidance and steps
 │      └─ Create FocusRecommendations entities with complete analysis
 │
 │ 4. Financial modeling and validation:
 │      ├─ Calculate monthly savings before and after discounts
 │      ├─ Project annual savings with compound effects
 │      ├─ Validate savings calculations against cost baselines
 │      ├─ Assess ROI and payback periods
 │      └─ Generate confidence intervals for savings estimates
 │
 │ 5. Persist recommendations and publish events:
 │      ├─ Batch insert recommendations to t_recommendations_report
 │      ├─ Link recommendations to source FocusLog for traceability
 │      ├─ Publish RecommendationGeneratedEvent for downstream processing
 │      ├─ Update recommendation statistics and metrics
 │      └─ Initialize portfolio-level optimization analysis
 │
 ▼
Actionable optimization recommendations ready for implementation

Advanced Recommendation Filtering Flow
FinOps Engineer
 │
 │ 1. Submit complex filter request (POST /api/recommendations/filter)
 ▼
RecommendationController.filterRecommendations
 │
 │ 2. Extract RecommendationFilterRequest with multi-dimensional criteria
 │
 │ 3. Validate filter parameters and user access:
 │      ├─ Validate savings ranges and percentage thresholds
 │      ├─ Validate action types and implementation effort levels
 │      ├─ Validate geographic and resource type filters
 │      ├─ Ensure user has access to specified organizations/connections
 │      └─ If validation fails:
 │            ├─ Return 400 Bad Request
 │            └─ Include detailed parameter validation errors
 │
 │ 4. Execute complex filtering logic:
 │      ├─ Build dynamic JPA query with multiple WHERE conditions
 │      ├─ Apply focus log association filters
 │      ├─ Apply financial criteria (savings amounts and percentages)
 │      ├─ Apply technical filters (regions, resource types, effort levels)
 │      ├─ Apply implementation constraint filters (restart/rollback requirements)
 │      ├─ Apply organization-based multi-tenant filtering
 │      └─ Apply sorting and pagination parameters
 │
 │ 5. Enhanced response processing:
 │      ├─ Convert entities to comprehensive RecommendationDto objects
 │      ├─ Calculate aggregated portfolio metrics for filtered set
 │      ├─ Generate implementation priority scores
 │      ├─ Include related resource and cost context
 │      ├─ Add recommendation confidence and data quality indicators
 │      └─ Format response with pagination metadata
 │
 │ 6. Advanced analytics processing:
 │      ├─ Calculate filtered portfolio savings potential
 │      ├─ Generate implementation effort distribution
 │      ├─ Identify quick wins within filtered recommendations
 │      ├─ Provide optimization roadmap for filtered set
 │      └─ Include comparative analysis against full portfolio
 │
 ▼
Precisely filtered recommendations with comprehensive analysis delivered

Savings Calculation and Portfolio Analysis Flow
Financial Analyst
 │
 │ 1. Request portfolio savings analysis (GET /api/recommendations/portfolio-savings)
 ▼
SavingsCalculationService.calculatePortfolioSavings
 │
 │ 2. Aggregate recommendations by organization and CSP connections:
 │      ├─ Query all active recommendations for organization
 │      ├─ Group recommendations by action type and service
 │      ├─ Calculate total and average savings across portfolio
 │      ├─ Assess implementation effort distribution
 │      └─ Identify optimization patterns and trends
 │
 │ 3. Financial modeling and projections:
 │      ├─ Calculate total monthly and annual savings potential
 │      ├─ Model compound savings effects for interconnected optimizations
 │      ├─ Assess portfolio-level ROI and payback periods
 │      ├─ Project cash flow impact over multiple time horizons
 │      └─ Calculate risk-adjusted savings with confidence intervals
 │
 │ 4. Implementation roadmap generation:
 │      ├─ Prioritize recommendations using impact-effort matrix
 │      ├─ Identify implementation dependencies and prerequisites
 │      ├─ Generate phased implementation plan with timelines
 │      ├─ Estimate required resources and coordination efforts
 │      └─ Assess business impact and change management requirements
 │
 │ 5. Comparative and trend analysis:
 │      ├─ Compare current optimization opportunity against historical trends
 │      ├─ Analyze optimization success rates and patterns
 │      ├─ Benchmark against industry optimization standards
 │      ├─ Identify recurring optimization opportunities requiring systemic fixes
 │      └─ Generate optimization maturity assessment
 │
 ▼
Comprehensive portfolio optimization analysis
```

### **1. Recommendation Generation Engine**

├── **Pattern Analysis** - Statistical analysis of cost and utilization patterns for optimization identification \
└── **Risk Assessment** - Implementation effort and risk analysis with rollback capability evaluation

### **2. Advanced Filtering System**

├── **Multi-Dimensional Filtering** - Complex filtering across financial, technical, and business criteria \
├── **Dynamic Query Building** - Flexible JPA query construction for complex filter combinations \
├── **Performance Optimization** - Efficient query execution with proper indexing and caching \
└── **Result Enrichment** - Enhanced response processing with contextual information and analytics

### **3. Savings Calculation Engine**

├── **Financial Analysis** - Comprehensive cost comparison and savings quantification \
├── **Portfolio Aggregation** - Organization-wide optimization analysis and prioritization \
└── **Risk-Adjusted Analysis** - Confidence-based savings calculations with uncertainty quantification

### **4. Implementation Guidance System**

├── **Effort Assessment** - Detailed analysis of implementation complexity and resource requirements \
├── **Risk Analysis** - Comprehensive risk assessment with mitigation strategies \
└── **Step-by-Step Guidance** - Detailed implementation instructions with prerequisites and validation \

### **5. Multi-Tenant Data Management**

├── **Organization Isolation** - Complete data separation by organization with security enforcement \
├── **Connection Scoping** - Recommendations scoped to specific CSP connections \
├── **User Attribution** - Comprehensive audit trails for recommendation access and implementation \
└── **Access Control** - Role-based access control for recommendation viewing and management

### **6. Integration Event Processing**

├── **Recommendation Events** - Domain events for external system integration and workflow coordination \
├── **External System APIs** - RESTful APIs for integration with deployment and change management tools \
└── **Notification Framework** - Event-driven notifications for high-impact optimization opportunities

### **7. Performance and Scalability**

├── **Batch Operations** - Efficient batch processing for portfolio-level analysis \
└── **Query Optimization** - Database query optimization for complex filtering and aggregation operations

---
