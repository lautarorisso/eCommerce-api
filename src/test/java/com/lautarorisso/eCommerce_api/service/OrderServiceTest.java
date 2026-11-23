package com.lautarorisso.eCommerce_api.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.lautarorisso.eCommerce_api.dto.response.OrderDto;
import com.lautarorisso.eCommerce_api.enums.OrderStatus;
import com.lautarorisso.eCommerce_api.exceptions.InsufficientResourcesException;
import com.lautarorisso.eCommerce_api.exceptions.InvalidOperationException;
import com.lautarorisso.eCommerce_api.mapper.OrderMapper;
import com.lautarorisso.eCommerce_api.model.CartEntity;
import com.lautarorisso.eCommerce_api.model.CartItemEntity;
import com.lautarorisso.eCommerce_api.model.OrderEntity;
import com.lautarorisso.eCommerce_api.model.ProductEntity;
import com.lautarorisso.eCommerce_api.model.UserEntity;
import com.lautarorisso.eCommerce_api.repository.CartRepository;
import com.lautarorisso.eCommerce_api.repository.OrderRepository;
import com.lautarorisso.eCommerce_api.repository.ProductRepository;
import com.lautarorisso.eCommerce_api.security.SecurityUtils;
import com.lautarorisso.eCommerce_api.service.impl.OrderServiceImpl;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

  @Mock
  private CartRepository cartRepository;

  @Mock
  private OrderRepository orderRepository;

  @Mock
  private ProductRepository productRepository;

  @Mock
  private OrderMapper orderMapper;

  @Mock
  private SecurityUtils securityUtils;

  @InjectMocks
  private OrderServiceImpl orderService;

  private UserEntity user;
  private ProductEntity product;
  private OrderDto orderDto;

  @BeforeEach
  void setUp() {
    user = new UserEntity("johndoe", "user@example.com", "pass",
        com.lautarorisso.eCommerce_api.enums.Role.USER);
    ReflectionTestUtils.setField(user, "id", 1L);

    product = new ProductEntity("Mouse", "Wireless mouse", BigDecimal.valueOf(29.99), 100);

    orderDto = new OrderDto(1L, java.util.List.of(), BigDecimal.valueOf(59.98), OrderStatus.PENDING, null, 1L);
  }

  @Test
  @DisplayName("createOrder - should create order from cart")
  void createOrder_withValidCart_createsOrder() {
    var cart = new CartEntity(user);
    var item = new CartItemEntity(cart, product, 2, product.getUnitPrice());
    cart.addItem(item);

    when(cartRepository.findById(1L)).thenReturn(Optional.of(cart));
    doNothing().when(securityUtils).assertOwnerOrAdmin(anyLong());
    when(orderRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
    when(orderMapper.toDto(any())).thenReturn(orderDto);

    var result = orderService.createOrder(1L);

    assertNotNull(result);
    assertEquals(0, cart.getItems().size());
    verify(orderRepository).save(any());
  }

  @Test
  @DisplayName("createOrder - should throw InvalidOperationException when cart is empty")
  void createOrder_withEmptyCart_throwsException() {
    var cart = new CartEntity(user);

    when(cartRepository.findById(1L)).thenReturn(Optional.of(cart));
    doNothing().when(securityUtils).assertOwnerOrAdmin(anyLong());

    assertThrows(InvalidOperationException.class, () -> orderService.createOrder(1L));
    verify(orderRepository, never()).save(any());
  }

  @Test
  @DisplayName("createOrder - should throw InvalidOperationException when product price changed after adding to cart")
  void createOrder_withPriceChangedAfterCart_throwsException() {
    var product = new ProductEntity("Mouse", "Wireless mouse", BigDecimal.valueOf(29.99), 10);
    var cart = new CartEntity(user);
    // Add item with current price (29.99)
    var item = new CartItemEntity(cart, product, 2, product.getUnitPrice());
    cart.addItem(item);

    // Simulate admin price change after item was added to cart
    product.updatePrice(BigDecimal.valueOf(39.99));

    when(cartRepository.findById(1L)).thenReturn(Optional.of(cart));
    doNothing().when(securityUtils).assertOwnerOrAdmin(anyLong());

    assertThrows(InvalidOperationException.class, () -> orderService.createOrder(1L));
    verify(orderRepository, never()).save(any());
  }

  @Test
  @DisplayName("createOrder - should throw InsufficientResourcesException when stock is insufficient")
  void createOrder_withInsufficientStock_throwsException() {
    var lowStockProduct = new ProductEntity("Mouse", "Wireless mouse", BigDecimal.valueOf(29.99), 1);
    var cart = new CartEntity(user);
    var item = new CartItemEntity(cart, lowStockProduct, 5, lowStockProduct.getUnitPrice());
    cart.addItem(item);

    when(cartRepository.findById(1L)).thenReturn(Optional.of(cart));
    doNothing().when(securityUtils).assertOwnerOrAdmin(anyLong());

    assertThrows(InsufficientResourcesException.class, () -> orderService.createOrder(1L));
    verify(orderRepository, never()).save(any());
  }

  @Test
  @DisplayName("payOrder - should mark order as paid (stock already decreased at order creation)")
  void payOrder_whenPending_marksAsPaid() {
    var order = new OrderEntity(user);
    order.addItem(new com.lautarorisso.eCommerce_api.model.OrderItemEntity(order, product, 2, BigDecimal.valueOf(29.99)));

    when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
    doNothing().when(securityUtils).assertOwnerOrAdmin(anyLong());

    orderService.payOrder(1L);

    assertEquals(OrderStatus.PAID, order.getStatus());
    assertNotNull(order.getPaidAt());
    verify(orderRepository).save(order);
  }

  @Test
  @DisplayName("shipOrder - should ship a paid order and record shipping details and shippedAt")
  void shipOrder_whenPaid_setsShippingDetails() {
    var order = paidOrder();

    when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
    doNothing().when(securityUtils).assertAdmin();

    orderService.shipOrder(1L, "Calle Falsa 123", "TRACK-001");

    assertEquals(OrderStatus.SHIPPED, order.getStatus());
    assertEquals("Calle Falsa 123", order.getShippingAddress());
    assertEquals("TRACK-001", order.getTrackingNumber());
    assertNotNull(order.getShippedAt());
    verify(orderRepository).save(order);
  }

  @Test
  @DisplayName("shipOrder - should reject shipping a pending order")
  void shipOrder_whenPending_throwsException() {
    var order = new OrderEntity(user);

    when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
    doNothing().when(securityUtils).assertAdmin();

    assertThrows(IllegalStateException.class,
        () -> orderService.shipOrder(1L, "Calle Falsa 123", "TRACK-001"));
    verify(orderRepository, never()).save(any());
  }

  @Test
  @DisplayName("shipOrder - should reject blank shipping address")
  void shipOrder_withBlankShippingAddress_throwsException() {
    var order = paidOrder();

    when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
    doNothing().when(securityUtils).assertAdmin();

    assertThrows(IllegalArgumentException.class,
        () -> orderService.shipOrder(1L, "  ", "TRACK-001"));
    verify(orderRepository, never()).save(any());
  }

  @Test
  @DisplayName("shipOrder - should reject blank tracking number")
  void shipOrder_withBlankTrackingNumber_throwsException() {
    var order = paidOrder();

    when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
    doNothing().when(securityUtils).assertAdmin();

    assertThrows(IllegalArgumentException.class,
        () -> orderService.shipOrder(1L, "Calle Falsa 123", " "));
    verify(orderRepository, never()).save(any());
  }

  @Test
  @DisplayName("deliverOrder - should deliver a shipped order")
  void deliverOrder_whenShipped_marksAsDelivered() {
    var order = paidOrder();
    order.markAsShipped("Calle Falsa 123", "TRACK-001");

    when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
    doNothing().when(securityUtils).assertAdmin();

    orderService.deliverOrder(1L);

    assertEquals(OrderStatus.DELIVERED, order.getStatus());
    verify(orderRepository).save(order);
  }

  @Test
  @DisplayName("deliverOrder - should reject delivering a paid but not shipped order")
  void deliverOrder_whenPaid_throwsException() {
    var order = paidOrder();

    when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
    doNothing().when(securityUtils).assertAdmin();

    assertThrows(IllegalStateException.class, () -> orderService.deliverOrder(1L));
    verify(orderRepository, never()).save(any());
  }

  private OrderEntity paidOrder() {
    var order = new OrderEntity(user);
    order.markAsPaid();
    return order;
  }

  @Test
  @DisplayName("cancelOrder - should cancel a pending order")
  void cancelOrder_whenPending_cancelsSuccessfully() {
    var order = new OrderEntity(user);

    when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
    doNothing().when(securityUtils).assertOwnerOrAdmin(anyLong());

    orderService.cancelOrder(1L);

    assertEquals(OrderStatus.CANCELLED, order.getStatus());
    verify(orderRepository).save(order);
  }

  @Test
  @DisplayName("getOrderById - should return order when found")
  void getOrderById_whenFound_returnsOrder() {
    var order = new OrderEntity(user);

    when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
    doNothing().when(securityUtils).assertOwnerOrAdmin(anyLong());
    when(orderMapper.toDto(order)).thenReturn(orderDto);

    var result = orderService.getOrderById(1L);

    assertNotNull(result);
    assertEquals(OrderStatus.PENDING, result.status());
  }

  @Test
  @DisplayName("createOrder - should decrease stock from products when creating order")
  void createOrder_withProducts_decreasesStock() {
    var product = new ProductEntity("Mouse", "Wireless mouse", BigDecimal.valueOf(29.99), 10);
    var cart = new CartEntity(user);
    var item = new CartItemEntity(cart, product, 2, product.getUnitPrice());
    cart.addItem(item);

    when(cartRepository.findById(1L)).thenReturn(Optional.of(cart));
    doNothing().when(securityUtils).assertOwnerOrAdmin(anyLong());
    when(orderRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
    when(productRepository.saveAll(any())).thenAnswer(inv -> inv.getArgument(0));
    when(orderMapper.toDto(any())).thenReturn(orderDto);

    orderService.createOrder(1L);

    assertEquals(8, product.getStock());
    verify(productRepository).saveAll(any());
  }

  @Test
  @DisplayName("cancelOrder - should restore stock when canceling a pending order")
  void cancelOrder_whenPending_restoresStock() {
    var product = new ProductEntity("Mouse", "Wireless mouse", BigDecimal.valueOf(29.99), 8);
    var order = new OrderEntity(user);
    order.addItem(new com.lautarorisso.eCommerce_api.model.OrderItemEntity(order, product, 2, BigDecimal.valueOf(29.99)));

    when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
    doNothing().when(securityUtils).assertOwnerOrAdmin(anyLong());
    when(orderRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
    when(productRepository.saveAll(any())).thenAnswer(inv -> inv.getArgument(0));

    orderService.cancelOrder(1L);

    assertEquals(OrderStatus.CANCELLED, order.getStatus());
    assertEquals(10, product.getStock());
  }
}
