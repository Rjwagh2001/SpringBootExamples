# REST API Complete Learning Guide

This guide provides a full learning path to master RESTful API development using **Spring Boot (Java)** — from basics to enterprise-level practices, following real-world patterns used in **top product-based companies**.

---

## 1. Foundation Concepts

### What is REST?

* REST (Representational State Transfer) is an architectural style for building web services.
* Uses **HTTP methods** for CRUD operations.
* Stateless and scalable.

### Key Principles
- Client-Server Architecture
- Statelessness
- Cacheable
- Layered System
- Uniform Interface

### REST vs SOAP
| Feature | REST | SOAP |
|----------|------|------|
| Protocol | HTTP | XML/HTTP |
| Format | JSON, XML | XML only |
| Simplicity | Easy | Complex |

### Spring Boot Setup

**pom.xml**
```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>
</dependencies>
```

**application.properties**
```
server.port=8080
spring.application.name=rest-api-demo
```

---

## 2. Basic CRUD Operations

### Model Class
```java
@Entity
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private double price;
}
```

### Repository
```java
public interface ProductRepository extends JpaRepository<Product, Long> {}
```

### REST Controller
```java
@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductRepository repo;

    @GetMapping
    public List<Product> getAll() {
        return repo.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getById(@PathVariable Long id) {
        return repo.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Product create(@RequestBody Product product) {
        return repo.save(product);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> update(@PathVariable Long id, @RequestBody Product product) {
        return repo.findById(id).map(existing -> {
            existing.setName(product.getName());
            existing.setPrice(product.getPrice());
            return ResponseEntity.ok(repo.save(existing));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!repo.existsById(id)) return ResponseEntity.notFound().build();
        repo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
```

---

## 3. HTTP Methods Deep Dive

| Method | Purpose | Example |
|---------|----------|----------|
| GET | Read | `/api/products` |
| POST | Create | `/api/products` |
| PUT | Update (replace) | `/api/products/1` |
| PATCH | Update (partial) | `/api/products/1` |
| DELETE | Delete | `/api/products/1` |

**Best Practices:**
- Use nouns, not verbs.
- Use plural resource names.
- Keep URLs consistent.

---

## 4. Request & Response Handling

### Request Parameters
```java
@GetMapping("/search")
public List<Product> search(@RequestParam String name) {
    return repo.findByNameContaining(name);
}
```

### Path Variables
```java
@GetMapping("/{id}")
public Product getProduct(@PathVariable Long id) { ... }
```

### ResponseEntity Usage
```java
return ResponseEntity.status(HttpStatus.CREATED).body(savedProduct);
```

### Custom Response Structure
```java
public class ApiResponse<T> {
    private String message;
    private T data;
}
```

---

## 5. Validation & Exception Handling

### Bean Validation
```java
public class Product {
    @NotBlank private String name;
    @Min(1) private double price;
}
```

### Global Exception Handler
```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(err -> errors.put(err.getField(), err.getDefaultMessage()));
        return ResponseEntity.badRequest().body(errors);
    }
}
```

---

## 6. Advanced Features

### Pagination & Sorting
```java
@GetMapping("/page")
public Page<Product> getPaginated(Pageable pageable) {
    return repo.findAll(pageable);
}
```

### File Uploads
```java
@PostMapping("/upload")
public String uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
    return "Uploaded: " + file.getOriginalFilename();
}
```

### JSON Views
```java
@JsonView(Views.Public.class)
private String name;
```

---

## 7. Security & Authentication

### Basic Auth Example
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable().authorizeRequests()
            .antMatchers("/api/public/**").permitAll()
            .anyRequest().authenticated()
            .and().httpBasic();
    }
}
```

### JWT Authentication
- Generate token on login.
- Include token in `Authorization: Bearer <token>` header.

**Libraries:** `jjwt`, `spring-security-oauth2`

---

## 8. Performance & Best Practices

* Use DTOs to avoid over-fetching.
* Implement caching (`@Cacheable`).
* Compress responses (GZIP).
* Use asynchronous APIs (`@Async`).
* Log important events (SLF4J/Logback).

---

## 9. Real-World Project: E-Commerce REST API

### Entities
* User, Product, Order, Cart, Payment

### Endpoints
| Resource | Method | Endpoint | Description |
|-----------|---------|-----------|--------------|
| Products | GET | /api/products | List all products |
| Orders | POST | /api/orders | Place new order |
| Cart | PUT | /api/cart/{id} | Update user cart |
| Auth | POST | /api/auth/login | JWT-based login |

### Features
* Authentication with JWT
* Exception handling
* DTOs for responses
* Pagination, Sorting, Validation
* Integration Tests (MockMVC)

---

**References:**
* [Spring Boot REST Docs](https://docs.spring.io/spring-boot/docs/current/reference/html/web.html)
* [Baeldung REST API Guide](https://www.baeldung.com/spring-boot-rest-api)
* [GeeksforGeeks REST API Tutorial](https://www.geeksforgeeks.org/rest-api/)
