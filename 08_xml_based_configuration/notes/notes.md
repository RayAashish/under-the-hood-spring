# Spring XML-Based Configuration

**Author:** raysi  
**Topic:** Spring Framework Core — XML Configuration & Inversion of Control (IoC)  

---

## Introduction & Table of Contents

This document provides a complete reference guide for Spring XML-based configuration, bean lifecycle management, dependency injection patterns, and modular container setups.

### Quick Navigation

- [1. Why XML Configuration Exists](#1-why-xml-configuration-exists)
- [2. What Does Spring Actually Need?](#2-what-does-spring-actually-need)
- [3. Configuration Metadata in Spring](#3-configuration-metadata-in-spring)
- [4. Why Should We Learn XML Configuration?](#4-why-should-we-learn-xml-configuration)
- [5. Recommended Project Structure](#5-recommended-project-structure)

#### Part 1: Creating the First XML Bean
- [6. Create a Simple Java Class](#6-create-a-simple-java-class)
- [7. Create beans.xml](#7-create-beansxml)
- [8. Register the First Bean](#8-register-the-first-bean)
- [9. XML Bean Compared with @Component](#9-xml-bean-compared-with-component)
- [10. Load XML Configuration in Java](#10-load-xml-configuration-in-java)
- [11. Why Did the Constructor Run Immediately?](#11-why-did-the-constructor-run-immediately)

#### Part 2: Getting Beans from the Container
- [12. Get Bean by Name](#12-get-bean-by-name)
- [13. Get Bean by Name and Type](#13-get-bean-by-name-and-type)
- [14. Get Bean by Type](#14-get-bean-by-type)

#### Part 3: Bean Naming in XML
- [15. Bean Name Convention](#15-bean-name-convention)
- [16. What If We Do Not Give an id?](#16-what-if-we-do-not-give-an-id)
- [17. What If Two Beans Have the Same id?](#17-what-if-two-beans-have-the-same-id)
- [18. Create Two Different Beans](#18-create-two-different-beans)

#### Part 4: id, name, and Alias
- [19. id Attribute](#19-id-attribute)
- [20. name Attribute](#20-name-attribute)
- [21. Multiple Names Using name](#21-multiple-names-using-name)
- [22. External Alias Using `<alias>`](#22-external-alias-using-alias)

#### Part 5 & 6: Dependency Injection & Constructor Injection
- [23. Two Common Ways of Dependency Injection in XML](#23-two-common-ways-of-dependency-injection-in-xml)
- [24. Constructor Injection with Bean Reference](#24-constructor-injection-with-bean-reference)
- [25. Constructor Injection with Simple Value](#25-constructor-injection-with-simple-value)
- [26. Constructor Injection with Multiple Arguments](#26-constructor-injection-with-multiple-arguments)
- [27. Constructor Injection by Index](#27-constructor-injection-by-index)
- [28. Constructor Injection by Name](#28-constructor-injection-by-name)
- [29. Constructor Injection by Type](#29-constructor-injection-by-type)
- [30. Summary of Constructor Injection Options](#30-summary-of-constructor-injection-options)

#### Part 7: Setter Injection
- [31. Create OrderService with Setter](#31-create-orderservice-with-setter)
- [32. XML Configuration for Setter Injection](#32-xml-configuration-for-setter-injection)
- [33. value vs ref](#33-value-vs-ref)

#### Part 8: Duplicate Beans During Dependency Injection
- [34. Problem: Multiple Beans of Same Type](#34-problem-multiple-beans-of-same-type)
- [35. Solution 1: Explicit Wiring Using ref](#35-solution-1-explicit-wiring-using-ref)
- [36. Solution 2: Using primary="true"](#36-solution-2-using-primarytrue)
- [37. Solution 3: Using autowire-candidate="false"](#37-solution-3-using-autowire-candidatefalse)

#### Part 9: XML Autowiring
- [38. What Is XML Autowiring?](#38-what-is-xml-autowiring)
- [39. Common XML Autowiring Modes](#39-common-xml-autowiring-modes)
- [40. autowire="byName"](#40-autowirebyname)
- [41. autowire="byType"](#41-autowirebytype)
- [42. autowire="constructor"](#42-autowireconstructor)
- [43. Comparison of XML Autowiring Modes](#43-comparison-of-xml-autowiring-modes)

#### Part 10: Bean Scopes in XML
- [44. Two Important Scopes](#44-two-important-scopes)
- [45. Singleton Scope](#45-singleton-scope)
- [46. Prototype Scope](#46-prototype-scope)
- [47. Singleton Bean Depending on Prototype Bean](#47-singleton-bean-depending-on-prototype-bean)

#### Part 11: Bean Lifecycle Methods in XML
- [48. Custom Init and Destroy Methods](#48-custom-init-and-destroy-methods)
- [49. XML Configuration with init-method and destroy-method](#49-xml-configuration-with-init-method-and-destroy-method)
- [50. Closing the Context](#50-closing-the-context)
- [51. Prototype Scope with Init and Destroy](#51-prototype-scope-with-init-and-destroy)

#### Part 12: Collection Injection in XML
- [52. Injecting a List](#52-injecting-a-list)
- [53. Injecting a Map](#53-injecting-a-map)

#### Part 13: Splitting XML Files
- [54. Why Split XML Files?](#54-why-split-xml-files)
- [55. Example: orderContext.xml](#55-example-ordercontextxml)
- [56. Example: userContext.xml](#56-example-usercontextxml)
- [57. Main File: applicationContext.xml](#57-main-file-applicationcontextxml)

#### Part 14 & Summary
- [58. Can XML and Annotations Work Together?](#58-can-xml-and-annotations-work-together)
- [59. Component Scanning from XML](#59-component-scanning-from-xml)
- [60. Should Beginners Mix XML and Annotations?](#60-should-beginners-mix-xml-and-annotations)
- [Final Summary & Key Takeaways](#final-summary--key-takeaways)

---

## 1. Why XML Configuration Exists

In Spring, annotations are not the only way to tell the IoC container which objects it should manage.

Before annotation-based configuration became common, Spring applications were mostly configured using XML files.

XML configuration is another way of telling Spring:
- These are my classes.
- These are the objects you need to create.
- These are their dependencies.
- Please manage them inside the IoC container.

[Back to Top](#quick-navigation)

---

## 2. What Does Spring Actually Need?

The main job of the Spring IoC container is to:
> **Create objects, connect their dependencies, manage their lifecycle, and destroy them when required.**

To do this, Spring needs configuration information such as:
1. Which classes should become beans?
2. What should be the bean name?
3. Should the bean be singleton or prototype?
4. Which dependency should be injected?
5. Which bean should be preferred if multiple beans of the same type exist?
6. Which method should run after bean creation?
7. Which method should run before bean destruction?

This information is called **configuration metadata**.

[Back to Top](#quick-navigation)

---

## 3. Configuration Metadata in Spring

Spring can receive configuration metadata in different ways:

| Configuration Style | Where Configuration Is Written |
| :--- | :--- |
| **XML-based configuration** | Inside an XML file |
| **Annotation-based configuration** | Inside Java classes |
| **Java-based configuration** | Inside Java config classes using `@Configuration` and `@Bean` |

No matter which style we use, Spring converts this configuration metadata into internal objects called **`BeanDefinition`** objects.

```
       Configuration Metadata
                 |
      +----------+----------+
      |                     |
     XML               Annotations
      |                     |
      +----------+----------+
                 |
           BeanDefinition
                 |
           IoC Container
                 |
   Creates + Wires + Manages Beans
```

### The Core Principle
- XML, annotations, and Java config are only different ways of giving instructions to Spring.
- Internally, Spring uses those instructions to create bean definitions (`BeanDefinition`).

[Back to Top](#quick-navigation)

---

## 4. Why Should We Learn XML Configuration?

Even though modern Spring projects mostly use annotations and Java-based configuration, XML is still useful to understand for two major reasons:

### Reason 1: Legacy Projects
Many old Spring applications still use XML configuration. If you work on an older enterprise project, you may find files like:
- `beans.xml`
- `applicationContext.xml`
- `spring-context.xml`

Understanding XML configuration helps you read and maintain such projects.

### Reason 2: Better Understanding of Spring Internals
Annotations sometimes hide what Spring is doing internally. XML makes the wiring more visible because we manually write:
- Which bean should be created
- What its name should be
- Which dependency should be injected
- Which lifecycle method should be called

This makes XML a great way to understand the foundation of Spring.

[Back to Top](#quick-navigation)

---

## 5. Recommended Project Structure

A simple XML-based Spring project can follow this standard Maven / Gradle structure:

```text
src
 └── main
     ├── java
     │   └── in
     │       └── raysi
     │           ├── Main.java
     │           ├── PaymentService.java
     │           └── OrderService.java
     │
     └── resources
         └── beans.xml
```

The XML file should be placed inside:
```
src/main/resources
```
This is important because files inside `resources` become available on the **classpath**.

So when we write:
```java
new ClassPathXmlApplicationContext("beans.xml");
```
Spring searches for `beans.xml` from the classpath root.

[Back to Top](#quick-navigation)

---

## 6. Create a Simple Java Class

```java
package in.raysi;

public class PaymentService {

    public PaymentService() {
        System.out.println("PaymentService object created");
    }

    public void pay() {
        System.out.println("Payment successful");
    }
}
```

- This is a normal Java class (POJO).
- There is no annotation like `@Component`.
- Therefore, Spring will not automatically detect it.
- We will register this class as a bean manually using XML.

[Back to Top](#quick-navigation)

---

## 7. Create beans.xml

Inside `src/main/resources`, create a file named `beans.xml`.

Add the basic XML structure:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="
           http://www.springframework.org/schema/beans
           http://www.springframework.org/schema/beans/spring-beans.xsd">

</beans>
```

- For now, this XML file is empty.
- The root `<beans>` tag is the container where all bean definitions are declared.

[Back to Top](#quick-navigation)

---

## 8. Register the First Bean

Now add a `<bean>` definition inside the `<beans>` tag:

```xml
<bean id="paymentService" class="in.raysi.PaymentService"/>
```

### Complete `beans.xml`:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="
           http://www.springframework.org/schema/beans
           http://www.springframework.org/schema/beans/spring-beans.xsd">

    <bean id="paymentService" class="in.raysi.PaymentService"/>

</beans>
```

### Meaning of this line:
- Spring, create and manage one object of `PaymentService`.
- Keep its bean name as `paymentService`.

| XML Attribute | Meaning |
| :--- | :--- |
| **`id`** | Name of the bean inside the Spring container |
| **`class`** | Fully qualified class name whose object Spring should instantiate |

[Back to Top](#quick-navigation)

---

## 9. XML Bean Compared with @Component

In annotation-based configuration, we write:
```java
@Component
public class PaymentService {
}
```
Spring usually gives this bean the default decapitalized name: `paymentService`.

In XML configuration, we manually write:
```xml
<bean id="paymentService" class="in.raysi.PaymentService"/>
```

So this XML configuration is conceptually similar to:
```java
@Component("paymentService")
public class PaymentService {
}
```

### Key Differences:

| Aspect | Annotation Config | XML Config |
| :--- | :--- | :--- |
| **Location** | Written directly inside Java class | Written inside external XML file |
| **Detection** | Detected using component scanning | Read directly from XML definitions |
| **Bean Naming** | Usually generated automatically | Usually specified manually |

[Back to Top](#quick-navigation)

---

## 10. Load XML Configuration in Java

Create `Main.java`:

```java
package in.raysi;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Main {
    public static void main(String[] args) {
        ApplicationContext context =
                new ClassPathXmlApplicationContext("beans.xml");

        PaymentService paymentService =
                context.getBean("paymentService", PaymentService.class);

        paymentService.pay();
    }
}
```

### Output:
```text
PaymentService object created
Payment successful
```

[Back to Top](#quick-navigation)

---

## 11. Why Did the Constructor Run Immediately?

When we write:
```java
ApplicationContext context =
        new ClassPathXmlApplicationContext("beans.xml");
```
Spring loads the XML file and creates the beans immediately.

By default, Spring beans are:
**singleton + eager**

- This means Spring creates singleton beans immediately when the container starts.
- That is why `"PaymentService object created"` prints before calling `getBean()`.

[Back to Top](#quick-navigation)

---

## 12. Get Bean by Name

```java
PaymentService paymentService =
        (PaymentService) context.getBean("paymentService");
```
*Note:* This works, but requires manual type casting.

[Back to Top](#quick-navigation)

---

## 13. Get Bean by Name and Type

```java
PaymentService paymentService =
        context.getBean("paymentService", PaymentService.class);
```
*Note:* This is a safer and cleaner approach because it avoids manual casting and provides compile-time type safety.

[Back to Top](#quick-navigation)

---

## 14. Get Bean by Type

If there is only one bean of that type registered, we can write:
```java
PaymentService paymentService =
        context.getBean(PaymentService.class);
```
This works **only** when Spring has exactly one matching bean of that type.

### Example:
```xml
<bean id="paymentService" class="in.raysi.PaymentService"/>
```
This is fine because only one `PaymentService` bean exists.

### What happens if multiple beans of the same type exist?
```xml
<bean id="paymentService1" class="in.raysi.PaymentService"/>
<bean id="paymentService2" class="in.raysi.PaymentService"/>
```
If we call:
```java
PaymentService paymentService = context.getBean(PaymentService.class);
```
Spring will **not** randomly choose one. It will throw:
```text
org.springframework.beans.factory.NoUniqueBeanDefinitionException
```
because multiple beans match the same type.

[Back to Top](#quick-navigation)

---

## 15. Bean Name Convention

Bean names usually follow standard Java camelCase variable naming conventions.

```xml
<bean id="paymentService" class="in.raysi.PaymentService"/>
```
Here, the bean name is `paymentService`.

[Back to Top](#quick-navigation)

---

## 16. What If We Do Not Give an id?

We can omit the `id`:
```xml
<bean class="in.raysi.PaymentService"/>
```

In this case, we have not manually given the bean a clear name. So this will not work:
```java
context.getBean("paymentService", PaymentService.class);
```
because no bean named `paymentService` was registered.

However, resolving by type may still work:
```java
PaymentService paymentService = context.getBean(PaymentService.class);
```
*(Provided only one matching bean exists).*

> **Best Practice:** For XML configuration, always give beans a clear, explicit `id`.

[Back to Top](#quick-navigation)

---

## 17. What If Two Beans Have the Same id?

This is **invalid**:
```xml
<bean id="paymentService" class="in.raysi.PaymentService"/>
<bean id="paymentService" class="in.raysi.PaymentService"/>
```
In the same XML configuration file, bean IDs must be unique. Otherwise, Spring cannot unambiguously identify which bean is referenced by `context.getBean("paymentService")`.

[Back to Top](#quick-navigation)

---

## 18. Create Two Different Beans

Create another class `OrderService.java`:

```java
package in.raysi;

public class OrderService {
    public OrderService() {
        System.out.println("OrderService object created");
    }

    public void placeOrder() {
        System.out.println("Order placed");
    }
}
```

Now register both beans in `beans.xml`:
```xml
<bean id="paymentService" class="in.raysi.PaymentService"/>
<bean id="orderService" class="in.raysi.OrderService"/>
```

Use them in Java:
```java
PaymentService paymentService =
        context.getBean("paymentService", PaymentService.class);
OrderService orderService =
        context.getBean("orderService", OrderService.class);

paymentService.pay();
orderService.placeOrder();
```

[Back to Top](#quick-navigation)

---

## 19. id Attribute

The `id` attribute gives one unique identifier to a bean:
```xml
<bean id="paymentService" class="in.raysi.PaymentService"/>
```
Access via:
```java
PaymentService paymentService =
        context.getBean("paymentService", PaymentService.class);
```

[Back to Top](#quick-navigation)

---

## 20. name Attribute

XML also supports the `name` attribute:
```xml
<bean name="paymentService" class="in.raysi.PaymentService"/>
```
This can also be accessed as:
```java
PaymentService paymentService =
        context.getBean("paymentService", PaymentService.class);
```

At a basic level, both `<bean id="..."/>` and `<bean name="..."/>` allow accessing the bean using that name.

[Back to Top](#quick-navigation)

---

## 21. Multiple Names Using name

- The `id` attribute provides one unique primary name.
- The `name` attribute allows specifying multiple comma-separated or whitespace-separated aliases.

```xml
<bean id="paymentService"
      name="payService, gatewayService, transactionService"
      class="in.raysi.PaymentService"/>
```

Now the same bean instance can be accessed using any of these aliases:
```java
context.getBean("paymentService", PaymentService.class);
context.getBean("payService", PaymentService.class);
context.getBean("gatewayService", PaymentService.class);
context.getBean("transactionService", PaymentService.class);
```
All of these refer to the exact same bean instance.

[Back to Top](#quick-navigation)

---

## 22. External Alias Using alias

When a bean is already defined in an XML file:
```xml
<bean id="paymentService" class="in.raysi.PaymentService"/>
```
We can define an alias externally without modifying the original `<bean>` definition:
```xml
<alias name="paymentService" alias="payService"/>
```

Now this works as well:
```java
PaymentService paymentService =
        context.getBean("payService", PaymentService.class);
```

[Back to Top](#quick-navigation)

---

## 23. Two Common Ways of Dependency Injection in XML

In XML configuration, dependency injection is mainly done in two ways:
1. **Constructor Injection** (`<constructor-arg>`)
2. **Setter Injection** (`<property>`)

> **Note:** XML does not directly support field injection (such as private field reflection without setters/constructors). Spring injects dependencies in XML by invoking either constructors or setter methods.

[Back to Top](#quick-navigation)

---

## 24. Constructor Injection with Bean Reference

Suppose `OrderService` depends on `PaymentService`:

```java
package in.raysi;

public class OrderService {
    private final PaymentService paymentService;

    public OrderService(PaymentService paymentService) {
        System.out.println("OrderService constructor called");
        this.paymentService = paymentService;
    }

    public void placeOrder() {
        paymentService.pay();
        System.out.println("Order placed");
    }
}
```

### XML Configuration:
```xml
<bean id="paymentService" class="in.raysi.PaymentService"/>

<bean id="orderService" class="in.raysi.OrderService">
    <constructor-arg ref="paymentService"/>
</bean>
```

### What happens:
1. Spring creates the `PaymentService` bean.
2. Spring creates the `OrderService` bean.
3. While creating `OrderService`, Spring passes `paymentService` into its constructor.

| Attribute | Meaning |
| :--- | :--- |
| **`constructor-arg`** | Injects value or dependency through a constructor |
| **`ref`** | Specifies a reference to another bean managed in the container |

[Back to Top](#quick-navigation)

---

## 25. Constructor Injection with Simple Value

Suppose `PaymentService` requires a configuration string:

```java
package in.raysi;

public class PaymentService {
    private final String providerName;

    public PaymentService(String providerName) {
        this.providerName = providerName;
    }

    public void pay() {
        System.out.println("Payment successful using " + providerName);
    }
}
```

### XML Configuration:
```xml
<bean id="paymentService" class="in.raysi.PaymentService">
    <constructor-arg value="Razorpay"/>
</bean>
```
`value="Razorpay"` instructs Spring to pass `"Razorpay"` as a literal value, rather than resolving a bean reference.

[Back to Top](#quick-navigation)

---

## 26. Constructor Injection with Multiple Arguments

```java
package in.raysi;

public class PaymentService {
    private final String providerName;
    private final int retryCount;

    public PaymentService(String providerName, int retryCount) {
        this.providerName = providerName;
        this.retryCount = retryCount;
    }

    public void pay() {
        System.out.println("Payment successful using " + providerName);
        System.out.println("Retry count: " + retryCount);
    }
}
```

### XML Configuration:
```xml
<bean id="paymentService" class="in.raysi.PaymentService">
    <constructor-arg value="Razorpay"/>
    <constructor-arg value="3"/>
</bean>
```
Spring matches constructor arguments by order and compatible type.

[Back to Top](#quick-navigation)

---

## 27. Constructor Injection by Index

To guarantee parameter ordering unambiguously, specify the 0-based `index`:

```xml
<bean id="paymentService" class="in.raysi.PaymentService">
    <constructor-arg index="0" value="Razorpay"/>
    <constructor-arg index="1" value="3"/>
</bean>
```
- `index="0"`: First constructor parameter
- `index="1"`: Second constructor parameter

[Back to Top](#quick-navigation)

---

## 28. Constructor Injection by Name

If debug symbols or `-parameters` compiler flags are preserved:

```xml
<bean id="paymentService" class="in.raysi.PaymentService">
    <constructor-arg name="providerName" value="Razorpay"/>
    <constructor-arg name="retryCount" value="3"/>
</bean>
```

[Back to Top](#quick-navigation)

---

## 29. Constructor Injection by Type

When argument ambiguity arises:

```xml
<bean id="paymentService" class="in.raysi.PaymentService">
    <constructor-arg type="java.lang.String" value="Razorpay"/>
    <constructor-arg type="int" value="3"/>
</bean>
```

[Back to Top](#quick-navigation)

---

## 30. Summary of Constructor Injection Options

| Method | Example | Best Use Case |
| :--- | :--- | :--- |
| **By order** | `<constructor-arg value="Razorpay"/>` | Simple constructors with few params |
| **By index** | `<constructor-arg index="0" value="Razorpay"/>` | When order must be strictly guaranteed |
| **By name** | `<constructor-arg name="providerName" value="Razorpay"/>` | When parameter names are available & readability matters |
| **By type** | `<constructor-arg type="java.lang.String" value="Razorpay"/>` | When constructor matching needs disambiguation |

[Back to Top](#quick-navigation)

---

## 31. Create OrderService with Setter

```java
package in.raysi;

public class OrderService {
    private PaymentService paymentService;

    public OrderService() {
        System.out.println("OrderService object created");
    }

    public void setPaymentService(PaymentService paymentService) {
        System.out.println("Setter called for paymentService");
        this.paymentService = paymentService;
    }

    public void placeOrder() {
        paymentService.pay();
        System.out.println("Order placed");
    }
}
```

The JavaBean setter convention is required:
```java
public void setPaymentService(PaymentService paymentService)
```

[Back to Top](#quick-navigation)

---

## 32. XML Configuration for Setter Injection

```xml
<bean id="paymentService" class="in.raysi.PaymentService"/>

<bean id="orderService" class="in.raysi.OrderService">
    <property name="paymentService" ref="paymentService"/>
</bean>
```

### Breakdown of `<property>`:
- `name="paymentService"`: Refers to the property name in `OrderService` (Spring looks for `setPaymentService(...)`).
- `ref="paymentService"`: Refers to the bean id `paymentService` in the IoC container.

### Execution Flow:
1. Instantiate `OrderService` using default no-arg constructor.
2. Resolve bean with ID `paymentService`.
3. Call `setPaymentService(paymentService)`.

[Back to Top](#quick-navigation)

---

## 33. value vs ref

| Attribute | Meaning | Example |
| :--- | :--- | :--- |
| **`value`** | Injects primitive literals or strings (`String`, `int`, `boolean`) | `<constructor-arg value="Razorpay"/>` |
| **`ref`** | Injects a reference to another bean in the Spring container | `<constructor-arg ref="paymentService"/>` |

[Back to Top](#quick-navigation)

---

## 34. Problem: Multiple Beans of Same Type

Suppose `OrderService` depends on `PaymentService`, but two beans exist:

```xml
<bean id="razorpayPaymentService" class="in.raysi.RazorpayPaymentService"/>
<bean id="stripePaymentService" class="in.raysi.StripePaymentService"/>
```
Spring cannot guess which one to inject automatically.

[Back to Top](#quick-navigation)

---

## 35. Solution 1: Explicit Wiring Using ref

```xml
<bean id="razorpayPaymentService" class="in.raysi.RazorpayPaymentService"/>
<bean id="stripePaymentService" class="in.raysi.StripePaymentService"/>

<bean id="orderService" class="in.raysi.OrderService">
    <constructor-arg ref="razorpayPaymentService"/>
</bean>
```
Explicit wiring removes ambiguity completely: Spring knows exactly to inject `razorpayPaymentService`.

[Back to Top](#quick-navigation)

---

## 36. Solution 2: Using primary="true"

```xml
<bean id="razorpayPaymentService"
      class="in.raysi.RazorpayPaymentService"
      primary="true"/>

<bean id="stripePaymentService"
      class="in.raysi.StripePaymentService"/>
```
When Spring resolves dependencies by type, the bean flagged with `primary="true"` is prioritized.

[Back to Top](#quick-navigation)

---

## 37. Solution 3: Using autowire-candidate="false"

```xml
<bean id="razorpayPaymentService"
      class="in.raysi.RazorpayPaymentService"/>

<bean id="stripePaymentService"
      class="in.raysi.StripePaymentService"
      autowire-candidate="false"/>
```
This instructs Spring: **do not consider `stripePaymentService` as a candidate for autowiring**.

[Back to Top](#quick-navigation)

---

## 38. What Is XML Autowiring?

Instead of manually wiring every dependency with `<constructor-arg ref="...">` or `<property ref="...">`, XML autowiring instructs Spring to detect and wire dependencies automatically.

### Without Autowiring (Explicit):
```xml
<bean id="paymentService" class="in.raysi.PaymentService"/>
<bean id="orderService" class="in.raysi.OrderService">
    <property name="paymentService" ref="paymentService"/>
</bean>
```

### With XML Autowiring:
```xml
<bean id="paymentService" class="in.raysi.PaymentService"/>
<bean id="orderService"
      class="in.raysi.OrderService"
      autowire="byName"/>
```

[Back to Top](#quick-navigation)

---

## 39. Common XML Autowiring Modes

| Mode | Meaning |
| :--- | :--- |
| **`byName`** | Matches dependency using property name and bean ID |
| **`byType`** | Matches dependency using matching bean type |
| **`constructor`** | Matches constructor parameter types using available beans in the container |

[Back to Top](#quick-navigation)

---

## 40. autowire="byName"

```xml
<bean id="paymentService" class="in.raysi.PaymentService"/>

<bean id="orderService"
      class="in.raysi.OrderService"
      autowire="byName"/>
```

If `OrderService` has:
```java
public void setPaymentService(PaymentService paymentService) {
    this.paymentService = paymentService;
}
```
1. Spring extracts the property name `paymentService` from `setPaymentService`.
2. Searches the container for a bean whose `id` is `paymentService`.
3. Injects that bean automatically.

[Back to Top](#quick-navigation)

---

## 41. autowire="byType"

```xml
<bean id="abc" class="in.raysi.PaymentService"/>

<bean id="orderService"
      class="in.raysi.OrderService"
      autowire="byType"/>
```

- Spring checks property types instead of names.
- Since `OrderService` needs a `PaymentService`, Spring finds bean `abc` because its type matches `PaymentService`.
- **Limitation:** Fails if multiple beans of type `PaymentService` exist in the container.

[Back to Top](#quick-navigation)

---

## 42. autowire="constructor"

```xml
<bean id="paymentService" class="in.raysi.PaymentService"/>

<bean id="orderService"
      class="in.raysi.OrderService"
      autowire="constructor"/>
```

Spring inspects the constructor of `OrderService`, identifies parameters requiring `PaymentService`, and resolves matching beans from the container.

[Back to Top](#quick-navigation)

---

## 43. Comparison of XML Autowiring Modes

| XML Autowiring Mode | How Spring Matches Dependency | Works Best When |
| :--- | :--- | :--- |
| **`byName`** | Property name matches bean `id` | Bean names are uniform and predictable |
| **`byType`** | Property type matches bean type | Exactly one bean of that type exists |
| **`constructor`** | Constructor parameter type matches bean type | Constructor dependencies have unambiguous types |

> **Guideline:** Explicit wiring with `ref` is clearer and preferred for maintainability. Autowiring in XML should be used judiciously.

[Back to Top](#quick-navigation)

---

## 44. Two Important Scopes

Spring supports multiple bean scopes. The two core scopes are:
1. **`singleton`** (Default)
2. **`prototype`**

[Back to Top](#quick-navigation)

---

## 45. Singleton Scope

By default, Spring beans are singletons:

```xml
<bean id="paymentService" class="in.raysi.PaymentService"/>
```
Spring instantiates exactly one shared object instance inside the container.

```java
PaymentService p1 = context.getBean("paymentService", PaymentService.class);
PaymentService p2 = context.getBean("paymentService", PaymentService.class);

System.out.println(p1 == p2); // Prints: true
```
Both references point to the exact same memory address.

[Back to Top](#quick-navigation)

---

## 46. Prototype Scope

A prototype bean creates a new instance every time it is requested from the container:

```xml
<bean id="paymentService"
      class="in.raysi.PaymentService"
      scope="prototype"/>
```

```java
PaymentService p1 = context.getBean("paymentService", PaymentService.class);
PaymentService p2 = context.getBean("paymentService", PaymentService.class);

System.out.println(p1 == p2); // Prints: false
```

[Back to Top](#quick-navigation)

---

## 47. Singleton Bean Depending on Prototype Bean

Consider this common design scenario:
- `OrderService` -> `singleton`
- `PaymentService` -> `prototype`

```xml
<bean id="paymentService"
      class="in.raysi.PaymentService"
      scope="prototype"/>

<bean id="orderService"
      class="in.raysi.OrderService"
      scope="singleton">
    <constructor-arg ref="paymentService"/>
</bean>
```

> **Critical Rule:** The prototype bean is injected **only once** at the time the singleton bean is created.  
> `OrderService` will keep using that same injected `PaymentService` instance across all calls. It does **not** fetch a new prototype instance per method invocation.

[Back to Top](#quick-navigation)

---

## 48. Custom Init and Destroy Methods

In XML configuration, custom lifecycle callback methods can be configured without implementing Spring-specific interfaces (`InitializingBean`, `DisposableBean`).

```java
package in.raysi;

public class PaymentService {

    public PaymentService() {
        System.out.println("1. PaymentService constructor called");
    }

    public void init() {
        System.out.println("2. PaymentService init method called");
    }

    public void pay() {
        System.out.println("3. Payment successful");
    }

    public void cleanup() {
        System.out.println("4. PaymentService cleanup method called");
    }
}
```

[Back to Top](#quick-navigation)

---

## 49. XML Configuration with init-method and destroy-method

```xml
<bean id="paymentService"
      class="in.raysi.PaymentService"
      init-method="init"
      destroy-method="cleanup"/>
```

| Attribute | Meaning |
| :--- | :--- |
| **`init-method`** | Method to execute after instantiation and dependency injection |
| **`destroy-method`** | Method to execute before container shutdown and bean destruction |

[Back to Top](#quick-navigation)

---

## 50. Closing the Context

For destroy callbacks to run, the `ApplicationContext` must be properly closed.

### Using Explicit Close:
```java
ClassPathXmlApplicationContext context =
        new ClassPathXmlApplicationContext("beans.xml");

PaymentService paymentService =
        context.getBean("paymentService", PaymentService.class);
paymentService.pay();

context.close(); // Triggers destroy methods
```

### Recommended Approach: Try-With-Resources
```java
try (ClassPathXmlApplicationContext context =
             new ClassPathXmlApplicationContext("beans.xml")) {
    PaymentService paymentService =
            context.getBean("paymentService", PaymentService.class);
    paymentService.pay();
} // Context automatically closes here
```

[Back to Top](#quick-navigation)

---

## 51. Prototype Scope with Init and Destroy

```xml
<bean id="paymentService"
      class="in.raysi.PaymentService"
      scope="prototype"
      init-method="init"
      destroy-method="cleanup"/>
```

For prototype beans:
1. Spring creates the object.
2. Spring injects dependencies.
3. Spring calls the `init-method`.
4. Spring hands the instance to the caller and ceases tracking its lifecycle.

> **Crucial Rule:** Spring does **not** call `destroy-method` for prototype beans when the container closes. The client code is responsible for cleaning up prototype instances.

[Back to Top](#quick-navigation)

---

## 52. Injecting a List

```java
package in.raysi;

import java.util.List;

public class UserService {
    private final List<String> userNames;

    public UserService(List<String> userNames) {
        this.userNames = userNames;
    }

    public void printUsers() {
        System.out.println(userNames);
    }
}
```

### XML Configuration:
```xml
<bean id="userService" class="in.raysi.UserService">
    <constructor-arg name="userNames">
        <list>
            <value>Aditya</value>
            <value>Rohit</value>
            <value>Rohan</value>
        </list>
    </constructor-arg>
</bean>
```

[Back to Top](#quick-navigation)

---

## 53. Injecting a Map

```java
package in.raysi;

import java.util.Map;

public class PaymentService {
    private final Map<String, Double> userBalances;

    public PaymentService(Map<String, Double> userBalances) {
        this.userBalances = userBalances;
    }

    public void printBalances() {
        System.out.println(userBalances);
    }
}
```

### XML Configuration:
```xml
<bean id="paymentService" class="in.raysi.PaymentService">
    <constructor-arg name="userBalances">
        <map>
            <entry key="Aditya" value="2.0"/>
            <entry key="Rohit" value="2.5"/>
            <entry key="Rohan" value="3.0"/>
        </map>
    </constructor-arg>
</bean>
```

[Back to Top](#quick-navigation)

---

## 54. Why Split XML Files?

In enterprise codebases, a single configuration file becomes unmaintainable. Spring allows modularizing configuration across domain contexts:

```text
src/main/resources
 ├── applicationContext.xml  (Main Aggregator)
 ├── orderContext.xml        (Order subsystem)
 └── userContext.xml         (User subsystem)
```

[Back to Top](#quick-navigation)

---

## 55. Example: orderContext.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="
           http://www.springframework.org/schema/beans
           http://www.springframework.org/schema/beans/spring-beans.xsd">

    <bean id="paymentService" class="in.raysi.PaymentService"/>

    <bean id="orderService" class="in.raysi.OrderService">
        <constructor-arg ref="paymentService"/>
    </bean>
</beans>
```

[Back to Top](#quick-navigation)

---

## 56. Example: userContext.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="
           http://www.springframework.org/schema/beans
           http://www.springframework.org/schema/beans/spring-beans.xsd">

    <bean id="userService" class="in.raysi.UserService"/>
</beans>
```

[Back to Top](#quick-navigation)

---

## 57. Main File: applicationContext.xml

Aggregates module files using `<import>`:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="
           http://www.springframework.org/schema/beans
           http://www.springframework.org/schema/beans/spring-beans.xsd">

    <import resource="orderContext.xml"/>
    <import resource="userContext.xml"/>

</beans>
```

### Loading in Java:
```java
ClassPathXmlApplicationContext context =
        new ClassPathXmlApplicationContext("applicationContext.xml");
```

[Back to Top](#quick-navigation)

---

## 58. Can XML and Annotations Work Together?

**Yes.** Both XML tags and annotations serve the identical underlying purpose: generating `BeanDefinition` metadata for the IoC container. A hybrid project can combine:
- Legacy beans declared in XML.
- Modern domain beans discovered via `@Component` scanning.
- Infrastructure beans instantiated via Java `@Configuration` and `@Bean`.

[Back to Top](#quick-navigation)

---

## 59. Component Scanning from XML

To activate annotation scanning from an XML configuration file, use `<context:component-scan>`:

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

    <!-- Scans package and detects @Component, @Service, @Repository -->
    <context:component-scan base-package="in.raysi"/>

</beans>
```

[Back to Top](#quick-navigation)

---

## 60. Should Beginners Mix XML and Annotations?

For foundational learning, study each strategy independently:
1. Master pure annotation-based configuration first.
2. Master pure XML-based configuration next.
3. Understand hybrid patterns for legacy migration scenarios.

Mixing both prematurely introduces debugging friction due to circular references, duplicate bean definitions, and overlapping lifecycle declarations.

[Back to Top](#quick-navigation)

---

## Final Summary & Key Takeaways

- **Configuration Metadata:** XML provides metadata instructions that Spring converts into internal `BeanDefinition` records.
- **`<bean>` Definition:** Defines managed components. `id` provides the unique name; `class` gives the fully qualified class name.
- **Dependency Injection:** Done via `<constructor-arg>` (Constructor Injection) or `<property>` (Setter Injection).
- **`value` vs `ref`:** `value` is for literals/primitives; `ref` wires other beans from the container.
- **Disambiguation:** When multiple beans of the same type exist, explicit wiring using `ref="..."` is the clearest approach. `primary="true"` or `autowire-candidate="false"` can also guide resolution.
- **Autowiring Modes:** XML supports `byName`, `byType`, and `constructor` autowiring.
- **Scopes:** 
  - `singleton`: One shared object per container (default).
  - `prototype`: A new instance created on every `getBean()` request.
- **Lifecycles:** `init-method` and `destroy-method` manage callbacks without coupling classes to Spring interfaces. Spring does not manage destroy callbacks for prototype beans.
- **Modularity:** Large configurations can be split across multiple context files and imported using `<import resource="..."/>`.
- **Interoperability:** XML and annotations coexist seamlessly through `<context:component-scan>`.

[Back to Top](#quick-navigation)

---
*Document prepared for raysi.*

