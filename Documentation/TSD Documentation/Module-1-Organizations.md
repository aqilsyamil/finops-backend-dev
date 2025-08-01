# **FinOps System - Technical Specification Document**

# **Overview**

The FinOps system is a Spring Boot application designed for Financial Operations (FinOps) across multiple cloud service providers (CSPs). The system provides a billing dashboard for cloud cost management, financial optimization, and multi-tenant cost analytics with FOCUS standard compliance.

**Purpose:** Enable organizations to manage, monitor, and optimize cloud spending across multiple CSPs through automated cost analytics, budget management, anomaly detection, and recommendations.

**Scope:** Multi-tenant cloud cost management platform supporting AWS, Azure, and GCP with comprehensive financial analytics, budgeting, alerting, and cost optimization features.

**Architecture:** Domain-Driven Design (DDD) with 7 business modules organized around distinct financial operations domains.

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

---

# **Module 1: Organization - Authentication & Multi-Tenancy Foundation**

## **Overview**

The Organization module serves as the foundational layer providing multi-tenant security, user management, authentication, and role-based access control for the entire FinOps system. This module establishes the security context and organizational boundaries that all other business modules depend upon.

**Purpose:** Provide secure multi-tenant architecture with JWT-based authentication, comprehensive user lifecycle management, and granular role-based feature permissions.

**Scope:** User authentication, organization management, role-based access control, team collaboration, and security infrastructure for the entire FinOps platform.

## **API Endpoints**

### **Authentication Operations**

| Endpoint | Method | Description | Response Codes |
|----------|--------|-------------|----------------|
| `/api/auth/login` | POST | User authentication and JWT token generation | 200: Success, 400: Invalid credentials, 401: Unauthorized, 500: Server error |

### **Organization Management Operations**

| Endpoint | Method | Description | Response Codes |
|----------|--------|-------------|----------------|
| `/api/organizations` | GET, POST, PUT, DELETE | Manage organization entities | 200: Success, 400: Bad Request, 401: Unauthorized, 404: Not Found, 409: Duplicate organization |

### **User Management Operations**

| Endpoint | Method | Description | Response Codes |
|----------|--------|-------------|----------------|
| `/api/users` | GET, POST, PUT, DELETE | Manage user accounts and profiles | 200: Success, 400: Bad Request, 401: Unauthorized, 404: User not found, 409: Duplicate email |

### **Role Management Operations**

| Endpoint | Method | Description | Response Codes |
|----------|--------|-------------|----------------|
| `/api/roles` | GET, POST, PUT, DELETE | Manage system and custom roles | 200: Success, 400: Bad Request, 401: Unauthorized, 404: Role not found, 409: Duplicate role |

### **Team Management Operations**

| Endpoint | Method | Description | Response Codes |
|----------|--------|-------------|----------------|
| `/api/teams` | GET, POST, PUT, DELETE | Manage organizational teams and memberships | 200: Success, 400: Bad Request, 401: Unauthorized, 404: Team not found, 409: Duplicate team |

---

**Database Tables (Schema: finops_v2):**

| Table Name                      | Description                                      | Key Columns                                                                                                                                                                                                                               |
| ------------------------------- | ------------------------------------------------ | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `t_mt_organization`             | Organization master data for multi-tenancy       | `id` UUID PRIMARY KEY, `organization_name` TEXT NOT NULL, `created_at` TIMESTAMPTZ DEFAULT now(), `updated_at` TIMESTAMPTZ, `deleted_at` TIMESTAMPTZ                                                                                      |
| `t_mt_users`                    | User accounts with authentication credentials    | `id` UUID PRIMARY KEY, `organization_id` UUID, `username` VARCHAR(100) NOT NULL, `email` VARCHAR(255) NOT NULL UNIQUE, `role_id` UUID, `status` VARCHAR(20) NOT NULL, `last_access_at` TIMESTAMPTZ, `password_hash` VARCHAR(255) NOT NULL, `created_at` TIMESTAMPTZ DEFAULT now(), `updated_at` TIMESTAMPTZ, `deleted_at` TIMESTAMPTZ |
| `t_mt_roles`                    | Role definitions (system and custom roles)       | `id` UUID PRIMARY KEY, `role_name` VARCHAR(100) NOT NULL, `role_type` VARCHAR(50) NOT NULL, `status` VARCHAR(20) NOT NULL, `created_at` TIMESTAMPTZ DEFAULT now(), `updated_at` TIMESTAMPTZ, `deleted_at` TIMESTAMPTZ, `created_by` UUID, `updated_by` UUID |
| `t_mt_features`                 | Available system features for permission mapping | `id` UUID PRIMARY KEY, `feature_name` VARCHAR(100) NOT NULL UNIQUE, `created_at` TIMESTAMPTZ DEFAULT now(), `updated_at` TIMESTAMPTZ, `created_by` UUID, `updated_by` UUID, `deleted_at` TIMESTAMPTZ                                    |
| `t_mt_role_feature_permissions` | Granular role-to-feature permission mappings     | `id` UUID PRIMARY KEY, `role_id` UUID, `feature_id` UUID, `can_read` BOOLEAN DEFAULT false NOT NULL, `can_write` BOOLEAN DEFAULT false NOT NULL, `can_list` BOOLEAN DEFAULT false NOT NULL, `can_modify` BOOLEAN DEFAULT false NOT NULL, `created_at` TIMESTAMPTZ DEFAULT now(), `updated_at` TIMESTAMPTZ, `created_by` UUID, `updated_by` UUID, `deleted_at` TIMESTAMPTZ |
| `t_mt_teams`                    | Team organization within tenant boundaries       | `id` UUID PRIMARY KEY, `team_name` VARCHAR(100) NOT NULL, `status` VARCHAR(20) NOT NULL, `organization_id` UUID, `created_at` TIMESTAMPTZ DEFAULT now(), `updated_at` TIMESTAMPTZ, `created_by` UUID, `updated_by` UUID, `deleted_at` TIMESTAMPTZ |
| `t_mt_team_members`             | User membership in teams                         | `id` UUID PRIMARY KEY, `team_id` UUID, `user_id` UUID, `created_at` TIMESTAMPTZ DEFAULT now(), `updated_at` TIMESTAMPTZ, `deleted_at` TIMESTAMPTZ                                                                                        |

**Key Constraints:**

- `t_mt_users.status` CHECK: Must be 'active' or 'deactivated'
- `t_mt_roles.role_type` CHECK: Must be 'system' or 'custom'
- `t_mt_roles.status` CHECK: Must be 'active' or 'not active'
- `t_mt_teams.status` CHECK: Must be 'active' or 'inactive'
- All tables include standard audit fields: `created_at`, `updated_at`, `created_by`, `updated_by`, `deleted_at`

---

## **Request Body Examples**

### **Authentication Request**

```json
{
  "email": "admin@test.com",
  "password": "admin123"
}
```

### **Create User Request**

```json
{
  "username": "johnsmith",
  "email": "john.smith@deloitte.com",
  "password": "SecurePass123!",
  "organizationId": "550e8400-e29b-41d4-a716-446655440000",
  "roleId": "660e8400-e29b-41d4-a716-446655440001"
}
```

### **Create Organization Request**

```json
{
  "organizationName": "Acme Corporation",
  "organizationCode": "ACME001",
  "contactEmail": "admin@acme.com",
}
```

### **Request Body Field Tables**

#### **Authentication Request Fields**

| Field    | Type   | Required | Description                               |
| -------- | ------ | -------- | ----------------------------------------- |
| email    | String | Yes      | User's email address (serves as username) |
| password | String | Yes      | User's password for authentication        |

#### **Create User Request Fields**

| Field          | Type   | Required | Description                              |
| -------------- | ------ | -------- | ---------------------------------------- |
| username       | String | Yes      | Unique alphanumeric username             |
| email          | String | Yes      | Unique email address for login           |
| password       | String | Yes      | Password (BCrypt hashed on server)       |
| organizationId | UUID   | Yes      | UUID of the organization user belongs to |
| roleId         | UUID   | Yes      | UUID of the role to assign to user       |

---

## **Response Body Examples**

### **Authentication Success Response**

```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbkB0ZXN0LmNvbSIsImlhdCI6MTY0MjY4MDAwMCwiZXhwIjoxNjQyNzY2NDAwfQ.signature",
  "type": "Bearer",
  "user": {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "username": "admin",
    "email": "admin@test.com",
    "status": "active",
    "lastAccessAt": "2025-07-30T10:30:00Z",
    "organization": {
      "id": "660e8400-e29b-41d4-a716-446655440001",
      "organizationName": "Test Organization"
    },
    "role": {
      "id": "770e8400-e29b-41d4-a716-446655440002",
      "roleName": "Admin",
      "roleType": "system"
    }
  }
}
```

### **Create User Success Response**

```json
{
  "id": "880e8400-e29b-41d4-a716-446655440003",
  "username": "johnsmith",
  "email": "john.smith@deloitte.com",
  "status": "active",
  "createdAt": "2024-01-15T10:30:00Z",
  "organization": {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "organizationName": "Acme Corporation"
  },
  "role": {
    "id": "660e8400-e29b-41d4-a716-446655440001",
    "roleName": "User",
    "roleType": "system"
  }
}
```

### **Error Response Example**

```json
{
  "timestamp": "2025-07-30T10:30:00Z",
  "status": 401,
  "error": "Unauthorized",
  "message": "Invalid email or password",
  "path": "/api/auth/login"
}
```

### **Response Field Tables**

#### **Authentication Success Response Fields**

| Field             | Type   | Description                                      |
| ----------------- | ------ | ------------------------------------------------ |
| token             | String | JWT access token (24-hour expiration)            |
| type              | String | Token type (always "Bearer")                     |
| user              | Object | Complete user profile with organization and role |
| user.id           | UUID   | Unique user identifier                           |
| user.username     | String | User's display name                              |
| user.email        | String | User's email address                             |
| user.status       | String | Account status (active/deactivated)              |
| user.organization | Object | Organization details                             |
| user.role         | Object | Assigned role details                            |

---

## **Business Logic Explanation**

### **Authentication Flow**

The system implements JWT-based authentication with the following security measures:

1. **Credential Validation** - Email and BCrypt password verification
2. **Token Generation** - HMAC-SHA256 signed JWT with user context
3. **Token Validation** - Request-level token verification via security filters
4. **Session Management** - Stateless authentication with token-based sessions
5. **Security Context** - Spring Security integration for authorization decisions

### **Multi-Tenant Security Model**

#### **Organization Isolation**

- All major business entities reference Organization for tenant separation
- Database-level isolation through organization_id foreign keys
- API-level filtering ensures cross-tenant data access prevention

#### **Role-Based Permissions**

- **Feature-Based Access Control** - Permissions tied to specific system features
- **Permission Levels** - Read, write, list, and modify access granularity
- **Role Types** - System roles (built-in) and custom roles (organization-specific)
- **Dynamic Authorization** - Runtime permission evaluation based on role assignments

### **User Management Rules**

#### **Account Creation Requirements**

- **Unique Email** - Email serves as username and must be unique system-wide
- **Username Uniqueness** - Alphanumeric usernames must be unique within organization
- **Password Security** - BCrypt hashing with salt for password storage
- **Organization Assignment** - Every user must belong to exactly one organization
- **Role Assignment** - Every user must have exactly one role assignment

#### **User Status Management**

- **Active Status** - Normal operational status for authentication
- **Deactivated Status** - Prevents authentication while preserving audit trail
- **Last Access Tracking** - Records timestamp of last successful authentication

### **Team Collaboration Features**

#### **Team Structure**

- **Organization Scoped** - Teams exist within organization boundaries only
- **Member Management** - Users can belong to multiple teams within their organization
- **Team Roles** - Team-specific role assignments (separate from system roles)
- **Collaboration Context** - Teams provide grouping for budget management and cost allocation

### **Data Integrity & Security Guarantees**

#### **Database Security**

- **UUID Primary Keys** - Prevents enumeration attacks and ensures uniqueness
- **Audit Trail** - Complete created_by/updated_by tracking across all entities
- **Soft Deletion** - Logical deletion with deleted_at timestamps preserves audit history
- **Foreign Key Constraints** - Referential integrity maintained at database level

#### **Application Security**

- **Password Security** - BCrypt hashing prevents rainbow table attacks
- **Token Security** - JWT tokens signed with secret key, configurable expiration
- **CORS Configuration** - Controlled cross-origin access for web applications
- **Input Validation** - Request-level validation prevents injection attacks

#### **Compliance Features**

- **Audit Logging** - Complete action audit trail for compliance requirements
- **Data Retention** - Soft deletion preserves data for audit and compliance
- **Access Control** - Comprehensive authorization controls meet security standards
- **Multi-Tenancy** - Complete tenant isolation ensures data privacy compliance

---

## **API Call Flow Description**

```
User Authentication Flow
User
 │
 │ 1. Submit login credentials (POST /api/auth/login)
 ▼
AuthController.login
 │
 │ 2. Extract LoginRequest (email, password)
 │
 │ 3. Call AuthService.authenticateUser
 │      └─ If credentials invalid:
 │            ├─ Return 401 Unauthorized
 │            └─ Include error message
 │
 │ 4. Successful authentication flow:
 │      ├─ Load UserDetails via UserDetailsServiceImpl
 │      ├─ Validate password using BCrypt
 │      ├─ Generate JWT token using JwtUtils
 │      ├─ Build JwtResponse with user details
 │      └─ Return 200 OK with token and user info
 │
 ▼
JWT Token returned to client for subsequent API calls

User Creation Flow
Admin User
 │
 │ 1. Submit create user form (POST /api/users)
 ▼
UserController.createUser
 │
 │ 2. Extract CreateUserRequest with user details
 │
 │ 3. Validate required fields and constraints
 │      └─ If validation fails:
 │            ├─ Return 400 Bad Request
 │            └─ Include field-specific error messages
 │
 │ 4. Call UserService.createUser
 │      ├─ Check email uniqueness constraint
 │      ├─ Check username uniqueness within organization
 │      ├─ Validate organization and role existence
 │      └─ If business rule violation:
 │            ├─ Return 409 Conflict
 │            └─ Include constraint violation details
 │
 │ 5. User creation process:
 │      ├─ Hash password using BCrypt
 │      ├─ Create User entity with audit fields
 │      ├─ Persist to t_mt_users table
 │      ├─ Log creation event for audit trail
 │      └─ Return UserDto with created user details
 │
 ▼
User successfully created and ready for authentication
```

### **1. Authentication Controller Flow**

├── **Request Processing** - Extract and validate login credentials\
├── **AuthService.authenticateUser()** - Core authentication business logic\
├── **UserDetailsServiceImpl.loadUserByUsername()** - Load user by email\
└── **JwtUtils.generateJwtToken()** - Create signed JWT with user context

### **2. Security Filter Chain**

├── **JwtRequestFilter.doFilterInternal()** - Intercept all API requests\
├── **JWT Token Extraction** - Parse Authorization header for Bearer token\
├── **Token Validation** - Verify signature and expiration using JwtUtils\
└── **Security Context Setup** - Establish authentication for request processing

### **3. User Management Business Logic**

├── **UserService.createUser()** - Main user creation orchestration\
├── **Validation Layer** - Email uniqueness, username format, password strength\
├── **Organization Context** - Validate organization membership and permissions\
└── **Role Assignment** - Assign role and establish feature permissions

### **4. Role-Based Authorization**

├── **RoleFeaturePermission Evaluation** - Check user's role permissions for requested feature\
├── **Permission Level Validation** - Verify read/write/modify access for specific operations\
├── **Feature-Based Access Control** - Grant or deny access based on role configuration\
└── **Dynamic Permission Resolution** - Runtime evaluation of user permissions

### **5. Team Collaboration Integration**

├── **Team Membership Resolution** - Determine user's team memberships within organization\
├── **Team-Based Data Filtering** - Apply team context for collaborative features\
├── **Team Role Evaluation** - Consider team-specific role assignments\
└── **Collaboration Context** - Establish team boundaries for shared resources

### **6. Error Handling & Response Generation**

├── **Validation Error Processing** - Format field-level validation errors\
├── **Business Logic Error Handling** - Handle constraint violations and business rule failures\
├── **Security Error Processing** - Format authentication and authorization errors\
└── **Standardized Response Format** - Consistent error response structure across all endpoints

---
