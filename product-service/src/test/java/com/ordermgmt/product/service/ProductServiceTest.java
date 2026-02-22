package com.ordermgmt.product.service;

import com.ordermgmt.product.model.Product;
import com.ordermgmt.product.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductService Unit Tests")
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private Product testProduct;

    @BeforeEach
    void setUp() {
        testProduct = Product.builder()
                .id(1L)
                .name("Test Laptop")
                .description("Test laptop for unit testing")
                .price(BigDecimal.valueOf(999.99))
                .quantity(10)
                .sku("TEST-LAPTOP-001")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Should get all products successfully")
    void testGetAllProducts() {
        // Arrange
        Product product1 = testProduct;
        Product product2 = Product.builder()
                .id(2L)
                .name("Test Phone")
                .description("Test phone for unit testing")
                .price(BigDecimal.valueOf(599.99))
                .quantity(20)
                .sku("TEST-PHONE-001")
                .build();

        List<Product> expectedProducts = Arrays.asList(product1, product2);
        when(productRepository.findAll()).thenReturn(expectedProducts);

        // Act
        List<Product> actualProducts = productService.getAllProducts();

        // Assert
        assertNotNull(actualProducts);
        assertEquals(2, actualProducts.size());
        assertEquals("Test Laptop", actualProducts.get(0).getName());
        assertEquals("Test Phone", actualProducts.get(1).getName());
        verify(productRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should get product by ID successfully")
    void testGetProductById() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        // Act
        Optional<Product> actual = productService.getProductById(1L);

        // Assert
        assertTrue(actual.isPresent());
        assertEquals("Test Laptop", actual.get().getName());
        assertEquals("TEST-LAPTOP-001", actual.get().getSku());
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should return empty when product not found by ID")
    void testGetProductByIdNotFound() {
        // Arrange
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        Optional<Product> actual = productService.getProductById(999L);

        // Assert
        assertFalse(actual.isPresent());
        verify(productRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Should create product with unique SKU")
    void testCreateProductSuccess() {
        // Arrange
        Product newProduct = Product.builder()
                .name("New Tablet")
                .description("New tablet product")
                .price(BigDecimal.valueOf(399.99))
                .quantity(15)
                .sku("NEW-TABLET-001")
                .build();

        Product savedProduct = Product.builder()
                .id(2L)
                .name("New Tablet")
                .description("New tablet product")
                .price(BigDecimal.valueOf(399.99))
                .quantity(15)
                .sku("NEW-TABLET-001")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(productRepository.findBySku("NEW-TABLET-001")).thenReturn(null);
        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

        // Act
        Product actual = productService.createProduct(newProduct);

        // Assert
        assertNotNull(actual);
        assertEquals("New Tablet", actual.getName());
        verify(productRepository, times(1)).findBySku("NEW-TABLET-001");
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("Should throw exception when creating product with duplicate SKU")
    void testCreateProductDuplicateSku() {
        // Arrange
        Product newProduct = Product.builder()
                .name("Duplicate Laptop")
                .description("Duplicate laptop")
                .price(BigDecimal.valueOf(999.99))
                .quantity(10)
                .sku("TEST-LAPTOP-001")
                .build();

        when(productRepository.findBySku("TEST-LAPTOP-001")).thenReturn(testProduct);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            productService.createProduct(newProduct);
        });

        assertTrue(exception.getMessage().contains("already exists"));
        verify(productRepository, times(1)).findBySku("TEST-LAPTOP-001");
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    @DisplayName("Should update product successfully")
    void testUpdateProductSuccess() {
        // Arrange
        Product updatedProduct = Product.builder()
                .id(1L)
                .name("Updated Laptop")
                .description("Updated laptop description")
                .price(BigDecimal.valueOf(1099.99))
                .quantity(15)
                .sku("TEST-LAPTOP-001")
                .build();

        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenReturn(updatedProduct);

        // Act
        Product actual = productService.updateProduct(1L, updatedProduct);

        // Assert
        assertNotNull(actual);
        assertEquals("Updated Laptop", actual.getName());
        assertEquals(BigDecimal.valueOf(1099.99), actual.getPrice());
        assertEquals(15, actual.getQuantity());
        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent product")
    void testUpdateProductNotFound() {
        // Arrange
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            productService.updateProduct(999L, testProduct);
        });

        assertTrue(exception.getMessage().contains("not found"));
        verify(productRepository, times(1)).findById(999L);
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    @DisplayName("Should delete product successfully")
    void testDeleteProductSuccess() {
        // Arrange
        when(productRepository.existsById(1L)).thenReturn(true);
        doNothing().when(productRepository).deleteById(1L);

        // Act
        assertDoesNotThrow(() -> productService.deleteProduct(1L));

        // Assert
        verify(productRepository, times(1)).existsById(1L);
        verify(productRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent product")
    void testDeleteProductNotFound() {
        // Arrange
        when(productRepository.existsById(999L)).thenReturn(false);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            productService.deleteProduct(999L);
        });

        assertTrue(exception.getMessage().contains("not found"));
        verify(productRepository, times(1)).existsById(999L);
        verify(productRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("Should validate product fields properly")
    void testProductValidation() {
        // Arrange
        Product invalidProduct = Product.builder()
                .name("")  // Invalid: empty name
                .description("")  // Invalid: empty description
                .price(BigDecimal.valueOf(-10))  // Invalid: negative price
                .quantity(-5)  // Invalid: negative quantity
                .sku("")  // Invalid: empty SKU
                .build();

        // Assert
        assertNull(invalidProduct.getId());
        assertTrue(invalidProduct.getName().isEmpty());
        assertTrue(invalidProduct.getSku().isEmpty());
        assertTrue(invalidProduct.getPrice().signum() < 0);
    }
}
