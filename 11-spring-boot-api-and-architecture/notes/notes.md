# Spring Boot CRUD Operations & Architecture

**Author:** raysi  
**Topic:** Spring Boot REST APIs, Layered Architecture, JPA, & MySQL Setup  

---

## Introduction & Table of Contents

This document provides a comprehensive guide to building CRUD (Create, Read, Update, Delete) RESTful applications using Spring Boot, Spring Data JPA, and MySQL. It details system architecture, layered separation of concerns, entity mapping, auto-configuration fundamentals, and step-by-step database setup.

### Quick Navigation

- [1. What Exactly Is a CRUD Application?](#1-what-exactly-is-a-crud-application)
- [2. CRUD Mapping with HTTP Methods](#2-crud-mapping-with-http-methods)
- [3. What Features Will We Build? (Student Management API)](#3-what-features-will-we-build-student-management-api)
  - [3.1 Create Student (POST /api/students/)](#31-create-student-post-apistudents)
  - [3.2 Get All Students (GET /api/students/)](#32-get-all-students-get-apistudents)
  - [3.3 Get Student by ID (GET /api/students/{id})](#33-get-student-by-id-get-apistudentsid)
  - [3.4 Update Student (PUT /api/students/{id})](#34-update-student-put-apistudentsid)
  - [3.5 Delete Student (DELETE /api/students/{id})](#35-delete-student-delete-apistudentsid)
- [4. Database Table Schema & Entity Representation](#4-database-table-schema--entity-representation)
- [5. Why Do We Need Architecture?](#5-why-do-we-need-architecture)
- [6. The Final Layered Architecture](#6-the-final-layered-architecture)
  - [Step 1: Client / Postman Request](#step-1-client--postman-request)
  - [Step 2: Controller Layer](#step-2-controller-layer)
  - [Step 3: Service Layer (Business Logic)](#step-3-service-layer-business-logic)
  - [Step 4: Repository Layer (Data Access)](#step-4-repository-layer-data-access)
  - [Step 5: Database Layer (Persistence)](#step-5-database-layer-persistence)
- [7. Layer-by-Layer Responsibility Matrix](#7-layer-by-layer-responsibility-matrix)
- [8. Where Does Entity / Model Fit In?](#8-where-does-entity--model-fit-in)
- [9. Project Setup & Essential Dependencies](#9-project-setup--essential-dependencies)
  - [Why Spring Web?](#why-spring-web)
  - [Why Spring Data JPA?](#why-spring-data-jpa)
  - [Why MySQL Driver?](#why-mysql-driver)
- [10. First App Run & Common DataSource Configuration Error](#10-first-app-run--common-datasource-configuration-error)
- [11. Why Auto-Configuration Triggered the Failure](#11-why-auto-configuration-triggered-the-failure)
- [12. Recommended Package & Directory Structure](#12-recommended-package--directory-structure)
- [13. Main Application Class](#13-main-application-class)
- [14. Entity Package & Model Definition](#14-entity-package--model-definition)
- [15. Why MySQL & What Are We Creating?](#15-why-mysql--what-are-we-creating)
- [16. Setting Up MySQL: Server, Client, and GUI](#16-setting-up-mysql-server-client-and-gui)
- [17. Part A: Installing MySQL on macOS](#17-part-a-installing-mysql-on-macos)
- [18. Part B: Installing MySQL on Windows](#18-part-b-installing-mysql-on-windows)
- [19. Connecting and Testing MySQL Server](#19-connecting-and-testing-mysql-server)

---

## 1. What Exactly Is a CRUD Application?

At its core, almost every backend software system performs four fundamental operations on data:
- **C**reate data
- **R**ead data
- **U**pdate data
- **D**elete data

CRUD is not merely beginner content—it forms the architectural backbone of production backend systems.

### Real-World Examples:
1. **YouTube Video System:**
   - *Create:* Upload video
   - *Read:* Watch / retrieve video details
   - *Update:* Edit title, description, thumbnail
   - *Delete:* Remove video
2. **Instagram Post System:**
   - *Create:* Upload photo / reel
   - *Read:* View post in feed
   - *Update:* Edit caption / location
   - *Delete:* Delete post

[Back to Top](#quick-navigation)

---

## 2. CRUD Mapping with HTTP Methods

In RESTful API design, CRUD operations map standard HTTP verbs to resource endpoints:

| CRUD Operation | HTTP Verb | Example URL | Purpose |
| :--- | :--- | :--- | :--- |
| **Create** | `POST` | `/api/students/` | Add a new student record |
| **Read All** | `GET` | `/api/students/` | Fetch all active students |
| **Read One** | `GET` | `/api/students/{id}` | Fetch a single student by identifier |
| **Update** | `PUT` | `/api/students/{id}` | Replace/update complete student record |
| **Delete** | `DELETE` | `/api/students/{id}` | Remove a student record |

[Back to Top](#quick-navigation)

---

## 3. What Features Will We Build? (Student Management API)

We will implement five primary REST endpoints for the Student Management API.

### 3.1 Create Student (POST /api/students/)
- **Endpoint:** `POST /api/students/`
- **Request Body (JSON):**
  ```json
  {
    "name": "Rahul Sharma",
    "email": "rahul@example.com",
    "course": "Spring Boot",
    "age": 22,
    "rollNo": 101
  }
  ```
- **Action:** Inserts a new student record into the MySQL database.

### 3.2 Get All Students (GET /api/students/)
- **Endpoint:** `GET /api/students/`
- **Action:** Returns a list of all active students from the database.

### 3.3 Get Student by ID (GET /api/students/{id})
- **Endpoint:** `GET /api/students/1`
- **Key Annotation:** `@PathVariable` (extracts the dynamic ID value directly from the URL path).
- **Action:** Returns details of the student with primary key `id = 1`.

### 3.4 Update Student (PUT /api/students/{id})
- **Endpoint:** `PUT /api/students/1`
- **Request Body (JSON):**
  ```json
  {
    "name": "Rahul Sharma",
    "email": "rahul.updated@example.com",
    "course": "Spring Data JPA",
    "age": 23,
    "rollNo": 101
  }
  ```
- **Backend Workflow:**
  1. Find student entity by `id`.
  2. Modify target attributes with request values.
  3. Save the modified entity back to the database.

### 3.5 Delete Student (DELETE /api/students/{id})
- **Endpoint:** `DELETE /api/students/1`
- **Two Delete Strategies:**
  - **Hard Delete:** Permanently deletes the row using SQL `DELETE`.
  - **Soft Delete:** Retains the row in database while marking a flag: `isDeleted = true`.

[Back to Top](#quick-navigation)

---

## 4. Database Table Schema & Entity Representation

The database table `students` is defined as follows:

| Column Name | SQL Type | Description |
| :--- | :--- | :--- |
| `id` | `BIGINT` | Primary Key, Auto-incremented unique ID |
| `name` | `VARCHAR` | Student's full name |
| `email` | `VARCHAR` | Student's email address |
| `course` | `VARCHAR` | Enrolled course |
| `age` | `INT` | Age of student |
| `roll_no` | `INT` | Student's roll number |

### Java Entity Equivalent:
```java
package com.raysi.studentcrud.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "students")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;
    private int rollNo;
    private int age;
    private String subject;
    private boolean isDeleted;

    // Constructors, Getters, and Setters
}
```

JPA bridges the object-oriented Java domain with the relational database schema automatically.

[Back to Top](#quick-navigation)

---

## 5. Why Do We Need Architecture?

Consider an anti-pattern where all duties are written into a single monolithic class:

```java
// Anti-Pattern: Fat Controller Doing Everything
@RestController
public class StudentController {
    // 1. Receive HTTP request
    // 2. Validate input parameters
    // 3. Execute business calculations
    // 4. Open SQL connection
    // 5. Run queries against MySQL
    // 6. Format HTTP response
}
```

### Why This Fails:
- **Tight Coupling:** Impossible to test business rules independently of HTTP requests.
- **Violation of Single Responsibility Principle (SRP):** Changes in SQL schemas break controller endpoints.
- **Unmaintainable:** Code quickly devolves into spaghetti as requirements expand.

To solve this, professional systems use **Layered Architecture**.

[Back to Top](#quick-navigation)

---

## 6. The Final Layered Architecture

```
  [ Client (Postman / Web App) ]
               │
               ▼
      [ Controller Layer ]      <--- HTTP handling, request/response parsing
               │
               ▼
       [ Service Layer ]        <--- Business logic, validations, orchestration
               │
               ▼
      [ Repository Layer ]      <--- Spring Data JPA data access abstraction
               │
               ▼
      [ Database (MySQL) ]      <--- Physical persistence
```

### Step 1: Client / Postman Request
The client (React, Angular, mobile client, or Postman) initiates an HTTP request carrying headers and payloads.

### Step 2: Controller Layer
The entry gate to the application.
- Decodes HTTP verbs, URL paths, query parameters, and JSON bodies.
- Delegates business logic execution directly to the Service layer.
- Serializes responses back to HTTP status codes and JSON.

### Step 3: Service Layer (Business Logic)
Houses the business rules:
- Validating if a student is eligible for registration.
- Filtering out soft-deleted records.
- Ensuring transactional consistency across multiple operations.

### Step 4: Repository Layer (Data Access)
Connects the Service layer to the physical storage:
- Uses Spring Data JPA interfaces without writing manual boilerplate SQL queries.
- Pre-built operations: `save()`, `findAll()`, `findById()`, `deleteById()`.

### Step 5: Database Layer (Persistence)
MySQL Server processes SQL statements, applies constraint checks, and commits data into storage.

```
Database ──> Repository ──> Service ──> Controller ──> Client / Postman
```

[Back to Top](#quick-navigation)

---

## 7. Layer-by-Layer Responsibility Matrix

| Layer | Primary Responsibility | Component Example |
| :--- | :--- | :--- |
| **Postman / Client** | Dispatches HTTP requests | `POST /api/students` |
| **Controller** | Handles HTTP requests, parameter routing | `StudentController` |
| **Service** | Implements core business logic & rules | `StudentService` |
| **Repository** | Performs database operations via JPA | `StudentRepository` |
| **Database** | Persists physical rows | MySQL `students` table |
| **Entity / Model** | Maps relational table rows to Java POJOs | `Student` class |

[Back to Top](#quick-navigation)

---

## 8. Where Does Entity / Model Fit In?

The Entity is not a processing layer in the call stack; it is the **data carrier** that travels across layers:

```
[Controller]  <── Student Object ──>  [Service]  <── Student Object ──>  [Repository]
                                                                               │
                                                                   Mapped by JPA / Hibernate
                                                                               ▼
                                                                     [MySQL: students table]
```

- Controller receives JSON and deserializes it into a `Student` instance.
- Service verifies business constraints on the `Student` object.
- Repository transfers the `Student` instance into the database context.
- Hibernate generates and issues the relevant SQL statements.

[Back to Top](#quick-navigation)

---

## 9. Project Setup & Essential Dependencies

To build this REST CRUD application, declare three core dependencies in `pom.xml`:

```xml
<dependencies>
    <!-- 1. Spring Web: REST APIs & Embedded Tomcat -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>

    <!-- 2. Spring Data JPA: Hibernate & Repository abstractions -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>

    <!-- 3. MySQL Driver: JDBC communication bridge -->
    <dependency>
        <groupId>com.mysql</groupId>
        <artifactId>mysql-connector-j</artifactId>
        <scope>runtime</scope>
    </dependency>
</dependencies>
```

### Why Spring Web?
Provides REST API support, Jackson JSON serialization, embedded Tomcat, and endpoint annotations:
- `@RestController`, `@RequestMapping`
- `@GetMapping`, `@PostMapping`, `@PutMapping`, `@DeleteMapping`, `@PatchMapping`

### Why Spring Data JPA?
Abstracts persistence logic by eliminating repetitive boilerplate JDBC code (`PreparedStatement`, `ResultSet`, connection pools).

### Why MySQL Driver?
Enables Java database connectivity (JDBC) to transmit SQL queries across TCP/IP to MySQL Server.

[Back to Top](#quick-navigation)

---

## 10. First App Run & Common DataSource Configuration Error

Running an empty Spring Boot application immediately after adding JPA and MySQL dependencies results in startup failure:

```text
***************************
APPLICATION FAILED TO START
***************************

Description:
Failed to configure a DataSource: 'url' attribute is not specified and no embedded datasource could be configured.

Reason: Failed to determine a suitable driver class
```

[Back to Top](#quick-navigation)

---

## 11. Why Auto-Configuration Triggered the Failure

Spring Boot auto-configuration operates deterministically based on classes present on the classpath:

```
spring-boot-starter-data-jpa + mysql-connector-j on classpath
                           │
                           ▼
Spring Boot assumes: "This application requires a relational database connection"
                           │
                           ▼
Attempts to configure DataSource, EntityManagerFactory, and TransactionManager
                           │
                           ▼
Searches for connection properties in application.properties:
  - spring.datasource.url
  - spring.datasource.username
  - spring.datasource.password
                           │
                           ▼
Properties missing -> Application fails fast before starting Tomcat
```

### Resolution: Configure `application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/student_crud_db?useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=yourpassword
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# Hibernate Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

[Back to Top](#quick-navigation)

---

## 12. Recommended Package & Directory Structure

To maintain modularity as the project scales, organize code under the `com.raysi.studentcrud` root:

```text
com.raysi.studentcrud
 ├── StudentcrudApplication.java     <-- Root entry point
 ├── entity
 │    └── Student.java               <-- JPA Entity
 ├── repository
 │    └── StudentRepository.java     <-- Data JPA Interface
 ├── service
 │    ├── StudentService.java        <-- Business Logic Interface
 │    └── impl
 │         └── StudentServiceImpl.java
 └── controller
      └── StudentController.java     <-- REST API Endpoints
```

[Back to Top](#quick-navigation)

---

## 13. Main Application Class

The bootstrap entry point must reside at the root package so its implicit `@ComponentScan` registers all subordinate packages:

```java
package com.raysi.studentcrud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class StudentcrudApplication {

    public static void main(String[] args) {
        SpringApplication.run(StudentcrudApplication.class, args);
    }
}
```

[Back to Top](#quick-navigation)

---

## 14. Entity Package & Model Definition

```java
package com.raysi.studentcrud.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "students")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;
    private int rollNo;
    private int age;
    private String subject;
    private boolean isDeleted;

    public Student() {
    }

    public Student(String name, String email, int rollNo, int age, String subject) {
        this.name = name;
        this.email = email;
        this.rollNo = rollNo;
        this.age = age;
        this.subject = subject;
        this.isDeleted = false;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public int getRollNo() { return rollNo; }
    public void setRollNo(int rollNo) { this.rollNo = rollNo; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public boolean isDeleted() { return isDeleted; }
    public void setDeleted(boolean deleted) { isDeleted = deleted; }
}
```

[Back to Top](#quick-navigation)

---

## 15. Why MySQL & What Are We Creating?

### Why MySQL?
- High adoption rate across enterprise architectures.
- Easy to inspect and query via CLI or graphical tools.
- Excellent compatibility with Spring Boot and Hibernate.

### What We Are Creating:
1. **Database Schema:** `student_crud_db`
2. **Database Table:** `students` (created and updated automatically by Hibernate via `ddl-auto=update`).

We only need to manually initialize the database instance in MySQL:
```sql
CREATE DATABASE student_crud_db;
```
The table and columns are mapped and managed automatically by Spring Data JPA.

[Back to Top](#quick-navigation)

---

## 16. Setting Up MySQL: Server, Client, and GUI

Key terminology:
- **MySQL Server:** The background engine storing and processing relational data.
- **MySQL Client:** Terminal CLI tool (`mysql`) used to execute queries directly.
- **MySQL GUI Tool:** Visual interface for managing databases:
  - **MySQL Workbench** (Official MySQL tool)
  - **Sequel Ace** (Lightweight native macOS client)
  - **DBeaver** (Cross-platform database tool)

[Back to Top](#quick-navigation)

---

## 17. Part A: Installing MySQL on macOS

### 1. Install via Homebrew:
```bash
brew install mysql
```

### 2. Start MySQL Service:
```bash
brew services start mysql
```

### 3. Verify Service Status:
```bash
brew services list
mysql --version
```

### 4. Run Security Installation & Set Root Password:
```bash
mysql_secure_installation
```
- Set root password? -> **Yes**
- Enter secure password.
- Remove anonymous users? -> **Yes**
- Disallow root login remotely? -> **Yes**
- Remove test database? -> **Yes**
- Reload privilege tables? -> **Yes**

### 5. Test Terminal Login:
```bash
mysql -u root -p
```

### Useful macOS CLI Commands:
```bash
brew services start mysql    # Start server
brew services stop mysql     # Stop server
brew services restart mysql  # Restart server
mysql -u root -p             # Connect via CLI
exit;                        # Disconnect
```

[Back to Top](#quick-navigation)

---

## 18. Part B: Installing MySQL on Windows

### Step 1: Download MySQL Installer
Download the official MSI installer from `dev.mysql.com`. Select **Developer Default** to install Server, Workbench, and CLI tools.

### Step 2: Configuration Wizard
- **Config Type:** Development Computer
- **Port:** `3306` (Default TCP/IP)
- **Authentication Method:** Strong Password Encryption
- **Username:** `root`
- Set and confirm your root password.
- **Windows Service:** Check *Standard System Account* and *Start at System Startup*.

### Step 3: Service Control Commands
Open Command Prompt / PowerShell as Administrator:
```powershell
Get-Service *mysql*          # Verify service name (e.g. MySQL80)
net start MySQL80            # Start service
net stop MySQL80             # Stop service
```

### Step 4: Login via Windows Terminal
```cmd
mysql -u root -p
```
Enter your configured root password to verify the prompt:
```sql
mysql>
```

[Back to Top](#quick-navigation)

---

## 19. Connecting and Testing MySQL Server

Once connected via CLI or Workbench:

```sql
-- 1. Create the application database
CREATE DATABASE student_crud_db;

-- 2. Verify database creation
SHOW DATABASES;

-- 3. Select database
USE student_crud_db;
```

When the Spring Boot application boots with `spring.jpa.hibernate.ddl-auto=update`, verify that Hibernate has created the table:
```sql
SHOW TABLES;
DESCRIBE students;
```

[Back to Top](#quick-navigation)

---
*Document prepared for raysi.*

