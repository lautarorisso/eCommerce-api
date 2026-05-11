package com.lautarorisso.eCommerce_api.service.impl;

import java.util.ArrayList;

import org.springframework.stereotype.Service;

import com.lautarorisso.eCommerce_api.model.CartEntity;
import com.lautarorisso.eCommerce_api.model.UserEntity;
import com.lautarorisso.eCommerce_api.model.ProductEntity;
import com.lautarorisso.eCommerce_api.repository.UserRepository;
import com.lautarorisso.eCommerce_api.service.CartService;
import com.lautarorisso.eCommerce_api.repository.ProductRepository;
import com.lautarorisso.eCommerce_api.repository.CartRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

  private final CartRepository cartRepository;
  private final UserRepository userRepository;
  private final ProductRepository productRepository;

  @Override
  public CartEntity getCartByUserId(Long userId) {
    return cartRepository.findByUserId(userId).orElseGet(() -> createCart(userId));
  }

  @Override
  public CartEntity addProduct(Long cartId, Long productId, int quantity) {
    CartEntity cart = cartRepository.findById(cartId).orElseThrow(() -> new RuntimeException("Cart not found"));
    ProductEntity product = productRepository.findById(productId)
        .orElseThrow(() -> new RuntimeException("Product not found"));
    Optional<CartItemEntity> existingItem = cart.getItems().stream()
        .filter(item -> item.getProduct().getId().equals(productId)).findFirst();
    if (existingItem.isPresent()) {
      CartItemEntity item = existingItem.get();
      item.setQuantity(item.getQuantity() + quantity);
    } else {
      CartItemEntity newItem = new CartItemEntity();
      newItem.setCart(cart);
      newItem.setProduct();
    }
  }

  private CartEntity createCart(Long userId) {
    UserEntity user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
    CartEntity cart = new CartEntity();
    cart.setUser(user);
    cart.setItems(new ArrayList<>());
    return cartRepository.save(cart);
  }
}
