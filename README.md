# Order Management Microservices

A Spring Boot microservices architecture for managing Products and Orders with clean, layered structure, comprehensive testing, security, and API documentation.

## Project Structure

```
order-management/
├── pom.xml (Parent POM - defines shared dependencies)
├── product-service/ (Port 8081)
│   ├── pom.xml
│   └── src/
│       ├── main/
│       │   ├── java/com/ordermgmt/product/
│       │   │   ├── ProductServiceApplication.java
│       │   │   └── config/
│       │   │       └── SecurityConfig.java
│       │   └── resources/
│       │       └── application.yml
│       └── test/java/com/ordermgmt/product/
└── order-service/ (Port 8082)
    ├── pom.xml
    └── src/
        ├── main/
        │   ├── java/com/ordermgmt/order/
        │   │   ├── OrderServiceApplication.java
        │   │   └── config/
        │   │       ├── SecurityConfig.java
        │   │       └── WebClientConfig.java
        │   └── resources/
        │       └── application.yml
        └── test/java/com/ordermgmt/order/
```

## Features Implemented

### Phase 1: Setup (✅ Completed)
- [x] Maven multi-module project structure
- [x] Spring Boot 3.2.0 with Java 17
- [x] H2 in-memory databases (separate for each service)
- [x] Spring Security with Basic Authentication
- [x] SpringDoc OpenAPI (Swagger UI)
- [x] SLF4J Logging configuration
- [x] JUnit 5, Mockito, Spring Security Test

### Phase 2: Features (✅ Completed)
- [x] Product Service CRUD endpoints
- [x] Order Service CRUD endpoints
- [x] Inter-service communication (Order -> Product)
- [x] Business logic and validation

### Phase 3: Testing (✅ Completed)
- [x] Unit tests for services (JUnit 5 with Mockito)
- [x] Product Service tests (10 tests)
- [x] Order Service tests (11 tests)
- [ ] Integration tests for APIs

### Phase 4: Documentation
- [ ] README with setup instructions
- [ ] Design decisions documentation

## Quick Start

### Prerequisites
- Java 17+
- Maven 3.6+

### Running the Services

#### Build the project:
```bash
mvn clean install
```

#### Run Product Service (Port 8081):
```bash
mvn -pl product-service spring-boot:run
```

#### Run Order Service (Port 8082):
```bash
mvn -pl order-service spring-boot:run
```

### API Documentation

#### Product Service Swagger UI:
```
http://localhost:8081/api/v1/swagger-ui.html
```

#### Order Service Swagger UI:
```
http://localhost:8082/api/v1/swagger-ui.html
```

### Authentication

**Credentials:**
- Username: `admin`
- Password: `password`

All endpoints (except Swagger UI) require Basic Authentication.

## Technology Stack

- **Framework**: Spring Boot 3.2.0
- **Language**: Java 17
- **Database**: H2 (in-memory)
- **ORM**: Hibernate/JPA
- **Security**: Spring Security with Basic Auth
- **API Documentation**: SpringDoc OpenAPI (Swagger)
- **Testing**: JUnit 5, Mockito
- **Build**: Maven

## Unit Testing

### Test Setup & Architecture

The project includes comprehensive unit tests using **JUnit 5** and **Mockito** for both services. Tests are organized as follows:

#### Product Service Tests
- **ProductServiceTest** (10 tests)
  - Tests CRUD operations (create, read, update, delete)
  - Tests SKU uniqueness validation
  - Tests error handling and edge cases
  - Uses Mockito to mock ProductRepository
  - Location: `product-service/src/test/java/com/ordermgmt/product/service/ProductServiceTest.java`

#### Order Service Tests
- **OrderServiceTest** (11 tests)
  - Tests CRUD operations for orders
  - Tests inter-service communication with Product Service via RestTemplate
  - Tests product validation logic
  - Tests order total price calculation
  - Tests error handling (product not found, service unavailable)
  - Uses Mockito to mock OrderRepository and RestTemplate
  - Location: `order-service/src/test/java/com/ordermgmt/order/service/OrderServiceTest.java`

### Mocking Strategy

**Product Service Tests:**
- Mocks `ProductRepository` using `@Mock`
- Injects mocks into `ProductService` using `@InjectMocks`
- Verifies method calls and arguments using Mockito's `verify()`

**Order Service Tests:**
- Mocks `OrderRepository` for database operations
- Mocks `RestTemplate` for inter-service HTTP calls to Product Service
- Tests product validation with both successful and failed scenarios
- Verifies correct headers and authentication in RestTemplate calls

### Running Tests

#### Run all tests:
```bash
mvn test -DskipITs
```

#### Run tests for specific module:
```bash
# Product Service tests only
mvn -pl product-service test

# Order Service tests only
mvn -pl order-service test
```

#### Run specific test class:
```bash
# Run ProductServiceTest
mvn -pl product-service test -Dtest=ProductServiceTest

# Run OrderServiceTest
mvn -pl order-service test -Dtest=OrderServiceTest
```

#### Run specific test method:
```bash
# Run a single test
mvn -pl product-service test -Dtest=ProductServiceTest#testGetAllProducts
```

#### Generate test coverage report:
```bash
mvn clean test jacoco:report
# Coverage report will be in: target/site/jacoco/index.html
```

### Test Results

Current test status: **21 tests passing** ✅

```
Product Service: 10/10 tests passing
├── testGetAllProducts
├── testGetProductById
├── testGetProductByIdNotFound
├── testCreateProductSuccess
├── testCreateProductDuplicateSku
├── testUpdateProductSuccess
├── testUpdateProductNotFound
├── testDeleteProductSuccess
├── testDeleteProductNotFound
└── testProductValidation

Order Service: 11/11 tests passing
├── testGetAllOrders
├── testGetOrderById
├── testGetOrderByIdNotFound
├── testCreateOrderSuccess
├── testCreateOrderWithNoItems
├── testCreateOrderProductNotFound
├── testUpdateOrderSuccess
├── testUpdateOrderNotFound
├── testDeleteOrderSuccess
├── testDeleteOrderNotFound
└── testOrderTotalPriceCalculation
```

### Test Examples

#### Example: Product Service - Testing SKU Uniqueness
```java
@Test
@DisplayName("Should throw exception when creating product with duplicate SKU")
void testCreateProductDuplicateSku() {
    // Arrange
    Product newProduct = Product.builder()
            .name("Duplicate Laptop")
            .sku("TEST-LAPTOP-001")
            .build();
    
    when(productRepository.findBySku("TEST-LAPTOP-001"))
            .thenReturn(Optional.of(existingProduct));
    
    // Act & Assert
    RuntimeException exception = assertThrows(RuntimeException.class, () -> {
        productService.createProduct(newProduct);
    });
    
    assertTrue(exception.getMessage().contains("already exists"));
    verify(productRepository, never()).save(any(Product.class));
}
```

#### Example: Order Service - Testing Inter-Service Communication
```java
@Test
@DisplayName("Should create order successfully with valid product")
void testCreateOrderSuccess() {
    // Arrange - Mock successful product validation
    ResponseEntity<ProductResponse> response = 
            new ResponseEntity<>(productResponse, HttpStatus.OK);
    
    when(restTemplate.exchange(
            contains("products/1"),
            eq(HttpMethod.GET),
            any(),
            eq(ProductResponse.class)))
            .thenReturn(response);
    
    when(orderRepository.save(any(Order.class)))
            .thenReturn(savedOrder);
    
    // Act
    Order actual = orderService.createOrder(order);
    
    // Assert
    assertNotNull(actual);
    assertEquals(1, actual.getItems().size());
    verify(restTemplate, times(1)).exchange(anyString(), any(), any(), any());
}
```


### Health Check Tests

Once services are running, verify they are healthy:

#### Product Service Health Check:
```bash
curl http://localhost:8081/api/v1/health
```

**Expected Response:**
```json
{
  "status": "UP",
  "message": "Product Service is running"
}
```

#### Order Service Health Check:
```bash
curl http://localhost:8082/api/v1/health
```

**Expected Response:**
```json
{
  "status": "UP",
  "message": "Order Service is running"
}
```

### API Documentation & Swagger UI

View interactive API documentation using Swagger UI:

#### Product Service Swagger UI:
- **URL**: http://localhost:8081/api/v1/swagger-ui.html
- **OpenAPI Spec**: http://localhost:8081/api/v1/v3/api-docs

#### Order Service Swagger UI:
- **URL**: http://localhost:8082/api/v1/swagger-ui.html
- **OpenAPI Spec**: http://localhost:8082/api/v1/v3/api-docs

**Note:** Swagger UI and API docs are publicly accessible without authentication.

### Testing Endpoints with curl

#### Test Product Service Health (pretty printed):
```bash
curl -s http://localhost:8081/api/v1/health | jq .
```

#### Test Order Service Health (pretty printed):
```bash
curl -s http://localhost:8082/api/v1/health | jq .
```

#### Test with Authentication (for protected endpoints):
```bash
curl -u admin:password http://localhost:8081/api/v1/products
```

### Example Test Session

Start both services in separate terminals:

**Terminal 1 - Product Service:**
```bash
mvn -pl product-service spring-boot:run
# Service starts on port 8081
```

**Terminal 2 - Order Service:**
```bash
mvn -pl order-service spring-boot:run
# Service starts on port 8082
```

**Terminal 3 - Run Tests:**
```bash
# Test Product Service
curl http://localhost:8081/api/v1/health

# Test Order Service
curl http://localhost:8082/api/v1/health

# Access Product Service Swagger
open http://localhost:8081/api/v1/swagger-ui.html

# Access Order Service Swagger
open http://localhost:8082/api/v1/swagger-ui.html
```

