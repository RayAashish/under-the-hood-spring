# Module 06: Spring Security & Identity Management

---

<a id="table-of-contents"></a>
## 📑 Table of Contents
- [1. Spring Security Foundations & Core Architecture (Lecture 35)](#1-spring-security-foundations--core-architecture-lecture-35)
  - [1.1 Authentication vs Authorization](#11-authentication-vs-authorization)
  - [1.2 Security Context & The Identity Model: `Principal`, `GrantedAuthority`, `Authentication`](#12-security-context--the-identity-model-principal-grantedauthority-authentication)
  - [1.3 `SecurityContextHolder` & `ThreadLocal` Storage Strategy](#13-securitycontextholder--threadlocal-storage-strategy)
  - [1.4 The Security Filter Chain: `DelegatingFilterProxy` & `FilterChainProxy`](#14-the-security-filter-chain-delegatingfilterproxy--filterchainproxy)
  - [1.5 `SecurityFilterChain` & Modern Lambda DSL Configuration](#15-securityfilterchain--modern-lambda-dsl-configuration)
- [2. Database Authentication & Password Security (Lecture 36)](#2-database-authentication--password-security-lecture-36)
  - [2.1 The Danger of Plain Text & Fast Hashing (MD5/SHA)](#21-the-danger-of-plain-text--fast-hashing-md5sha)
  - [2.2 Salt, Work Factor, and Adaptive Hashing (BCrypt, SCrypt, Argon2)](#22-salt-work-factor-and-adaptive-hashing-bcrypt-scrypt-argon2)
  - [2.3 Spring Security `PasswordEncoder` & BCrypt Decoded](#23-spring-security-passwordencoder--bcrypt-decoded)
  - [2.4 Modeling User Identity: `UserDetails` & `UserDetailsService`](#24-modeling-user-identity-userdetails--userdetailsservice)
  - [2.5 Database Schema Design for Users and Roles](#25-database-schema-design-for-users-and-roles)
- [3. Complete Authentication Flow & Internals (Lecture 37)](#3-complete-authentication-flow--internals-lecture-37)
  - [3.1 The 9-Step Authentication Execution Pipeline](#31-the-9-step-authentication-execution-pipeline)
  - [3.2 Key Components: `AuthenticationManager`, `ProviderManager`, `DaoAuthenticationProvider`](#32-key-components-authenticationmanager-providermanager-daoauthenticationprovider)
  - [3.3 Custom Database Authentication Implementation](#33-custom-database-authentication-implementation)
  - [3.4 Role-Based Access Control (RBAC) & Method Security](#34-role-based-access-control-rbac--method-security)
- [4. Stateless Security with JSON Web Tokens (JWT) (Lecture 38)](#4-stateless-security-with-json-web-tokens-jwt-lecture-38)
  - [4.1 Stateful Sessions vs Stateless Tokens](#41-stateful-sessions-vs-stateless-tokens)
  - [4.2 Anatomy of a JWT: Header, Payload (Claims), Signature](#42-anatomy-of-a-jwt-header-payload-claims-signature)
  - [4.3 Building `JwtUtils`: Token Generation, Parsing & Signing](#43-building-jwtutils-token-generation-parsing--signing)
  - [4.4 Building the `JwtAuthenticationFilter` (`OncePerRequestFilter`)](#44-building-the-jwtauthenticationfilter-onceperrequestfilter)
  - [4.5 Configuring Stateless `SecurityFilterChain`](#45-configuring-stateless-securityfilterchain)
- [5. OAuth 2.0, OpenID Connect & Social Login (Lecture 39)](#5-oauth-20-openid-connect--social-login-lecture-39)
  - [5.1 The Delegated Authorization Problem](#51-the-delegated-authorization-problem)
  - [5.2 The 4 OAuth 2.0 Roles & OIDC Identity Layer](#52-the-4-oauth-20-roles--oidc-identity-layer)
  - [5.3 Authorization Code Flow with PKCE](#53-authorization-code-flow-with-pkce)
  - [5.4 Spring Boot Google Social Login Setup](#54-spring-boot-google-social-login-setup)

---

## 1. Spring Security Foundations & Core Architecture (Lecture 35)

### 1.1 Authentication vs Authorization
Security is divided into two distinct gates:
1. **Authentication ("Who are you?")**:
   - The process of validating a user's claimed identity using credentials (Username/Password, OTP, API Key, Biometrics, JWT).
2. **Authorization ("What are you allowed to do?")**:
   - The process of verifying whether an authenticated user has the necessary permissions (Roles / Authorities) to access a specific resource or execute an action (e.g., Can User X access `DELETE /api/v1/students/42`?).

> [!NOTE]
> Authorization can **never** happen before Authentication. An identity must be verified before permissions can be evaluated.

[⬆ Back to Top](#table-of-contents)

---

### 1.2 Security Context & The Identity Model: `Principal`, `GrantedAuthority`, `Authentication`

```
┌────────────────────────────────────────────────────────┐
│ SecurityContextHolder (Static Access Point)            │
└───────────────────────┬────────────────────────────────┘
                        │ holds
┌───────────────────────▼────────────────────────────────┐
│ SecurityContext                                        │
└───────────────────────┬────────────────────────────────┘
                        │ holds
┌───────────────────────▼────────────────────────────────┐
│ Authentication                                         │
│  - getPrincipal()   --> Returns UserDetails / Identity │
│  - getCredentials() --> Raw password (erased post-auth)│
│  - getAuthorities() --> Collection of GrantedAuthority │
│  - isAuthenticated()--> boolean flag                   │
└────────────────────────────────────────────────────────┘
```

1. **`Principal`**: The currently logged-in entity (usually an implementation of `UserDetails`).
2. **`GrantedAuthority`**: An individual permission granted to the principal (e.g., `ROLE_ADMIN`, `READ_PRIVILEGE`).
3. **`Authentication`**: The core token representing an authentication request or an authenticated principal.
4. **`SecurityContext`**: Holds the currently active `Authentication` object for the current request.

[⬆ Back to Top](#table-of-contents)

---

### 1.3 `SecurityContextHolder` & `ThreadLocal` Storage Strategy
`SecurityContextHolder` is where Spring Security stores details of the currently authenticated principal:

```java
// Accessing currently authenticated user anywhere in the call stack
Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
String currentUsername = authentication.getName();
```

#### Under the Hood: The `ThreadLocal` Strategy
By default, `SecurityContextHolder` uses a **`ThreadLocal`** strategy.
Because Tomcat allocates a dedicated worker thread from its thread pool for each incoming HTTP request, storing the `SecurityContext` in `ThreadLocal` means:
- The authenticated identity is automatically available anywhere across the Service, Repository, or Controller layers during that request.
- Different concurrent user requests never leak or collide with each other.
- At the end of the request, Spring Security clears the `ThreadLocal` to prevent thread-pool reuse leaks.

[⬆ Back to Top](#table-of-contents)

---

### 1.4 The Security Filter Chain: `DelegatingFilterProxy` & `FilterChainProxy`
How does Spring Security (managed in the Spring IoC Container) intercept HTTP requests arriving at Apache Tomcat (managed outside Spring in the Servlet Container)?

```
Tomcat Servlet Container
       │
       ▼
┌──────────────────────────────────────┐
│ DelegatingFilterProxy                │  <-- Standard Servlet Filter registered in Tomcat
└──────────────────┬───────────────────┘
                   │ Delegates request to Spring IoC Bean
                   ▼
┌──────────────────────────────────────┐
│ FilterChainProxy                     │  <-- Spring Bean managing security chains
└──────────────────┬───────────────────┘
                   │ Routes to matching chain
                   ▼
┌─────────────────────────────────────────────────────────────────┐
│ SecurityFilterChain (List of Security Filters)                  │
│  1. CorsFilter                                                  │
│  2. CsrfFilter                                                  │
│  3. UsernamePasswordAuthenticationFilter / JwtAuthFilter        │
│  4. SecurityContextHolderAwareRequestFilter                     │
│  5. AuthorizationFilter (Enforces URL path RBAC permissions)   │
└─────────────────────────────────────────────────────────────────┘
```

1. **`DelegatingFilterProxy`**: A standard Servlet filter known to Tomcat. It does not perform security logic itself; it acts as a bridge that looks up the `springSecurityFilterChain` bean in the Spring `ApplicationContext`.
2. **`FilterChainProxy`**: The master Spring bean that coordinates one or more `SecurityFilterChain` instances.
3. **`SecurityFilterChain`**: An ordered list of filters responsible for authentication, CSRF, session management, and authorization.

[⬆ Back to Top](#table-of-contents)

---

### 1.5 `SecurityFilterChain` & Modern Lambda DSL Configuration
In modern Spring Security 6 (Spring Boot 3), configuration is declared using the lambda DSL on a `@Bean` returning `SecurityFilterChain`:

```java
package com.raysi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class BasicSecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 1. Configure URL-based authorization rules
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/public/**", "/api/v1/auth/**").permitAll()
                .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            // 2. Enable HTTP Basic Authentication
            .httpBasic(Customizer.withDefaults());

        return http.build();
    }
}
```

[⬆ Back to Top](#table-of-contents)

---

## 2. Database Authentication & Password Security (Lecture 36)

### 2.1 The Danger of Plain Text & Fast Hashing (MD5/SHA)
- **Plain Text**: Storing passwords as raw text (`password123`) is a catastrophic security disaster. A single SQL injection or database dump compromises all user accounts.
- **Fast Hashing Algorithms (MD5, SHA-1, SHA-256)**:
  - Designed to hash files rapidly (gigabytes per second).
  - *Vulnerability*: Attackers use precomputed **Rainbow Tables** or modern GPUs capable of calculating billions of SHA-256 hashes per second to reverse hashes instantly.

[⬆ Back to Top](#table-of-contents)

---

### 2.2 Salt, Work Factor, and Adaptive Hashing (BCrypt, SCrypt, Argon2)
Modern password security requires **Key Derivation Functions (Slow Hashing Algorithms)**:
1. **Cryptographic Salt**: A unique, random byte string generated per password before hashing. Neutralizes rainbow tables completely because identical passwords produce entirely different hashes.
2. **Work Factor (Cost Factor)**: Configurable iteration parameter (e.g., $2^{10} = 1,024$ rounds in BCrypt). Makes hashing deliberately slow (e.g., 100 milliseconds per hash) to make brute-force attacks computationally impossible.

[⬆ Back to Top](#table-of-contents)

---

### 2.3 Spring Security `PasswordEncoder` & BCrypt Decoded

```java
package com.raysi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SecurityBeansConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        // Default strength cost factor is 10 (2^10 = 1024 rounds)
        return new BCryptPasswordEncoder(12);
    }
}
```

#### Anatomy of a BCrypt Hash String (60 Characters):
```
$2a$12$e8uq9kY6d2WpG0sP8Y5c.O3mE0H9gV8qL7vB5xW4zT1nQ2yU6rM.e
│──│ ││ │────────────────────││──────────────────────────────│
 1    2           3                          4
```
1. **`$2a$`**: BCrypt algorithm version identifier.
2. **`$12$`**: Cost / Work Factor ($2^{12} = 4,096$ rounds).
3. **`e8uq9kY6d2WpG0sP8Y5c.O`**: 22-character Base64 encoded **Salt** (128 bits).
4. **`3mE0H9gV8qL7vB5xW4zT1nQ2yU6rM.e`**: 31-character Base64 encoded **Hash Output** (184 bits).

> [!TIP]
> The salt is stored directly inside the hash string! When verifying `encoder.matches(rawPassword, encodedHash)`, BCrypt extracts the salt and cost factor from the stored string, hashes the candidate password using that exact salt and cost, and verifies if the resulting hash matches.

[⬆ Back to Top](#table-of-contents)

---

### 2.4 Modeling User Identity: `UserDetails` & `UserDetailsService`

#### 1. The `UserDetails` Interface
Spring Security's representation of an application user:

```java
package com.raysi.security;

import com.raysi.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.stream.Collectors;

public class CustomUserDetails implements UserDetails {

    private final User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() { return user.getPassword(); }

    @Override
    public String getUsername() { return user.getUsername(); }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return !user.isLocked(); }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return user.isEnabled(); }
}
```

#### 2. The `UserDetailsService` Interface
The bridge between Spring Security and your database repository:

```java
package com.raysi.security;

import com.raysi.entity.User;
import com.raysi.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        return new CustomUserDetails(user);
    }
}
```

[⬆ Back to Top](#table-of-contents)

---

## 3. Complete Authentication Flow & Internals (Lecture 37)

### 3.1 The 9-Step Authentication Execution Pipeline

```
1. Client sends HTTP Request with credentials (POST /login)
       │
       ▼
2. UsernamePasswordAuthenticationFilter
   - Extracts username & password
   - Creates unauthenticated UsernamePasswordAuthenticationToken
       │
       ▼
3. AuthenticationManager (ProviderManager)
   - Iterates registered AuthenticationProviders
       │
       ▼
4. DaoAuthenticationProvider
   - Invokes UserDetailsService.loadUserByUsername(username)
       │
       ▼
5. Database (UserRepository)
   - Fetches UserDetails with stored BCrypt password hash
       │
       ▼
6. PasswordEncoder.matches(rawPassword, storedHash)
   - Validates password match
       │
       ├── Password Mismatch? ──[ YES ]──> Throws BadCredentialsException (401)
       │
      [ MATCHES ]
       │
       ▼
7. Create Authenticated UsernamePasswordAuthenticationToken
   - Contains Principal, Authorities, and credentials erased
       │
       ▼
8. SecurityContextHolder.getContext().setAuthentication(authenticatedToken)
       │
       ▼
9. Request passes to Controller / Application Endpoint!
```

[⬆ Back to Top](#table-of-contents)

---

### 3.2 Key Components: `AuthenticationManager`, `ProviderManager`, `DaoAuthenticationProvider`
- **`AuthenticationManager`**: The core API interface defining how Spring Security authenticates.
- **`ProviderManager`**: Default implementation of `AuthenticationManager`. It holds a list of `AuthenticationProvider` delegates.
- **`DaoAuthenticationProvider`**: Specializes in verifying username/password against a database via `UserDetailsService` and `PasswordEncoder`.

```java
package com.raysi.config;

import com.raysi.security.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AuthManagerConfig {

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider(
            CustomUserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
```

[⬆ Back to Top](#table-of-contents)

---

### 3.4 Role-Based Access Control (RBAC) & Method Security
Enable fine-grained method-level security by adding `@EnableMethodSecurity`:

```java
package com.raysi.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@Configuration
@EnableMethodSecurity // Activates @PreAuthorize, @PostAuthorize, @Secured
public class MethodSecurityConfig {}
```

```java
package com.raysi.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/students")
public class SecureStudentController {

    @GetMapping
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
    public String viewStudents() {
        return "Student list data";
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteStudent(@PathVariable Long id) {
        return "Student " + id + " deleted by ADMIN";
    }
}
```

[⬆ Back to Top](#table-of-contents)

---

## 4. Stateless Security with JSON Web Tokens (JWT) (Lecture 38)

### 4.1 Stateful Sessions vs Stateless Tokens

| Dimension | Stateful Session-Based Security | Stateless Token-Based Security (JWT) |
| :--- | :--- | :--- |
| **State Location** | Server Memory (HttpSession / Redis) | Client-side (Token sent with each request) |
| **Identification**| Random `JSESSIONID` cookie | Cryptographically signed, self-contained JWT |
| **Scalability** | Hard to scale across multiple servers without sticky sessions or distributed cache | Horizontally scalable out of the box (any server can verify token signature) |
| **CSRF Risk** | High (Vulnerable to Cross-Site Request Forgery via ambient cookies) | Minimal (Tokens passed explicitly in `Authorization: Bearer` headers) |

[⬆ Back to Top](#table-of-contents)

---

### 4.2 Anatomy of a JWT: Header, Payload (Claims), Signature
A JSON Web Token consists of three parts joined by dots (`.`): `header.payload.signature`

```
eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.
eyJzdWIiOiJhYXNoaXNoIiwicm9sZXMiOlsiUk9MRV9BRE1JTiJdLCJleHAiOjE3OTIyNjAwMDB9.
k8z5O7wL1qJ2xV9mN4bC6vP3zT8yU0rI5eA4sD1fG7h
```

1. **Header (Base64Url Encoded)**:
   ```json
   { "alg": "HS256", "typ": "JWT" }
   ```
2. **Payload / Claims (Base64Url Encoded)**:
   ```json
   {
     "sub": "aashish",
     "roles": ["ROLE_ADMIN"],
     "iat": 1792250000,
     "exp": 1792260000
   }
   ```
3. **Digital Signature**:
   ```
   HMACSHA256(
     base64UrlEncode(header) + "." + base64UrlEncode(payload),
     secretKey
   )
   ```
> [!CAUTION]
> The payload is **encoded**, NOT encrypted! Anyone can decode Base64 and view claims. Never place passwords or credit cards in a JWT payload!

[⬆ Back to Top](#table-of-contents)

---

### 4.3 Building `JwtUtils`: Token Generation, Parsing & Signing
Add the `jjwt` library to `pom.xml`:

```java
package com.raysi.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtils {

    // Must be at least 256 bits (32 bytes) for HMAC-SHA256
    private static final String SECRET = "raysiSuperSecretKeyForJwtSigningMustBeVeryLongAndSecure2026";
    private static final long EXPIRATION_MS = 86400000L; // 24 hours

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
    }

    // 1. Generate Token
    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_MS))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // 2. Extract Username
    public String extractUsername(String token) {
        return getClaims(token).getSubject();
    }

    // 3. Validate Token
    public boolean validateToken(String token) {
        try {
            getClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
```

[⬆ Back to Top](#table-of-contents)

---

### 4.4 Building the `JwtAuthenticationFilter` (`OncePerRequestFilter`)

```java
package com.raysi.security.jwt;

import com.raysi.security.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final CustomUserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtUtils jwtUtils, CustomUserDetailsService userDetailsService) {
        this.jwtUtils = jwtUtils;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        // 1. Check for Bearer token in Authorization header
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String token = authHeader.substring(7);

        // 2. Validate token and extract username
        if (jwtUtils.validateToken(token)) {
            String username = jwtUtils.extractUsername(token);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // 3. Set authentication in SecurityContext
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}
```

[⬆ Back to Top](#table-of-contents)

---

### 4.5 Configuring Stateless `SecurityFilterChain`

```java
package com.raysi.config;

import com.raysi.security.jwt.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class JwtSecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;

    public JwtSecurityConfig(JwtAuthenticationFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 1. Disable CSRF for stateless REST APIs
            .csrf(csrf -> csrf.disable())
            // 2. Set Session Creation Policy to STATELESS (Never create HttpSession)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            // 3. Define authorization rules
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/v1/auth/**").permitAll()
                .anyRequest().authenticated()
            )
            // 4. Inject JwtAuthenticationFilter before UsernamePasswordAuthenticationFilter
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
```

[⬆ Back to Top](#table-of-contents)

---

## 5. OAuth 2.0, OpenID Connect & Social Login (Lecture 39)

### 5.1 The Delegated Authorization Problem
In the past, to let an application access your photos or calendar, you had to hand your credentials (username & password) directly to the third-party application.
- Password compromised if third-party breached.
- Third party gained full, unrestricted access to the entire account.
- Revoking access required changing your master password.

**Solution**: **OAuth 2.0** provides **Delegated Access via Scoped Access Tokens**.

[⬆ Back to Top](#table-of-contents)

---

### 5.2 The 4 OAuth 2.0 Roles & OIDC Identity Layer

```
┌──────────────────────────────────────┐
│       1. Resource Owner (User)       │
└──────────────────┬───────────────────┘
                   │ Grants permission
                   ▼
┌──────────────────────────────────────┐
│       2. Client Application          │
│         (Spring Boot App)            │
└─────────┬───────────────────┬────────┘
          │                   │
  Token   │                   │ Request Protected
  Request │                   │ Resource with Token
          ▼                   ▼
┌──────────────────┐ ┌──────────────────┐
│3. Authorization  │ │4. Resource Server│
│   Server (Google)│ │   (Google API)   │
└──────────────────┘ └──────────────────┘
```

- **OAuth 2.0**: Framework for **Authorization** (Access Token).
- **OpenID Connect (OIDC)**: Thin identity layer on top of OAuth 2.0 that provides an **`id_token`** (JWT) describing *who* the user is (name, email, avatar).

[⬆ Back to Top](#table-of-contents)

---

### 5.3 Authorization Code Flow with PKCE
The most secure authorization flow for web and mobile clients:
1. User clicks **"Login with Google"**.
2. Spring Boot redirects browser to Google's Authorization Endpoint with:
   - `client_id`
   - `redirect_uri` (e.g. `http://localhost:8080/login/oauth2/code/google`)
   - `response_type=code`
   - `scope=openid profile email`
3. User signs into Google and confirms consent.
4. Google redirects user back to Spring Boot with a short-lived authorization `code`.
5. Spring Boot makes a secure server-to-server POST call directly to Google's Token Endpoint exchanging `code` + `client_secret` for `access_token` and `id_token`.
6. Spring Boot decodes the `id_token` and creates an authenticated `OAuth2AuthenticationToken`.

[⬆ Back to Top](#table-of-contents)

---

### 5.4 Spring Boot Google Social Login Setup

#### 1. Add Dependency:
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-oauth2-client</artifactId>
</dependency>
```

#### 2. Configuration (`application.yml`):
```yaml
spring:
  security:
    oauth2:
      client:
        registration:
          google:
            client-id: YOUR_GOOGLE_CLIENT_ID.apps.googleusercontent.com
            client-secret: YOUR_GOOGLE_CLIENT_SECRET
            scope:
              - openid
              - profile
              - email
```

#### 3. Enable in `SecurityFilterChain`:
```java
@Bean
public SecurityFilterChain oauth2SecurityFilterChain(HttpSecurity http) throws Exception {
    http
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/", "/login**", "/error").permitAll()
            .anyRequest().authenticated()
        )
        // Enables Google login redirect handling automatically!
        .oauth2Login(Customizer.withDefaults());

    return http.build();
}
```

#### 4. Accessing Authenticated User Profile in Controller:
```java
package com.raysi.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
public class UserProfileController {

    @GetMapping("/user/profile")
    public Map<String, Object> getUserProfile(@AuthenticationPrincipal OAuth2User principal) {
        // Contains Google name, email, picture, sub ID
        return principal.getAttributes();
    }
}
```

[⬆ Back to Top](#table-of-contents)
