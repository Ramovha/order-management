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

### Phase 2: Features (⏳ In Progress)
- [ ] Product Service CRUD endpoints
- [ ] Order Service CRUD endpoints
- [ ] Inter-service communication (Order -> Product)
- [ ] Business logic and validation

### Phase 3: Testing
- [ ] Unit tests for services
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

## Next Steps

1. Implement Product Service entities and endpoints
2. Implement Order Service entities and endpoints
3. Add inter-service communication
4. Create comprehensive unit and integration tests
5. Document design decisions

## Testing Guide

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

