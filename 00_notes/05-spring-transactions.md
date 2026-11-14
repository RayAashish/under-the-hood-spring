# Module 05: Transactions & Concurrency Control

---

<a id="table-of-contents"></a>
## 📑 Table of Contents
- [1. Database Transaction Fundamentals (Lecture 33)](#1-database-transaction-fundamentals-lecture-33)
  - [1.1 What is a Database Transaction?](#11-what-is-a-database-transaction)
  - [1.2 ACID Properties from First Principles](#12-acid-properties-from-first-principles)
  - [1.3 Concurrency Phenomena & Read Anomalies](#13-concurrency-phenomena--read-anomalies)
- [2. Spring Transaction Management & `@Transactional` (Lecture 34)](#2-spring-transaction-management--transactional-lecture-34)
  - [2.1 Declarative Transaction Architecture & AOP Proxies](#21-declarative-transaction-architecture--aop-proxies)
  - [2.2 Transaction Propagation Behaviors](#22-transaction-propagation-behaviors)
  - [2.3 The `UnexpectedRollbackException` Trap](#23-the-unexpectedrollbackexception-trap)
  - [2.4 Database Isolation Levels](#24-database-isolation-levels)
  - [2.5 Rollback Rules: Checked vs Unchecked Exceptions](#25-rollback-rules-checked-vs-unchecked-exceptions)
  - [2.6 The Self-Invocation Pitfall & Solutions](#26-the-self-invocation-pitfall--solutions)

---

## 1. Database Transaction Fundamentals (Lecture 33)

### 1.1 What is a Database Transaction?
A **Database Transaction** is a sequence of one or more database operations treated as a **single, indivisible logical unit of work**.

#### The Classic Bank Transfer Problem:
Suppose Account A transfers ₹5,000 to Account B:
1. `UPDATE accounts SET balance = balance - 5000 WHERE id = 'A';`
2. *(System crash, network failure, or DB error occurs here)*
3. `UPDATE accounts SET balance = balance + 5000 WHERE id = 'B';`

Without a transaction boundary, ₹5,000 vanishes from Account A without ever reaching Account B! A transaction guarantees that either **both** updates succeed (**Commit**) or **neither** update takes effect (**Rollback**).

```
Transaction Begins
       │
       ▼
Debit Account A (balance - 5000)
       │
       ├── System Error? ──[ YES ]──> Rollback (Account A restored to original)
       │
      [ NO ]
       │
       ▼
Credit Account B (balance + 5000)
       │
       ▼
Commit (Changes permanently written to disk)
```

[⬆ Back to Top](#table-of-contents)

---

### 1.2 ACID Properties from First Principles

| Property | Meaning | Guarantee |
| :--- | :--- | :--- |
| **Atomicity** | All or Nothing | All SQL operations in the transaction succeed together, or the entire transaction is rolled back completely. |
| **Consistency** | Invariant Preservation | The database moves from one valid state to another valid state, obeying all schema constraints, foreign keys, and business rules. |
| **Isolation** | Concurrency Protection | Dictates whether and how concurrent executing transactions are shielded from viewing each other's intermediate uncommitted states. |
| **Durability** | Permanent Survival | Once a transaction commits, its modifications survive crashes, power cuts, or server restarts (written to WAL/redo log). |

[⬆ Back to Top](#table-of-contents)

---

### 1.3 Concurrency Phenomena & Read Anomalies
When multiple database transactions run simultaneously without adequate isolation, four critical data anomalies can occur:

#### 1. Dirty Read
Transaction A reads uncommitted modifications made by Transaction B. If Transaction B rolls back, Transaction A is left holding invalid, phantom data.
```
Tx 1: UPDATE account SET balance = 10000;
Tx 2: SELECT balance FROM account; --> Reads 10000 (Dirty Read!)
Tx 1: ROLLBACK; (Real balance is still 5000!)
```

#### 2. Non-Repeatable Read (Fuzzy Read)
Transaction A reads a row. Transaction B modifies that same row and commits. Transaction A re-reads the row and observes different column values within the same transaction.
```
Tx 1: SELECT status FROM orders WHERE id = 10; --> "PENDING"
Tx 2: UPDATE orders SET status = "SHIPPED" WHERE id = 10; COMMIT;
Tx 1: SELECT status FROM orders WHERE id = 10; --> "SHIPPED" (Non-repeatable!)
```

#### 3. Phantom Read
Transaction A queries a range of rows matching a predicate. Transaction B inserts new rows matching that predicate and commits. Transaction A re-executes the query and discovers newly appeared "phantom" rows.
```
Tx 1: SELECT COUNT(*) FROM students WHERE marks > 80; --> Returns 5
Tx 2: INSERT INTO students (name, marks) VALUES ('Rohan', 92); COMMIT;
Tx 1: SELECT COUNT(*) FROM students WHERE marks > 80; --> Returns 6 (Phantom!)
```

#### 4. Lost Update
Two transactions read the same row concurrently, calculate an update, and write back. The transaction that commits last overwrites the changes made by the first without considering its update.

[⬆ Back to Top](#table-of-contents)

---

## 2. Spring Transaction Management & `@Transactional` (Lecture 34)

### 2.1 Declarative Transaction Architecture & AOP Proxies
Instead of manually opening connections and calling `conn.commit()` / `conn.rollback()` in `try-catch` blocks, Spring provides **Declarative Transaction Management** via `@Transactional`.

```
Client
  │
  ▼
┌────────────────────────────────────────────────────────┐
│ Spring AOP Transactional Proxy                         │
│  1. Intercepts method call                             │
│  2. Obtains connection from DataSource                 │
│  3. Begins transaction (conn.setAutoCommit(false))     │
│  4. Binds connection to current thread (ThreadLocal)   │
└───────────────────────┬────────────────────────────────┘
                        │
                        ▼
┌────────────────────────────────────────────────────────┐
│ Target Service (e.g., PaymentServiceImpl)              │
│  - Executes business queries using current connection  │
└───────────────────────┬────────────────────────────────┘
                        │
                        ▼
┌────────────────────────────────────────────────────────┐
│ Spring AOP Transactional Proxy                         │
│  - If method completes normally: conn.commit()         │
│  - If RuntimeException thrown: conn.rollback()         │
│  - Restores connection autoCommit & releases to pool   │
└────────────────────────────────────────────────────────┘
```

[⬆ Back to Top](#table-of-contents)

---

### 2.2 Transaction Propagation Behaviors
Propagation defines what happens if a transactional method is called by another method that already has an active transaction.

```java
@Transactional(propagation = Propagation.REQUIRED)
public void performOperation() { ... }
```

| Propagation | Behavior |
| :--- | :--- |
| **`REQUIRED`** *(Default)* | Joins the current transaction if one exists; creates a new transaction if none exists. |
| **`REQUIRES_NEW`** | **Suspends** the existing transaction and creates a completely independent new transaction. The outer transaction commits/rolls back independently of the inner. |
| **`SUPPORTS`** | Runs inside existing transaction if one exists; runs non-transactionally if none exists. |
| **`NOT_SUPPORTED`** | Always executes non-transactionally. Suspends any existing active transaction. |
| **`MANDATORY`** | Requires an existing transaction to be active. If called without one, throws `IllegalTransactionStateException`. |
| **`NEVER`** | Requires that **no** transaction is active. Throws `IllegalTransactionStateException` if called inside an active transaction. |
| **`NESTED`** | Executes within a nested transaction using **JDBC Savepoints**. If the nested method fails, it rolls back to the savepoint without affecting the outer transaction. |

[⬆ Back to Top](#table-of-contents)

---

### 2.3 The `UnexpectedRollbackException` Trap
This is one of the most common enterprise Spring transaction interview questions and production bugs.

#### The Scenario:
```java
@Service
public class OrderService {
    private final PaymentService paymentService;

    @Transactional // Starts Transaction A (REQUIRED)
    public void placeOrder() {
        // ... save order ...
        try {
            paymentService.processPayment(); // Uses REQUIRED (Joins Transaction A)
        } catch (Exception e) {
            System.out.println("Payment failed, but I caught the exception!");
        }
        // ... continue doing other tasks ...
    }
}
```

```java
@Service
public class PaymentService {
    @Transactional // Propagation.REQUIRED (Default)
    public void processPayment() {
        throw new RuntimeException("Payment gateway unreachable!");
    }
}
```

#### What Happens Step-by-Step:
1. `OrderService.placeOrder()` starts **Transaction A**.
2. It invokes `PaymentService.processPayment()`. Because its propagation is `REQUIRED`, it **joins Transaction A**.
3. `processPayment()` throws a `RuntimeException`.
4. Spring's `TransactionInterceptor` on `PaymentService` intercepts the exception and marks **Transaction A as `rollback-only`**.
5. The exception bubbles back up to `placeOrder()`, which catches it inside a `try-catch` block.
6. `placeOrder()` reaches the end of its method and attempts to **commit** Transaction A.
7. Spring inspects Transaction A and detects that it has been marked `rollback-only`.
8. Spring **rolls back the entire transaction** and throws:
   **`org.springframework.transaction.UnexpectedRollbackException: Transaction rolled back because it has been marked as rollback-only`**!

#### How to Fix It:
If the inner operation's failure should not kill the outer transaction, mark the inner method with **`Propagation.REQUIRES_NEW`**:
```java
@Transactional(propagation = Propagation.REQUIRES_NEW)
public void processPayment() { ... }
```

[⬆ Back to Top](#table-of-contents)

---

### 2.4 Database Isolation Levels

```java
@Transactional(isolation = Isolation.READ_COMMITTED)
public void queryFinancialRecords() { ... }
```

| Isolation Level | Dirty Read | Non-Repeatable Read | Phantom Read | Notes |
| :--- | :---: | :---: | :---: | :--- |
| **`READ_UNCOMMITTED`** | Allowed | Allowed | Allowed | Lowest isolation; highest concurrency. Reads uncommitted data. |
| **`READ_COMMITTED`** | **Prevented** | Allowed | Allowed | Default for PostgreSQL, Oracle, SQL Server. |
| **`REPEATABLE_READ`** | **Prevented** | **Prevented** | Allowed | Default for MySQL InnoDB (MVCC prevents phantoms in most cases). |
| **`SERIALIZABLE`** | **Prevented** | **Prevented** | **Prevented** | Highest isolation; transactions execute as if serially ordered. |

[⬆ Back to Top](#table-of-contents)

---

### 2.5 Rollback Rules: Checked vs Unchecked Exceptions

> [!WARNING]
> By default, Spring `@Transactional` rolls back **ONLY on unchecked exceptions** (`RuntimeException` and `Error`). It **COMMITS on checked exceptions** (`Exception`, `IOException`, `SQLException`)!

```java
// DANGEROUS: If FileNotFoundException occurs, database changes WILL COMMIT!
@Transactional
public void processFile() throws IOException {
    repository.save(entity);
    throw new IOException("Disk error"); // Changes are COMMITTED by default!
}
```

#### The Production Standard:
Always specify `rollbackFor = Exception.class` to ensure rollback on **all** exceptions:
```java
@Transactional(rollbackFor = Exception.class)
public void safeTransactionalOperation() throws CustomCheckedException {
    // Guaranteed rollback on ALL exceptions
}
```

[⬆ Back to Top](#table-of-contents)

---

### 2.6 The Self-Invocation Pitfall & Solutions

#### The Bug:
```java
@Service
public class StudentService {

    public void processBatch() {
        // Direct internal 'this' method call bypasses Spring Proxy!
        saveStudentWithTx();
    }

    @Transactional
    public void saveStudentWithTx() {
        studentRepository.save(new Student());
    }
}
```
**Why the transaction does not start**:
Spring creates a dynamic proxy around `StudentService`. Outside callers invoke the proxy, which executes the transaction interceptor.
However, when `processBatch()` calls `saveStudentWithTx()`, it calls `this.saveStudentWithTx()` directly inside the JVM on the raw target object, **bypassing the proxy entirely**! The `@Transactional` annotation is completely ignored!

#### Solution 1: Extract to a Separate Service Class (Recommended)
Move `saveStudentWithTx()` into another Spring `@Service` bean and inject it into `StudentService`.

#### Solution 2: Self-Injection
Inject the proxy of itself:
```java
@Service
public class StudentService {
    @Autowired
    @Lazy
    private StudentService self;

    public void processBatch() {
        self.saveStudentWithTx(); // Calls through proxy!
    }

    @Transactional
    public void saveStudentWithTx() { ... }
}
```

#### Solution 3: Programmatic `TransactionTemplate`
```java
@Service
public class StudentService {
    private final TransactionTemplate txTemplate;

    public StudentService(TransactionTemplate txTemplate) {
        this.txTemplate = txTemplate;
    }

    public void processBatch() {
        txTemplate.execute(status -> {
            studentRepository.save(new Student());
            return null;
        });
    }
}
```

[⬆ Back to Top](#table-of-contents)
