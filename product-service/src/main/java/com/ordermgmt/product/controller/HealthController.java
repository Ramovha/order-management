package com.ordermgmt.product.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Health Check Controller for the Product Service.
 *
 * <p>Provides endpoints to verify the service is running and healthy.
 * These endpoints are publicly accessible without authentication.
 *
 * @author Order Management Team
 * @version 1.0.0
 */
@RestController
@RequestMapping("/health")
@Tag(name = "Health", description = "Service health check endpoints")
public class HealthController {

    /**
     * Health check endpoint.
     *
     * <p>Returns a simple status to verify the service is running.
     * This endpoint does not require authentication.
     *
     * @return HTTP 200 with status message
     */
    @GetMapping
    @Operation(summary = "Health check", description = "Verify service is running", security = @SecurityRequirement(name = ""))
    public ResponseEntity<?> health() {
        return ResponseEntity.ok(new HealthStatus("UP", "Product Service is running"));
    }

    /**
     * Inner class representing health status response.
     */
    public static class HealthStatus {
        public String status;
        public String message;

        public HealthStatus(String status, String message) {
            this.status = status;
            this.message = message;
        }

        public String getStatus() {
            return status;
        }

        public String getMessage() {
            return message;
        }
    }
}
