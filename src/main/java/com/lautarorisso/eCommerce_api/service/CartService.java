package com.lautarorisso.eCommerce_api.service;

import java.math.BigDecimal;

import com.lautarorisso.eCommerce_api.model.CartEntity;

public interface CartService {
  CartDto getCartByUserId(Long userId);

  void addProduct(Long cartId, Long productId, int quantity);

  void removeProduct(Long cartId, Long productId);

  void updateQuantity(Long cartId, Long productId, int quantity);

  void clearCart(Long cartId);

  BigDecimal calculateCartTotal(Long userId);
}
