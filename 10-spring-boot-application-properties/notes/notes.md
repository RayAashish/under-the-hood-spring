# Spring Boot Configuration: `application.properties`, `@Value`, `@ConfigurationProperties`, and Runner Interfaces

**Author:** raysi  
**Topic:** Spring Boot Configuration & Startup Execution  

---

## Introduction & Table of Contents

This reference guide covers externalized configuration in Spring Boot, injecting properties via `@Value` and `@ConfigurationProperties`, relaxed binding mechanics, startup runner interfaces (`CommandLineRunner` and `ApplicationRunner`), and the complete Spring Boot bootstrap lifecycle.

### Quick Navigation

- [1. Why Do We Need Configuration?](#1-why-do-we-need-configuration)
- [2. What Is application.properties?](#2-what-is-applicationproperties)
- [3. Important Point: Is application.properties Really External?](#3-important-point-is-applicationproperties-really-external)
- [4. What Is Externalized Configuration?](#4-what-is-externalized-configuration)
- [5. Using Configuration Values with @Value](#5-using-configuration-values-with-value)
- [6. Default Value with @Value](#6-default-value-with-value)
- [7. Problem with Too Many @Value Annotations](#7-problem-with-too-many-value-annotations)
- [8. What Is @ConfigurationProperties?](#8-what-is-configurationproperties)
- [9. Why Does payment.retry-count Map to retryCount?](#9-why-does-paymentretry-count-map-to-retrycount)
- [10. @Value VS @ConfigurationProperties](#10-value-vs-configurationproperties)
- [11. Why Do We Need Runner Interfaces?](#11-why-do-we-need-runner-interfaces)
- [12. Why Not Call the Bean Manually from main()?](#12-why-not-call-the-bean-manually-from-main)
- [13. CommandLineRunner](#13-commandlinerunner)
- [14. Passing Arguments to CommandLineRunner](#14-passing-arguments-to-commandlinerunner)
- [15. ApplicationRunner](#15-applicationrunner)
- [16. Complete Startup Flow](#16-complete-startup-flow)
- [17. Final Summary & Key Takeaways](#17-final-summary--key-takeaways)

---

## 1. Why Do We Need Configuration?

In real applications, not every value should be hardcoded inside Java classes.

For example:

```java
package in.raysi.service;

import org.springframework.stereotype.Component;

@Component
public class PaymentService {
    private String providerName = "Razorpay";
    private int retryCount = 3;

    public void pay() {
        System.out.println("Payment done using " + providerName);
        System.out.println("Retry count: " + retryCount);
    }
}
```

This code works, but the values are fixed inside the Java source code.

Now imagine that tomorrow we want to change the payment provider from:
- `Razorpay`
to:
- `Stripe`

If the value is hardcoded, we would need to:
1. Change the Java source code
2. Recompile the project
3. Rebuild the application package
4. Redeploy the application

For values that change across environments or over time, hardcoding is an anti-pattern. Examples of such values:
- Payment provider name
- Retry count
- Timeout thresholds
- Feature toggles / flags (enabled/disabled)
- Database connection URLs
- API keys & secrets
- Server port
- External service endpoint URLs

These values are better kept outside the main business logic. This is where configuration files come in.

[Back to Top](#quick-navigation)

---

## 2. What Is application.properties?

`application.properties` is a standard key-value configuration file that Spring Boot automatically detects and loads by convention.

The default location is:
```text
src/main/resources/application.properties
```

### Example:
```properties
payment.provider=Razorpay
payment.retry-count=3
payment.enabled=true
payment.timeout=5000
```

Here, the values are no longer written directly inside the Java class. They are managed as external configuration properties. Spring Boot automatically loads this file when the application starts.

Spring Boot also natively supports YAML configuration using:
```text
src/main/resources/application.yml
```

### Equivalent YAML Example:
```yaml
payment:
  provider: Razorpay
  retry-count: 3
  enabled: true
  timeout: 5000
```

Both `application.properties` and `application.yml` serve the identical purpose; the difference is primarily formatting syntax.

[Back to Top](#quick-navigation)

---

## 3. Important Point: Is application.properties Really External?

When `application.properties` is placed inside `src/main/resources`, it is packaged directly inside the final JAR/WAR artifact.

- Technically, if you change this packaged file in the source repository, you still need to rebuild the project.
- However, Spring Boot provides **externalized configuration**, allowing these internal values to be dynamically overridden from outside the packaged artifact at runtime without recompiling.

Spring Boot resolves configuration values from multiple external sources following an established precedence order:
1. Command-line arguments (`--server.port=9090`)
2. OS environment variables (`PAYMENT_PROVIDER=Stripe`)
3. Java System properties (`-Dpayment.provider=Stripe`)
4. External configuration files located outside the packaged JAR (e.g., in `/config` directory)
5. Internal packaged configuration files (`application.properties` / `application.yml`)

The architectural rule:
> **Keep changeable values outside Java business logic so the code remains clean, portable, and flexible.**

[Back to Top](#quick-navigation)

---

## 4. What Is Externalized Configuration?

Externalized configuration means separating operational variables from compiled Java bytecode.

Instead of writing:
```java
private String providerName = "Razorpay";
```
we declare the property in configuration:
```properties
payment.provider=Razorpay
```
and inject that value into the Java class at runtime.

This makes managing multiple deployment environments straightforward:

### Development Environment (`application-dev.properties`):
```properties
payment.provider=TestProvider
payment.retry-count=1
```

### Production Environment (`application-prod.properties`):
```properties
payment.provider=Razorpay
payment.retry-count=3
```

The compiled Java class remains untouched; only the configuration profile changes.

[Back to Top](#quick-navigation)

---

## 5. Using Configuration Values with @Value

Once properties are defined in `application.properties`, we inject them into Spring components using the `@Value` annotation.

### Constructor Injection Example:

```java
package in.raysi.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PaymentService {
    private final String providerName;
    private final int retryCount;

    public PaymentService(
            @Value("${payment.provider}") String providerName,
            @Value("${payment.retry-count}") int retryCount) {
        this.providerName = providerName;
        this.retryCount = retryCount;
    }

    public void pay() {
        System.out.println("Payment done using " + providerName);
        System.out.println("Retry count: " + retryCount);
    }
}
```

### How `@Value` Works:
- `@Value("${payment.provider}")`: Spring queries the `Environment` abstraction for the key `payment.provider` and injects its resolved value into the parameter.
- `@Value("${payment.retry-count}")`: Spring finds `payment.retry-count`, automatically parses the String `"3"` into an `int`, and assigns it.

[Back to Top](#quick-navigation)

---

## 6. Default Value with @Value

If a property is missing from `application.properties` and no fallback is specified, Spring Boot will throw an `IllegalArgumentException` / `BeanCreationException` during container startup:
```text
Could not resolve placeholder 'payment.provider' in value "${payment.provider}"
```

To prevent startup failures for optional configurations, provide a default value using the `:` delimiter:

```java
@Value("${payment.provider:DefaultProvider}")
private String providerName;
```

Syntax breakdown:
- `${property.name:fallbackValue}`
- If `payment.provider` is present in the environment, its value is used.
- If `payment.provider` is absent, `"DefaultProvider"` is injected.

### Constructor Example with Default Value:

```java
package in.raysi.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PaymentService {
    private final String providerName;

    public PaymentService(
            @Value("${payment.provider:DefaultProvider}") String providerName) {
        this.providerName = providerName;
    }
}
```

[Back to Top](#quick-navigation)

---

## 7. Problem with Too Many @Value Annotations

`@Value` works well for isolated, individual properties. However, real enterprise components often require dozens of related settings:

```properties
payment.provider=Razorpay
payment.retry-count=3
payment.enabled=true
payment.timeout=5000
```

Injecting every value individually via `@Value` leads to repetitive boilerplate:

```java
@Value("${payment.provider}")
private String provider;

@Value("${payment.retry-count}")
private int retryCount;

@Value("${payment.enabled}")
private boolean enabled;

@Value("${payment.timeout}")
private int timeout;
```

### Drawbacks of Excessive `@Value`:
1. Boilerplate code and clutter in business classes.
2. No type-safe group encapsulation.
3. Difficult to validate hierarchical or nested configuration trees.

For grouped, hierarchical configuration, Spring Boot provides a superior alternative: **`@ConfigurationProperties`**.

[Back to Top](#quick-navigation)

---

## 8. What Is @ConfigurationProperties?

`@ConfigurationProperties` binds an entire group of related external properties to a strongly typed Java POJO based on a common prefix.

### Step 1: Declare Properties in `application.properties`

```properties
payment.provider=Razorpay
payment.retry-count=3
payment.enabled=true
payment.timeout=5000
```

### Step 2: Create a Configuration Properties Class

```java
package in.raysi.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "payment")
public class PaymentProperties {
    private String provider;
    private int retryCount;
    private boolean enabled;
    private int timeout;

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
}
```

### Property Mapping Table:

| Configuration Property | Target Java Field |
| :--- | :--- |
| `payment.provider` | `provider` |
| `payment.retry-count` | `retryCount` |
| `payment.enabled` | `enabled` |
| `payment.timeout` | `timeout` |

### Step 3: Inject the Properties Object into Services

```java
package in.raysi.service;

import in.raysi.config.PaymentProperties;
import org.springframework.stereotype.Component;

@Component
public class PaymentService {
    private final PaymentProperties paymentProperties;

    public PaymentService(PaymentProperties paymentProperties) {
        this.paymentProperties = paymentProperties;
    }

    public void pay() {
        System.out.println("Payment done using " + paymentProperties.getProvider());
        System.out.println("Retry count: " + paymentProperties.getRetryCount());
        System.out.println("Enabled: " + paymentProperties.isEnabled());
        System.out.println("Timeout: " + paymentProperties.getTimeout());
    }
}
```

[Back to Top](#quick-navigation)

---

## 9. Why Does payment.retry-count Map to retryCount?

This automatic mapping is powered by Spring Boot's **relaxed binding** engine.

Spring Boot is lenient when matching property names in configuration files to Java fields. All of the following formats will bind cleanly to the Java field `private int retryCount;`:

| Format | Syntax Example |
| :--- | :--- |
| **kebab-case** (Standard in `.properties` / `.yml`) | `payment.retry-count=3` |
| **camelCase** (Standard in Java) | `payment.retryCount=3` |
| **snake_case** | `payment.retry_count=3` |
| **UPPER_CASE** (Standard in OS Environment variables) | `PAYMENT_RETRYCOUNT=3` / `PAYMENT_RETRY_COUNT=3` |

### General Convention:
- Write configuration keys in **kebab-case** (`retry-count`).
- Write Java class fields in **camelCase** (`retryCount`).
- Spring Boot seamlessly bridges the two.

[Back to Top](#quick-navigation)

---

## 10. @Value VS @ConfigurationProperties

| Comparison Criterion | `@Value` | `@ConfigurationProperties` |
| :--- | :--- | :--- |
| **Use Case** | Single, isolated values | Structured groups of related settings |
| **Relaxed Binding** | Limited support | Full support across all formats |
| **SpEL Support** | Fully supports Spring Expression Language (`#{...}`) | Does not support SpEL |
| **Validation** | Manual | Supports JSR-380 (`@Validated`, `@NotNull`, etc.) |
| **Encapsulation** | Scattered across individual fields | Centralized in dedicated POJO |
| **Maintainability** | Degrades as property counts increase | Highly maintainable and clean |

### Simple Decision Rule:
- For **1 or 2 isolated values**, `@Value` is quick and convenient.
- For **grouped or hierarchical configuration**, always use `@ConfigurationProperties`.

[Back to Top](#quick-navigation)

---

## 11. Why Do We Need Runner Interfaces?

In traditional web applications, business logic is triggered by incoming HTTP requests:
$$	ext{Client / Postman} \longrightarrow 	ext{Controller} \longrightarrow 	ext{Service}$$

When building command-line utilities, batch processors, data seeders, or non-web applications without `spring-boot-starter-web`:
- There are no HTTP endpoints.
- There is no web server (like Tomcat) listening for requests.

We need a standardized way to execute code **automatically once the Spring Boot container has finished initializing**. Spring Boot provides two dedicated interfaces for this purpose:
1. **`CommandLineRunner`**
2. **`ApplicationRunner`**

[Back to Top](#quick-navigation)

---

## 12. Why Not Call the Bean Manually from main()?

Technically, one could manually invoke service methods from the `main` method:

```java
package in.raysi;

import in.raysi.service.PaymentService;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

public class SpringBootCoreDemoApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext context =
                SpringApplication.run(SpringBootCoreDemoApplication.class, args);

        PaymentService paymentService = context.getBean(PaymentService.class);
        paymentService.pay();
    }
}
```

### Why this is discouraged:
1. Reverts to manual bean lookup (`context.getBean(...)`), breaking clean dependency injection.
2. Clutters the bootstrapping class with application execution logic.
3. Makes unit testing and lifecycle coordination difficult.

### Idiomatic Spring Boot Approach:
Declare a component implementing `CommandLineRunner` or `ApplicationRunner`. Spring manages the component lifecycle and executes it automatically.

[Back to Top](#quick-navigation)

---

## 13. CommandLineRunner

`CommandLineRunner` is a functional callback interface executed after application context refresh.

### Implementation:

```java
package in.raysi.runner;

import in.raysi.service.PaymentService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class AppRunner implements CommandLineRunner {
    private final PaymentService paymentService;

    public AppRunner(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @Override
    public void run(String... args) {
        paymentService.pay();
    }
}
```

### Execution Lifecycle:
1. Spring Boot creates `AppRunner` as a managed bean.
2. Injects the `PaymentService` dependency via constructor injection.
3. Container initialization finishes.
4. Spring Boot identifies beans implementing `CommandLineRunner`.
5. Invokes `run(String... args)` automatically.
6. `paymentService.pay()` executes.

The parameter signature `String... args` delivers raw command-line arguments as a variable-length string array.

[Back to Top](#quick-navigation)

---

## 14. Passing Arguments to CommandLineRunner

When starting the application from the command line:

```bash
java -jar app.jar hello world
```

### Accessing Arguments in Code:

```java
package in.raysi.runner;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class AppRunner implements CommandLineRunner {
    @Override
    public void run(String... args) {
        for (String arg : args) {
            System.out.println(arg);
        }
    }
}
```

### Console Output:
```text
hello
world
```

`CommandLineRunner` exposes arguments directly as unprocessed raw strings.

[Back to Top](#quick-navigation)

---

## 15. ApplicationRunner

`ApplicationRunner` serves the same operational purpose as `CommandLineRunner`, but wraps arguments in a structured **`ApplicationArguments`** abstraction rather than a raw `String[]`.

### Implementation:

```java
package in.raysi.runner;

import in.raysi.service.PaymentService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class AppStartupRunner implements ApplicationRunner {
    private final PaymentService paymentService;

    public AppStartupRunner(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @Override
    public void run(ApplicationArguments args) {
        paymentService.pay();
    }
}
```

### Parsing Named Options:

When launching with option flags:
```bash
java -jar app.jar --provider=Razorpay --retry=3 standalone
```

`ApplicationArguments` categorizes arguments into:
- **Option Arguments** (prefixed with `--`): e.g., `--provider=Razorpay`
- **Non-Option Arguments**: e.g., `standalone`

```java
@Override
public void run(ApplicationArguments args) {
    // Check if an option was supplied
    boolean hasProvider = args.containsOption("provider");

    // Retrieve option values
    System.out.println("Provider values: " + args.getOptionValues("provider"));
    System.out.println("Retry values: " + args.getOptionValues("retry"));

    // Retrieve non-option values
    System.out.println("Non-option args: " + args.getNonOptionArgs());
}
```

### Core Difference:
- `CommandLineRunner`: provides raw `String... args`.
- `ApplicationRunner`: provides structured, queryable `ApplicationArguments`.

[Back to Top](#quick-navigation)

---

## 16. Complete Startup Flow

```
main() method starts
       │
       ▼
SpringApplication.run() executes
       │
       ▼
Spring Boot prepares Environment
(Loads application.properties, application.yml, OS env vars, CLI args)
       │
       ▼
ApplicationContext is instantiated
       │
       ▼
@SpringBootApplication processed
(@ComponentScan scans packages, detects @Component, @Service, @Repository, etc.)
       │
       ▼
@EnableAutoConfiguration evaluates classpath dependencies
(Applies matching auto-configurations)
       │
       ▼
Beans are instantiated & dependencies injected
       │
       ▼
Configuration values injected/bound
(@Value fields populated, @ConfigurationProperties objects bound)
       │
       ▼
CommandLineRunner / ApplicationRunner beans execute run(...)
       │
       ▼
Application either keeps running (Web / daemon) or terminates cleanly
```

[Back to Top](#quick-navigation)

---

## 17. Final Summary & Key Takeaways

- **`application.properties` / `.yml`**: Used to externalize configuration values in key-value or hierarchical syntax under `src/main/resources`.
- **Externalized Configuration**: Allows application behavior to change across environments (Dev, Test, Prod) without recompiling Java source code.
- **`@Value`**: Ideal for injecting individual, isolated scalar values or expressions. Supports default fallbacks using `${property.name:defaultValue}`.
- **`@ConfigurationProperties`**: Binds cohesive groups of configuration properties to strongly typed Java POJOs using a common prefix.
- **Relaxed Binding**: Automatically resolves discrepancies between configuration naming conventions (e.g., kebab-case `payment.retry-count`) and Java naming conventions (camelCase `retryCount`).
- **Runner Interfaces (`CommandLineRunner` / `ApplicationRunner`)**: Provide clean post-startup execution hooks when container initialization completes.
  - `CommandLineRunner` handles raw `String[]` arguments.
  - `ApplicationRunner` handles structured `ApplicationArguments` with native option flag support (`--key=value`).

[Back to Top](#quick-navigation)

---
*Document prepared for raysi.*

