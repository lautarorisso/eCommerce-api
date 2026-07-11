package com.lautarorisso.eCommerce_api.service.impl;

import java.util.Optional;

import com.lautarorisso.eCommerce_api.exceptions.InsufficientResourcesException;
import com.lautarorisso.eCommerce_api.security.SecurityUtils;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lautarorisso.eCommerce_api.dto.response.CartDto;
import com.lautarorisso.eCommerce_api.exceptions.InvalidOperationException;
import com.lautarorisso.eCommerce_api.exceptions.ResourceNotFoundException;
import com.lautarorisso.eCommerce_api.mapper.CartMapper;
import com.lautarorisso.eCommerce_api.model.CartEntity;
import com.lautarorisso.eCommerce_api.model.CartItemEntity;
import com.lautarorisso.eCommerce_api.model.ProductEntity;
import com.lautarorisso.eCommerce_api.service.CartService;

import com.lautarorisso.eCommerce_api.repository.ProductRepository;
import com.lautarorisso.eCommerce_api.repository.CartRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

  private final CartRepository cartRepository;
  private final ProductRepository productRepository;
  private final CartMapper cartMapper;
  private final SecurityUtils securityUtils;

  @Transactional(readOnly = true)
  @Override
  public CartDto getCartByUserId(Long userId) {
    securityUtils.assertOwnerOrAdmin(userId);
    CartEntity cart = cartRepository.findByUserId(userId)
        .orElseThrow(() -> new ResourceNotFoundException("Cart", "userId", userId.toString()));
    return cartMapper.toDto(cart);
  }

  @Transactional
  @Override
  public CartDto addProduct(Long cartId, Long productId, int quantity) {
    CartEntity cart = validateCartAccess(cartId);
    validateCartActive(cart);
    ProductEntity product = productRepository.findById(productId)
        .orElseThrow(() -> new ResourceNotFoundException("Product", productId));
    Optional<CartItemEntity> existingItem = cart.getItems().stream()
        .filter(item -> item.getProduct().getId().equals(productId)).findFirst();
    int totalQty = existingItem.map(CartItemEntity::getQuantity).orElse(0) + quantity;
    if (totalQty > product.getStock()) {
      throw new InsufficientResourcesException(product.getName(), totalQty, product.getStock());
    }
    if (existingItem.isPresent()) {
      existingItem.get().changeQuantity(totalQty);
    } else {
      CartItemEntity newItem = new CartItemEntity(cart, product, quantity, product.getUnitPrice());
      cart.addItem(newItem);
    }
    cartRepository.save(cart);
    return cartMapper.toDto(cart);
  }

  @Transactional
  @Override
  public CartDto removeProduct(Long cartId, Long productId) {
    CartEntity cart = validateCartAccess(cartId);
    validateCartActive(cart);
    CartItemEntity existingItem = cart.getItems().stream()
        .filter(item -> item.getProduct().getId().equals(productId)).findFirst()
        .orElseThrow(() -> new ResourceNotFoundException("CartItem", "productId", productId.toString()));
    cart.removeItem(existingItem);
    cartRepository.save(cart);
    return cartMapper.toDto(cart);
  }

  @Transactional
  @Override
  public CartDto updateQuantity(Long cartId, Long productId, int quantity) {
    CartEntity cart = validateCartAccess(cartId);
    validateCartActive(cart);
    CartItemEntity existingItem = cart.getItems().stream()
        .filter(item -> item.getProduct().getId().equals(productId)).findFirst()
        .orElseThrow(() -> new ResourceNotFoundException("CartItem", "productId", productId.toString()));
    existingItem.changeQuantity(quantity);
    cartRepository.save(cart);
    return cartMapper.toDto(cart);
  }

  @Transactional
  @Override
  public void clearCart(Long cartId) {
    CartEntity cart = validateCartAccess(cartId);
    validateCartActive(cart);
    cart.clear();
    cartRepository.save(cart);
  }

  private CartEntity validateCartAccess(Long cartId) {
    CartEntity cart = cartRepository.findById(cartId)
        .orElseThrow(() -> new ResourceNotFoundException("Cart", cartId));
    securityUtils.assertOwnerOrAdmin(cart.getUser().getId());
    return cart;
  }

  private void validateCartActive(CartEntity cart) {
    if (!cart.isActive()) {
      throw new InvalidOperationException("Cart is not active");
    }
  }

}
