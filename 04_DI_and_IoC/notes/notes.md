# Spring Core: Dependency Injection, IoC & Loosely Coupled Design

**Spring Core** provides the foundational runtime environment and design principles that enable scalable, maintainable, and decoupled enterprise Java applications. At its foundation, it automates object lifecycle management, inverts control flow, and cleanly injects dependencies to eliminate tight coupling.

```java
// Loosely coupled architecture powered by Spring Dependency Injection
@Service
public class OrderService {
    private final NotificationService notificationService;

    // Dependency is injected from outside (Constructor Injection)
    @Autowired
    public OrderService(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    public void completeOrder() {
        notificationService.sendNotification("Order confirmed!");
    }
}
```

> Memory aid: In a restaurant, a chef doesn't grow vegetables or forge cooking pans — suppliers deliver them from outside. The chef simply specifies what is needed to fulfill orders.

Java provides building blocks like classes, objects, interfaces, and packages, but Spring provides the wiring engine to manage them effortlessly at scale.

---

## Table of Contents

1. [Why Do We Need Spring Core?](#1-why-do-we-need-spring-core)
2. [Understanding Dependencies in Software Design](#2-understanding-dependencies-in-software-design)
3. [The Problem: Tight Coupling](#3-the-problem-tight-coupling)
4. [Tight Coupling Illustrated in Code](#4-tight-coupling-illustrated-in-code)
5. [What Exactly Is Tight Coupling?](#5-what-exactly-is-tight-coupling)
6. [First Step: Introducing Interfaces](#6-first-step-introducing-interfaces)
7. [Why an Interface Alone Is Not Enough](#7-why-an-interface-alone-is-not-enough)
8. [The Solution: Dependency Injection (DI)](#8-the-solution-dependency-injection-di)
9. [Externalizing Object Creation](#9-externalizing-object-creation)
10. [Formal Definition of Dependency Injection](#10-formal-definition-of-dependency-injection)
11. [Core Benefits of Dependency Injection](#11-core-benefits-of-dependency-injection)
12. [Types of Dependency Injection](#12-types-of-dependency-injection)
13. [Inversion of Control (IoC) Explained](#13-inversion-of-control-ioc-explained)
14. [Direct Comparison: Normal Control vs Inverted Control](#14-direct-comparison-normal-control-vs-inverted-control)
15. [Relationship Between IoC and DI](#15-relationship-between-ioc-and-di)
16. [Where Spring Fits In: Moving Beyond Plain Java](#16-where-spring-fits-in-moving-beyond-plain-java)
17. [The Spring IoC Container](#17-the-spring-ioc-container)
18. [Responsibilities of Spring Core](#18-responsibilities-of-spring-core)
19. [What Is a Spring Bean?](#19-what-is-a-spring-bean)
20. [Plain Java Flow vs Spring Container Flow](#20-plain-java-flow-vs-spring-container-flow)
21. [Common Pitfalls](#21-common-pitfalls)
22. [Interview Questions](#22-interview-questions)
23. [Interview-Style Code Analysis & Output Questions](#23-interview-style-code-analysis--output-questions)
24. [Quick Cheat Sheet](#24-quick-cheat-sheet)

---

## 1. Why Do We Need Spring Core?

Java provides rich object-oriented building blocks out of the box:
- Classes and Objects
- Constructors and Methods
- Interfaces and Abstract Classes
- Inheritance and Polymorphism
- Packages and Modules

So a natural question arises:
> **If Java already gives us all these features, why do we need Spring Core?**

Spring Core is **not** primarily about building web applications or REST APIs. Those web capabilities belong to **Spring MVC** and **Spring Boot**.

The primary purpose of Spring Core is:
```text
+--------------------------------------------------------------------------+
|  Spring creates objects, manages dependencies, configures lifecycle,    |
|  and connects objects together in a clean, maintainable, and scalable way|
+--------------------------------------------------------------------------+
```

In toy projects or small scripts, manual object instantiation with `new` is trivial. However, in enterprise systems with thousands of service classes, repositories, security filters, and data sources:
- `Class A` depends on `Class B`
- `Class B` depends on `Class C` and `Class D`
- `Class D` depends on external configurations and connection pools

Creating, wiring, managing singletons, and cleaning up this deep dependency graph manually becomes fragile and boilerplate-heavy. That is precisely where Spring Core excels.

---

## 2. Understanding Dependencies in Software Design

Suppose we build an `EmailService` class responsible for sending email notifications:

```java
public class EmailService {
    public void sendEmail(String message) {
        System.out.println("Email sent: " + message);
    }
}
```

Now consider an `OrderService` that processes customer checkout orders and sends an email confirmation:

```java
public class OrderService {
    // OrderService creates and holds a direct instance of EmailService
    private EmailService emailService = new EmailService();

    public void placeOrder(String orderId) {
        System.out.println("Order placed: " + orderId);
        emailService.sendEmail("Order " + orderId + " has been confirmed.");
    }
}
```

Here, `OrderService` **depends on** `EmailService` to fulfill its business function.

> **Definition:** A **dependency** is any collaborating object, service, or resource that a class requires in order to perform its work.

In this initial code:
```java
private EmailService emailService = new EmailService();
```
`OrderService` is taking full responsibility for **both executing business logic** AND **instantiating its own dependency**. This design flaw is called **tight coupling**.

---

## 3. The Problem: Tight Coupling

There are two primary architectural styles when connecting software components:

| Architectural Style | Description | Impact |
| :--- | :--- | :--- |
| **Tightly Coupled Design** | Components directly depend on concrete classes and create their own dependencies | Rigid, hard to test, difficult to modify |
| **Loosely Coupled Design** | Components depend on abstractions (interfaces) and receive dependencies from outside | Modular, testable, flexible, maintainable |

### Real-Life Analogy
Imagine you want to travel from Delhi to Chandigarh:

* **Tightly Coupled Mindset:**  
  *"I can travel only by one specific white sedan, driven by one specific individual, managed by one specific rental agency."*  
  If the driver falls sick or that specific car breaks down, your entire travel plan collapses.

* **Loosely Coupled Mindset:**  
  *"I require a Transportation Service. It can be a bus, train, cab, or flight."*  
  You depend on the capability (**transportation**) rather than a single concrete implementation.

---

## 4. Tight Coupling Illustrated in Code

In our initial example:

```text
+----------------+              +--------------------+
|  OrderService  |  --------->  |    EmailService    |
+----------------+  (creates)   +--------------------+
```

```java
public class OrderService {
    private EmailService emailService = new EmailService();

    public void placeOrder() {
        System.out.println("Order placed");
        emailService.sendEmail("Order confirmed");
    }
}
```

Suppose a new business requirement arrives: **Customers now prefer SMS notifications instead of emails.**

We write an `SmsService`:

```java
public class SmsService {
    public void sendSms(String message) {
        System.out.println("SMS notification sent to customer: " + message);
    }
}
```

Because `OrderService` was tightly coupled to `EmailService`, we are forced to go inside `OrderService` and rewrite its source code:

```java
public class OrderService {
    // Forced modification: replaced EmailService with SmsService
    private SmsService smsService = new SmsService();

    public void placeOrder() {
        System.out.println("Order placed successfully");
        smsService.sendSms("Order confirmed");
    }
}
```

The issue is **not** that business requirements changed (requirements always change).  
**The real flaw is that a change in notification policy forced a code rewrite inside `OrderService`.**

---

## 5. What Exactly Is Tight Coupling?

> **Tight coupling occurs when a class has direct, hardcoded dependencies on concrete classes rather than abstractions.**

### Negative Consequences of Tight Coupling:
1. **Zero Extensibility:** Adding WhatsApp, Push Notifications, or Webhooks requires changing every consuming class.
2. **Untestable Unit Tests:** You cannot test `OrderService` in isolation without actually sending real emails or SMS messages.
3. **Cascading Breaking Changes:** A minor constructor or behavior change in `EmailService` breaks `OrderService`.
4. **Poor Reusability:** `OrderService` cannot be reused in another environment where `EmailService` is absent.

---

## 6. First Step: Introducing Interfaces

To loosen the coupling, we apply the **Dependency Inversion Principle**:  
*Depend on abstractions, not on concrete implementations.*

We extract a common contract:

```java
public interface NotificationService {
    void sendNotification(String message);
}
```

Both concrete notification providers implement this interface:

```java
public class EmailService implements NotificationService {
    @Override
    public void sendNotification(String message) {
        System.out.println("Email: " + message);
    }
}
```

```java
public class SmsService implements NotificationService {
    @Override
    public void sendNotification(String message) {
        System.out.println("SMS: " + message);
    }
}
```

Now, update `OrderService` to refer to the interface type:

```java
public class OrderService {
    // Reference type changed to interface
    private NotificationService notificationService = new EmailService();

    public void placeOrder() {
        System.out.println("Order placed successfully");
        notificationService.sendNotification("Order placed");
    }
}
```

This improves polymorphic dispatch, but a fundamental flaw remains.

---

## 7. Why an Interface Alone Is Not Enough

Inspect the initialization line carefully:

```java
private NotificationService notificationService = new EmailService();
```

Even though the reference type is an interface (`NotificationService`), the concrete instantiation is still hardcoded:
```java
new EmailService();
```

### Why this is still problematic:
1. **Wrong Responsibility:** `OrderService` is a business domain component. Its sole concern should be processing orders, calculating totals, and handling checkout rules. It should **not** decide which specific notification provider gets instantiated.
2. **Violation of SOLID Principles:**
   - **Single Responsibility Principle (SRP):** `OrderService` handles order business logic AND controls dependency instantiation.
   - **Open/Closed Principle (OCP):** Switching to `SmsService` or `WhatsAppService` still requires opening and editing `OrderService.java`.

> **Key Rule:** Creating objects is not bad. Creating objects inside the classes that consume them is the anti-pattern.

---

## 8. The Solution: Dependency Injection (DI)

Instead of letting `OrderService` instantiate its dependency, we **pass (inject)** the dependency from an external entity into `OrderService`.

```java
public class OrderService {
    // Depend purely on the abstraction
    private final NotificationService notificationService;

    // Dependency is injected through the constructor
    public OrderService(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    public void placeOrder() {
        System.out.println("Order placed successfully");
        notificationService.sendNotification("Order confirmed");
    }
}
```

Notice what changed:
- `OrderService` contains **zero** `new` keywords for services.
- `OrderService` has no awareness of `EmailService`, `SmsService`, or any concrete class.
- It simply declares: *"To do my job, provide me with any object that adheres to `NotificationService`."*

---

## 9. Externalizing Object Creation

Now, instantiation and wiring are pushed up to an external caller (e.g., `Main`):

```java
public class Main {
    public static void main(String[] args) {
        // Step 1: Create the dependency
        NotificationService emailSender = new EmailService();

        // Step 2: Inject the dependency into the consumer
        OrderService orderService = new OrderService(emailSender);

        // Step 3: Execute business logic
        orderService.placeOrder();
    }
}
```

If business requirements demand switching to SMS notifications tomorrow:

```java
public class Main {
    public static void main(String[] args) {
        // Swap implementation without touching OrderService!
        NotificationService smsSender = new SmsService();
        OrderService orderService = new OrderService(smsSender);

        orderService.placeOrder();
    }
}
```

`OrderService.java` remains **100% untouched and uncompiled**. This is true loose coupling.

---

## 10. Formal Definition of Dependency Injection

> **Dependency Injection (DI)** is a software design pattern where an object receives its dependencies from an external assembler or provider, rather than instantiating them internally.

```text
+-------------------------------------------------------------------+
|  "Do not instantiate your dependencies. Receive your dependencies.|
|   Ask for what you need; do not build everything yourself."       |
+-------------------------------------------------------------------+
```

### Critical Realization:
- Dependency Injection is **not a Spring-exclusive invention**.
- DI is a general, language-agnostic Object-Oriented Design Principle.
- You can write 100% pure DI in basic Java without adding any third-party framework.
- **Spring did not invent Dependency Injection; Spring automates and manages it.**

---

## 11. Core Benefits of Dependency Injection

```text
                             BENEFITS OF DI
       +----------------------------+---------------------------+
       |                            |                           |
       v                            v                           v
Seamless Implementation      Effortless Unit            Maximum Component
       Swapping                 Testing                    Reusability
```

### 1. Seamless Implementation Swapping
You can alter runtime behavior (e.g., swapping `MySqlRepository` with `PostgresRepository`, or `EmailService` with `SmsService`) strictly in configuration or bootstrap code without altering any business classes.

### 2. Effortless Unit Testing (Mocking)
During automated testing, you do not want to charge real credit cards or dispatch actual emails. DI makes mocking trivial:

```java
// Test class using a stub/mock
public class FakeNotificationService implements NotificationService {
    public boolean notificationSent = false;

    @Override
    public void sendNotification(String message) {
        this.notificationSent = true;
        System.out.println("[TEST STUB] Simulated notification delivery");
    }
}
```

```java
public class OrderServiceTest {
    public static void main(String[] args) {
        FakeNotificationService testStub = new FakeNotificationService();
        OrderService service = new OrderService(testStub);

        service.placeOrder();
        assert testStub.notificationSent : "Test Failed: Notification was not sent!";
        System.out.println("Test passed successfully!");
    }
}
```

### 3. Maximum Component Reusability
The same `OrderService` class can be deployed in production (with AWS SES email), staging (with mock email), or local developer testing without adjusting a single line of code.

---

## 12. Types of Dependency Injection

There are three primary techniques to inject dependencies into an object:

```text
                  DEPENDENCY INJECTION TECHNIQUES
    +---------------------------+---------------------------+
    |                           |                           |
    v                           v                           v
Constructor Injection        Setter Injection            Field Injection
 (Recommended Default)     (Optional Dependencies)      (Reflection-based)
```

### 1. Constructor Injection (Industry Standard)
Dependencies are supplied via the class constructor during object instantiation.

```java
public class OrderService {
    private final NotificationService notificationService;

    // Injected at creation time
    public OrderService(NotificationService notificationService) {
        if (notificationService == null) {
            throw new IllegalArgumentException("NotificationService cannot be null");
        }
        this.notificationService = notificationService;
    }
}
```

**Key Advantages:**
- Allows fields to be marked `final` (ensuring immutability and thread safety).
- Guarantees the object is never created in an uninitialized, invalid state.
- Dependencies are clearly declared and visible to consumers.
- Recommended by the Spring development team as the default strategy.

---

### 2. Setter Injection
Dependencies are injected through public setter methods after object construction.

```java
public class OrderService {
    private NotificationService notificationService;

    // Optional or mutable dependency injected later
    public void setNotificationService(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    public void placeOrder() {
        if (notificationService == null) {
            System.out.println("No notification provider configured.");
            return;
        }
        notificationService.sendNotification("Order confirmed");
    }
}
```

**When to use:**
- Useful for optional dependencies or configurations that may change at runtime.
- Risk: The object can exist in a partially initialized state before setter methods run, risking `NullPointerException`.

---

### 3. Field Injection
Dependencies are injected directly into private fields using reflection (via `@Autowired` in Spring) without constructors or setters.

```java
public class OrderService {
    @Autowired
    private NotificationService notificationService; // Injected via reflection
}
```

**Drawbacks:**
- Hard to unit test outside a Spring container (cannot easily supply mock dependencies).
- Hides class dependencies from client code.
- Fields cannot be declared `final`.
- Often discouraged in modern software development in favor of constructor injection.

---

## 13. Inversion of Control (IoC) Explained

**IoC** stands for **Inversion of Control**.

To grasp IoC, ask a fundamental control-flow question:
> **In standard Java code, who controls the instantiation and lifecycle of dependencies?**

In traditional procedural or standard OO code:
```java
public class OrderService {
    // OrderService CONTROLS when and how EmailService is born
    private EmailService emailService = new EmailService();
}
```
Here, **the class itself controls the dependency lifecycle**.

When we apply Dependency Injection:
- `OrderService` loses control over object creation.
- An external component creates `EmailService` and hands it to `OrderService`.

**The control over dependency creation is inverted (reversed) from the dependent class to an external orchestrator.**

---

## 14. Direct Comparison: Normal Control vs Inverted Control

```text
TRADITIONAL CONTROL FLOW:
+-------------------------------------------------------+
|  OrderService                                         |
|    |                                                  |
|    +---> [new EmailService()] (OrderService in control)|
+-------------------------------------------------------+

INVERTED CONTROL FLOW (IoC):
+-------------------------------------------------------+
|  External Context / Container                         |
|    |                                                  |
|    +---> Creates [EmailService]                       |
|    +---> Creates [OrderService]                       |
|    +---> Injects EmailService into OrderService       |
+-------------------------------------------------------+
```

| Trait | Traditional Direct Control | Inverted Control (IoC) |
| :--- | :--- | :--- |
| **Object Creation** | Handled internally via `new` | Handled externally by a container or factory |
| **Coupling** | Tightly bound to concrete classes | Loosely bound to interfaces |
| **Lifecycle** | Managed by consuming class | Managed by external orchestrator |
| **Testing** | Difficult, requires real collaborators | Easy, allows mock injection |

---

## 15. Relationship Between IoC and DI

IoC and DI are frequently conflated, but they operate at different conceptual levels:

```text
+-----------------------------------------------------------------+
|  IoC (Inversion of Control)  ===> Architectural Principle       |
|  DI  (Dependency Injection)  ===> Concrete Implementation Pattern|
+-----------------------------------------------------------------+
```

- **IoC is the overarching principle:** Control over application flow, event handling, or object creation is moved outside the component. (Other forms of IoC include template method design patterns, UI event listeners, and lookup services).
- **DI is the specific mechanism:** We achieve Inversion of Control specifically by injecting dependencies from the outside.

---

## 16. Where Spring Fits In: Moving Beyond Plain Java

In our previous manual example, `Main.java` handled object creation and wiring:

```java
NotificationService notificationService = new EmailService();
OrderService orderService = new OrderService(notificationService);
```

While manageable for 2 or 3 classes, consider an enterprise system with:
- 500+ service classes
- 200+ repository DAO components
- Multiple database connections, caching layers, and security filters

```text
MANUAL WIRING CHAOS IN LARGE APPS:
ClassA a = new ClassA();
ClassB b = new ClassB(a);
ClassC c = new ClassC(b, a);
ClassD d = new ClassD(c, config);
// Hundreds of lines of repetitive instantiation boilerplate!
```

If `Main` or manual factory classes have to create, wire, order, and destroy hundreds of objects, application maintenance collapses.

We need a dedicated, automated infrastructure engine to:
1. Scan and detect application components.
2. Instantiate objects in their required dependency order.
3. Inject appropriate dependencies automatically.
4. Manage caching, scopes (singletons, prototypes), and lifecycle cleanups.

**This engine is the Spring Framework.**

---

## 17. The Spring IoC Container

The **Spring IoC Container** is the core runtime heart of the Spring Framework.

```text
                 SPRING IoC CONTAINER ARCHITECTURE
 +-------------------------------------------------------------------+
 |                    SPRING IoC CONTAINER                           |
 |                                                                   |
 |   Configuration Metadata                   Managed Objects        |
 |   (Annotations / Java Config)             (Spring Beans)          |
 |             |                                    |                |
 |             v                                    v                |
 |   [@Service, @Repository]  ===>   [EmailService]  [OrderService]  |
 |   [@Component, @Bean]             [PaymentGateway] [UserDAO]      |
 |                                                  |                |
 |                        Automatic Wiring          |                |
 |                    <-----------------------------+                |
 +-------------------------------------------------------------------+
```

### Core Responsibilities of the IoC Container:
1. **Instantiating Beans:** Creates object instances when the application boots up.
2. **Configuring Beans:** Binds properties, environment configurations, and secrets.
3. **Assembling Dependencies:** Analyzes constructors and automatically matches collaborators.
4. **Managing Lifecycle:** Executes post-construction initialization (`@PostConstruct`) and pre-destruction cleanup (`@PreDestroy`).

In plain Java, `Main` acted as an amateur, hardcoded container.  
In Spring, the `ApplicationContext` acts as the enterprise IoC container.

---

## 18. Responsibilities of Spring Core

At a high level, **Spring Core** executes three foundational tasks:

```text
+-----------------------+     +-----------------------+     +-----------------------+
|  1. CREATE OBJECTS    | ==> |   2. MANAGE OBJECTS   | ==> |   3. CONNECT OBJECTS  |
|  Scans and builds     |     |  Controls lifecycle & |     |  Resolves dependencies|
|  managed components   |     |  scopes (Singletons)  |     |  via DI automatically |
+-----------------------+     +-----------------------+     +-----------------------+
```

Before diving into advanced Spring technologies (Spring Data JPA, Spring Security, Spring MVC, Spring Cloud, Spring Batch), mastering this three-pillar foundation is essential.

---

## 19. What Is a Spring Bean?

In ordinary Java programming, any instance created in the JVM is an object:
```java
OrderService order = new OrderService(); // Ordinary Java Object
```

In the Spring ecosystem:
> **A Spring Bean is a Java object that is instantiated, assembled, and managed by the Spring IoC Container.**

```text
+-------------------------------------------------------------------------+
|  Every Spring Bean is an Object,                                        |
|  but not every Java Object is a Spring Bean.                            |
+-------------------------------------------------------------------------+
```

### Direct Comparison:

```java
// 1. Regular Plain Java Object:
EmailService email = new EmailService();
// Managed manually by developer. Spring knows nothing about it.

// 2. Spring Managed Bean:
@Component
public class EmailService implements NotificationService {
    // Spring discovers, instantiates, and injects this as a singleton bean.
}
```

---

## 20. Plain Java Flow vs Spring Container Flow

### 1. Plain Java Flow (Manual Orchestration)

```text
                    +---------------+
                    |   Main.java   |
                    +---------------+
                      |           |
       1. creates     |           |  2. creates
                      v           v
             +--------------+   +----------------+
             | EmailService |   |  OrderService  |
             +--------------+   +----------------+
                      |                 ^
                      |  3. passes into |
                      +-----------------+
```
*Developer writes all wiring code imperatively.*

---

### 2. Spring Framework Flow (Automated Container Management)

```text
               +----------------------------------+
               |       Spring IoC Container       |
               +----------------------------------+
                   |                          |
        creates &  |               creates &  |
        manages    v               manages    v
             +--------------+         +----------------+
             | EmailService | ======> |  OrderService  |
             |    (Bean)    | injects |     (Bean)     |
             +--------------+         +----------------+
```
*Container reads annotations, creates beans in optimal dependency order, and injects them seamlessly.*

---

## 21. Common Pitfalls

| # | Common Pitfall | Why It Causes Problems | Recommended Solution |
| :--- | :--- | :--- | :--- |
| 1 | **Using `new` inside service classes** | Hardcodes concrete implementations and breaks loose coupling | Declare the dependency as an interface and inject it via constructor |
| 2 | **Relying solely on interfaces without DI** | Instantiating `new InterfaceImpl()` inside the class still tightly couples it | Inject the interface reference from the outside |
| 3 | **Overusing Field Injection (`@Autowired` on private field)** | Prevents testing without Spring reflection and disables `final` immutability | Use Constructor Injection as the default |
| 4 | **Circular Dependencies** | Class A requires Class B in constructor, while Class B requires Class A | Redesign classes to eliminate circular dependency or use events |
| 5 | **Confusing IoC with DI** | IoC is an abstract architectural principle; DI is the specific wiring technique | Understand DI as one concrete realization of IoC |
| 6 | **Treating every helper object as a Spring Bean** | Bloats memory and container overhead with short-lived DTOs or entities | Only manage stateless, reusable services, repositories, and utilities as Beans |

---

## 22. Interview Questions

**Q1. What is the fundamental problem with using `new` to instantiate dependencies inside a class?**  
Using `new` inside a class binds it directly to a specific concrete implementation. This prevents swapping implementations at runtime, breaks the Open/Closed Principle, and makes unit testing impossible without invoking external production dependencies (like databases or third-party email APIs).

---

**Q2. Why is an interface alone insufficient to achieve loose coupling?**  
An interface defines an abstraction, but if the consuming class contains `private NotificationService service = new EmailService();`, the class is still directly responsible for instantiating the concrete class. A change in the implementation still requires modifying the consuming class. True loose coupling requires both an abstraction and external dependency injection.

---

**Q3. What is the difference between Inversion of Control (IoC) and Dependency Injection (DI)?**  
IoC is an overarching software design principle where control of object creation, program flow, or lifecycle management is shifted from internal class logic to an external framework or container. DI is a specific pattern used to implement IoC, where dependencies are provided to an object from the outside.

---

**Q4. Why is Constructor Injection generally preferred over Setter or Field Injection?**  
Constructor injection allows dependencies to be marked as `final`, ensuring immutability and thread safety. It prevents objects from existing in partially constructed, invalid states (preventing `NullPointerException`), and makes dependencies explicit when writing pure unit tests without reflection or Spring test runners.

---

**Q5. What is a Spring Bean, and how does it differ from a standard Java object?**  
A standard Java object is created manually by the developer using `new` and is managed by JVM garbage collection without container awareness. A Spring Bean is a Java object whose creation, configuration, lifecycle callbacks, dependency injection, and destruction are fully managed by the Spring IoC Container.

---

**Q6. Did Spring invent Dependency Injection?**  
No. Dependency Injection is an object-oriented design pattern that can be written in plain Java. Spring popularized DI in enterprise Java by automating dependency discovery, wiring, lifecycle coordination, and scope management through its IoC container.

---

## 23. Interview-Style Code Analysis & Output Questions

### Question 1: Identifying Architectural Coupling
```java
public class PaymentGateway {
    public void pay(double amt) { System.out.println("Paid: " + amt); }
}

public class CheckoutService {
    private PaymentGateway gateway = new PaymentGateway();

    public void checkout(double amt) {
        gateway.pay(amt);
    }
}
```
**Q: What design flaws exist in `CheckoutService`?**  
**Answer:**
1. `CheckoutService` is tightly coupled to concrete `PaymentGateway`.
2. It cannot be unit-tested without executing the actual payment logic.
3. Supporting a new payment gateway (like `StripeGateway` or `PayPalGateway`) forces modifications to `CheckoutService`.
4. Violates the Single Responsibility Principle and Dependency Inversion Principle.

---

### Question 2: Predict the Output
```java
interface Engine { void run(); }

class V8Engine implements Engine {
    public void run() { System.out.println("V8 roaring"); }
}

class ElectricMotor implements Engine {
    public void run() { System.out.println("Silent drive"); }
}

class Car {
    private Engine engine;
    public Car(Engine engine) { this.engine = engine; }
    public void drive() { engine.run(); }
}

public class App {
    public static void main(String[] args) {
        Car muscleCar = new Car(new V8Engine());
        Car evCar = new Car(new ElectricMotor());

        muscleCar.drive();
        evCar.drive();
    }
}
```
**Answer:**
```text
V8 roaring
Silent drive
```
*Explanation:* `Car` depends purely on the `Engine` interface. Through constructor injection, different behaviors are achieved at runtime without modifying the `Car` class.

---

### Question 3: Spot the Bug in Dependency Wiring
```java
public class ReportService {
    private NotificationService notifier;

    public void setNotifier(NotificationService notifier) {
        this.notifier = notifier;
    }

    public void generateReport() {
        System.out.println("Report created");
        notifier.sendNotification("Report ready");
    }
}

public class TestApp {
    public static void main(String[] args) {
        ReportService reportService = new ReportService();
        reportService.generateReport();
    }
}
```
**Answer:**  
Throws `NullPointerException` at runtime.  
*Explanation:* `ReportService` uses setter injection, but `setNotifier(...)` was never invoked before `generateReport()`. This demonstrates the vulnerability of setter injection compared to constructor injection, which prevents instantiation without required collaborators.

---

## 24. Quick Cheat Sheet

```text
TIGHT COUPLING            Class A creates Class B directly via 'new'
                          - Rigid, breaks Open/Closed Principle
                          - Impossible to unit-test without real dependencies

LOOSE COUPLING            Class A depends on Interface B; dependency passed from outside
                          - Easily swappable, highly testable, modular

DEPENDENCY INJECTION (DI) Technique where objects receive dependencies rather than creating them
                          - Constructor Injection  ===> Preferred, immutable (final), null-safe
                          - Setter Injection       ===> For optional/mutable dependencies
                          - Field Injection        ===> Avoid in production; tightly couples to reflection

INVERSION OF CONTROL (IoC)Principle where object lifecycle and control flow shift from internal
                          code to an external system/container

IoC vs DI                 - IoC is the guiding principle (the WHAT)
                          - DI is the design pattern implementation (the HOW)

SPRING IoC CONTAINER      The core engine of Spring Core (ApplicationContext)
                          - Instantiates, configures, wires, and manages object lifecycles

SPRING BEAN               A Java object managed by the Spring IoC Container
                          - "Every Spring Bean is an object; not every object is a Spring Bean"

CORE PURPOSE OF SPRING    1. Create objects
                          2. Manage object lifecycles & scopes
                          3. Connect/wire objects automatically
```

**Key Takeaways to Remember:**
1. A dependency is simply an object a class requires to complete its task.
2. An interface abstracts behavior, but without Dependency Injection, tight coupling remains at the instantiation site.
3. Spring did not create DI; Spring automates DI at scale so you never write manual wiring boilerplate.
4. Always default to **Constructor Injection** with `final` fields for robust, testable enterprise code.

