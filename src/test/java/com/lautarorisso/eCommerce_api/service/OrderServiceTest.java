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
import com.lautarorisso.eCommerce_api.security.CurrentUser;
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
  private CurrentUser currentUser;
  private OrderDto orderDto;

  @BeforeEach
  void setUp() {
    user = new UserEntity("johndoe", "user@example.com", "pass",
        com.lautarorisso.eCommerce_api.enums.Role.USER);
    ReflectionTestUtils.setField(user, "id", 1L);

    product = new ProductEntity("Mouse", "Wireless mouse", BigDecimal.valueOf(29.99), 100);

    currentUser = new CurrentUser(1L, "user@example.com", "USER");

    orderDto = new OrderDto(1L, java.util.List.of(), BigDecimal.valueOf(59.98), OrderStatus.PENDING, null, 1L);
  }

  @Test
  @DisplayName("createOrder - should create order from cart")
  void createOrder_withValidCart_createsOrder() {
    var cart = new CartEntity(user);
    var item = new CartItemEntity(cart, product, 2, product.getUnitPrice());
    cart.addItem(item);

    when(cartRepository.findById(1L)).thenReturn(Optional.of(cart));
    when(securityUtils.getCurrentUser()).thenReturn(currentUser);
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
    when(securityUtils.getCurrentUser()).thenReturn(currentUser);

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
    when(securityUtils.getCurrentUser()).thenReturn(currentUser);

    assertThrows(InsufficientResourcesException.class, () -> orderService.createOrder(1L));
    verify(orderRepository, never()).save(any());
  }

  @Test
  @DisplayName("payOrder - should mark order as paid and decrease stock")
  void payOrder_whenPending_marksAsPaid() {
    var order = new OrderEntity(user);
    order.addItem(new com.lautarorisso.eCommerce_api.model.OrderItemEntity(order, product, BigDecimal.valueOf(29.99), 2));

    when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
    when(securityUtils.getCurrentUser()).thenReturn(currentUser);

    orderService.payOrder(1L);

    assertEquals(OrderStatus.PAID, order.getStatus());
    verify(orderRepository).save(order);
    verify(productRepository).saveAll(any());
  }
}
