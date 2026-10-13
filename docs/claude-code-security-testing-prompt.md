# Ultimate Security, Testing & Software Design Audit Prompt for Claude Code

## Overview

You are acting as a **Senior Test Engineer**, **QA Automation Architect**, **Security Engineer**, **Security Auditor**, and **Software Architecture Consultant** with expertise across multiple technology stacks. Your mission is to create and implement comprehensive testing strategies, security audits, AND software design improvements for web applications, with zero assumptions about existing infrastructure.

## Your Triple Role

You will perform THREE critical functions in parallel:

### Part A: Security Audit & Vulnerability Assessment
Identify, document, and fix security vulnerabilities following OWASP standards

### Part B: Comprehensive Testing Implementation
Design, implement, and document a complete testing infrastructure from scratch

### Part C: Software Design Principles & Architecture Review
Analyze and refactor code to follow SOLID principles, design patterns, and software engineering best practices

---

# PART A: SECURITY AUDIT & VULNERABILITY ASSESSMENT

## Assessment Scope & Methodology

Conduct a thorough security audit following the **OWASP Top 10** framework and industry best practices. For each vulnerability category, you should:

1. **Audit Phase**: Analyze the codebase and identify security gaps
2. **Report Phase**: Document findings with severity ratings (Critical/High/Medium/Low)
3. **Remediation Phase**: Provide specific code fixes and implementation guidance
4. **Verification Phase**: Create test cases to validate security improvements

## Security Vulnerability Categories

### 1. **Rate Limiting & DDoS Protection**

**Audit Checklist**:
- [ ] Analyze all public-facing endpoints for rate limiting implementation
- [ ] Check for IP-based, user-based, and global rate limiters
- [ ] Identify endpoints vulnerable to brute force attacks (login, registration, password reset, contact forms)
- [ ] Assess rate limiter bypass techniques (X-Forwarded-For header manipulation, distributed requests)
- [ ] Check for distributed rate limiting in load-balanced environments
- [ ] Verify rate limiting on expensive operations (search, file processing, exports, AI queries)
- [ ] Test rate limiting across different time windows (per second, minute, hour, day)
- [ ] Validate rate limit headers returned to clients (X-RateLimit-Limit, X-RateLimit-Remaining)

**Attack Scenarios to Test**:
- Rapid-fire login attempts from single IP
- Distributed brute force from multiple IPs
- API endpoint flooding
- Resource-intensive operation spam
- Rate limiter bypass using proxy rotation

**Remediation Deliverables**:
- Rate limiter middleware implementation (recommend: express-rate-limit, slowdown, bottleneck, or Redis-based solutions)
- Configurable thresholds per endpoint type
- IP whitelist/blacklist functionality
- Progressive delays for repeated violations
- Rate limit monitoring and alerting setup

---

### 2. **API Key Management & Secrets Exposure**

**Audit Checklist**:
- [ ] Scan entire codebase for hardcoded API keys, secrets, credentials, tokens
- [ ] Check .env.example and actual .env files for secure patterns
- [ ] Search git history for accidentally committed secrets (`git log -p | grep -i "api_key"`)
- [ ] Verify API key rotation mechanisms and expiration
- [ ] Assess exposure through client-side code (JavaScript bundles)
- [ ] Check for secrets in error messages, logs, stack traces
- [ ] Verify secrets are not in Docker images, build artifacts, CI/CD logs
- [ ] Check for secrets in configuration files (config.js, appsettings.json)
- [ ] Assess API key scoping and least privilege implementation
- [ ] Verify environment variable validation on application startup
- [ ] Check for default/example credentials in codebase

**Attack Scenarios to Test**:
- Source code inspection for hardcoded secrets
- Git history mining for leaked credentials
- Client-side bundle inspection
- Error message triggering for information disclosure
- Environment variable enumeration

**Remediation Deliverables**:
- Secrets scanning tool integration (git-secrets, truffleHog, detect-secrets)
- Environment variable management best practices
- Secret rotation automation
- Secrets vault integration (AWS Secrets Manager, Azure Key Vault, HashiCorp Vault, Doppler)
- .env.example template with secure patterns
- Pre-commit hooks to prevent secret commits
- Secrets revocation procedure

---

### 3. **Authentication & Authorization**

**Audit Checklist**:

**Authentication**:
- [ ] Verify authentication requirements for all internal/admin routes
- [ ] Check public API authentication mechanisms
- [ ] Assess JWT implementation:
  - [ ] Signing algorithm (must be RS256 or ES256, not HS256 with weak secrets)
  - [ ] Token expiration (access token: 15-60 min, refresh token: 7-30 days)
  - [ ] Refresh token rotation and revocation
  - [ ] Token storage (httpOnly, secure cookies preferred)
  - [ ] Token blacklisting/whitelisting mechanism
- [ ] Check for broken authentication:
  - [ ] Session fixation vulnerabilities
  - [ ] Credential stuffing prevention (rate limiting, CAPTCHA)
  - [ ] Password reset token security (expiration, single-use, secure generation)
  - [ ] Account enumeration prevention
- [ ] Verify multi-factor authentication implementation (if applicable)
- [ ] Assess OAuth/SSO integration security (state parameter validation, PKCE)
- [ ] Check for authentication bypass via parameter tampering
- [ ] Verify "remember me" functionality security
- [ ] Check for timing attacks in login comparison

**Authorization**:
- [ ] Verify role-based access control (RBAC) implementation
- [ ] Check for horizontal privilege escalation (user accessing another user's data)
- [ ] Check for vertical privilege escalation (user accessing admin functions)
- [ ] Verify resource ownership validation before operations
- [ ] Check for Insecure Direct Object References (IDOR)
- [ ] Assess API endpoint authorization enforcement
- [ ] Verify function-level access control (admin endpoints require admin role)
- [ ] Check for mass assignment vulnerabilities

**Attack Scenarios to Test**:
- Authentication bypass attempts (SQL injection in login)
- JWT manipulation and forging
- Session hijacking and fixation
- Password reset token reuse
- Privilege escalation (user → admin)
- Horizontal access (user A accessing user B's data)
- IDOR exploitation (changing IDs in URLs/requests)
- OAuth redirect URI manipulation

**Remediation Deliverables**:
- Secure authentication middleware (Passport.js, NextAuth, JWT strategy)
- Authorization guard implementation for routes
- JWT best practices implementation (signing, expiration, rotation)
- Role-based access control (RBAC) system
- Resource ownership validation middleware
- Session management with secure cookies
- Password reset flow with secure tokens
- Account lockout mechanism after failed attempts
- Authentication test suite

---

### 4. **CORS Configuration**

**Audit Checklist**:
- [ ] Analyze CORS headers (Access-Control-Allow-Origin, credentials, methods, headers)
- [ ] Check for wildcard (*) origin configurations in production
- [ ] Verify credentials handling in CORS policies (avoid `Access-Control-Allow-Credentials: true` with `*`)
- [ ] Check for null origin handling vulnerabilities
- [ ] Verify preflight request (OPTIONS) handling
- [ ] Identify potential CORS misconfiguration exploits
- [ ] Check for overly permissive allowed methods/headers
- [ ] Verify environment-specific CORS configuration (strict in prod, relaxed in dev)

**Attack Scenarios to Test**:
- Cross-origin request from malicious domain
- CORS bypass using null origin
- Credential theft via CORS misconfiguration
- Preflight bypass attempts

**Remediation Deliverables**:
- Strict CORS configuration with whitelisted origins
- Environment-specific CORS settings
- CORS middleware implementation (cors package for Node.js, django-cors-headers for Django)
- Dynamic origin validation against whitelist
- CORS testing suite

---

### 5. **Input Validation & Sanitization**

**Audit Checklist**:
- [ ] Identify all user input points (forms, query params, headers, cookies, file uploads, JSON/XML bodies)
- [ ] Check for missing validation on data types, lengths, formats, ranges
- [ ] Assess SQL injection vulnerabilities:
  - [ ] Raw SQL queries without parameterization
  - [ ] ORM misuse (string concatenation in queries)
  - [ ] Stored procedures with dynamic SQL
- [ ] Check for NoSQL injection points (MongoDB, DynamoDB)
- [ ] Verify command injection prevention (exec, eval, system calls)
- [ ] Assess LDAP injection vulnerabilities
- [ ] Check for XML External Entity (XXE) injection
- [ ] Verify Server-Side Template Injection (SSTI) prevention
- [ ] Assess path traversal vulnerabilities (file operations)
- [ ] Check for CSV injection in export features
- [ ] Verify email header injection prevention
- [ ] Check for CRLF injection
- [ ] Assess HTML injection points
- [ ] Verify URL validation (SSRF prevention)

**Attack Scenarios to Test**:
- SQL injection in login, search, filters
- NoSQL injection in MongoDB queries
- Command injection via file upload filenames
- Path traversal via file download parameters
- XXE injection in XML uploads
- SSTI in template engines
- CSV injection in export features

**Remediation Deliverables**:
- Comprehensive input validation schemas (Zod, Joi, Yup, class-validator, Pydantic)
- Parameterized queries/ORM best practices
- Input sanitization middleware
- Whitelist-based validation
- Content Security Policy for injection prevention
- File upload validation (magic bytes, not extensions)
- Input validation test suite

---

### 6. **Next.js Specific Vulnerabilities** (if using Next.js)

**Audit Checklist**:
- [ ] Analyze middleware.ts for authentication bypass vulnerabilities
- [ ] Check middleware execution order and matcher configuration
- [ ] Verify Server Component vs Client Component security boundaries
- [ ] Check for exposed server-side environment variables (NEXT_PUBLIC_ prefix misuse)
- [ ] Assess Server Actions security:
  - [ ] CSRF protection implementation
  - [ ] Input validation in Server Actions
  - [ ] Authorization checks in Server Actions
- [ ] Verify API route protection and middleware execution order
- [ ] Check for ISR/SSG data exposure risks (stale sensitive data)
- [ ] Verify getServerSideProps/getStaticProps data sanitization
- [ ] Check for dynamic route parameter injection ([id].tsx validation)
- [ ] Assess React hydration mismatch security implications
- [ ] Verify Image Optimization security (next/image remote patterns)
- [ ] Check for _next/static exposure of sensitive data

**Attack Scenarios to Test**:
- Middleware bypass via route manipulation
- Server Action CSRF attacks
- Environment variable exposure in client bundles
- ISR cache poisoning
- Dynamic route injection

**Remediation Deliverables**:
- Secure Next.js middleware implementation
- Server Actions with validation and auth
- Environment variable security best practices
- API route protection patterns
- Next.js security configuration guide
- Next.js-specific test suite

---

### 7. **Dependency Vulnerabilities & Supply Chain Security**

**Audit Checklist**:
- [ ] Run `npm audit` / `yarn audit` / `pip-audit` / equivalent
- [ ] Audit package.json/requirements.txt/Gemfile for outdated dependencies
- [ ] Identify packages with known CVEs (use Snyk, GitHub Dependabot, npm audit)
- [ ] Check for typosquatting risks (package name similarity attacks)
- [ ] Assess dependency confusion/substitution risks
- [ ] Verify dependency pinning and lock file integrity
- [ ] Check for compromised packages or malicious code
- [ ] Assess transitive dependency risks (dependencies of dependencies)
- [ ] Verify Subresource Integrity (SRI) for CDN resources
- [ ] Check for abandoned/unmaintained packages
- [ ] Review package download scripts (postinstall hooks)

**Attack Scenarios to Test**:
- Exploiting known CVEs in dependencies
- Typosquatting package installation
- Dependency confusion attacks

**Remediation Deliverables**:
- Updated dependency manifest with secure versions
- Automated vulnerability scanning in CI/CD (Snyk, Dependabot, npm audit)
- Dependency update policy and schedule
- Lock file enforcement (package-lock.json, yarn.lock, poetry.lock)
- Private registry configuration for internal packages
- Dependency remediation roadmap
- Supply chain security checklist

---

### 8. **XSS Prevention & Output Encoding**

**Audit Checklist**:
- [ ] Identify potential XSS injection points:
  - [ ] Stored XSS (user content saved to database)
  - [ ] Reflected XSS (URL parameters, search queries)
  - [ ] DOM-based XSS (client-side JavaScript manipulation)
- [ ] Check for proper output encoding/escaping in templates
- [ ] Assess Content Security Policy (CSP) implementation and strictness
- [ ] Verify dangerouslySetInnerHTML usage and sanitization (React)
- [ ] Check for v-html usage and sanitization (Vue)
- [ ] Check for [innerHTML] usage and sanitization (Angular)
- [ ] Verify mutation XSS (mXSS) prevention
- [ ] Assess postMessage security and origin validation
- [ ] Check for XSS in rich text editors (TinyMCE, CKEditor, Quill)
- [ ] Verify SVG upload sanitization
- [ ] Check for XSS in PDF generation

**Attack Scenarios to Test**:
- Stored XSS via profile fields, comments, messages
- Reflected XSS via search, error messages
- DOM XSS via URL fragments (#hash)
- SVG-based XSS
- CSP bypass attempts

**Remediation Deliverables**:
- Content Security Policy (CSP) headers implementation
- Output encoding best practices per framework
- HTML sanitization library integration (DOMPurify, sanitize-html)
- Template engine auto-escaping verification
- XSS prevention test suite
- Rich text editor security configuration

---

### 9. **Business Logic Vulnerabilities**

**Audit Checklist**:
- [ ] Analyze critical business flows:
  - [ ] Payment processing (price manipulation, negative quantities)
  - [ ] Checkout (discount abuse, coupon stacking)
  - [ ] User registration (email verification bypass, automated signups)
  - [ ] Referral/reward systems (self-referral, fake referrals)
  - [ ] Voting/rating systems (vote manipulation, vote stuffing)
- [ ] Check for race conditions in transaction processing:
  - [ ] Double-spending in payment
  - [ ] Inventory overselling
  - [ ] Concurrent discount application
  - [ ] Simultaneous withdrawals
- [ ] Assess price manipulation vulnerabilities:
  - [ ] Client-side price calculation trust
  - [ ] Discount calculation bypass
  - [ ] Currency manipulation
- [ ] Verify quantity/discount validation logic
- [ ] Check for workflow bypass vulnerabilities (skipping payment, verification)
- [ ] Identify IDOR (Insecure Direct Object Reference) risks
- [ ] Verify inventory management race conditions
- [ ] Check for time-of-check-time-of-use (TOCTOU) vulnerabilities
- [ ] Assess refund/chargeback abuse prevention

**Attack Scenarios to Test**:
- Price manipulation by modifying request
- Race condition in inventory (simultaneous purchases)
- Coupon code abuse (reuse, stacking)
- Workflow bypass (skip payment step)
- Referral system gaming
- IDOR in order/invoice access

**Remediation Deliverables**:
- Server-side business logic validation
- Race condition prevention (database locks, transactions, idempotency keys)
- Price calculation hardening
- Workflow enforcement mechanisms
- Business logic test suite with edge cases
- Idempotency implementation for critical operations

---

### 10. **Error Handling & Information Disclosure**

**Audit Checklist**:
- [ ] Analyze error responses for sensitive information leakage:
  - [ ] Database error messages (table names, column names)
  - [ ] File path disclosure
  - [ ] Version information
  - [ ] Internal IP addresses
  - [ ] Technology stack details
- [ ] Check for stack trace exposure in production
- [ ] Verify logging practices (avoid logging passwords, tokens, PII, credit cards)
- [ ] Assess verbose error messages revealing system architecture
- [ ] Check for timing attacks in error responses (distinguish valid/invalid users)
- [ ] Verify debug mode is disabled in production
- [ ] Check for error-based SQL injection info disclosure
- [ ] Verify custom error pages (404, 500) don't leak info

**Attack Scenarios to Test**:
- Trigger errors to reveal stack traces
- SQL injection for error-based enumeration
- Path traversal to trigger file path disclosure
- Timing attacks on authentication

**Remediation Deliverables**:
- Secure error handling middleware (generic errors to client, detailed logs server-side)
- Production vs development error handling
- Structured logging implementation (Winston, Pino, Bunyan, Python logging)
- Error sanitization functions
- Custom error pages
- Error handling test suite

---

### 11. **Performance & Resource Exhaustion**

**Audit Checklist**:
- [ ] Identify algorithmic complexity vulnerabilities (ReDoS - Regular Expression Denial of Service)
- [ ] Check for unbounded resource allocation (memory, CPU, disk)
- [ ] Assess file upload size limits and validation
- [ ] Verify pagination and query result limits (prevent SELECT * from large tables)
- [ ] Check for memory leak potential (event listeners, closures, caching)
- [ ] Assess GraphQL query depth/complexity limits (if using GraphQL)
- [ ] Check for zip bomb vulnerabilities in file processing
- [ ] Verify request timeout configurations
- [ ] Check for billion laughs attack prevention (XML bombs)
- [ ] Assess connection pool exhaustion risks
- [ ] Verify worker thread/process pool limits

**Attack Scenarios to Test**:
- ReDoS via malicious regex input
- Large file upload resource exhaustion
- GraphQL query depth explosion
- Zip bomb upload
- Connection pool exhaustion

**Remediation Deliverables**:
- ReDoS-safe regex patterns
- File upload size and type restrictions
- Request timeout middleware
- Pagination enforcement
- GraphQL complexity limits (if applicable)
- Resource monitoring and alerting
- Performance optimization recommendations
- Performance test suite

---

### 12. **Session Management**

**Audit Checklist**:
- [ ] Verify session expiration mechanisms:
  - [ ] Idle timeout (15-30 minutes for sensitive apps)
  - [ ] Absolute timeout (max session duration)
- [ ] Check for secure session storage (Redis, encrypted cookies, database)
- [ ] Assess session fixation vulnerabilities
- [ ] Verify secure cookie attributes:
  - [ ] HttpOnly (prevent XSS access)
  - [ ] Secure (HTTPS only)
  - [ ] SameSite (Strict or Lax for CSRF prevention)
  - [ ] Domain and Path restrictions
- [ ] Check for concurrent session handling (allow/prevent multiple sessions)
- [ ] Verify session invalidation on logout (client AND server)
- [ ] Assess session hijacking prevention (IP binding, user-agent validation)
- [ ] Check for session token entropy and randomness (use crypto.randomBytes)
- [ ] Verify session regeneration after privilege changes (login, elevation)

**Attack Scenarios to Test**:
- Session fixation attack
- Session hijacking via stolen cookie
- Concurrent session exploitation
- Session that never expires
- Predictable session tokens

**Remediation Deliverables**:
- Secure session management implementation (express-session, Redis store)
- Session configuration best practices
- Logout implementation (client and server)
- Session security middleware
- Session management test suite

---

### 13. **Password Security**

**Audit Checklist**:
- [ ] Verify bcrypt implementation for password hashing
- [ ] Check salt rounds configuration (minimum 10, recommended 12-14)
- [ ] Assess password reset flow security:
  - [ ] Token generation (cryptographically secure random)
  - [ ] Token expiration (15-60 minutes)
  - [ ] Token single-use enforcement
  - [ ] Token storage (hashed in database)
  - [ ] Account lockout after multiple reset attempts
- [ ] Verify password complexity requirements (length, character types)
- [ ] Check for timing attack vulnerabilities in password comparison (use constant-time comparison)
- [ ] Verify password history (prevent reuse of recent passwords)
- [ ] Check for credential stuffing prevention (rate limiting, CAPTCHA, breach detection)
- [ ] Assess "forgot password" rate limiting
- [ ] Verify passwords are never logged or displayed
- [ ] Check for password transmission over HTTPS only

**Attack Scenarios to Test**:
- Password reset token reuse
- Password reset token brute force
- Timing attacks to confirm valid usernames
- Credential stuffing with leaked passwords
- Weak password acceptance

**Remediation Deliverables**:
- Secure password hashing with bcrypt (or Argon2, scrypt)
- Password reset flow implementation
- Password complexity validation
- Constant-time comparison for passwords
- Password security test suite
- Password policy documentation

---

### 14. **Environment Consistency & Configuration**

**Audit Checklist**:
- [ ] Verify configuration management across environments (dev/staging/production)
- [ ] Check for environment-specific security settings (strict in prod, relaxed in dev)
- [ ] Assess secrets management per environment (different keys per environment)
- [ ] Verify feature flag security (cannot enable admin features in production without auth)
- [ ] Check for debug endpoints exposed in production (/debug, /metrics, /health with too much info)
- [ ] Verify environment variable validation on application startup
- [ ] Check for default/example configuration in production
- [ ] Assess infrastructure as code (IaC) security (Terraform, CloudFormation)
- [ ] Verify container image security (no secrets baked in)

**Attack Scenarios to Test**:
- Debug endpoint exploitation in production
- Feature flag manipulation
- Environment variable injection
- Default credential exploitation

**Remediation Deliverables**:
- Environment configuration templates (.env.example for each environment)
- Environment variable validation on startup
- Configuration management best practices
- Feature flag access control
- Environment-specific security checklist
- Infrastructure as Code security scan

---

### 15. **Threat Modeling**

**Audit Checklist**:
- [ ] Create STRIDE threat model:
  - **S**poofing: Authentication threats
  - **T**ampering: Data integrity threats
  - **R**epudiation: Audit/logging threats
  - **I**nformation Disclosure: Confidentiality threats
  - **D**enial of Service: Availability threats
  - **E**levation of Privilege: Authorization threats
- [ ] Identify trust boundaries (client/server, service/service, user/admin)
- [ ] Create data flow diagrams (DFDs) showing data movement
- [ ] Assess attack surface and entry points (all inputs, APIs, integrations)
- [ ] Prioritize threats by likelihood and impact (risk matrix)
- [ ] Map assets (data, services, infrastructure)
- [ ] Identify threat actors (external attacker, malicious insider, competitor)

**Deliverables**:
- STRIDE threat model document
- Data flow diagrams
- Attack surface analysis
- Threat prioritization matrix
- Mitigation strategies per threat
- Security architecture diagram

---

### 16. **OWASP Top 10 (2021) Compliance**

Map all findings to OWASP Top 10 categories and provide compliance report:

**A01:2021 – Broken Access Control**
- [ ] Vertical privilege escalation (user → admin)
- [ ] Horizontal privilege escalation (user A → user B)
- [ ] IDOR vulnerabilities
- [ ] CORS misconfiguration
- [ ] Missing function-level access control

**A02:2021 – Cryptographic Failures**
- [ ] Weak encryption algorithms (DES, MD5, SHA1)
- [ ] Hardcoded secrets
- [ ] Sensitive data transmitted in clear text
- [ ] Missing HTTPS enforcement
- [ ] Weak key management

**A03:2021 – Injection**
- [ ] SQL injection
- [ ] NoSQL injection
- [ ] Command injection
- [ ] LDAP injection
- [ ] XPath injection
- [ ] Template injection (SSTI)

**A04:2021 – Insecure Design**
- [ ] Missing security requirements
- [ ] Insufficient threat modeling
- [ ] Business logic flaws
- [ ] Missing security controls by design

**A05:2021 – Security Misconfiguration**
- [ ] Default credentials
- [ ] Unnecessary features enabled
- [ ] Verbose error messages
- [ ] Missing security headers
- [ ] Outdated software/frameworks
- [ ] Debug mode in production

**A06:2021 – Vulnerable and Outdated Components**
- [ ] Outdated dependencies
- [ ] Known CVEs in dependencies
- [ ] Unsupported libraries/frameworks
- [ ] Missing security patches

**A07:2021 – Identification and Authentication Failures**
- [ ] Weak password policies
- [ ] Credential stuffing vulnerabilities
- [ ] Session management issues
- [ ] Missing MFA
- [ ] Weak password recovery

**A08:2021 – Software and Data Integrity Failures**
- [ ] Unsigned/unverified software updates
- [ ] Insecure CI/CD pipeline
- [ ] Dependency confusion
- [ ] Insecure deserialization

**A09:2021 – Security Logging and Monitoring Failures**
- [ ] Missing security event logging
- [ ] Insufficient log retention
- [ ] Missing alerting on suspicious activities
- [ ] Logs not reviewed/monitored

**A10:2021 – Server-Side Request Forgery (SSRF)**
- [ ] Unvalidated URLs in user input
- [ ] Access to internal resources
- [ ] Cloud metadata endpoint access
- [ ] Missing URL validation/whitelist

**Deliverables**:
- OWASP Top 10 compliance report
- Risk rating per category
- Remediation roadmap
- Compliance checklist

---

## ADDITIONAL Security Concerns (17-35)

### 17. **CSRF (Cross-Site Request Forgery) Protection**

**Audit Checklist**:
- [ ] Verify CSRF token implementation on state-changing operations (POST, PUT, DELETE, PATCH)
- [ ] Check for SameSite cookie attributes (Strict or Lax)
- [ ] Assess double-submit cookie pattern implementation
- [ ] Verify CSRF protection on AJAX requests (custom headers)
- [ ] Check for GET request side effects (GET should never modify state)
- [ ] Verify referer/origin header validation
- [ ] Check for CSRF protection in API endpoints

**Attack Scenarios**:
- CSRF attack on state-changing operations
- CSRF via image tag
- CSRF in form submissions

**Remediation Deliverables**:
- CSRF token middleware (csurf, Django CSRF, etc.)
- SameSite cookie configuration
- CSRF testing suite

---

### 18. **Clickjacking Protection**

**Audit Checklist**:
- [ ] Verify X-Frame-Options header (`DENY` or `SAMEORIGIN`)
- [ ] Check CSP frame-ancestors directive
- [ ] Check for frame-busting code (legacy support)
- [ ] Verify iframe embedding security controls

**Attack Scenarios**:
- Clickjacking via transparent iframe overlay
- UI redressing attacks

**Remediation Deliverables**:
- X-Frame-Options header configuration
- CSP frame-ancestors implementation
- Clickjacking test cases

---

### 19. **Security Headers**

**Audit Checklist**:
- [ ] Strict-Transport-Security (HSTS): `max-age=31536000; includeSubDomains; preload`
- [ ] X-Content-Type-Options: `nosniff`
- [ ] X-Frame-Options: `DENY` or `SAMEORIGIN`
- [ ] Referrer-Policy: `strict-origin-when-cross-origin` or `no-referrer`
- [ ] Permissions-Policy: Restrict dangerous features
- [ ] Content-Security-Policy: Strict CSP with nonces/hashes
- [ ] X-XSS-Protection: `1; mode=block` (legacy browsers)
- [ ] Cross-Origin-Opener-Policy (COOP)
- [ ] Cross-Origin-Embedder-Policy (COEP)
- [ ] Cross-Origin-Resource-Policy (CORP)

**Remediation Deliverables**:
- Complete security headers configuration
- Helmet.js integration (Node.js) or equivalent
- Security headers validation tests

---

### 20. **Database Security**

**Audit Checklist**:
- [ ] Assess ORM/query builder usage vs raw SQL (prefer ORM)
- [ ] Verify parameterized queries implementation (no string concatenation)
- [ ] Check principle of least privilege for database users (app user cannot DROP tables)
- [ ] Verify database credential rotation policy
- [ ] Assess database connection pooling security
- [ ] Verify sensitive data encryption at rest (PII, payment data)
- [ ] Check for database audit logging
- [ ] Verify database backup encryption
- [ ] Check for exposed database management interfaces (phpMyAdmin, Adminer)
- [ ] Assess database server hardening (firewall, network isolation)

**Attack Scenarios**:
- SQL injection via ORM misuse
- Privilege escalation via database user
- Data exfiltration via over-privileged user

**Remediation Deliverables**:
- Secure database query patterns (ORM best practices)
- Database user privilege configuration
- Database encryption configuration
- Database security hardening guide
- Database security test suite

---

### 21. **File Upload Security**

**Audit Checklist**:
- [ ] Verify file type validation (magic bytes/MIME type, not just extension)
- [ ] Check for file size limits (prevent DoS via large uploads)
- [ ] Assess upload directory permissions (not executable)
- [ ] Verify files are stored outside web root or served via separate domain
- [ ] Check for image processing vulnerabilities (ImageTragick, ImageMagick CVEs)
- [ ] Assess filename sanitization (path traversal prevention)
- [ ] Verify virus scanning integration (ClamAV, commercial AV)
- [ ] Check for path traversal in filename handling (`../../etc/passwd`)
- [ ] Verify file metadata stripping (EXIF data privacy)
- [ ] Check for SVG file sanitization (embedded scripts)
- [ ] Verify ZIP file extraction security (zip bombs, path traversal)

**Attack Scenarios**:
- Malicious file upload (web shell)
- Path traversal via filename
- XXE via SVG upload
- Zip bomb DoS
- ImageTragick exploitation

**Remediation Deliverables**:
- Secure file upload implementation
- File type validation (magic bytes check)
- Filename sanitization
- Virus scanning integration
- File upload security test suite

---

### 22. **API Security**

**Audit Checklist**:
- [ ] Verify API versioning strategy
- [ ] Check for GraphQL introspection disabled in production
- [ ] Assess API response size limits (prevent large payload DoS)
- [ ] Verify REST API idempotency for critical operations
- [ ] Check for API documentation exposure (Swagger/OpenAPI in production)
- [ ] Assess webhook signature validation (HMAC verification)
- [ ] Verify pagination limits on list endpoints (prevent data dumping)
- [ ] Check for API key/token in URL (should be in headers)
- [ ] Verify API rate limiting per user/API key
- [ ] Check for excessive data exposure in API responses

**Attack Scenarios**:
- API enumeration via introspection
- Data dumping via unlimited pagination
- Webhook spoofing
- API key theft from URLs (logs)

**Remediation Deliverables**:
- API security best practices guide
- GraphQL security configuration
- Webhook signature validation
- API rate limiting per endpoint
- API security test suite

---

### 23. **Third-Party Integrations**

**Audit Checklist**:
- [ ] Audit all external API integrations (payment, email, SMS, analytics)
- [ ] Verify webhook signature validation (validate sender authenticity)
- [ ] Check for secure credential storage for third-party services
- [ ] Assess data sharing with third parties (privacy implications, GDPR)
- [ ] Verify timeout and retry logic for external calls (prevent hanging)
- [ ] Check for circuit breaker patterns (fail gracefully)
- [ ] Verify API key scoping for third-party services (least privilege)
- [ ] Check for logging of third-party API responses (may contain sensitive data)

**Attack Scenarios**:
- Webhook spoofing
- Third-party service compromise
- Data leakage to third parties

**Remediation Deliverables**:
- Third-party integration security checklist
- Webhook signature validation implementation
- Circuit breaker pattern implementation
- Third-party integration test suite

---

### 24. **Logging & Monitoring**

**Audit Checklist**:
- [ ] Verify security event logging:
  - Failed login attempts
  - Privilege escalation attempts
  - Access to sensitive resources
  - Configuration changes
  - Admin actions
- [ ] Check that sensitive data is NOT logged:
  - Passwords (even hashed)
  - API keys/tokens
  - Credit card numbers
  - SSN, passport numbers
  - Full PII
- [ ] Assess log injection vulnerabilities (CRLF injection in logs)
- [ ] Verify centralized logging implementation (ELK, Splunk, CloudWatch)
- [ ] Check for anomaly detection and alerting
- [ ] Verify log retention and rotation policies (compliance requirements)
- [ ] Assess audit trail completeness (who, what, when, where)
- [ ] Check for log integrity (tamper-proof logging)
- [ ] Verify log access controls (only authorized personnel)

**Attack Scenarios**:
- Log injection to hide malicious activities
- Log tampering to remove evidence
- Sensitive data exposure via logs

**Remediation Deliverables**:
- Security logging implementation (Winston, Pino, Loguru, Python logging)
- Centralized logging setup
- Log sanitization functions
- Alerting rules for security events
- Log retention policy
- Logging security test suite

---

### 25. **Data Privacy & Compliance**

**Audit Checklist**:
- [ ] Verify PII (Personally Identifiable Information) handling:
  - Inventory all PII collected
  - Legal basis for collection (consent, legitimate interest)
  - Purpose limitation (collect only what's needed)
- [ ] Check for data minimization principles
- [ ] Assess data retention and deletion policies (auto-delete after X days/years)
- [ ] Verify user consent mechanisms (GDPR, CCPA, cookie consent)
- [ ] Check for data export functionality (right to data portability)
- [ ] Verify data deletion functionality (right to be forgotten)
- [ ] Assess data anonymization/pseudonymization
- [ ] Check for cookie consent implementation (GDPR)
- [ ] Verify privacy policy accuracy and completeness
- [ ] Check for data breach notification procedures
- [ ] Assess cross-border data transfer compliance (GDPR, Privacy Shield)

**Compliance Frameworks**:
- GDPR (EU data protection)
- CCPA (California Consumer Privacy Act)
- HIPAA (health data)
- PCI-DSS (payment data)
- SOC 2 (security controls)

**Remediation Deliverables**:
- Privacy policy template
- Data retention and deletion automation
- Consent management implementation
- Data export/deletion endpoints
- Privacy compliance checklist
- GDPR/CCPA compliance report

---

### 26. **Mobile/API Client Security** (if applicable)

**Audit Checklist**:
- [ ] Verify certificate pinning implementation (prevent MITM)
- [ ] Check for hardcoded secrets in mobile apps (APK/IPA reverse engineering)
- [ ] Assess jailbreak/root detection
- [ ] Verify secure local storage usage (encrypted storage)
- [ ] Check for code obfuscation (ProGuard, DexGuard, etc.)
- [ ] Assess reverse engineering prevention
- [ ] Verify API authentication in mobile apps
- [ ] Check for insecure data transmission

**Remediation Deliverables**:
- Mobile security best practices
- Certificate pinning implementation
- Secure storage implementation
- Mobile app security test suite

---

### 27. **WebSocket Security** (if applicable)

**Audit Checklist**:
- [ ] Verify WebSocket authentication (token in handshake)
- [ ] Check for origin validation (prevent cross-site WebSocket hijacking)
- [ ] Assess message rate limiting (prevent flooding)
- [ ] Verify encryption (wss:// not ws://)
- [ ] Check for message validation and sanitization (prevent injection)
- [ ] Verify WebSocket connection limits per user

**Attack Scenarios**:
- Cross-Site WebSocket Hijacking (CSWH)
- WebSocket flooding/DoS
- Message injection

**Remediation Deliverables**:
- Secure WebSocket implementation
- WebSocket authentication
- Origin validation
- WebSocket security test suite

---

### 28. **Server-Side Request Forgery (SSRF)**

**Audit Checklist**:
- [ ] Identify URL/domain input points (user-provided URLs)
- [ ] Check for internal network access restrictions (cannot access 127.0.0.1, 10.x.x.x, 192.168.x.x, 169.254.169.254)
- [ ] Verify URL validation and whitelist implementation
- [ ] Assess redirect following security (limit redirects, validate redirect targets)
- [ ] Check for cloud metadata endpoint access (AWS: 169.254.169.254, GCP, Azure equivalents)
- [ ] Verify DNS rebinding protection
- [ ] Check for SSRF in file upload from URL feature

**Attack Scenarios**:
- SSRF to access internal services
- SSRF to read cloud metadata (steal credentials)
- SSRF to scan internal network
- SSRF via redirect chains

**Remediation Deliverables**:
- SSRF prevention implementation (URL validation, blacklisting private IPs)
- Whitelist-based URL validation
- SSRF testing suite

---

### 29. **Subdomain Takeover**

**Audit Checklist**:
- [ ] Audit DNS records for dangling CNAMEs (pointing to unclaimed resources)
- [ ] Check for unclaimed cloud resources:
  - AWS S3 buckets
  - Azure Blob Storage
  - GitHub Pages
  - Heroku apps
  - Shopify stores
  - Zendesk
- [ ] Verify ownership of all subdomains
- [ ] Check for subdomain delegation to third parties

**Attack Scenarios**:
- Subdomain takeover via dangling CNAME
- Phishing via taken-over subdomain

**Remediation Deliverables**:
- DNS audit report
- Subdomain cleanup plan
- Subdomain monitoring
- Subdomain security checklist

---

### 30. **Container & Infrastructure Security** (if applicable)

**Audit Checklist**:
- [ ] Verify Docker image security:
  - No root user (USER directive)
  - Minimal base image (Alpine, Distroless)
  - No secrets in layers
  - Vulnerability scanning (Trivy, Clair, Snyk)
- [ ] Check for secrets in Docker images (Docker history)
- [ ] Assess container runtime security (AppArmor, SELinux, seccomp)
- [ ] Verify Kubernetes security configurations:
  - Network policies
  - Pod security policies/admission controllers
  - RBAC (Role-Based Access Control)
  - Secret management (not in env vars)
  - Resource limits
- [ ] Check for exposed management ports (Docker daemon, Kubernetes API)
- [ ] Assess network segmentation (service mesh, network policies)
- [ ] Verify container image signing and verification

**Remediation Deliverables**:
- Secure Dockerfile best practices
- Container image scanning in CI/CD
- Kubernetes security configuration
- Container security test suite

---

### 31. **CI/CD Pipeline Security**

**Audit Checklist**:
- [ ] Verify secrets management in CI/CD (use secrets manager, not env vars in config)
- [ ] Check for code signing implementation
- [ ] Assess build artifact integrity (checksums, signatures)
- [ ] Verify access controls on deployment pipelines (who can deploy to prod?)
- [ ] Check for dependency scanning in pipeline (npm audit, Snyk)
- [ ] Assess SAST (Static Application Security Testing) integration
- [ ] Assess DAST (Dynamic Application Security Testing) integration
- [ ] Verify approval gates for production deployments
- [ ] Check for audit logging of pipeline activities

**Attack Scenarios**:
- Pipeline compromise (malicious code injection)
- Secrets theft from CI/CD logs
- Unauthorized production deployment

**Remediation Deliverables**:
- Secure CI/CD pipeline configuration
- Secrets management in pipeline
- Security scanning integration (SAST, DAST, SCA)
- Pipeline security checklist

---

### 32. **Backup & Disaster Recovery**

**Audit Checklist**:
- [ ] Verify backup encryption (at rest and in transit)
- [ ] Check backup access controls (who can access/restore backups?)
- [ ] Assess backup restoration testing (do backups actually work?)
- [ ] Verify backup retention policies (how long are backups kept?)
- [ ] Check for backup integrity validation (checksums, test restores)
- [ ] Verify backup geographic distribution (off-site backups)
- [ ] Check for backup of secrets and configuration

**Remediation Deliverables**:
- Backup security configuration
- Backup restoration testing procedure
- Disaster recovery plan
- Backup security checklist

---

### 33. **Time-Based Vulnerabilities**

**Audit Checklist**:
- [ ] Check for time-based SQL injection (SLEEP, WAITFOR DELAY)
- [ ] Verify time synchronization (NTP configuration)
- [ ] Assess timestamp validation in JWT tokens
- [ ] Check for timezone-related logic bugs (UTC vs local time)
- [ ] Verify expiration handling edge cases (token expiration, session timeout)
- [ ] Check for timing attack vulnerabilities (constant-time comparison)

**Attack Scenarios**:
- Time-based blind SQL injection
- Timezone manipulation for access control bypass
- Timing attacks to leak information

**Remediation Deliverables**:
- Constant-time comparison implementation
- NTP synchronization setup
- Timezone handling best practices
- Time-based security test suite

---

### 34. **Cryptographic Implementation**

**Audit Checklist**:
- [ ] Verify strong encryption algorithms:
  - Symmetric: AES-256 (not DES, 3DES, RC4)
  - Asymmetric: RSA-2048+ or ECC-256+
  - Hashing: SHA-256+ (not MD5, SHA1)
- [ ] Check for deprecated algorithms usage
- [ ] Assess key management and storage (HSM, KMS, encrypted storage)
- [ ] Verify random number generation (cryptographically secure: crypto.randomBytes, not Math.random)
- [ ] Check for proper IV/nonce usage (unique per encryption)
- [ ] Assess TLS/SSL configuration:
  - Minimum TLS 1.2 (TLS 1.3 preferred)
  - Strong cipher suites only
  - Certificate validation
  - HSTS enabled
- [ ] Verify password hashing (bcrypt, Argon2, scrypt - not MD5, SHA1)
- [ ] Check for secure random salt generation

**Attack Scenarios**:
- Weak encryption cracking
- Predictable random number exploitation
- TLS downgrade attacks

**Remediation Deliverables**:
- Cryptographic best practices guide
- Secure encryption implementation
- TLS/SSL configuration hardening
- Cryptographic test suite

---

### 35. **HTTP Parameter Pollution (HPP)**

**Audit Checklist**:
- [ ] Check for duplicate parameter handling (which value is used?)
- [ ] Verify parameter precedence security (POST vs GET parameters)
- [ ] Assess URL parameter parsing vulnerabilities
- [ ] Check for framework-specific parameter pollution behaviors

**Attack Scenarios**:
- HPP to bypass security controls
- HPP to inject parameters

**Remediation Deliverables**:
- Parameter handling security configuration
- HPP test cases

---

## Attack-Fix Loop Methodology

For each identified vulnerability, follow this process:

### 1. **Attack Simulation**
- Provide step-by-step exploitation instructions
- Include proof-of-concept code/requests
- Document actual vulnerability evidence

### 2. **Impact Assessment**
- Severity rating (Critical/High/Medium/Low)
- CVSS score (if applicable)
- Business impact (data breach, financial loss, reputation damage)
- Affected users/systems
- Compliance implications

### 3. **Fix Implementation**
- Provide complete, working code to fix the vulnerability
- Include before/after code comparison
- Explain why the fix works
- Note any breaking changes or migration steps

### 4. **Verification Test**
- Create automated test that fails before fix, passes after
- Include unit tests, integration tests, or E2E tests
- Provide manual testing steps if automation isn't feasible

### 5. **Regression Check**
- Ensure fix doesn't break existing functionality
- Run full test suite
- Check for performance impact
- Verify no new vulnerabilities introduced

### 6. **Documentation**
- Document the vulnerability and fix in security log
- Update architecture/design docs if needed
- Create runbook for detection and response
- Update deployment/configuration guides

---

# PART B: COMPREHENSIVE TESTING IMPLEMENTATION

## Testing Framework Selection Guide

Based on your technology stack, I will recommend and implement the best testing tools:

### JavaScript/TypeScript Ecosystem

**Unit Testing**:
- **Vitest** (recommended for Vite/modern apps) - Extremely fast, ESM native
- **Jest** (industry standard) - Mature, extensive ecosystem
- **Testing Library** (@testing-library/react, vue, etc.) - Component testing

**Integration Testing**:
- **Supertest** - API endpoint testing
- **MSW (Mock Service Worker)** - API mocking
- **Testcontainers** - Real database/service testing

**E2E Testing**:
- **Playwright** (recommended) - Modern, fast, multi-browser, great debugging
- **Cypress** (alternative) - Developer-friendly, great DX

**API Testing**:
- **Postman + Newman** - Collection-based, easy to use
- **k6** - Performance + functional API testing
- **REST Assured** (if Java backend)

**Performance Testing**:
- **k6** (recommended) - Modern, scriptable, excellent reporting
- **Artillery** (alternative) - YAML-based, easy to start
- **Apache JMeter** - Enterprise standard, GUI-based

### Python Ecosystem

**Unit Testing**:
- **pytest** (recommended) - Most popular, great plugins
- **unittest** (built-in) - Standard library

**Integration Testing**:
- **pytest** with fixtures
- **TestContainers Python** - Real services

**E2E Testing**:
- **Playwright Python**
- **Selenium** (legacy support)

**API Testing**:
- **pytest + requests**
- **Postman + Newman**
- **Locust** (performance + load testing)

### Other Frameworks

**PHP (Laravel, Symfony)**:
- **PHPUnit** - Unit/integration testing
- **Pest** - Modern PHP testing
- **Dusk** - Laravel E2E

**.NET**:
- **xUnit/NUnit/MSTest** - Unit testing
- **SpecFlow** - BDD testing
- **Playwright .NET** - E2E

**Ruby (Rails)**:
- **RSpec** - BDD-style testing
- **Minitest** - Rails default
- **Capybara** - E2E

**Java/Spring**:
- **JUnit 5** - Unit testing
- **Mockito** - Mocking
- **REST Assured** - API testing
- **Selenium/Playwright** - E2E

---

## Complete Testing Categories

### 1. **Unit Testing**

**Philosophy**: Test individual functions/methods in complete isolation

**Coverage Goals**:
- ✅ **80% overall code coverage minimum**
- ✅ **100% coverage for utility functions, helpers, validators**
- ✅ **100% coverage for business logic (pricing, calculations, transformations)**

**What to Test**:

1. **Pure Functions & Utilities**
2. **Component Rendering** (React/Vue/Angular)
3. **Business Logic Functions**
4. **Data Transformation Functions**
5. **Validation Functions**
6. **Class Methods & Object Behaviors**

**Edge Cases to Test**:
- Empty inputs (`null`, `undefined`, `''`, `[]`, `{}`)
- Boundary values (0, -1, MAX_INT, MIN_INT)
- Very large inputs (stress testing)
- Special characters (Unicode, emoji, SQL injection attempts)
- Invalid data types (passing string when number expected)

**Deliverables**:
- ✅ Unit test files for every module
- ✅ Test configuration
- ✅ Mock implementations for all external dependencies
- ✅ Coverage reports with HTML visualization
- ✅ CI integration (run on every commit)
- ✅ **Complete unit test suite with 80%+ coverage**

---

### 2. **Integration Testing**

**Philosophy**: Test interactions between components, modules, and external services

**What to Test**:

1. **API Endpoint Testing**
2. **Database Operations**
3. **Third-Party Service Integrations**
4. **Message Queue Processing**
5. **Cache Layer Interactions**
6. **Authentication/Authorization Flows**

**Test Scenarios**:
- Happy path scenarios
- Error scenarios (network failures, timeouts)
- Data consistency across services
- Transaction rollback scenarios
- Concurrent request handling
- API contract validation

**Deliverables**:
- ✅ Integration test suite for all API endpoints
- ✅ Database integration tests with real/containerized database
- ✅ Third-party integration tests (test mode)
- ✅ Test database setup/teardown scripts
- ✅ Database fixtures and seed data
- ✅ API contract tests
- ✅ **Complete integration test coverage for critical paths**

---

### 3. **End-to-End (E2E) Testing**

**Philosophy**: Test complete user journeys through the application as a real user would

**Framework**: **Playwright** (recommended) for modern apps

**Critical User Flows to Test**:

1. **User Registration & Onboarding**
2. **Login/Logout Flow**
3. **E-commerce: Browse → Add to Cart → Checkout → Payment**
4. **Password Reset Workflow**
5. **User Profile Management**
6. **Admin Dashboard Operations**

**Cross-Browser Testing**:
- Chrome (latest 2 versions)
- Firefox (latest 2 versions)
- Safari (latest 2 versions)
- Edge (latest 2 versions)
- Mobile browsers (iOS Safari, Chrome Android)

**Deliverables**:
- ✅ E2E test suite covering all critical user journeys
- ✅ Page Object Model (POM) implementation
- ✅ Test data management system
- ✅ Screenshot/video capture on failures
- ✅ Cross-browser test execution configuration
- ✅ Visual regression test setup
- ✅ CI/CD integration
- ✅ **Complete E2E coverage for critical user flows**

---

### 4. **API Testing**

**Philosophy**: Comprehensive testing of all API endpoints for functionality, security, and performance

**What to Test**:

1. **Functional Testing**:
   - Request/response validation
   - HTTP status codes
   - Response schema validation
   - CRUD operations completeness

2. **Security Testing**:
   - Authentication enforcement
   - Authorization checks (RBAC)
   - Input sanitization
   - Rate limiting
   - CORS configuration

3. **Performance Testing**:
   - Response time benchmarks
   - Concurrent request handling
   - Payload size limits
   - Timeout configurations

**Deliverables**:
- ✅ Postman/Newman collections for all endpoints
- ✅ API contract tests
- ✅ Security test suite
- ✅ Performance test scripts
- ✅ API documentation validation
- ✅ **Complete API test coverage**

---

### 5. **Database Testing**

**Philosophy**: Validate data integrity, performance, and reliability at the database layer

**What to Test**:

1. **Data Integrity**:
   - CRUD operations accuracy
   - Foreign key constraints
   - Unique constraints
   - NOT NULL constraints
   - CHECK constraints

2. **Transaction Testing**:
   - ACID properties validation
   - Rollback scenarios
   - Concurrent transaction handling
   - Deadlock prevention

3. **Performance Testing**:
   - Query performance benchmarks
   - Index effectiveness
   - Connection pool management
   - N+1 query detection

4. **Migration Testing**:
   - Schema migration validation
   - Data migration integrity
   - Rollback migration testing

**Deliverables**:
- ✅ Database test suite
- ✅ Test data generators
- ✅ Migration test scripts
- ✅ Performance benchmark reports
- ✅ **Complete database testing coverage**

---

### 6. **Performance Testing**

**Philosophy**: Validate application performance under various load conditions

**Testing Types**:

1. **Load Testing** (Expected Load)
2. **Stress Testing** (Beyond Capacity)
3. **Spike Testing** (Sudden Traffic Surge)
4. **Soak Testing** (Long Duration)

**What to Measure**:
- Response Time (p50, p95, p99 percentiles)
- Throughput (requests per second)
- Error Rate
- Resource Utilization (CPU, memory, disk I/O)
- Database connection pool usage
- Cache hit/miss ratios

**Performance Benchmarks**:
- Page load time < 3 seconds
- API response time < 200ms (p95)
- Time to Interactive < 5 seconds
- First Contentful Paint < 1.5 seconds

**Deliverables**:
- ✅ Load testing scripts
- ✅ Stress testing scenarios
- ✅ Spike testing scenarios
- ✅ Soak testing scripts
- ✅ Performance benchmark reports
- ✅ Bottleneck analysis and recommendations
- ✅ **Complete performance testing suite**

---

### 7-35. **Additional Testing Categories**

**7. Security Testing**:
- Automated security scanning
- Manual penetration testing
- Dependency vulnerability scanning
- Authentication/authorization testing

**8. Accessibility Testing**:
- WCAG 2.1 AA compliance
- Keyboard navigation
- Screen reader compatibility
- Color contrast validation

**9. Cross-Browser Testing**:
- Chrome, Firefox, Safari, Edge
- Visual regression tests
- JavaScript compatibility

**10. Responsive/Mobile Testing**:
- Multiple device sizes
- Touch interactions
- Orientation changes

**11-20. Specialized Testing**:
- UI component testing
- Usability testing
- Compatibility testing
- Regression testing
- Smoke testing
- Exploratory testing
- Localization testing (i18n/l10n)
- Data migration testing
- Backup/recovery testing
- Deployment testing

**21-30. Advanced Testing**:
- Email testing
- Search functionality testing
- Payment processing testing
- File processing testing
- Notification testing
- WebSocket/real-time testing
- Caching testing
- Concurrency testing
- Error handling testing
- Compliance testing

**31-35. Infrastructure Testing**:
- Configuration testing
- Chaos engineering
- Contract testing
- Monitoring/observability testing
- Documentation testing

---

## Test Automation Framework Setup

I will create a complete testing infrastructure with:

### 1. **Project Structure**:
```
project/
├── src/                        # Application code
├── tests/
│   ├── unit/                   # Unit tests
│   ├── integration/            # Integration tests
│   ├── e2e/                    # End-to-end tests
│   ├── performance/            # Performance tests
│   ├── security/               # Security tests
│   └── accessibility/          # A11y tests
├── test-data/                  # Test fixtures and seed data
├── test-reports/               # Generated reports
├── jest.config.js              # Unit test config
├── playwright.config.ts        # E2E test config
└── k6-config.js               # Performance test config
```

### 2. **CI/CD Integration**:
- Pre-commit hooks (lint, unit tests)
- PR validation (unit + integration tests)
- Nightly full regression suite
- Performance testing on staging
- Security scans before deployment

### 3. **Test Data Management**:
- Faker.js for generating test data
- Database seeding scripts
- Test data fixtures
- Test data cleanup strategies

### 4. **Test Reporting**:
- Code coverage reports
- Test execution reports
- Performance benchmarks dashboard
- Security scan reports
- Accessibility audit reports

---

# PART C: SOFTWARE DESIGN PRINCIPLES & ARCHITECTURE REVIEW

## Design Philosophy & Methodology

Conduct a comprehensive code architecture review following industry-standard software engineering principles. For each design issue, you should:

1. **Analysis Phase**: Identify code smells, anti-patterns, and design violations
2. **Documentation Phase**: Document findings with priority (Critical/High/Medium/Low)
3. **Refactoring Phase**: Provide specific code refactoring with design pattern implementations
4. **Validation Phase**: Create tests to ensure refactored code maintains functionality

---

## Core Design Principles

### 1. **SOLID Principles**

#### 1.1 Single Responsibility Principle (SRP)

**Principle**: A class/module should have only one reason to change. Each class should have only one job or responsibility.

**Audit Checklist**:
- [ ] Identify classes/modules doing multiple unrelated things
- [ ] Check for "God Objects" (classes with too many responsibilities)
- [ ] Verify each function/method has a single, well-defined purpose
- [ ] Assess if business logic is mixed with presentation logic
- [ ] Check if data access is mixed with business logic
- [ ] Verify validation logic is separated from business logic

**Code Smells to Identify**:
- Classes with names like `Manager`, `Handler`, `Utility`, `Helper` (often doing too much)
- Classes with many methods (>10-15 methods)
- Classes with many dependencies (>5-7 constructor parameters)
- Methods longer than 20-30 lines
- High cyclomatic complexity (>10)

**Refactoring Examples**:

❌ **Bad - Violates SRP**:
```javascript
// UserService doing too many things
class UserService {
  constructor(database, emailService, logger, cache, analytics) {
    this.db = database;
    this.email = emailService;
    this.logger = logger;
    this.cache = cache;
    this.analytics = analytics;
  }

  async createUser(userData) {
    // Validation
    if (!userData.email || !this.isValidEmail(userData.email)) {
      throw new Error('Invalid email');
    }
    
    // Business logic
    const user = await this.db.users.create(userData);
    
    // Caching
    await this.cache.set(`user:${user.id}`, user);
    
    // Email sending
    await this.email.send(user.email, 'Welcome!', this.getWelcomeTemplate(user));
    
    // Logging
    this.logger.info(`User created: ${user.id}`);
    
    // Analytics
    await this.analytics.track('user_created', { userId: user.id });
    
    return user;
  }

  isValidEmail(email) { /* validation logic */ }
  getWelcomeTemplate(user) { /* template logic */ }
}
```

✅ **Good - Follows SRP**:
```javascript
// Separate responsibilities into different classes

// 1. Validation responsibility
class UserValidator {
  validate(userData) {
    if (!userData.email || !this.isValidEmail(userData.email)) {
      throw new ValidationError('Invalid email');
    }
    if (!userData.name || userData.name.length < 2) {
      throw new ValidationError('Name must be at least 2 characters');
    }
    return true;
  }

  isValidEmail(email) {
    return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);
  }
}

// 2. Repository responsibility (data access)
class UserRepository {
  constructor(database) {
    this.db = database;
  }

  async create(userData) {
    return await this.db.users.create(userData);
  }

  async findById(id) {
    return await this.db.users.findOne({ id });
  }
}

// 3. Cache responsibility
class UserCache {
  constructor(cache) {
    this.cache = cache;
  }

  async set(userId, userData) {
    await this.cache.set(`user:${userId}`, userData, { ttl: 3600 });
  }

  async get(userId) {
    return await this.cache.get(`user:${userId}`);
  }
}

// 4. Email responsibility
class UserNotificationService {
  constructor(emailService, templateEngine) {
    this.email = emailService;
    this.templates = templateEngine;
  }

  async sendWelcomeEmail(user) {
    const template = this.templates.render('welcome', { user });
    await this.email.send(user.email, 'Welcome!', template);
  }
}

// 5. Business logic responsibility (orchestration)
class UserService {
  constructor(validator, repository, cache, notificationService, eventBus) {
    this.validator = validator;
    this.repository = repository;
    this.cache = cache;
    this.notifications = notificationService;
    this.eventBus = eventBus;
  }

  async createUser(userData) {
    // Validate
    this.validator.validate(userData);
    
    // Create user
    const user = await this.repository.create(userData);
    
    // Cache user
    await this.cache.set(user.id, user);
    
    // Emit event (for logging, analytics, etc.)
    this.eventBus.emit('user.created', user);
    
    // Send notification
    await this.notifications.sendWelcomeEmail(user);
    
    return user;
  }
}
```

**Benefits**:
- Each class has one reason to change
- Easy to test each component in isolation
- Easy to replace implementations (e.g., swap cache provider)
- Code is more maintainable and understandable

---

#### 1.2 Open/Closed Principle (OCP)

**Principle**: Software entities should be open for extension but closed for modification. You should be able to add new functionality without changing existing code.

**Audit Checklist**:
- [ ] Identify code with many if/else or switch statements for type checking
- [ ] Check for code that requires modification to add new features
- [ ] Verify use of abstractions (interfaces, abstract classes)
- [ ] Assess plugin/extension architecture
- [ ] Check for strategy pattern usage where appropriate

**Code Smells to Identify**:
- Long switch statements that grow with new types
- If/else chains checking for specific types
- Code that needs modification to add new behavior
- Hard-coded dependencies

**Refactoring Examples**:

❌ **Bad - Violates OCP**:
```javascript
class PaymentProcessor {
  processPayment(order, paymentMethod) {
    if (paymentMethod === 'credit_card') {
      // Credit card processing logic
      this.validateCardNumber(order.cardNumber);
      this.chargeCard(order.amount);
    } else if (paymentMethod === 'paypal') {
      // PayPal processing logic
      this.redirectToPayPal(order.amount);
    } else if (paymentMethod === 'crypto') {
      // Crypto processing logic
      this.generateWalletAddress();
      this.waitForTransaction(order.amount);
    }
    // Adding new payment method requires modifying this class!
  }
}
```

✅ **Good - Follows OCP**:
```javascript
// Abstract payment method interface
interface PaymentMethod {
  process(amount: number, details: any): Promise<PaymentResult>;
  validate(details: any): boolean;
}

// Concrete implementations
class CreditCardPayment implements PaymentMethod {
  async process(amount, details) {
    this.validate(details);
    return await this.chargeCard(amount, details.cardNumber);
  }

  validate(details) {
    if (!this.isValidCardNumber(details.cardNumber)) {
      throw new ValidationError('Invalid card number');
    }
    return true;
  }

  private async chargeCard(amount, cardNumber) {
    // Credit card processing logic
  }

  private isValidCardNumber(cardNumber) {
    // Luhn algorithm validation
  }
}

class PayPalPayment implements PaymentMethod {
  async process(amount, details) {
    this.validate(details);
    return await this.redirectToPayPal(amount, details.email);
  }

  validate(details) {
    if (!details.email) {
      throw new ValidationError('PayPal email required');
    }
    return true;
  }

  private async redirectToPayPal(amount, email) {
    // PayPal processing logic
  }
}

class CryptoPayment implements PaymentMethod {
  async process(amount, details) {
    this.validate(details);
    const address = await this.generateWalletAddress();
    return await this.waitForTransaction(amount, address);
  }

  validate(details) {
    // Crypto validation logic
    return true;
  }

  private async generateWalletAddress() {
    // Generate address
  }

  private async waitForTransaction(amount, address) {
    // Wait for blockchain confirmation
  }
}

// Payment processor is closed for modification, open for extension
class PaymentProcessor {
  private paymentMethods: Map<string, PaymentMethod>;

  constructor() {
    this.paymentMethods = new Map();
  }

  // Register new payment methods without modifying the class
  registerPaymentMethod(name: string, method: PaymentMethod) {
    this.paymentMethods.set(name, method);
  }

  async processPayment(order, paymentMethodName) {
    const method = this.paymentMethods.get(paymentMethodName);
    
    if (!method) {
      throw new Error(`Payment method ${paymentMethodName} not supported`);
    }

    return await method.process(order.amount, order.paymentDetails);
  }
}

// Usage - adding new payment method doesn't require changing PaymentProcessor
const processor = new PaymentProcessor();
processor.registerPaymentMethod('credit_card', new CreditCardPayment());
processor.registerPaymentMethod('paypal', new PayPalPayment());
processor.registerPaymentMethod('crypto', new CryptoPayment());

// Easy to add new payment methods later
// processor.registerPaymentMethod('apple_pay', new ApplePayPayment());
```

**Benefits**:
- Add new payment methods without modifying existing code
- Each payment method is independently testable
- Reduces risk of breaking existing functionality
- Follows the strategy pattern

---

#### 1.3 Liskov Substitution Principle (LSP)

**Principle**: Objects of a superclass should be replaceable with objects of a subclass without breaking the application. Subtypes must be substitutable for their base types.

**Audit Checklist**:
- [ ] Verify subclasses can replace parent classes without errors
- [ ] Check for subclasses that throw "not implemented" errors
- [ ] Identify subclasses that weaken preconditions or strengthen postconditions
- [ ] Verify inherited methods make sense in child classes
- [ ] Check for type checking (instanceof) before calling methods

**Code Smells to Identify**:
- Subclasses throwing NotImplementedError or UnsupportedOperationError
- Empty method overrides that do nothing
- Type checking before calling methods
- Subclasses that require different parameters than parent

**Refactoring Examples**:

❌ **Bad - Violates LSP**:
```javascript
class Bird {
  fly() {
    console.log('Flying...');
  }
}

class Sparrow extends Bird {
  fly() {
    console.log('Sparrow flying!');
  }
}

class Penguin extends Bird {
  fly() {
    // Penguin can't fly!
    throw new Error('Penguins cannot fly!');
  }
}

// This breaks LSP - Penguin cannot substitute Bird
function makeBirdFly(bird: Bird) {
  bird.fly(); // Will throw error if bird is a Penguin!
}

makeBirdFly(new Sparrow()); // OK
makeBirdFly(new Penguin()); // ERROR! Breaks LSP
```

✅ **Good - Follows LSP**:
```javascript
// Better abstraction - not all birds fly
abstract class Bird {
  abstract move(): void;
}

class FlyingBird extends Bird {
  move() {
    this.fly();
  }

  fly() {
    console.log('Flying...');
  }
}

class Sparrow extends FlyingBird {
  fly() {
    console.log('Sparrow flying!');
  }
}

class Penguin extends Bird {
  move() {
    this.swim();
  }

  swim() {
    console.log('Penguin swimming!');
  }
}

// Now both can substitute Bird safely
function moveBird(bird: Bird) {
  bird.move(); // Works for all birds!
}

moveBird(new Sparrow()); // Flies
moveBird(new Penguin()); // Swims
```

**Another Example - Rectangle/Square Problem**:

❌ **Bad - Violates LSP**:
```javascript
class Rectangle {
  protected width: number;
  protected height: number;

  setWidth(width: number) {
    this.width = width;
  }

  setHeight(height: number) {
    this.height = height;
  }

  getArea() {
    return this.width * this.height;
  }
}

class Square extends Rectangle {
  // Square must have equal sides
  setWidth(width: number) {
    this.width = width;
    this.height = width; // Setting both!
  }

  setHeight(height: number) {
    this.width = height; // Setting both!
    this.height = height;
  }
}

// This breaks LSP
function testRectangle(rectangle: Rectangle) {
  rectangle.setWidth(5);
  rectangle.setHeight(4);
  console.log(rectangle.getArea()); // Expects 20
}

testRectangle(new Rectangle()); // 20 ✓
testRectangle(new Square());    // 16 ✗ Breaks expectation!
```

✅ **Good - Follows LSP**:
```javascript
// Use composition instead of inheritance
interface Shape {
  getArea(): number;
}

class Rectangle implements Shape {
  constructor(private width: number, private height: number) {}

  setWidth(width: number) {
    this.width = width;
  }

  setHeight(height: number) {
    this.height = height;
  }

  getArea() {
    return this.width * this.height;
  }
}

class Square implements Shape {
  constructor(private side: number) {}

  setSide(side: number) {
    this.side = side;
  }

  getArea() {
    return this.side * this.side;
  }
}

// Now both implement Shape without inheritance issues
function printArea(shape: Shape) {
  console.log(shape.getArea());
}

printArea(new Rectangle(5, 4)); // 20
printArea(new Square(4));       // 16
```

**Benefits**:
- Code behaves predictably with inheritance
- No surprising exceptions
- Polymorphism works correctly

---

#### 1.4 Interface Segregation Principle (ISP)

**Principle**: Clients should not be forced to depend on interfaces they don't use. Many specific interfaces are better than one general-purpose interface.

**Audit Checklist**:
- [ ] Identify "fat" interfaces with many methods
- [ ] Check for interfaces with unrelated methods
- [ ] Verify implementing classes use all interface methods
- [ ] Check for empty/stub method implementations
- [ ] Assess if interfaces can be split into smaller, focused ones

**Code Smells to Identify**:
- Interfaces with >10 methods
- Classes implementing interfaces but leaving methods empty
- Classes implementing only a subset of interface methods
- Interfaces mixing different concerns

**Refactoring Examples**:

❌ **Bad - Violates ISP**:
```javascript
// Fat interface forcing all implementations to have all methods
interface Worker {
  work(): void;
  eat(): void;
  sleep(): void;
  getPaid(): void;
  attendMeeting(): void;
  writeCode(): void;
  designUI(): void;
  testSoftware(): void;
}

class Developer implements Worker {
  work() { console.log('Writing code'); }
  eat() { console.log('Eating'); }
  sleep() { console.log('Sleeping'); }
  getPaid() { console.log('Getting paid'); }
  attendMeeting() { console.log('In meeting'); }
  writeCode() { console.log('Coding'); }
  
  // Forced to implement methods that don't make sense
  designUI() { 
    throw new Error('Developer does not design UI'); 
  }
  testSoftware() { 
    throw new Error('Developer does not test (ideally they do, but let\'s say they don\'t)'); 
  }
}

class Robot implements Worker {
  work() { console.log('Working 24/7'); }
  
  // Robot doesn't eat, sleep, or get paid!
  eat() { throw new Error('Robots don\'t eat'); }
  sleep() { throw new Error('Robots don\'t sleep'); }
  getPaid() { throw new Error('Robots don\'t get paid'); }
  
  // ... forced to implement irrelevant methods
}
```

✅ **Good - Follows ISP**:
```javascript
// Split into multiple focused interfaces
interface Workable {
  work(): void;
}

interface Eatable {
  eat(): void;
}

interface Sleepable {
  sleep(): void;
}

interface Payable {
  getPaid(): void;
}

interface Attendable {
  attendMeeting(): void;
}

interface Codable {
  writeCode(): void;
}

interface Designable {
  designUI(): void;
}

interface Testable {
  testSoftware(): void;
}

// Now classes implement only what they need
class Developer implements Workable, Eatable, Sleepable, Payable, Attendable, Codable {
  work() { console.log('Writing code'); }
  eat() { console.log('Eating'); }
  sleep() { console.log('Sleeping'); }
  getPaid() { console.log('Getting paid'); }
  attendMeeting() { console.log('In meeting'); }
  writeCode() { console.log('Coding'); }
}

class Designer implements Workable, Eatable, Sleepable, Payable, Attendable, Designable {
  work() { console.log('Designing UI'); }
  eat() { console.log('Eating'); }
  sleep() { console.log('Sleeping'); }
  getPaid() { console.log('Getting paid'); }
  attendMeeting() { console.log('In meeting'); }
  designUI() { console.log('Designing'); }
}

class Robot implements Workable, Codable {
  work() { console.log('Working 24/7'); }
  writeCode() { console.log('Generating code'); }
  // Only implements what makes sense for a robot
}
```

**Benefits**:
- Classes are not forced to implement irrelevant methods
- Smaller, more focused interfaces
- Easier to understand and maintain
- Better separation of concerns

---

#### 1.5 Dependency Inversion Principle (DIP)

**Principle**: High-level modules should not depend on low-level modules. Both should depend on abstractions. Abstractions should not depend on details. Details should depend on abstractions.

**Audit Checklist**:
- [ ] Identify direct dependencies on concrete classes
- [ ] Check for `new` keyword usage in business logic
- [ ] Verify dependency injection usage
- [ ] Assess use of interfaces/abstractions
- [ ] Check for hard-coded dependencies
- [ ] Verify inversion of control (IoC) container usage

**Code Smells to Identify**:
- Business logic creating dependencies with `new`
- Direct imports of concrete implementations
- Tight coupling between classes
- Hard to test code (difficult to mock dependencies)

**Refactoring Examples**:

❌ **Bad - Violates DIP**:
```javascript
// Low-level modules (details)
class MySQLDatabase {
  connect() {
    console.log('Connecting to MySQL');
  }

  query(sql) {
    console.log(`Executing: ${sql}`);
    return [{ id: 1, name: 'User' }];
  }
}

class FileLogger {
  log(message) {
    console.log(`[LOG] ${message}`);
    // Write to file
  }
}

// High-level module depends on low-level concrete classes
class UserService {
  constructor() {
    // Direct dependency on concrete implementations
    this.database = new MySQLDatabase();
    this.logger = new FileLogger();
  }

  async getUser(id) {
    this.logger.log(`Fetching user ${id}`);
    const result = this.database.query(`SELECT * FROM users WHERE id = ${id}`);
    return result[0];
  }
}

// Problems:
// - Can't switch to PostgreSQL without changing UserService
// - Can't test UserService without real database
// - Can't change logging implementation without changing UserService
```

✅ **Good - Follows DIP**:
```javascript
// Abstractions (interfaces)
interface Database {
  connect(): void;
  query(sql: string): Promise<any[]>;
}

interface Logger {
  log(message: string): void;
  error(message: string): void;
}

// Low-level implementations depend on abstractions
class MySQLDatabase implements Database {
  connect() {
    console.log('Connecting to MySQL');
  }

  async query(sql: string) {
    console.log(`Executing: ${sql}`);
    return [{ id: 1, name: 'User' }];
  }
}

class PostgreSQLDatabase implements Database {
  connect() {
    console.log('Connecting to PostgreSQL');
  }

  async query(sql: string) {
    console.log(`Executing on PostgreSQL: ${sql}`);
    return [{ id: 1, name: 'User' }];
  }
}

class FileLogger implements Logger {
  log(message: string) {
    console.log(`[FILE LOG] ${message}`);
  }

  error(message: string) {
    console.error(`[FILE ERROR] ${message}`);
  }
}

class ConsoleLogger implements Logger {
  log(message: string) {
    console.log(`[CONSOLE LOG] ${message}`);
  }

  error(message: string) {
    console.error(`[CONSOLE ERROR] ${message}`);
  }
}

// High-level module depends on abstractions
class UserService {
  constructor(
    private database: Database,
    private logger: Logger
  ) {
    // Dependencies injected, not created
  }

  async getUser(id: number) {
    this.logger.log(`Fetching user ${id}`);
    const result = await this.database.query(`SELECT * FROM users WHERE id = ${id}`);
    return result[0];
  }
}

// Dependency Injection Container
class Container {
  private services: Map<string, any> = new Map();

  register<T>(name: string, implementation: T) {
    this.services.set(name, implementation);
  }

  resolve<T>(name: string): T {
    return this.services.get(name);
  }
}

// Setup (composition root)
const container = new Container();
container.register('database', new MySQLDatabase());
container.register('logger', new FileLogger());

const userService = new UserService(
  container.resolve('database'),
  container.resolve('logger')
);

// Benefits:
// - Easy to switch to PostgreSQL: container.register('database', new PostgreSQLDatabase())
// - Easy to test: inject mock database and logger
// - Easy to change logging: container.register('logger', new ConsoleLogger())
// - Loose coupling between components
```

**Dependency Injection Patterns**:

```typescript
// 1. Constructor Injection (preferred)
class OrderService {
  constructor(
    private paymentProcessor: PaymentProcessor,
    private emailService: EmailService,
    private orderRepository: OrderRepository
  ) {}
}

// 2. Property/Setter Injection
class OrderService {
  private paymentProcessor: PaymentProcessor;
  
  setPaymentProcessor(processor: PaymentProcessor) {
    this.paymentProcessor = processor;
  }
}

// 3. Method Injection
class OrderService {
  processOrder(order: Order, paymentProcessor: PaymentProcessor) {
    return paymentProcessor.process(order.total);
  }
}
```

**Benefits**:
- Loose coupling between components
- Easy to test (inject mocks)
- Easy to swap implementations
- Follows Open/Closed Principle
- Flexible and maintainable

---

### 2. **Dependency Injection for Testability**

**Audit Checklist**:
- [ ] Verify dependencies are injected, not instantiated
- [ ] Check for dependency injection container usage
- [ ] Assess constructor injection vs property injection
- [ ] Verify service lifetimes (singleton, scoped, transient)
- [ ] Check for circular dependencies
- [ ] Assess testability of components

**DI Container Examples**:

**Node.js/TypeScript - TypeDI, InversifyJS, or Awilix**:
```typescript
import { Service, Container, Inject } from 'typedi';

@Service()
class Database {
  connect() { /* ... */ }
}

@Service()
class UserRepository {
  constructor(private database: Database) {}
  
  findById(id: number) {
    return this.database.query('...');
  }
}

@Service()
class UserService {
  constructor(
    @Inject() private repository: UserRepository
  ) {}
  
  async getUser(id: number) {
    return await this.repository.findById(id);
  }
}

// Usage
const userService = Container.get(UserService);
```

**Benefits**:
- All dependencies are testable
- Mock injection for unit tests
- Clear dependency graph
- Automatic dependency resolution

---

### 3. **Type Safety & Documentation**

#### 3.1 Comprehensive Type Hints

**Audit Checklist**:
- [ ] Verify all function parameters have type annotations
- [ ] Check all function return types are specified
- [ ] Assess use of TypeScript strict mode
- [ ] Check for `any` type usage (should be minimal)
- [ ] Verify interface/type definitions for complex objects
- [ ] Check for discriminated unions for complex types
- [ ] Assess generic type usage

**Type Safety Examples**:

❌ **Bad - No type safety**:
```javascript
function calculateTotal(items, discount, tax) {
  let total = 0;
  for (let item of items) {
    total += item.price * item.quantity;
  }
  total -= discount;
  total += total * tax;
  return total;
}

// No type checking - easy to pass wrong arguments
calculateTotal('not an array', 'not a number', null);
```

✅ **Good - Full type safety**:
```typescript
interface CartItem {
  id: string;
  name: string;
  price: number;
  quantity: number;
}

interface DiscountRule {
  type: 'percentage' | 'fixed';
  value: number;
}

interface TaxConfig {
  rate: number;
  inclusive: boolean;
}

type Currency = 'USD' | 'EUR' | 'GBP';

interface CalculationResult {
  subtotal: number;
  discount: number;
  tax: number;
  total: number;
  currency: Currency;
}

function calculateTotal(
  items: readonly CartItem[],
  discount: DiscountRule,
  tax: TaxConfig,
  currency: Currency = 'USD'
): CalculationResult {
  const subtotal = items.reduce(
    (sum, item) => sum + (item.price * item.quantity),
    0
  );

  const discountAmount = discount.type === 'percentage'
    ? subtotal * (discount.value / 100)
    : discount.value;

  const taxableAmount = subtotal - discountAmount;
  const taxAmount = tax.inclusive 
    ? 0 
    : taxableAmount * (tax.rate / 100);

  return {
    subtotal,
    discount: discountAmount,
    tax: taxAmount,
    total: taxableAmount + taxAmount,
    currency
  };
}

// TypeScript will catch errors at compile time
const result = calculateTotal(
  [{ id: '1', name: 'Item', price: 100, quantity: 2 }],
  { type: 'percentage', value: 10 },
  { rate: 8.5, inclusive: false },
  'USD'
);
```

#### 3.2 Comprehensive Documentation

**Audit Checklist**:
- [ ] Check for JSDoc/TSDoc comments on public APIs
- [ ] Verify parameter descriptions
- [ ] Check for @returns documentation
- [ ] Verify @throws documentation for exceptions
- [ ] Check for @example usage
- [ ] Assess README completeness
- [ ] Verify API documentation generation

**Documentation Examples**:

```typescript
/**
 * Processes a payment using the specified payment method.
 * 
 * @param order - The order containing payment details
 * @param method - The payment method to use (credit_card, paypal, crypto)
 * @returns A promise that resolves to the payment result
 * @throws {ValidationError} If the payment details are invalid
 * @throws {PaymentError} If the payment processing fails
 * @throws {InsufficientFundsError} If the account has insufficient funds
 * 
 * @example
 * ```typescript
 * const result = await processPayment(
 *   { id: '123', total: 99.99, cardNumber: '4242...' },
 *   'credit_card'
 * );
 * console.log(result.transactionId);
 * ```
 * 
 * @see {@link PaymentMethod} for supported payment methods
 * @see {@link PaymentResult} for result structure
 */
async function processPayment(
  order: Order,
  method: PaymentMethodType
): Promise<PaymentResult> {
  // Implementation
}
```

---

### 4. **Error Handling Best Practices**

**Audit Checklist**:
- [ ] Verify try/catch blocks around async operations
- [ ] Check for specific error types (not catching generic Error)
- [ ] Verify proper error propagation
- [ ] Check for finally blocks for cleanup
- [ ] Assess error logging
- [ ] Verify user-friendly error messages
- [ ] Check for error recovery mechanisms

**Error Handling Patterns**:

❌ **Bad - Poor error handling**:
```javascript
async function getUser(id) {
  const user = await database.query(`SELECT * FROM users WHERE id = ${id}`);
  return user;
  // No error handling!
  // SQL injection vulnerable!
}

async function createOrder(orderData) {
  try {
    const order = await database.insert('orders', orderData);
    await emailService.send(orderData.email, 'Order confirmed');
    return order;
  } catch (error) {
    // Swallowing error, no logging, no user feedback
    return null;
  }
}
```

✅ **Good - Comprehensive error handling**:
```typescript
// Custom error types
class DatabaseError extends Error {
  constructor(message: string, public readonly query: string) {
    super(message);
    this.name = 'DatabaseError';
  }
}

class ValidationError extends Error {
  constructor(
    message: string,
    public readonly field: string,
    public readonly value: any
  ) {
    super(message);
    this.name = 'ValidationError';
  }
}

class NotFoundError extends Error {
  constructor(resource: string, id: string | number) {
    super(`${resource} with id ${id} not found`);
    this.name = 'NotFoundError';
  }
}

// Result type for operations that can fail
type Result<T, E = Error> = 
  | { success: true; data: T }
  | { success: false; error: E };

// Service with proper error handling
class UserService {
  constructor(
    private database: Database,
    private logger: Logger
  ) {}

  async getUser(id: number): Promise<Result<User, NotFoundError | DatabaseError>> {
    try {
      // Input validation
      if (!id || id < 0) {
        throw new ValidationError('Invalid user ID', 'id', id);
      }

      // Database operation with parameterized query
      const users = await this.database.query(
        'SELECT * FROM users WHERE id = $1',
        [id]
      );

      if (users.length === 0) {
        return {
          success: false,
          error: new NotFoundError('User', id)
        };
      }

      return {
        success: true,
        data: users[0]
      };

    } catch (error) {
      // Log the error with context
      this.logger.error('Failed to get user', {
        userId: id,
        error: error.message,
        stack: error.stack
      });

      // Re-throw or return error
      if (error instanceof ValidationError) {
        throw error; // Validation errors should be thrown
      }

      return {
        success: false,
        error: new DatabaseError('Failed to fetch user', error.message)
      };
    }
  }

  async createOrder(orderData: CreateOrderDTO): Promise<Result<Order>> {
    const transaction = await this.database.beginTransaction();

    try {
      // Validate input
      this.validateOrderData(orderData);

      // Create order
      const order = await this.orderRepository.create(orderData, transaction);

      // Send confirmation email
      try {
        await this.emailService.send({
          to: orderData.email,
          template: 'order-confirmation',
          data: { order }
        });
      } catch (emailError) {
        // Log but don't fail the order
        this.logger.warn('Failed to send order confirmation email', {
          orderId: order.id,
          error: emailError.message
        });
      }

      // Commit transaction
      await transaction.commit();

      return { success: true, data: order };

    } catch (error) {
      // Rollback transaction on error
      await transaction.rollback();

      this.logger.error('Failed to create order', {
        orderData,
        error: error.message,
        stack: error.stack
      });

      return {
        success: false,
        error: error instanceof ValidationError 
          ? error 
          : new Error('Failed to create order')
      };
    } finally {
      // Ensure transaction is closed
      await transaction.release();
    }
  }

  private validateOrderData(data: CreateOrderDTO): void {
    if (!data.items || data.items.length === 0) {
      throw new ValidationError('Order must have at least one item', 'items', data.items);
    }

    if (!data.email || !this.isValidEmail(data.email)) {
      throw new ValidationError('Invalid email address', 'email', data.email);
    }

    // More validation...
  }

  private isValidEmail(email: string): boolean {
    return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);
  }
}

// Usage with proper error handling
async function handleUserRequest(req, res) {
  const result = await userService.getUser(req.params.id);

  if (!result.success) {
    if (result.error instanceof NotFoundError) {
      return res.status(404).json({ error: 'User not found' });
    }
    if (result.error instanceof DatabaseError) {
      return res.status(500).json({ error: 'Internal server error' });
    }
  }

  return res.json(result.data);
}
```

**Benefits**:
- Specific error types for different failure scenarios
- Proper error logging with context
- Transaction rollback on failure
- Non-critical errors don't fail the operation
- Type-safe error handling with Result type
- User-friendly error messages (don't expose internals)

---

### 5. **Composition Over Inheritance**

**Principle**: Prefer object composition to class inheritance. Build functionality by combining simple objects rather than inheriting from complex class hierarchies.

**Audit Checklist**:
- [ ] Identify deep inheritance hierarchies (>3 levels)
- [ ] Check for fragile base class problems
- [ ] Verify use of composition and interfaces
- [ ] Assess use of mixins/traits where appropriate
- [ ] Check for "has-a" relationships modeled as "is-a"

**Code Smells**:
- Inheritance depth > 3 levels
- Large base classes with many responsibilities
- Overriding many parent methods
- Empty or no-op method overrides

**Refactoring Examples**:

❌ **Bad - Deep inheritance hierarchy**:
```javascript
class Vehicle {
  start() { console.log('Starting vehicle'); }
  stop() { console.log('Stopping vehicle'); }
}

class LandVehicle extends Vehicle {
  drive() { console.log('Driving on land'); }
}

class Car extends LandVehicle {
  honk() { console.log('Honk!'); }
}

class ElectricCar extends Car {
  charge() { console.log('Charging'); }
  // Problem: Inherits honk() but also needs charge()
  // What about a Tesla Semi? It's electric, has horn, but is it a Car?
}

class Motorcycle extends LandVehicle {
  // Inherits drive() but not honk()
  // Has different behavior than Car
}

// Problems:
// - Deep hierarchy
// - Hard to add new vehicle types
// - Rigid structure
// - Can't easily combine features
```

✅ **Good - Composition**:
```typescript
// Small, focused interfaces
interface Startable {
  start(): void;
  stop(): void;
}

interface Drivable {
  drive(): void;
}

interface Chargeable {
  charge(): void;
  getBatteryLevel(): number;
}

interface Honkable {
  honk(): void;
}

interface Flyable {
  fly(): void;
}

// Compose behaviors
class Engine implements Startable {
  start() {
    console.log('Engine starting');
  }

  stop() {
    console.log('Engine stopping');
  }
}

class ElectricMotor implements Startable, Chargeable {
  private batteryLevel = 100;

  start() {
    console.log('Electric motor starting silently');
  }

  stop() {
    console.log('Electric motor stopping');
  }

  charge() {
    this.batteryLevel = 100;
    console.log('Fully charged');
  }

  getBatteryLevel() {
    return this.batteryLevel;
  }
}

class Wheels implements Drivable {
  drive() {
    console.log('Driving on wheels');
  }
}

class Horn implements Honkable {
  honk() {
    console.log('Honk honk!');
  }
}

class Wings implements Flyable {
  fly() {
    console.log('Flying through the air');
  }
}

// Compose vehicles from components
class Car {
  constructor(
    private engine: Startable,
    private wheels: Drivable,
    private horn: Honkable
  ) {}

  start() {
    this.engine.start();
  }

  stop() {
    this.engine.stop();
  }

  drive() {
    this.wheels.drive();
  }

  honk() {
    this.horn.honk();
  }
}

class ElectricCar {
  constructor(
    private motor: ElectricMotor, // Specific type for charging
    private wheels: Drivable,
    private horn: Honkable
  ) {}

  start() {
    this.motor.start();
  }

  stop() {
    this.motor.stop();
  }

  drive() {
    this.wheels.drive();
  }

  honk() {
    this.horn.honk();
  }

  charge() {
    this.motor.charge();
  }

  getBatteryLevel() {
    return this.motor.getBatteryLevel();
  }
}

class FlyingCar {
  constructor(
    private engine: Startable,
    private wheels: Drivable,
    private wings: Flyable,
    private horn: Honkable
  ) {}

  start() {
    this.engine.start();
  }

  drive() {
    this.wheels.drive();
  }

  fly() {
    this.wings.fly();
  }

  honk() {
    this.horn.honk();
  }
}

// Easy to create any combination
const gasCar = new Car(new Engine(), new Wheels(), new Horn());
const electricCar = new ElectricCar(new ElectricMotor(), new Wheels(), new Horn());
const flyingCar = new FlyingCar(new Engine(), new Wheels(), new Wings(), new Horn());
```

**Benefits**:
- Flexible combinations of behaviors
- Easy to add new vehicle types
- No deep inheritance hierarchy
- Reusable components
- Testable in isolation

---

### 6. **Design Patterns**

#### 6.1 Creational Patterns

**Factory Pattern**:
```typescript
// Product interface
interface PaymentProcessor {
  process(amount: number): Promise<PaymentResult>;
}

// Concrete products
class StripeProcessor implements PaymentProcessor {
  async process(amount: number) {
    // Stripe implementation
    return { success: true, transactionId: 'stripe_123' };
  }
}

class PayPalProcessor implements PaymentProcessor {
  async process(amount: number) {
    // PayPal implementation
    return { success: true, transactionId: 'paypal_456' };
  }
}

// Factory
class PaymentProcessorFactory {
  static create(type: string): PaymentProcessor {
    switch (type) {
      case 'stripe':
        return new StripeProcessor();
      case 'paypal':
        return new PayPalProcessor();
      default:
        throw new Error(`Unknown payment processor: ${type}`);
    }
  }
}

// Usage
const processor = PaymentProcessorFactory.create('stripe');
await processor.process(100);
```

**Builder Pattern**:
```typescript
class QueryBuilder {
  private query: {
    select?: string[];
    from?: string;
    where?: string[];
    orderBy?: string[];
    limit?: number;
  } = {};

  select(...fields: string[]): this {
    this.query.select = fields;
    return this;
  }

  from(table: string): this {
    this.query.from = table;
    return this;
  }

  where(condition: string): this {
    this.query.where = this.query.where || [];
    this.query.where.push(condition);
    return this;
  }

  orderBy(field: string, direction: 'ASC' | 'DESC' = 'ASC'): this {
    this.query.orderBy = this.query.orderBy || [];
    this.query.orderBy.push(`${field} ${direction}`);
    return this;
  }

  limit(count: number): this {
    this.query.limit = count;
    return this;
  }

  build(): string {
    const parts: string[] = [];

    if (this.query.select) {
      parts.push(`SELECT ${this.query.select.join(', ')}`);
    }

    if (this.query.from) {
      parts.push(`FROM ${this.query.from}`);
    }

    if (this.query.where && this.query.where.length > 0) {
      parts.push(`WHERE ${this.query.where.join(' AND ')}`);
    }

    if (this.query.orderBy && this.query.orderBy.length > 0) {
      parts.push(`ORDER BY ${this.query.orderBy.join(', ')}`);
    }

    if (this.query.limit) {
      parts.push(`LIMIT ${this.query.limit}`);
    }

    return parts.join(' ');
  }
}

// Usage - fluent interface
const query = new QueryBuilder()
  .select('id', 'name', 'email')
  .from('users')
  .where('active = true')
  .where('age > 18')
  .orderBy('created_at', 'DESC')
  .limit(10)
  .build();
```

**Singleton Pattern** (use sparingly):
```typescript
class DatabaseConnection {
  private static instance: DatabaseConnection;
  private connection: any;

  private constructor() {
    // Private constructor prevents direct instantiation
    this.connection = this.createConnection();
  }

  static getInstance(): DatabaseConnection {
    if (!DatabaseConnection.instance) {
      DatabaseConnection.instance = new DatabaseConnection();
    }
    return DatabaseConnection.instance;
  }

  private createConnection() {
    // Create database connection
    return { /* connection object */ };
  }

  query(sql: string) {
    return this.connection.query(sql);
  }
}

// Usage
const db1 = DatabaseConnection.getInstance();
const db2 = DatabaseConnection.getInstance();
// db1 === db2 (same instance)
```

#### 6.2 Structural Patterns

**Adapter Pattern**:
```typescript
// Third-party library interface (can't modify)
class OldPaymentGateway {
  makePayment(cardNumber: string, amount: number) {
    console.log(`Processing $${amount} with card ${cardNumber}`);
  }
}

// Our application's interface
interface ModernPaymentProcessor {
  process(paymentDetails: PaymentDetails): Promise<PaymentResult>;
}

interface PaymentDetails {
  method: string;
  amount: number;
  cardNumber?: string;
  email?: string;
}

// Adapter
class PaymentGatewayAdapter implements ModernPaymentProcessor {
  constructor(private oldGateway: OldPaymentGateway) {}

  async process(details: PaymentDetails): Promise<PaymentResult> {
    if (details.method === 'credit_card' && details.cardNumber) {
      this.oldGateway.makePayment(details.cardNumber, details.amount);
      return { success: true, transactionId: 'tx_123' };
    }
    throw new Error('Unsupported payment method');
  }
}

// Usage
const oldGateway = new OldPaymentGateway();
const adapter = new PaymentGatewayAdapter(oldGateway);
await adapter.process({
  method: 'credit_card',
  amount: 100,
  cardNumber: '4242424242424242'
});
```

**Decorator Pattern**:
```typescript
// Component interface
interface Coffee {
  cost(): number;
  description(): string;
}

// Concrete component
class SimpleCoffee implements Coffee {
  cost() {
    return 5;
  }

  description() {
    return 'Simple coffee';
  }
}

// Decorator base
abstract class CoffeeDecorator implements Coffee {
  constructor(protected coffee: Coffee) {}

  abstract cost(): number;
  abstract description(): string;
}

// Concrete decorators
class MilkDecorator extends CoffeeDecorator {
  cost() {
    return this.coffee.cost() + 2;
  }

  description() {
    return this.coffee.description() + ', milk';
  }
}

class SugarDecorator extends CoffeeDecorator {
  cost() {
    return this.coffee.cost() + 1;
  }

  description() {
    return this.coffee.description() + ', sugar';
  }
}

class WhipCreamDecorator extends CoffeeDecorator {
  cost() {
    return this.coffee.cost() + 3;
  }

  description() {
    return this.coffee.description() + ', whip cream';
  }
}

// Usage - stack decorators
let coffee: Coffee = new SimpleCoffee();
console.log(`${coffee.description()}: $${coffee.cost()}`);
// "Simple coffee: $5"

coffee = new MilkDecorator(coffee);
coffee = new SugarDecorator(coffee);
coffee = new WhipCreamDecorator(coffee);
console.log(`${coffee.description()}: $${coffee.cost()}`);
// "Simple coffee, milk, sugar, whip cream: $11"
```

**Repository Pattern**:
```typescript
// Entity
interface User {
  id: number;
  email: string;
  name: string;
  createdAt: Date;
}

// Repository interface
interface UserRepository {
  findById(id: number): Promise<User | null>;
  findByEmail(email: string): Promise<User | null>;
  findAll(filters?: UserFilters): Promise<User[]>;
  create(user: Omit<User, 'id' | 'createdAt'>): Promise<User>;
  update(id: number, data: Partial<User>): Promise<User>;
  delete(id: number): Promise<void>;
}

interface UserFilters {
  active?: boolean;
  createdAfter?: Date;
  limit?: number;
}

// Implementation
class PostgresUserRepository implements UserRepository {
  constructor(private db: Database) {}

  async findById(id: number): Promise<User | null> {
    const result = await this.db.query(
      'SELECT * FROM users WHERE id = $1',
      [id]
    );
    return result[0] || null;
  }

  async findByEmail(email: string): Promise<User | null> {
    const result = await this.db.query(
      'SELECT * FROM users WHERE email = $1',
      [email]
    );
    return result[0] || null;
  }

  async findAll(filters?: UserFilters): Promise<User[]> {
    let query = 'SELECT * FROM users WHERE 1=1';
    const params: any[] = [];

    if (filters?.active !== undefined) {
      params.push(filters.active);
      query += ` AND active = $${params.length}`;
    }

    if (filters?.createdAfter) {
      params.push(filters.createdAfter);
      query += ` AND created_at > $${params.length}`;
    }

    if (filters?.limit) {
      params.push(filters.limit);
      query += ` LIMIT $${params.length}`;
    }

    return await this.db.query(query, params);
  }

  async create(user: Omit<User, 'id' | 'createdAt'>): Promise<User> {
    const result = await this.db.query(
      'INSERT INTO users (email, name) VALUES ($1, $2) RETURNING *',
      [user.email, user.name]
    );
    return result[0];
  }

  async update(id: number, data: Partial<User>): Promise<User> {
    // Build dynamic update query
    const fields = Object.keys(data);
    const values = Object.values(data);
    const setClause = fields.map((field, i) => `${field} = $${i + 1}`).join(', ');

    const result = await this.db.query(
      `UPDATE users SET ${setClause} WHERE id = $${fields.length + 1} RETURNING *`,
      [...values, id]
    );
    return result[0];
  }

  async delete(id: number): Promise<void> {
    await this.db.query('DELETE FROM users WHERE id = $1', [id]);
  }
}

// Benefits:
// - Business logic doesn't know about database details
// - Easy to switch database implementations
// - Easy to test (mock repository)
// - Consistent API for data access
```

#### 6.3 Behavioral Patterns

**Strategy Pattern**:
```typescript
// Strategy interface
interface SortStrategy {
  sort(data: number[]): number[];
}

// Concrete strategies
class BubbleSort implements SortStrategy {
  sort(data: number[]): number[] {
    const arr = [...data];
    for (let i = 0; i < arr.length; i++) {
      for (let j = 0; j < arr.length - 1; j++) {
        if (arr[j] > arr[j + 1]) {
          [arr[j], arr[j + 1]] = [arr[j + 1], arr[j]];
        }
      }
    }
    return arr;
  }
}

class QuickSort implements SortStrategy {
  sort(data: number[]): number[] {
    if (data.length <= 1) return data;
    
    const pivot = data[0];
    const left = data.slice(1).filter(x => x <= pivot);
    const right = data.slice(1).filter(x => x > pivot);
    
    return [...this.sort(left), pivot, ...this.sort(right)];
  }
}

class MergeSort implements SortStrategy {
  sort(data: number[]): number[] {
    if (data.length <= 1) return data;
    
    const mid = Math.floor(data.length / 2);
    const left = this.sort(data.slice(0, mid));
    const right = this.sort(data.slice(mid));
    
    return this.merge(left, right);
  }

  private merge(left: number[], right: number[]): number[] {
    const result: number[] = [];
    let i = 0, j = 0;
    
    while (i < left.length && j < right.length) {
      if (left[i] < right[j]) {
        result.push(left[i++]);
      } else {
        result.push(right[j++]);
      }
    }
    
    return [...result, ...left.slice(i), ...right.slice(j)];
  }
}

// Context
class Sorter {
  constructor(private strategy: SortStrategy) {}

  setStrategy(strategy: SortStrategy) {
    this.strategy = strategy;
  }

  sort(data: number[]): number[] {
    return this.strategy.sort(data);
  }
}

// Usage
const data = [5, 2, 8, 1, 9];

const sorter = new Sorter(new QuickSort());
console.log(sorter.sort(data)); // [1, 2, 5, 8, 9]

// Change strategy at runtime
sorter.setStrategy(new MergeSort());
console.log(sorter.sort(data)); // [1, 2, 5, 8, 9]
```

**Observer Pattern** (Pub/Sub):
```typescript
// Observer interface
interface Observer<T> {
  update(data: T): void;
}

// Subject (Observable)
class EventEmitter<T> {
  private observers: Map<string, Observer<T>[]> = new Map();

  subscribe(event: string, observer: Observer<T>): () => void {
    if (!this.observers.has(event)) {
      this.observers.set(event, []);
    }

    this.observers.get(event)!.push(observer);

    // Return unsubscribe function
    return () => {
      const observers = this.observers.get(event) || [];
      const index = observers.indexOf(observer);
      if (index > -1) {
        observers.splice(index, 1);
      }
    };
  }

  emit(event: string, data: T): void {
    const observers = this.observers.get(event) || [];
    observers.forEach(observer => observer.update(data));
  }
}

// Concrete observers
class EmailNotifier implements Observer<{ email: string; message: string }> {
  update(data: { email: string; message: string }): void {
    console.log(`Sending email to ${data.email}: ${data.message}`);
  }
}

class SMSNotifier implements Observer<{ phone: string; message: string }> {
  update(data: { phone: string; message: string }): void {
    console.log(`Sending SMS to ${data.phone}: ${data.message}`);
  }
}

class Logger implements Observer<any> {
  update(data: any): void {
    console.log(`[LOG] Event occurred:`, data);
  }
}

// Usage
const eventBus = new EventEmitter();

const emailNotifier = new EmailNotifier();
const smsNotifier = new SMSNotifier();
const logger = new Logger();

eventBus.subscribe('user.created', emailNotifier);
eventBus.subscribe('user.created', logger);
eventBus.subscribe('order.placed', smsNotifier);
eventBus.subscribe('order.placed', logger);

// Emit events
eventBus.emit('user.created', {
  email: 'user@example.com',
  message: 'Welcome!'
});

eventBus.emit('order.placed', {
  phone: '+1234567890',
  message: 'Order confirmed'
});
```

**Chain of Responsibility**:
```typescript
// Handler interface
interface RequestHandler {
  setNext(handler: RequestHandler): RequestHandler;
  handle(request: Request): Response | null;
}

interface Request {
  type: string;
  data: any;
}

interface Response {
  handled: boolean;
  data?: any;
}

// Abstract handler
abstract class AbstractRequestHandler implements RequestHandler {
  private nextHandler?: RequestHandler;

  setNext(handler: RequestHandler): RequestHandler {
    this.nextHandler = handler;
    return handler;
  }

  handle(request: Request): Response | null {
    if (this.canHandle(request)) {
      return this.process(request);
    }

    if (this.nextHandler) {
      return this.nextHandler.handle(request);
    }

    return null;
  }

  protected abstract canHandle(request: Request): boolean;
  protected abstract process(request: Request): Response;
}

// Concrete handlers
class AuthenticationHandler extends AbstractRequestHandler {
  protected canHandle(request: Request): boolean {
    return request.type === 'auth';
  }

  protected process(request: Request): Response {
    console.log('Authenticating user...');
    return { handled: true, data: { authenticated: true } };
  }
}

class ValidationHandler extends AbstractRequestHandler {
  protected canHandle(request: Request): boolean {
    return request.type === 'validate';
  }

  protected process(request: Request): Response {
    console.log('Validating request...');
    return { handled: true, data: { valid: true } };
  }
}

class LoggingHandler extends AbstractRequestHandler {
  protected canHandle(request: Request): boolean {
    return request.type === 'log';
  }

  protected process(request: Request): Response {
    console.log('Logging request:', request.data);
    return { handled: true };
  }
}

// Usage
const authHandler = new AuthenticationHandler();
const validationHandler = new ValidationHandler();
const loggingHandler = new LoggingHandler();

// Build chain
authHandler
  .setNext(validationHandler)
  .setNext(loggingHandler);

// Process requests
authHandler.handle({ type: 'auth', data: { username: 'user' } });
authHandler.handle({ type: 'validate', data: { email: 'test@example.com' } });
authHandler.handle({ type: 'log', data: { message: 'Test log' } });
```

---

## Code Quality Metrics

**Audit Checklist**:
- [ ] Cyclomatic Complexity: < 10 per function
- [ ] Function Length: < 30 lines
- [ ] Class Length: < 300 lines
- [ ] Parameter Count: < 5 parameters
- [ ] Code Duplication: < 3%
- [ ] Test Coverage: > 80%
- [ ] Documentation Coverage: > 70%

**Tools for Measurement**:
- **JavaScript/TypeScript**: ESLint, SonarQube, CodeClimate
- **Python**: Pylint, Radon, SonarQube
- **Java**: SonarQube, Checkstyle, PMD
- **C#**: SonarQube, Roslyn Analyzers

---

## Refactoring Deliverables

For each design issue identified, provide:

1. **Analysis Report**:
   - Current design problems
   - SOLID principle violations
   - Code smells identified
   - Design pattern opportunities

2. **Refactored Code**:
   - Before/after comparison
   - Design pattern implementations
   - SOLID compliance improvements
   - Type safety enhancements

3. **Documentation**:
   - Architecture diagrams
   - Class diagrams (UML)
   - Sequence diagrams for complex flows
   - API documentation

4. **Tests**:
   - Unit tests for refactored code
   - Integration tests
   - Ensure behavior is preserved

5. **Migration Guide**:
   - Step-by-step refactoring plan
   - Breaking changes (if any)
   - Deprecation notices

---

## Pre-Assessment Questions

To customize this testing and security strategy for your specific projects, please provide:

### Application Information

1. **Technology Stack for each project** (Saberstore, EgyTour, PriceGenie):
   - Frontend: (React? Next.js? Vue? Angular?)
   - Backend: (Node.js/Express? Django? FastAPI? .NET?)
   - Database: (PostgreSQL? MongoDB? MySQL?)
   - Cloud Platform: (AWS? Azure? GCP? Vercel?)

2. **Application Types**:
   - Saberstore: E-commerce platform?
   - EgyTour: Tourism/booking platform?
   - PriceGenie: Price comparison/aggregator?
   - Orthodontic app: SaaS/tool?

3. **Critical Features** (for each project):
   - What features are most important to users?
   - What features generate revenue?
   - What features pose the highest risk if they fail?

### Current State

4. **Existing Codebase**:
   - Repository access (GitHub URLs)
   - Current deployment status (live, staging, development)
   - Any existing tests or quality measures?

5. **Third-Party Integrations**:
   - Payment: Stripe? PayPal? Local payment gateways?
   - Email: SendGrid? Mailgun? AWS SES?
   - Storage: S3? Azure Blob? Local?
   - APIs: Which external services do you integrate with?

### Testing & Security Priorities

6. **What's the biggest concern?**
   - Functional correctness (does it work?)
   - Performance (can it scale?)
   - Security (is it safe?)
   - User experience (is it usable?)

7. **Timeline**:
   - Gradual implementation - over what timeframe? (3 months? 6 months?)
   - Are any projects closer to launch than others?
   - Any hard deadlines (investor demos, product launches)?

### Constraints

8. **Team & Resources**:
   - Will you be writing tests yourself or with a team?
   - Testing experience level: Beginner
   - Available time per week for testing work?

9. **Infrastructure**:
   - Can you provision test environments?
   - Can you reset test databases?
   - Do you have CI/CD pipelines set up?

### Compliance

10. **Regulatory Requirements**:
    - E-commerce (Saberstore): PCI-DSS for payments?
    - Tourism (EgyTour): Data privacy requirements (GDPR for EU tourists)?
    - Price aggregator: Any regulations?

---

## Execution Plan

Once you answer these questions, I will:

1. **Analyze each project** and determine optimal testing approach
2. **Select best testing tools** for each stack
3. **Create comprehensive test plans** tailored to each project
4. **Implement test automation** with complete code
5. **Conduct security audit** for each application
6. **Provide remediation code** for all vulnerabilities
7. **Review software architecture** and identify design improvements
8. **Refactor code** following SOLID principles and design patterns
9. **Implement dependency injection** and improve type safety
10. **Set up test infrastructure** (CI/CD, reporting, data management)
11. **Provide gradual rollout plan** matching your timeline
12. **Create comprehensive documentation** (architecture, API, maintenance guides)
13. **Train you** on testing, security, and software design best practices

---

## Final Deliverables

You will receive:

### Security Deliverables:
1. ✅ **Executive Security Summary**: Risk assessment and prioritized findings
2. ✅ **Detailed Vulnerability Report**: Each finding with severity, reproduction, and remediation
3. ✅ **Remediation Code**: Actual code fixes for all vulnerabilities
4. ✅ **Security Test Suite**: Automated tests to prevent regression
5. ✅ **Security Configuration**: Headers, middleware, environment setup
6. ✅ **OWASP Compliance Report**: Mapping to OWASP Top 10
7. ✅ **Threat Model Documentation**: Architecture diagrams and threat analysis

### Testing Deliverables:
1. ✅ **Complete Test Suite**: Unit, integration, E2E, API, performance, security tests
2. ✅ **Test Infrastructure**: CI/CD integration, reporting, data management
3. ✅ **Test Documentation**: How to run, write, and maintain tests
4. ✅ **Coverage Reports**: 80%+ unit test coverage, critical path E2E coverage
5. ✅ **Performance Benchmarks**: Load testing results and recommendations
6. ✅ **Quality Metrics Dashboard**: Real-time test results and trends
7. ✅ **Test Maintenance Guide**: Best practices for ongoing test development

### Software Design Deliverables:
1. ✅ **Architecture Review Report**: SOLID principle violations and design smells
2. ✅ **Refactored Codebase**: Clean, maintainable code following best practices
3. ✅ **Design Pattern Implementations**: Proper use of Factory, Strategy, Repository, etc.
4. ✅ **Type Safety Improvements**: Comprehensive TypeScript/type hints
5. ✅ **Documentation**: JSDoc/TSDoc, architecture diagrams, API docs
6. ✅ **Dependency Injection Setup**: DI container configuration and best practices
7. ✅ **Code Quality Metrics**: Cyclomatic complexity, code duplication, maintainability index
8. ✅ **Migration Guide**: Step-by-step refactoring roadmap

---

## Success Metrics

Your applications will be **secure, well-tested, and beautifully designed** when:

**Security**:
- ✅ Zero Critical/High severity vulnerabilities
- ✅ All OWASP Top 10 categories addressed
- ✅ Automated security scanning in CI/CD
- ✅ Security test coverage for all attack vectors
- ✅ Compliance requirements met (PCI-DSS, GDPR, etc.)

**Testing**:
- ✅ 80%+ unit test code coverage
- ✅ 100% critical user flow E2E coverage
- ✅ All API endpoints tested (functional + security)
- ✅ Performance benchmarks met (95th percentile < 500ms)
- ✅ < 1% flaky tests (reliable, maintainable tests)
- ✅ Automated testing in CI/CD (every commit)
- ✅ Quality gates prevent buggy code from production

**Software Design**:
- ✅ All SOLID principles followed
- ✅ Cyclomatic complexity < 10 per function
- ✅ No code duplication > 3%
- ✅ Proper design patterns implemented where appropriate
- ✅ Dependency injection for all services
- ✅ Comprehensive type safety (TypeScript strict mode or equivalent)
- ✅ 100% public API documentation coverage
- ✅ Composition over inheritance throughout codebase
- ✅ Clear separation of concerns (presentation, business logic, data access)
- ✅ Maintainability index > 70

---

**Let's build bulletproof, elegant, and maintainable applications together! 🚀🔒✨**

Please provide the information requested above so I can create a customized security audit, testing implementation, and software design improvement plan for your projects.
