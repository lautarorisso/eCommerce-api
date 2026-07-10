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

import com.lautarorisso.eCommerce_api.dto.response.CartDto;
import com.lautarorisso.eCommerce_api.enums.CartStatus;
import com.lautarorisso.eCommerce_api.exceptions.InsufficientResourcesException;
import com.lautarorisso.eCommerce_api.exceptions.InvalidOperationException;
import com.lautarorisso.eCommerce_api.mapper.CartMapper;
import com.lautarorisso.eCommerce_api.model.CartEntity;
import com.lautarorisso.eCommerce_api.model.CartItemEntity;
import com.lautarorisso.eCommerce_api.model.ProductEntity;
import com.lautarorisso.eCommerce_api.model.UserEntity;
import com.lautarorisso.eCommerce_api.repository.CartRepository;
import com.lautarorisso.eCommerce_api.repository.ProductRepository;
import com.lautarorisso.eCommerce_api.repository.UserRepository;
import com.lautarorisso.eCommerce_api.security.CurrentUser;
import com.lautarorisso.eCommerce_api.security.SecurityUtils;
import com.lautarorisso.eCommerce_api.service.impl.CartServiceImpl;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

  @Mock
  private CartRepository cartRepository;

  @Mock
  private ProductRepository productRepository;

  @Mock
  private UserRepository userRepository;

  @Mock
  private CartMapper cartMapper;

  @Mock
  private SecurityUtils securityUtils;

  @InjectMocks
  private CartServiceImpl cartService;

  private UserEntity user;
  private ProductEntity product;
  private CurrentUser currentUser;
  private CartDto cartDto;

  @BeforeEach
  void setUp() {
    user = new UserEntity("johndoe", "user@example.com", "pass",
        com.lautarorisso.eCommerce_api.enums.Role.USER);
    ReflectionTestUtils.setField(user, "id", 1L);

    product = new ProductEntity("Mouse", "Wireless mouse", BigDecimal.valueOf(29.99), 100);
    ReflectionTestUtils.setField(product, "id", 1L);

    currentUser = new CurrentUser(1L, "user@example.com", "USER");

    cartDto = new CartDto(1L, java.util.List.of(), CartStatus.ACTIVE, BigDecimal.ZERO);
  }

  @Test
  @DisplayName("addProduct - should add item to cart")
  void addProduct_withValidData_addsItem() {
    var cart = new CartEntity(user);

    when(cartRepository.findById(1L)).thenReturn(Optional.of(cart));
    when(securityUtils.getCurrentUser()).thenReturn(currentUser);
    when(productRepository.findById(1L)).thenReturn(Optional.of(product));
    when(cartMapper.toDto(cart)).thenReturn(cartDto);

    var result = cartService.addProduct(1L, 1L, 2);

    assertNotNull(result);
    assertEquals(1, cart.getItems().size());
    verify(cartRepository).save(cart);
  }

  @Test
  @DisplayName("addProduct - should throw InsufficientResourcesException when stock is insufficient")
  void addProduct_withInsufficientStock_throwsException() {
    var cart = new CartEntity(user);

    when(cartRepository.findById(1L)).thenReturn(Optional.of(cart));
    when(securityUtils.getCurrentUser()).thenReturn(currentUser);
    when(productRepository.findById(1L)).thenReturn(Optional.of(product));

    assertThrows(InsufficientResourcesException.class, () -> cartService.addProduct(1L, 1L, 200));
    verify(cartRepository, never()).save(any());
  }

  @Test
  @DisplayName("addProduct - should throw InvalidOperationException when cart is not active")
  void addProduct_withInactiveCart_throwsException() {
    var cart = new CartEntity(user);
    cart.abandon();

    when(cartRepository.findById(1L)).thenReturn(Optional.of(cart));
    when(securityUtils.getCurrentUser()).thenReturn(currentUser);

    assertThrows(InvalidOperationException.class, () -> cartService.addProduct(1L, 1L, 2));
    verify(cartRepository, never()).save(any());
  }

  @Test
  @DisplayName("getMyCart - should create cart when none exists for current user")
  void getMyCart_whenNoCart_createsAndReturns() {
    var cart = new CartEntity(user);

    when(securityUtils.getCurrentUserId()).thenReturn(1L);
    when(cartRepository.findByUserId(1L)).thenReturn(Optional.empty());
    when(userRepository.findById(1L)).thenReturn(Optional.of(user));
    when(cartRepository.save(any())).thenReturn(cart);
    when(cartMapper.toDto(cart)).thenReturn(cartDto);

    var result = cartService.getMyCart();

    assertNotNull(result);
    verify(cartRepository).findByUserId(1L);
    verify(userRepository).findById(1L);
    verify(cartRepository).save(any());
  }

  @Test
  @DisplayName("clearMyCart - should clear all items from current user's cart")
  void clearMyCart_withValidData_clearsItems() {
    var cart = new CartEntity(user);
    var item = new CartItemEntity(cart, product, 2, product.getUnitPrice());
    cart.addItem(item);

    when(securityUtils.getCurrentUserId()).thenReturn(1L);
    when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));

    cartService.clearMyCart();

    assertEquals(0, cart.getItems().size());
    verify(cartRepository).save(cart);
  }
}
