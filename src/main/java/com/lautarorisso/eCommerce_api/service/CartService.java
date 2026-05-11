package com.lautarorisso.eCommerce_api.service;

import com.lautarorisso.eCommerce_api.model.CartEntity;

public interface CartService {
    CartEntity getCartByUserId(Long userId);

    CartEntity addProduct(Long cartId, Long productId, int quantity);

    CartEntity removeProduct(Long cartId, Long productId);

    CartEntity updateQuantity(Long cartId, Long productId, int quantity);

    void clearCart(Long cartId);
}
