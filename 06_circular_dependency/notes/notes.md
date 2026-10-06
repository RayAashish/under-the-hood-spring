# Spring Core: Circular Dependency, Bean Scopes & Bean Lifecycle

**Spring Core** manages the creation, configuration, dependency resolution, and complete lifecycle of components inside an enterprise application. Understanding dependency order, circular references, bean scoping models, and initialization lifecycles is crucial for writing robust and bug-free Spring applications.

```java
// Spring IoC Container manages bean scoping, proxying, and lifecycle events
@Service
@Scope("singleton")
public class PaymentService {

    @PostConstruct
    public void init() {
        System.out.println("PaymentService initialized and ready for use.");
    }

    @PreDestroy
    public void cleanup() {
        System.out.println("PaymentService resources released before shutdown.");
    }
}
```

> Memory aid: Think of the Spring Container as a general contractor. Before building the roof (consuming bean), the contractor must first pour the foundation and erect the supporting walls (dependencies). If the walls require the roof to stand up, you have a circular dependency deadlock.

---

## Table of Contents

1. [Quick Recap: DI, IoC and Beans](#1-quick-recap-di-ioc-and-beans)
2. [`@Configuration` as a Spring Component](#2-configuration-as-a-spring-component)
3. [Why Bean Creation Order Matters](#3-why-bean-creation-order-matters)
4. [What Is Circular Dependency?](#4-what-is-circular-dependency)
5. [Circular Dependency in Plain Java (StackOverflowError)](#5-circular-dependency-in-plain-java-stackoverflowerror)
6. [Why Constructor Injection Fails with Circular Dependency](#6-why-constructor-injection-fails-with-circular-dependency)
7. [Why Constructor Injection Remains the Recommended Default](#7-why-constructor-injection-remains-the-recommended-default)
8. [Setter and Field Injection with Circular Dependencies](#8-setter-and-field-injection-with-circular-dependencies)
9. [Spring's Early Reference Mechanism](#9-springs-early-reference-mechanism)
10. [Spring Boot 2.6+ Circular Dependency Restrictions](#10-spring-boot-26-circular-dependency-restrictions)
11. [Why Circular Dependency Is an Architectural Code Smell](#11-why-circular-dependency-is-an-architectural-code-smell)
12. [Best Ways to Refactor Circular Dependencies](#12-best-ways-to-refactor-circular-dependencies)
13. [Introduction to Bean Scopes](#13-introduction-to-bean-scopes)
14. [Singleton Scope (Default)](#14-singleton-scope-default)
15. [Spring Singleton vs Classic GoF Singleton Pattern](#15-spring-singleton-vs-classic-gof-singleton-pattern)
16. [Prototype Scope](#16-prototype-scope)
17. [When to Choose Singleton vs Prototype](#17-when-to-choose-singleton-vs-prototype)
18. [The Prototype-Inside-Singleton Gotcha](#18-the-prototype-inside-singleton-gotcha)
19. [Web-Aware Scopes: Request, Session, Application, WebSocket](#19-web-aware-scopes-request-session-application-websocket)
20. [Bean Initialization Models: Eager vs Lazy](#20-bean-initialization-models-eager-vs-lazy)
21. [Eager Initialization (Fail-Fast Advantage)](#21-eager-initialization-fail-fast-advantage)
22. [Lazy Initialization with `@Lazy`](#22-lazy-initialization-with-lazy)
23. [Bean-Level `@Lazy` vs Injection-Point `@Lazy` (Proxy Mechanism)](#23-bean-level-lazy-vs-injection-point-lazy-proxy-mechanism)
24. [Global Lazy Initialization in Spring Boot](#24-global-lazy-initialization-in-spring-boot)
25. [Common Lazy Initialization Interaction Scenarios](#25-common-lazy-initialization-interaction-scenarios)
26. [Using `@Lazy` to Break Circular Dependencies (Workaround)](#26-using-lazy-to-break-circular-dependencies-workaround)
27. [The Complete Spring Bean Lifecycle](#27-the-complete-spring-bean-lifecycle)
28. [Common Pitfalls](#28-common-pitfalls)
29. [Interview Questions](#29-interview-questions)
30. [Interview-Style Code Analysis & Output Questions](#30-interview-style-code-analysis--output-questions)
31. [Quick Cheat Sheet](#31-quick-cheat-sheet)

---

## 1. Quick Recap: DI, IoC and Beans

Before diving into circular dependencies, scopes, and lifecycle events, three foundational pillars must be crystal clear:

| Concept | Meaning & Core Responsibility |
| :--- | :--- |
| **DI (Dependency Injection)** | Design pattern where collaborators and resources are supplied to a class from outside rather than instantiated internally. |
| **IoC (Inversion of Control)** | Architectural principle where control over object lifecycle, configuration, and flow is transferred from developer code to the Spring container. |
| **Spring Bean** | A Java object that is instantiated, configured, assembled, and managed entirely by the Spring IoC Container. |

In Spring applications, classes annotated with stereotype annotations (`@Component`, `@Service`, `@Repository`, `@Controller`) or factory methods annotated with `@Bean` inside configuration classes are automatically registered as beans.

---

## 2. `@Configuration` as a Spring Component

In Spring, `@Configuration` denotes classes that declare one or more `@Bean` methods:

```java
@Configuration
public class AppConfig {

    @Bean
    public PaymentService paymentService() {
        return new PaymentService();
    }
}
```

### Key Insight:
`@Configuration` is meta-annotated with `@Component`:
```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface Configuration { ... }
```
Because of this inheritance, every `@Configuration` class is itself auto-detected during component scanning, registered as a Spring bean, and enhanced via CGLIB proxies to enforce singleton semantics on `@Bean` calls.

---

## 3. Why Bean Creation Order Matters

Spring does not instantiate beans in arbitrary order. **Bean instantiation strictly follows dependency resolution order (Directed Acyclic Graph - DAG).**

Consider a standard enterprise service chain:
```text
OrderController  ==> depends on ==>  OrderService
OrderService     ==> depends on ==>  PaymentService
PaymentService   ==> depends on ==>  PaymentGateway
```

To instantiate `OrderService`, Spring requires an already initialized `PaymentService`.  
To instantiate `PaymentService`, Spring requires an already initialized `PaymentGateway`.

```text
CONSTRUCTION EXECUTION ORDER:
[1. PaymentGateway] ---> [2. PaymentService] ---> [3. OrderService] ---> [4. OrderController]
```

Spring always resolves from the leaves up to the root consumers.

---

## 4. What Is Circular Dependency?

A **Circular Dependency** occurs when two or more beans depend on each other directly or transitively, creating an unbroken dependency loop:

```text
DIRECT CIRCULAR LOOP:
+-------------------+                    +--------------------+
|   OrderService    |  ===============>  |   PaymentService   |
| (needs Payment)   |  <===============  |   (needs Order)    |
+-------------------+                    +--------------------+
```

```java
@Service
public class OrderService {
    private final PaymentService paymentService;

    public OrderService(PaymentService paymentService) {
        this.paymentService = paymentService;
    }
}

@Service
public class PaymentService {
    private final OrderService orderService;

    public PaymentService(OrderService orderService) {
        this.orderService = orderService;
    }
}
```

### The Deadlock:
- To construct `OrderService`, Spring needs `PaymentService`.
- To construct `PaymentService`, Spring needs `OrderService`.
- There is no valid leaf node or entry point to begin instantiation.

---

## 5. Circular Dependency in Plain Java (StackOverflowError)

Circular dependency is not a Spring-specific issue; it is a fundamental object-oriented design flaw that crashes plain Java as well:

```java
public class A {
    private B b = new B();
}

public class B {
    private A a = new A();
}

public class Main {
    public static void main(String[] args) {
        A objA = new A(); // Throws StackOverflowError
    }
}
```

```text
EXECUTION CALL STACK:
new A()
  └── new B()
        └── new A()
              └── new B() ... -> StackOverflowError
```

Java runs out of call stack frames because recursive constructor invocations never terminate. Spring simply intercepts and exposes this design flaw at startup time.

---

## 6. Why Constructor Injection Fails with Circular Dependency

Constructor injection enforces a strict JVM guarantee:
> **An object cannot be instantiated in memory until its constructor completes, and a constructor cannot complete until all its arguments are supplied.**

When `OrderService` and `PaymentService` both demand each other in their constructors:
```text
Spring attempts: create OrderService -> asks for PaymentService
Spring attempts: create PaymentService -> asks for OrderService
Spring attempts: create OrderService -> asks for PaymentService
... [DEADLOCK]
```

Spring detects this cycle during application startup and halts initialization, throwing:
```text
org.springframework.beans.factory.BeanCurrentlyInCreationException:
Error creating bean with name 'orderService': Requested bean is currently in creation:
Is there an unresolvable circular reference?
```

---

## 7. Why Constructor Injection Remains the Recommended Default

Even though constructor injection strictly forbids circular references, it remains the gold standard in production:

1. **Guaranteed Immutability:** Fields can be marked `final`, ensuring thread safety after construction.
2. **Defensive against Incomplete State:** Eliminates classes existing in partially initialized states.
3. **Explicit Visibility:** Dependencies are obvious to tests without requiring reflection.
4. **Early Architecture Alarm:** It loudly fails when circular dependencies occur, forcing developers to clean up bad architecture.

Constructor injection does not create the flaw; it exposes bad coupling early.

---

## 8. Setter and Field Injection with Circular Dependencies

Unlike constructor injection, **Setter and Field Injection decouple object instantiation from dependency injection**:

```java
@Service
public class OrderService {
    private PaymentService paymentService;

    @Autowired
    public void setPaymentService(PaymentService paymentService) {
        this.paymentService = paymentService;
    }
}

@Service
public class PaymentService {
    private OrderService orderService;

    @Autowired
    public void setOrderService(OrderService orderService) {
        this.orderService = orderService;
    }
}
```

### Multi-Step Resolution Process:
```text
Step 1: Allocate & call default constructor for OrderService (empty object in memory)
Step 2: Allocate & call default constructor for PaymentService (empty object in memory)
Step 3: Inject PaymentService reference into OrderService setter
Step 4: Inject OrderService reference into PaymentService setter
```

Because default constructors run without parameters, Spring can separate instantiation from wiring.

---

## 9. Spring's Early Reference Mechanism

To resolve circular dependencies with setter/field injection for singletons, Spring uses a **3-Level Cache** mechanism:

```text
+--------------------------------------------------------------------------+
|                     SPRING 3-LEVEL CACHE ARCHITECTURE                    |
|                                                                          |
| 1. singletonObjects (1st Level Cache): Fully initialized, ready beans    |
| 2. earlySingletonObjects (2nd Level Cache): Raw/early unpopulated beans  |
| 3. singletonFactories (3rd Level Cache): ObjectFactory generating proxies|
+--------------------------------------------------------------------------+
```

```java
// Conceptual sequence:
A a = new A();     // 1. Raw object created
exposeEarlyRef(a); // 2. Exposed in early cache
B b = new B();     // 3. B instantiated
b.setA(a);         // 4. Injects early uninitialized reference of A into B
a.setB(b);         // 5. Injects completed B into A
```

> **Definition:** An **Early Reference** is an unpopulated object reference exposed after constructor invocation but before dependency population and `@PostConstruct` initialization.

---

## 10. Spring Boot 2.6+ Circular Dependency Restrictions

Historically, Spring silently resolved setter/field circular dependencies via its early reference cache.

However, starting in **Spring Boot 2.6.0**, circular dependencies are **forbidden by default**:
```properties
# Default configuration in Spring Boot 2.6+
spring.main.allow-circular-references=false
```

If a cycle is detected, the boot sequence immediately halts with a descriptive error message:
```text
***************************
APPLICATION FAILED TO START
***************************
Description:
The dependencies of some of the beans in the application context form a cycle:
┌─────┐
|  orderService (field private PaymentService)
↑     ↓
|  paymentService (field private OrderService)
└─────┘
```

You can bypass this check:
```properties
spring.main.allow-circular-references=true
```
**Engineering Warning:** Re-enabling this setting is an anti-pattern. It should only be used as a legacy stopgap, never in greenfield software.

---

## 11. Why Circular Dependency Is an Architectural Code Smell

Circular dependencies almost always signify violated design principles:
1. **Broken Single Responsibility Principle (SRP):** Classes do too many things and know too much about each other.
2. **Missing Domain Abstractions:** An intermediate domain concept or coordinator is missing.
3. **High Coupling:** Neither component can be reused, tested, or reasoned about in isolation.

```text
SMELL: OrderService <===> PaymentService (Two-way tight dependency)
```

---

## 12. Best Ways to Refactor Circular Dependencies

| Refactoring Technique | Description |
| :--- | :--- |
| **Extract Mediator / Orchestrator** | Introduce a third service (e.g., `CheckoutCoordinator`) that calls both `OrderService` and `PaymentService`. |
| **Invert Dependency with Interfaces** | Make one service depend on an abstraction rather than the concrete counterpart. |
| **Event-Driven Architecture** | Replace direct invocation with decoupled application events (`ApplicationEventPublisher`). |
| **Extract Shared Logic** | Move shared data structures or lookup logic into a distinct repository or utility service. |

```text
CLEAN MEDIATOR PATTERN:
                +---------------------+
                | CheckoutCoordinator |
                +---------------------+
                   /                                 v                 v
          +--------------+   +----------------+
          | OrderService |   | PaymentService |
          +--------------+   +----------------+
                 (No circular reference between services!)
```

---

## 13. Introduction to Bean Scopes

> **Bean Scope** dictates how many instances of a bean Spring will create, how long those instances live, and how they are shared across the application context.

```text
                          SPRING BEAN SCOPES
   +-------------------------------+-------------------------------+
   |                               |                               |
   v                               v                               v
Core Scopes:                   Web Scopes:                     Advanced:
- singleton (Default)          - request                       - custom scopes
- prototype                    - session
                               - application
                               - websocket
```

---

## 14. Singleton Scope (Default)

> **Singleton Scope** ensures that the container creates **exactly one shared instance** per bean definition within that Spring container.

```java
@Component
@Scope("singleton") // Optional: singleton is the default
public class PaymentService {
}
```

```java
ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
PaymentService p1 = context.getBean(PaymentService.class);
PaymentService p2 = context.getBean(PaymentService.class);

System.out.println(p1 == p2); // Prints: true (Same memory reference)
```

- Instantiated eagerly during startup (by default).
- Shared across all threads and consumers.
- Must remain **stateless** or thread-safe to avoid race conditions.

---

## 15. Spring Singleton vs Classic GoF Singleton Pattern

| Dimension | GoF Singleton Pattern | Spring Singleton Scope |
| :--- | :--- | :--- |
| **Scope Boundary** | Exactly one object per JVM `ClassLoader` | Exactly one object per **Bean Definition** in a container |
| **Instantiation** | Enforced via `private` constructor | Normal `public` constructor, managed by Spring container |
| **Testability** | Hard to mock/test due to static state | Easily mocked; regular POJO injected via constructors |
| **Multiple Instances** | Impossible without reflection hacks | Possible if multiple bean definitions are declared for the same class |

```java
@Configuration
public class Config {
    @Bean public User userOne() { return new User(); }
    @Bean public User userTwo() { return new User(); }
}
// Results in two distinct singleton beans for the same User class!
```

---

## 16. Prototype Scope

> **Prototype Scope** instructs Spring to create a **brand-new object instance** every single time the bean is requested from the container.

```java
@Component
@Scope("prototype")
public class OrderRequest {
    public OrderRequest() {
        System.out.println("OrderRequest instance created");
    }
}
```

```java
ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
OrderRequest r1 = context.getBean(OrderRequest.class);
OrderRequest r2 = context.getBean(OrderRequest.class);

System.out.println(r1 == r2); // Prints: false
```

- Behaves similarly to manual `new` invocations, but benefits from container dependency injection.
- Spring does **not** manage the full lifecycle of prototype beans (destruction lifecycle callbacks are omitted).

---

## 17. When to Choose Singleton vs Prototype

| Scope | Best For | Examples |
| :--- | :--- | :--- |
| **Singleton** | Stateless services, repositories, controllers, shared behavior | `OrderService`, `UserRepository`, `EmailSender` |
| **Prototype** | Stateful components holding per-operation transient data | Dynamic calculators, command tasks, state machines |

---

## 18. The Prototype-Inside-Singleton Gotcha

### The Problem:
What happens if you inject a `@Scope("prototype")` bean into a `@Scope("singleton")` bean?

```java
@Component
@Scope("prototype")
public class TokenGenerator {
    private final String token = UUID.randomUUID().toString();
    public String getToken() { return token; }
}

@Service // Singleton by default
public class AuthService {
    private final TokenGenerator tokenGenerator;

    public AuthService(TokenGenerator tokenGenerator) {
        this.tokenGenerator = tokenGenerator;
    }

    public void authenticate() {
        System.out.println("Token: " + tokenGenerator.getToken());
    }
}
```

### The Output Reality:
Every call to `authService.authenticate()` prints the **exact same token**.

### Why?
The singleton `AuthService` is instantiated **only once** at container startup. Spring resolves its dependencies at that moment, injecting a single prototype instance. After that, `AuthService` holds that reference forever.

### Solutions:
1. Use **`ObjectProvider<TokenGenerator>`** or `Provider<T>`:
```java
@Autowired
private ObjectProvider<TokenGenerator> tokenProvider;

public void authenticate() {
    TokenGenerator freshToken = tokenProvider.getObject(); // Returns new instance every call
}
```
2. Use **`@Lookup` method injection**.
3. Use a scoped proxy: `@Scope(value = "prototype", proxyMode = ScopedProxyMode.TARGET_CLASS)`.

---

## 19. Web-Aware Scopes: Request, Session, Application, WebSocket

Available strictly within web-aware Spring contexts (e.g., Spring MVC, Spring Boot Web):

| Web Scope | Lifecycle Boundary | Typical Use Case |
| :--- | :--- | :--- |
| **`request`** | Single HTTP request-response cycle | Per-request tracing, API audit payloads |
| **`session`** | Single HTTP user session | User profile cache, shopping carts |
| **`application`** | Lifespan of the `ServletContext` | Web-wide configuration settings |
| **`websocket`** | Lifespan of a WebSocket session | Real-time chat channel context |

---

## 20. Bean Initialization Models: Eager vs Lazy

| Initialization Model | When Bean Is Created | Default For |
| :--- | :--- | :--- |
| **Eager Initialization** | At application startup when context boots | Singletons |
| **Lazy Initialization** | On-demand upon first method call or retrieval | Prototypes, or beans with `@Lazy` |

---

## 21. Eager Initialization (Fail-Fast Advantage)

By default, Spring pre-instantiates all singleton beans eagerly:

```java
@Component
public class PaymentGateway {
    public PaymentGateway() {
        System.out.println("PaymentGateway initialized at startup");
    }
}
```

### Why Fail-Fast Matters:
If a class contains a bad configuration string, a missing database URL, or an unresolvable constructor dependency, the application **fails immediately during bootstrap**.  
This guarantees you detect issues in CI/CD or staging before end users hit production endpoints.

---

## 22. Lazy Initialization with `@Lazy`

Annotating a bean with `@Lazy` delays instantiation until it is explicitly requested:

```java
@Component
@Lazy
public class HeavyReportService {
    public HeavyReportService() {
        System.out.println("HeavyReportService allocated in memory");
    }

    public void generate() {
        System.out.println("Generating heavy enterprise PDF");
    }
}
```

```java
// Startup completed: HeavyReportService is NOT yet created
HeavyReportService report = context.getBean(HeavyReportService.class);
// "HeavyReportService allocated in memory" prints now!
report.generate();
```

---

## 23. Bean-Level `@Lazy` vs Injection-Point `@Lazy` (Proxy Mechanism)

### 1. Bean-Level `@Lazy`
```java
@Component
@Lazy
public class ReportService { }
```
Prevents creation during bootstrap unless an eager singleton directly depends on it.

### 2. Injection-Point `@Lazy` (Spring Proxy Magic)
```java
@Service
public class UserService {
    private final EmailService emailService;

    public UserService(@Lazy EmailService emailService) {
        this.emailService = emailService;
    }
}
```

When `@Lazy` is placed at the injection point:
- Spring injects a lightweight **CGLIB Proxy** instead of the real bean.
- The real `EmailService` is **not instantiated**.
- The moment `emailService.sendEmail()` is invoked, the proxy intercepts the call, instantiates the target `EmailService`, and delegates the invocation.

---

## 24. Global Lazy Initialization in Spring Boot

You can force all beans across your Spring Boot application to initialize lazily:

```properties
spring.main.lazy-initialization=true
```

Or via `application.yml`:
```yaml
spring:
  main:
    lazy-initialization: true
```

### Trade-offs:
- **Pros:** Drastically cuts application startup time (useful in local dev and testing).
- **Cons:** Eliminates fail-fast benefits; the first HTTP request hits latency spikes and may crash if a dependency is misconfigured.

To exempt critical beans from global lazy initialization:
```java
@Component
@Lazy(false) // Forces eager initialization even when global lazy is true
public class CriticalSecurityFilter { }
```

---

## 25. Common Lazy Initialization Interaction Scenarios

```text
1. Eager Bean depends on Lazy Bean:
   [Eager Bean] ===(requires)===> [Lazy Bean]
   Result: Lazy Bean is forced to initialize eagerly at startup!
   Fix: Add @Lazy to the injection point inside Eager Bean.

2. Lazy Bean depends on Eager Bean:
   [Lazy Bean] ===(requires)===> [Eager Bean]
   Result: Eager Bean initializes at startup. Lazy Bean remains uninstantiated.

3. Lazy Bean depends on Lazy Bean:
   [Lazy Bean A] ===(requires)===> [Lazy Bean B]
   Result: Neither initializes at startup. Both initialize when A is first called.
```

---

## 26. Using `@Lazy` to Break Circular Dependencies (Workaround)

When dealing with legacy code where circular constructor dependencies cannot be immediately refactored, `@Lazy` on an injection point breaks the cycle:

```java
@Service
public class OrderService {
    private final PaymentService paymentService;

    // Breaks deadlock: Injects a CGLIB proxy instead of waiting for real PaymentService
    public OrderService(@Lazy PaymentService paymentService) {
        this.paymentService = paymentService;
    }
}

@Service
public class PaymentService {
    private final OrderService orderService;

    public PaymentService(OrderService orderService) {
        this.orderService = orderService;
    }
}
```

```text
EXECUTION RESOLUTION:
1. Spring builds OrderService using a dynamic proxy for PaymentService.
2. OrderService is now fully created in memory!
3. Spring builds PaymentService, injecting the real OrderService.
4. When OrderService calls paymentService, the proxy delegates to the real bean.
```

> **Warning:** This is a diagnostic workaround, not good architecture. Always prioritize refactoring.

---

## 27. The Complete Spring Bean Lifecycle

Every bean managed by Spring undergoes a standardized, predictable sequence of phases:

```text
               SPRING BEAN COMPLETE LIFECYCLE
+--------------------------------------------------------------+
| 1. Bean Definition Discovery (@Component, @Bean, XML)        |
+--------------------------------------------------------------+
                               |
                               v
+--------------------------------------------------------------+
| 2. Instantiation (Constructor called via reflection)          |
+--------------------------------------------------------------+
                               |
                               v
+--------------------------------------------------------------+
| 3. Dependency Injection (Populate properties, setters, fields)|
+--------------------------------------------------------------+
                               |
                               v
+--------------------------------------------------------------+
| 4. Aware Interfaces (BeanNameAware, ApplicationContextAware)  |
+--------------------------------------------------------------+
                               |
                               v
+--------------------------------------------------------------+
| 5. BeanPostProcessor: postProcessBeforeInitialization        |
+--------------------------------------------------------------+
                               |
                               v
+--------------------------------------------------------------+
| 6. Initialization Phase:                                      |
|    - @PostConstruct method runs                              |
|    - InitializingBean.afterPropertiesSet() runs              |
|    - Custom init-method executes                             |
+--------------------------------------------------------------+
                               |
                               v
+--------------------------------------------------------------+
| 7. BeanPostProcessor: postProcessAfterInitialization (Proxies)|
+--------------------------------------------------------------+
                               |
                               v
+==============================================================+
| 8. BEAN IS READY TO USE IN APPLICATION                       |
+==============================================================+
                               |
                               v (Container Shutdown)
+--------------------------------------------------------------+
| 9. Destruction Phase (Singleton beans only):                 |
|    - @PreDestroy method runs                                 |
|    - DisposableBean.destroy() runs                           |
|    - Custom destroy-method executes                          |
+--------------------------------------------------------------+
```

```java
@Component
public class LifecycleDemoBean implements InitializingBean, DisposableBean {

    public LifecycleDemoBean() {
        System.out.println("Step 1: Constructor called");
    }

    @PostConstruct
    public void postConstruct() {
        System.out.println("Step 2: @PostConstruct initialization");
    }

    @Override
    public void afterPropertiesSet() {
        System.out.println("Step 3: InitializingBean.afterPropertiesSet()");
    }

    @PreDestroy
    public void preDestroy() {
        System.out.println("Step 4: @PreDestroy cleanup");
    }

    @Override
    public void destroy() {
        System.out.println("Step 5: DisposableBean.destroy()");
    }
}
```

---

## 28. Common Pitfalls

| # | Pitfall | Consequence | Proper Solution |
| :--- | :--- | :--- | :--- |
| 1 | **Relying on `@Lazy` to ignore circular dependencies** | Masking architectural decay; proxies add minor overhead and hide poor boundaries | Refactor mutual dependencies via mediator classes or domain events |
| 2 | **Assuming `@Scope("prototype")` injects a new object per singleton call** | Stale state bugs; singleton captures only one instance at boot | Use `ObjectProvider<T>` or method lookup injection |
| 3 | **Expecting Spring to call `@PreDestroy` on Prototype beans** | Memory leaks or dangling file handles | Manually trigger cleanup logic or use destruction BeanPostProcessors |
| 4 | **Enabling `spring.main.allow-circular-references=true` in production** | Creates hidden order-dependent initialization bugs | Fix the root architecture; keep circular references disabled |
| 5 | **Putting mutable state inside a Singleton Bean** | Race conditions and thread-safety bugs under concurrent web loads | Keep singletons stateless, or guard state with concurrent data structures |

---

## 29. Interview Questions

**Q1. Why does constructor injection fail with circular dependencies while setter injection succeeds?**  
Constructor injection requires all collaborator beans to exist in memory before the constructor can finish executing. If two classes require each other in their constructors, neither can be instantiated. Setter injection splits object creation from dependency wiring: Spring instantiates both empty objects via default constructors first, and then invokes setters to wire them up using its early reference cache.

---

**Q2. What is the difference between `@PostConstruct` and writing initialization logic inside a constructor?**  
Inside a constructor, dependencies injected via field or setter injection are not yet available (they are still `null`). By contrast, `@PostConstruct` runs strictly after all dependencies have been injected, making it safe to run configuration validation, resource initialization, and database warmups.

---

**Q3. Does Spring manage the complete lifecycle of prototype beans?**  
No. Spring instantiates, configures, and injects dependencies into prototype beans, and executes initialization methods (`@PostConstruct`). However, Spring does **not** track or manage the destruction lifecycle of prototypes. The client code is responsible for destroying prototype instances and cleaning up resources.

---

**Q4. How does `@Lazy` break a constructor-based circular dependency?**  
When `@Lazy` is applied to a constructor parameter, Spring generates and injects a dynamic CGLIB runtime proxy instead of constructing the real dependency immediately. This allows the host bean to finish its constructor execution. The real dependency is only instantiated when a method on the proxy is first invoked.

---

**Q5. What happens if an eager singleton bean depends on a lazy singleton bean?**  
The lazy singleton bean will be instantiated eagerly during startup. Spring must satisfy all direct dependencies of eager singletons before the container boot sequence can complete. To prevent this, `@Lazy` must also be placed at the injection site in the eager bean.

---

## 30. Interview-Style Code Analysis & Output Questions

### Question 1: Predict the Console Output
```java
@Component
class Alpha {
    public Alpha() { System.out.println("Alpha constructed"); }
    @PostConstruct public void init() { System.out.println("Alpha postConstruct"); }
}

@Component
class Beta {
    private final Alpha alpha;
    public Beta(Alpha alpha) {
        this.alpha = alpha;
        System.out.println("Beta constructed");
    }
}
```
**Answer:**
```text
Alpha constructed
Alpha postConstruct
Beta constructed
```
*Explanation:* Spring must fully instantiate and initialize `Alpha` (including its `@PostConstruct` phase) before passing it into `Beta`'s constructor.

---

### Question 2: Detect the Bug
```java
@Service
public class OrderService {
    @Autowired
    private PaymentService paymentService;

    public OrderService() {
        paymentService.validateConfiguration();
    }
}
```
**Answer:**  
Throws a `NullPointerException` at startup.  
*Explanation:* In the constructor, field injection has not occurred yet; `paymentService` is `null`. The call to `validateConfiguration()` must be moved into a method annotated with `@PostConstruct`.

---

### Question 3: Predict the Identity Comparison
```java
@Configuration
public class MyConfig {
    @Bean
    public Counter counterA() { return new Counter(); }

    @Bean
    public Counter counterB() { return new Counter(); }
}
```
```java
Counter c1 = context.getBean("counterA", Counter.class);
Counter c2 = context.getBean("counterA", Counter.class);
Counter c3 = context.getBean("counterB", Counter.class);

System.out.println(c1 == c2);
System.out.println(c1 == c3);
```
**Answer:**
```text
true
false
```
*Explanation:* Spring singletons are unique per **bean definition / bean name**, not per class. `counterA` always yields the same singleton instance (`c1 == c2`), whereas `counterB` is a separate bean definition with its own singleton instance.

---

## 31. Quick Cheat Sheet

```text
CIRCULAR DEPENDENCY        A -> B and B -> A
                           - Fails by default in Spring Boot 2.6+
                           - Constructor injection always fails (BeanCurrentlyInCreationException)
                           - Workaround: @Lazy on constructor injection point
                           - Proper Solution: Refactor via mediator or events

BEAN SCOPES                - singleton: ONE instance per bean definition in container (Default)
                           - prototype: NEW instance on every getBean() or injection
                           - request: ONE per HTTP request
                           - session: ONE per user HTTP session

INITIALIZATION             - Eager: Created at startup; enables fail-fast error detection (Default)
                           - Lazy: Created on first access via @Lazy; reduces startup memory

LIFECYCLE ORDER            1. Constructor
                           2. Dependency Injection (Setters/Fields)
                           3. Aware Interfaces
                           4. BeanPostProcessor (BeforeInit)
                           5. @PostConstruct / InitializingBean
                           6. BeanPostProcessor (AfterInit - Proxies created here)
                           7. Bean is ready
                           8. @PreDestroy / DisposableBean (On shutdown)
```

**Key Takeaways to Remember:**
1. Spring singletons are scoped to the **IoC Container definition**, not the whole JVM.
2. Keep singleton beans **stateless** and thread-safe.
3. Constructor injection enforces clean, acyclic architectures by deliberately failing when cycles occur.
4. `@PostConstruct` is the correct place to run startup validation, never the constructor.

