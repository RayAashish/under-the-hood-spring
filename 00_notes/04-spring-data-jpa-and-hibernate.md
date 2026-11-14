# Module 04: Data Persistence: JDBC, Hibernate & Spring Data JPA

---

<a id="table-of-contents"></a>
## 📑 Table of Contents
- [1. JDBC Fundamentals & Raw Database Operations (Lecture 26)](#1-jdbc-fundamentals--raw-database-operations-lecture-26)
  - [1.1 JDBC Architecture & Database Drivers](#11-jdbc-architecture--database-drivers)
  - [1.2 Core JDBC Components: `Connection`, `Statement`, `PreparedStatement`, `ResultSet`](#12-core-jdbc-components-connection-statement-preparedstatement-resultset)
  - [1.3 SQL Injection Vulnerability & `PreparedStatement` Defense](#13-sql-injection-vulnerability--preparedstatement-defense)
  - [1.4 Safe Resource Management with `try-with-resources`](#14-safe-resource-management-with-try-with-resources)
  - [1.5 Complete Raw JDBC CRUD Implementation](#15-complete-raw-jdbc-crud-implementation)
- [2. Spring JDBC & `JdbcTemplate` (Lecture 27)](#2-spring-jdbc--jdbctemplate-lecture-27)
  - [2.1 The Boilerplate Problem of Raw JDBC](#21-the-boilerplate-problem-of-raw-jdbc)
  - [2.2 The Spring Template Pattern & `JdbcTemplate`](#22-the-spring-template-pattern--jdbctemplate)
  - [2.3 Row Mapping: `RowMapper<T>` vs `ResultSetExtractor<T>`](#23-row-mapping-rowmappert-vs-resultsetextractort)
  - [2.4 Named Parameters with `NamedParameterJdbcTemplate`](#24-named-parameters-with-namedparameterjdbctemplate)
  - [2.5 Spring DataAccessException Hierarchy](#25-spring-dataaccessexception-hierarchy)
- [3. Introduction to Hibernate & ORM (Lecture 28)](#3-introduction-to-hibernate--orm-lecture-28)
  - [3.1 Object-Relational Impedance Mismatch](#31-object-relational-impedance-mismatch)
  - [3.2 JPA vs Hibernate vs Spring Data JPA](#32-jpa-vs-hibernate-vs-spring-data-jpa)
  - [3.3 Core JPA Annotations: `@Entity`, `@Table`, `@Id`, `@GeneratedValue`](#33-core-jpa-annotations-entity-table-id-generatedvalue)
  - [3.4 Primary Key Generation Strategies (`IDENTITY`, `SEQUENCE`, `AUTO`)](#34-primary-key-generation-strategies-identity-sequence-auto)
  - [3.5 Value Types vs Entity Types: `@Embeddable` & `@Embedded`](#35-value-types-vs-entity-types-embeddable--embedded)
- [4. Hibernate Internals & Persistence Context (Lecture 29)](#4-hibernate-internals--persistence-context-lecture-29)
  - [4.1 The Persistence Context & First-Level Cache](#41-the-persistence-context--first-level-cache)
  - [4.2 Automatic Dirty Checking](#42-automatic-dirty-checking)
  - [4.3 The Four Entity Lifecycle States](#43-the-four-entity-lifecycle-states)
  - [4.4 Flushing vs Committing](#44-flushing-vs-committing)
- [5. JPA Relationships: Mapping, Ownership & Cardinality (Lecture 30)](#5-jpa-relationships-mapping-ownership--cardinality-lecture-30)
  - [5.1 Relational Foreign Keys vs Object References](#51-relational-foreign-keys-vs-object-references)
  - [5.2 Relationship Cardinality: `@OneToOne`, `@OneToMany`, `@ManyToOne`, `@ManyToMany`](#52-relationship-cardinality-onetoone-onetomany-manytoone-manytomany)
  - [5.3 Unidirectional vs Bidirectional Mappings](#53-unidirectional-vs-bidirectional-mappings)
  - [5.4 The Owning Side vs Inverse Side (`mappedBy` & `@JoinColumn`)](#54-the-owning-side-vs-inverse-side-mappedby--joincolumn)
- [6. Cascading, Fetch Strategies & The N+1 Problem (Lecture 31)](#6-cascading-fetch-strategies--the-n1-problem-lecture-31)
  - [6.1 Entity Cascading (`CascadeType`) & Orphan Removal](#61-entity-cascading-cascadetype--orphan-removal)
  - [6.2 Fetch Strategies: `FetchType.LAZY` vs `FetchType.EAGER`](#62-fetch-strategies-fetchtypelazy-vs-fetchtypeeager)
  - [6.3 The N+1 Query Problem Explained](#63-the-n1-query-problem-explained)
  - [6.4 Production Solutions: `JOIN FETCH`, `@EntityGraph`, and `@BatchSize`](#64-production-solutions-join-fetch-entitygraph-and-batchsize)
- [7. Spring Data JPA Repositories (Lecture 32)](#7-spring-data-jpa-repositories-lecture-32)
  - [7.1 Repository Hierarchy: `Repository`, `CrudRepository`, `JpaRepository`](#71-repository-hierarchy-repository-crudrepository-jparepository)
  - [7.2 Derived Query Methods & Keyword Parsing](#72-derived-query-methods--keyword-parsing)
  - [7.3 Custom Queries with `@Query`: JPQL vs Native SQL](#73-custom-queries-with-query-jpql-vs-native-sql)
  - [7.4 Pagination and Sorting (`Pageable`, `Sort`, `Page<T>` vs `Slice<T>`)](#74-pagination-and-sorting-pageable-sort-paget-vs-slicet)

---

## 1. JDBC Fundamentals & Raw Database Operations (Lecture 26)

### 1.1 JDBC Architecture & Database Drivers
**Java Database Connectivity (JDBC)** is the standard Java API (`java.sql` and `javax.sql`) for connecting Java applications to relational database management systems (RDBMS).

```
┌──────────────────────────────────────┐
│       Java Application Code          │
└──────────────────┬───────────────────┘
                   │ Calls standard java.sql interfaces
                   ▼
┌──────────────────────────────────────┐
│           JDBC API (Interfaces)      │
│  Connection, Statement, ResultSet    │
└──────────────────┬───────────────────┘
                   │ Implemented by vendor driver
                   ▼
┌──────────────────────────────────────┐
│ JDBC Driver (e.g., MySQL Connector/J)│
└──────────────────┬───────────────────┘
                   │ Network Protocol (TCP Port 3306)
                   ▼
┌──────────────────────────────────────┐
│         MySQL Database Server        │
└──────────────────────────────────────┘
```

[⬆ Back to Top](#table-of-contents)

---

### 1.2 Core JDBC Components: `Connection`, `Statement`, `PreparedStatement`, `ResultSet`
1. **`DriverManager`**: Factory class that creates connections based on registered driver and database connection URL.
2. **`Connection`**: Represents an active physical socket session with the database. Manages transactions (`setAutoCommit()`, `commit()`, `rollback()`).
3. **`Statement`**: Used to execute static SQL statements. Vulnerable to SQL injection.
4. **`PreparedStatement`**: Pre-compiles SQL queries on the database engine. Accepts parameterized placeholders (`?`). High performance and immune to SQL injection.
5. **`ResultSet`**: A cursor-based tabular data structure representing database query result rows.

[⬆ Back to Top](#table-of-contents)

---

### 1.3 SQL Injection Vulnerability & `PreparedStatement` Defense

#### The Vulnerability with `Statement`
Concatenating raw user input directly into SQL strings allows attackers to alter query logic:
```java
// DANGEROUS ANTI-PATTERN: String concatenation
String query = "SELECT * FROM users WHERE email = '" + userInputEmail + "' AND password = '" + userPassword + "'";
Statement stmt = connection.createStatement();
ResultSet rs = stmt.executeQuery(query);
```
If an attacker inputs:
`userInputEmail = admin@raysi.com' --`
The resulting query becomes:
`SELECT * FROM users WHERE email = 'admin@raysi.com' --' AND password = '...'`
The `--` comments out the password verification entirely, granting unauthorized admin access!

#### The Defense with `PreparedStatement`
```java
// SAFE: Parameterized Query
String sql = "SELECT * FROM users WHERE email = ? AND password = ?";
PreparedStatement pstmt = connection.prepareStatement(sql);
pstmt.setString(1, userInputEmail);
pstmt.setString(2, userPassword);
ResultSet rs = pstmt.executeQuery();
```
**Why it is secure**: The database compiles the SQL query structure **first**. Parameters passed via `pstmt.setString()` are treated purely as literal string values, never as executable SQL commands.

[⬆ Back to Top](#table-of-contents)

---

### 1.4 Safe Resource Management with `try-with-resources`
Database connections, statements, and result sets represent scarce operating system file descriptors and TCP sockets. Failing to close them causes **Connection Pool Exhaustion**.

Always manage JDBC resources using Java's `try-with-resources` construct (implementing `AutoCloseable`):
```java
try (Connection conn = DriverManager.getConnection(url, user, pass);
     PreparedStatement pstmt = conn.prepareStatement(sql);
     ResultSet rs = pstmt.executeQuery()) {
    while (rs.next()) {
        // Process row
    }
} // Resources closed automatically in reverse order even if exceptions occur!
```

[⬆ Back to Top](#table-of-contents)

---

### 1.5 Complete Raw JDBC CRUD Implementation

```java
package com.raysi.jdbc;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RawJdbcStudentRepository {

    private final String url = "jdbc:mysql://localhost:3306/raysi_db";
    private final String user = "root";
    private final String pass = "secret123";

    // 1. CREATE
    public void save(String name, String email) {
        String sql = "INSERT INTO students (name, email) VALUES (?, ?)";
        try (Connection conn = DriverManager.getConnection(url, user, pass);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, email);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 2. READ ALL
    public List<StudentModel> findAll() {
        List<StudentModel> students = new ArrayList<>();
        String sql = "SELECT id, name, email FROM students";
        try (Connection conn = DriverManager.getConnection(url, user, pass);
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                students.add(new StudentModel(
                    rs.getLong("id"),
                    rs.getString("name"),
                    rs.getString("email")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return students;
    }
}
```

[⬆ Back to Top](#table-of-contents)

---

## 2. Spring JDBC & `JdbcTemplate` (Lecture 27)

### 2.1 The Boilerplate Problem of Raw JDBC
Raw JDBC requires over 80% repetitive infrastructure code:
- Opening connections.
- Creating statements.
- Binding parameters index by index (`pstmt.setString(1, ...)`).
- Iterating cursors (`while (rs.next())`).
- Catching checked `SQLException`.
- Manually closing resources in `finally` blocks.

[⬆ Back to Top](#table-of-contents)

---

### 2.2 The Spring Template Pattern & `JdbcTemplate`
Spring's `JdbcTemplate` eliminates all boilerplate by executing the Template Method Design Pattern:
- **Spring Handles**: Connection acquisition, statement preparation, exception handling, transaction synchronization, and resource cleanup.
- **Developer Supplies**: SQL query string and Row-to-Object mapping logic.

```java
package com.raysi.repository;

import com.raysi.model.Student;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class StudentJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    public StudentJdbcRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public int save(Student student) {
        String sql = "INSERT INTO students (name, email) VALUES (?, ?)";
        return jdbcTemplate.update(sql, student.getName(), student.getEmail());
    }

    public List<Student> findAll() {
        String sql = "SELECT id, name, email FROM students";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new Student(
            rs.getLong("id"),
            rs.getString("name"),
            rs.getString("email")
        ));
    }
}
```

[⬆ Back to Top](#table-of-contents)

---

### 2.3 Row Mapping: `RowMapper<T>` vs `ResultSetExtractor<T>`
- **`RowMapper<T>`**: Maps a **single row** of a `ResultSet` to a domain object. Called once per row by Spring.
  ```java
  RowMapper<Student> mapper = (rs, rowNum) -> new Student(
      rs.getLong("id"),
      rs.getString("name"),
      rs.getString("email")
  );
  ```
- **`ResultSetExtractor<T>`**: Gives full control over the **entire** `ResultSet`. Called once for the complete query. Essential when mapping 1-to-Many relationships where multiple SQL join rows correspond to a single parent object containing a list of children.

[⬆ Back to Top](#table-of-contents)

---

### 2.4 Named Parameters with `NamedParameterJdbcTemplate`
Positional parameters (`?`) are error-prone in large queries. `NamedParameterJdbcTemplate` allows named placeholders (`:parameterName`):

```java
package com.raysi.repository;

import com.raysi.model.Student;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class StudentNamedParamRepository {

    private final NamedParameterJdbcTemplate namedTemplate;

    public StudentNamedParamRepository(NamedParameterJdbcTemplate namedTemplate) {
        this.namedTemplate = namedTemplate;
    }

    public int updateEmail(Long id, String newEmail) {
        String sql = "UPDATE students SET email = :email WHERE id = :id";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("email", newEmail)
                .addValue("id", id);
        return namedTemplate.update(sql, params);
    }
}
```

[⬆ Back to Top](#table-of-contents)

---

### 2.5 Spring DataAccessException Hierarchy
Raw JDBC throws checked `java.sql.SQLException`. Spring translates database-specific vendor error codes into a consistent, portable, **unchecked** exception hierarchy rooted at **`DataAccessException`**:
- `DuplicateKeyException` (e.g. Unique constraint violation).
- `DataIntegrityViolationException` (e.g. Foreign key constraint violation).
- `CannotAcquireLockException` (e.g. Deadlock detected).
- `BadSqlGrammarException` (e.g. Syntax error or missing table).

[⬆ Back to Top](#table-of-contents)

---

## 3. Introduction to Hibernate & ORM (Lecture 28)

### 3.1 Object-Relational Impedance Mismatch
Java and Relational Databases operate under fundamentally different paradigms:
1. **Granularity**: Java models rich domain types (e.g., `Address`), whereas databases use primitive column types (`VARCHAR`, `INT`).
2. **Subtyping / Inheritance**: Java natively supports polymorphism and class inheritance; relational tables do not have inheritance.
3. **Identity**: Java uses object reference equality (`==`) and value equality (`equals()`); databases use Primary Keys (`id = 10`).
4. **Associations**: Java uses directed object references (`student.getDepartment()`); databases use bidirectional Foreign Keys with JOIN operations.

**Object-Relational Mapping (ORM)** is the bridge that automates the translation between Java in-memory objects and relational database rows.

[⬆ Back to Top](#table-of-contents)

---

### 3.2 JPA vs Hibernate vs Spring Data JPA
- **JPA (Jakarta Persistence API)**: A **specification** (standard set of interfaces and annotations such as `EntityManager`, `@Entity`, `@Table`). It contains no executable code.
- **Hibernate**: The leading **implementation** (ORM provider) of the JPA specification.
- **Spring Data JPA**: An abstraction layer built on top of JPA that eliminates DAO boilerplate by dynamically generating repository implementations at runtime.

```
┌──────────────────────────────────────────────┐
│ Spring Data JPA (JpaRepository Interfaces)   │
└──────────────────────┬───────────────────────┘
                       │ delegates to
┌──────────────────────▼───────────────────────┐
│ JPA Specification (EntityManager, @Entity)   │
└──────────────────────┬───────────────────────┘
                       │ implemented by
┌──────────────────────▼───────────────────────┐
│ Hibernate ORM (Bytecode enhancer, SQL gen)   │
└──────────────────────┬───────────────────────┘
                       │ translates to
┌──────────────────────▼───────────────────────┐
│ JDBC & Database (MySQL / PostgreSQL)         │
└──────────────────────────────────────────────┘
```

[⬆ Back to Top](#table-of-contents)

---

### 3.3 Core JPA Annotations: `@Entity`, `@Table`, `@Id`, `@GeneratedValue`

```java
package com.raysi.entity;

import jakarta.persistence.*;

@Entity                                  // Marks class as a JPA entity managed by Hibernate
@Table(name = "tbl_students")            // Maps to specific database table name
public class Student {

    @Id                                  // Designates primary key field
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-increment column
    private Long id;

    @Column(name = "full_name", nullable = false, length = 100)
    private String name;

    @Column(name = "email_address", unique = true, nullable = false)
    private String email;

    @Transient                           // Not mapped to any database column
    private String temporaryToken;

    // Default no-arg constructor required by Hibernate reflection
    public Student() {}
}
```

[⬆ Back to Top](#table-of-contents)

---

### 3.4 Primary Key Generation Strategies (`IDENTITY`, `SEQUENCE`, `AUTO`)
- **`GenerationType.IDENTITY`**: Relies on database `AUTO_INCREMENT` column.
  - *Caveat*: Primary key is generated only upon row insertion. Hibernate cannot batch INSERT statements because it must execute each INSERT immediately to obtain the generated ID.
- **`GenerationType.SEQUENCE`**: Uses database sequences (PostgreSQL, Oracle).
  - *Advantage*: Hibernate can pre-allocate blocks of IDs (via `allocationSize`), enabling high-performance JDBC batch inserts.
- **`GenerationType.AUTO`**: Lets Hibernate choose strategy based on database dialect.

[⬆ Back to Top](#table-of-contents)

---

### 3.5 Value Types vs Entity Types: `@Embeddable` & `@Embedded`
An **Entity** has its own database identity (Primary Key) and lifecycle. A **Value Type** has no independent identity and belongs entirely to an owning entity.

```java
package com.raysi.entity;

import jakarta.persistence.Embeddable;

@Embeddable // Reusable component class
public class Address {
    private String street;
    private String city;
    private String zipcode;
    // Constructors, Getters, Setters
}
```

```java
@Entity
@Table(name = "students")
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Embedded // Flattens Address fields directly into students table columns!
    private Address address;
}
```

[⬆ Back to Top](#table-of-contents)

---

## 4. Hibernate Internals & Persistence Context (Lecture 29)

### 4.1 The Persistence Context & First-Level Cache
The **Persistence Context** is Hibernate's in-memory scratchpad (`EntityManager`). It manages a set of entity instances associated with the currently executing database transaction.

#### The First-Level Cache (L1 Cache):
- Scoped to the current `EntityManager` / `@Transactional` method.
- When an entity is requested via `entityManager.find(Student.class, 1L)`, Hibernate first checks the L1 Cache:
  - If present: Returns reference immediately **without querying the database**.
  - If absent: Executes SQL `SELECT`, caches the entity, and returns it.

```
Transaction Starts
        │
        ▼
entityManager.find(Student.class, 1L)
        │
        ├── Is ID 1 in L1 Cache? ──[ YES ]──> Return cached instance (0 queries)
        │
       [ NO ]
        │
        ▼
Execute SQL: SELECT * FROM students WHERE id = 1;
Store in L1 Cache & Return instance
```

[⬆ Back to Top](#table-of-contents)

---

### 4.2 Automatic Dirty Checking
When Hibernate loads an entity into the Persistence Context, it takes an internal **Snapshot** of all its column values.

At transaction commit or flush:
1. Hibernate compares the current state of the Java object with the original snapshot.
2. If any field changed, Hibernate **automatically generates and executes an SQL `UPDATE` statement**.
3. **You never need to call `repository.save()` or `session.update()` on a managed entity!**

```java
@Transactional
public void updateStudentName(Long id, String newName) {
    Student student = studentRepository.findById(id).orElseThrow();
    student.setName(newName); 
    // No repository.save(student) required!
    // Dirty checking detects the change and triggers UPDATE on commit!
}
```

[⬆ Back to Top](#table-of-contents)

---

### 4.3 The Four Entity Lifecycle States

```
           new Student()
                 │
                 ▼
          ┌─────────────┐
          │  TRANSIENT  │  (Java object in memory; no DB identity; not in L1 cache)
          └──────┬──────┘
                 │ entityManager.persist() / repository.save()
                 ▼
          ┌─────────────┐   entityManager.detach()
          │  PERSISTENT │ ──────────────────────────> ┌────────────┐
          │  (MANAGED)  │                             │  DETACHED  │
          └──────┬──────┘ <────────────────────────── └────────────┘
                 │          entityManager.merge()
                 │ entityManager.remove()
                 ▼
          ┌─────────────┐
          │   REMOVED   │  (Scheduled for SQL DELETE on transaction commit)
          └─────────────┘
```

1. **Transient**: Created via `new Student()`. Not associated with any Persistence Context; no ID.
2. **Persistent (Managed)**: Has a DB ID, associated with active `EntityManager`, tracked by dirty checking.
3. **Detached**: Has a DB ID, but the Session/Transaction that loaded it has closed. Changes made to detached objects are **not** written to the database unless re-attached via `merge()`.
4. **Removed**: Marked for physical deletion (`DELETE`) on transaction commit.

[⬆ Back to Top](#table-of-contents)

---

### 4.4 Flushing vs Committing
- **Flush (`entityManager.flush()`)**: Synchronizes the in-memory state of the Persistence Context with the underlying database by issuing pending SQL `INSERT`, `UPDATE`, and `DELETE` statements. *Does not make data permanent*.
- **Commit**: The transaction boundary commits. Makes all flushed changes permanent in the database engine. A commit always triggers a flush first.

[⬆ Back to Top](#table-of-contents)

---

## 5. JPA Relationships: Mapping, Ownership & Cardinality (Lecture 30)

### 5.1 Relational Foreign Keys vs Object References
- In Relational Databases, relationships are modeled via **Foreign Keys (FK)** stored in a child table pointing to a parent table.
- In Java, relationships are modeled via **Object References** (`student.setDepartment(dept)`).

[⬆ Back to Top](#table-of-contents)

---

### 5.2 Relationship Cardinality: `@OneToOne`, `@OneToMany`, `@ManyToOne`, `@ManyToMany`

```
One-to-One:
[ Student ] 1 ───────── 1 [ StudentProfile ]

Many-to-One / One-to-Many:
[ Student ] N ───────── 1 [ Department ]

Many-to-Many:
[ Student ] N ───────── N [ Course ]
           (via student_courses join table)
```

[⬆ Back to Top](#table-of-contents)

---

### 5.3 Unidirectional vs Bidirectional Mappings
- **Unidirectional**: Only one entity holds a reference to the other (e.g., `Student` has `Department`, but `Department` has no reference to its students).
- **Bidirectional**: Both entities reference each other (`Student` has `Department`, and `Department` holds `List<Student>`).

[⬆ Back to Top](#table-of-contents)

---

### 5.4 The Owning Side vs Inverse Side (`mappedBy` & `@JoinColumn`)
In a bidirectional relationship, **exactly one side must own the relationship**:
- **Owning Side**:
  - Physically owns the Foreign Key in the database table.
  - Declares `@JoinColumn(name = "department_id")`.
  - Hibernate **only** inspects the owning side when generating SQL INSERT/UPDATE statements!
- **Inverse (Non-Owning) Side**:
  - Declares `mappedBy = "fieldNameOnOwningEntity"`.
  - Serves as a read-only mirror; Hibernate ignores changes made to this side when persisting foreign keys.

```java
// Owning Side (Student Table contains department_id Foreign Key)
@Entity
@Table(name = "students")
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", nullable = false) // Defines Foreign Key column
    private Department department;
}
```

```java
// Inverse Side (Department Table has NO student foreign key)
@Entity
@Table(name = "departments")
public class Department {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    // mappedBy points to the java field name on the owning entity (Student.department)
    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL)
    private List<Student> students = new ArrayList<>();
}
```

[⬆ Back to Top](#table-of-contents)

---

## 6. Cascading, Fetch Strategies & The N+1 Problem (Lecture 31)

### 6.1 Entity Cascading (`CascadeType`) & Orphan Removal
Cascading dictates whether operations performed on a parent entity automatically propagate to associated child entities:
- `CascadeType.PERSIST`: Saving parent saves child.
- `CascadeType.REMOVE`: Deleting parent deletes child.
- `CascadeType.MERGE`: Merging parent merges child.
- `CascadeType.ALL`: Propagates all lifecycle events.
- **`orphanRemoval = true`**: If a child entity is removed from the parent's collection (`students.remove(student)`), Hibernate automatically deletes that child row from the database table.

[⬆ Back to Top](#table-of-contents)

---

### 6.2 Fetch Strategies: `FetchType.LAZY` vs `FetchType.EAGER`
- **`FetchType.EAGER`**: The associated entity is fetched immediately along with the parent query (usually via SQL `LEFT OUTER JOIN`).
- **`FetchType.LAZY`**: The associated entity is **not** fetched immediately. Hibernate injects a dynamic Bytecode Proxy. The actual SQL query is executed only when a getter is called on the association (`student.getDepartment().getName()`).

#### JPA Default Fetch Types:
- `@OneToOne`: **EAGER** (Dangerous!)
- `@ManyToOne`: **EAGER** (Dangerous!)
- `@OneToMany`: **LAZY**
- `@ManyToMany`: **LAZY**

> [!TIP]
> **Industry Best Practice**: Explicitly set `fetch = FetchType.LAZY` on **ALL** `@ManyToOne` and `@OneToOne` associations to avoid catastrophic unintentional table joins across large databases!

[⬆ Back to Top](#table-of-contents)

---

### 6.3 The N+1 Query Problem Explained
The **N+1 Problem** occurs when an application executes 1 query to fetch parent records, followed by N separate queries to fetch associated child records.

```
Query 1 (Fetches 50 Students):
SELECT * FROM students;

Queries 2 to 51 (Hibernate fires 50 individual queries when accessing department!):
SELECT * FROM departments WHERE id = 1;
SELECT * FROM departments WHERE id = 2;
...
SELECT * FROM departments WHERE id = 50;

Total queries: 1 + 50 = 51 database roundtrips!
```

[⬆ Back to Top](#table-of-contents)

---

### 6.4 Production Solutions: `JOIN FETCH`, `@EntityGraph`, and `@BatchSize`

#### 1. Solution 1: JPQL `JOIN FETCH` (Recommended)
Tells Hibernate to join and initialize the association in a **single SQL query**:
```java
@Query("SELECT s FROM Student s JOIN FETCH s.department")
List<Student> findAllWithDepartment();
```
SQL Executed (Exactly 1 Query):
```sql
SELECT s.*, d.* FROM students s INNER JOIN departments d ON s.department_id = d.id;
```

#### 2. Solution 2: `@EntityGraph`
Declaratively defines fetch plan without altering JPQL query structure:
```java
@EntityGraph(attributePaths = {"department"})
List<Student> findAll();
```

#### 3. Solution 3: Hibernate `@BatchSize`
Fetches associations in batches using the SQL `IN` operator:
```java
@BatchSize(size = 25)
@OneToMany(mappedBy = "department")
private List<Student> students;
```
Instead of 50 individual queries, Hibernate fires only 2 queries: `WHERE department_id IN (?, ?, ..., ?)`.

[⬆ Back to Top](#table-of-contents)

---

## 7. Spring Data JPA Repositories (Lecture 32)

### 7.1 Repository Hierarchy: `Repository`, `CrudRepository`, `JpaRepository`

```
┌──────────────────────────────────────────────┐
│        org.springframework.data.Repository   │  <-- Marker interface
└──────────────────────┬───────────────────────┘
                       │ extends
┌──────────────────────▼───────────────────────┐
│              CrudRepository                  │  <-- Basic CRUD (save, findById, delete)
└──────────────────────┬───────────────────────┘
                       │ extends
┌──────────────────────▼───────────────────────┐
│        PagingAndSortingRepository            │  <-- findAll(Sort), findAll(Pageable)
└──────────────────────┬───────────────────────┘
                       │ extends
┌──────────────────────▼───────────────────────┐
│               JpaRepository                  │  <-- flush(), saveAndFlush(), deleteInBatch()
└──────────────────────────────────────────────┘
```

```java
package com.raysi.repository;

import com.raysi.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
}
```

[⬆ Back to Top](#table-of-contents)

---

### 7.2 Derived Query Methods & Keyword Parsing
Spring Data JPA parses method names and generates SQL automatically:

```java
public interface StudentRepository extends JpaRepository<Student, Long> {

    // SELECT * FROM students WHERE email = ?
    Optional<Student> findByEmail(String email);

    // SELECT * FROM students WHERE name LIKE ?
    List<Student> findByNameContainingIgnoreCase(String keyword);

    // SELECT * FROM students WHERE age >= ? AND is_deleted = false
    List<Student> findByAgeGreaterThanEqualAndIsDeletedFalse(int age);

    // SELECT COUNT(*) FROM students WHERE department_id = ?
    long countByDepartmentId(Long departmentId);
}
```

[⬆ Back to Top](#table-of-contents)

---

### 7.3 Custom Queries with `@Query`: JPQL vs Native SQL

#### JPQL (Operates on Java Entity Names and Fields):
```java
// Uses Entity class 'Student' and property 'email', not table/column names!
@Query("SELECT s FROM Student s WHERE s.email LIKE %:domain")
List<Student> findByEmailDomain(@Param("domain") String domain);
```

#### Native SQL (Operates on Raw Database Tables and Columns):
```java
@Query(value = "SELECT * FROM students WHERE created_at < NOW() - INTERVAL 30 DAY", nativeQuery = true)
List<Student> findInactiveStudents();
```

[⬆ Back to Top](#table-of-contents)

---

### 7.4 Pagination and Sorting (`Pageable`, `Sort`, `Page<T>` vs `Slice<T>`)

```java
package com.raysi.service;

import com.raysi.entity.Student;
import com.raysi.repository.StudentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class StudentQueryService {

    private final StudentRepository repository;

    public StudentQueryService(StudentRepository repository) {
        this.repository = repository;
    }

    public Page<Student> getStudentsPaged(int pageNo, int pageSize, String sortBy) {
        // PageRequest: page indices are zero-based
        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(sortBy).descending());
        
        Page<Student> pageResult = repository.findAll(pageable);
        
        System.out.println("Total Records: " + pageResult.getTotalElements());
        System.out.println("Total Pages: " + pageResult.getTotalPages());
        System.out.println("Current Page Number: " + pageResult.getNumber());

        return pageResult;
    }
}
```

#### `Page<T>` vs `Slice<T>`:
- **`Page<T>`**: Executes a count query (`SELECT COUNT(*)`) to calculate total elements and pages. Ideal for desktop tables with page navigation buttons.
- **`Slice<T>`**: Queries only for `limit + 1` records. Does **not** execute a costly `COUNT(*)` query. Ideal for mobile infinite-scrolling feeds where total pages are not required.

[⬆ Back to Top](#table-of-contents)
