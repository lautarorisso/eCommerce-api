package com.lautarorisso.eCommerce_api.service;

import com.lautarorisso.eCommerce_api.dto.response.CartDto;

public interface CartService {
  CartDto getCartByUserId(Long userId);

  CartDto addProduct(Long cartId, Long productId, int quantity);

  CartDto removeProduct(Long cartId, Long productId);

  CartDto updateQuantity(Long cartId, Long productId, int quantity);

  void clearCart(Long cartId);

  CartDto getMyCart();

  CartDto addProductToMyCart(Long productId, int quantity);

  CartDto removeProductFromMyCart(Long productId);

  CartDto updateQuantityInMyCart(Long productId, int quantity);

  void clearMyCart();
}
