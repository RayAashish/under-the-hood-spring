# Module 03: Aspect-Oriented Programming (Spring AOP)

---

<a id="table-of-contents"></a>
## 📑 Table of Contents
- [1. Introduction to Spring AOP (Lecture 22)](#1-introduction-to-spring-aop-lecture-22)
  - [1.1 Cross-Cutting Concerns: Scattering vs Tangling](#11-cross-cutting-concerns-scattering-vs-tangling)
  - [1.2 Core AOP Terminology (Aspect, JoinPoint, Advice, Pointcut, Target)](#12-core-aop-terminology-aspect-joinpoint-advice-pointcut-target)
  - [1.3 Weaving & Dynamic Proxies: JDK Dynamic Proxy vs CGLIB](#13-weaving--dynamic-proxies-jdk-dynamic-proxy-vs-cglib)
  - [1.4 The Proxy Invocation Flow](#14-the-proxy-invocation-flow)
- [2. Spring AOP Advice Types (Lecture 23)](#2-spring-aop-advice-types-lecture-23)
  - [2.1 Setup & `@EnableAspectJAutoProxy`](#21-setup--enableaspectjautoproxy)
  - [2.2 `@Before` Advice](#22-before-advice)
  - [2.3 `@After` (Finally) Advice](#23-after-finally-advice)
  - [2.4 `@AfterReturning` Advice & Return Value Capture](#24-afterreturning-advice--return-value-capture)
  - [2.5 `@AfterThrowing` Advice & Exception Auditing](#25-afterthrowing-advice--exception-auditing)
  - [2.6 `@Around` Advice & `ProceedingJoinPoint`](#26-around-advice--proceedingjoinpoint)
  - [2.7 Advice Execution Order](#27-advice-execution-order)
- [3. Pointcut Expressions & Designators (Lecture 24)](#3-pointcut-expressions--designators-lecture-24)
  - [3.1 Pointcut Syntax & Wildcards (`*` and `..`)](#31-pointcut-syntax--wildcards--and-)
  - [3.2 The `execution()` Designator in Depth](#32-the-execution-designator-in-depth)
  - [3.3 Package & Type Matching: `within()`](#33-package--type-matching-within)
  - [3.4 Argument Matching: `args()` & `@args()`](#34-argument-matching-args--args)
  - [3.5 Target & Proxy Matching: `target()`, `this()`, and `bean()`](#35-target--proxy-matching-target-this-and-bean)
  - [3.6 Combining Pointcuts (`&&`, `||`, `!`) & Named Pointcuts with `@Pointcut`](#36-combining-pointcuts---and-named-pointcuts-with-pointcut)
- [4. Custom Annotations & AOP Parameter Binding (Lecture 25)](#4-custom-annotations--aop-parameter-binding-lecture-25)
  - [4.1 Declarative Interception: Separating "What" from "How"](#41-declarative-interception-separating-what-from-how)
  - [4.2 Creating Custom Annotations (`@Target`, `@Retention`, `@Documented`)](#42-creating-custom-annotations-target-retention-documented)
  - [4.3 Matching Annotations with `@annotation()`](#43-matching-annotations-with-annotation)
  - [4.4 Binding Annotation & Method Parameters Directly into Advice](#44-binding-annotation--method-parameters-directly-into-advice)
  - [4.5 Production Implementation: Method Execution Timer & Audit Logger](#45-production-implementation-method-execution-timer--audit-logger)

---

## 1. Introduction to Spring AOP (Lecture 22)

### 1.1 Cross-Cutting Concerns: Scattering vs Tangling
In standard Object-Oriented Programming (OOP), applications are structured around core business entities and services (e.g., `StudentService`, `OrderService`, `PaymentService`).

However, enterprise applications require non-functional capabilities that cut across multiple service boundaries:
- **Logging**
- **Security & Authorization**
- **Transaction Management**
- **Performance Profiling / Metrics**
- **Auditing & Input Sanitization**

#### Without AOP: The Two Fatal Code Smells
1. **Code Tangling**: Business logic inside a method is intertwined and obscured by boilerplate logging, security checks, and try-catch blocks.
2. **Code Scattering**: The identical security or logging logic is duplicated and copy-pasted across dozens of different service methods.

```
Without AOP (Scattering & Tangling):
┌───────────────────────────┐    ┌───────────────────────────┐
│       OrderService        │    │       PaymentService      │
├───────────────────────────┤    ├───────────────────────────┤
│ [Log Request]             │    │ [Log Request]             │
│ [Security Check]          │    │ [Security Check]          │
│ >>> Core Order Logic <<<  │    │ >>> Core Pay Logic <<<    │
│ [Transaction Commit]      │    │ [Transaction Commit]      │
│ [Log Time Taken]          │    │ [Log Time Taken]          │
└───────────────────────────┘    └───────────────────────────┘

With AOP (Modular Aspects):
┌────────────────────────────────────────────────────────────┐
│ Logging Aspect | Security Aspect | Transaction Aspect      │
└─────────────────────────────┬──────────────────────────────┘
                              │ Applied transparently
         ┌────────────────────┴────────────────────┐
         ▼                                         ▼
┌───────────────────────────┐         ┌───────────────────────────┐
│       OrderService        │         │       PaymentService      │
│ >>> Core Order Logic <<<  │         │ >>> Core Pay Logic <<<    │
└───────────────────────────┘         └───────────────────────────┘
```

[⬆ Back to Top](#table-of-contents)

---

### 1.2 Core AOP Terminology (Aspect, JoinPoint, Advice, Pointcut, Target)

| Term | Concept | Plain English Definition |
| :--- | :--- | :--- |
| **Aspect** | Module of Concern | A class annotated with `@Aspect` that encapsulates a cross-cutting concern (e.g., `LoggingAspect`). |
| **Join Point** | Execution Event | A candidate point during the execution of a program. In Spring AOP, a join point is **always a method execution**. |
| **Advice** | Action Executed | The action taken by an aspect at a particular join point (`@Before`, `@After`, `@Around`). |
| **Pointcut** | Predicate / Filter | An expression that determines *which* join points match and should trigger the advice. |
| **Target Object**| Business Bean | The raw Spring bean object containing core business logic being advised. |
| **AOP Proxy** | Wrapper Object | An object created by Spring AOP to intercept calls to the target object and execute advice. |
| **Weaving** | Linking Process | The process of linking aspects with target beans to create advised proxy objects. Spring performs weaving at **runtime**. |

[⬆ Back to Top](#table-of-contents)

---

### 1.3 Weaving & Dynamic Proxies: JDK Dynamic Proxy vs CGLIB
Spring AOP does not modify source code or class bytecode directly at compile time (unlike full AspectJ). Instead, it creates **Runtime Dynamic Proxies**.

```
Client (Caller)
       │
       ▼
┌────────────────────────────────────────────────────────┐
│ Spring AOP Dynamic Proxy                               │
│  1. Executes @Before Advice                            │
│  2. Delegates call to Target Object (Real Bean)        │
│  3. Executes @After / @AfterReturning Advice           │
└───────────────────────┬────────────────────────────────┘
                        │
                        ▼
┌────────────────────────────────────────────────────────┐
│ Target Object (e.g. StudentServiceImpl)                │
│  - Executes core business logic                        │
└────────────────────────────────────────────────────────┘
```

#### Proxy Strategies:
1. **JDK Dynamic Proxies**:
   - Built into Java standard library (`java.lang.reflect.Proxy`).
   - Requires the target class to implement at least one **interface**.
   - The proxy implements the interface and delegates calls to the target.
2. **CGLIB (Code Generation Library) Proxies**:
   - Generates dynamic bytecode subclasses at runtime (`TargetClass$$SpringCGLIB$$0`).
   - Does **not** require interfaces; works on concrete classes.
   - *Limitation*: Cannot advise `final` classes or `final` methods (because they cannot be overridden).

> [!NOTE]
> Since **Spring Boot 2.x**, Spring Boot defaults to **CGLIB proxies** for all beans (`spring.aop.proxy-target-class=true`), even when interfaces are present.

[⬆ Back to Top](#table-of-contents)

---

## 2. Spring AOP Advice Types (Lecture 23)

### 2.1 Setup & `@EnableAspectJAutoProxy`
Add the starter to `pom.xml`:
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-aop</artifactId>
</dependency>
```
In Spring Boot, `@EnableAspectJAutoProxy` is configured automatically. To declare an aspect, annotate a class with `@Aspect` and `@Component`:

```java
package com.raysi.aspect;

import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {
}
```

[⬆ Back to Top](#table-of-contents)

---

### 2.2 `@Before` Advice
Runs before the target method is invoked. Can inspect method arguments and signature via `JoinPoint`.

```java
package com.raysi.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import java.util.Arrays;

@Aspect
@Component
public class SecurityValidationAspect {

    @Before("execution(* com.raysi.service.StudentService.*(..))")
    public void logBeforeMethod(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        System.out.println("[BEFORE] Calling method: " + methodName + " with args: " + Arrays.toString(args));
    }
}
```
*Note*: If `@Before` throws an exception, the target method is **not** executed.

[⬆ Back to Top](#table-of-contents)

---

### 2.3 `@After` (Finally) Advice
Runs after the target method finishes, regardless of whether it succeeded or threw an exception (equivalent to a `finally` block).

```java
@After("execution(* com.raysi.service.StudentService.*(..))")
public void logAfterFinally(JoinPoint joinPoint) {
    System.out.println("[AFTER FINALLY] Method execution completed for: " + joinPoint.getSignature().getName());
}
```

[⬆ Back to Top](#table-of-contents)

---

### 2.4 `@AfterReturning` Advice & Return Value Capture
Runs only when the target method completes **successfully** without throwing an exception. Can capture and inspect the return value using the `returning` attribute.

```java
@AfterReturning(
    pointcut = "execution(* com.raysi.service.StudentService.getStudentById(..))",
    returning = "result"
)
public void logAfterSuccessfulReturn(JoinPoint joinPoint, Object result) {
    System.out.println("[AFTER RETURNING] Method " + joinPoint.getSignature().getName() 
            + " returned: " + result);
}
```

[⬆ Back to Top](#table-of-contents)

---

### 2.5 `@AfterThrowing` Advice & Exception Auditing
Runs only when the target method terminates abruptly by throwing an exception. Can capture the exception object using the `throwing` attribute.

```java
@AfterThrowing(
    pointcut = "execution(* com.raysi.service.StudentService.*(..))",
    throwing = "ex"
)
public void logException(JoinPoint joinPoint, Throwable ex) {
    System.err.println("[AFTER THROWING] Method " + joinPoint.getSignature().getName() 
            + " threw exception: " + ex.getMessage());
}
```

[⬆ Back to Top](#table-of-contents)

---

### 2.6 `@Around` Advice & `ProceedingJoinPoint`
The most comprehensive and powerful advice type. It wraps the target method invocation completely.
- Must accept `ProceedingJoinPoint` as its first parameter.
- Controls whether and when the target method executes via `joinPoint.proceed()`.
- Can modify arguments passed to the target method.
- Can alter the return value or catch/suppress exceptions.

```java
package com.raysi.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class PerformanceMonitoringAspect {

    @Around("execution(* com.raysi.service..*.*(..))")
    public Object profileMethod(ProceedingJoinPoint pjp) throws Throwable {
        long start = System.currentTimeMillis();
        String methodName = pjp.getSignature().toShortString();

        System.out.println("[AROUND - START] Executing " + methodName);
        try {
            // Proceed with actual target method execution
            Object result = pjp.proceed();
            return result;
        } finally {
            long duration = System.currentTimeMillis() - start;
            System.out.println("[AROUND - END] " + methodName + " executed in " + duration + " ms");
        }
    }
}
```

[⬆ Back to Top](#table-of-contents)

---

### 2.7 Advice Execution Order
When multiple advice types advise the same method, the lifecycle executes in this sequence:

```
Around Advice (Before proceed())
  └──> Before Advice
         └──> Target Method Execution
  ┌──> After Returning Advice (if success)
  │    OR
  │    After Throwing Advice (if exception)
  └──> After (Finally) Advice
Around Advice (After proceed() / finally)
```

[⬆ Back to Top](#table-of-contents)

---

## 3. Pointcut Expressions & Designators (Lecture 24)

### 3.1 Pointcut Syntax & Wildcards (`*` and `..`)
Pointcut expressions use AspectJ pointcut designator syntax:

```
execution(modifiers-pattern? ret-type-pattern declaring-type-pattern?name-pattern(param-pattern) throws-pattern?)
```

#### Wildcard Symbols:
- `*`: Matches any single element (any return type, any single package name, any method name).
- `..`:
  - In package patterns: Matches the package and **all sub-packages** recursively.
  - In parameter patterns: Matches **zero or more** parameters of any type.

[⬆ Back to Top](#table-of-contents)

---

### 3.2 The `execution()` Designator in Depth

| Pointcut Expression | Meaning |
| :--- | :--- |
| `execution(* *(..))` | Any method, any return type, any package, any parameters. |
| `execution(public * *(..))` | Any `public` method with any return type and parameters. |
| `execution(* com.raysi.service.*.*(..))` | Any method in any class directly inside package `com.raysi.service`. |
| `execution(* com.raysi.service..*.*(..))` | Any method in `com.raysi.service` or **any of its sub-packages**. |
| `execution(String com.raysi.service.StudentService.get*(..))` | Any method in `StudentService` returning `String` whose name starts with `get`. |
| `execution(* com.raysi..*.*(Long, ..))` | Any method where the **first parameter is a `Long`**. |

[⬆ Back to Top](#table-of-contents)

---

### 3.3 Package & Type Matching: `within()`
`within()` restricts matching to join points within certain types or packages:

```java
// Matches all method executions in the service package and its sub-packages
@Pointcut("within(com.raysi.service..*)")
public void serviceLayerPointcut() {}

// Matches all method executions strictly within StudentServiceImpl class
@Pointcut("within(com.raysi.service.impl.StudentServiceImpl)")
public void inStudentServiceImpl() {}
```

[⬆ Back to Top](#table-of-contents)

---

### 3.4 Argument Matching: `args()` & `@args()`
- `args()`: Matches methods based on runtime argument types (unlike `execution` which matches declared method signature).
- `@args()`: Matches methods where the runtime argument class has a specific class-level annotation.

```java
// Matches any method receiving a single Student model as its parameter
@Before("args(com.raysi.model.Student)")
public void processStudentArg(JoinPoint jp) {}
```

[⬆ Back to Top](#table-of-contents)

---

### 3.5 Target & Proxy Matching: `target()`, `this()`, and `bean()`
- `target(Type)`: Limits match to where the **target object** (actual bean behind proxy) is an instance of `Type`.
- `this(Type)`: Limits match to where the **AOP proxy** object is an instance of `Type`.
- `bean(idOrNamePattern)`: Limits match to Spring beans by ID or wildcard name.

```java
// Advises all beans whose name ends with 'Service'
@Before("bean(*Service)")
public void adviseAllServiceBeans() {}
```

[⬆ Back to Top](#table-of-contents)

---

### 3.6 Combining Pointcuts (`&&`, `||`, `!`) & Named Pointcuts with `@Pointcut`
Avoid repeating pointcut strings across advice methods by declaring reusable **Named Pointcuts**:

```java
package com.raysi.aspect;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class ArchitecturePointcuts {

    // Pointcut for all classes in controller package
    @Pointcut("within(com.raysi.controller..*)")
    public void inControllerLayer() {}

    // Pointcut for all classes in service package
    @Pointcut("within(com.raysi.service..*)")
    public void inServiceLayer() {}

    // Pointcut for all classes in repository package
    @Pointcut("within(com.raysi.repository..*)")
    public void inRepositoryLayer() {}

    // Composite: In business layers, excluding repository
    @Pointcut("inServiceLayer() && !inRepositoryLayer()")
    public void businessOperationsOnly() {}
}
```

[⬆ Back to Top](#table-of-contents)

---

## 4. Custom Annotations & AOP Parameter Binding (Lecture 25)

### 4.1 Declarative Interception: Separating "What" from "How"
Relying solely on package naming conventions (`within(..service..)`) is fragile:
- Moving a class breaks interception.
- Advising single specific methods across different packages becomes messy.

**Solution**: Use **Custom Annotations**.
- The business method declares *what* it needs by placing an annotation.
- The AOP aspect provides *how* it is executed behind the scenes.

[⬆ Back to Top](#table-of-contents)

---

### 4.2 Creating Custom Annotations (`@Target`, `@Retention`, `@Documented`)

```java
package com.raysi.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)              // Can only be placed on methods
@Retention(RetentionPolicy.RUNTIME)      // Retained in bytecode & visible to reflection at runtime
@Documented                              // Included in JavaDoc
public @interface TrackExecutionTime {
    String action() default "DEFAULT_OPERATION";
}
```

[⬆ Back to Top](#table-of-contents)

---

### 4.3 Matching Annotations with `@annotation()`
Use the `@annotation()` designator to intercept any method annotated with your custom annotation:

```java
@Around("@annotation(com.raysi.annotation.TrackExecutionTime)")
public Object monitor(ProceedingJoinPoint pjp) throws Throwable {
    return pjp.proceed();
}
```

[⬆ Back to Top](#table-of-contents)

---

### 4.4 Binding Annotation & Method Parameters Directly into Advice
Instead of using reflection (`method.getAnnotation(...)`), Spring AOP allows binding the annotation object directly into advice parameters:

```java
package com.raysi.aspect;

import com.raysi.annotation.TrackExecutionTime;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class TrackExecutionTimeAspect {

    // Passing parameter name 'trackAnnotation' matches the advice argument directly!
    @Around("@annotation(trackAnnotation)")
    public Object profile(ProceedingJoinPoint joinPoint, TrackExecutionTime trackAnnotation) throws Throwable {
        long startTime = System.currentTimeMillis();
        
        System.out.println("Starting action: " + trackAnnotation.action());

        try {
            return joinPoint.proceed();
        } finally {
            long executionTime = System.currentTimeMillis() - startTime;
            System.out.println("Action [" + trackAnnotation.action() + "] executed in " + executionTime + " ms");
        }
    }
}
```

#### Applying the Annotation to Business Methods:
```java
package com.raysi.service;

import com.raysi.annotation.TrackExecutionTime;
import org.springframework.stereotype.Service;

@Service
public class ReportService {

    @TrackExecutionTime(action = "GENERATE_ANNUAL_FINANCIAL_REPORT")
    public void generateAnnualReport() throws InterruptedException {
        // Core business logic
        Thread.sleep(1200);
        System.out.println("Report calculation complete.");
    }
}
```

[⬆ Back to Top](#table-of-contents)
