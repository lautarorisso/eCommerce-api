package com.lautarorisso.eCommerce_api.service;

import com.lautarorisso.eCommerce_api.dto.response.cartDto;

public interface CartService {
  cartDto getCartByUserId(Long userId);

  cartDto addProduct(Long cartId, Long productId, int quantity);

  cartDto removeProduct(Long cartId, Long productId);

  cartDto updateQuantity(Long cartId, Long productId, int quantity);

  void clearCart(Long cartId);

  cartDto checkout(Long cartId);
}
