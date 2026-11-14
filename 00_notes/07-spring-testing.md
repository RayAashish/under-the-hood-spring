# Module 07: Testing in Spring Boot with JUnit 5 & Mockito

---

<a id="table-of-contents"></a>
## 📑 Table of Contents
- [1. Testing Foundations & The Testing Pyramid (Lecture 40)](#1-testing-foundations--the-testing-pyramid-lecture-40)
  - [1.1 Why Testing Exists](#11-why-testing-exists)
  - [1.2 The Testing Pyramid: Unit vs Integration vs E2E Tests](#12-the-testing-pyramid-unit-vs-integration-vs-e2e-tests)
- [2. JUnit 5 Fundamentals](#2-junit-5-fundamentals)
  - [2.1 Test Lifecycle Annotations (`@Test`, `@BeforeEach`, `@AfterEach`, `@BeforeAll`, `@AfterAll`)](#21-test-lifecycle-annotations-test-beforeeach-aftereach-beforeall-afterall)
  - [2.2 Assertions & Exception Testing (`assertThrows`)](#22-assertions--exception-testing-assertthrows)
  - [2.3 Parameterized Tests (`@ParameterizedTest`, `@ValueSource`, `@CsvSource`)](#23-parameterized-tests-parameterizedtest-valuesource-csvsource)
- [3. Mocking with Mockito](#3-mocking-with-mockito)
  - [3.1 What is Mocking and Why is it Necessary?](#31-what-is-mocking-and-why-is-it-necessary)
  - [3.2 Key Mockito Annotations: `@Mock`, `@InjectMocks`, `@Spy`](#32-key-mockito-annotations-mock-injectmocks-spy)
  - [3.3 Stubbing Behavior: `when().thenReturn()`, `doThrow()`](#33-stubbing-behavior-whenthenreturn-dothrow)
  - [3.4 Verifying Invocations & Interactions (`verify()`, `times()`)](#34-verifying-invocations--interactions-verify-times)
  - [3.5 Capturing Method Arguments with `ArgumentCaptor`](#35-capturing-method-arguments-with-argumentcaptor)
- [4. Unit Testing the Service Layer](#4-unit-testing-the-service-layer)
  - [4.1 Isolating Service from Repository & External Dependencies](#41-isolating-service-from-repository--external-dependencies)
  - [4.2 Testing Happy Paths](#42-testing-happy-paths)
  - [4.3 Testing Business Exceptions & Edge Cases](#43-testing-business-exceptions--edge-cases)
- [5. Testing the Web Layer with `MockMvc` & `@WebMvcTest`](#5-testing-the-web-layer-with-mockmvc--webmvctest)
  - [5.1 Why Not Invoke Controller Methods Directly?](#51-why-not-invoke-controller-methods-directly)
  - [5.2 Spring Boot Slices: `@WebMvcTest`](#52-spring-boot-slices-webmvctest)
  - [5.3 Mocking Context Dependencies with `@MockBean`](#53-mocking-context-dependencies-with-mockbean)
  - [5.4 Validating HTTP Status Codes, Response JSON, and Headers with `jsonPath`](#54-validating-http-status-codes-response-json-and-headers-with-jsonpath)
- [6. Data Layer & Integration Testing](#6-data-layer--integration-testing)
  - [6.1 Repository Slice Testing with `@DataJpaTest`](#61-repository-slice-testing-with-datajpatest)
  - [6.2 Full-Context Integration Testing with `@SpringBootTest`](#62-full-context-integration-testing-with-springboottest)

---

## 1. Testing Foundations & The Testing Pyramid (Lecture 40)

### 1.1 Why Testing Exists
Manual testing via Postman or browser clicking is slow, error-prone, and cannot scale.
Automated tests provide:
1. **Regression Prevention**: Ensures new code does not break existing functionality.
2. **Fearless Refactoring**: Allows developers to redesign internal architectures with confidence.
3. **Executable Documentation**: Serves as clear, verifiable documentation of system behavior and business edge cases.
4. **Faster Delivery**: Shortens QA release cycles from weeks to minutes.

[⬆ Back to Top](#table-of-contents)

---

### 1.2 The Testing Pyramid: Unit vs Integration vs E2E Tests

```
           /\
          /  \
         / E2E\          (Fewest, Slowest, Highest Cost, Highest Realism)
        / Tests\
       /────────\
      /  Slice   \       (@WebMvcTest, @DataJpaTest)
     /Integration \
    /    Tests     \
   /────────────────\
  /    Unit Tests    \   (Most Numerous, Milliseconds, Isolated Mocks)
 /────────────────────\
```

1. **Unit Tests**:
   - Tests a single method or class in complete isolation.
   - External dependencies (Repositories, REST clients, message queues) are replaced with **Mocks**.
   - Execution time: A few milliseconds per test.
2. **Integration / Slice Tests**:
   - Tests interactions between two or more components (e.g., Service interacting with real database or Controller with HTTP filter chain).
   - Verifies SQL queries, transactions, or JSON serialization contracts.
3. **End-to-End (E2E) Tests**:
   - Boots the complete application stack and tests realistic user workflows from client to database.

[⬆ Back to Top](#table-of-contents)

---

## 2. JUnit 5 Fundamentals

### 2.1 Test Lifecycle Annotations (`@Test`, `@BeforeEach`, `@AfterEach`, `@BeforeAll`, `@AfterAll`)
Spring Boot 3 uses **JUnit 5 (Jupiter)**.

```java
package com.raysi.test;

import org.junit.jupiter.api.*;

public class LifecycleTest {

    @BeforeAll
    static void initAll() {
        System.out.println("1. @BeforeAll - Runs once before any test executes (DB Setup / TestContainers)");
    }

    @BeforeEach
    void init() {
        System.out.println("2. @BeforeEach - Runs before EVERY test method (State reset / Mock setup)");
    }

    @Test
    @DisplayName("Sample positive calculation test")
    void testCalculation() {
        System.out.println("3. Executing Test Method");
        Assertions.assertEquals(4, 2 + 2);
    }

    @AfterEach
    void tearDown() {
        System.out.println("4. @AfterEach - Runs after EVERY test method (Cleanup)");
    }

    @AfterAll
    static void tearDownAll() {
        System.out.println("5. @AfterAll - Runs once after all tests have completed");
    }
}
```

[⬆ Back to Top](#table-of-contents)

---

### 2.2 Assertions & Exception Testing (`assertThrows`)

```java
import static org.junit.jupiter.api.Assertions.*;

@Test
void testAssertions() {
    String actual = "Spring Boot";
    assertEquals("Spring Boot", actual, "Strings should match");
    assertNotNull(actual);
    assertTrue(actual.startsWith("Spring"));
}

@Test
void testExceptionHandling() {
    Calculator calc = new Calculator();
    
    // Asserts that executing lambda throws IllegalArgumentException
    IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
        calc.divide(10, 0);
    });
    
    assertEquals("Division by zero is not permitted", ex.getMessage());
}
```

[⬆ Back to Top](#table-of-contents)

---

### 2.3 Parameterized Tests (`@ParameterizedTest`, `@ValueSource`, `@CsvSource`)
Run the identical test logic against a matrix of different inputs:

```java
package com.raysi.test;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import static org.junit.jupiter.api.Assertions.*;

public class ParameterizedValidationTest {

    @ParameterizedTest
    @ValueSource(strings = {"racecar", "radar", "level", "madam"})
    void testPalindromes(String word) {
        assertTrue(word.contentEquals(new StringBuilder(word).reverse()));
    }

    @ParameterizedTest
    @CsvSource({
        "100, 10, 90",
        "200, 20, 160",
        "50, 0, 50"
    })
    void testDiscountCalculation(int price, int discount, int expected) {
        int finalPrice = price - (price * discount / 100);
        assertEquals(expected, finalPrice);
    }
}
```

[⬆ Back to Top](#table-of-contents)

---

## 3. Mocking with Mockito

### 3.1 What is Mocking and Why is it Necessary?
When testing `StudentService`, we want to test its business rules, **not** whether MySQL is running or whether a network connection is alive.
- A **Mock** is a simulated object that mimics the behavior of a real dependency.
- It returns pre-programmed answers and records interactions (invocations, argument values).

[⬆ Back to Top](#table-of-contents)

---

### 3.2 Key Mockito Annotations: `@Mock`, `@InjectMocks`, `@Spy`
- **`@Mock`**: Creates a dummy mock implementation of the interface or class.
- **`@InjectMocks`**: Creates the real instance of the class under test and automatically injects all `@Mock` fields into its constructor.
- **`@Spy`**: Wraps a real object; real methods are called unless explicitly stubbed.
- **`@ExtendWith(MockitoExtension.class)`**: Initializes Mockito annotations in JUnit 5.

```java
@ExtendWith(MockitoExtension.class)
public class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository; // Dummy mock

    @InjectMocks
    private StudentService studentService;       // Real service with mock injected
}
```

[⬆ Back to Top](#table-of-contents)

---

### 3.3 Stubbing Behavior: `when().thenReturn()`, `doThrow()`

```java
// 1. Stubbing successful return
when(studentRepository.findById(1L)).thenReturn(Optional.of(new Student(1L, "Aashish", "aashish@raysi.com")));

// 2. Stubbing empty return
when(studentRepository.findById(999L)).thenReturn(Optional.empty());

// 3. Stubbing an exception
when(studentRepository.save(any(Student.class))).thenThrow(new DataIntegrityViolationException("Duplicate email"));

// 4. Stubbing void methods to throw exception
doThrow(new RuntimeException("Database error")).when(studentRepository).deleteById(anyLong());
```

[⬆ Back to Top](#table-of-contents)

---

### 3.4 Verifying Invocations & Interactions (`verify()`, `times()`)
Verify that specific collaborator methods were executed with expected arguments:

```java
// Verify findById was called exactly once with argument 1L
verify(studentRepository, times(1)).findById(1L);

// Verify save was never called
verify(studentRepository, never()).save(any(Student.class));

// Verify no more unexpected interactions occurred on mock
verifyNoMoreInteractions(studentRepository);
```

[⬆ Back to Top](#table-of-contents)

---

### 3.5 Capturing Method Arguments with `ArgumentCaptor`
Inspect the exact object passed into a mock method:

```java
ArgumentCaptor<Student> studentCaptor = ArgumentCaptor.forClass(Student.class);
verify(studentRepository).save(studentCaptor.capture());

Student captured = studentCaptor.getValue();
assertEquals("Aashish", captured.getName());
assertEquals("aashish@raysi.com", captured.getEmail());
```

[⬆ Back to Top](#table-of-contents)

---

## 4. Unit Testing the Service Layer

```java
package com.raysi.service;

import com.raysi.dto.StudentRequestDTO;
import com.raysi.dto.StudentResponseDTO;
import com.raysi.entity.Student;
import com.raysi.exception.ResourceNotFoundException;
import com.raysi.repository.StudentRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StudentServiceUnitTest {

    @Mock
    private StudentRepository studentRepository;

    @InjectMocks
    private StudentService studentService;

    @Test
    @DisplayName("Should successfully create student when email is unique")
    void testCreateStudentSuccess() {
        StudentRequestDTO request = new StudentRequestDTO("Aashish", "aashish@raysi.com", 22);
        Student savedEntity = new Student(1L, "Aashish", "aashish@raysi.com");

        when(studentRepository.findByEmail(request.email())).thenReturn(Optional.empty());
        when(studentRepository.save(any(Student.class))).thenReturn(savedEntity);

        StudentResponseDTO response = studentService.createStudent(request);

        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals("Aashish", response.name());
        verify(studentRepository, times(1)).save(any(Student.class));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when student ID does not exist")
    void testGetStudentByIdNotFound() {
        when(studentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            studentService.getStudentById(99L);
        });

        verify(studentRepository, times(1)).findById(99L);
    }
}
```

[⬆ Back to Top](#table-of-contents)

---

## 5. Testing the Web Layer with `MockMvc` & `@WebMvcTest`

### 5.1 Why Not Invoke Controller Methods Directly?
Instantiating a controller manually (`new StudentController(mockService).getStudent(1L)`) tests the Java method, but misses critical web tier infrastructure:
- URL path routing (`@GetMapping("/api/v1/students/{id}")`).
- Request body JSON deserialization.
- Validation annotations (`@Valid`, `@NotNull`).
- HTTP Status codes (`201 Created` vs `200 OK`).
- Global Exception Handlers (`@RestControllerAdvice`).
- HTTP response serialization.

[⬆ Back to Top](#table-of-contents)

---

### 5.2 Spring Boot Slices: `@WebMvcTest`
`@WebMvcTest` performs a **Test Slice**:
- Loads **only** the web layer (`@Controller`, `@RestController`, `Filter`, `WebMvcConfigurer`).
- Does **not** load `@Service`, `@Repository`, or database connection pools, making tests blazing fast.

[⬆ Back to Top](#table-of-contents)

---

### 5.3 Mocking Context Dependencies with `@MockBean`
`@MockBean` creates a Mockito mock and puts it directly inside the Spring `ApplicationContext`, replacing any matching bean.

```java
package com.raysi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.raysi.dto.StudentRequestDTO;
import com.raysi.dto.StudentResponseDTO;
import com.raysi.exception.ResourceNotFoundException;
import com.raysi.service.StudentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StudentController.class)
public class StudentControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private StudentService studentService;

    @Test
    @DisplayName("POST /api/v1/students - Should return 201 Created and JSON payload")
    void testCreateStudentEndpoint() throws Exception {
        StudentRequestDTO request = new StudentRequestDTO("Aashish", "aashish@raysi.com", 24);
        StudentResponseDTO response = new StudentResponseDTO(1L, "Aashish", "aashish@raysi.com");

        when(studentService.createStudent(any(StudentRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Aashish"))
                .andExpect(jsonPath("$.email").value("aashish@raysi.com"));
    }

    @Test
    @DisplayName("POST /api/v1/students - Invalid payload should return 400 Bad Request")
    void testCreateStudentValidationFailure() throws Exception {
        // Name is blank, invalid email format
        StudentRequestDTO invalidRequest = new StudentRequestDTO("", "not-an-email", 12);

        mockMvc.perform(post("/api/v1/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validationErrors.name").exists())
                .andExpect(jsonPath("$.validationErrors.email").exists());
    }

    @Test
    @DisplayName("GET /api/v1/students/{id} - When not found should return 404")
    void testGetStudentNotFound() throws Exception {
        when(studentService.getStudentById(404L))
                .thenThrow(new ResourceNotFoundException("Student not found with ID: 404"));

        mockMvc.perform(get("/api/v1/students/404"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Student not found with ID: 404"));
    }
}
```

[⬆ Back to Top](#table-of-contents)

---

## 6. Data Layer & Integration Testing

### 6.1 Repository Slice Testing with `@DataJpaTest`
`@DataJpaTest` configures an embedded in-memory database (such as H2), scans `@Entity` classes, and configures Spring Data JPA repositories:

```java
package com.raysi.repository;

import com.raysi.entity.Student;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class StudentRepositoryDataJpaTest {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private TestEntityManager entityManager; // Convenient helper for JPA test setup

    @Test
    void testFindByEmail() {
        Student student = new Student("Aashish", "aashish@raysi.com");
        entityManager.persistAndFlush(student);

        Optional<Student> found = studentRepository.findByEmail("aashish@raysi.com");

        assertTrue(found.isPresent());
        assertEquals("Aashish", found.get().getName());
    }
}
```

[⬆ Back to Top](#table-of-contents)

---

### 6.2 Full-Context Integration Testing with `@SpringBootTest`
Boots the full Spring `ApplicationContext` with all beans and real configurations:

```java
package com.raysi.integration;

import com.raysi.entity.Student;
import com.raysi.repository.StudentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class StudentFullIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private StudentRepository studentRepository;

    @Test
    void testCompleteStudentFlow() {
        Student newStudent = new Student("Aashish", "integration@raysi.com");
        
        ResponseEntity<Student> response = restTemplate.postForEntity(
                "/api/v1/students", newStudent, Student.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody().getId());

        // Verify in physical database
        assertTrue(studentRepository.findByEmail("integration@raysi.com").isPresent());
    }
}
```

[⬆ Back to Top](#table-of-contents)
