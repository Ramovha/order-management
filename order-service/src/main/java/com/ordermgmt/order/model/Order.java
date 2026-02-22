package com.ordermgmt.order.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Order entity representing a customer order.
 * 
 * @author Order Management Team
 * @version 1.0
 */
@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Customer name is required")
    @Size(min = 3, max = 100, message = "Customer name must be between 3 and 100 characters")
    @Column(nullable = false, length = 100)
    private String customerName;

    @Email(message = "Customer email must be valid")
    @NotBlank(message = "Customer email is required")
    @Column(nullable = false, length = 100)
    private String customerEmail;

    @NotBlank(message = "Shipping address is required")
    @Size(min = 10, max = 255, message = "Shipping address must be between 10 and 255 characters")
    @Column(nullable = false, length = 255)
    private String shippingAddress;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private OrderStatus status = OrderStatus.PENDING;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @Builder.Default
    private List<OrderItem> items = new ArrayList<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        calculateTotalPrice();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        calculateTotalPrice();
    }

    /**
     * Calculate total price from all order items
     */
    public void calculateTotalPrice() {
        if (items == null || items.isEmpty()) {
            this.totalPrice = BigDecimal.ZERO;
        } else {
            this.totalPrice = items.stream()
                    .map(OrderItem::getTotalPrice)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }
    }

    /**
     * Add item to order
     */
    public void addItem(OrderItem item) {
        if (items == null) {
            items = new ArrayList<>();
        }
        item.setOrder(this);
        items.add(item);
        calculateTotalPrice();
    }

    /**
     * Order status enumeration
     */
    public enum OrderStatus {
        PENDING,
        CONFIRMED,
        SHIPPED,
        DELIVERED,
        CANCELLED
    }
}
