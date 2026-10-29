# Spring Boot Annotations and Configuration

**Author:** raysi  
**Topic:** Spring Boot Internals — Startup, Annotations, Auto-Configuration & Conditions  

---

## Introduction & Table of Contents

This guide covers why Spring Boot is used after Spring Core, project setup fundamentals, the internals of `SpringApplication.run()`, core annotations (`@SpringBootApplication`, `@SpringBootConfiguration`, `@ComponentScan`, `@EnableAutoConfiguration`), and condition-driven auto-configuration (`@ConditionalOnClass`, `@ConditionalOnMissingBean`).

### Quick Navigation

- [1. Why Spring Boot After Spring Core?](#1-why-spring-boot-after-spring-core)
- [2. Setting Up a Spring Boot Project](#2-setting-up-a-spring-boot-project)
- [3. Why Do We Use spring-boot-starter-parent?](#3-why-do-we-use-spring-boot-starter-parent)
- [4. What Is a Spring Boot Starter?](#4-what-is-a-spring-boot-starter)
- [5. Spring Core Startup vs Spring Boot Startup](#5-spring-core-startup-vs-spring-boot-startup)
- [6. Same Application in Spring Boot](#6-same-application-in-spring-boot)
- [7. Adding Simple Components](#7-adding-simple-components)
- [8. How Will the Method Run? (CommandLineRunner)](#8-how-will-the-method-run-commandlinerunner)
- [9. Understanding SpringApplication.run()](#9-understanding-springapplicationrun)
- [10. Understanding @SpringBootApplication](#10-understanding-springbootapplication)
- [11. @SpringBootConfiguration](#11-springbootconfiguration)
- [12. @ComponentScan in Spring Boot](#12-componentscan-in-spring-boot)
- [13. Can We Manually Change Component Scanning?](#13-can-we-manually-change-component-scanning)
- [14. @EnableAutoConfiguration](#14-enableautoconfiguration)
- [15. How Auto-Configuration Works Internally](#15-how-auto-configuration-works-internally)
- [16. Manual Configuration vs Auto-Configuration](#16-manual-configuration-vs-auto-configuration)
- [17. Classpath-Based Decision Making](#17-classpath-based-decision-making)
- [18. What Happens When We Add Web Dependency?](#18-what-happens-when-we-add-web-dependency)
- [19. Three Main Checks in Auto-Configuration](#19-three-main-checks-in-auto-configuration)
- [20. @ConditionalOnClass](#20-conditionalonclass)
- [21. @ConditionalOnMissingBean](#21-conditionalonmissingbean)
- [22. Our Beans vs Auto-Configured Beans](#22-our-beans-vs-auto-configured-beans)
- [23. Complete Spring Boot Startup Flow](#23-complete-spring-boot-startup-flow)
- [24. Important Difference: @ComponentScan VS @EnableAutoConfiguration](#24-important-difference-componentscan-vs-enableautoconfiguration)
- [25. Final Takeaways](#25-final-takeaways)
- [26. One-Line Summary](#26-one-line-summary)

---

## 1. Why Spring Boot After Spring Core?

In pure Spring Core, we manage objects and the IoC container manually:
```java
ApplicationContext context =
        new AnnotationConfigApplicationContext(AppConfig.class);
```

Then we provide a configuration class:
```java
@Configuration
@ComponentScan(basePackages = "in.raysi")
public class AppConfig {
}
```

Then we manually retrieve the bean and invoke methods:
```java
OrderService orderService = context.getBean(OrderService.class);
orderService.placeOrder();
```

While this explicit wiring helps developers understand Spring internals, it becomes highly repetitive in production environments. Every Spring application requires repetitive bootstrap steps:
- Start application
- Create Spring container
- Read configuration
- Scan components
- Create beans
- Inject dependencies
- Load properties
- Prepare application context

**Spring Boot solves this repetitive startup problem.**
- Spring Boot does **not** replace Spring Core.
- Spring Boot builds on top of Spring Core and provides an automated, opinionated way to bootstrap, configure, and execute the Spring container.

> **Common Misunderstanding:** `Spring Boot = Web API`.  
> That is incorrect. Spring Boot can power:
> - Console applications
> - Web applications & REST APIs
> - Database-driven batch processors
> - Message-driven microservices
> 
> Web server capability is added only when web starters are included.

[Back to Top](#quick-navigation)

---

## 2. Setting Up a Spring Boot Project

There are two primary approaches to creating a Spring Boot project:
1. **Manual Setup:** Create a standard Maven/Gradle project and add Spring Boot starter dependencies manually.
2. **Spring Initializr (`start.spring.io`):** Recommended standard for generating project scaffolding with configured parent dependencies, build plugins, and compatible libraries.

[Back to Top](#quick-navigation)

---

## 3. Why Do We Use spring-boot-starter-parent?

Spring Boot projects typically define `spring-boot-starter-parent` in `pom.xml`:

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>3.x.x</version>
    <relativePath/>
</parent>
```

### Core Responsibilities of the Parent POM:
- **Dependency Version Management:** Curates tested, mutually compatible dependency versions without requiring explicit `<version>` tags for third-party libraries (Hibernate, Jackson, Spring JDBC, etc.).
- **Plugin Management:** Preconfigures compiler plugins, resource filtering, and the `spring-boot-maven-plugin` executable repackaging.
- **Sensible Defaults:** Sets UTF-8 encoding and Java target compilation flags.

[Back to Top](#quick-navigation)

---

## 4. What Is a Spring Boot Starter?

A **starter** is an aggregated dependency descriptor that pulls in all required transitive dependencies for a given domain:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter</artifactId>
</dependency>
```

- `spring-boot-starter` is the fundamental starter for non-web applications, providing core Spring Boot setup, logging (`Logback`/`SLF4J`), YAML/properties parsing, and auto-configuration support.
- A starter is not an individual library; it is a curated collection of dependencies.

[Back to Top](#quick-navigation)

---

## 5. Spring Core Startup vs Spring Boot Startup

| Step | Spring Core | Spring Boot |
| :--- | :--- | :--- |
| **Container Creation** | Manual `new AnnotationConfigApplicationContext(...)` | Automated via `SpringApplication.run(...)` |
| **Configuration** | Explicit `@Configuration` + `@ComponentScan` | Consolidated under `@SpringBootApplication` |
| **Bean Retrieval** | Manual `context.getBean(...)` | Auto-injected or triggered via runners |
| **Execution** | Direct invocation in `main` | Automatic lifecycle execution |

[Back to Top](#quick-navigation)

---

## 6. Same Application in Spring Boot

A baseline Spring Boot application entry point:

```java
package in.raysi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringBootCoreDemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpringBootCoreDemoApplication.class, args);
    }
}
```

There is no need to write manual context initializers or explicit `@ComponentScan` declarations because `@SpringBootApplication` packages them together.

[Back to Top](#quick-navigation)

---

## 7. Adding Simple Components

Standard Spring components operate identically under Spring Boot:

```java
package in.raysi.service;

import org.springframework.stereotype.Component;

@Component
public class PaymentService {
    public void pay() {
        System.out.println("Payment completed");
    }
}
```

```java
package in.raysi.service;

import org.springframework.stereotype.Component;

@Component
public class OrderService {
    private final PaymentService paymentService;

    public OrderService(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    public void placeOrder() {
        paymentService.pay();
        System.out.println("Order placed");
    }
}
```

Spring detects `@Component`, registers the beans, and performs constructor dependency injection seamlessly.

[Back to Top](#quick-navigation)

---

## 8. How Will the Method Run? (CommandLineRunner)

Instead of manually extracting beans from the context using `context.getBean()`, Spring Boot provides `CommandLineRunner`:

```java
package in.raysi.runner;

import in.raysi.service.OrderService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class AppRunner implements CommandLineRunner {
    private final OrderService orderService;

    public AppRunner(OrderService orderService) {
        this.orderService = orderService;
    }

    @Override
    public void run(String... args) {
        orderService.placeOrder();
    }
}
```

`CommandLineRunner#run` executes automatically after the `ApplicationContext` is fully instantiated and ready.

[Back to Top](#quick-navigation)

---

## 9. Understanding SpringApplication.run()

```java
SpringApplication.run(SpringBootCoreDemoApplication.class, args);
```

### Execution Responsibilities:
1. Bootstraps the Spring application runtime.
2. Instantiates the appropriate `ApplicationContext` (`AnnotationConfigServletWebServerApplicationContext` or standard `AnnotationConfigApplicationContext`).
3. Loads configuration properties and environment variables.
4. Performs classpath scanning.
5. Evaluates conditional auto-configuration classes.
6. Instantiates singletons and wires dependencies.
7. Executes registered `ApplicationRunner` and `CommandLineRunner` beans.

[Back to Top](#quick-navigation)

---

## 10. Understanding @SpringBootApplication

`@SpringBootApplication` is a composite meta-annotation encapsulating three primary annotations:

```java
@SpringBootConfiguration
@EnableAutoConfiguration
@ComponentScan
public class SpringBootCoreDemoApplication {
}
```

| Component Annotation | Responsibility |
| :--- | :--- |
| **`@SpringBootConfiguration`** | Designates the primary configuration class for the context |
| **`@EnableAutoConfiguration`** | Activates Spring Boot's condition-based auto-configuration |
| **`@ComponentScan`** | Scans current package and all sub-packages for components |

[Back to Top](#quick-navigation)

---

## 11. @SpringBootConfiguration

`@SpringBootConfiguration` is Spring Boot's specialized `@Configuration` annotation.

- It identifies the class as a configuration source capable of declaring `@Bean` definitions.
- It enables Spring Boot test harnesses (`@SpringBootTest`) to locate the root configuration context automatically.

```java
@Configuration
public class AppConfig {
    @Bean
    public PaymentService paymentService() {
        return new PaymentService();
    }
}
```

*Best practice:* Keep the main `@SpringBootApplication` class uncluttered and declare domain beans inside dedicated configuration classes.

[Back to Top](#quick-navigation)

---

## 12. @ComponentScan in Spring Boot

By default, `@ComponentScan` scans the package containing the main application class and all child packages:

```text
in.raysi                              <-- Root package (scanned)
 ├── SpringBootCoreDemoApplication.java
 ├── controller/
 ├── service/
 ├── repository/
 └── config/
```

Placing classes outside this package root prevents Spring Boot from automatically registering those components.

[Back to Top](#quick-navigation)

---

## 13. Can We Manually Change Component Scanning?

Yes, by explicitly specifying base packages:

```java
@SpringBootApplication(scanBasePackages = "in.raysi")
public class SpringBootCoreDemoApplication {
}
```

*Recommendation:* Retain the default package hierarchy convention with the main application class at the root.

[Back to Top](#quick-navigation)

---

## 14. @EnableAutoConfiguration

`@EnableAutoConfiguration` instructs Spring Boot to analyze classpath dependencies, existing bean registrations, and configuration properties to configure default infrastructure beans.

### Examples of Auto-Configuration Classes:
- `DataSourceAutoConfiguration`
- `WebMvcAutoConfiguration`
- `JacksonAutoConfiguration`
- `TaskExecutionAutoConfiguration`

[Back to Top](#quick-navigation)

---

## 15. How Auto-Configuration Works Internally

Auto-configuration classes are evaluated conditionally rather than applied unconditionally:

```java
@AutoConfiguration
@ConditionalOnClass(SomeLibrary.class)
public class SomeLibraryAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public SomeService someService() {
        return new SomeService();
    }
}
```

- **Condition 1:** Is `SomeLibrary.class` on the classpath?
- **Condition 2:** Has the developer declared a custom `SomeService` bean?
- If the required class exists and no bean is registered, the auto-configuration bean is activated.

[Back to Top](#quick-navigation)

---

## 16. Manual Configuration vs Auto-Configuration

```
  Manual Configuration (Spring Core)
  Developer writes @Configuration + @Bean
                 |
                 v
     Direct, unconditioned creation

  Auto-Configuration (Spring Boot)
  Pre-written template configurations
                 |
                 v
   Evaluated against runtime conditions
                 |
                 v
   Instantiated only if conditions match
```

[Back to Top](#quick-navigation)

---

## 17. Classpath-Based Decision Making

The classpath acts as a signal to Spring Boot:
- Including `spring-boot-starter` indicates a standard non-web application. Tomcat will not boot.
- If web classes are missing, no web server or servlet listeners are initialized.

```
pom.xml dependency -> JARs on classpath -> Conditional matches -> Context initialized
```

[Back to Top](#quick-navigation)

---

## 18. What Happens When We Add Web Dependency?

Adding `spring-boot-starter-web` places Spring MVC and embedded Tomcat onto the classpath:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

Spring Boot detects `Servlet.class`, `Tomcat.class`, and `DispatcherServlet.class`, registers MVC handlers, launches embedded Tomcat on port 8080, and maintains the thread pool to serve HTTP requests.

[Back to Top](#quick-navigation)

---

## 19. Three Main Checks in Auto-Configuration

1. **Classpath Check:** Is the class/library present (`@ConditionalOnClass`)?
2. **Bean Registry Check:** Has a bean of this type already been defined (`@ConditionalOnMissingBean`)?
3. **Property Check:** Is a required application property enabled (`@ConditionalOnProperty`)?

```java
@AutoConfiguration
@ConditionalOnClass(PaymentGateway.class)
public class PaymentAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public PaymentGateway paymentGateway() {
        return new DefaultPaymentGateway();
    }
}
```

[Back to Top](#quick-navigation)

---

## 20. @ConditionalOnClass

Matches only when the specified class can be loaded from the application classpath.

```java
@ConditionalOnClass(SomeLibrary.class)
```

Enables Spring Boot to identify what libraries and third-party frameworks are bundled in the application artifact.

[Back to Top](#quick-navigation)

---

## 21. @ConditionalOnMissingBean

Matches only if no bean matching the specified class or type is currently registered in the `ApplicationContext`.

```java
@Bean
@ConditionalOnMissingBean
public SomeService someService() {
    return new DefaultSomeService();
}
```

Ensures that user-defined beans always override Spring Boot's fallback auto-configurations.

[Back to Top](#quick-navigation)

---

## 22. Our Beans vs Auto-Configured Beans

| Attribute | User-Defined Beans | Auto-Configured Beans |
| :--- | :--- | :--- |
| **Origin** | Application source code (`@Component`, `@Bean`) | Spring Boot starter libraries |
| **Discovery** | Component scanning or configuration classes | Conditional configuration scanning |
| **Precedence** | High (takes priority) | Low (yields via `@ConditionalOnMissingBean`) |

[Back to Top](#quick-navigation)

---

## 23. Complete Spring Boot Startup Flow

```
Application starts
       │
       ▼
SpringApplication.run() invoked
       │
       ▼
ApplicationContext created & Environment prepared
       │
       ▼
User configuration processed via @ComponentScan
       │
       ▼
Auto-configuration classes evaluated
  ├─ Check @ConditionalOnClass
  ├─ Check @ConditionalOnMissingBean
  └─ Register fallback beans
       │
       ▼
Dependencies injected & singletons initialized
       │
       ▼
ApplicationContext ready
       │
       ▼
CommandLineRunner / ApplicationRunner executed
```

[Back to Top](#quick-navigation)

---

## 24. Important Difference: @ComponentScan VS @EnableAutoConfiguration

| Annotation | Function | Target |
| :--- | :--- | :--- |
| **`@ComponentScan`** | Discovers application classes | Project source code |
| **`@EnableAutoConfiguration`** | Applies opinionated starter configurations | Framework & third-party libraries |

[Back to Top](#quick-navigation)

---

## 25. Final Takeaways

- Spring Boot does not replace Spring Core; it automates container startup and configuration.
- `@SpringBootApplication` unifies `@SpringBootConfiguration`, `@EnableAutoConfiguration`, and `@ComponentScan`.
- Auto-configuration is conditional, governed primarily by `@ConditionalOnClass` and `@ConditionalOnMissingBean`.
- Classpath contents determine which features and servers (e.g., Tomcat) activate.
- User-declared beans always override auto-configured defaults.

[Back to Top](#quick-navigation)

---

## 26. One-Line Summary

> **Spring Boot is not magic. It is Spring Core coupled with a condition-driven startup and configuration system that uses dependencies, properties, and classpath inspection to automate boilerplate setup.**

[Back to Top](#quick-navigation)

---
*Document prepared for raysi.*

