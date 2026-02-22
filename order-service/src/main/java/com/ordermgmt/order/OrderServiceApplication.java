package com.ordermgmt.order;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * Order Service Application - Main entry point for the Order microservice.
 *
 * <p>This is a Spring Boot application that manages order-related operations including:
 * <ul>
 *   <li>Creating new orders</li>
 *   <li>Retrieving order information by ID</li>
 *   <li>Listing all orders</li>
 *   <li>Validating product availability through Product Service integration</li>
 * </ul>
 *
 * <p>The application runs on port 8082 with context path /api/v1 and provides REST endpoints
 * secured with HTTP Basic Authentication. It communicates with the Product Service via REST
 * to validate product existence and retrieve product details. API documentation is available 
 * through Swagger/OpenAPI.
 *
 * @author Order Management Team
 * @version 1.0.0
 * @see org.springframework.boot.autoconfigure.SpringBootApplication
 */
@SpringBootApplication
public class OrderServiceApplication {

    /**
     * Main method to start the Order Service application.
     *
     * @param args Command-line arguments (optional)
     */
    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
    }

    /**
     * Configures OpenAPI (Swagger) documentation for the Order Service API.
     *
     * <p>This bean defines the API metadata including title, version, description,
     * and security scheme (HTTP Basic Authentication) to be displayed in the Swagger UI.
     *
     * @return Configured OpenAPI object with API information and security requirements
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .addSecurityItem(new SecurityRequirement().addList("basicAuth"))
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes("basicAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("basic")
                                        .description("HTTP Basic Authentication")))
                .info(new Info()
                        .title("Order Service API")
                        .version("1.0.0")
                        .description("RESTful API for managing orders"));
    }
}
