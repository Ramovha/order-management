package com.ordermgmt.product.repository;

import com.ordermgmt.product.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for Product entity.
 * Extends JpaRepository to provide CRUD operations.
 * 
 * @author Order Management Team
 * @version 1.0
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Product findBySku(String sku);
}
