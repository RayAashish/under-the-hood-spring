# Module 02: Spring Boot Essentials, Web & MVC

---

<a id="table-of-contents"></a>
## 📑 Table of Contents
- [1. Spring Boot Auto-Configuration & Starters (Lecture 09)](#1-spring-boot-auto-configuration--starters-lecture-09)
  - [1.1 Why Spring Boot After Spring Core?](#11-why-spring-boot-after-spring-core)
  - [1.2 Starter Dependencies & `spring-boot-starter-parent`](#12-starter-dependencies--spring-boot-starter-parent)
  - [1.3 Anatomy of `@SpringBootApplication` & Startup Mechanism](#13-anatomy-of-springbootapplication--startup-mechanism)
  - [1.4 How Auto-Configuration Works (`@ConditionalOnClass`, `@ConditionalOnMissingBean`)](#14-how-auto-configuration-works-conditionalonclass-conditionalonmissingbean)
- [2. Configuration Management: Properties, `@Value` & `@ConfigurationProperties` (Lecture 10)](#2-configuration-management-properties-value--configurationproperties-lecture-10)
  - [2.1 Externalized Configuration Hierarchy](#21-externalized-configuration-hierarchy)
  - [2.2 Value Injection with `@Value` & Default Values](#22-value-injection-with-value--default-values)
  - [2.3 Type-Safe Binding with `@ConfigurationProperties`](#23-type-safe-binding-with-configurationproperties)
  - [2.4 Relaxed Binding & Property Validation](#24-relaxed-binding--property-validation)
- [3. REST Fundamentals & CRUD Operations (Lecture 11)](#3-rest-fundamentals--crud-operations-lecture-11)
  - [3.1 Principles of RESTful APIs](#31-principles-of-restful-apis)
  - [3.2 HTTP Verbs & Status Code Mapping](#32-http-verbs--status-code-mapping)
  - [3.3 Spring Web Annotations: `@RestController`, `@RequestMapping`, `@PathVariable`, `@RequestBody`](#33-spring-web-annotations-restcontroller-requestmapping-pathvariable-requestbody)
  - [3.4 In-Memory CRUD Implementation](#34-in-memory-crud-implementation)
- [4. Layered Architecture: Database, Entity, Service, Controller (Lecture 12)](#4-layered-architecture-database-entity-service-controller-lecture-12)
  - [4.1 Why Layered Architecture?](#41-why-layered-architecture)
  - [4.2 Layer Responsibilities: Controller, Service, Repository, Entity](#42-layer-responsibilities-controller-service-repository-entity)
  - [4.3 Configuring Database Connection (`application.properties`)](#43-configuring-database-connection-applicationproperties)
  - [4.4 End-to-End CRUD Flow Across Layers](#44-end-to-end-crud-flow-across-layers)
- [5. Soft Delete Architecture (Lecture 13)](#5-soft-delete-architecture-lecture-13)
  - [5.1 Hard Delete vs Soft Delete](#51-hard-delete-vs-soft-delete)
  - [5.2 Why Use `PATCH` for Soft Deletes?](#52-why-use-patch-for-soft-deletes)
  - [5.3 Database Schema & Repository Query Filtering](#53-database-schema--repository-query-filtering)
  - [5.4 Hibernate Automatic Soft Delete (`@SQLDelete`, `@SQLRestriction`)](#54-hibernate-automatic-soft-delete-sqldelete-sqlrestriction)
- [6. Servlets Complete Masterclass (Lecture 14)](#6-servlets-complete-masterclass-lecture-14)
  - [6.1 Web Server vs Servlet Container (Apache Tomcat)](#61-web-server-vs-servlet-container-apache-tomcat)
  - [6.2 The Servlet API Hierarchy: `Servlet`, `GenericServlet`, `HttpServlet`](#62-the-servlet-api-hierarchy-servlet-genericservlet-httpservlet)
  - [6.3 Servlet Lifecycle: `init()`, `service()`, `destroy()`](#63-servlet-lifecycle-init-service-destroy)
  - [6.4 `HttpServletRequest` & `HttpServletResponse` Mechanics](#64-httpservletrequest--httpservletresponse-mechanics)
- [7. Spring MVC Architecture & Internals (Lecture 15)](#7-spring-mvc-architecture--internals-lecture-15)
  - [7.1 The Front Controller Pattern: `DispatcherServlet`](#71-the-front-controller-pattern-dispatcherservlet)
  - [7.2 The 7-Step Request Processing Pipeline](#72-the-7-step-request-processing-pipeline)
  - [7.3 `HandlerMapping` & `HandlerAdapter`](#73-handlermapping--handleradapter)
  - [7.4 `HttpMessageConverter` & Content Negotiation (Jackson)](#74-httpmessageconverter--content-negotiation-jackson)
- [8. DTOs & Jakarta Bean Validation (Lecture 16)](#8-dtos--jakarta-bean-validation-lecture-16)
  - [8.1 The Risk of Exposing Database Entities](#81-the-risk-of-exposing-database-entities)
  - [8.2 Data Transfer Object (DTO) Pattern: Request vs Response DTOs](#82-data-transfer-object-dto-pattern-request-vs-response-dtos)
  - [8.3 Jakarta Validation Annotations (`@NotNull`, `@NotBlank`, `@Size`, `@Email`)](#83-jakarta-validation-annotations-notnull-notblank-size-email)
  - [8.4 Triggering Validation with `@Valid` & `@Validated`](#84-triggering-validation-with-valid--validated)
- [9. Enterprise Exception Handling (Lecture 17)](#9-enterprise-exception-handling-lecture-17)
  - [9.1 Standardizing Error Responses](#91-standardizing-error-responses)
  - [9.2 Local Handling with `@ExceptionHandler`](#92-local-handling-with-exceptionhandler)
  - [9.3 Global Handling with `@ControllerAdvice` & `@RestControllerAdvice`](#93-global-handling-with-controlleradvice--restcontrolleradvice)
  - [9.4 Handling Validation Exceptions (`MethodArgumentNotValidException`)](#94-handling-validation-exceptions-methodargumentnotvalidexception)
  - [9.5 Modern Spring Standard: RFC 7807 `ProblemDetail`](#95-modern-spring-standard-rfc-7807-problemdetail)
- [10. Advanced Configuration, YAML & Spring Profiles (Lecture 18)](#10-advanced-configuration-yaml--spring-profiles-lecture-18)
  - [10.1 Properties vs YAML Syntax Comparison](#101-properties-vs-yaml-syntax-comparison)
  - [10.2 Environment-Specific Configuration with Profiles (`dev`, `test`, `prod`)](#102-environment-specific-configuration-with-profiles-dev-test-prod)
  - [10.3 Profile-Specific Files & Multi-Document YAML](#103-profile-specific-files--multi-document-yaml)
  - [10.4 Conditional Bean Registration with `@Profile`](#104-conditional-bean-registration-with-profile)
- [11. Servlet Filters in Spring Boot (Lecture 19)](#11-servlet-filters-in-spring-boot-lecture-19)
  - [11.1 What is a Servlet Filter?](#111-what-is-a-servlet-filter)
  - [11.2 Request/Response Lifecycle at the Servlet Boundary](#112-requestresponse-lifecycle-at-the-servlet-boundary)
  - [11.3 Implementing `Filter` & The `FilterChain`](#113-implementing-filter--the-filterchain)
  - [11.4 Common Use Cases: Logging, Request Timing, Header Inspection](#114-common-use-cases-logging-request-timing-header-inspection)
- [12. Advanced Filters: Wrappers & Execution Guarantees (Lecture 20)](#12-advanced-filters-wrappers--execution-guarantees-lecture-20)
  - [12.1 The Stream-Consumed-Once Problem](#121-the-stream-consumed-once-problem)
  - [12.2 Request & Response Wrappers: `ContentCachingRequestWrapper`](#122-request--response-wrappers-contentcachingrequestwrapper)
  - [12.3 Explicit Filter Ordering with `FilterRegistrationBean`](#123-explicit-filter-ordering-with-filterregistrationbean)
  - [12.4 Preventing Duplicate Filtering with `OncePerRequestFilter`](#124-preventing-duplicate-filtering-with-onceperrequestfilter)
- [13. Spring Boot Interceptors vs Filters (Lecture 21)](#13-spring-boot-interceptors-vs-filters-lecture-21)
  - [13.1 What is a Spring MVC Interceptor?](#131-what-is-a-spring-mvc-interceptor)
  - [13.2 The Interceptor Lifecycle: `preHandle`, `postHandle`, `afterCompletion`](#132-the-interceptor-lifecycle-prehandle-posthandle-aftercompletion)
  - [13.3 Registering Interceptors via `WebMvcConfigurer`](#133-registering-interceptors-via-webmvcconfigurer)
  - [13.4 Deep Architectural Comparison: Filter vs Interceptor](#134-deep-architectural-comparison-filter-vs-interceptor)

---

## 1. Spring Boot Auto-Configuration & Starters (Lecture 09)

### 1.1 Why Spring Boot After Spring Core?
In standard Spring Core, configuring a production web application required enormous boilerplate:
- Writing hundreds of lines of XML or Java `@Configuration`.
- Manually declaring `DispatcherServlet`, `InternalResourceViewResolver`, `Jackson2ObjectMapperBuilder`.
- Configuring an external Apache Tomcat server, deploying a WAR archive, and managing version conflicts manually.

**Spring Boot** solves this by adopting **Opinionated Defaults** and **Convention over Configuration**:
1. It preconfigures common libraries automatically.
2. It embeds the web server directly inside your JAR.
3. It bundles compatible dependencies into unified **Starters**.

[⬆ Back to Top](#table-of-contents)

---

### 1.2 Starter Dependencies & `spring-boot-starter-parent`
A **Starter** is a curated descriptor that packages all necessary transitive dependencies for a specific capability under a single coordinate:

```xml
<!-- One starter brings in Tomcat, Spring MVC, Jackson, Validation, and YAML parsers -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

Common Starters:
- `spring-boot-starter-web`: Spring MVC, REST, embedded Tomcat, Jackson.
- `spring-boot-starter-data-jpa`: Hibernate, Spring Data JPA, HikariCP connection pool.
- `spring-boot-starter-security`: Spring Security core, OAuth2, Web security filter chains.
- `spring-boot-starter-test`: JUnit 5, Mockito, AssertJ, Hamcrest, MockMvc.

[⬆ Back to Top](#table-of-contents)

---

### 1.3 Anatomy of `@SpringBootApplication` & Startup Mechanism
The `@SpringBootApplication` annotation placed on your main entry class is composed of three critical annotations:

```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@SpringBootConfiguration      // 1. Tags class as a Spring @Configuration source
@EnableAutoConfiguration       // 2. Activates intelligent auto-configuration
@ComponentScan                 // 3. Scans current package and all sub-packages
public @interface SpringBootApplication { ... }
```

#### What Happens When `SpringApplication.run()` Executes:
1. Starts stopwatch to measure boot time.
2. Creates appropriate `ApplicationContext` (`AnnotationConfigServletWebServerApplicationContext` for web).
3. Reads `application.properties` / `application.yml` and initializes the Spring `Environment`.
4. Executes `@ComponentScan` starting from the root package `com.raysi`.
5. Executes `@EnableAutoConfiguration` evaluating conditional beans.
6. Starts the embedded web server (Apache Tomcat on port 8080).
7. Publishes `ApplicationReadyEvent` and invokes any `CommandLineRunner` / `ApplicationRunner` beans.

[⬆ Back to Top](#table-of-contents)

---

### 1.4 How Auto-Configuration Works (`@ConditionalOnClass`, `@ConditionalOnMissingBean`)
Spring Boot inspects classes present on your classpath via **Auto-Configuration Classes** located in `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`.

It uses **Conditional Annotations** to safely create beans:
```java
@AutoConfiguration
@ConditionalOnClass({ Servlet.class, Tomcat.class }) // Only runs if Tomcat is on classpath
public class EmbeddedWebServerFactoryCustomizerAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean // Only creates bean if developer hasn't declared their own
    public TomcatServletWebServerFactory tomcatServletWebServerFactory() {
        return new TomcatServletWebServerFactory();
    }
}
```

#### Key Conditions:
- `@ConditionalOnClass(X.class)`: Activates only if class `X` is present on the runtime classpath.
- `@ConditionalOnMissingBean(X.class)`: Activates only if the developer has not explicitly defined their own bean of type `X` (enabling seamless custom overrides).
- `@ConditionalOnProperty(name = "feature.enabled", havingValue = "true")`: Activates conditionally based on configuration properties.

[⬆ Back to Top](#table-of-contents)

---

## 2. Configuration Management: Properties, `@Value` & `@ConfigurationProperties` (Lecture 10)

### 2.1 Externalized Configuration Hierarchy
Spring Boot allows externalizing configuration so the exact same application package can run in development, testing, and production environments.

Spring resolves property key collisions using a defined precedence hierarchy (highest priority wins):
1. Command-line arguments (`--server.port=9090`)
2. JVM System Properties (`-Dserver.port=9090`)
3. OS Environment Variables (`SERVER_PORT=9090`)
4. Profile-specific application properties (`application-prod.properties`)
5. Packaged application properties (`application.properties` inside JAR)

[⬆ Back to Top](#table-of-contents)

---

### 2.2 Value Injection with `@Value` & Default Values
Inject individual configuration values using the `@Value` annotation:

```properties
# src/main/resources/application.properties
app.name=Student Management Portal
app.timeout=5000
app.admin.email=admin@raysi.com
```

```java
package com.raysi.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AppMetadata {

    // Simple key lookup
    @Value("${app.name}")
    private String appName;

    // Key lookup with fallback default value if missing
    @Value("${app.timeout:3000}")
    private int timeout;

    // SpEL (Spring Expression Language) evaluation
    @Value("#{systemProperties['user.timezone'] ?: 'UTC'}")
    private String defaultTimezone;
}
```

[⬆ Back to Top](#table-of-contents)

---

### 2.3 Type-Safe Binding with `@ConfigurationProperties`
When managing multiple related configuration settings, `@Value` becomes repetitive and fragile. `@ConfigurationProperties` provides strong typing, hierarchical grouping, and IDE autocomplete.

```properties
server.mail.host=smtp.raysi.com
server.mail.port=587
server.mail.username=mailer@raysi.com
server.mail.auth.enabled=true
```

```java
package com.raysi.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@ConfigurationProperties(prefix = "server.mail")
public class MailProperties {
    private String host;
    private int port;
    private String username;
    private Auth auth = new Auth(); // Nested object

    public static class Auth {
        private boolean enabled;
        // Getters and Setters
        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }
    }

    // Getters and Setters
    public String getHost() { return host; }
    public void setHost(String host) { this.host = host; }
    public int getPort() { return port; }
    public void setPort(int port) { this.port = port; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public Auth getAuth() { return auth; }
    public void setAuth(Auth auth) { this.auth = auth; }
}
```

[⬆ Back to Top](#table-of-contents)

---

### 2.4 Relaxed Binding & Property Validation

#### Relaxed Binding
Spring Boot matches properties regardless of casing convention. All of the following match `mailProperties.maxConnectionCount`:
- `server.mail.max-connection-count` (Kebab-case — Recommended for `.properties` / `.yml`)
- `server.mail.maxConnectionCount` (CamelCase)
- `server.mail.max_connection_count` (Snake_case)
- `SERVER_MAIL_MAXCONNECTIONCOUNT` (Upper snake case — Recommended for OS Environment variables)

#### Property Validation
You can combine `@ConfigurationProperties` with Jakarta Bean Validation:
```java
@Component
@Validated
@ConfigurationProperties(prefix = "app.security")
public class SecurityProperties {
    @NotNull
    private String secretKey;

    @Min(60) @Max(3600)
    private int sessionTimeoutSeconds;
    // Getters and Setters
}
```

[⬆ Back to Top](#table-of-contents)

---

## 3. REST Fundamentals & CRUD Operations (Lecture 11)

### 3.1 Principles of RESTful APIs
**Representational State Transfer (REST)** is an architectural style for designing networked applications:
1. **Resource-Oriented**: Resources are nouns (`/students`), not verbs (`/getStudents` is an anti-pattern).
2. **Stateless**: Every request from client to server must contain all information required to understand and fulfill the request.
3. **Uniform Interface**: Standard HTTP methods perform standard actions.
4. **Self-Descriptive Messages**: Messages carry `Content-Type` headers indicating how to parse the payload (e.g., `application/json`).

[⬆ Back to Top](#table-of-contents)

---

### 3.2 HTTP Verbs & Status Code Mapping

| Action | HTTP Verb | Endpoint Path | Success Status | Idempotent |
| :--- | :--- | :--- | :--- | :--- |
| **Create** | `POST` | `/api/v1/students` | `201 Created` | No |
| **Read All** | `GET` | `/api/v1/students` | `200 OK` | Yes |
| **Read by ID** | `GET` | `/api/v1/students/{id}` | `200 OK` | Yes |
| **Update Full**| `PUT` | `/api/v1/students/{id}` | `200 OK` | Yes |
| **Update Partial**| `PATCH` | `/api/v1/students/{id}` | `200 OK` | No |
| **Delete** | `DELETE`| `/api/v1/students/{id}` | `204 No Content` | Yes |

[⬆ Back to Top](#table-of-contents)

---

### 3.3 Spring Web Annotations: `@RestController`, `@RequestMapping`, `@PathVariable`, `@RequestBody`

```java
package com.raysi.controller;

import com.raysi.model.Student;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/v1/students")
public class StudentController {

    private final Map<Long, Student> studentDb = new HashMap<>();
    private long idSequence = 1L;

    // 1. Create Student
    @PostMapping
    public ResponseEntity<Student> createStudent(@RequestBody Student student) {
        student.setId(idSequence++);
        studentDb.put(student.getId(), student);
        return new ResponseEntity<>(student, HttpStatus.CREATED);
    }

    // 2. Get All Students
    @GetMapping
    public ResponseEntity<List<Student>> getAllStudents() {
        return ResponseEntity.ok(new ArrayList<>(studentDb.values()));
    }

    // 3. Get Student By ID
    @GetMapping("/{id}")
    public ResponseEntity<Student> getStudentById(@PathVariable Long id) {
        Student student = studentDb.get(id);
        if (student == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(student);
    }

    // 4. Update Student
    @PutMapping("/{id}")
    public ResponseEntity<Student> updateStudent(@PathVariable Long id, @RequestBody Student updated) {
        if (!studentDb.containsKey(id)) {
            return ResponseEntity.notFound().build();
        }
        updated.setId(id);
        studentDb.put(id, updated);
        return ResponseEntity.ok(updated);
    }

    // 5. Delete Student
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
        if (!studentDb.containsKey(id)) {
            return ResponseEntity.notFound().build();
        }
        studentDb.remove(id);
        return ResponseEntity.noContent().build();
    }
}
```

[⬆ Back to Top](#table-of-contents)

---

## 4. Layered Architecture: Database, Entity, Service, Controller (Lecture 12)

### 4.1 Why Layered Architecture?
Placing database queries, HTTP parsing, validation, and business logic into a single class creates a "God Class" that is unmaintainable, un-testable, and fragile.

The **Separation of Concerns (SoC)** principle dictates splitting enterprise applications into distinct horizontal layers.

```
Client (Browser / Postman / Mobile)
               │ HTTP Requests
               ▼
┌──────────────────────────────────────┐
│ Presentation Layer (Controller)      │  <-- Routing, HTTP validation, status codes
└──────────────┬───────────────────────┘
               │ DTO / Domain Models
               ▼
┌──────────────────────────────────────┐
│ Business Logic Layer (Service)       │  <-- Calculations, transactions, business rules
└──────────────┬───────────────────────┘
               │ Domain Entities
               ▼
┌──────────────────────────────────────┐
│ Data Access Layer (Repository / DAO) │  <-- DB queries, JPA interfaces, CRUD operations
└──────────────┬───────────────────────┘
               │ SQL Queries / JDBC
               ▼
┌──────────────────────────────────────┐
│ Database (MySQL / PostgreSQL)        │
└──────────────────────────────────────┘
```

[⬆ Back to Top](#table-of-contents)

---

### 4.2 Layer Responsibilities: Controller, Service, Repository, Entity

1. **Entity Layer (`com.raysi.entity.Student`)**:
   Represents a row in the relational database table.
```java
package com.raysi.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "students")
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    // Default constructor for JPA reflection
    public Student() {}

    public Student(String name, String email) {
        this.name = name;
        this.email = email;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
```

2. **Repository Layer (`com.raysi.repository.StudentRepository`)**:
   Provides database abstraction using Spring Data JPA.
```java
package com.raysi.repository;

import com.raysi.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByEmail(String email);
}
```

3. **Service Layer (`com.raysi.service.StudentService`)**:
   Houses core business rules and transaction boundaries.
```java
package com.raysi.service;

import com.raysi.entity.Student;
import com.raysi.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class StudentService {
    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public Student registerStudent(Student student) {
        if (studentRepository.findByEmail(student.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already registered: " + student.getEmail());
        }
        return studentRepository.save(student);
    }

    @Transactional(readOnly = true)
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }
}
```

4. **Controller Layer (`com.raysi.controller.StudentController`)**:
   Exposes HTTP endpoints and coordinates service invocation.

[⬆ Back to Top](#table-of-contents)

---

### 4.3 Configuring Database Connection (`application.properties`)

```properties
# Database URL and Driver
spring.datasource.url=jdbc:mysql://localhost:3306/raysi_school_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=secret123
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# Hibernate DDL and SQL logging
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

[⬆ Back to Top](#table-of-contents)

---

## 5. Soft Delete Architecture (Lecture 13)

### 5.1 Hard Delete vs Soft Delete
- **Hard Delete (`DELETE FROM students WHERE id = 5;`)**:
  - Permanently removes the row from the physical storage engine.
  - *Risks*: Destroys historical audit trails; violates foreign key constraints (`ON DELETE CASCADE` risks accidental mass deletion).
- **Soft Delete (`UPDATE students SET is_deleted = true WHERE id = 5;`)**:
  - Sets a boolean flag (e.g., `is_deleted = true`) or timestamp (`deleted_at = NOW()`).
  - The record is retained for compliance, data recovery, and analytics, but invisible to normal application operations.

[⬆ Back to Top](#table-of-contents)

---

### 5.2 Why Use `PATCH` for Soft Deletes?
While clients may send `DELETE /api/v1/students/5`, under the hood this operation does not delete the row; it performs a **partial state update** (`is_deleted = true`). In internal services or admin dashboards, exposing `PATCH /api/v1/students/5/deactivate` explicitly models the intent.

[⬆ Back to Top](#table-of-contents)

---

### 5.3 Database Schema & Repository Query Filtering

```sql
ALTER TABLE students ADD COLUMN is_deleted BOOLEAN DEFAULT FALSE;
```

#### Filtering Soft-Deleted Rows via JPA Method Names:
```java
package com.raysi.repository;

import com.raysi.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {
    List<Student> findAllByIsDeletedFalse();
    Optional<Student> findByIdAndIsDeletedFalse(Long id);
}
```

[⬆ Back to Top](#table-of-contents)

---

### 5.4 Hibernate Automatic Soft Delete (`@SQLDelete`, `@SQLRestriction`)
Instead of manually appending `WHERE is_deleted = false` to every single query, modern Hibernate allows declaring soft delete rules directly on the Entity:

```java
package com.raysi.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "students")
// Intercepts repository.delete() and executes UPDATE instead of DELETE
@SQLDelete(sql = "UPDATE students SET is_deleted = true WHERE id = ?")
// Automatically appends filter condition to ALL SELECT queries
@SQLRestriction("is_deleted = false")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;

    // Getters and Setters
}
```

[⬆ Back to Top](#table-of-contents)

---

## 6. Servlets Complete Masterclass (Lecture 14)

### 6.1 Web Server vs Servlet Container (Apache Tomcat)
- **Web Server (Nginx / Apache HTTP)**: Serves static files (HTML, CSS, JS, images), performs SSL termination, and handles load balancing. Does not execute Java code.
- **Servlet Container (Apache Tomcat / Eclipse Jetty)**:
  - Listens on TCP sockets (e.g., port 8080).
  - Manages HTTP connection thread pools (`org.apache.tomcat.util.threads.ThreadPoolExecutor`).
  - Translates raw HTTP bytes into `HttpServletRequest` and `HttpServletResponse` objects.
  - Manages the lifecycle of Java Servlets (`init()`, `service()`, `destroy()`).

[⬆ Back to Top](#table-of-contents)

---

### 6.2 The Servlet API Hierarchy: `Servlet`, `GenericServlet`, `HttpServlet`

```
┌──────────────────────────────────────┐
│        jakarta.servlet.Servlet       │  <-- Root interface (init, service, destroy)
└──────────────────┬───────────────────┘
                   │ implements
┌──────────────────▼───────────────────┐
│     jakarta.servlet.GenericServlet   │  <-- Protocol-independent abstract class
└──────────────────┬───────────────────┘
                   │ extends
┌──────────────────▼───────────────────┐
│    jakarta.servlet.http.HttpServlet  │  <-- HTTP-specific (doGet, doPost, doPut, etc.)
└──────────────────┬───────────────────┘
                   │ extends
┌──────────────────▼───────────────────┐
│ Spring Framework's DispatcherServlet │  <-- Front Controller for Spring MVC
└──────────────────────────────────────┘
```

[⬆ Back to Top](#table-of-contents)

---

### 6.3 Servlet Lifecycle: `init()`, `service()`, `destroy()`
1. **Loading & Instantiation**: Tomcat loads the servlet class and instantiates it using default constructor.
2. **Initialization (`init(ServletConfig config)`)**: Executed **once** in the servlet's life to load resources, DB pools, or configuration.
3. **Request Handling (`service(ServletRequest req, ServletResponse res)`)**:
   - Executed on a dedicated worker thread **per incoming request**.
   - Inspects `request.getMethod()` and routes to `doGet()`, `doPost()`, `doPut()`, `doDelete()`.
4. **Destruction (`destroy()`)**: Executed **once** when Tomcat is shutting down or the web application is unloaded.

```java
package com.raysi.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/legacy-hello")
public class TraditionalHelloServlet extends HttpServlet {

    @Override
    public void init() throws ServletException {
        System.out.println("Servlet initialized once!");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        resp.setContentType("text/plain");
        PrintWriter out = resp.getWriter();
        out.println("Handled directly by Tomcat HttpServlet!");
    }

    @Override
    public void destroy() {
        System.out.println("Servlet destroyed on server shutdown!");
    }
}
```

[⬆ Back to Top](#table-of-contents)

---

## 7. Spring MVC Architecture & Internals (Lecture 15)

### 7.1 The Front Controller Pattern: `DispatcherServlet`
Instead of mapping individual Servlets to individual URL paths (`/students` -> `StudentServlet`, `/courses` -> `CourseServlet`), modern web architectures use the **Front Controller Design Pattern**:
- **`DispatcherServlet`** is the single master entry point for **all** HTTP traffic.
- It centralizes common tasks: locale resolution, theme resolution, security checks, and routes the request to appropriate controller handler methods.

[⬆ Back to Top](#table-of-contents)

---

### 7.2 The 7-Step Request Processing Pipeline

```
1. HTTP Request
        │
        ▼
┌────────────────────────────────────────────────────────┐
│ Apache Tomcat Container (Port 8080)                    │
└───────────────────────┬────────────────────────────────┘
                        │
                        ▼
┌────────────────────────────────────────────────────────┐
│ DispatcherServlet (Front Controller)                   │
└───────┬────────────────────────────────────────┬───────┘
        │ 2. Find Handler                        │ 4. Execute Controller
        ▼                                        ▼
┌──────────────────────┐               ┌──────────────────────┐
│    HandlerMapping    │               │    HandlerAdapter    │
│ (RequestMappingInfo) │               │(InvocableHandlerMeth)│
└──────────────────────┘               └──────────┬───────────┘
                                                  │ 5. Invoke Method
                                                  ▼
                                       ┌──────────────────────┐
                                       │ @RestController      │
                                       │ (StudentController)  │
                                       └──────────┬───────────┘
                                                  │ 6. Return Data
                                                  ▼
                                       ┌──────────────────────┐
                                       │ HttpMessageConverter │
                                       │ (MappingJackson2)    │
                                       └──────────┬───────────┘
                                                  │
                                                  ▼
                                       7. HTTP Response (JSON)
```

1. **Step 1**: The client sends an HTTP request. Tomcat receives it and routes it to `DispatcherServlet`.
2. **Step 2**: `DispatcherServlet` queries `HandlerMapping` to find which controller and method corresponds to the requested URL path and HTTP method.
3. **Step 3**: `HandlerMapping` returns a `HandlerExecutionChain` (containing the controller method plus any registered interceptors).
4. **Step 4**: `DispatcherServlet` delegates execution to a `HandlerAdapter` (specifically `RequestMappingHandlerAdapter`).
5. **Step 5**: `HandlerAdapter` resolves method arguments (`@PathVariable`, `@RequestBody`) and invokes the method on the target `@RestController`.
6. **Step 6**: The controller executes business services and returns a Java object.
7. **Step 7**: `HttpMessageConverter` (e.g., `MappingJackson2HttpMessageConverter`) serializes the Java object into a JSON string and writes it to `HttpServletResponse.getOutputStream()`.

[⬆ Back to Top](#table-of-contents)

---

## 8. DTOs & Jakarta Bean Validation (Lecture 16)

### 8.1 The Risk of Exposing Database Entities
Directly exposing JPA Entities to client requests and responses creates severe production issues:
1. **Security Vulnerabilities (Mass Assignment)**: A malicious user can send JSON payload including `"role": "ADMIN"` or `"isVerified": true`, which binds directly into database columns.
2. **Over-Fetching / Leaking Sensitive Data**: Returning an entity serializes internal fields like password hashes, internal DB sequence IDs, and deleted flags.
3. **Tight Coupling**: Any internal schema migration (renaming DB columns) breaks external API clients.
4. **Infinite Recursion**: Bidirectional JPA relationships cause Jackson JSON serialization to throw `JsonMappingException: Infinite recursion (StackOverflowError)`.

[⬆ Back to Top](#table-of-contents)

---

### 8.2 Data Transfer Object (DTO) Pattern: Request vs Response DTOs
Separate external data contracts from internal relational entities:

```
[ HTTP JSON Request ] ──> StudentRequestDTO
                                  │
                                  ▼ (Validation & Mapping)
                             Student Entity (JPA DB)
                                  │
                                  ▼ (Mapping)
[ HTTP JSON Response ] <── StudentResponseDTO
```

```java
package com.raysi.dto;

import jakarta.validation.constraints.*;

// Request DTO with validation rules
public record StudentRequestDTO(
    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
    String name,

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    String email,

    @Min(value = 18, message = "Student must be at least 18 years old")
    int age
) {}
```

```java
package com.raysi.dto;

// Response DTO containing only safe, public fields
public record StudentResponseDTO(
    Long id,
    String name,
    String email
) {}
```

[⬆ Back to Top](#table-of-contents)

---

### 8.3 Jakarta Validation Annotations (`@NotNull`, `@NotBlank`, `@Size`, `@Email`)
Add `spring-boot-starter-validation` to `pom.xml`:

| Annotation | Applied To | Validation Rule |
| :--- | :--- | :--- |
| **`@NotNull`** | Any Type | Value cannot be `null` (empty string `""` is allowed). |
| **`@NotEmpty`**| Strings, Collections, Maps | Value cannot be `null` and `size() > 0` (whitespace `"   "` is allowed). |
| **`@NotBlank`**| Strings | Value cannot be `null`, length > 0, and trimmed length > 0. |
| **`@Size(min, max)`** | Strings, Collections | Length or size must fall within boundaries. |
| **`@Min` / `@Max`** | Numbers | Value must be >= min or <= max. |
| **`@Email`** | Strings | Must match standard RFC email formatting regex. |
| **`@Pattern(regexp)`** | Strings | Must conform to explicit Regular Expression. |

[⬆ Back to Top](#table-of-contents)

---

### 8.4 Triggering Validation with `@Valid` & `@Validated`
Add `@Valid` on the `@RequestBody` parameter in your controller method:

```java
@PostMapping
public ResponseEntity<StudentResponseDTO> createStudent(@Valid @RequestBody StudentRequestDTO dto) {
    StudentResponseDTO response = studentService.createStudent(dto);
    return new ResponseEntity<>(response, HttpStatus.CREATED);
}
```
If validation fails, Spring prevents method execution and immediately throws `MethodArgumentNotValidException`.

[⬆ Back to Top](#table-of-contents)

---

## 9. Enterprise Exception Handling (Lecture 17)

### 9.1 Standardizing Error Responses
When an API encounters an error, clients expect a uniform, structured JSON payload rather than an HTML Tomcat error page or raw stack trace.

```java
package com.raysi.exception;

import java.time.LocalDateTime;
import java.util.Map;

public record ApiErrorResponse(
    int status,
    String error,
    String message,
    String path,
    LocalDateTime timestamp,
    Map<String, String> validationErrors
) {}
```

[⬆ Back to Top](#table-of-contents)

---

### 9.2 Local Handling with `@ExceptionHandler`
`@ExceptionHandler` declared inside a specific controller handles exceptions originating exclusively from that controller:

```java
@ExceptionHandler(ResourceNotFoundException.class)
public ResponseEntity<String> handleNotFound(ResourceNotFoundException ex) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
}
```

[⬆ Back to Top](#table-of-contents)

---

### 9.3 Global Handling with `@ControllerAdvice` & `@RestControllerAdvice`
To handle exceptions globally across all controllers in the application, annotate a dedicated class with `@RestControllerAdvice`:

```java
package com.raysi.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 1. Handle Resource Not Found
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleResourceNotFound(
            ResourceNotFoundException ex, HttpServletRequest request) {
        ApiErrorResponse error = new ApiErrorResponse(
            HttpStatus.NOT_FOUND.value(),
            HttpStatus.NOT_FOUND.getReasonPhrase(),
            ex.getMessage(),
            request.getRequestURI(),
            LocalDateTime.now(),
            null
        );
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    // 2. Handle Validation Errors (@Valid failures)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationExceptions(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        ApiErrorResponse error = new ApiErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            "Validation Failed",
            "Input payload validation failed",
            request.getRequestURI(),
            LocalDateTime.now(),
            errors
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    // 3. Fallback for unhandled server exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGeneralException(
            Exception ex, HttpServletRequest request) {
        ApiErrorResponse error = new ApiErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
            "An unexpected internal error occurred.",
            request.getRequestURI(),
            LocalDateTime.now(),
            null
        );
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
```

[⬆ Back to Top](#table-of-contents)

---

### 9.5 Modern Spring Standard: RFC 7807 `ProblemDetail`
Spring 6 and Spring Boot 3 introduce native support for the IETF standard **RFC 7807 (Problem Details for HTTP APIs)**:

```java
@ExceptionHandler(ResourceNotFoundException.class)
public ProblemDetail handleProblemDetail(ResourceNotFoundException ex) {
    ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
    problem.setTitle("Resource Not Found");
    problem.setProperty("timestamp", LocalDateTime.now());
    return problem;
}
```
Produces standard JSON:
```json
{
  "type": "about:blank",
  "title": "Resource Not Found",
  "status": 404,
  "detail": "Student not found with ID: 42",
  "instance": "/api/v1/students/42",
  "timestamp": "2026-10-29T12:00:00"
}
```

[⬆ Back to Top](#table-of-contents)

---

## 10. Advanced Configuration, YAML & Spring Profiles (Lecture 18)

### 10.1 Properties vs YAML Syntax Comparison

#### `application.properties`:
```properties
server.port=8080
spring.datasource.url=jdbc:mysql://localhost:3306/raysi_db
spring.datasource.username=root
spring.datasource.hikari.maximum-pool-size=20
```

#### `application.yml`:
```yaml
server:
  port: 8080
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/raysi_db
    username: root
    hikari:
      maximum-pool-size: 20
```
**YAML Advantages**: Hierarchical nesting eliminates key repetition, supports ordered arrays/lists natively, and supports multi-profile documents.

[⬆ Back to Top](#table-of-contents)

---

### 10.2 Environment-Specific Configuration with Profiles (`dev`, `test`, `prod`)
Spring Profiles allow segregating parts of application configuration and making them available only in designated runtime environments.

#### Profile File Naming Convention:
- `application.yml` (Base configuration applied everywhere)
- `application-dev.yml` (Activated in development)
- `application-prod.yml` (Activated in production)

#### Activating a Profile:
1. In `application.yml`:
   ```yaml
   spring:
     profiles:
       active: dev
   ```
2. Via Command-Line Argument:
   ```bash
   java -jar app.jar --spring.profiles.active=prod
   ```
3. Via OS Environment Variable:
   ```bash
   export SPRING_PROFILES_ACTIVE=prod
   ```

[⬆ Back to Top](#table-of-contents)

---

### 10.4 Conditional Bean Registration with `@Profile`
Beans can be marked to exist in the `ApplicationContext` only when specific profiles are active:

```java
package com.raysi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class NotificationConfig {

    @Bean
    @Profile("dev")
    public NotificationSender mockSender() {
        return message -> System.out.println("[DEV LOG ONLY] Notification: " + message);
    }

    @Bean
    @Profile("prod")
    public NotificationSender awsSnsSender() {
        return new AwsSnsNotificationSender();
    }
}
```

[⬆ Back to Top](#table-of-contents)

---

## 11. Servlet Filters in Spring Boot (Lecture 19)

### 11.1 What is a Servlet Filter?
A **Servlet Filter** is a component defined by the Java Servlet Specification (`jakarta.servlet.Filter`) that intercepts requests **before** they reach the `DispatcherServlet`, and intercepts responses **after** they leave the servlet.

```
Client Request
      │
      ▼
┌──────────────────────────────────────────────┐
│ Tomcat Servlet Container                     │
│                                              │
│  ┌──────────────┐                            │
│  │   Filter A   │ (Logging / Timing)         │
│  └──────┬───────┘                            │
│         ▼                                    │
│  ┌──────────────┐                            │
│  │   Filter B   │ (Security / Auth / Token)  │
│  └──────┬───────┘                            │
│         ▼                                    │
│  ┌────────────────────────────────────────┐  │
│  │ Spring MVC DispatcherServlet           │  │
│  │     (HandlerMapping -> Controller)     │  │
│  └────────────────────────────────────────┘  │
└──────────────────────────────────────────────┘
```

[⬆ Back to Top](#table-of-contents)

---

### 11.3 Implementing `Filter` & The `FilterChain`
Every filter implements `jakarta.servlet.Filter`:

```java
package com.raysi.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import java.io.IOException;

@Component
public class RequestTimingFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        long startTime = System.currentTimeMillis();

        try {
            // Pass request to the next filter in the chain (or DispatcherServlet)
            chain.doFilter(request, response);
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            System.out.println("URI: " + httpRequest.getRequestURI() + " executed in " + duration + " ms");
        }
    }
}
```
> [!IMPORTANT]
> If you omit `chain.doFilter(request, response)`, the filter chain terminates immediately, blocking the request from ever reaching your Spring controller!

[⬆ Back to Top](#table-of-contents)

---

## 12. Advanced Filters: Wrappers & Execution Guarantees (Lecture 20)

### 12.1 The Stream-Consumed-Once Problem
In Java Servlets, `HttpServletRequest.getInputStream()` is a one-time read stream. Once a filter reads the request body (e.g., to log JSON payload), the stream is exhausted. When `DispatcherServlet` later attempts to deserialize the body into a `@RequestBody` DTO, it throws:
`HttpMediaTypeNotSupportedException` or `HttpMessageNotReadableException: Required request body is missing`.

[⬆ Back to Top](#table-of-contents)

---

### 12.2 Request & Response Wrappers: `ContentCachingRequestWrapper`
Spring provides specialized wrappers that buffer bytes in memory, allowing payload to be read repeatedly:
- `ContentCachingRequestWrapper`: Buffers request input stream.
- `ContentCachingResponseWrapper`: Buffers response output stream so it can be inspected and logged before being copied back to the client.

```java
package com.raysi.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
public class PayloadLoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);

        try {
            filterChain.doFilter(wrappedRequest, wrappedResponse);
        } finally {
            byte[] requestBody = wrappedRequest.getContentAsByteArray();
            byte[] responseBody = wrappedResponse.getContentAsByteArray();

            System.out.println("Request Payload: " + new String(requestBody, StandardCharsets.UTF_8));
            System.out.println("Response Payload: " + new String(responseBody, StandardCharsets.UTF_8));

            // CRITICAL: Copy cached body back to the actual HTTP response stream!
            wrappedResponse.copyBodyToResponse();
        }
    }
}
```

[⬆ Back to Top](#table-of-contents)

---

### 12.3 Explicit Filter Ordering with `FilterRegistrationBean`
To control the exact execution order and URL mappings of filters, register them via `FilterRegistrationBean` rather than `@Component`:

```java
package com.raysi.config;

import com.raysi.filter.RequestTimingFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<RequestTimingFilter> timingFilter() {
        FilterRegistrationBean<RequestTimingFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new RequestTimingFilter());
        registration.addUrlPatterns("/api/*"); // Only applies to /api paths
        registration.setOrder(1);              // Lowest integer runs first
        return registration;
    }
}
```

[⬆ Back to Top](#table-of-contents)

---

## 13. Spring Boot Interceptors vs Filters (Lecture 21)

### 13.1 What is a Spring MVC Interceptor?
A **HandlerInterceptor** belongs to the Spring MVC framework, not the Servlet container. It operates inside `DispatcherServlet`, giving it direct access to the target Java Controller class, method metadata, and `ModelAndView`.

```
DispatcherServlet
       │
       ▼
┌─────────────────────────┐
│ Interceptor.preHandle() │ ──> Returns false? Terminate request immediately
└────────────┬────────────┘
             │ Returns true
             ▼
┌─────────────────────────┐
│   Controller Method     │
└────────────┬────────────┘
             │
             ▼
┌─────────────────────────┐
│ Interceptor.postHandle()│ ──> Inspect/modify Model & View before rendering
└────────────┬────────────┘
             │
             ▼
┌─────────────────────────────┐
│Interceptor.afterCompletion()│ ──> Executed always (cleanup, metrics, exception audit)
└─────────────────────────────┘
```

[⬆ Back to Top](#table-of-contents)

---

### 13.2 The Interceptor Lifecycle: `preHandle`, `postHandle`, `afterCompletion`

```java
package com.raysi.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Component
public class AuditLogInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (handler instanceof HandlerMethod handlerMethod) {
            System.out.println("Executing Controller: " + handlerMethod.getBeanType().getSimpleName()
                    + " -> Method: " + handlerMethod.getMethod().getName());
        }
        return true; // continue execution chain
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, 
                           Object handler, ModelAndView modelAndView) {
        // Runs after controller returns, before response is committed
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, 
                                Object handler, Exception ex) {
        if (ex != null) {
            System.out.println("Request failed with exception: " + ex.getMessage());
        }
        System.out.println("Request completed successfully.");
    }
}
```

[⬆ Back to Top](#table-of-contents)

---

### 13.3 Registering Interceptors via `WebMvcConfigurer`

```java
package com.raysi.config;

import com.raysi.interceptor.AuditLogInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final AuditLogInterceptor auditLogInterceptor;

    public WebMvcConfig(AuditLogInterceptor auditLogInterceptor) {
        this.auditLogInterceptor = auditLogInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(auditLogInterceptor)
                .addPathPatterns("/api/v1/**")
                .excludePathPatterns("/api/v1/auth/**", "/public/**");
    }
}
```

[⬆ Back to Top](#table-of-contents)

---

### 13.4 Deep Architectural Comparison: Filter vs Interceptor

| Feature | Servlet Filter | Spring MVC Interceptor |
| :--- | :--- | :--- |
| **Origin / Spec** | Java Servlet Specification (`jakarta.servlet.Filter`) | Spring Framework (`HandlerInterceptor`) |
| **Execution Point**| Outside `DispatcherServlet` at Tomcat container boundary | Inside `DispatcherServlet` right before/after Controller |
| **Spring Context Access** | Limited (unless defined as a Spring Bean) | Complete access to Spring ApplicationContext |
| **Controller Access** | **No knowledge** of which Controller or method handles request | Has direct reference to `HandlerMethod` and method annotations |
| **Request/Response Alteration** | Can wrap and alter `HttpServletRequest` and `Response` streams | Cannot wrap or replace request/response streams |
| **Ideal For** | Request stream buffering, CORS, GZIP compression, IP blocking | Business metrics, controller auditing, fine-grained RBAC checks |

[⬆ Back to Top](#table-of-contents)
