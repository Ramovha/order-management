package com.ordermgmt.order.service;

import com.ordermgmt.order.dto.ProductResponse;
import com.ordermgmt.order.model.Order;
import com.ordermgmt.order.model.OrderItem;
import com.ordermgmt.order.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("OrderService Unit Tests")
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private OrderService orderService;

    private Order testOrder;
    private OrderItem testOrderItem;
    private ProductResponse testProductResponse;

    @BeforeEach
    void setUp() {
        // Inject productServiceUrl via reflection
        ReflectionTestUtils.setField(orderService, "productServiceUrl", "http://localhost:9081/api/v1");

        testProductResponse = ProductResponse.builder()
                .id(1L)
                .name("Test Laptop")
                .description("Test laptop")
                .price(BigDecimal.valueOf(999.99))
                .quantity(10)
                .sku("TEST-LAPTOP-001")
                .build();

        testOrderItem = OrderItem.builder()
                .id(1L)
                .productId(1L)
                .quantity(1)
                .unitPrice(BigDecimal.valueOf(999.99))
                .build();
        testOrderItem.calculateTotalPrice();

        testOrder = Order.builder()
                .id(1L)
                .customerName("John Doe")
                .customerEmail("john@example.com")
                .shippingAddress("123 Main Street, New York, NY")
                .items(new ArrayList<>(List.of(testOrderItem)))
                .status(Order.OrderStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        testOrder.calculateTotalPrice();
    }

    @Test
    @DisplayName("Should get all orders successfully")
    void testGetAllOrders() {
        // Arrange
        List<Order> orders = List.of(testOrder);
        when(orderRepository.findAll()).thenReturn(orders);

        // Act
        List<Order> actual = orderService.getAllOrders();

        // Assert
        assertNotNull(actual);
        assertEquals(1, actual.size());
        assertEquals("John Doe", actual.get(0).getCustomerName());
        verify(orderRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should get order by ID successfully")
    void testGetOrderById() {
        // Arrange
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));

        // Act
        Optional<Order> actual = orderService.getOrderById(1L);

        // Assert
        assertTrue(actual.isPresent());
        assertEquals("John Doe", actual.get().getCustomerName());
        assertEquals("john@example.com", actual.get().getCustomerEmail());
        verify(orderRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should return empty when order not found by ID")
    void testGetOrderByIdNotFound() {
        // Arrange
        when(orderRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        Optional<Order> actual = orderService.getOrderById(999L);

        // Assert
        assertFalse(actual.isPresent());
        verify(orderRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Should throw exception when order has no items")
    void testCreateOrderWithNoItems() {
        // Arrange
        Order orderWithNoItems = Order.builder()
                .customerName("Jane Doe")
                .customerEmail("jane@example.com")
                .shippingAddress("456 Oak Avenue, Boston, MA")
                .items(new ArrayList<>())
                .build();

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            orderService.createOrder(orderWithNoItems);
        });

        assertEquals("Order must contain at least one item", exception.getMessage());
        verify(restTemplate, never()).exchange(anyString(), any(), any(), eq(ProductResponse.class));
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("Should update order successfully")
    void testUpdateOrderSuccess() {
        // Arrange
        Order updatedOrder = Order.builder()
                .id(1L)
                .customerName("Jane Doe")
                .customerEmail("jane@example.com")
                .shippingAddress("456 Oak Avenue, Boston, MA")
                .status(Order.OrderStatus.CONFIRMED)
                .build();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(updatedOrder);

        // Act
        Order actual = orderService.updateOrder(1L, updatedOrder);

        // Assert
        assertNotNull(actual);
        assertEquals("Jane Doe", actual.getCustomerName());
        assertEquals(Order.OrderStatus.CONFIRMED, actual.getStatus());
        verify(orderRepository, times(1)).findById(1L);
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent order")
    void testUpdateOrderNotFound() {
        // Arrange
        when(orderRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orderService.updateOrder(999L, testOrder);
        });

        assertTrue(exception.getMessage().contains("not found"));
        verify(orderRepository, times(1)).findById(999L);
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("Should delete order successfully")
    void testDeleteOrderSuccess() {
        // Arrange
        when(orderRepository.existsById(1L)).thenReturn(true);
        doNothing().when(orderRepository).deleteById(1L);

        // Act
        assertDoesNotThrow(() -> orderService.deleteOrder(1L));

        // Assert
        verify(orderRepository, times(1)).existsById(1L);
        verify(orderRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent order")
    void testDeleteOrderNotFound() {
        // Arrange
        when(orderRepository.existsById(999L)).thenReturn(false);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orderService.deleteOrder(999L);
        });

        assertTrue(exception.getMessage().contains("not found"));
        verify(orderRepository, times(1)).existsById(999L);
        verify(orderRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("Should get orders by customer email")
    void testGetOrdersByCustomerEmail() {
        // Arrange
        List<Order> orders = List.of(testOrder);
        when(orderRepository.findByCustomerEmail("john@example.com")).thenReturn(orders);

        // Act
        List<Order> actual = orderService.getOrdersByCustomerEmail("john@example.com");

        // Assert
        assertNotNull(actual);
        assertEquals(1, actual.size());
        assertEquals("john@example.com", actual.get(0).getCustomerEmail());
        verify(orderRepository, times(1)).findByCustomerEmail("john@example.com");
    }

    @Test
    @DisplayName("Should get orders by status")
    void testGetOrdersByStatus() {
        // Arrange
        List<Order> orders = List.of(testOrder);
        when(orderRepository.findByStatus(Order.OrderStatus.PENDING)).thenReturn(orders);

        // Act
        List<Order> actual = orderService.getOrdersByStatus(Order.OrderStatus.PENDING);

        // Assert
        assertNotNull(actual);
        assertEquals(1, actual.size());
        assertEquals(Order.OrderStatus.PENDING, actual.get(0).getStatus());
        verify(orderRepository, times(1)).findByStatus(Order.OrderStatus.PENDING);
    }

    @Test
    @DisplayName("Should calculate order total price correctly")
    void testOrderTotalPriceCalculation() {
        // Arrange
        OrderItem item1 = OrderItem.builder()
                .productId(1L)
                .quantity(2)
                .unitPrice(BigDecimal.valueOf(100))
                .build();
        item1.calculateTotalPrice();

        OrderItem item2 = OrderItem.builder()
                .productId(2L)
                .quantity(3)
                .unitPrice(BigDecimal.valueOf(50))
                .build();
        item2.calculateTotalPrice();

        Order order = Order.builder()
                .customerName("Test Customer")
                .customerEmail("test@example.com")
                .shippingAddress("Test Address")
                .items(new ArrayList<>(List.of(item1, item2)))
                .build();

        // Act
        order.calculateTotalPrice();

        // Assert
        // item1: 2 * 100 = 200
        // item2: 3 * 50 = 150
        // total: 350
        assertEquals(BigDecimal.valueOf(350), order.getTotalPrice());
    }
}
