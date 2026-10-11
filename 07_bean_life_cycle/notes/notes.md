# Spring Bean Lifecycle: Detailed Notes

The **Spring Bean Lifecycle** represents the complete journey of a Spring-managed object from the instant the IoC container discovers its configuration until the container shuts down and destroys it. Instead of manually constructing objects, populating dependencies, and releasing resources using plain Java, Spring manages these stages systematically.

```java
@Component
public class PaymentService {

    public PaymentService() {
        System.out.println("1. Instantiation: Constructor called");
    }

    @PostConstruct
    public void init() {
        System.out.println("2. Initialization: @PostConstruct called after DI");
    }

    @PreDestroy
    public void cleanup() {
        System.out.println("3. Destruction: @PreDestroy called before shutdown");
    }
}
```

> Memory aid: In a hotel, you don't build the room or discard the furniture when checking out — the staff prepares the room before you enter, serves your requests during your stay, and cleans up after you check out.

---

## Table of Contents

1. [What is Bean Lifecycle?](#1-what-is-bean-lifecycle)
2. [Simple Definition](#2-simple-definition)
3. [Complete Bean Lifecycle Flow](#3-complete-bean-lifecycle-flow)
4. [Step 0: Spring Container Starts](#4-step-0-spring-container-starts)
5. [Step 1: Bean Definition is Read](#5-step-1-bean-definition-is-read)
6. [Step 2: Bean Object is Instantiated](#6-step-2-bean-object-is-instantiated)
7. [Step 3: Dependencies are Injected](#7-step-3-dependencies-are-injected)
8. [Step 4: Aware Interfaces are Called](#8-step-4-aware-interfaces-are-called)
9. [Step 5: Initialization Callbacks](#9-step-5-initialization-callbacks)
10. [Step 6: Bean is Ready to Use](#10-step-6-bean-is-ready-to-use)
11. [Step 7: Destruction Callbacks](#11-step-7-destruction-callbacks)
12. [Full Lifecycle Summary](#12-full-lifecycle-summary)
13. [Singleton Bean Lifecycle](#13-singleton-bean-lifecycle)
14. [Prototype Bean Lifecycle](#14-prototype-bean-lifecycle)
15. [Why Prototype Destruction Does Not Happen Automatically](#15-why-prototype-destruction-does-not-happen-automatically)
16. [Important Case: Singleton Depends on Prototype](#16-important-case-singleton-depends-on-prototype)
17. [@Lazy and Bean Lifecycle](#17-lazy-and-bean-lifecycle)
18. [@PostConstruct and Circular Dependency](#18-postconstruct-and-circular-dependency)
19. [Common Pitfalls](#19-common-pitfalls)
20. [Interview Questions](#20-interview-questions)
21. [Interview-Style Output Questions](#21-interview-style-output-questions)
22. [Quick Cheat Sheet](#22-quick-cheat-sheet)

---

## 1. What is Bean Lifecycle?

In standard Java, when we instantiate an object manually using `new`, we take on the burden of every stage of its lifecycle:
- Create the object instance
- Set properties and mandatory configurations
- Invoke setup or startup logic
- Release open resources when finished

```java
// Manual Java approach
PaymentService paymentService = new PaymentService();
paymentService.setApiKey("secret_key");
paymentService.connect();
// ... use paymentService ...
paymentService.disconnect();
```

In Spring, we delegate this orchestration to the Spring IoC Container:

```java
// Option A: Class-level annotation
@Component
public class PaymentService {
    // Managed automatically by Spring
}
```

```java
// Option B: Explicit configuration class
@Configuration
public class AppConfig {
    @Bean
    public PaymentService paymentService() {
        return new PaymentService();
    }
}
```

Once registered, Spring takes complete ownership to:
1. Discover the bean metadata
2. Instantiate the object instance
3. Inject collaborators and configurations
4. Trigger configured lifecycle callbacks
5. Keep the bean in service
6. Clean up and destroy it gracefully upon container shutdown

---

## 2. Simple Definition

> **Bean Lifecycle:** The complete end-to-end journey of a Spring-managed object from the exact moment Spring reads its definition until the container destroys and discards it.

Whenever we discuss the **Spring Bean Lifecycle**, we examine how Spring creates, prepares, manages, and cleans up beans.

---

## 3. Complete Bean Lifecycle Flow

```text
Spring Container Starts
        |
Reads Configuration / Annotations
        |
Creates BeanDefinition Objects
        |
Instantiates Bean Instance (Constructor)
        |
Injects Dependencies (DI)
        |
Calls Aware Interfaces (BeanNameAware, ApplicationContextAware, etc.)
        |
Runs BeanPostProcessor (postProcessBeforeInitialization)
        |
Executes Initialization Callbacks (@PostConstruct -> InitializingBean -> custom initMethod)
        |
Runs BeanPostProcessor (postProcessAfterInitialization)
        |
Bean is Ready for Use (Served to Application)
        |
Container Closes / Shuts Down
        |
Executes Destruction Callbacks (@PreDestroy -> DisposableBean -> custom destroyMethod)
        |
Bean is Removed / Disposed
```

---

## 4. Step 0: Spring Container Starts

When bootstrapping the Spring container:

```java
ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
```

Notice that we pass `AppConfig.class` (class metadata), not an instantiated object.  
Spring leverages Java **Reflection** to inspect the metadata and answer fundamental structural questions:
- Is this class decorated with `@Configuration`?
- Where is `@ComponentScan` instructed to look?
- Which `@Bean` producer methods are declared?
- Which package trees need scanning for `@Component`, `@Service`, or `@Repository`?
- What bean definitions need registration before instantiation begins?

---

## 5. Step 1: Bean Definition is Read

Before invoking constructors or allocating memory, Spring compiles bean metadata into internal **`BeanDefinition`** objects.

A `BeanDefinition` contains configuration details such as:
- **Bean Name:** `paymentService`
- **Bean Class:** `in.raysi.PaymentService`
- **Scope:** `singleton`, `prototype`, etc.
- **Lazy Init:** `true` or `false`
- **Dependencies:** Collaborator references to resolve later
- **Init Method:** Explicit method name to trigger post-injection
- **Destroy Method:** Cleanup method name to trigger on container close

```text
OrderService --------> PaymentService --------> PaymentGateway --------> ApiClient
```

Spring constructs this complete dependency graph in metadata first. Once it understands the overall graph, it determines the exact creation order.

### Sources of Bean Definitions

```java
// 1. Annotation-driven scanning
@Configuration
@ComponentScan(basePackages = "in.raysi")
public class AppConfig { }

@Component
public class PaymentService { }
```

```java
// 2. Java Configuration (@Bean)
@Configuration
public class AppConfig {
    @Bean
    public PaymentService paymentService() {
        return new PaymentService();
    }
}
```

```xml
<!-- 3. XML Configuration -->
<bean id="paymentService" class="in.raysi.PaymentService"/>
```

---

## 6. Step 2: Bean Object is Instantiated

Once definitions are registered, Spring instantiates the raw object via its constructor.

```java
@Component
public class PaymentService {
    public PaymentService() {
        System.out.println("PaymentService constructor called");
    }
}
```

```text
Instantiation  ===> Allocating memory and running the constructor (Raw Object)
Initialization ===> Running post-wiring setup logic after dependencies are set
```

> **Crucial Difference:** Instantiation creates the raw instance. Initialization prepares and primes the object after all dependencies have been injected.

---

## 7. Step 3: Dependencies are Injected

Once memory is allocated, Spring injects required collaborators.

### Constructor Injection
```java
@Component
public class OrderService {
    private final PaymentService paymentService;

    public OrderService(PaymentService paymentService) {
        this.paymentService = paymentService;
    }
}
```
*Flow:* Spring resolves or instantiates `PaymentService` first, then invokes `OrderService`'s constructor with that reference.

### Setter Injection
```java
@Component
public class OrderService {
    private PaymentService paymentService;

    @Autowired
    public void setPaymentService(PaymentService paymentService) {
        this.paymentService = paymentService;
    }
}
```
*Flow:* Spring creates `OrderService` via its default constructor, then invokes the setter method to assign `PaymentService`.

### Field Injection
```java
@Component
public class OrderService {
    @Autowired
    private PaymentService paymentService;
}
```
*Flow:* Spring creates `OrderService` via its no-arg constructor, then injects `PaymentService` directly using reflection.

---

## 8. Step 4: Aware Interfaces are Called

If a bean needs insight into container-level metadata, it implements Spring's **Aware** interfaces. Aware interfaces are callback hooks where the container injects its internal infrastructure into the bean.

### Common Aware Interfaces:
- `BeanNameAware`: Injects the configured bean name.
- `BeanFactoryAware`: Injects the enclosing `BeanFactory`.
- `ApplicationContextAware`: Injects the active `ApplicationContext`.

```java
@Component("customPaymentService")
public class CustomBean implements BeanNameAware, ApplicationContextAware {

    public CustomBean() {
        System.out.println("1. Constructor called");
    }

    @Override
    public void setBeanName(String name) {
        System.out.println("2. BeanNameAware called -> Bean name: " + name);
    }

    @Override
    public void setApplicationContext(ApplicationContext context) {
        System.out.println("3. ApplicationContextAware called -> Context: " + context.getClass().getSimpleName());
    }
}
```

```text
Output:
1. Constructor called
2. BeanNameAware called -> Bean name: customPaymentService
3. ApplicationContextAware called -> Context: AnnotationConfigApplicationContext
```

> **Why `setBeanName` instead of `getBeanName`?**  
> Because the communication flows from the **Spring Container $
ightarrow$ Bean**. Spring pushes the name into the bean.

Aware interfaces are primarily utilized when developing infrastructure libraries, logging frameworks, or specialized container extensions rather than core business domain logic.

---

## 9. Step 5: Initialization Callbacks

Once constructor execution, dependency injection, and Aware hooks complete, Spring allows the bean to run startup initialization logic (e.g., warming caches, validating configuration, opening external sockets).

### Option 1: `@PostConstruct` (Recommended Standard)

```java
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
public class PaymentService {

    @PostConstruct
    public void init() {
        System.out.println("Bean initialized via @PostConstruct");
    }
}
```

If developing on standalone Spring Core without Spring Boot, import the Jakarta Annotations API:

```xml
<dependency>
    <groupId>jakarta.annotation</groupId>
    <artifactId>jakarta.annotation-api</artifactId>
    <version>3.0.0</version>
</dependency>
```

### Option 2: `InitializingBean` Interface

```java
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

@Component
public class PaymentService implements InitializingBean {

    @Override
    public void afterPropertiesSet() {
        System.out.println("Bean initialized via InitializingBean.afterPropertiesSet()");
    }
}
```
*Trade-off:* Couples your business class directly to the Spring API.

### Option 3: Custom `initMethod` via `@Bean`

```java
@Configuration
public class AppConfig {
    @Bean(initMethod = "customInit")
    public PaymentService paymentService() {
        return new PaymentService();
    }
}

public class PaymentService {
    public void customInit() {
        System.out.println("Bean initialized via custom initMethod");
    }
}
```
*Trade-off:* Clean separation of code without Spring dependencies, but requires `@Bean` configuration.

### Initialization Callback Order

If all three mechanisms are configured simultaneously on the same bean, Spring executes them in this sequence:

```text
1. @PostConstruct
        |
2. InitializingBean.afterPropertiesSet()
        |
3. Custom initMethod
```

---

## 10. Step 6: Bean is Ready to Use

Once initialization concludes, the bean enters the **In-Service** state.

```text
Instantiated  ===>  Injected  ===>  Initialized  ===>  READY TO USE
```

- For **singleton** beans, Spring caches the reference in its internal singleton registry and serves that exact instance across all injection points.
- The bean can now safely handle concurrent business operations.

---

## 11. Step 7: Destruction Callbacks

When the container receives a termination or close signal (`context.close()`), it initiates graceful destruction. This allows beans to release file handles, close database connection pools, and shut down background thread executors.

### Option 1: `@PreDestroy` (Recommended Standard)

```java
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Component;

@Component
public class PaymentService {

    @PreDestroy
    public void cleanup() {
        System.out.println("Cleaned up via @PreDestroy");
    }
}
```

### Option 2: `DisposableBean` Interface

```java
import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Component;

@Component
public class PaymentService implements DisposableBean {

    @Override
    public void destroy() {
        System.out.println("Cleaned up via DisposableBean.destroy()");
    }
}
```

### Option 3: Custom `destroyMethod` via `@Bean`

```java
@Configuration
public class AppConfig {
    @Bean(destroyMethod = "customCleanup")
    public PaymentService paymentService() {
        return new PaymentService();
    }
}

public class PaymentService {
    public void customCleanup() {
        System.out.println("Cleaned up via custom destroyMethod");
    }
}
```

### Destruction Callback Order

If all three destruction mechanisms are defined on the same bean, Spring executes them in this sequence:

```text
1. @PreDestroy
        |
2. DisposableBean.destroy()
        |
3. Custom destroyMethod
```

---

## 12. Full Lifecycle Summary

```text
 1. Container startup triggered
 2. Configuration & annotations parsed
 3. BeanDefinition objects registered
 4. Bean instantiated (Constructor executed)
 5. Dependencies injected (DI resolved)
 6. Aware callbacks executed (BeanNameAware, ApplicationContextAware)
 7. BeanPostProcessor.postProcessBeforeInitialization()
 8. Initialization callbacks executed (@PostConstruct -> afterPropertiesSet -> initMethod)
 9. BeanPostProcessor.postProcessAfterInitialization()
10. Bean is ready to use in the application
11. Container receives shutdown command
12. Destruction callbacks executed (@PreDestroy -> destroy -> destroyMethod)
13. Bean instance discarded
```

---

## 13. Singleton Bean Lifecycle

By default, all beans in Spring are **singleton** scoped.

> **Singleton Scope:** Spring creates exactly one instance of the bean per Spring IoC Container and caches it.

For singleton beans, Spring oversees the entire lifecycle from start to finish:

```text
Spring Creates  --->  Spring Injects  --->  Spring Initializes  --->  Spring Caches/Serves  --->  Spring Destroys
```

```java
@Component
public class PaymentService {

    public PaymentService() {
        System.out.println("1. Constructor");
    }

    @PostConstruct
    public void init() {
        System.out.println("2. @PostConstruct");
    }

    @PreDestroy
    public void destroy() {
        System.out.println("3. @PreDestroy");
    }
}
```

When running:
```java
AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
// Output:
// 1. Constructor
// 2. @PostConstruct

context.close();
// Output:
// 3. @PreDestroy
```

---

## 14. Prototype Bean Lifecycle

> **Prototype Scope:** Spring creates a brand-new bean instance every time the bean is requested from the container.

```java
@Component
@Scope("prototype")
public class PaymentService {
    public PaymentService() {
        System.out.println("PaymentService created");
    }
}
```

```java
PaymentService p1 = context.getBean(PaymentService.class);
PaymentService p2 = context.getBean(PaymentService.class);
// p1 and p2 are two completely distinct instances in heap memory
```

### Prototype Lifecycle Execution Flow:

```text
Container Starts
       |
BeanDefinition Registered (No instance created at startup)
       |
Client Requests Bean (context.getBean or Injection)
       |
Instance Created (Constructor)
       |
Dependencies Injected
       |
Aware Callbacks Executed
       |
Initialization Callbacks Executed (@PostConstruct)
       |
Instance Handed to Caller
       |
Spring Stops Tracking Instance (No automated destruction!)
```

---

## 15. Why Prototype Destruction Does Not Happen Automatically

Consider what happens when defining `@PreDestroy` on a prototype bean:

```java
@Component
@Scope("prototype")
public class PaymentService {

    public PaymentService() {
        System.out.println("Constructor called");
    }

    @PostConstruct
    public void init() {
        System.out.println("@PostConstruct called");
    }

    @PreDestroy
    public void destroy() {
        System.out.println("@PreDestroy called");
    }
}
```

```java
AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
PaymentService p1 = context.getBean(PaymentService.class);
context.close();
```

```text
Output:
Constructor called
@PostConstruct called
(Notice: @PreDestroy is NEVER called!)
```

### Why Spring Does Not Call `@PreDestroy` for Prototypes:
1. **No Long-term Reference Tracking:** Once Spring instantiates, injects, and initializes a prototype bean, it hands the reference directly to the client and forgets about it.
2. **Preventing Memory Leaks:** If Spring held hard references to every prototype bean ever created, those objects could never be garbage-collected by the JVM, leading to severe memory leaks in high-throughput applications.

> **Rule:** If a prototype bean holds scarce system resources (such as active socket connections or open files), the calling code must release them manually.

---

## 16. Important Case: Singleton Depends on Prototype

A common architectural trap occurs when a singleton bean injects a prototype bean:

```java
@Component
@Scope("prototype")
public class PaymentSession {
    public PaymentSession() {
        System.out.println("PaymentSession instantiated");
    }
}

@Component
public class OrderService {
    private final PaymentSession paymentSession;

    public OrderService(PaymentSession paymentSession) {
        this.paymentSession = paymentSession;
    }
}
```

### The Problem:
- `OrderService` is a singleton; it is instantiated **only once** at container startup.
- During that single creation step, Spring resolves its dependencies, creating **one** instance of `PaymentSession`.
- `OrderService` holds onto that same `PaymentSession` forever.
- Even though `PaymentSession` is marked `@Scope("prototype")`, it will **not** be re-created on successive calls to `OrderService`.

### The Solution: `ObjectProvider<T>`

To obtain a brand-new prototype instance on demand, inject an `ObjectProvider<T>`:

```java
@Component
public class OrderService {
    private final ObjectProvider<PaymentSession> paymentSessionProvider;

    public OrderService(ObjectProvider<PaymentSession> paymentSessionProvider) {
        this.paymentSessionProvider = paymentSessionProvider;
    }

    public void processOrder() {
        // Fetches a fresh prototype instance each time this method executes
        PaymentSession session = paymentSessionProvider.getObject();
        System.out.println("Processing with session: " + session);
    }
}
```

---

## 17. @Lazy and Bean Lifecycle

By default, Spring singleton beans are **eagerly initialized** at application boot time.

```java
@Component
@Lazy
public class PaymentService {
    public PaymentService() {
        System.out.println("PaymentService created lazily");
    }
}
```

### How `@Lazy` Modifies the Lifecycle:
- **Startup:** Spring scans the class and registers its `BeanDefinition`, but defers object creation.
- **First Request:** When another bean requires it or `context.getBean()` is invoked, Spring triggers instantiation, injection, and initialization.
- **Post-Startup:** Once initialized, it behaves like a regular singleton and remains cached.

---

## 18. @PostConstruct and Circular Dependency

Constructor-based circular dependencies trigger an immediate runtime exception (`BeanCurrentlyInCreationException`):

```java
@Component
public class A {
    public A(B b) { } // Requires B
}

@Component
public class B {
    public B(A a) { } // Requires A -> DEADLOCK
}
```

### Resolving Circular Dependencies via Setter / `@PostConstruct`:

```java
@Component
public class A {
    private B b;

    public A(B b) {
        this.b = b;
    }

    @PostConstruct
    public void linkDependencies() {
        b.setA(this);
    }
}

@Component
public class B {
    private A a;

    public void setA(A a) {
        this.a = a;
    }
}
```

---

## 19. Common Pitfalls

| # | Pitfall | Reason / Symptom | Solution |
|---|---------|------------------|----------|
| 1 | Expecting `@PreDestroy` to execute on prototype beans | Spring does not track prototype instances after initialization | Clean up resources manually or manage them in the caller |
| 2 | Expecting a prototype bean inside a singleton to refresh per call | Injected only once when the parent singleton was constructed | Use `ObjectProvider<T>`, `@Lookup`, or request-scoped beans |
| 3 | Accessing injected fields inside the constructor | Fields injected via setter or field injection are still `null` inside constructor | Move post-dependency initialization logic into a `@PostConstruct` method |
| 4 | Relying on `@PostConstruct` with heavy long-running operations | Blocks the container startup thread, slowing boot times | Offload heavy warmup logic asynchronously or use application event listeners |
| 5 | Using Spring-specific interfaces (`InitializingBean`) needlessly | Tightly couples business domain code to the Spring framework | Prefer standard `@PostConstruct` and `@PreDestroy` annotations |

---

## 20. Interview Questions

**Q1. What is the fundamental difference between bean instantiation and bean initialization in Spring?**  
Instantiation is the creation of the raw object in JVM heap memory by invoking its constructor. Initialization occurs later, after all dependencies have been injected, where custom setup logic (such as `@PostConstruct`) executes to prime the bean for use.

---

**Q2. What is a `BeanDefinition`?**  
A `BeanDefinition` is an internal Spring metadata object describing a bean's configuration. It holds key attributes including the bean's class name, scope, lazy initialization flag, autowiring rules, constructor arguments, and init/destroy method hooks.

---

**Q3. Why does Spring not manage the destruction lifecycle of prototype beans?**  
Because prototype beans can be created repeatedly, retaining references to all created prototype instances for container-managed destruction would prevent garbage collection and lead to memory leaks. Spring relinquishes ownership immediately after initialization.

---

**Q4. What is the execution order of initialization callbacks in Spring?**  
1. Methods annotated with `@PostConstruct`  
2. `InitializingBean.afterPropertiesSet()`  
3. Custom `initMethod` defined in `@Bean`

---

**Q5. How can a singleton bean obtain a fresh prototype instance on every method call?**  
By using `ObjectProvider<T>`, Spring's `@Lookup` method injection, or by injecting the `ApplicationContext` directly and calling `getBean(...)`.

---

## 21. Interview-Style Output Questions

### Q1: Lifecycle Execution Sequence
```java
@Component
public class ServiceBean implements InitializingBean {

    public ServiceBean() {
        System.out.println("Constructor");
    }

    @PostConstruct
    public void postConstruct() {
        System.out.println("@PostConstruct");
    }

    @Override
    public void afterPropertiesSet() {
        System.out.println("afterPropertiesSet");
    }
}
```
**Answer:**
```text
Constructor
@PostConstruct
afterPropertiesSet
```

---

### Q2: Field Availability in Constructor vs `@PostConstruct`
```java
@Component
public class OrderService {

    @Autowired
    private PaymentService paymentService;

    public OrderService() {
        System.out.println("Constructor paymentService: " + paymentService);
    }

    @PostConstruct
    public void init() {
        System.out.println("@PostConstruct paymentService: " + paymentService);
    }
}
```
**Answer:**
```text
Constructor paymentService: null
@PostConstruct paymentService: in.raysi.PaymentService@<hashcode>
```
*Explanation:* Dependencies are injected *after* constructor execution. Inside the constructor, injected fields remain `null`. They are available once `@PostConstruct` executes.

---

### Q3: Prototype Destruction Behavior
```java
@Component
@Scope("prototype")
public class Worker {
    @PreDestroy
    public void cleanup() {
        System.out.println("Cleaning worker");
    }
}

public class App {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(AppConfig.class);
        Worker w = ctx.getBean(Worker.class);
        ctx.close();
    }
}
```
**Answer:**  
*(No console output printed)*  
*Explanation:* Spring does not invoke destruction callbacks on prototype beans when the container closes.

---

## 22. Quick Cheat Sheet

```text
PHASE 1: DEFINITION       Container reads annotations/XML -> Creates BeanDefinition registry
PHASE 2: INSTANTIATION    Raw object created via constructor reflection
PHASE 3: POPULATION       Dependencies injected (Constructor / Setter / Field)
PHASE 4: AWARE HOOKS      Container injects BeanNameAware, ApplicationContextAware, etc.
PHASE 5: INITIALIZATION   1. @PostConstruct
                          2. InitializingBean.afterPropertiesSet()
                          3. Custom initMethod
PHASE 6: IN-SERVICE       Bean serves requests (Singletons cached in registry)
PHASE 7: DESTRUCTION      1. @PreDestroy
                          2. DisposableBean.destroy()
                          3. Custom destroyMethod

SINGLETON LIFECYCLE       Complete: Instantiation -> Initialization -> In-Service -> Destruction
PROTOTYPE LIFECYCLE       Partial:  Instantiation -> Initialization -> Handed to caller (NO destruction)
@LAZY BEANS               BeanDefinition loaded at boot; creation deferred until first request
```

**Key Principles to Remember:**
1. Instantiation allocates memory; Initialization prepares the populated bean.
2. Dependencies are available inside `@PostConstruct`, never inside field/setter constructors.
3. Spring manages the entire lifecycle of singletons, but hands prototypes off to the caller.
4. Prefer standard `@PostConstruct` and `@PreDestroy` annotations over framework-coupled interfaces.

