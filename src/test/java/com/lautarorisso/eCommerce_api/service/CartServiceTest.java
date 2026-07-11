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
import com.lautarorisso.eCommerce_api.exceptions.ResourceNotFoundException;
import com.lautarorisso.eCommerce_api.mapper.CartMapper;
import com.lautarorisso.eCommerce_api.model.CartEntity;
import com.lautarorisso.eCommerce_api.model.CartItemEntity;
import com.lautarorisso.eCommerce_api.model.ProductEntity;
import com.lautarorisso.eCommerce_api.model.UserEntity;
import com.lautarorisso.eCommerce_api.repository.CartRepository;
import com.lautarorisso.eCommerce_api.repository.ProductRepository;
import com.lautarorisso.eCommerce_api.security.SecurityUtils;
import com.lautarorisso.eCommerce_api.service.impl.CartServiceImpl;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

  @Mock
  private CartRepository cartRepository;

  @Mock
  private ProductRepository productRepository;

  @Mock
  private CartMapper cartMapper;

  @Mock
  private SecurityUtils securityUtils;

  @InjectMocks
  private CartServiceImpl cartService;

  private UserEntity user;
  private ProductEntity product;
  private CartDto cartDto;

  @BeforeEach
  void setUp() {
    user = new UserEntity("johndoe", "user@example.com", "pass",
        com.lautarorisso.eCommerce_api.enums.Role.USER);
    ReflectionTestUtils.setField(user, "id", 1L);

    product = new ProductEntity("Mouse", "Wireless mouse", BigDecimal.valueOf(29.99), 100);
    ReflectionTestUtils.setField(product, "id", 1L);

    cartDto = new CartDto(1L, java.util.List.of(), CartStatus.ACTIVE, BigDecimal.ZERO);
  }

  @Test
  @DisplayName("addProduct - should add item to cart")
  void addProduct_withValidData_addsItem() {
    var cart = new CartEntity(user);

    when(cartRepository.findById(1L)).thenReturn(Optional.of(cart));
    doNothing().when(securityUtils).assertOwnerOrAdmin(anyLong());
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
    doNothing().when(securityUtils).assertOwnerOrAdmin(anyLong());
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
    doNothing().when(securityUtils).assertOwnerOrAdmin(anyLong());

    assertThrows(InvalidOperationException.class, () -> cartService.addProduct(1L, 1L, 2));
    verify(cartRepository, never()).save(any());
  }

  @Test
  @DisplayName("removeProduct - should remove item from cart")
  void removeProduct_withValidData_removesItem() {
    var cart = new CartEntity(user);
    var item = new CartItemEntity(cart, product, 2, product.getUnitPrice());
    cart.addItem(item);

    when(cartRepository.findById(1L)).thenReturn(Optional.of(cart));
    doNothing().when(securityUtils).assertOwnerOrAdmin(anyLong());
    when(cartMapper.toDto(cart)).thenReturn(cartDto);

    var result = cartService.removeProduct(1L, 1L);

    assertNotNull(result);
    assertEquals(0, cart.getItems().size());
    verify(cartRepository).save(cart);
  }

  @Test
  @DisplayName("removeProduct - should throw ResourceNotFoundException when item not in cart")
  void removeProduct_withNonExistentItem_throwsException() {
    var cart = new CartEntity(user);

    when(cartRepository.findById(1L)).thenReturn(Optional.of(cart));
    doNothing().when(securityUtils).assertOwnerOrAdmin(anyLong());

    assertThrows(ResourceNotFoundException.class, () -> cartService.removeProduct(1L, 999L));
    verify(cartRepository, never()).save(any());
  }

  @Test
  @DisplayName("updateQuantity - should throw InsufficientResourcesException when stock is insufficient")
  void updateQuantity_withInsufficientStock_throwsException() {
    var lowStockProduct = new ProductEntity("Mouse", "Wireless mouse", BigDecimal.valueOf(29.99), 5);
    ReflectionTestUtils.setField(lowStockProduct, "id", 1L);
    var cart = new CartEntity(user);
    var item = new CartItemEntity(cart, lowStockProduct, 3, lowStockProduct.getUnitPrice());
    cart.addItem(item);

    when(cartRepository.findById(1L)).thenReturn(Optional.of(cart));
    doNothing().when(securityUtils).assertOwnerOrAdmin(anyLong());

    assertThrows(InsufficientResourcesException.class, () -> cartService.updateQuantity(1L, 1L, 10));
    verify(cartRepository, never()).save(any());
  }

  @Test
  @DisplayName("clearCart - should clear all items from cart")
  void clearCart_withValidData_clearsItems() {
    var cart = new CartEntity(user);
    var item = new CartItemEntity(cart, product, 2, product.getUnitPrice());
    cart.addItem(item);

    when(cartRepository.findById(1L)).thenReturn(Optional.of(cart));
    doNothing().when(securityUtils).assertOwnerOrAdmin(anyLong());

    cartService.clearCart(1L);

    assertEquals(0, cart.getItems().size());
    verify(cartRepository).save(cart);
  }
}
