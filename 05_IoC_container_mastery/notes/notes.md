# Spring IoC Container, Beans & Annotation-Based Configuration

---

## 1. Core Idea: Classes Should Not Create Their Own Dependencies

In a well-designed application, a class should focus on its own responsibility.  
For example, `OrderService` should focus on placing an order. It should not be responsible for creating the object of `PaymentService`.

```java
public class OrderService {
    private PaymentService paymentService = new PaymentService();

    public void placeOrder() {
        paymentService.pay();
        System.out.println("Order placed");
    }
}
```

The problem here is that `OrderService` is tightly coupled with `PaymentService`.  
If tomorrow we want to replace `PaymentService` with another implementation, we will have to change the code inside `OrderService`.

A better design is:

```java
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

Now `OrderService` does not create its dependency. It receives the dependency from outside.  
**This is Dependency Injection.**

---

## 2. First-Principles Understanding

At the most basic level, every application needs objects. Those objects may also need other objects.

So the real questions are:
- Who will create these objects?
- Who will connect them together?
- Who will manage their lifecycle?

Without Spring, we usually do this manually:
- `main()` creates objects.
- `main()` connects objects.
- `main()` behaves like a small manual container.

With Spring, this responsibility is shifted to the **Spring IoC container**:
- Spring creates objects.
- Spring connects objects.
- Spring manages their lifecycle.

> **This is the basic idea of IoC: Inversion of Control.**  
> Instead of our code controlling object creation and dependency wiring, Spring controls it for us.

---

## 3. Basic Project Setup

To work with Spring Core using annotation-based configuration:
1. Create a Maven project.
2. Add the `spring-context` dependency.

`spring-context` gives us important container features such as:
- `ApplicationContext`
- Annotation-based configuration
- Component scanning
- Bean creation and dependency injection

---

## 4. What Is a Spring Bean?

A **Spring Bean** is an object managed by the Spring IoC container. That means Spring is responsible for:
- Creating the object
- Wiring its dependencies
- Managing its lifecycle
- Giving it to us when we ask for it

> **Simple Definition:**  
> A Spring Bean is an object whose creation, dependency wiring, and lifecycle are managed by the Spring IoC container.

---

## 5. How Can Spring Manage Our Objects?

Spring can manage objects mainly through two configuration styles:

1. **Annotation-Based Configuration**  
   This is the modern and commonly used approach. We use annotations such as:
   - `@Component`
   - `@Configuration`
   - `@ComponentScan`
   - `@Autowired`
   - `@Bean`

2. **XML-Based Configuration**  
   This was widely used in older Spring applications. Today, XML configuration is not preferred for new projects, but it is still useful to understand because many legacy projects may still contain XML-based bean configuration.

---

## 6. Reflection: Why `Student.class` Matters

When we write something like:
```java
Student.class
```
we are not creating a `Student` object. Instead, we are referring to a special object of type `Class`.

**Example:**
```java
Class<Student> c = Student.class;
```

Here, `c` does not contain a `Student` object. It contains **metadata** about the `Student` class, such as:
- **Class name** $
ightarrow$ `Student`
- **Fields** $
ightarrow$ `name`, `age`
- **Methods** $
ightarrow$ `study()`
- **Constructors** $
ightarrow$ `Student()`
- **Annotations** $
ightarrow$ `@Component`, `@Service`, etc.

Spring uses this kind of class metadata internally. When we pass a class like:
```java
new AnnotationConfigApplicationContext(AppConfig.class);
```
we are giving Spring metadata about the `AppConfig` class so that Spring can read configuration instructions from it.

---

## 7. Telling Spring Which Classes to Manage

Spring does not automatically manage every class in the project. We need to tell Spring which classes are eligible to become beans.

One common way is by using `@Component`:

```java
@Component
public class PaymentService {
    public void pay() {
        System.out.println("Payment done");
    }
}
```

> `@Component` tells Spring: **This class is eligible to become a Spring bean.**

But just writing `@Component` is not enough. Spring also needs to know where it should search for such classes. That is where `@ComponentScan` comes in.

---

## 8. ApplicationContext: The Spring IoC Container

In normal Spring applications, we commonly work with `ApplicationContext`:

```java
ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
```

`ApplicationContext` represents the Spring IoC container. It is responsible for:
- Reading configuration
- Creating beans
- Resolving dependencies
- Managing bean lifecycle
- Providing beans when requested

> **Important point:**  
> `ApplicationContext context = new ApplicationContext();`  
> This is not possible because `ApplicationContext` is an **interface**. So we use one of its implementations. For annotation-based configuration, we commonly use **`AnnotationConfigApplicationContext`**.

---

## 9. What Is `AnnotationConfigApplicationContext`?

`AnnotationConfigApplicationContext` is an implementation of `ApplicationContext`. It starts a Spring container using Java annotation-based configuration.

```java
ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
```

This means:
- Start the Spring container.
- Read instructions from `AppConfig.class`.
- Use annotation-based configuration.
- Create and manage beans accordingly.

---

## 10. What Is `AppConfig.class`?

`AppConfig` is a configuration class.

```java
@Configuration
@ComponentScan("com.raysi")
public class AppConfig {
}
```

This class tells Spring:
- This is a configuration class.
- Scan the package `com.raysi`.
- Find classes marked with annotations like `@Component`.
- Create their beans.
- Wire their dependencies.

So when we write:
```java
new AnnotationConfigApplicationContext(AppConfig.class)
```
we are giving Spring its startup instruction file.

---

## 11. What Is `@Configuration`?

`@Configuration` tells Spring that a class contains Spring configuration instructions.

```java
@Configuration
public class AppConfig {
}
```

When Spring sees this, it understands:
- This is not just a normal class.
- This class may contain Spring setup instructions.
- This class can be a source of bean definitions.

A configuration class may contain:
- `@ComponentScan`
- `@Bean` methods
- Other configuration-related instructions

> **Simple definition:** `@Configuration` marks a class as a source of bean definitions.

---

## 12. What Is `@ComponentScan`?

When Spring starts, it needs to know where to search for classes marked with annotations like `@Component`.

```java
@Configuration
@ComponentScan("com.raysi")
public class AppConfig {
}
```

This tells Spring:
- Start scanning from `com.raysi`.
- Also scan its sub-packages.
- Find classes marked with `@Component`, `@Service`, `@Repository`, `@Controller`, etc.
- Register them as beans.

### `@Component` vs `@ComponentScan`

| Annotation | Meaning |
| :--- | :--- |
| **`@Component`** | Marks a class as eligible to become a Spring bean |
| **`@ComponentScan`** | Tells Spring where to search for such classes |

### Can We Write `@ComponentScan` Without a Package Name?
**Yes.**

```java
@Configuration
@ComponentScan
public class AppConfig {
}
```

In this case, Spring scans the package where `AppConfig` is present and its sub-packages.  
For example, if `AppConfig` is located in `com.raysi.AppConfig`, Spring scans `com.raysi` and all its sub-packages.

---

## 13. What Does `getBean()` Mean?

After Spring creates and stores beans inside the container, we can ask the container for a bean:

```java
OrderService orderService = context.getBean(OrderService.class);
```

This means: *"Spring, give me the `OrderService` bean from your container."*
- If Spring has already created the bean, it returns that object.
- If Spring does not know about `OrderService`, we will get an error.

**Common reasons for errors:**
- `OrderService` is not annotated with `@Component`.
- The package of `OrderService` is not included in `@ComponentScan`.
- The bean is not registered using `@Bean` or XML.

**Complete Example:**

```java
public class Main {
    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        OrderService orderService = context.getBean(OrderService.class);
        orderService.placeOrder();
    }
}
```

---

## 14. Types of Dependency Injection in Spring

Spring commonly supports three types of dependency injection:
1. **Constructor injection**
2. **Field injection**
3. **Setter injection**

---

## 15. Constructor Injection

In constructor injection, dependencies are provided through the constructor:

```java
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

In modern Spring, if a bean has only one constructor, writing `@Autowired` on that constructor is **optional**. Spring can automatically use that constructor for dependency injection.

Writing `@Autowired` explicitly is also valid:

```java
@Component
public class OrderService {
    private final PaymentService paymentService;

    @Autowired
    public OrderService(PaymentService paymentService) {
        this.paymentService = paymentService;
    }
}
```

---

## 16. Why Constructor Injection Is Preferred

A constructor is called when an object is created. If `OrderService` needs `PaymentService`, the cleanest time to provide `PaymentService` is while creating `OrderService`. That means the object is created in a complete and usable state.

### Benefit 1: Dependency Is Mandatory
If `OrderService` cannot work without `PaymentService`, constructor injection makes that requirement clear:
```java
public OrderService(PaymentService paymentService) {
    this.paymentService = paymentService;
}
```
The object cannot be created without its required dependency.

### Benefit 2: We Can Use `final`
```java
private final PaymentService paymentService;
```
This means once the dependency is assigned, it cannot be changed accidentally, making the class safer.

### Benefit 3: Easy to Test Without Spring
With constructor injection, we can manually create and test the class without starting a Spring container:
```java
PaymentService paymentService = new PaymentService();
OrderService orderService = new OrderService(paymentService);
orderService.placeOrder();
```
We do not need Spring just to instantiate the object. This keeps our class clean, simple, and testable. Spring helps us wire the object, but our Java class does not become tightly bound to the container.

---

## 17. Field Injection

In field injection, Spring directly injects the dependency into a field:

```java
@Component
public class OrderService {
    @Autowired
    private PaymentService paymentService;

    public void placeOrder() {
        paymentService.pay();
        System.out.println("Order placed");
    }
}
```

This works because Spring uses reflection to set the private field value.

**However, field injection is generally not preferred.**  
**Reasons:**
- The dependency is hidden.
- The class cannot be easily unit tested without Spring.
- The field cannot be marked as `final`.
- The object can exist in an incomplete/broken state before Spring injects the field.

> Constructor injection is usually the better choice for required dependencies.

---

## 18. Setter Injection

In setter injection, Spring creates the object first and then calls a setter method to provide the dependency:

```java
@Component
public class OrderService {
    private PaymentService paymentService;

    @Autowired
    public void setPaymentService(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    public void placeOrder() {
        paymentService.pay();
        System.out.println("Order placed");
    }
}
```

**Spring follows this flow:**
1. Create `OrderService` object using the no-argument constructor.
2. Call `setPaymentService()`.
3. Pass `PaymentService` into the setter.

Setter injection is useful when a dependency is optional or can be reconfigured after object creation. For mandatory dependencies, constructor injection is preferred.

---

## 19. What Happens Internally When Spring Starts?

When we execute:
```java
new AnnotationConfigApplicationContext(AppConfig.class);
```
Spring starts the container and performs several internal steps:

- **Step 1: Spring Starts the Container**  
  Spring creates an `ApplicationContext`. This becomes the IoC container for our application.

- **Step 2: Spring Reads `AppConfig.class`**  
  Spring inspects the class metadata, recognizing it contains configuration instructions and annotations.

- **Step 3: Spring Processes `@ComponentScan`**  
  If `AppConfig` contains `@ComponentScan("com.raysi")`, Spring notes to search inside `com.raysi` and all its sub-packages.

- **Step 4: Spring Finds Component Classes**  
  Spring scans the classpath and locates classes annotated with `@Component`, `@Service`, `@Repository`, `@Controller`, etc.:
  ```java
  @Component
  public class PaymentService { }

  @Component
  public class OrderService { }
  ```

- **Step 5: Spring Creates Bean Definitions**  
  Before creating actual instances, Spring stores metadata about each object in a `BeanDefinition`:
  ```
  Bean name         -> paymentService
  Bean class        -> com.raysi.service.PaymentService
  Scope             -> singleton
  Dependencies      -> none
  Creation strategy -> constructor
  ```
  A `BeanDefinition` is not the actual object; it is the blueprint defining how the object must be created and managed.

---

## 20. Why Does Spring Create Bean Definitions First?

Spring manages a complete enterprise application lifecycle, not just an isolated object. Before instantiation, Spring needs to know:
- Which beans exist
- What their classes are
- What their scopes are (e.g., singleton, prototype)
- What dependencies each bean requires
- How they should be created
- What lifecycle methods/callbacks must be applied

That is why Spring first builds a `BeanDefinition` registry (`paymentService`, `orderService`) before creating actual runtime instances.

- **Step 6: Spring Creates Bean Objects**  
  If `PaymentService` has no dependencies, Spring instantiates it:
  ```java
  PaymentService paymentService = new PaymentService();
  ```
  Now the container stores the `paymentService` bean.

- **Step 7: Spring Resolves Dependencies for `OrderService`**  
  Spring inspects the constructor:
  ```java
  public OrderService(PaymentService paymentService)
  ```
  It checks whether a matching bean of type `PaymentService` exists in the container.

- **Step 8: Spring Injects Dependencies**  
  Spring passes the `PaymentService` bean directly into the constructor:
  ```java
  OrderService orderService = new OrderService(paymentService);
  ```
  `OrderService` is created in a complete and usable state.

- **Step 9: Our Application Uses the Bean**  
  ```java
  orderService.placeOrder();
  ```
  **Output:**
  ```
  Payment done
  Order placed
  ```

---

## 21. What If No Matching Bean Exists?

Suppose `OrderService` requires `PaymentService`, but Spring cannot find any matching bean in the container.  
Spring fails to start and throws an exception:
```
NoSuchBeanDefinitionException: No qualifying bean of type 'PaymentService' available
```

**Common reasons:**
- `PaymentService` is not annotated with `@Component`.
- Its package is excluded from `@ComponentScan`.
- No `@Bean` method is defined for it.
- The required dependency was never registered in the Spring container.

---

## 22. What If Multiple Matching Beans Exist?

Consider an interface with multiple implementations:

```java
public interface PaymentService {
    void pay();
}

@Component
public class UPIPaymentService implements PaymentService {
    public void pay() {
        System.out.println("UPI payment done");
    }
}

@Component
public class CardPaymentService implements PaymentService {
    public void pay() {
        System.out.println("Card payment done");
    }
}
```

Now `OrderService` depends on the interface:

```java
@Component
public class OrderService {
    private final PaymentService paymentService;

    public OrderService(PaymentService paymentService) {
        this.paymentService = paymentService;
    }
}
```

Spring finds two candidate beans:
- `upiPaymentService` $
ightarrow$ implements `PaymentService`
- `cardPaymentService` $
ightarrow$ implements `PaymentService`

Spring is unable to decide which bean to inject, resulting in a **bean ambiguity error** (`NoUniqueBeanDefinitionException`).

**Solutions:**
1. `@Primary`
2. `@Qualifier`

---

## 23. Using `@Primary`

`@Primary` marks one implementation as the primary/default candidate:

```java
@Primary
@Component
public class UPIPaymentService implements PaymentService {
    public void pay() {
        System.out.println("UPI payment done");
    }
}
```

This instructs Spring: *If multiple `PaymentService` beans exist, prefer this one by default.*  
`OrderService` will receive `UPIPaymentService` without needing further configuration.

---

## 24. Using `@Qualifier`

`@Qualifier` explicitly designates which bean must be injected by its bean name:

```java
@Component
public class OrderService {
    private final PaymentService paymentService;

    public OrderService(@Qualifier("cardPaymentService") PaymentService paymentService) {
        this.paymentService = paymentService;
    }
}
```

### How Does Spring Name Beans by Default?
Spring derives the default bean name from the class name by converting the first letter to lowercase:
- `CardPaymentService` $
ightarrow$ `cardPaymentService`
- `UPIPaymentService` $
ightarrow$ `UPIPaymentService` *(acronym rules may vary depending on JavaBeans naming conventions)*

---

## 25. Custom Bean Names with `@Component`

To avoid ambiguity, custom bean names can be specified directly:

```java
@Component("upi")
public class UPIPaymentService implements PaymentService { }

@Component("card")
public class CardPaymentService implements PaymentService { }
```

Then reference the custom identifier:

```java
public OrderService(@Qualifier("card") PaymentService paymentService) {
    this.paymentService = paymentService;
}
```

---

## 26. `@Qualifier` with Field Injection

```java
@Component
public class OrderService {
    @Autowired
    @Qualifier("upiPaymentService")
    private PaymentService paymentService;

    public void placeOrder() {
        paymentService.pay();
        System.out.println("Order placed");
    }
}
```

---

## 27. `@Qualifier` with Setter Injection

```java
@Component
public class OrderService {
    private PaymentService paymentService;

    @Autowired
    public void setPaymentService(@Qualifier("cardPaymentService") PaymentService paymentService) {
        this.paymentService = paymentService;
    }
}
```

---

## 28. What If Both `@Primary` and `@Qualifier` Are Used?

> **Rule:** `@Qualifier` always takes priority over `@Primary`.

- `@Primary` provides a **default** choice.
- `@Qualifier` provides a **specific** choice.
- A specific choice always overrides the default.

---

## 29. Why Do We Need `@Bean`?

`@Component` works when we can modify source code. However, we cannot use `@Component` on classes originating from external third-party libraries:

```java
// From a third-party JAR library
public class EmailClient {
    private final String apiKey;

    public EmailClient(String apiKey) {
        this.apiKey = apiKey;
    }

    public void sendEmail() {
        System.out.println("Email sent using API key: " + apiKey);
    }
}
```

Because external library source files cannot be edited to add `@Component`, we use `@Bean` methods inside a configuration class to register them into the Spring container.

---

## 30. Custom Object Creation with `@Bean`

When object creation involves custom logic or parameters:

```java
public class User {
    private String name;
    private String email;

    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }
}
```

Spring does not automatically know what values to supply for `name` and `email`. `@Bean` lets us configure construction explicitly.

---

## 31. What Exactly Does `@Bean` Mean?

`@Bean` is applied to methods within a `@Configuration` class:

```java
@Configuration
public class AppConfig {
    @Bean
    public PaymentService paymentService() {
        return new PaymentService();
    }
}
```

This instructs Spring:
- Execute this method.
- Take the returned object.
- Register it into the IoC container.
- Manage it as a Spring Bean (by default, as a singleton).

---

## 32. Custom Bean Name with `@Bean`

By default, the bean name equals the method name (`paymentService`). A custom name can be specified:

```java
@Bean("myPaymentService")
public PaymentService paymentService() {
    return new PaymentService();
}
```

Fetch it by custom name:
```java
PaymentService paymentService = (PaymentService) context.getBean("myPaymentService");
```

---

## 33. `@Bean` with Dependencies

```java
@Configuration
public class AppConfig {
    @Bean
    public PaymentService paymentService() {
        return new PaymentService();
    }

    @Bean
    public OrderService orderService(PaymentService paymentService) {
        return new OrderService(paymentService);
    }
}
```

Spring resolves `PaymentService` first and passes it as a parameter into `orderService()`.

Alternatively:
```java
@Bean
public OrderService orderService() {
    return new OrderService(paymentService());
}
```
In `@Configuration` classes, Spring's CGLIB proxy intercepts direct method invocations to ensure singleton semantics. However, using method parameters is cleaner and recommended.

---

## 34. `@Component` vs `@Bean`

| Point | `@Component` | `@Bean` |
| :--- | :--- | :--- |
| **Where used?** | On a class | On a method |
| **Style** | Automatic detection | Explicit / manual registration |
| **Best for** | Application source classes | External library classes or custom creation |
| **Component scanning needed?** | Yes | No (configuration class must be loaded) |
| **Bean name** | Class name (decapitalized) by default | Method name by default |

---

## 35. Important Note: Avoid Creating the Same Bean Twice

Do not register the same class using both `@Component` and `@Bean` unless explicitly intending to create multiple beans.

```java
@Component
public class PaymentService { }

// In AppConfig:
@Bean
public PaymentService paymentService() {
    return new PaymentService();
}
```

This causes naming conflicts, ambiguous autowiring by type, or unintended bean overriding behavior.

> **Clean rule:** Register a bean using either `@Component` or `@Bean`, not both.

---

## 36. Dependency Resolution with `@Bean`

When multiple beans of the same type are registered via `@Bean`:

```java
@Bean
@Primary
public PaymentService upiPaymentService() {
    return new UPIPaymentService();
}

@Bean
public OrderService orderService(@Qualifier("cardPaymentService") PaymentService paymentService) {
    return new OrderService(paymentService);
}
```

---

## 37. Why Not Put Everything in `main()`?

`main()` should only serve as the application entry point. Placing object instantiation, dependency wiring, and configuration inside `main()` returns to manual dependency management.

Keeping `main()` lightweight:
```java
public class Main {
    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        OrderService orderService = context.getBean(OrderService.class);
        orderService.placeOrder();
    }
}
```

---

## 38. `BeanFactory` vs `ApplicationContext`

- **`BeanFactory`**: The foundational, low-level container interface.
- **`ApplicationContext`**: A complete enterprise-grade container interface extending `BeanFactory`.

```
BeanFactory          -> Basic container
ApplicationContext   -> Advanced container commonly used in real-world applications
```

`ApplicationContext` provides:
- Bean creation and dependency injection
- Lifecycle management
- Event publication
- Internationalization (i18n) support
- Seamless integration with enterprise Spring features

---

## 39. Final Flow Summary

When starting the Spring container:
```java
ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
```

1. Start the IoC container.
2. Read `AppConfig.class`.
3. Process `@Configuration`.
4. Process `@ComponentScan`.
5. Scan specified package and sub-packages.
6. Find classes marked with `@Component`, `@Service`, `@Repository`, `@Controller`.
7. Create `BeanDefinition` metadata records.
8. Resolve dependencies between definitions.
9. Instantiate bean objects.
10. Inject dependencies (constructor, field, or setter).
11. Store managed beans in container context.
12. Return beans when requested via `getBean()`.

---

## 40. Key Takeaways

- A **Spring Bean** is an object managed by the Spring IoC container [cite: 52].
- **`ApplicationContext`** represents the Spring IoC container in modern applications [cite: 52].
- **`AnnotationConfigApplicationContext`** initializes a container using annotation-based configuration [cite: 53].
- **`@Configuration`** designates a class containing Spring configuration metadata [cite: 53].
- **`@ComponentScan`** defines base packages to scan for components [cite: 53].
- **`@Component`** registers a class as an eligible Spring bean [cite: 53].
- **`getBean()`** retrieves a managed instance from the container [cite: 53].
- **Constructor injection** is the preferred DI style for mandatory dependencies [cite: 53].
- **Field injection** is discouraged due to testing and immutability drawbacks [cite: 32, 53].
- **Setter injection** is suitable for optional or mutable dependencies [cite: 33, 53].
- Spring constructs **`BeanDefinition`** metadata prior to creating actual bean instances [cite: 35, 53].
- If no matching bean is found, an exception is thrown; if multiple beans match, resolve using **`@Primary`** or **`@Qualifier`** [cite: 40, 53].
- **`@Bean`** manually registers external library objects or handles customized object initialization [cite: 45, 53].
- Do not duplicate bean registration by combining `@Component` and `@Bean` for the same class [cite: 50, 53].

---

## 41. One-Line Revision

> **The Spring IoC Container creates objects, wires dependencies, manages lifecycle, and provides ready-to-use beans when required.** [cite: 53]

