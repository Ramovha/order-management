package com.ordermgmt.order.controller;

import com.ordermgmt.order.model.Order;
import com.ordermgmt.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * REST Controller for Order endpoints.
 * Provides CRUD operations for orders with inter-service validation.
 * 
 * @author Order Management Team
 * @version 1.0
 */
@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@Tag(name = "Orders", description = "Order management endpoints")
public class OrderController {

    private final OrderService orderService;

    /**
     * Get all orders.
     * 
     * @return List of all orders
     */
    @GetMapping
    @Operation(summary = "Get all orders", description = "Retrieve a list of all orders")
    public ResponseEntity<List<Order>> getAllOrders() {
        List<Order> orders = orderService.getAllOrders();
        return ResponseEntity.ok(orders);
    }

    /**
     * Get order by ID.
     * 
     * @param id Order ID
     * @return Order with the given ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get order by ID", description = "Retrieve a specific order by its ID")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
        Optional<Order> order = orderService.getOrderById(id);
        return order.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Get orders by customer email.
     * 
     * @param email Customer email
     * @return List of orders for customer
     */
    @GetMapping("/customer/{email}")
    @Operation(summary = "Get orders by customer email", description = "Retrieve all orders for a specific customer")
    public ResponseEntity<List<Order>> getOrdersByCustomerEmail(@PathVariable String email) {
        List<Order> orders = orderService.getOrdersByCustomerEmail(email);
        return ResponseEntity.ok(orders);
    }

    /**
     * Get orders by status.
     * 
     * @param status Order status
     * @return List of orders with given status
     */
    @GetMapping("/status/{status}")
    @Operation(summary = "Get orders by status", description = "Retrieve all orders with a specific status")
    public ResponseEntity<List<Order>> getOrdersByStatus(@PathVariable Order.OrderStatus status) {
        List<Order> orders = orderService.getOrdersByStatus(status);
        return ResponseEntity.ok(orders);
    }

    /**
     * Create a new order.
     * Validates all products exist in Product Service before creating order.
     * 
     * @param order Order to create
     * @return Created order
     */
    @PostMapping
    @Operation(summary = "Create a new order", description = "Create a new order with items. Products must exist in Product Service.")
    public ResponseEntity<?> createOrder(@Valid @RequestBody Order order) {
        try {
            Order createdOrder = orderService.createOrder(order);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdOrder);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid order: " + e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Order creation failed: " + e.getMessage());
        }
    }

    /**
     * Update an existing order.
     * 
     * @param id Order ID to update
     * @param order Updated order data
     * @return Updated order
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update an order", description = "Update an existing order by its ID")
    public ResponseEntity<Order> updateOrder(@PathVariable Long id, @Valid @RequestBody Order order) {
        try {
            Order updatedOrder = orderService.updateOrder(id, order);
            return ResponseEntity.ok(updatedOrder);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Delete an order.
     * 
     * @param id Order ID to delete
     * @return No content response
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an order", description = "Delete an order by its ID")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        try {
            orderService.deleteOrder(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
