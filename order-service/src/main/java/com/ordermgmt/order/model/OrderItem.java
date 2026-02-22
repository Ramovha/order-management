package com.ordermgmt.order.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Represents a line item in an order.
 * Each OrderItem references a product and contains quantity and pricing information.
 * 
 * @author Order Management Team
 * @version 1.0
 */
@Entity
@Table(name = "order_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Product ID cannot be null")
    @Column(nullable = false)
    private Long productId;

    @NotNull(message = "Quantity cannot be null")
    @Min(value = 1, message = "Quantity must be at least 1")
    @Column(nullable = false)
    private Integer quantity;

    @NotNull(message = "Unit price cannot be null")
    @DecimalMin(value = "0.01", message = "Unit price must be greater than 0")
    @Column(nullable = false)
    private BigDecimal unitPrice;

    @Column(nullable = false)
    private BigDecimal totalPrice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    @JsonIgnore
    private Order order;

    /**
     * Calculate the total price for this item (quantity * unitPrice).
     */
    public void calculateTotalPrice() {
        if (this.quantity != null && this.unitPrice != null) {
            this.totalPrice = this.unitPrice.multiply(new BigDecimal(this.quantity));
        }
    }

    /**
     * Get display-friendly total price.
     * 
     * @return Total price as BigDecimal
     */
    public BigDecimal getTotalPrice() {
        if (this.totalPrice == null) {
            calculateTotalPrice();
        }
        return this.totalPrice;
    }
}
