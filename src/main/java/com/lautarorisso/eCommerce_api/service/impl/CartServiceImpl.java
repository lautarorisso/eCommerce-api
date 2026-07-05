package com.lautarorisso.eCommerce_api.service.impl;

import java.util.ArrayList;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.lautarorisso.eCommerce_api.dto.response.CartDto;
import com.lautarorisso.eCommerce_api.exceptions.InvalidOperationException;
import com.lautarorisso.eCommerce_api.exceptions.ResourceNotFoundException;
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
  private final CartMapper cartMapper;

  @Override
  public CartDto getCartByUserId(Long userId) {
    CartEntity cart = cartRepository.findByUserId(userId).orElseGet(() -> createCart(userId));
    return cartMapper.toDto(cart);
  }

  private CartEntity createCart(Long userId) {
    UserEntity user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User", userId));
    CartEntity cart = new CartEntity(user);
    return cartRepository.save(cart);
  }

  @Override
  public CartDto addProduct(Long cartId, Long productId, int quantity) {
    CartEntity cart = cartRepository.findById(cartId).orElseThrow(() -> new ResourceNotFoundException("Cart", cartId));
    if (!cart.isActive()) {
      throw new InvalidOperationException("Cart is not active");
    }
    ProductEntity product = productRepository.findById(productId)
        .orElseThrow(() -> new ResourceNotFoundException("Product", productId));
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
    return cartMapper.toDto(cart);
  }

  @Override
  public CartDto removeProduct(Long cartId, Long productId) {
    CartEntity cart = cartRepository.findById(cartId).orElseThrow(() -> new ResourceNotFoundException("Cart", cartId));
    if (!cart.isActive()) {
      throw new InvalidOperationException("Cart is not active");
    }
    CartItemEntity existingItem = cart.getItems().stream()
        .filter(item -> item.getProduct().getId().equals(productId)).findFirst()
        .orElseThrow(() -> new ResourceNotFoundException("CartItem", "productId", productId.toString()));
    cart.removeItem(existingItem);
    cartRepository.save(cart);
    return cartMapper.toDto(cart);
  }

  @Override
  public CartDto updateQuantity(Long cartId, Long productId, int quantity) {
    CartEntity cart = cartRepository.findById(cartId).orElseThrow(() -> new ResourceNotFoundException("Cart", cartId));
    if (!cart.isActive()) {
      throw new InvalidOperationException("Cart is not active");
    }
    CartItemEntity existingItem = cart.getItems().stream()
        .filter(item -> item.getProduct().getId().equals(productId)).findFirst()
        .orElseThrow(() -> new ResourceNotFoundException("CartItem", "productId", productId.toString()));
    existingItem.changeQuantity(quantity);
    cartRepository.save(cart);
    return cartMapper.toDto(cart);
  }

  @Override
  public void clearCart(Long cartId) {
    CartEntity cart = cartRepository.findById(cartId).orElseThrow(() -> new ResourceNotFoundException("Cart", cartId));
    if (!cart.isActive()) {
      throw new InvalidOperationException("Cart is not active");
    }
    cart.clear();
    cartRepository.save(cart);
  }

  @Override
  public CartDto checkout(Long cartId) {
    CartEntity cart = cartRepository.findById(cartId).orElseThrow(() -> new ResourceNotFoundException("Cart", cartId));
    if (cart.isEmpty()) {
      throw new InvalidOperationException("Cart is empty");
    }
    cart.checkout();
    cartRepository.save(cart);
    return cartMapper.toDto(cart);
  }
}
