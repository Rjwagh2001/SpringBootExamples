# Complete REST API Learning Path

**Goal:** Build enterprise-grade REST APIs like top 10 product companies

## Table of Contents
1. [Foundation Concepts](#1-foundation-concepts)
2. [Basic CRUD Operations](#2-basic-crud-operations)
3. [HTTP Methods Deep Dive](#3-http-methods-deep-dive)
4. [Request & Response Handling](#4-request--response-handling)
5. [Validation & Exception Handling](#5-validation--exception-handling)
6. [Advanced Features](#6-advanced-features)
7. [Security & Authentication](#7-security--authentication)
8. [Performance & Best Practices](#8-performance--best-practices)
9. [Real-World E-Commerce API](#9-real-world-e-commerce-api)

---

## 1. Foundation Concepts

### What is REST API?
REST (Representational State Transfer) is an architectural style for designing networked applications.

**Key Principles:**
- **Stateless**: Each request contains all information needed
- **Client-Server**: Separation of concerns
- **Cacheable**: Responses can be cached
- **Uniform Interface**: Standard HTTP methods
- **Layered System**: Architecture can have multiple layers

**HTTP Methods:**
- `GET` - Retrieve data
- `POST` - Create new resource
- `PUT` - Replace entire resource
- `PATCH` - Update specific fields
- `DELETE` - Remove resource
- `HEAD` - Get headers only
- `OPTIONS` - Get supported methods

### Setup (Maven)

```xml
<dependencies>
    <!-- Spring Boot Web -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    
    <!-- Spring Boot Data JPA -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    
    <!-- Validation -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>
    
    <!-- MySQL Driver -->
    <dependency>
        <groupId>mysql</groupId>
        <artifactId>mysql-connector-java</artifactId>
    </dependency>
    
    <!-- Lombok (Optional) -->
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
    </dependency>
</dependencies>
```

### application.properties

```properties
# Server Configuration
server.port=8080

# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/ecommerce
spring.datasource.username=root
spring.datasource.password=root
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# JSON Configuration
spring.jackson.default-property-inclusion=non_null
spring.jackson.serialization.write-dates-as-timestamps=false

# Error Handling
server.error.include-message=always
server.error.include-binding-errors=always
```

---

## 2. Basic CRUD Operations

### Entity

```java
package com.example.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    private String description;
    
    @Column(nullable = false)
    private Double price;
    
    private Integer stockQuantity;
    
    private String imageUrl;
    
    private Boolean active = true;
    
    @Column(updatable = false)
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
```

### Repository

```java
package com.example.repository;

import com.example.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    List<Product> findByActive(Boolean active);
    List<Product> findByNameContainingIgnoreCase(String name);
    List<Product> findByPriceBetween(Double minPrice, Double maxPrice);
}
```

### Service

```java
package com.example.service;

import com.example.entity.Product;
import com.example.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class ProductService {
    
    @Autowired
    private ProductRepository productRepository;
    
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
    
    public Product getProductById(Long id) {
        return productRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
    }
    
    public Product createProduct(Product product) {
        return productRepository.save(product);
    }
    
    public Product updateProduct(Long id, Product productDetails) {
        Product product = getProductById(id);
        
        product.setName(productDetails.getName());
        product.setDescription(productDetails.getDescription());
        product.setPrice(productDetails.getPrice());
        product.setStockQuantity(productDetails.getStockQuantity());
        product.setImageUrl(productDetails.getImageUrl());
        product.setActive(productDetails.getActive());
        
        return productRepository.save(product);
    }
    
    public void deleteProduct(Long id) {
        Product product = getProductById(id);
        productRepository.delete(product);
    }
    
    public List<Product> searchProducts(String keyword) {
        return productRepository.findByNameContainingIgnoreCase(keyword);
    }
}
```

### Basic Controller

```java
package com.example.controller;

import com.example.entity.Product;
import com.example.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "*")
public class ProductController {
    
    @Autowired
    private ProductService productService;
    
    // GET all products
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }
    
    // GET product by ID
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }
    
    // POST create product
    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        Product created = productService.createProduct(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
    
    // PUT update product
    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(
            @PathVariable Long id, 
            @RequestBody Product product) {
        return ResponseEntity.ok(productService.updateProduct(id, product));
    }
    
    // DELETE product
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
    
    // SEARCH products
    @GetMapping("/search")
    public ResponseEntity<List<Product>> searchProducts(@RequestParam String keyword) {
        return ResponseEntity.ok(productService.searchProducts(keyword));
    }
}
```

---

## 3. HTTP Methods Deep Dive

### Complete Controller with All Methods

```java
@RestController
@RequestMapping("/api/products")
public class CompleteProductController {
    
    @Autowired
    private ProductService productService;
    
    // ========== GET - RETRIEVE ==========
    
    // Get all with pagination
    @GetMapping
    public ResponseEntity<Page<Product>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return ResponseEntity.ok(productService.getAllProducts(pageable));
    }
    
    // Get by ID
    @GetMapping("/{id}")
    public ResponseEntity<Product> getById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }
    
    // Get with filters
    @GetMapping("/filter")
    public ResponseEntity<List<Product>> getFiltered(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) Boolean active) {
        return ResponseEntity.ok(productService.getFiltered(name, minPrice, maxPrice, active));
    }
    
    // ========== POST - CREATE ==========
    
    // Create single
    @PostMapping
    public ResponseEntity<Product> create(@Valid @RequestBody Product product) {
        Product created = productService.createProduct(product);
        URI location = ServletUriComponentsBuilder
            .fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(created.getId())
            .toUri();
        return ResponseEntity.created(location).body(created);
    }
    
    // Bulk create
    @PostMapping("/bulk")
    public ResponseEntity<List<Product>> createBulk(@RequestBody List<Product> products) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(productService.createBulk(products));
    }
    
    // ========== PUT - REPLACE ==========
    
    // Replace entire resource
    @PutMapping("/{id}")
    public ResponseEntity<Product> replace(
            @PathVariable Long id,
            @Valid @RequestBody Product product) {
        return ResponseEntity.ok(productService.replaceProduct(id, product));
    }
    
    // ========== PATCH - PARTIAL UPDATE ==========
    
    // Update specific fields
    @PatchMapping("/{id}")
    public ResponseEntity<Product> partialUpdate(
            @PathVariable Long id,
            @RequestBody Map<String, Object> updates) {
        return ResponseEntity.ok(productService.partialUpdate(id, updates));
    }
    
    // Update stock
    @PatchMapping("/{id}/stock")
    public ResponseEntity<Product> updateStock(
            @PathVariable Long id,
            @RequestParam Integer quantity) {
        return ResponseEntity.ok(productService.updateStock(id, quantity));
    }
    
    // Update price
    @PatchMapping("/{id}/price")
    public ResponseEntity<Product> updatePrice(
            @PathVariable Long id,
            @RequestParam Double price) {
        return ResponseEntity.ok(productService.updatePrice(id, price));
    }
    
    // ========== DELETE - REMOVE ==========
    
    // Delete single
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
    
    // Soft delete
    @DeleteMapping("/{id}/soft")
    public ResponseEntity<Product> softDelete(@PathVariable Long id) {
        return ResponseEntity.ok(productService.softDelete(id));
    }
    
    // Delete multiple
    @DeleteMapping("/bulk")
    public ResponseEntity<Void> deleteBulk(@RequestBody List<Long> ids) {
        productService.deleteBulk(ids);
        return ResponseEntity.noContent().build();
    }
    
    // ========== HEAD - CHECK EXISTENCE ==========
    
    @RequestMapping(value = "/{id}", method = RequestMethod.HEAD)
    public ResponseEntity<Void> checkExists(@PathVariable Long id) {
        boolean exists = productService.existsById(id);
        return exists ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }
    
    // ========== OPTIONS - GET ALLOWED METHODS ==========
    
    @RequestMapping(method = RequestMethod.OPTIONS)
    public ResponseEntity<Void> options() {
        return ResponseEntity.ok()
            .allow(HttpMethod.GET, HttpMethod.POST, HttpMethod.PUT, 
                   HttpMethod.PATCH, HttpMethod.DELETE, HttpMethod.HEAD)
            .build();
    }
}
```

---

## 4. Request & Response Handling

### DTOs (Data Transfer Objects)

```java
// Request DTO
@Data
public class ProductRequestDTO {
    
    @NotNull(message = "Name is required")
    @Size(min = 3, max = 100, message = "Name must be between 3 and 100 characters")
    private String name;
    
    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;
    
    @NotNull(message = "Price is required")
    @Min(value = 0, message = "Price must be positive")
    private Double price;
    
    @Min(value = 0, message = "Stock quantity must be positive")
    private Integer stockQuantity;
    
    @Pattern(regexp = "^https?://.*", message = "Invalid image URL")
    private String imageUrl;
}

// Response DTO
@Data
@AllArgsConstructor
public class ProductResponseDTO {
    private Long id;
    private String name;
    private String description;
    private Double price;
    private Integer stockQuantity;
    private String imageUrl;
    private Boolean active;
    private LocalDateTime createdAt;
}

// API Response Wrapper
@Data
@AllArgsConstructor
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;
    private LocalDateTime timestamp;
    
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, "Success", data, LocalDateTime.now());
    }
    
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data, LocalDateTime.now());
    }
    
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, message, null, LocalDateTime.now());
    }
}

// Paginated Response
@Data
public class PagedResponse<T> {
    private List<T> content;
    private int pageNumber;
    private int pageSize;
    private long totalElements;
    private int totalPages;
    private boolean last;
    
    public PagedResponse(Page<T> page) {
        this.content = page.getContent();
        this.pageNumber = page.getNumber();
        this.pageSize = page.getSize();
        this.totalElements = page.getTotalElements();
        this.totalPages = page.getTotalPages();
        this.last = page.isLast();
    }
}
```

### Controller with DTOs

```java
@RestController
@RequestMapping("/api/v1/products")
public class ProductDTOController {
    
    @Autowired
    private ProductService productService;
    
    @Autowired
    private ProductMapper productMapper;
    
    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<ProductResponseDTO>>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Page<Product> productsPage = productService.getAllProducts(PageRequest.of(page, size));
        Page<ProductResponseDTO> dtoPage = productsPage.map(productMapper::toDTO);
        
        return ResponseEntity.ok(
            ApiResponse.success("Products retrieved successfully", 
                new PagedResponse<>(dtoPage))
        );
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponseDTO>> getById(@PathVariable Long id) {
        Product product = productService.getProductById(id);
        return ResponseEntity.ok(
            ApiResponse.success(productMapper.toDTO(product))
        );
    }
    
    @PostMapping
    public ResponseEntity<ApiResponse<ProductResponseDTO>> create(
            @Valid @RequestBody ProductRequestDTO requestDTO) {
        
        Product product = productMapper.toEntity(requestDTO);
        Product created = productService.createProduct(product);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(
            ApiResponse.success("Product created successfully", 
                productMapper.toDTO(created))
        );
    }
}
```

### Mapper Class

```java
@Component
public class ProductMapper {
    
    public ProductResponseDTO toDTO(Product product) {
        return new ProductResponseDTO(
            product.getId(),
            product.getName(),
            product.getDescription(),
            product.getPrice(),
            product.getStockQuantity(),
            product.getImageUrl(),
            product.getActive(),
            product.getCreatedAt()
        );
    }
    
    public Product toEntity(ProductRequestDTO dto) {
        Product product = new Product();
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setStockQuantity(dto.getStockQuantity());
        product.setImageUrl(dto.getImageUrl());
        return product;
    }
}
```

---

## 5. Validation & Exception Handling

### Custom Exceptions

```java
// Base Exception
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}

public class DuplicateResourceException extends RuntimeException {
    public DuplicateResourceException(String message) {
        super(message);
    }
}

public class InvalidRequestException extends RuntimeException {
    public InvalidRequestException(String message) {
        super(message);
    }
}
```

### Global Exception Handler

```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ApiResponse.error(ex.getMessage()));
    }
    
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ApiResponse<Void>> handleDuplicate(DuplicateResourceException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(ApiResponse.error(ex.getMessage()));
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidation(
            MethodArgumentNotValidException ex) {
        
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> 
            errors.put(error.getField(), error.getDefaultMessage())
        );
        
        return ResponseEntity.badRequest()
            .body(new ApiResponse<>(false, "Validation failed", errors, LocalDateTime.now()));
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGeneral(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ApiResponse.error("Internal server error: " + ex.getMessage()));
    }
}
```

---

## Key Differences Summary

| Feature | Spring Data JPA | REST API |
|---------|----------------|----------|
| **Purpose** | Database Operations | HTTP Communication |
| **Layer** | Data Access Layer | Presentation Layer |
| **Annotations** | `@Entity`, `@Repository` | `@RestController`, `@GetMapping` |
| **Focus** | Query Methods, Relationships | HTTP Methods, Status Codes |
| **Returns** | Entity Objects | JSON/XML Responses |

**Combined:** REST API uses Spring Data JPA for database operations! 🚀


// ========== 6. ADVANCED FEATURES ==========

package com.example.advanced;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.*;
import org.springframework.data.domain.*;
import java.util.*;

// ========== FILTERING & SEARCHING ==========

@RestController
@RequestMapping("/api/v1/products")
public class AdvancedProductController {
    
    /**
     * Advanced Search with Multiple Filters
     * URL: /api/v1/products/advanced-search?name=laptop&minPrice=1000&maxPrice=5000&category=electronics
     */
    @GetMapping("/advanced-search")
    public ResponseEntity<ApiResponse<List<ProductDTO>>> advancedSearch(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) Boolean inStock,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortDir) {
        
        ProductSearchCriteria criteria = ProductSearchCriteria.builder()
            .name(name)
            .category(category)
            .minPrice(minPrice)
            .maxPrice(maxPrice)
            .inStock(inStock)
            .sortBy(sortBy)
            .sortDirection(sortDir)
            .build();
        
        List<ProductDTO> products = productService.advancedSearch(criteria);
        return ResponseEntity.ok(ApiResponse.success(products));
    }
    
    /**
     * Dynamic Filtering with Specifications
     */
    @PostMapping("/filter")
    public ResponseEntity<ApiResponse<Page<ProductDTO>>> dynamicFilter(
            @RequestBody ProductFilterRequest filterRequest,
            Pageable pageable) {
        
        Specification<Product> spec = ProductSpecifications.fromFilter(filterRequest);
        Page<ProductDTO> products = productService.findBySpecification(spec, pageable);
        
        return ResponseEntity.ok(ApiResponse.success(products));
    }
}

// ========== FILE UPLOAD & DOWNLOAD ==========

@RestController
@RequestMapping("/api/v1/files")
public class FileController {
    
    @Autowired
    private FileStorageService fileStorageService;
    
    /**
     * Upload single file
     */
    @PostMapping("/upload")
    public ResponseEntity<ApiResponse<FileUploadResponse>> uploadFile(
            @RequestParam("file") MultipartFile file) {
        
        String fileName = fileStorageService.storeFile(file);
        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
            .path("/api/v1/files/download/")
            .path(fileName)
            .toUriString();
        
        FileUploadResponse response = new FileUploadResponse(
            fileName,
            fileDownloadUri,
            file.getContentType(),
            file.getSize()
        );
        
        return ResponseEntity.ok(ApiResponse.success("File uploaded successfully", response));
    }
    
    /**
     * Upload multiple files
     */
    @PostMapping("/upload-multiple")
    public ResponseEntity<ApiResponse<List<FileUploadResponse>>> uploadMultipleFiles(
            @RequestParam("files") MultipartFile[] files) {
        
        List<FileUploadResponse> responses = Arrays.stream(files)
            .map(file -> {
                String fileName = fileStorageService.storeFile(file);
                String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/api/v1/files/download/")
                    .path(fileName)
                    .toUriString();
                
                return new FileUploadResponse(fileName, fileDownloadUri, 
                    file.getContentType(), file.getSize());
            })
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(ApiResponse.success("Files uploaded successfully", responses));
    }
    
    /**
     * Download file
     */
    @GetMapping("/download/{fileName:.+}")
    public ResponseEntity<Resource> downloadFile(
            @PathVariable String fileName,
            HttpServletRequest request) {
        
        Resource resource = fileStorageService.loadFileAsResource(fileName);
        
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            contentType = "application/octet-stream";
        }
        
        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(contentType))
            .header(HttpHeaders.CONTENT_DISPOSITION, 
                "attachment; filename=\"" + resource.getFilename() + "\"")
            .body(resource);
    }
}

// ========== VERSIONING ==========

// Method 1: URI Versioning
@RestController
@RequestMapping("/api/v1/products")
public class ProductV1Controller {
    @GetMapping("/{id}")
    public ResponseEntity<ProductV1DTO> getProduct(@PathVariable Long id) {
        // V1 implementation
        return ResponseEntity.ok(productService.getProductV1(id));
    }
}

@RestController
@RequestMapping("/api/v2/products")
public class ProductV2Controller {
    @GetMapping("/{id}")
    public ResponseEntity<ProductV2DTO> getProduct(@PathVariable Long id) {
        // V2 implementation with additional fields
        return ResponseEntity.ok(productService.getProductV2(id));
    }
}

// Method 2: Request Parameter Versioning
@RestController
@RequestMapping("/api/products")
public class ProductVersionedController {
    
    @GetMapping(params = "version=1")
    public ResponseEntity<ProductV1DTO> getProductV1(@RequestParam Long id) {
        return ResponseEntity.ok(productService.getProductV1(id));
    }
    
    @GetMapping(params = "version=2")
    public ResponseEntity<ProductV2DTO> getProductV2(@RequestParam Long id) {
        return ResponseEntity.ok(productService.getProductV2(id));
    }
}

// Method 3: Header Versioning
@RestController
@RequestMapping("/api/products")
public class ProductHeaderVersionController {
    
    @GetMapping(value = "/{id}", headers = "X-API-VERSION=1")
    public ResponseEntity<ProductV1DTO> getProductV1(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductV1(id));
    }
    
    @GetMapping(value = "/{id}", headers = "X-API-VERSION=2")
    public ResponseEntity<ProductV2DTO> getProductV2(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductV2(id));
    }
}

// Method 4: Media Type Versioning (Content Negotiation)
@RestController
@RequestMapping("/api/products")
public class ProductMediaTypeVersionController {
    
    @GetMapping(value = "/{id}", produces = "application/vnd.api.v1+json")
    public ResponseEntity<ProductV1DTO> getProductV1(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductV1(id));
    }
    
    @GetMapping(value = "/{id}", produces = "application/vnd.api.v2+json")
    public ResponseEntity<ProductV2DTO> getProductV2(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductV2(id));
    }
}

// ========== CONTENT NEGOTIATION ==========

@RestController
@RequestMapping("/api/products")
public class ContentNegotiationController {
    
    /**
     * Returns JSON or XML based on Accept header
     * Accept: application/json → Returns JSON
     * Accept: application/xml → Returns XML
     */
    @GetMapping(value = "/{id}", 
                produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Product> getProduct(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }
    
    /**
     * Accepts JSON or XML based on Content-Type header
     */
    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
                 produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        Product created = productService.createProduct(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}

// ========== CACHING ==========

@RestController
@RequestMapping("/api/products")
public class CachedProductController {
    
    @Autowired
    private ProductService productService;
    
    /**
     * With Cache-Control headers
     */
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductWithCache(@PathVariable Long id) {
        Product product = productService.getProductById(id);
        
        return ResponseEntity.ok()
            .cacheControl(CacheControl.maxAge(60, TimeUnit.MINUTES))
            .eTag(String.valueOf(product.hashCode()))
            .body(product);
    }
    
    /**
     * Conditional request with ETags
     */
    @GetMapping("/{id}/etag")
    public ResponseEntity<Product> getProductWithETag(
            @PathVariable Long id,
            @RequestHeader(value = "If-None-Match", required = false) String ifNoneMatch) {
        
        Product product = productService.getProductById(id);
        String etag = String.valueOf(product.hashCode());
        
        if (etag.equals(ifNoneMatch)) {
            return ResponseEntity.status(HttpStatus.NOT_MODIFIED).build();
        }
        
        return ResponseEntity.ok()
            .eTag(etag)
            .body(product);
    }
}

// ========== RATE LIMITING ==========

@RestController
@RequestMapping("/api/products")
public class RateLimitedController {
    
    @Autowired
    private RateLimiter rateLimiter;
    
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts(HttpServletRequest request) {
        String clientId = request.getRemoteAddr();
        
        if (!rateLimiter.allowRequest(clientId)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .header("X-RateLimit-Retry-After", "60")
                .build();
        }
        
        List<Product> products = productService.getAllProducts();
        return ResponseEntity.ok()
            .header("X-RateLimit-Remaining", String.valueOf(rateLimiter.getRemainingRequests(clientId)))
            .body(products);
    }
}

// Rate Limiter Implementation
@Component
class RateLimiter {
    private final Map<String, List<Long>> requestCounts = new ConcurrentHashMap<>();
    private final int MAX_REQUESTS = 100;
    private final long TIME_WINDOW = 60000; // 1 minute
    
    public boolean allowRequest(String clientId) {
        long currentTime = System.currentTimeMillis();
        requestCounts.putIfAbsent(clientId, new ArrayList<>());
        
        List<Long> timestamps = requestCounts.get(clientId);
        timestamps.removeIf(timestamp -> currentTime - timestamp > TIME_WINDOW);
        
        if (timestamps.size() < MAX_REQUESTS) {
            timestamps.add(currentTime);
            return true;
        }
        return false;
    }
    
    public int getRemainingRequests(String clientId) {
        List<Long> timestamps = requestCounts.get(clientId);
        return MAX_REQUESTS - (timestamps != null ? timestamps.size() : 0);
    }
}

// ========== ASYNC PROCESSING ==========

@RestController
@RequestMapping("/api/async")
public class AsyncController {
    
    @Autowired
    private AsyncService asyncService;
    
    /**
     * Async with CompletableFuture
     */
    @GetMapping("/products")
    public CompletableFuture<ResponseEntity<List<Product>>> getProductsAsync() {
        return asyncService.getProductsAsync()
            .thenApply(products -> ResponseEntity.ok(products))
            .exceptionally(ex -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
    }
    
    /**
     * Long running task - return 202 Accepted
     */
    @PostMapping("/process")
    public ResponseEntity<Map<String, String>> processLongTask(@RequestBody TaskRequest request) {
        String taskId = UUID.randomUUID().toString();
        asyncService.processTask(taskId, request);
        
        Map<String, String> response = Map.of(
            "taskId", taskId,
            "status", "PROCESSING",
            "statusUrl", "/api/async/status/" + taskId
        );
        
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }
    
    /**
     * Check task status
     */
    @GetMapping("/status/{taskId}")
    public ResponseEntity<TaskStatus> getTaskStatus(@PathVariable String taskId) {
        TaskStatus status = asyncService.getTaskStatus(taskId);
        return ResponseEntity.ok(status);
    }
}

// ========== WEBHOOKS ==========

@RestController
@RequestMapping("/api/webhooks")
public class WebhookController {
    
    @Autowired
    private WebhookService webhookService;
    
    /**
     * Register webhook
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Webhook>> registerWebhook(
            @RequestBody WebhookRegistrationRequest request) {
        
        Webhook webhook = webhookService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("Webhook registered successfully", webhook));
    }
    
    /**
     * Test webhook
     */
    @PostMapping("/test/{webhookId}")
    public ResponseEntity<ApiResponse<String>> testWebhook(@PathVariable Long webhookId) {
        webhookService.testWebhook(webhookId);
        return ResponseEntity.ok(ApiResponse.success("Test webhook sent"));
    }
    
    /**
     * Receive webhook (for testing)
     */
    @PostMapping("/receive")
    public ResponseEntity<String> receiveWebhook(@RequestBody Map<String, Object> payload) {
        System.out.println("Webhook received: " + payload);
        return ResponseEntity.ok("Webhook received");
    }
}

// ========== 7. SECURITY & AUTHENTICATION ==========

// Basic Authentication
@RestController
@RequestMapping("/api/secure")
public class SecureController {
    
    /**
     * Endpoint requiring authentication
     */
    @GetMapping("/products")
    public ResponseEntity<List<Product>> getSecureProducts(
            @RequestHeader("Authorization") String authHeader) {
        
        // Basic Auth: Authorization: Basic base64(username:password)
        if (authHeader != null && authHeader.startsWith("Basic ")) {
            String base64Credentials = authHeader.substring(6);
            String credentials = new String(Base64.getDecoder().decode(base64Credentials));
            String[] values = credentials.split(":", 2);
            
            String username = values[0];
            String password = values[1];
            
            if (authenticate(username, password)) {
                return ResponseEntity.ok(productService.getAllProducts());
            }
        }
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
    
    private boolean authenticate(String username, String password) {
        // Check credentials against database
        return "admin".equals(username) && "password".equals(password);
    }
}

// JWT Authentication
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    @Autowired
    private AuthService authService;
    
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    
    /**
     * Login and get JWT token
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@RequestBody LoginRequest request) {
        User user = authService.authenticate(request.getUsername(), request.getPassword());
        
        if (user != null) {
            String token = jwtTokenProvider.generateToken(user);
            AuthResponse response = new AuthResponse(token, user.getUsername(), user.getRoles());
            return ResponseEntity.ok(ApiResponse.success("Login successful", response));
        }
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(ApiResponse.error("Invalid credentials"));
    }
    
    /**
     * Register new user
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<User>> register(@Valid @RequestBody RegisterRequest request) {
        User user = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("User registered successfully", user));
    }
    
    /**
     * Refresh token
     */
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refreshToken(
            @RequestHeader("Authorization") String token) {
        
        String newToken = jwtTokenProvider.refreshToken(token);
        AuthResponse response = new AuthResponse(newToken, null, null);
        return ResponseEntity.ok(ApiResponse.success("Token refreshed", response));
    }
}

// Protected endpoints with JWT
@RestController
@RequestMapping("/api/v1/products")
public class JwtProtectedController {
    
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<Product>>> getAllProducts(
            @RequestHeader("Authorization") String token) {
        
        if (token != null && token.startsWith("Bearer ")) {
            String jwt = token.substring(7);
            
            if (jwtTokenProvider.validateToken(jwt)) {
                String username = jwtTokenProvider.getUsernameFromToken(jwt);
                List<Product> products = productService.getAllProducts();
                return ResponseEntity.ok(ApiResponse.success(products));
            }
        }
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(ApiResponse.error("Unauthorized"));
    }
}

// JWT Token Provider
@Component
class JwtTokenProvider {
    
    @Value("${jwt.secret}")
    private String jwtSecret = "mySecretKey";
    
    @Value("${jwt.expiration}")
    private long jwtExpiration = 86400000; // 24 hours
    
    public String generateToken(User user) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpiration);
        
        return Jwts.builder()
            .setSubject(user.getUsername())
            .setIssuedAt(now)
            .setExpiration(expiryDate)
            .signWith(SignatureAlgorithm.HS512, jwtSecret)
            .compact();
    }
    
    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parser()
            .setSigningKey(jwtSecret)
            .parseClaimsJws(token)
            .getBody();
        
        return claims.getSubject();
    }
    
    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    public String refreshToken(String oldToken) {
        String username = getUsernameFromToken(oldToken);
        // Generate new token with same username
        return generateToken(new User(username, null, null));
    }
}

// Role-based authorization
@RestController
@RequestMapping("/api/admin")
public class AdminController {
    
    @GetMapping("/products")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Product>> getAllProductsAdmin() {
        return ResponseEntity.ok(productService.getAllProducts());
    }
    
    @DeleteMapping("/products/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}

// ========== 8. PERFORMANCE & BEST PRACTICES ==========

// Compression
@Configuration
class CompressionConfig {
    
    @Bean
    public FilterRegistrationBean<CompressionFilter> compressionFilter() {
        FilterRegistrationBean<CompressionFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new CompressionFilter());
        registration.addUrlPatterns("/api/*");
        return registration;
    }
}

// CORS Configuration
@Configuration
class CorsConfig {
    
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**")
                    .allowedOrigins("http://localhost:3000", "https://myapp.com")
                    .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                    .allowedHeaders("*")
                    .allowCredentials(true)
                    .maxAge(3600);
            }
        };
    }
}

// Request Logging Interceptor
@Component
class RequestLoggingInterceptor implements HandlerInterceptor {
    
    private static final Logger logger = LoggerFactory.getLogger(RequestLoggingInterceptor.class);
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, 
                            Object handler) {
        logger.info("Request: {} {}", request.getMethod(), request.getRequestURI());
        logger.info("Headers: {}", Collections.list(request.getHeaderNames()));
        return true;
    }
    
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, 
                               Object handler, Exception ex) {
        logger.info("Response Status: {}", response.getStatus());
    }
}

// Response Compression
@Configuration
class WebConfig implements WebMvcConfigurer {
    
    @Autowired
    private RequestLoggingInterceptor requestLoggingInterceptor;
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(requestLoggingInterceptor);
    }
    
    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer
            .favorParameter(true)
            .parameterName("mediaType")
            .defaultContentType(MediaType.APPLICATION_JSON)
            .mediaType("json", MediaType.APPLICATION_JSON)
            .mediaType("xml", MediaType.APPLICATION_XML);
    }
}

// Health Check Endpoint
@RestController
@RequestMapping("/api/health")
public class HealthController {
    
    @Autowired
    private DataSource dataSource;
    
    @GetMapping
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("timestamp", LocalDateTime.now());
        
        // Check database
        try (Connection conn = dataSource.getConnection()) {
            health.put("database", "UP");
        } catch (Exception e) {
            health.put("database", "DOWN");
            health.put("status", "DOWN");
        }
        
        return ResponseEntity.ok(health);
    }
    
    @GetMapping("/liveness")
    public ResponseEntity<String> liveness() {
        return ResponseEntity.ok("OK");
    }
    
    @GetMapping("/readiness")
    public ResponseEntity<String> readiness() {
        // Check if app is ready to accept traffic
        boolean isReady = checkDatabaseConnection() && checkExternalServices();
        return isReady ? ResponseEntity.ok("READY") : 
                        ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("NOT READY");
    }
    
    private boolean checkDatabaseConnection() {
        try (Connection conn = dataSource.getConnection()) {
            return conn.isValid(2);
        } catch (Exception e) {
            return false;
        }
    }
    
    private boolean checkExternalServices() {
        // Check external services
        return true;
    }
}

// API Documentation with Swagger/OpenAPI
@Configuration
class SwaggerConfig {
    
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("E-Commerce API")
                .version("1.0")
                .description("Complete REST API for E-Commerce Application")
                .contact(new Contact()
                    .name("API Support")
                    .email("support@ecommerce.com")))
            .servers(Arrays.asList(
                new Server().url("http://localhost:8080").description("Development"),
                new Server().url("https://api.ecommerce.com").description("Production")
            ));
    }
}



// ========== 9. REAL-WORLD E-COMMERCE API PROJECT ==========

package com.ecommerce.api;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

// ========== ENTITIES ==========

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String email;
    
    @Column(nullable = false)
    private String password;
    
    private String firstName;
    private String lastName;
    private String phone;
    
    @Enumerated(EnumType.STRING)
    private UserRole role;
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Order> orders = new ArrayList<>();
    
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Cart cart;
    
    private LocalDateTime createdAt;
    private LocalDateTime lastLogin;
}

enum UserRole {
    CUSTOMER, SELLER, ADMIN
}

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(length = 2000)
    private String description;
    
    @Column(nullable = false)
    private BigDecimal price;
    
    private BigDecimal discountPrice;
    
    @Column(nullable = false)
    private Integer stockQuantity;
    
    @Column(unique = true)
    private String sku;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id")
    private User seller;
    
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<Review> reviews = new ArrayList<>();
    
    private Double averageRating;
    private Integer reviewCount;
    private Boolean active = true;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

@Entity
@Table(name = "categories")
@Data
class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true)
    private String name;
    
    private String description;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Category parent;
    
    @OneToMany(mappedBy = "parent")
    private List<Category> subcategories = new ArrayList<>();
    
    @OneToMany(mappedBy = "category")
    private List<Product> products = new ArrayList<>();
}

@Entity
@Table(name = "carts")
@Data
class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
    
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> items = new ArrayList<>();
    
    private BigDecimal totalAmount;
    private LocalDateTime updatedAt;
}

@Entity
@Table(name = "cart_items")
@Data
class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "cart_id")
    private Cart cart;
    
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
    
    private Integer quantity;
    private BigDecimal price;
    private BigDecimal subtotal;
}

@Entity
@Table(name = "orders")
@Data
class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true)
    private String orderNumber;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> items = new ArrayList<>();
    
    @Enumerated(EnumType.STRING)
    private OrderStatus status;
    
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;
    
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;
    
    private BigDecimal subtotal;
    private BigDecimal tax;
    private BigDecimal shippingCost;
    private BigDecimal totalAmount;
    
    @Embedded
    private Address shippingAddress;
    
    private LocalDateTime orderDate;
    private LocalDateTime deliveryDate;
}

enum OrderStatus {
    PENDING, CONFIRMED, PROCESSING, SHIPPED, DELIVERED, CANCELLED, REFUNDED
}

enum PaymentMethod {
    CREDIT_CARD, DEBIT_CARD, UPI, NET_BANKING, CASH_ON_DELIVERY
}

enum PaymentStatus {
    PENDING, COMPLETED, FAILED, REFUNDED
}

@Embeddable
@Data
class Address {
    private String street;
    private String city;
    private String state;
    private String zipCode;
    private String country;
}

@Entity
@Table(name = "order_items")
@Data
class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;
    
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
    
    private Integer quantity;
    private BigDecimal price;
    private BigDecimal subtotal;
}

@Entity
@Table(name = "reviews")
@Data
class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    
    @Min(1) @Max(5)
    private Integer rating;
    
    @Column(length = 1000)
    private String comment;
    
    private LocalDateTime createdAt;
}

// ========== REPOSITORIES ==========

@Repository
interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
}

@Repository
interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
    List<Product> findByActiveTrue();
    List<Product> findByCategory_Id(Long categoryId);
    List<Product> findByNameContainingIgnoreCase(String name);
    Page<Product> findByActiveTrue(Pageable pageable);
}

@Repository
interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByName(String name);
    List<Category> findByParentIsNull();
}

@Repository
interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUser_Id(Long userId);
}

@Repository
interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUser_Id(Long userId);
    Optional<Order> findByOrderNumber(String orderNumber);
    List<Order> findByStatus(OrderStatus status);
}

@Repository
interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByProduct_Id(Long productId);
    List<Review> findByUser_Id(Long userId);
    Optional<Review> findByProduct_IdAndUser_Id(Long productId, Long userId);
}

// ========== SERVICES ==========

@Service
@Transactional
class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    public User registerUser(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already registered");
        }
        
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhone(request.getPhone());
        user.setRole(UserRole.CUSTOMER);
        user.setCreatedAt(LocalDateTime.now());
        
        // Create cart for user
        Cart cart = new Cart();
        cart.setUser(user);
        user.setCart(cart);
        
        return userRepository.save(user);
    }
    
    public User getUserById(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
    
    public User updateUser(Long id, UserUpdateRequest request) {
        User user = getUserById(id);
        
        if (request.getFirstName() != null) user.setFirstName(request.getFirstName());
        if (request.getLastName() != null) user.setLastName(request.getLastName());
        if (request.getPhone() != null) user.setPhone(request.getPhone());
        
        return userRepository.save(user);
    }
}

@Service
@Transactional
class ProductService {
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    public Page<Product> getAllProducts(Pageable pageable) {
        return productRepository.findByActiveTrue(pageable);
    }
    
    public Product getProductById(Long id) {
        return productRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
    }
    
    public Product createProduct(ProductRequest request, Long sellerId) {
        Category category = categoryRepository.findById(request.getCategoryId())
            .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        
        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStockQuantity(request.getStockQuantity());
        product.setSku(UUID.randomUUID().toString());
        product.setCategory(category);
        product.setActive(true);
        product.setCreatedAt(LocalDateTime.now());
        
        return productRepository.save(product);
    }
    
    public Product updateProduct(Long id, ProductUpdateRequest request) {
        Product product = getProductById(id);
        
        if (request.getName() != null) product.setName(request.getName());
        if (request.getDescription() != null) product.setDescription(request.getDescription());
        if (request.getPrice() != null) product.setPrice(request.getPrice());
        if (request.getStockQuantity() != null) product.setStockQuantity(request.getStockQuantity());
        
        product.setUpdatedAt(LocalDateTime.now());
        return productRepository.save(product);
    }
    
    public void deleteProduct(Long id) {
        Product product = getProductById(id);
        product.setActive(false);
        productRepository.save(product);
    }
    
    public List<Product> searchProducts(String keyword) {
        return productRepository.findByNameContainingIgnoreCase(keyword);
    }
}

@Service
@Transactional
class CartService {
    
    @Autowired
    private CartRepository cartRepository;
    
    @Autowired
    private ProductRepository productRepository;
    
    public Cart getCartByUserId(Long userId) {
        return cartRepository.findByUser_Id(userId)
            .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));
    }
    
    public Cart addToCart(Long userId, Long productId, Integer quantity) {
        Cart cart = getCartByUserId(userId);
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        
        if (product.getStockQuantity() < quantity) {
            throw new InvalidRequestException("Insufficient stock");
        }
        
        // Check if product already in cart
        Optional<CartItem> existingItem = cart.getItems().stream()
            .filter(item -> item.getProduct().getId().equals(productId))
            .findFirst();
        
        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + quantity);
            item.setSubtotal(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        } else {
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setProduct(product);
            newItem.setQuantity(quantity);
            newItem.setPrice(product.getPrice());
            newItem.setSubtotal(product.getPrice().multiply(BigDecimal.valueOf(quantity)));
            cart.getItems().add(newItem);
        }
        
        updateCartTotal(cart);
        cart.setUpdatedAt(LocalDateTime.now());
        
        return cartRepository.save(cart);
    }
    
    public Cart removeFromCart(Long userId, Long productId) {
        Cart cart = getCartByUserId(userId);
        cart.getItems().removeIf(item -> item.getProduct().getId().equals(productId));
        updateCartTotal(cart);
        return cartRepository.save(cart);
    }
    
    public Cart updateCartItemQuantity(Long userId, Long productId, Integer quantity) {
        Cart cart = getCartByUserId(userId);
        CartItem item = cart.getItems().stream()
            .filter(i -> i.getProduct().getId().equals(productId))
            .findFirst()
            .orElseThrow(() -> new ResourceNotFoundException("Item not in cart"));
        
        item.setQuantity(quantity);
        item.setSubtotal(item.getPrice().multiply(BigDecimal.valueOf(quantity)));
        updateCartTotal(cart);
        
        return cartRepository.save(cart);
    }
    
    public void clearCart(Long userId) {
        Cart cart = getCartByUserId(userId);
        cart.getItems().clear();
        cart.setTotalAmount(BigDecimal.ZERO);
        cartRepository.save(cart);
    }
    
    private void updateCartTotal(Cart cart) {
        BigDecimal total = cart.getItems().stream()
            .map(CartItem::getSubtotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        cart.setTotalAmount(total);
    }
}

@Service
@Transactional
class OrderService {
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private CartService cartService;
    
    @Autowired
    private ProductRepository productRepository;
    
    public Order createOrder(Long userId, OrderRequest request) {
        Cart cart = cartService.getCartByUserId(userId);
        
        if (cart.getItems().isEmpty()) {
            throw new InvalidRequestException("Cart is empty");
        }
        
        // Validate stock
        for (CartItem cartItem : cart.getItems()) {
            Product product = cartItem.getProduct();
            if (product.getStockQuantity() < cartItem.getQuantity()) {
                throw new InvalidRequestException("Insufficient stock for: " + product.getName());
            }
        }
        
        // Create order
        Order order = new Order();
        order.setOrderNumber("ORD-" + System.currentTimeMillis());
        order.setUser(cart.getUser());
        order.setStatus(OrderStatus.PENDING);
        order.setPaymentMethod(request.getPaymentMethod());
        order.setPaymentStatus(PaymentStatus.PENDING);
        order.setShippingAddress(request.getShippingAddress());
        order.setOrderDate(LocalDateTime.now());
        
        // Create order items and update stock
        BigDecimal subtotal = BigDecimal.ZERO;
        for (CartItem cartItem : cart.getItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(cartItem.getPrice());
            orderItem.setSubtotal(cartItem.getSubtotal());
            order.getItems().add(orderItem);
            
            subtotal = subtotal.add(cartItem.getSubtotal());
            
            // Update product stock
            Product product = cartItem.getProduct();
            product.setStockQuantity(product.getStockQuantity() - cartItem.getQuantity());
            productRepository.save(product);
        }
        
        // Calculate totals
        BigDecimal tax = subtotal.multiply(BigDecimal.valueOf(0.18)); // 18% tax
        BigDecimal shippingCost = BigDecimal.valueOf(50);
        BigDecimal total = subtotal.add(tax).add(shippingCost);
        
        order.setSubtotal(subtotal);
        order.setTax(tax);
        order.setShippingCost(shippingCost);
        order.setTotalAmount(total);
        
        Order savedOrder = orderRepository.save(order);
        
        // Clear cart
        cartService.clearCart(userId);
        
        return savedOrder;
    }
    
    public Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
            .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
    }
    
    public List<Order> getUserOrders(Long userId) {
        return orderRepository.findByUser_Id(userId);
    }
    
    public Order updateOrderStatus(Long orderId, OrderStatus newStatus) {
        Order order = getOrderById(orderId);
        order.setStatus(newStatus);
        
        if (newStatus == OrderStatus.DELIVERED) {
            order.setDeliveryDate(LocalDateTime.now());
        }
        
        return orderRepository.save(order);
    }
    
    public Order cancelOrder(Long orderId) {
        Order order = getOrderById(orderId);
        
        if (order.getStatus() == OrderStatus.SHIPPED || 
            order.getStatus() == OrderStatus.DELIVERED) {
            throw new InvalidRequestException("Cannot cancel shipped/delivered order");
        }
        
        // Restore stock
        for (OrderItem item : order.getItems()) {
            Product product = item.getProduct();
            product.setStockQuantity(product.getStockQuantity() + item.getQuantity());
            productRepository.save(product);
        }
        
        order.setStatus(OrderStatus.CANCELLED);
        return orderRepository.save(order);
    }
}

@Service
@Transactional
class ReviewService {
    
    @Autowired
    private ReviewRepository reviewRepository;
    
    @Autowired
    private ProductRepository productRepository;
    
    public Review createReview(Long userId, Long productId, ReviewRequest request) {
        // Check if user already reviewed
        if (reviewRepository.findByProduct_IdAndUser_Id(productId, userId).isPresent()) {
            throw new DuplicateResourceException("You have already reviewed this product");
        }
        
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        
        Review review = new Review();
        review.setProduct(product);
        review.setUser(new User(userId, null, null, null, null, null, null, null, null, null, null));
        review.setRating(request.getRating());
        review.setComment(request.getComment());
        review.setCreatedAt(LocalDateTime.now());
        
        Review savedReview = reviewRepository.save(review);
        
        // Update product rating
        updateProductRating(productId);
        
        return savedReview;
    }
    
    public List<Review> getProductReviews(Long productId) {
        return reviewRepository.findByProduct_Id(productId);
    }
    
    private void updateProductRating(Long productId) {
        List<Review> reviews = reviewRepository.findByProduct_Id(productId);
        
        if (!reviews.isEmpty()) {
            double average = reviews.stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0.0);
            
            Product product = productRepository.findById(productId).orElseThrow();
            product.setAverageRating(average);
            product.setReviewCount(reviews.size());
            productRepository.save(product);
        }
    }
}

// ========== REST CONTROLLERS ==========

@RestController
@RequestMapping("/api/v1/auth")
class AuthController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserDTO>> register(@Valid @RequestBody RegisterRequest request) {
        User user = userService.registerUser(request);
        UserDTO dto = new UserDTO(user.getId(), user.getEmail(), user.getFirstName(), 
                                   user.getLastName(), user.getPhone(), user.getRole());
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("User registered successfully", dto));
    }
    
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        // Authentication logic
        String token = jwtTokenProvider.generateToken(request.getEmail());
        AuthResponse response = new AuthResponse(token, request.getEmail(), UserRole.CUSTOMER);
        return ResponseEntity.ok(ApiResponse.success("Login successful", response));
    }
}

@RestController
@RequestMapping("/api/v1/users")
class UserController {
    
    @Autowired
    private UserService userService;
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserDTO>> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        UserDTO dto = new UserDTO(user.getId(), user.getEmail(), user.getFirstName(), 
                                   user.getLastName(), user.getPhone(), user.getRole());
        return ResponseEntity.ok(ApiResponse.success(dto));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserDTO>> updateUser(
            @PathVariable Long id, 
            @Valid @RequestBody UserUpdateRequest request) {
        User user = userService.updateUser(id, request);
        UserDTO dto = new UserDTO(user.getId(), user.getEmail(), user.getFirstName(), 
                                   user.getLastName(), user.getPhone(), user.getRole());
        return ResponseEntity.ok(ApiResponse.success("User updated successfully", dto));
    }
}

@RestController
@RequestMapping("/api/v1/products")
class ProductController {
    
    @Autowired
    private ProductService productService;
    
    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<ProductDTO>>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        Sort.Direction direction = sortDir.equalsIgnoreCase("desc") 
            ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        
        Page<Product> productsPage = productService.getAllProducts(pageable);
        Page<ProductDTO> dtoPage = productsPage.map(this::convertToDTO);
        
        return ResponseEntity.ok(ApiResponse.success(new PagedResponse<>(dtoPage)));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductDTO>> getProductById(@PathVariable Long id) {
        Product product = productService.getProductById(id);
        return ResponseEntity.ok(ApiResponse.success(convertToDTO(product)));
    }
    
    @PostMapping
    public ResponseEntity<ApiResponse<ProductDTO>> createProduct(
            @Valid @RequestBody ProductRequest request,
            @RequestHeader("User-Id") Long sellerId) {
        Product product = productService.createProduct(request, sellerId);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("Product created successfully", convertToDTO(product)));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductDTO>> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductUpdateRequest request) {
        Product product = productService.updateProduct(id, request);
        return ResponseEntity.ok(ApiResponse.success("Product updated successfully", convertToDTO(product)));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok(ApiResponse.success("Product deleted successfully", null));
    }
    
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<ProductDTO>>> searchProducts(@RequestParam String keyword) {
        List<Product> products = productService.searchProducts(keyword);
        List<ProductDTO> dtos = products.stream().map(this::convertToDTO).collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(dtos));
    }
    
    private ProductDTO convertToDTO(Product product) {
        return new ProductDTO(
            product.getId(),
            product.getName(),
            product.getDescription(),
            product.getPrice(),
            product.getDiscountPrice(),
            product.getStockQuantity(),
            product.getSku(),
            product.getCategory().getName(),
            product.getAverageRating(),
            product.getReviewCount(),
            product.getActive()
        );
    }
}

@RestController
@RequestMapping("/api/v1/cart")
class CartController {
    
    @Autowired
    private CartService cartService;
    
    @GetMapping
    public ResponseEntity<ApiResponse<CartDTO>> getCart(@RequestHeader("User-Id") Long userId) {
        Cart cart = cartService.getCartByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success(convertToDTO(cart)));
    }
    
    @PostMapping("/items")
    public ResponseEntity<ApiResponse<CartDTO>> addToCart(
            @RequestHeader("User-Id") Long userId,
            @Valid @RequestBody AddToCartRequest request) {
        Cart cart = cartService.addToCart(userId, request.getProductId(), request.getQuantity());
        return ResponseEntity.ok(ApiResponse.success("Item added to cart", convertToDTO(cart)));
    }
    
    @DeleteMapping("/items/{productId}")
    public ResponseEntity<ApiResponse<CartDTO>> removeFromCart(
            @RequestHeader("User-Id") Long userId,
            @PathVariable Long productId) {
        Cart cart = cartService.removeFromCart(userId, productId);
        return ResponseEntity.ok(ApiResponse.success("Item removed from cart", convertToDTO(cart)));
    }
    
    @PutMapping("/items/{productId}")
    public ResponseEntity<ApiResponse<CartDTO>> updateQuantity(
            @RequestHeader("User-Id") Long userId,
            @PathVariable Long productId,
            @RequestParam Integer quantity) {
        Cart cart = cartService.updateCartItemQuantity(userId, productId, quantity);
        return ResponseEntity.ok(ApiResponse.success("Cart updated", convertToDTO(cart)));
    }
    
    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> clearCart(@RequestHeader("User-Id") Long userId) {
        cartService.clearCart(userId);
        return ResponseEntity.ok(ApiResponse.success("Cart cleared", null));
    }
    
    private CartDTO convertToDTO(Cart cart) {
        List<CartItemDTO> items = cart.getItems().stream()
            .map(item -> new CartItemDTO(
                item.getId(),
                item.getProduct().getId(),
                item.getProduct().getName(),
                item.getQuantity(),
                item.getPrice(),
                item.getSubtotal()
            ))
            .collect(Collectors.toList());
        
        return new CartDTO(cart.getId(), items, cart.getTotalAmount(), cart.getUpdatedAt());
    }
}

@RestController
@RequestMapping("/api/v1/orders")
class OrderController {
    
    @Autowired
    private OrderService orderService;
    
    @PostMapping
    public ResponseEntity<ApiResponse<OrderDTO>> createOrder(
            @RequestHeader("User-Id") Long userId,
            @Valid @RequestBody OrderRequest request) {
        Order order = orderService.createOrder(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("Order created successfully", convertToDTO(order)));
    }
    
    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<OrderDTO>> getOrder(@PathVariable Long orderId) {
        Order order = orderService.getOrderById(orderId);
        return ResponseEntity.ok(ApiResponse.success(convertToDTO(order)));
    }
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<OrderDTO>>> getUserOrders(@RequestHeader("User-Id") Long userId) {
        List<Order> orders = orderService.getUserOrders(userId);
        List<OrderDTO> dtos = orders.stream().map(this::convertToDTO).collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(dtos));
    }
    
    @PatchMapping("/{orderId}/status")
    public ResponseEntity<ApiResponse<OrderDTO>> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestParam OrderStatus status) {
        Order order = orderService.updateOrderStatus(orderId, status);
        return ResponseEntity.ok(ApiResponse.success("Order status updated", convertToDTO(order)));
    }
    
    @DeleteMapping("/{orderId}")
    public ResponseEntity<ApiResponse<OrderDTO>> cancelOrder(@PathVariable Long orderId) {
        Order order = orderService.cancelOrder(orderId);
        return ResponseEntity.ok(ApiResponse.success("Order cancelled", convertToDTO(order)));
    }
    
    private OrderDTO convertToDTO(Order order) {
        List<OrderItemDTO> items = order.getItems().stream()
            .map(item -> new OrderItemDTO(
                item.getId(),
                item.getProduct().getName(),
                item.getQuantity(),
                item.getPrice(),
                item.getSubtotal()
            ))
            .collect(Collectors.toList());
        
        return new OrderDTO(
            order.getId(),
            order.getOrderNumber(),
            items,
            order.getStatus(),
            order.getPaymentMethod(),
            order.getPaymentStatus(),
            order.getSubtotal(),
            order.getTax(),
            order.getShippingCost(),
            order.getTotalAmount(),
            order.getShippingAddress(),
            order.getOrderDate(),
            order.getDeliveryDate()
        );
    }
}

@RestController
@RequestMapping("/api/v1/reviews")
class ReviewController {
    
    @Autowired
    private ReviewService reviewService;
    
    @PostMapping
    public ResponseEntity<ApiResponse<ReviewDTO>> createReview(
            @RequestHeader("User-Id") Long userId,
            @Valid @RequestBody ReviewRequest request) {
        Review review = reviewService.createReview(userId, request.getProductId(), request);
        ReviewDTO dto = new ReviewDTO(review.getId(), review.getProduct().getId(), 
                                      userId, review.getRating(), review.getComment(), 
                                      review.getCreatedAt());
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("Review added successfully", dto));
    }
    
    @GetMapping("/product/{productId}")
    public ResponseEntity<ApiResponse<List<ReviewDTO>>> getProductReviews(@PathVariable Long productId) {
        List<Review> reviews = reviewService.getProductReviews(productId);
        List<ReviewDTO> dtos = reviews.stream()
            .map(r -> new ReviewDTO(r.getId(), r.getProduct().getId(), r.getUser().getId(),
                                    r.getRating(), r.getComment(), r.getCreatedAt()))
            .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(dtos));
    }
}

// ========== DTOs & REQUEST/RESPONSE CLASSES ==========

@Data
@AllArgsConstructor
class UserDTO {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    private UserRole role;
}

@Data
class RegisterRequest {
    @NotNull @Email
    private String email;
    
    @NotNull @Size(min = 6)
    private String password;
    
    @NotNull
    private String firstName;
    
    @NotNull
    private String lastName;
    
    private String phone;
}

@Data
class LoginRequest {
    @NotNull @Email
    private String email;
    
    @NotNull
    private String password;
}

@Data
@AllArgsConstructor
class AuthResponse {
    private String token;
    private String email;
    private UserRole role;
}

@Data
class ProductDTO {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private BigDecimal discountPrice;
    private Integer stockQuantity;
    private String sku;
    private String categoryName;
    private Double averageRating;
    private Integer reviewCount;
    private Boolean active;
    
    public ProductDTO(Long id, String name, String description, BigDecimal price,
                      BigDecimal discountPrice, Integer stockQuantity, String sku,
                      String categoryName, Double averageRating, Integer reviewCount, Boolean active) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.discountPrice = discountPrice;
        this.stockQuantity = stockQuantity;
        this.sku = sku;
        this.categoryName = categoryName;
        this.averageRating = averageRating;
        this.reviewCount = reviewCount;
        this.active = active;
    }
}

@Data
class ProductRequest {
    @NotNull
    private String name;
    
    private String description;
    
    @NotNull @Min(0)
    private BigDecimal price;
    
    @NotNull @Min(0)
    private Integer stockQuantity;
    
    @NotNull
    private Long categoryId;
}

@Data
class CartDTO {
    private Long id;
    private List<CartItemDTO> items;
    private BigDecimal totalAmount;
    private LocalDateTime updatedAt;
    
    public CartDTO(Long id, List<CartItemDTO> items, BigDecimal totalAmount, LocalDateTime updatedAt) {
        this.id = id;
        this.items = items;
        this.totalAmount = totalAmount;
        this.updatedAt = updatedAt;
    }
}

@Data
@AllArgsConstructor
class CartItemDTO {
    private Long id;
    private Long productId;
    private String productName;
    private Integer quantity;
    private BigDecimal price;
    private BigDecimal subtotal;
}

@Data
class OrderDTO {
    private Long id;
    private String orderNumber;
    private List<OrderItemDTO> items;
    private OrderStatus status;
    private PaymentMethod paymentMethod;
    private PaymentStatus paymentStatus;
    private BigDecimal subtotal;
    private BigDecimal tax;
    private BigDecimal shippingCost;
    private BigDecimal totalAmount;
    private Address shippingAddress;
    private LocalDateTime orderDate;
    private LocalDateTime deliveryDate;
    
    public OrderDTO(Long id, String orderNumber, List<OrderItemDTO> items, OrderStatus status,
                    PaymentMethod paymentMethod, PaymentStatus paymentStatus, BigDecimal subtotal,
                    BigDecimal tax, BigDecimal shippingCost, BigDecimal totalAmount, 
                    Address shippingAddress, LocalDateTime orderDate, LocalDateTime deliveryDate) {
        this.id = id;
        this.orderNumber = orderNumber;
        this.items = items;
        this.status = status;
        this.paymentMethod = paymentMethod;
        this.paymentStatus = paymentStatus;
        this.subtotal = subtotal;
        this.tax = tax;
        this.shippingCost = shippingCost;
        this.totalAmount = totalAmount;
        this.shippingAddress = shippingAddress;
        this.orderDate = orderDate;
        this.deliveryDate = deliveryDate;
    }
}

@Data
@AllArgsConstructor
class OrderItemDTO {
    private Long id;
    private String productName;
    private Integer quantity;
    private BigDecimal price;
    private BigDecimal subtotal;
}

@Data
@AllArgsConstructor
class ReviewDTO {
    private Long id;
    private Long productId;
    private Long userId;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;
}


# Complete REST API Testing Guide

## 🧪 Testing the E-Commerce API

### 1. User Registration & Authentication

#### Register New User
```bash
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john@example.com",
    "password": "password123",
    "firstName": "John",
    "lastName": "Doe",
    "phone": "9876543210"
  }'
```

**Expected Response:**
```json
{
  "success": true,
  "message": "User registered successfully",
  "data": {
    "id": 1,
    "email": "john@example.com",
    "firstName": "John",
    "lastName": "Doe",
    "phone": "9876543210",
    "role": "CUSTOMER"
  },
  "timestamp": "2024-10-04T10:30:00"
}
```

#### Login
```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john@example.com",
    "password": "password123"
  }'
```

---

### 2. Product Operations

#### Get All Products (with pagination)
```bash
curl -X GET "http://localhost:8080/api/v1/products?page=0&size=10&sortBy=price&sortDir=asc"
```

#### Get Single Product
```bash
curl -X GET http://localhost:8080/api/v1/products/1
```

#### Create Product (Seller/Admin)
```bash
curl -X POST http://localhost:8080/api/v1/products \
  -H "Content-Type: application/json" \
  -H "User-Id: 1" \
  -d '{
    "name": "Laptop",
    "description": "High performance laptop",
    "price": 75000,
    "stockQuantity": 50,
    "categoryId": 1
  }'
```

#### Update Product
```bash
curl -X PUT http://localhost:8080/api/v1/products/1 \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Gaming Laptop",
    "price": 85000
  }'
```

#### Delete Product (Soft Delete)
```bash
curl -X DELETE http://localhost:8080/api/v1/products/1
```

#### Search Products
```bash
curl -X GET "http://localhost:8080/api/v1/products/search?keyword=laptop"
```

---

### 3. Cart Operations

#### Get Cart
```bash
curl -X GET http://localhost:8080/api/v1/cart \
  -H "User-Id: 1"
```

#### Add Item to Cart
```bash
curl -X POST http://localhost:8080/api/v1/cart/items \
  -H "Content-Type: application/json" \
  -H "User-Id: 1" \
  -d '{
    "productId": 1,
    "quantity": 2
  }'
```

**Expected Response:**
```json
{
  "success": true,
  "message": "Item added to cart",
  "data": {
    "id": 1,
    "items": [
      {
        "id": 1,
        "productId": 1,
        "productName": "Laptop",
        "quantity": 2,
        "price": 75000,
        "subtotal": 150000
      }
    ],
    "totalAmount": 150000,
    "updatedAt": "2024-10-04T10:35:00"
  }
}
```

#### Update Cart Item Quantity
```bash
curl -X PUT "http://localhost:8080/api/v1/cart/items/1?quantity=3" \
  -H "User-Id: 1"
```

#### Remove Item from Cart
```bash
curl -X DELETE http://localhost:8080/api/v1/cart/items/1 \
  -H "User-Id: 1"
```

#### Clear Cart
```bash
curl -X DELETE http://localhost:8080/api/v1/cart \
  -H "User-Id: 1"
```

---

### 4. Order Operations

#### Create Order (Checkout)
```bash
curl -X POST http://localhost:8080/api/v1/orders \
  -H "Content-Type: application/json" \
  -H "User-Id: 1" \
  -d '{
    "paymentMethod": "CREDIT_CARD",
    "shippingAddress": {
      "street": "123 Main St",
      "city": "Mumbai",
      "state": "Maharashtra",
      "zipCode": "400001",
      "country": "India"
    }
  }'
```

**Expected Response:**
```json
{
  "success": true,
  "message": "Order created successfully",
  "data": {
    "id": 1,
    "orderNumber": "ORD-1696410600000",
    "items": [
      {
        "id": 1,
        "productName": "Laptop",
        "quantity": 2,
        "price": 75000,
        "subtotal": 150000
      }
    ],
    "status": "PENDING",
    "paymentMethod": "CREDIT_CARD",
    "paymentStatus": "PENDING",
    "subtotal": 150000,
    "tax": 27000,
    "shippingCost": 50,
    "totalAmount": 177050,
    "shippingAddress": {
      "street": "123 Main St",
      "city": "Mumbai",
      "state": "Maharashtra",
      "zipCode": "400001",
      "country": "India"
    },
    "orderDate": "2024-10-04T10:40:00",
    "deliveryDate": null
  }
}
```

#### Get Order by ID
```bash
curl -X GET http://localhost:8080/api/v1/orders/1
```

#### Get User's All Orders
```bash
curl -X GET http://localhost:8080/api/v1/orders \
  -H "User-Id: 1"
```

#### Update Order Status (Admin)
```bash
curl -X PATCH "http://localhost:8080/api/v1/orders/1/status?status=SHIPPED"
```

#### Cancel Order
```bash
curl -X DELETE http://localhost:8080/api/v1/orders/1
```

---

### 5. Review Operations

#### Add Review
```bash
curl -X POST http://localhost:8080/api/v1/reviews \
  -H "Content-Type: application/json" \
  -H "User-Id: 1" \
  -d '{
    "productId": 1,
    "rating": 5,
    "comment": "Excellent product! Highly recommended."
  }'
```

#### Get Product Reviews
```bash
curl -X GET http://localhost:8080/api/v1/reviews/product/1
```

---

## 🔧 Postman Collection

### Setup Environment Variables
```
BASE_URL: http://localhost:8080
USER_ID: 1
TOKEN: your-jwt-token-here
```

### Common Headers
```
Content-Type: application/json
User-Id: {{USER_ID}}
Authorization: Bearer {{TOKEN}}
```

---

## 📊 API Response Patterns

### Success Response
```json
{
  "success": true,
  "message": "Operation successful",
  "data": { ... },
  "timestamp": "2024-10-04T10:30:00"
}
```

### Error Response
```json
{
  "success": false,
  "message": "Error message",
  "data": null,
  "timestamp": "2024-10-04T10:30:00"
}
```

### Validation Error
```json
{
  "success": false,
  "message": "Validation failed",
  "data": {
    "email": "Email is required",
    "password": "Password must be at least 6 characters"
  },
  "timestamp": "2024-10-04T10:30:00"
}
```

### Paginated Response
```json
{
  "success": true,
  "message": "Success",
  "data": {
    "content": [ ... ],
    "pageNumber": 0,
    "pageSize": 10,
    "totalElements": 100,
    "totalPages": 10,
    "last": false
  }
}
```

---

## 🎯 Complete API Flow Example

### User Journey: Browse → Add to Cart → Checkout

```bash
# Step 1: Register
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "customer@example.com",
    "password": "password123",
    "firstName": "Alice",
    "lastName": "Smith",
    "phone": "9876543210"
  }'

# Step 2: Login
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "customer@example.com",
    "password": "password123"
  }'

# Step 3: Browse Products
curl -X GET "http://localhost:8080/api/v1/products?page=0&size=10"

# Step 4: View Product Details
curl -X GET http://localhost:8080/api/v1/products/1

# Step 5: Add to Cart
curl -X POST http://localhost:8080/api/v1/cart/items \
  -H "Content-Type: application/json" \
  -H "User-Id: 1" \
  -d '{
    "productId": 1,
    "quantity": 2
  }'

# Step 6: Add Another Item
curl -X POST http://localhost:8080/api/v1/cart/items \
  -H "Content-Type: application/json" \
  -H "User-Id: 1" \
  -d '{
    "productId": 2,
    "quantity": 1
  }'

# Step 7: View Cart
curl -X GET http://localhost:8080/api/v1/cart \
  -H "User-Id: 1"

# Step 8: Update Quantity
curl -X PUT "http://localhost:8080/api/v1/cart/items/1?quantity=3" \
  -H "User-Id: 1"

# Step 9: Checkout (Create Order)
curl -X POST http://localhost:8080/api/v1/orders \
  -H "Content-Type: application/json" \
  -H "User-Id: 1" \
  -d '{
    "paymentMethod": "CREDIT_CARD",
    "shippingAddress": {
      "street": "456 Oak Avenue",
      "city": "Bangalore",
      "state": "Karnataka",
      "zipCode": "560001",
      "country": "India"
    }
  }'

# Step 10: View Order
curl -X GET http://localhost:8080/api/v1/orders/1

# Step 11: Add Review After Delivery
curl -X POST http://localhost:8080/api/v1/reviews \
  -H "Content-Type: application/json" \
  -H "User-Id: 1" \
  -d '{
    "productId": 1,
    "rating": 5,
    "comment": "Amazing product!"
  }'
```

---

## 🧪 Integration Testing with JUnit

```java
@SpringBootTest
@AutoConfigureMockMvc
public class ProductControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Test
    public void testGetAllProducts() throws Exception {
        mockMvc.perform(get("/api/v1/products")
                .param("page", "0")
                .param("size", "10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.content").isArray());
    }
    
    @Test
    public void testCreateProduct() throws Exception {
        ProductRequest request = new ProductRequest();
        request.setName("Test Product");
        request.setPrice(BigDecimal.valueOf(1000));
        request.setStockQuantity(50);
        request.setCategoryId(1L);
        
        mockMvc.perform(post("/api/v1/products")
                .header("User-Id", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.name").value("Test Product"));
    }
    
    @Test
    public void testGetProductById_NotFound() throws Exception {
        mockMvc.perform(get("/api/v1/products/999"))
            .andExpect(status().isNotFound());
    }
}

@SpringBootTest
public class CartServiceTest {
    
    @Autowired
    private CartService cartService;
    
    @Autowired
    private ProductRepository productRepository;
    
    @Test
    @Transactional
    public void testAddToCart() {
        // Create test product
        Product product = new Product();
        product.setName("Test Product");
        product.setPrice(BigDecimal.valueOf(1000));
        product.setStockQuantity(100);
        product = productRepository.save(product);
        
        // Add to cart
        Cart cart = cartService.addToCart(1L, product.getId(), 2);
        
        // Assertions
        assertNotNull(cart);
        assertEquals(1, cart.getItems().size());
        assertEquals(2, cart.getItems().get(0).getQuantity());
        assertEquals(BigDecimal.valueOf(2000), cart.getTotalAmount());
    }
    
    @Test
    @Transactional
    public void testAddToCart_InsufficientStock() {
        Product product = new Product();
        product.setName("Test Product");
        product.setPrice(BigDecimal.valueOf(1000));
        product.setStockQuantity(1);
        product = productRepository.save(product);
        
        assertThrows(InvalidRequestException.class, () -> {
            cartService.addToCart(1L, product.getId(), 10);
        });
    }
}
```

---

## 📱 Mobile App Integration Example (React/React Native)

```javascript
// API Service
import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api/v1';

class ApiService {
  constructor() {
    this.axios = axios.create({
      baseURL: API_BASE_URL,
      headers: {
        'Content-Type': 'application/json'
      }
    });
    
    // Add token to requests
    this.axios.interceptors.request.use(config => {
      const token = localStorage.getItem('token');
      if (token) {
        config.headers.Authorization = `Bearer ${token}`;
      }
      return config;
    });
  }
  
  // Auth
  async register(userData) {
    const response = await this.axios.post('/auth/register', userData);
    return response.data;
  }
  
  async login(credentials) {
    const response = await this.axios.post('/auth/login', credentials);
    if (response.data.success) {
      localStorage.setItem('token', response.data.data.token);
    }
    return response.data;
  }
  
  // Products
  async getProducts(page = 0, size = 20) {
    const response = await this.axios.get('/products', {
      params: { page, size }
    });
    return response.data;
  }
  
  async getProductById(id) {
    const response = await this.axios.get(`/products/${id}`);
    return response.data;
  }
  
  async searchProducts(keyword) {
    const response = await this.axios.get('/products/search', {
      params: { keyword }
    });
    return response.data;
  }
  
  // Cart
  async getCart(userId) {
    const response = await this.axios.get('/cart', {
      headers: { 'User-Id': userId }
    });
    return response.data;
  }
  
  async addToCart(userId, productId, quantity) {
    const response = await this.axios.post('/cart/items', 
      { productId, quantity },
      { headers: { 'User-Id': userId } }
    );
    return response.data;
  }
  
  async updateCartItem(userId, productId, quantity) {
    const response = await this.axios.put(
      `/cart/items/${productId}?quantity=${quantity}`,
      {},
      { headers: { 'User-Id': userId } }
    );
    return response.data;
  }
  
  async removeFromCart(userId, productId) {
    const response = await this.axios.delete(`/cart/items/${productId}`, {
      headers: { 'User-Id': userId }
    });
    return response.data;
  }
  
  // Orders
  async createOrder(userId, orderData) {
    const response = await this.axios.post('/orders', orderData, {
      headers: { 'User-Id': userId }
    });
    return response.data;
  }
  
  async getOrders(userId) {
    const response = await this.axios.get('/orders', {
      headers: { 'User-Id': userId }
    });
    return response.data;
  }
  
  async getOrderById(orderId) {
    const response = await this.axios.get(`/orders/${orderId}`);
    return response.data;
  }
  
  // Reviews
  async addReview(userId, reviewData) {
    const response = await this.axios.post('/reviews', reviewData, {
      headers: { 'User-Id': userId }
    });
    return response.data;
  }
  
  async getProductReviews(productId) {
    const response = await this.axios.get(`/reviews/product/${productId}`);
    return response.data;
  }
}

export default new ApiService();
```

### React Component Example

```javascript
import React, { useState, useEffect } from 'react';
import ApiService from './services/ApiService';

function ProductList() {
  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [page, setPage] = useState(0);
  
  useEffect(() => {
    loadProducts();
  }, [page]);
  
  const loadProducts = async () => {
    try {
      setLoading(true);
      const response = await ApiService.getProducts(page, 20);
      if (response.success) {
        setProducts(response.data.content);
      }
    } catch (error) {
      console.error('Error loading products:', error);
    } finally {
      setLoading(false);
    }
  };
  
  const handleAddToCart = async (productId) => {
    try {
      const userId = localStorage.getItem('userId');
      const response = await ApiService.addToCart(userId, productId, 1);
      if (response.success) {
        alert('Product added to cart!');
      }
    } catch (error) {
      alert('Failed to add product to cart');
    }
  };
  
  if (loading) return <div>Loading...</div>;
  
  return (
    <div className="product-list">
      {products.map(product => (
        <div key={product.id} className="product-card">
          <h3>{product.name}</h3>
          <p>{product.description}</p>
          <p className="price">₹{product.price}</p>
          <p>Stock: {product.stockQuantity}</p>
          <p>Rating: {product.averageRating} ⭐ ({product.reviewCount})</p>
          <button onClick={() => handleAddToCart(product.id)}>
            Add to Cart
          </button>
        </div>
      ))}
      
      <div className="pagination">
        <button onClick={() => setPage(page - 1)} disabled={page === 0}>
          Previous
        </button>
        <span>Page {page + 1}</span>
        <button onClick={() => setPage(page + 1)}>
          Next
        </button>
      </div>
    </div>
  );
}

export default ProductList;
```

---

## 🔐 Security Best Practices

### 1. HTTPS Only
```properties
# Force HTTPS in production
server.ssl.enabled=true
server.ssl.key-store=classpath:keystore.p12
server.ssl.key-store-password=password
server.ssl.key-store-type=PKCS12
```

### 2. CORS Configuration
```java
@Configuration
public class SecurityConfig {
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(
            "https://myapp.com",
            "https://admin.myapp.com"
        ));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration);
        return source;
    }
}
```

### 3. Rate Limiting
```java
@Component
public class RateLimitFilter implements Filter {
    private final Map<String, RateLimiter> limiters = new ConcurrentHashMap<>();
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, 
                        FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String clientId = httpRequest.getRemoteAddr();
        
        RateLimiter limiter = limiters.computeIfAbsent(
            clientId, 
            k -> RateLimiter.create(100.0) // 100 requests per second
        );
        
        if (limiter.tryAcquire()) {
            chain.doFilter(request, response);
        } else {
            ((HttpServletResponse) response).sendError(429, "Too Many Requests");
        }
    }
}
```

### 4. Input Validation
```java
@PostMapping
public ResponseEntity<?> createProduct(@Valid @RequestBody ProductRequest request) {
    // @Valid ensures validation
    // Add custom validation if needed
    if (request.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
        throw new InvalidRequestException("Price must be positive");
    }
    // ... rest of code
}
```

---

## 📊 Performance Optimization Tips

### 1. Database Connection Pool
```properties
# HikariCP Configuration
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.connection-timeout=30000
spring.datasource.hikari.idle-timeout=600000
spring.datasource.hikari.max-lifetime=1800000
```

### 2. Caching
```java
@Service
@CacheConfig(cacheNames = "products")
public class ProductService {
    
    @Cacheable(key = "#id")
    public Product getProductById(Long id) {
        return productRepository.findById(id).orElseThrow();
    }
    
    @CachePut(key = "#result.id")
    public Product updateProduct(Long id, Product product) {
        return productRepository.save(product);
    }
    
    @CacheEvict(key = "#id")
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }
}
```

### 3. Async Processing
```java
@Service
public class OrderService {
    
    @Autowired
    private JavaMailSender mailSender;
    
    @Async
    public void sendOrderConfirmationEmail(Order order) {
        // Send email asynchronously
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(order.getUser().getEmail());
        message.setSubject("Order Confirmation: " + order.getOrderNumber());
        message.setText("Your order has been confirmed!");
        mailSender.send(message);
    }
}
```

### 4. Response Compression
```properties
# Enable GZIP compression
server.compression.enabled=true
server.compression.mime-types=application/json,application/xml,text/html,text/xml,text/plain
server.compression.min-response-size=1024
```

---

## 📚 Key Takeaways

### REST API vs Spring Data JPA

| Aspect | Spring Data JPA | REST API |
|--------|----------------|----------|
| **Layer** | Data Access (Backend) | Presentation (API) |
| **Purpose** | Database Operations | HTTP Communication |
| **Used By** | Services | Controllers |
| **Returns** | Entity Objects | JSON/XML |
| **Annotations** | `@Entity`, `@Repository` | `@RestController`, `@GetMapping` |

### Complete Flow
```
Client (Mobile/Web)
    ↓ HTTP Request
REST API Controller
    ↓ Call
Service Layer
    ↓ Call
Repository (Spring Data JPA)
    ↓ Query
Database
    ↑ Data
Repository
    ↑ Entity
Service
    ↑ DTO
Controller
    ↑ JSON Response
Client
```

### HTTP Status Codes Cheat Sheet
- **200 OK** - Success (GET, PUT, PATCH)
- **201 Created** - Resource created (POST)
- **204 No Content** - Success, no data (DELETE)
- **400 Bad Request** - Invalid data
- **401 Unauthorized** - Not authenticated
- **403 Forbidden** - No permission
- **404 Not Found** - Resource doesn't exist
- **409 Conflict** - Duplicate/version conflict
- **429 Too Many Requests** - Rate limit exceeded
- **500 Internal Server Error** - Server error

---

## 🎓 Learning Path Summary

**You've learned:**
✅ REST API fundamentals & HTTP methods
✅ CRUD operations with proper status codes
✅ Request/Response handling with DTOs
✅ Validation & exception handling
✅ Authentication & security
✅ File upload/download
✅ Pagination & filtering
✅ Caching & performance optimization
✅ Complete E-Commerce project
✅ Testing strategies

**Next Steps:**
1. Add Spring Security with JWT
2. Implement payment gateway integration
3. Add email notifications
4. Implement real-time features with WebSocket
5. Deploy to cloud (AWS, Azure, or Heroku)
6. Add monitoring with Actuator
7. Write comprehensive tests
8. Document with Swagger/OpenAPI

🚀 **You're now ready to build production-grade REST APIs!**