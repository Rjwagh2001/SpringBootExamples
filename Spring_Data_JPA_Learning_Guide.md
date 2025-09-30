# Spring Data JPA Complete Learning Guide

This guide is a fully detailed Spring Data JPA learning path from basics to advanced features, with practical examples used in top product-based companies.

---

## 1. Foundation Concepts

### JPA vs Hibernate

* **JPA:** Java Persistence API, specification for ORM.
* **Hibernate:** Implementation of JPA with additional features.

### Spring Boot Setup

**pom.xml dependencies:**

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    <dependency>
        <groupId>com.h2database</groupId>
        <artifactId>h2</artifactId>
        <scope>runtime</scope>
    </dependency>
</dependencies>
```

**application.properties:**

```
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.show-sql=true
spring.jpa.hibernate.ddl-auto=update
```

### Entities & Table Mapping

```java
@Entity
@Table(name="employees")
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false)
    private String name;

    private String department;
}
```

### Repository Interfaces

```java
public interface EmployeeRepository extends JpaRepository<Employee, Long> {}
```

---

## 2. Basic CRUD Operations

### Create/Save

```java
Employee emp = new Employee();
emp.setName("Rahul");
emp.setDepartment("IT");
employeeRepository.save(emp);
```

### Read/Find

```java
List<Employee> employees = employeeRepository.findAll();
Employee emp = employeeRepository.findById(1L).orElse(null);
```

### Update

```java
Employee emp = employeeRepository.findById(1L).get();
emp.setDepartment("HR");
employeeRepository.save(emp);
```

### Delete

```java
employeeRepository.deleteById(1L);
```

**Practical Example:** Employee Management CRUD operations.

---

## 3. Query Methods

### Derived Query Methods

```java
List<Employee> findByDepartment(String department);
List<Employee> findByNameContaining(String keyword);
```

### Query by Example (QBE)

```java
Example<Employee> example = Example.of(new Employee("Rahul", null));
List<Employee> result = employeeRepository.findAll(example);
```

### Sorting & Pagination

```java
Pageable pageable = PageRequest.of(0, 5, Sort.by("name"));
Page<Employee> page = employeeRepository.findAll(pageable);
```

**Practical Example:** Product Catalog with search and pagination.

---

## 4. Custom Queries

### JPQL & @Query

```java
@Query("SELECT e FROM Employee e WHERE e.department = ?1")
List<Employee> findByDept(String department);
```

### Native Queries

```java
@Query(value = "SELECT * FROM employees WHERE department = ?1", nativeQuery = true)
List<Employee> findByDeptNative(String department);
```

### Named Queries

```java
@NamedQuery(name="Employee.findByName", query="SELECT e FROM Employee e WHERE e.name = :name")
```

**Practical Example:** Customer Orders filtered by status or date.

---

## 5. Relationships & Associations

### OneToOne

```java
@OneToOne
@JoinColumn(name="address_id")
private Address address;
```

### OneToMany / ManyToOne

```java
@OneToMany(mappedBy="employee")
private List<Task> tasks;

@ManyToOne
@JoinColumn(name="employee_id")
private Employee employee;
```

### ManyToMany

```java
@ManyToMany
@JoinTable(
  name="student_course",
  joinColumns=@JoinColumn(name="student_id"),
  inverseJoinColumns=@JoinColumn(name="course_id")
)
private Set<Course> courses;
```

### Cascading & Fetch Types

* `cascade = CascadeType.ALL` for dependent objects.
* `fetch = FetchType.LAZY / EAGER` for performance.

**Practical Example:** Social Media Platform (Users, Posts, Roles).

---

## 6. Advanced Features

### Projections & DTOs

```java
public interface EmployeeNameOnly {
  String getName();
}
List<EmployeeNameOnly> names = employeeRepository.findAllProjectedBy();
```

### Specifications / Dynamic Queries

```java
Specification<Employee> spec = (root, query, cb) -> cb.equal(root.get("department"), "IT");
List<Employee> employees = employeeRepository.findAll(spec);
```

### Auditing

```java
@CreatedDate private LocalDateTime createdAt;
@CreatedBy private String createdBy;
```

### Event Listeners

```java
@PrePersist
public void prePersist() {
    this.createdAt = LocalDateTime.now();
}
```

**Practical Example:** E-commerce app with auditing and dynamic filters.

---

## 7. Performance Optimization

### Lazy vs Eager Loading

* Use `LAZY` for large collections to avoid N+1 selects.

### Batch Operations

```java
List<Employee> batch = new ArrayList<>();
employeeRepository.saveAll(batch);
```

### Query Optimization

* Use `JOIN FETCH` and indexed columns.

### Caching

* 2nd level cache for frequently accessed entities.

**Practical Example:** Inventory Management System.

---

## 8. Real-World Project

### Online Bookstore / E-commerce Application

* **Entities:** Book, Author, Customer, Order
* Full CRUD operations
* Relationships, Pagination, Sorting
* Auditing and Event Listeners
* Optimizations for performance
* Unit & Integration tests

---

**References:**

* [Spring Data JPA Official Documentation](https://spring.io/projects/spring-data-jpa)
* [Baeldung Spring Data JPA Tutorials](https://www.baeldung.com/spring-data-jpa-tutorial)
* [GeeksforGeeks Spring Data JPA Guide](https://www.geeksforgeeks.org/spring-data-jpa/)
