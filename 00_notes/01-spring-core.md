# Module 01: Spring Core & Foundations

---

<a id="table-of-contents"></a>
## 📑 Table of Contents
- [1. Web Fundamentals & Client-Server Architecture (Lecture 01)](#1-web-fundamentals--client-server-architecture-lecture-01)
  - [1.1 Client-Server Communication & HTTP](#11-client-server-communication--http)
  - [1.2 Anatomy of an HTTP Request & Response](#12-anatomy-of-an-http-request--response)
  - [1.3 Core Java Limitations & The Evolution of Servlets to Spring](#13-core-java-limitations--the-evolution-of-servlets-to-spring)
- [2. First Spring Boot Application & Architecture (Lecture 02)](#2-first-spring-boot-application--architecture-lecture-02)
  - [2.1 Ports, Localhost & Socket Basics](#21-ports-localhost--socket-basics)
  - [2.2 Spring Initializr & Boot Project Layout](#22-spring-initializr--boot-project-layout)
  - [2.3 Controllers, `@RestController`, and First API Endpoint](#23-controllers-restcontroller-and-first-api-endpoint)
- [3. Maven Deep Dive & Build Automation (Lecture 03)](#3-maven-deep-dive--build-automation-lecture-03)
  - [3.1 Compilation, Classpath, and JAR Files](#31-compilation-classpath-and-jar-files)
  - [3.2 Anatomy of `pom.xml` & GAV Coordinates](#32-anatomy-of-pomxml--gav-coordinates)
  - [3.3 Repositories, Transitive Dependencies, and Maven Lifecycle](#33-repositories-transitive-dependencies-and-maven-lifecycle)
- [4. Inversion of Control (IoC) & Dependency Injection (Lecture 04)](#4-inversion-of-control-ioc--dependency-injection-lecture-04)
  - [4.1 The Tight Coupling Anti-Pattern](#41-the-tight-coupling-anti-pattern)
  - [4.2 Decoupling Through Interfaces](#42-decoupling-through-interfaces)
  - [4.3 Dependency Injection (DI) & Inversion of Control (IoC)](#43-dependency-injection-di--inversion-of-control-ioc)
  - [4.4 Injection Strategies: Constructor vs Setter vs Field](#44-injection-strategies-constructor-vs-setter-vs-field)
- [5. Spring IoC Container, Beans & Annotation Configuration (Lecture 05)](#5-spring-ioc-container-beans--annotation-configuration-lecture-05)
  - [5.1 What is a Spring Bean?](#51-what-is-a-spring-bean)
  - [5.2 `ApplicationContext` vs `BeanFactory`](#52-applicationcontext-vs-beanfactory)
  - [5.3 Annotation Configuration: `@Configuration`, `@ComponentScan`, `@Component`](#53-annotation-configuration-configuration-componentscan-component)
  - [5.4 Resolving Ambiguities: `@Autowired`, `@Qualifier`, and `@Primary`](#54-resolving-ambiguities-autowired-qualifier-and-primary)
  - [5.5 `@Bean` vs `@Component`](#55-bean-vs-component)
- [6. Circular Dependencies, Bean Scopes & Initialization (Lecture 06)](#6-circular-dependencies-bean-scopes--initialization-lecture-06)
  - [6.1 Circular Dependency: Root Cause & Mechanics](#61-circular-dependency-root-cause--mechanics)
  - [6.2 Constructor Injection vs Setter/Field Injection in Circular Loops](#62-constructor-injection-vs-setterfield-injection-in-circular-loops)
  - [6.3 Resolving Circular Dependencies & Architectural Smells](#63-resolving-circular-dependencies--architectural-smells)
  - [6.4 Spring Bean Scopes: Singleton, Prototype, Web Scopes](#64-spring-bean-scopes-singleton-prototype-web-scopes)
  - [6.5 Bean Initialization Strategies: Eager vs Lazy (`@Lazy`)](#65-bean-initialization-strategies-eager-vs-lazy-lazy)
- [7. Spring Bean Lifecycle (Lecture 07)](#7-spring-bean-lifecycle-lecture-07)
  - [7.1 Complete Bean Lifecycle Sequence](#71-complete-bean-lifecycle-sequence)
  - [7.2 Aware Interfaces](#72-aware-interfaces)
  - [7.3 Initialization Callbacks: `@PostConstruct`, `InitializingBean`, Custom `initMethod`](#73-initialization-callbacks-postconstruct-initializingbean-custom-initmethod)
  - [7.4 Destruction Callbacks: `@PreDestroy`, `DisposableBean`, Custom `destroyMethod`](#74-destruction-callbacks-predestroy-disposablebean-custom-destroymethod)
  - [7.5 The Role of `BeanPostProcessor` & Proxy Creation](#75-the-role-of-beanpostprocessor--proxy-creation)
- [8. Spring XML-Based Configuration (Lecture 08)](#8-spring-xml-based-configuration-lecture-08)
  - [8.1 Understanding `beans.xml` Metadata](#81-understanding-beansxml-metadata)
  - [8.2 Declaring Beans, Setter Injection (`<property>`), and Constructor Injection (`<constructor-arg>`)](#82-declaring-beans-setter-injection-property-and-constructor-injection-constructor-arg)
  - [8.3 Hybrid Configuration: XML Component Scanning](#83-hybrid-configuration-xml-component-scanning)
  - [8.4 Comparison: XML Configuration vs Java Annotations](#84-comparison-xml-configuration-vs-java-annotations)

---

## 1. Web Fundamentals & Client-Server Architecture (Lecture 01)

### 1.1 Client-Server Communication & HTTP
Every backend web system relies on the **Client-Server Architecture**:
- **Client**: Any device or program initiating a request (Browser, Postman, Mobile App, React frontend).
- **Server**: A high-availability machine or process running software designed to listen for incoming connections, process business logic, interact with data stores, and return a response.

```
+------------+       1. HTTP Request (GET /students)       +------------+
|   Client   | -----------------------------------------> |   Server   |
| (Browser/  |                                            | (Spring    |
|  Postman)  | <----------------------------------------- |  Backend)  |
+------------+       2. HTTP Response (JSON / HTML)       +------------+
```

Communication is governed by **HTTP (Hypertext Transfer Protocol)**, an application-layer, stateless protocol on top of TCP/IP.

[⬆ Back to Top](#table-of-contents)

---

### 1.2 Anatomy of an HTTP Request & Response

#### Anatomy of an HTTP Request
An HTTP request consists of three distinct parts:
1. **Request Line**: HTTP Method, Request URI (Path + Query Params), and HTTP Version.
   ```http
   POST /api/v1/students?dept=CS HTTP/1.1
   ```
2. **Request Headers**: Metadata about the client, message formatting, authentication, and content negotiation.
   ```http
   Host: api.raysi.com
   Content-Type: application/json
   Authorization: Bearer eyJhbGciOiJIUzI1Ni...
   Accept: application/json
   ```
3. **Request Body (Payload)**: Optional data sent to the server (primarily in `POST`, `PUT`, `PATCH`).
   ```json
   {
     "name": "Aashish",
     "email": "aashish@raysi.com"
   }
   ```

#### Common HTTP Methods
| Method | Purpose | Idempotent | Safe |
| :--- | :--- | :--- | :--- |
| **GET** | Retrieve a resource | Yes | Yes |
| **POST** | Create a new resource / execute operation | No | No |
| **PUT** | Replace a resource completely | Yes | No |
| **PATCH**| Partially update an existing resource | No | No |
| **DELETE**| Remove a resource | Yes | No |

#### Anatomy of an HTTP Response
1. **Status Line**: HTTP Version, Status Code, Reason Phrase.
   ```http
   HTTP/1.1 200 OK
   ```
2. **Response Headers**: Metadata describing the payload and server response.
   ```http
   Content-Type: application/json
   Content-Length: 54
   Date: Wed, 29 Oct 2026 12:00:00 GMT
   ```
3. **Response Body**: Data payload returned to the client (JSON, XML, HTML, binary).

#### HTTP Status Code Categories
- **1xx (Informational)**: Request received, continuing process (e.g., `100 Continue`).
- **2xx (Success)**: Action successfully received, understood, and accepted (`200 OK`, `201 Created`, `204 No Content`).
- **3xx (Redirection)**: Further action needed (`301 Moved Permanently`, `302 Found`, `304 Not Modified`).
- **4xx (Client Error)**: Request contains bad syntax or cannot be fulfilled (`400 Bad Request`, `401 Unauthorized`, `403 Forbidden`, `404 Not Found`, `409 Conflict`).
- **5xx (Server Error)**: Server failed to fulfill an apparently valid request (`500 Internal Server Error`, `502 Bad Gateway`, `503 Service Unavailable`).

[⬆ Back to Top](#table-of-contents)

---

### 1.3 Core Java Limitations & The Evolution of Servlets to Spring

#### Why Core Java Alone is Not Enough
A standard Java program runs inside a JVM with a single entry point: `public static void main(String[] args)`.
To handle web traffic, core Java would require:
1. Opening low-level TCP `ServerSocket(8080)`.
2. Listening in a blocking loop (`serverSocket.accept()`).
3. Spawning and managing operating system threads per connection.
4. Manually parsing raw byte streams into HTTP request strings.
5. Manually parsing headers, parameters, and bodies.
6. Formatting HTTP compliant text responses with correct CRLF (`\r\n`) terminators.

#### The Role of Servlets & Servlet Containers
The **Java Servlet Specification** (Jakarta/Java EE) introduced **Servlet Containers** (e.g., Apache Tomcat):
- **Web Server / Servlet Container (Tomcat)** handles the networking, TCP sockets, thread pooling, and converts incoming HTTP bytes into standard Java objects: `HttpServletRequest` and `HttpServletResponse`.
- **Servlets**: Java classes implementing `HttpServlet` overriding `doGet()` or `doPost()`.

```
Client HTTP Request
       │
       ▼
┌───────────────────────────────────────┐
│ Apache Tomcat (Servlet Container)     │
│  - Listens on TCP Port 8080           │
│  - Thread Pool Management             │
│  - Parses HTTP -> HttpServletRequest  │
│  - Creates HttpServletResponse        │
│                                       │
│        ┌─────────────────────────┐    │
│        │ Servlet (doGet/doPost)  │    │
│        └─────────────────────────┘    │
└───────────────────────────────────────┘
```

#### Why Spring Framework Emerged
While Servlets solved low-level networking, enterprise applications still had severe problems:
- Manual object management and tight coupling across services.
- Repetitive boilerplate code for JDBC database interactions, transactions, and security.
- Difficult unit testing due to hardcoded dependencies (`new Service()`).
- Inability to declare cross-cutting concerns (logging, transactions, security) declaratively.

**Spring Framework** introduced **Inversion of Control (IoC)**, **Dependency Injection (DI)**, and **Aspect-Oriented Programming (AOP)** to decouple application components and simplify enterprise Java development.

[⬆ Back to Top](#table-of-contents)

---

## 2. First Spring Boot Application & Architecture (Lecture 02)

### 2.1 Ports, Localhost & Socket Basics
- **IP Address**: Identifies a specific machine on a network (e.g., `127.0.0.1` is the loopback address reserved for the local machine, aliased as `localhost`).
- **Port Number**: A 16-bit number (0 to 65535) identifying a specific process or network service running on that machine:
  - Well-known ports: `80` (HTTP), `443` (HTTPS), `3306` (MySQL), `5432` (PostgreSQL).
  - Spring Boot Default: `8080`.
- When a browser makes a request to `http://localhost:8080/greet`:
  1. The OS routes traffic internally to `127.0.0.1`.
  2. The OS network stack inspects port `8080` and hands packets to the socket owned by the embedded Tomcat server.

[⬆ Back to Top](#table-of-contents)

---

### 2.2 Spring Initializr & Boot Project Layout
**Spring Initializr** (`start.spring.io`) generates standard project scaffolding:
- **Project**: Maven or Gradle.
- **Language**: Java.
- **Spring Boot Version**: Choose a stable GA (General Availability) release (avoid SNAPSHOT or Milestone releases in production).
- **Project Metadata**:
  - `Group`: e.g., `com.raysi`
  - `Artifact`: e.g., `first-app`
  - `Package name`: `com.raysi.firstapp`
  - `Packaging`: `Jar`
  - `Java Version`: 17 or 21 LTS

#### Standard Directory Layout
```
first-app/
├── pom.xml                               # Maven project object model
├── mvnw / mvnw.cmd                       # Maven wrapper scripts
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/raysi/firstapp/
│   │   │       └── FirstAppApplication.java   # Entry point (@SpringBootApplication)
│   │   └── resources/
│   │       ├── application.properties         # Configuration key-values
│   │       ├── static/                        # Static web assets (CSS, JS, images)
│   │       └── templates/                     # Server-side templates (Thymeleaf)
│   └── test/
│       └── java/
│           └── com/raysi/firstapp/
│               └── FirstAppApplicationTests.java
```

[⬆ Back to Top](#table-of-contents)

---

### 2.3 Controllers, `@RestController`, and First API Endpoint

#### The Main Entry Class
```java
package com.raysi.firstapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FirstAppApplication {
    public static void main(String[] args) {
        SpringApplication.run(FirstAppApplication.class, args);
    }
}
```
- `@SpringBootApplication` is a meta-annotation composed of:
  - `@SpringBootConfiguration`: Marks class as a configuration source.
  - `@EnableAutoConfiguration`: Enables Spring Boot's automatic bean configuration based on classpath starters.
  - `@ComponentScan`: Scans the current package (`com.raysi.firstapp`) and all child sub-packages for `@Component`, `@Service`, `@Repository`, `@RestController`.

#### Creating a REST Controller
```java
package com.raysi.firstapp.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/hello")
    public String sayHello(@RequestParam(defaultValue = "World") String name) {
        return "Hello, " + name + "!";
    }
}
```
- `@RestController` combines `@Controller` and `@ResponseBody`.
- Instead of resolving to an HTML view template, return values are serialized directly into the HTTP response body (Strings as plain text, Java objects as JSON via Jackson).

[⬆ Back to Top](#table-of-contents)

---

## 3. Maven Deep Dive & Build Automation (Lecture 03)

### 3.1 Compilation, Classpath, and JAR Files
1. **Compilation**: `javac Hello.java` produces bytecode `Hello.class`. Bytecode is hardware-independent and executed by the JVM (`java Hello`).
2. **Classpath**: An environment parameter or flag (`-cp` / `-classpath`) telling the JVM where to look for user-defined classes and external packages.
3. **JAR (Java ARchive)**: A zip-compressed archive containing compiled `.class` files, metadata, and resources:
   - **Library JAR**: Contains utility classes meant to be consumed by other projects. Has no `main-class` in `META-INF/MANIFEST.MF`.
   - **Executable "Fat / Uber" JAR**: Created by the `spring-boot-maven-plugin`. Packages compiled application classes along with all nested dependency JARs and an embedded Tomcat server into a single executable archive executed via `java -jar app.jar`.

[⬆ Back to Top](#table-of-contents)

---

### 3.2 Anatomy of `pom.xml` & GAV Coordinates
The **Project Object Model (`pom.xml`)** is the declarative heart of Maven.

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.0</version>
        <relativePath/>
    </parent>

    <groupId>com.raysi</groupId>
    <artifactId>maven-deep-dive</artifactId>
    <version>1.0.0</version>
    <packaging>jar</packaging>

    <properties>
        <java.version>17</java.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
```

#### GAV Coordinates
Every artifact in the Maven ecosystem is uniquely identified by **GAV**:
- **`groupId`**: Domain/organization reverse namespace (e.g., `com.raysi`, `org.springframework.boot`).
- **`artifactId`**: The specific project/module name (e.g., `student-service`).
- **`version`**: Release version (e.g., `1.0.0`, `3.2.0-SNAPSHOT`).

#### Why `spring-boot-starter-parent`?
Inheriting from `spring-boot-starter-parent` provides:
- Centralized dependency management with curated, compatible versions (eliminating dependency version conflicts).
- Default Java compilation compiler plugin settings.
- Resource filtering for `application.properties` and `application.yml`.

[⬆ Back to Top](#table-of-contents)

---

### 3.3 Repositories, Transitive Dependencies, and Maven Lifecycle

#### Maven Repositories Architecture
1. **Local Repository**: Stored in `~/.m2/repository`. When Maven needs a dependency, it checks local cache first.
2. **Central Repository**: The global, public repository hosted by Sonatype (`repo.maven.apache.org/maven2`).
3. **Remote / Enterprise Repository**: Private organizational repository (Nexus, Artifactory) hosting proprietary libraries.

#### Transitive Dependencies
If Project A depends on `spring-boot-starter-web`, Maven automatically inspects its POM and pulls down:
- `spring-web`
- `spring-webmvc`
- `tomcat-embed-core`
- `jackson-databind`
This automatic resolution of indirect requirements is called **Transitive Dependency Management**.

#### Maven Build Lifecycle Phases
Maven organizes builds into sequential lifecycles. Executing a phase triggers all prior phases:

```
validate ──> compile ──> test ──> package ──> verify ──> install ──> deploy
```
- **`validate`**: Verifies project correctness and all mandatory information is available.
- **`compile`**: Compiles source code in `src/main/java` to `target/classes`.
- **`test`**: Executes unit tests in `src/test/java` via Surefire.
- **`package`**: Takes compiled code and packages it into configured format (`JAR` / `WAR`) in `target/`.
- **`install`**: Installs package into local repository (`~/.m2/repository`) for use by local sibling projects.
- **`deploy`**: Copies final package to remote repository for sharing across team.

[⬆ Back to Top](#table-of-contents)

---

## 4. Inversion of Control (IoC) & Dependency Injection (Lecture 04)

### 4.1 The Tight Coupling Anti-Pattern
Consider an e-commerce order system:

```java
// Anti-Pattern: Tight Coupling
public class OrderService {
    // OrderService is tightly coupled to a specific implementation
    private RazorpayPaymentService paymentService = new RazorpayPaymentService();

    public void processOrder(double amount) {
        paymentService.pay(amount);
    }
}
```
#### Problems with this design:
1. **Zero Flexibility**: Switching from Razorpay to Stripe requires editing and recompiling `OrderService`.
2. **Impossible to Unit Test**: Cannot mock `paymentService`. Tests hit live payment gateways.
3. **Violation of Single Responsibility**: `OrderService` manages business logic *and* the instantiation/lifecycle of its dependencies.

[⬆ Back to Top](#table-of-contents)

---

### 4.2 Decoupling Through Interfaces
To decouple classes, program to an **interface** rather than a concrete implementation:

```java
package com.raysi.service;

public interface PaymentService {
    void pay(double amount);
}
```

```java
package com.raysi.service.impl;

import com.raysi.service.PaymentService;

public class RazorpayPaymentService implements PaymentService {
    @Override
    public void pay(double amount) {
        System.out.println("Processing ₹" + amount + " via Razorpay");
    }
}
```

```java
package com.raysi.service.impl;

import com.raysi.service.PaymentService;

public class StripePaymentService implements PaymentService {
    @Override
    public void pay(double amount) {
        System.out.println("Processing ₹" + amount + " via Stripe");
    }
}
```

However, writing `private PaymentService paymentService = new RazorpayPaymentService();` inside `OrderService` still retains tight coupling because the keyword `new` binds the class to a concrete type.

[⬆ Back to Top](#table-of-contents)

---

### 4.3 Dependency Injection (DI) & Inversion of Control (IoC)
- **Dependency**: An object that another object needs to function (`PaymentService` is a dependency of `OrderService`).
- **Inversion of Control (IoC)**: Inverting the control of object creation, configuration, and lifecycle management from the class itself to an external container/framework.
- **Dependency Injection (DI)**: The practical design pattern used to implement IoC. Instead of the class instantiating its dependencies, the dependencies are **injected** into the class from the outside.

```
Traditional Flow (Control with Class):
OrderService ── creates ──> new RazorpayPaymentService()

Inversion of Control (Control with Container):
IoC Container ── creates ──> RazorpayPaymentService
IoC Container ── injects ──> OrderService
```

[⬆ Back to Top](#table-of-contents)

---

### 4.4 Injection Strategies: Constructor vs Setter vs Field

#### 1. Constructor Injection (Industry Recommended)
Dependencies are passed through the class constructor.
```java
package com.raysi.service;

import org.springframework.stereotype.Service;

@Service
public class OrderService {
    private final PaymentService paymentService;

    // Recommended: Explicit constructor injection
    public OrderService(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    public void processOrder(double amount) {
        paymentService.pay(amount);
    }
}
```
**Benefits:**
- Fields can be marked `final`, ensuring **immutability**.
- Prevents `NullPointerException`: The class cannot be instantiated without its mandatory dependencies.
- Simplifies pure unit tests (dependencies can be passed manually without Spring context).

#### 2. Setter Injection
Dependencies are supplied via public setter methods.
```java
@Service
public class OrderService {
    private PaymentService paymentService;

    @Autowired
    public void setPaymentService(PaymentService paymentService) {
        this.paymentService = paymentService;
    }
}
```
**Use Case**: Optional or mutable dependencies that can be reconfigured or changed at runtime.

#### 3. Field Injection (Discouraged Anti-Pattern)
Dependencies are injected directly into private fields using reflection.
```java
@Service
public class OrderService {
    @Autowired
    private PaymentService paymentService; // Discouraged
}
```
**Drawbacks**:
- Cannot make fields `final`.
- Hides class dependencies.
- Cannot be unit-tested without Reflection or Spring context runner.

[⬆ Back to Top](#table-of-contents)

---

## 5. Spring IoC Container, Beans & Annotation Configuration (Lecture 05)

### 5.1 What is a Spring Bean?
A **Spring Bean** is any Java object that is instantiated, assembled, wired, and managed by the **Spring IoC Container**.
- Standard POJO + Spring Container Management = Spring Bean.

[⬆ Back to Top](#table-of-contents)

---

### 5.2 `ApplicationContext` vs `BeanFactory`

```
┌──────────────────────────────────────────────┐
│                BeanFactory                   │  <-- Basic IoC container
│   - Lazy bean loading                        │
│   - Lightweight memory footprint             │
└──────────────────────┬───────────────────────┘
                       │ extends
┌──────────────────────▼───────────────────────┐
│             ApplicationContext               │  <-- Enterprise container
│   - Eager singleton bean loading             │
│   - AOP integration & event publication      │
│   - MessageSource (i18n) support             │
│   - Environment & profile abstractions      │
└──────────────────────────────────────────────┘
```
In modern Spring Boot development, `ApplicationContext` is used almost exclusively. Common implementations include:
- `AnnotationConfigApplicationContext`: For standalone Java configuration classes.
- `ClassPathXmlApplicationContext`: For standalone XML configuration files.
- `AnnotationConfigServletWebServerApplicationContext`: Bootstrapped automatically by Spring Boot web applications.

[⬆ Back to Top](#table-of-contents)

---

### 5.3 Annotation Configuration: `@Configuration`, `@ComponentScan`, `@Component`

#### Configuration Class
```java
package com.raysi.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "com.raysi")
public class AppConfig {
}
```
- `@Configuration`: Indicates that the class declares one or more `@Bean` methods and may be processed by the Spring container to generate bean definitions.
- `@ComponentScan`: Instructs Spring where to scan for classes annotated with stereotype annotations (`@Component`, `@Service`, `@Repository`, `@Controller`).

#### Stereotype Annotations
All stereotype annotations are meta-annotated with `@Component`:
- `@Component`: Generic stereotype for any Spring-managed component.
- `@Service`: Specializes `@Component` for business service layer classes.
- `@Repository`: Specializes `@Component` for persistence/DAO classes; enables automatic database exception translation to Spring's `DataAccessException` hierarchy.
- `@Controller` / `@RestController`: Specializes `@Component` for web presentation layer handling HTTP requests.

[⬆ Back to Top](#table-of-contents)

---

### 5.4 Resolving Ambiguities: `@Autowired`, `@Qualifier`, and `@Primary`
When multiple beans implement the same interface, Spring encounters a `NoUniqueBeanDefinitionException`.

```java
public interface NotificationService {
    void send(String msg);
}

@Component("emailService")
public class EmailNotificationService implements NotificationService {
    public void send(String msg) { System.out.println("Email: " + msg); }
}

@Component("smsService")
@Primary // Option 1: Designate default bean
public class SmsNotificationService implements NotificationService {
    public void send(String msg) { System.out.println("SMS: " + msg); }
}
```

```java
@Service
public class UserService {
    private final NotificationService notificationService;

    // Option 2: Use @Qualifier to specify exact bean ID
    public UserService(@Qualifier("emailService") NotificationService notificationService) {
        this.notificationService = notificationService;
    }
}
```
- `@Primary`: Designates a bean as the default candidate when multiple beans match.
- `@Qualifier`: Explicitly names the target bean ID to inject at the injection point, overriding `@Primary`.

[⬆ Back to Top](#table-of-contents)

---

### 5.5 `@Bean` vs `@Component`

| Feature | `@Component` | `@Bean` |
| :--- | :--- | :--- |
| **Placement** | Placed on **Class level** | Placed on **Method level** inside `@Configuration` classes |
| **Control** | Requires source code access | Works with 3rd-party unmodifiable classes (e.g., `RestTemplate`, `ObjectMapper`) |
| **Creation** | Instantiated automatically by component scan | Instantiated manually in method body; Spring captures return value |
| **Custom Logic** | Limited initialization logic | Full flexibility (conditional logic, constructor parameters, configuration) |

```java
@Configuration
public class ThirdPartyConfig {

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper; // Managed as a Spring Bean
    }
}
```

[⬆ Back to Top](#table-of-contents)

---

## 6. Circular Dependencies, Bean Scopes & Initialization (Lecture 06)

### 6.1 Circular Dependency: Root Cause & Mechanics
A **Circular Dependency** occurs when Class A depends on Class B, and Class B directly or indirectly depends on Class A:

```
┌──────────────┐         depends on         ┌──────────────┐
│    Class A   │ ─────────────────────────> │    Class B   │
│              │ <───────────────────────── │              │
└──────────────┘         depends on         └──────────────┘
```

#### Code Example with Constructor Injection
```java
@Service
public class ServiceA {
    private final ServiceB serviceB;

    public ServiceA(ServiceB serviceB) {
        this.serviceB = serviceB;
    }
}

@Service
public class ServiceB {
    private final ServiceA serviceA;

    public ServiceB(ServiceA serviceA) {
        this.serviceA = serviceA;
    }
}
```
**What happens at startup?**
1. Spring attempts to instantiate `ServiceA`.
2. It detects that `ServiceA`'s constructor requires `ServiceB`.
3. Spring pauses `ServiceA` and attempts to instantiate `ServiceB`.
4. It detects that `ServiceB`'s constructor requires `ServiceA`.
5. Deadlock! Neither bean can have its constructor invoked.
6. Spring throws **`BeanCurrentlyInCreationException`**.

[⬆ Back to Top](#table-of-contents)

---

### 6.2 Constructor Injection vs Setter/Field Injection in Circular Loops
Historically, Spring could resolve circular dependencies when using **Setter Injection** or **Field Injection** using its internal **3-Level Cache (`DefaultSingletonBeanRegistry`)**:
1. `singletonObjects` (First Level Cache): Fully initialized, ready-to-use singleton beans.
2. `earlySingletonObjects` (Second Level Cache): Early references (instantiated via default constructor, but properties not yet injected).
3. `singletonFactories` (Third Level Cache): ObjectFactory holding raw bean instances, allowing early exposure for proxy generation.

With constructor injection, the Java Virtual Machine itself cannot allocate the object without passing valid parameters into the constructor, making circular resolution impossible.

> [!WARNING]
> Starting with **Spring Boot 2.6.0**, circular dependencies are **prohibited by default** even for setter and field injection. The application fails fast on startup.

[⬆ Back to Top](#table-of-contents)

---

### 6.3 Resolving Circular Dependencies & Architectural Smells
A circular dependency is almost always an **Architectural Code Smell** indicating violating the **Single Responsibility Principle (SRP)**.

#### Permanent Architectural Solutions:
1. **Refactor and Extract Shared Concern (Best Practice)**:
   Extract common logic into a third service (`ServiceC`) that both `ServiceA` and `ServiceB` depend on.

```
       ┌──────────────┐        ┌──────────────┐
       │   ServiceA   │        │   ServiceB   │
       └──────┬───────┘        └──────┬───────┘
              │                       │
              ▼                       ▼
       ┌──────────────────────────────────────┐
       │   ServiceC (Shared Business Logic)   │
       └──────────────────────────────────────┘
```

2. **Temporary Workaround: `@Lazy` Injection**:
   Break the instantiation loop by injecting a proxy instead of the real bean.
```java
@Service
public class ServiceA {
    private final ServiceB serviceB;

    public ServiceA(@Lazy ServiceB serviceB) {
        this.serviceB = serviceB;
    }
}
```

[⬆ Back to Top](#table-of-contents)

---

### 6.4 Spring Bean Scopes: Singleton, Prototype, Web Scopes
The bean scope determines the lifecycle and number of instances created by the container.

```java
@Component
@Scope("singleton") // or @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class TokenGenerator { }
```

| Scope | Instances Created | Lifecycle / Destruction |
| :--- | :--- | :--- |
| **`singleton`** *(Default)* | Exactly **one** instance per Spring IoC Container | Maintained throughout container life; destroyed when container stops |
| **`prototype`** | **New instance** created every time requested (`getBean()` or injected) | Spring creates and initializes it, but does **not** manage destruction callbacks |
| **`request`** *(Web)* | One instance per single HTTP request | Destroyed when HTTP request finishes |
| **`session`** *(Web)* | One instance per HTTP User Session | Destroyed when session is invalidated or expires |
| **`application`** *(Web)* | One instance per `ServletContext` | Destroyed when `ServletContext` is shut down |
| **`websocket`** *(Web)* | One instance per WebSocket session | Destroyed when WebSocket session terminates |

> [!CAUTION]
> Injecting a **`prototype`** bean into a **`singleton`** bean means the prototype is injected **only once** at container startup! To obtain a new prototype instance every time, use `ObjectProvider<T>`, `@Lookup`, or inject the `ApplicationContext`.

[⬆ Back to Top](#table-of-contents)

---

### 6.5 Bean Initialization Strategies: Eager vs Lazy (`@Lazy`)
- **Eager Initialization (Default for Singletons)**:
  - Spring instantiates all singleton beans during container startup (`refresh()`).
  - *Advantage*: Catches configuration errors, missing dependencies, and startup issues immediately.
  - *Disadvantage*: Longer application startup duration; higher memory consumption on boot.
- **Lazy Initialization (`@Lazy`)**:
  - The bean is not instantiated at startup; it is initialized only when first requested or injected.
```java
@Component
@Lazy
public class ExpensiveReportGenerator {
    public ExpensiveReportGenerator() {
        System.out.println("Initialized only when accessed!");
    }
}
```

[⬆ Back to Top](#table-of-contents)

---

## 7. Spring Bean Lifecycle (Lecture 07)

### 7.1 Complete Bean Lifecycle Sequence
The journey of a Spring Bean consists of distinct phases orchestrated by the IoC container:

```
┌────────────────────────────────────────────────────────┐
│ 1. Load Bean Definitions (Metadata parsing)            │
└───────────────────────────┬────────────────────────────┘
                            │
┌───────────────────────────▼────────────────────────────┐
│ 2. Bean Instantiation (Reflection constructor call)    │
└───────────────────────────┬────────────────────────────┘
                            │
┌───────────────────────────▼────────────────────────────┐
│ 3. Populate Properties (Dependency Injection)          │
└───────────────────────────┬────────────────────────────┘
                            │
┌───────────────────────────▼────────────────────────────┐
│ 4. Aware Interface Callbacks (BeanName, Context, etc.) │
└───────────────────────────┬────────────────────────────┘
                            │
┌───────────────────────────▼────────────────────────────┐
│ 5. BeanPostProcessor: postProcessBeforeInitialization  │
└───────────────────────────┬────────────────────────────┘
                            │
┌───────────────────────────▼────────────────────────────┐
│ 6. Initialization Callbacks                            │
│    - @PostConstruct                                    │
│    - InitializingBean.afterPropertiesSet()             │
│    - Custom init-method                                │
└───────────────────────────┬────────────────────────────┘
                            │
┌───────────────────────────▼────────────────────────────┐
│ 7. BeanPostProcessor: postProcessAfterInitialization   │
│    (Where AOP dynamic proxies are generated!)          │
└───────────────────────────┬────────────────────────────┘
                            │
┌───────────────────────────▼────────────────────────────┐
│ 8. Bean Ready for Use (In Service)                     │
└───────────────────────────┬────────────────────────────┘
                            │ Container Shutdown
┌───────────────────────────▼────────────────────────────┐
│ 9. Destruction Callbacks                               │
│    - @PreDestroy                                       │
│    - DisposableBean.destroy()                          │
│    - Custom destroy-method                             │
└────────────────────────────────────────────────────────┘
```

[⬆ Back to Top](#table-of-contents)

---

### 7.2 Aware Interfaces
Aware interfaces allow a bean to access Spring container infrastructure:
- **`BeanNameAware`**: Informs the bean of its assigned ID in the container.
- **`BeanFactoryAware`**: Supplies the owning `BeanFactory`.
- **`ApplicationContextAware`**: Supplies the running `ApplicationContext`.

```java
package com.raysi.lifecycle;

import org.springframework.beans.factory.BeanNameAware;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class InfrastructureAwareBean implements BeanNameAware, ApplicationContextAware {
    private String beanName;
    private ApplicationContext context;

    @Override
    public void setBeanName(String name) {
        this.beanName = name;
        System.out.println("Bean registered with name: " + name);
    }

    @Override
    public void setApplicationContext(ApplicationContext context) {
        this.context = context;
        System.out.println("ApplicationContext injected");
    }
}
```

[⬆ Back to Top](#table-of-contents)

---

### 7.3 Initialization Callbacks: `@PostConstruct`, `InitializingBean`, Custom `initMethod`
When dependencies are injected, the bean often needs to establish network sockets, open database connection pools, or pre-load cache data.

Spring provides three mechanisms (executed in exact order):
1. **`@PostConstruct`** (Standard JSR-250 annotation — Recommended).
2. **`InitializingBean.afterPropertiesSet()`** (Spring interface — tightly couples code to Spring).
3. **Custom `initMethod`** (Declared via `@Bean(initMethod = "init")` or XML).

```java
package com.raysi.lifecycle;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

@Component
public class DatabaseConnectionPool implements InitializingBean {

    public DatabaseConnectionPool() {
        System.out.println("Step 2: Constructor called");
    }

    @PostConstruct
    public void postConstruct() {
        System.out.println("Step 6a: @PostConstruct executed");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("Step 6b: InitializingBean.afterPropertiesSet executed");
    }

    public void customInit() {
        System.out.println("Step 6c: Custom initMethod executed");
    }
}
```

[⬆ Back to Top](#table-of-contents)

---

### 7.4 Destruction Callbacks: `@PreDestroy`, `DisposableBean`, Custom `destroyMethod`
Triggered during graceful container shutdown (e.g., `context.close()`) to release held resources:
1. **`@PreDestroy`** (Recommended).
2. **`DisposableBean.destroy()`**.
3. **Custom `destroyMethod`** (Declared via `@Bean(destroyMethod = "cleanup")`).

```java
package com.raysi.lifecycle;

import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Component;

@Component
public class CacheManager implements DisposableBean {

    @PreDestroy
    public void preDestroy() {
        System.out.println("Step 9a: @PreDestroy executed - Flushing dirty cache");
    }

    @Override
    public void destroy() throws Exception {
        System.out.println("Step 9b: DisposableBean.destroy executed - Closing socket");
    }
}
```

[⬆ Back to Top](#table-of-contents)

---

### 7.5 The Role of `BeanPostProcessor` & Proxy Creation
The `BeanPostProcessor` (BPP) interface is the backbone of Spring's extensibility. It intercepts bean creation across all beans in the container.

```java
package com.raysi.lifecycle;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

@Component
public class CustomLoggingBPP implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        // Runs before @PostConstruct
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        // Runs after @PostConstruct
        // THIS IS WHERE SPRING AOP AND TRANSACTIONAL PROXIES WRAP THE BEAN!
        return bean;
    }
}
```

[⬆ Back to Top](#table-of-contents)

---

## 8. Spring XML-Based Configuration (Lecture 08)

### 8.1 Understanding `beans.xml` Metadata
Before Java annotations became standard, Spring configuration was maintained externally in XML files located on the classpath (typically `src/main/resources/beans.xml`).

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
       http://www.springframework.org/schema/beans/spring-beans.xsd">

    <!-- Bean definition -->
    <bean id="studentService" class="com.raysi.service.StudentService"/>

</beans>
```

#### Bootstrapping XML Configuration in Java
```java
package com.raysi;

import com.raysi.service.StudentService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class XmlAppMain {
    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("beans.xml");
        
        // Fetch bean by ID and Type
        StudentService service = context.getBean("studentService", StudentService.class);
        service.performAction();
    }
}
```

[⬆ Back to Top](#table-of-contents)

---

### 8.2 Declaring Beans, Setter Injection (`<property>`), and Constructor Injection (`<constructor-arg>`)

#### 1. Setter Injection (`<property>`)
Requires a no-arg constructor and setter methods on the target class.
```xml
<bean id="studentRepository" class="com.raysi.repository.StudentRepository"/>

<bean id="studentService" class="com.raysi.service.StudentService">
    <!-- Injects another bean via setter setStudentRepository(...) -->
    <property name="studentRepository" ref="studentRepository"/>
    <!-- Injects literal value via setter setBatchName(...) -->
    <property name="batchName" value="Batch-2026"/>
</bean>
```

#### 2. Constructor Injection (`<constructor-arg>`)
Passes references or literal values into parameterized constructors.
```xml
<bean id="studentService" class="com.raysi.service.StudentService">
    <constructor-arg ref="studentRepository"/>
    <constructor-arg value="Batch-2026"/>
</bean>
```

[⬆ Back to Top](#table-of-contents)

---

### 8.3 Hybrid Configuration: XML Component Scanning
Spring allows mixing XML with modern annotations by registering the `context` schema:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:context="http://www.springframework.org/schema/context"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="
           http://www.springframework.org/schema/beans
           http://www.springframework.org/schema/beans/spring-beans.xsd
           http://www.springframework.org/schema/context
           http://www.springframework.org/schema/context/spring-context.xsd">

    <!-- Enables scanning for @Component, @Service, @Autowired under com.raysi -->
    <context:component-scan base-package="com.raysi"/>

</beans>
```

[⬆ Back to Top](#table-of-contents)

---

### 8.4 Comparison: XML Configuration vs Java Annotations

| Dimension | XML Configuration | Modern Java Annotation Configuration |
| :--- | :--- | :--- |
| **Separation** | Clean separation of configuration from Java code | Configuration co-located with code |
| **Compile-Time Safety** | Low (Class names & bean IDs in strings; typos fail at runtime) | High (Type-safe, refactoring tools update annotations) |
| **Maintenance** | Verbose and cumbersome in large enterprise codebases | Terse, concise, and readable |
| **Industry Adoption** | Legacy enterprise maintenance | Modern enterprise standard across Spring Boot |

[⬆ Back to Top](#table-of-contents)
