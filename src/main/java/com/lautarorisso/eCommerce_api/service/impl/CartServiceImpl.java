package com.lautarorisso.eCommerce_api.service.impl;

import java.util.Optional;

import com.lautarorisso.eCommerce_api.exceptions.InsufficientResourcesException;
import com.lautarorisso.eCommerce_api.security.CurrentUser;
import com.lautarorisso.eCommerce_api.security.SecurityUtils;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lautarorisso.eCommerce_api.dto.response.CartDto;
import com.lautarorisso.eCommerce_api.exceptions.InvalidOperationException;
import com.lautarorisso.eCommerce_api.exceptions.ResourceNotFoundException;
import com.lautarorisso.eCommerce_api.mapper.CartMapper;
import com.lautarorisso.eCommerce_api.model.CartEntity;
import com.lautarorisso.eCommerce_api.model.CartItemEntity;
import com.lautarorisso.eCommerce_api.model.ProductEntity;
import com.lautarorisso.eCommerce_api.model.UserEntity;
import com.lautarorisso.eCommerce_api.service.CartService;

import com.lautarorisso.eCommerce_api.repository.ProductRepository;
import com.lautarorisso.eCommerce_api.repository.CartRepository;
import com.lautarorisso.eCommerce_api.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

  private final CartRepository cartRepository;
  private final ProductRepository productRepository;
  private final UserRepository userRepository;
  private final CartMapper cartMapper;
  private final SecurityUtils securityUtils;

  @Override
  public CartDto getCartByUserId(Long userId) {
    CurrentUser currentUser = securityUtils.getCurrentUser();
    if (!currentUser.id().equals(userId) && !securityUtils.isAdmin()) {
      throw new AccessDeniedException("Access denied");
    }
    CartEntity cart = cartRepository.findByUserId(userId)
        .orElseThrow(() -> new ResourceNotFoundException("Cart", "userId", userId.toString()));
    return cartMapper.toDto(cart);
  }

  @Transactional
  @Override
  public CartDto addProduct(Long cartId, Long productId, int quantity) {
    CartEntity cart = cartRepository.findById(cartId).orElseThrow(() -> new ResourceNotFoundException("Cart", cartId));
    CurrentUser currentUser = securityUtils.getCurrentUser();
    if (!cart.getUser().getId().equals(currentUser.id()) && !securityUtils.isAdmin()) {
      throw new AccessDeniedException("Access denied");
    }
    if (!cart.isActive()) {
      throw new InvalidOperationException("Cart is not active");
    }
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
    CartEntity cart = cartRepository.findById(cartId).orElseThrow(() -> new ResourceNotFoundException("Cart", cartId));
    CurrentUser currentUser = securityUtils.getCurrentUser();
    if (!cart.getUser().getId().equals(currentUser.id()) && !securityUtils.isAdmin()) {
      throw new AccessDeniedException("Access denied");
    }
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

  @Transactional
  @Override
  public CartDto updateQuantity(Long cartId, Long productId, int quantity) {
    CartEntity cart = cartRepository.findById(cartId).orElseThrow(() -> new ResourceNotFoundException("Cart", cartId));
    CurrentUser currentUser = securityUtils.getCurrentUser();
    if (!cart.getUser().getId().equals(currentUser.id()) && !securityUtils.isAdmin()) {
      throw new AccessDeniedException("Access denied");
    }
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

  @Transactional
  protected CartEntity getOrCreateCartForCurrentUser() {
    Long userId = securityUtils.getCurrentUserId();
    return cartRepository.findByUserId(userId)
        .orElseGet(() -> {
          UserEntity user = userRepository.findById(userId)
              .orElseThrow(() -> new ResourceNotFoundException("User", userId));
          return cartRepository.save(new CartEntity(user));
        });
  }

  @Override
  public CartDto getMyCart() {
    CartEntity cart = getOrCreateCartForCurrentUser();
    return cartMapper.toDto(cart);
  }

  @Transactional
  @Override
  public CartDto addProductToMyCart(Long productId, int quantity) {
    CartEntity cart = getOrCreateCartForCurrentUser();
    if (!cart.isActive()) {
      throw new InvalidOperationException("Cart is not active");
    }
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
      cart.addItem(new CartItemEntity(cart, product, quantity, product.getUnitPrice()));
    }
    cartRepository.save(cart);
    return cartMapper.toDto(cart);
  }

  @Transactional
  @Override
  public CartDto removeProductFromMyCart(Long productId) {
    CartEntity cart = getOrCreateCartForCurrentUser();
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

  @Transactional
  @Override
  public CartDto updateQuantityInMyCart(Long productId, int quantity) {
    CartEntity cart = getOrCreateCartForCurrentUser();
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

  @Transactional
  @Override
  public void clearMyCart() {
    CartEntity cart = getOrCreateCartForCurrentUser();
    if (!cart.isActive()) {
      throw new InvalidOperationException("Cart is not active");
    }
    cart.clear();
    cartRepository.save(cart);
  }

  @Transactional
  @Override
  public void clearCart(Long cartId) {
    CartEntity cart = cartRepository.findById(cartId).orElseThrow(() -> new ResourceNotFoundException("Cart", cartId));
    CurrentUser currentUser = securityUtils.getCurrentUser();
    if (!cart.getUser().getId().equals(currentUser.id()) && !securityUtils.isAdmin()) {
      throw new AccessDeniedException("Access denied");
    }
    if (!cart.isActive()) {
      throw new InvalidOperationException("Cart is not active");
    }
    cart.clear();
    cartRepository.save(cart);
  }

}
