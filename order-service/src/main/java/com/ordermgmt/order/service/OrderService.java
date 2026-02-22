package com.ordermgmt.order.service;

import com.ordermgmt.order.dto.ProductResponse;
import com.ordermgmt.order.model.Order;
import com.ordermgmt.order.model.OrderItem;
import com.ordermgmt.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

/**
 * Service layer for Order business logic.
 * Handles CRUD operations and inter-service communication with Product Service.
 * 
 * @author Order Management Team
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);
    private final OrderRepository orderRepository;
    private final RestTemplate restTemplate;

    @Value("${external.product-service.url:http://localhost:9081/api/v1}")
    private String productServiceUrl;

    /**
     * Get all orders.
     * 
     * @return List of all orders
     */
    @Transactional(readOnly = true)
    public List<Order> getAllOrders() {
        logger.info("Retrieving all orders");
        return orderRepository.findAll();
    }

    /**
     * Get order by ID.
     * 
     * @param id Order ID
     * @return Optional containing order if found
     */
    @Transactional(readOnly = true)
    public Optional<Order> getOrderById(Long id) {
        logger.info("Retrieving order with id: {}", id);
        return orderRepository.findById(id);
    }

    /**
     * Create new order with items.
     * Validates that all products exist in Product Service before creating order.
     * 
     * @param order Order to create
     * @return Saved order
     */
    public Order createOrder(Order order) {
        logger.info("Creating new order for customer: {}", order.getCustomerName());

        if (order.getItems() == null || order.getItems().isEmpty()) {
            throw new IllegalArgumentException("Order must contain at least one item");
        }

        // Validate all products exist in Product Service
        for (OrderItem item : order.getItems()) {
            validateProductExists(item.getProductId());
        }

        // Ensure bidirectional relationship is set
        for (OrderItem item : order.getItems()) {
            item.setOrder(order);
            item.calculateTotalPrice();
        }

        // Calculate order total
        order.calculateTotalPrice();

        return orderRepository.save(order);
    }

    /**
     * Update order by ID.
     * 
     * @param id Order ID
     * @param order Updated order data
     * @return Updated order
     */
    public Order updateOrder(Long id, Order order) {
        logger.info("Updating order with id: {}", id);

        Optional<Order> existingOrder = orderRepository.findById(id);
        if (existingOrder.isEmpty()) {
            throw new RuntimeException("Order not found with id: " + id);
        }

        Order orderToUpdate = existingOrder.get();
        orderToUpdate.setCustomerName(order.getCustomerName());
        orderToUpdate.setCustomerEmail(order.getCustomerEmail());
        orderToUpdate.setShippingAddress(order.getShippingAddress());
        orderToUpdate.setStatus(order.getStatus());

        return orderRepository.save(orderToUpdate);
    }

    /**
     * Delete order by ID.
     * 
     * @param id Order ID
     */
    public void deleteOrder(Long id) {
        logger.info("Deleting order with id: {}", id);

        if (!orderRepository.existsById(id)) {
            throw new RuntimeException("Order not found with id: " + id);
        }

        orderRepository.deleteById(id);
    }

    /**
     * Get orders by customer email.
     * 
     * @param customerEmail Customer email
     * @return List of orders for customer
     */
    @Transactional(readOnly = true)
    public List<Order> getOrdersByCustomerEmail(String customerEmail) {
        logger.info("Retrieving orders for customer: {}", customerEmail);
        return orderRepository.findByCustomerEmail(customerEmail);
    }

    /**
     * Get orders by status.
     * 
     * @param status Order status
     * @return List of orders with given status
     */
    @Transactional(readOnly = true)
    public List<Order> getOrdersByStatus(Order.OrderStatus status) {
        logger.info("Retrieving orders with status: {}", status);
        return orderRepository.findByStatus(status);
    }

    /**
     * Validate that a product exists in Product Service.
     * Calls Product Service via RestTemplate with Basic Auth.
     * 
     * @param productId Product ID to validate
     * @throws RuntimeException if product not found
     */
    private void validateProductExists(Long productId) {
        logger.info("Validating product {} exists in Product Service", productId);
        logger.info("Product Service URL configured: {}", productServiceUrl);

        try {
            String url = productServiceUrl + "/products/" + productId;
            logger.info("Making request to: {}", url);
            
            // Create HTTP headers with Basic Auth
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));
            
            String auth = "admin:password";
            String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));
            headers.set("Authorization", "Basic " + encodedAuth);
            logger.info("Authorization header set");
            
            HttpEntity<String> entity = new HttpEntity<>(headers);
            logger.info("Calling Product Service...");
            ResponseEntity<ProductResponse> response = restTemplate.exchange(url, HttpMethod.GET, entity, ProductResponse.class);
            
            logger.info("Response status: {}", response.getStatusCode());
            logger.info("Response received from Product Service");
            
            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                logger.warn("Invalid response from Product Service for product {}", productId);
                throw new RuntimeException("Product not found with id: " + productId);
            }

            logger.info("Product {} validated successfully: {}", productId, response.getBody().getName());
        } catch (Exception e) {
            logger.error("Failed to validate product {}: {} - {}", productId, e.getClass().getSimpleName(), e.getMessage());
            throw new RuntimeException("Product Service unavailable or product not found: " + productId, e);
        }
    }
}
