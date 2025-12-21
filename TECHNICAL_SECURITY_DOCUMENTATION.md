# MyVehicleHub - Technical Security Documentation

## 🏗️ Architecture Overview

**Authentication Type:** JWT-based stateless authentication with refresh token rotation  
**Session Management:** Stateless (no server-side sessions)  
**Token Storage:** Refresh tokens in PostgreSQL, access tokens client-side only  
**Encryption:** BCrypt for passwords, HS256 for JWT signing  

---

## 🔐 Authentication Flow

### **1. Registration Flow**

```
Client                          Backend                         Database
  |                                |                                |
  |-- POST /auth/register -------->|                                |
  |    {email, password, ...}      |                                |
  |                                |-- Hash password (BCrypt) ----->|
  |                                |                                |
  |                                |-- Create user --------------->|
  |                                |   - role: USER                 |
  |                                |   - authProvider: EMAIL        |
  |                                |                                |
  |<-- 201 Created ----------------|                                |
  |    {userId}                    |                                |
```

### **2. Login Flow**

```
Client                          Backend                         Database
  |                                |                                |
  |-- POST /auth/login ----------->|                                |
  |    {email, password}           |                                |
  |                                |-- Authenticate user ---------->|
  |                                |   (verify password)            |
  |                                |                                |
  |                                |-- Generate JWT (HS256) --------|
  |                                |   Expiry: 30 min               |
  |                                |                                |
  |                                |-- Create refresh token ------->|
  |                                |   UUID + save to DB            |
  |                                |   Expiry: 90 days              |
  |                                |                                |
  |<-- 200 OK --------------------|                                |
  |    {jwtToken, refreshToken}    |                                |
```

### **3. API Request Flow**

```
Client                          Backend (JwtFilter)             Business Logic
  |                                |                                |
  |-- GET /user/profile ---------->|                                |
  |    Authorization: Bearer JWT   |                                |
  |                                |                                |
  |                                |-- Extract JWT from header -----|
  |                                |                                |
  |                                |-- Validate JWT ----------------|
  |                                |   - Verify signature           |
  |                                |   - Check expiration           |
  |                                |   - Extract username           |
  |                                |                                |
  |                                |-- Load UserDetails ----------->|
  |                                |                                |
  |                                |-- Set Authentication --------->|
  |                                |   in SecurityContext           |
  |                                |                                |
  |                                |                  Execute business logic
  |                                |                                |
  |<-- 200 OK ---------------------|<-------------------------------|
  |    {user data}                 |                                |
```

### **4. Token Refresh Flow**

```
Client                          Backend                         Database
  |                                |                                |
  |-- POST /auth/refreshToken ---->|                                |
  |    {refreshToken: UUID}        |                                |
  |                                |                                |
  |                                |-- Find token in DB ----------->|
  |                                |<-- Token data -----------------|
  |                                |                                |
  |                                |-- Verify expiration ---------->|
  |                                |   If expired: delete & error   |
  |                                |                                |
  |                                |-- Generate new JWT ------------|
  |                                |   Expiry: 30 min               |
  |                                |                                |
  |                                |-- Delete old refresh token --->|
  |                                |                                |
  |                                |-- Create new refresh token --->|
  |                                |   (Token Rotation)             |
  |                                |                                |
  |<-- 200 OK --------------------|                                |
  |    {accessToken, refreshToken} |                                |
```

---

## 🗄️ Database Schema

### **users Table**

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | UUID | PRIMARY KEY | User identifier |
| email | VARCHAR | UNIQUE, NOT NULL | User email |
| password | VARCHAR | NOT NULL | BCrypt hashed password |
| first_name | VARCHAR | | User's first name |
| last_name | VARCHAR | | User's last name |
| age | INTEGER | | User's age |
| role | VARCHAR(20) | NOT NULL, DEFAULT 'USER' | USER, ADMIN, MODERATOR |
| auth_provider | VARCHAR(20) | NOT NULL | EMAIL, GOOGLE, FACEBOOK, APPLE |

**Indexes:**
- PRIMARY KEY on `id`
- UNIQUE INDEX on `email`

### **refresh_tokens Table**

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT | Token identifier |
| token | VARCHAR(255) | UNIQUE, NOT NULL | UUID refresh token |
| user_id | UUID | FOREIGN KEY → users(id) | Owner of token |
| expiry_date | TIMESTAMP | NOT NULL | When token expires |
| created_date | TIMESTAMP | NOT NULL | When token was created |
| revoked | BOOLEAN | NOT NULL, DEFAULT false | Manual revocation flag |
| ip_address | VARCHAR(45) | | IP where token was created |
| user_agent | VARCHAR(255) | | Browser/device info |
| device_id | VARCHAR(255) | | Device identifier |

**Indexes:**
- PRIMARY KEY on `id`
- UNIQUE INDEX on `token`
- INDEX on `user_id` (for faster lookups)

**Foreign Keys:**
- `user_id` → `users(id)` (NO CASCADE to prevent accidental user deletion)

---

## 🔧 Configuration

### **Application Configuration**

The system uses externalized configuration for all sensitive and environment-specific settings:

**Database Settings:**
- PostgreSQL connection URL, username, and password
- Hibernate auto-update enabled for schema management

**JWT Settings:**
- Secret key stored as base64-encoded string (minimum 256 bits for HS256 algorithm)
- Token expiration set to 30 minutes (1800000 milliseconds)

**Refresh Token Settings:**
- Expiration period configured to 90 days (7776000 seconds)

**OAuth2 Settings:**
- Google client ID and secret for OAuth2 integration
- Used for "Sign in with Google" functionality

---

## 🛡️ Security Configuration

### **SecurityFilterChain Architecture**

The security filter chain is configured with the following characteristics:

**CSRF Protection:**
- Disabled for stateless REST API architecture
- Not applicable since authentication uses JWT tokens in headers, not cookies

**Request Authorization:**
- All authentication endpoints under `/api/v1/auth/**` are publicly accessible
- All other endpoints require valid JWT authentication

**Exception Handling:**
- Custom authentication entry point returns 401 Unauthorized for authentication failures
- Custom access denied handler returns 403 Forbidden for authorization failures

**Session Management:**
- Stateless session creation policy (no server-side sessions)
- Enables horizontal scaling and load balancing

**Filter Chain:**
- JWT filter executes before Spring Security's UsernamePasswordAuthenticationFilter
- Extracts and validates JWT tokens on every request

### **JWT Filter (JwtFilter.java)**

**Execution order:** Runs before `UsernamePasswordAuthenticationFilter`

**Process:**
1. Extract JWT from `Authorization: Bearer <token>` header
2. Validate token signature and expiration
3. Extract username from token
4. Load UserDetails from database
5. Create Authentication object
6. Set in SecurityContext
7. Continue filter chain

**Skip filter for:**
- `/api/v1/auth/**` endpoints (public)

---

## 📡 API Endpoints

### **Public Endpoints (No Authentication Required)**

| Method | Endpoint | Description | Request Body | Response |
|--------|----------|-------------|--------------|----------|
| POST | `/api/v1/auth/register` | Register with email | `{email, password, firstName, lastName, age}` | `{userId}` |
| POST | `/api/v1/auth/login` | Login with email | `{email, password}` | `{jwtToken, refreshToken}` |
| POST | `/api/v1/auth/google` | Login with Google | `{idToken}` | `{jwtToken, refreshToken}` |
| POST | `/api/v1/auth/refreshToken` | Refresh access token | `{refreshToken}` | `{accessToken, refreshToken, tokenType, expiresIn}` |

### **Protected Endpoints (Authentication Required)**

| Method | Endpoint | Description | Request Body | Response |
|--------|----------|-------------|--------------|----------|
| POST | `/api/v1/auth/logout` | Logout current device | `{refreshToken}` | `"Logged out successfully"` |
| POST | `/api/v1/auth/logout-all` | Logout all devices | - | `"Logged out from all devices successfully"` |

**Authentication:** All protected endpoints require `Authorization: Bearer <jwt_token>` header

---

## 🔑 JWT Token Structure

### **Token Components**

**Header:**
- Algorithm: HS256 (HMAC with SHA-256)
- Type: JWT

**Payload:**
- Subject (sub): User's email address
- Issued At (iat): Unix timestamp when token was created
- Expiration (exp): Unix timestamp when token expires (30 minutes from issue time)

**Signature:**
- Generated using HMACSHA256 algorithm
- Combines base64-encoded header and payload
- Signed with the application's secret key
- Ensures token integrity and authenticity

**Important Security Note:** 
User role is intentionally NOT included in the JWT payload. Role information is fetched from the database on each request to ensure permissions are always up-to-date. This prevents issues where a user's role is changed but old tokens still grant incorrect permissions.

---

## 🎭 Role-Based Access Control (RBAC)

### **Role Hierarchy**

The system implements a three-tier role hierarchy with inherited permissions:

**ADMIN (Highest Level):**
- Full system access and control
- User management capabilities
- Role assignment and modification
- All USER permissions

**MODERATOR (Middle Level):**
- Content moderation capabilities
- Report handling and management
- All USER permissions

**USER (Base Level):**
- Manage own data and profile
- CRUD operations on vehicles
- Access to standard application features

### **Implementation Approach**

**Method-Level Security:**
- Annotations can be used to protect specific service methods
- Supports single role requirement (e.g., ADMIN only)
- Supports multiple role requirements (e.g., ADMIN or MODERATOR)

**URL-Level Security:**
- Endpoints can be protected based on path patterns
- Admin endpoints restricted to ADMIN role

---

## 🔄 Token Rotation Strategy

**Why:** Prevents token replay attacks and limits exposure window

**How it works:**
```
1. User calls /auth/refreshToken with refresh token A
2. Server validates token A
3. Server generates new access token
4. Server deletes token A from database
5. Server generates new refresh token B
6. Server saves token B to database
7. Server returns both tokens
8. Token A is now invalid (one-time use)
```

**Benefits:**
- Stolen refresh token only works once
- Attacker cannot repeatedly get new access tokens
- Natural cleanup of old tokens

---

## 🧹 Automatic Cleanup

### **Scheduled Cleanup Task**

The system implements an automated cleanup mechanism that runs on a daily schedule:

**Execution Schedule:**
- Runs every day at midnight (00:00:00)
- Uses cron-style scheduling

**Functionality:**
- Identifies all refresh tokens with expiration dates in the past
- Removes expired tokens from the database
- Prevents database bloat and maintains optimal performance

**Transaction Management:**
- Cleanup operations are executed within a database transaction
- Ensures data consistency and atomicity

---

## 🚨 Exception Handling

### **Global Exception Handler**

| Exception | HTTP Status | Response | Use Case |
|-----------|-------------|----------|----------|
| `InvalidTokenException` | 401 | `{error, message}` | JWT validation fails |
| `ExpiredRefreshTokenException` | 401 | `{error, message}` | Refresh token expired |
| `ResponseStatusException` | Variable | `{error, message}` | Service layer errors |
| `HttpMessageNotReadableException` | 400 | `{error, message}` | Invalid JSON |
| `MethodArgumentNotValidException` | 400 | `{error, message}` | Validation errors |
| `Exception` | 500 | `{error, message}` | Unexpected errors |

### **Authentication vs Authorization Errors**

**401 Unauthorized:**
- Missing JWT token
- Invalid JWT token
- Expired JWT token
- Invalid credentials
→ "We don't know who you are"

**403 Forbidden:**
- Valid JWT but insufficient role
- USER trying to access ADMIN endpoint
→ "We know who you are, but you can't do this"

---

## 🔒 Security Best Practices Implemented

### **1. Password Security**
- ✅ BCrypt hashing (cost factor 10)
- ✅ Salted (automatic with BCrypt)
- ✅ Never logged or exposed in API

### **2. JWT Security**
- ✅ HS256 algorithm (HMAC with SHA-256)
- ✅ Secret key minimum 256 bits
- ✅ Short expiration (30 minutes)
- ✅ Signature verified on every request

### **3. Refresh Token Security**
- ✅ Random UUID (practically impossible to guess)
- ✅ Stored in database (can be revoked)
- ✅ One-time use (rotation)
- ✅ Long but limited expiration (90 days)

### **4. CSRF Protection**
- ✅ Disabled for stateless REST API
- ✅ Not needed (no cookies, no sessions)
- ✅ JWT in Authorization header (not vulnerable to CSRF)

### **5. Session Security**
- ✅ Stateless (no server-side sessions)
- ✅ Scalable across load balancers
- ✅ No session fixation attacks

---

## 📊 Performance Considerations

### **Database Queries**

| Operation | Queries | Indexes Used | Performance |
|-----------|---------|--------------|-------------|
| Login | 1 (find user by email) | email (unique) | O(log n) |
| JWT validation | 1 (load UserDetails) | email | O(log n) |
| Refresh token | 2 (find + delete/create) | token (unique), user_id | O(log n) |
| Logout | 1 (delete token) | token (unique) | O(log n) |
| Logout all | 1 (delete by user_id) | user_id | O(k) where k = user's tokens |

### **Optimization**

**Implemented:**
- Database indexes on frequently queried columns
- Stateless architecture (no session storage)
- JWT validation without database lookup (signature check)

---

**Last Updated:** December 21, 2025  
**Version:** 1.0


