package com.ordermgmt.product.service;

import com.ordermgmt.product.model.Product;
import com.ordermgmt.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service layer for Product business logic.
 * Handles CRUD operations and business rules.
 * 
 * @author Order Management Team
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {

    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);
    private final ProductRepository productRepository;

    /**
     * Get all products.
     * 
     * @return List of all products
     */
    @Transactional(readOnly = true)
    public List<Product> getAllProducts() {
        logger.info("Retrieving all products");
        return productRepository.findAll();
    }

    /**
     * Get product by ID.
     * 
     * @param id Product ID
     * @return Optional containing product if found
     */
    @Transactional(readOnly = true)
    public Optional<Product> getProductById(Long id) {
        logger.info("Retrieving product with id: {}", id);
        return productRepository.findById(id);
    }

    /**
     * Get product by SKU.
     * 
     * @param sku Product SKU
     * @return Product if found, null otherwise
     */
    @Transactional(readOnly = true)
    public Product getProductBySku(String sku) {
        logger.info("Retrieving product with SKU: {}", sku);
        return productRepository.findBySku(sku);
    }

    /**
     * Create new product.
     * 
     * @param product Product to create
     * @return Saved product
     */
    public Product createProduct(Product product) {
        logger.info("Creating new product with SKU: {}", product.getSku());
        
        // Check if product with same SKU already exists
        Product existingProduct = productRepository.findBySku(product.getSku());
        if (existingProduct != null) {
            throw new IllegalArgumentException("Product with SKU " + product.getSku() + " already exists");
        }
        
        return productRepository.save(product);
    }

    /**
     * Update existing product.
     * 
     * @param id Product ID
     * @param product Updated product data
     * @return Updated product
     */
    public Product updateProduct(Long id, Product product) {
        logger.info("Updating product with id: {}", id);
        
        Optional<Product> existingProduct = productRepository.findById(id);
        if (existingProduct.isEmpty()) {
            throw new RuntimeException("Product not found with id: " + id);
        }

        Product productToUpdate = existingProduct.get();
        productToUpdate.setName(product.getName());
        productToUpdate.setDescription(product.getDescription());
        productToUpdate.setPrice(product.getPrice());
        productToUpdate.setQuantity(product.getQuantity());
        
        return productRepository.save(productToUpdate);
    }

    /**
     * Delete product by ID.
     * 
     * @param id Product ID
     */
    public void deleteProduct(Long id) {
        logger.info("Deleting product with id: {}", id);
        
        if (!productRepository.existsById(id)) {
            throw new RuntimeException("Product not found with id: " + id);
        }
        
        productRepository.deleteById(id);
    }
}
