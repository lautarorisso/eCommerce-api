package com.lautarorisso.eCommerce_api.service.impl;

import java.util.ArrayList;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.lautarorisso.eCommerce_api.dto.response.CartDto;
import com.lautarorisso.eCommerce_api.mapper.CartMapper;
import com.lautarorisso.eCommerce_api.model.CartEntity;
import com.lautarorisso.eCommerce_api.model.CartItemEntity;
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
  private final CartMapper CartMapper;

  @Override
  public CartDto getCartByUserId(Long userId) {
    CartEntity cart = cartRepository.findByUserId(userId).orElseGet(() -> createCart(userId));
    return CartMapper.toDto(cart);
  }

  private CartEntity createCart(Long userId) {
    UserEntity user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
    CartEntity cart = new CartEntity(user);
    return cartRepository.save(cart);
  }

  @Override
  public CartDto addProduct(Long cartId, Long productId, int quantity) {
    CartEntity cart = cartRepository.findById(cartId).orElseThrow(() -> new RuntimeException("Cart not found"));
    if (!cart.isActive()) {
      throw new RuntimeException("Cart is not Active");
    }
    ProductEntity product = productRepository.findById(productId)
        .orElseThrow(() -> new RuntimeException("Product not found"));
    Optional<CartItemEntity> existingItem = cart.getItems().stream()
        .filter(item -> item.getProduct().getId().equals(productId)).findFirst();
    CartItemEntity item = existingItem.orElse(null);
    if (item != null) {
      item.changeQuantity(item.getQuantity() + quantity);
    } else {
      CartItemEntity newItem = new CartItemEntity(cart, product, quantity, product.getPrice());
      cart.addItem(newItem);
    }
    cartRepository.save(cart);
    return CartMapper.toDto(cart);
  }

  @Override
  public CartDto removeProduct(Long cartId, Long productId) {
    CartEntity cart = cartRepository.findById(cartId).orElseThrow(() -> new RuntimeException("Cart not found"));
    if (!cart.isActive()) {
      throw new RuntimeException("Cart is not Active");
    }
    CartItemEntity existingItem = cart.getItems().stream()
        .filter(item -> item.getProduct().getId().equals(productId)).findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Product not found in cart"));
    cart.removeItem(existingItem);
    cartRepository.save(cart);
    return CartMapper.toDto(cart);
  }

  @Override
  public CartDto updateQuantity(Long cartId, Long productId, int quantity) {
    CartEntity cart = cartRepository.findById(cartId).orElseThrow(() -> new RuntimeException("Cart not found"));
    if (!cart.isActive()) {
      throw new RuntimeException("Cart is not Active");
    }
    CartItemEntity existingItem = cart.getItems().stream()
        .filter(item -> item.getProduct().getId().equals(productId)).findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Product not found in cart"));
    existingItem.changeQuantity(quantity);
    cartRepository.save(cart);
    return CartMapper.toDto(cart);
  }

  @Override
  public void clearCart(Long cartId) {
    CartEntity cart = cartRepository.findById(cartId).orElseThrow(() -> new RuntimeException("Cart not found"));
    if (!cart.isActive()) {
      throw new RuntimeException("Cart is not Active");
    }
    cart.clear();
    cartRepository.save(cart);
  }

  @Override
  public CartDto checkout(Long cartId) {
    CartEntity cart = cartRepository.findById(cartId).orElseThrow(() -> new RuntimeException("Cart not found"));
    if (cart.isEmpty()) {
      throw new RuntimeException("Cart is empty");
    }
    cart.checkout();
    cartRepository.save(cart);
    return CartMapper.toDto(cart);
  }
}
