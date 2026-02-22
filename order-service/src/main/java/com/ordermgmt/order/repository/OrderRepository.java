package com.ordermgmt.order.repository;

import com.ordermgmt.order.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for Order entity.
 * Extends JpaRepository to provide CRUD operations.
 * 
 * @author Order Management Team
 * @version 1.0
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByCustomerEmail(String customerEmail);
    List<Order> findByStatus(Order.OrderStatus status);
}
