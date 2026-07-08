package com.lautarorisso.eCommerce_api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.lautarorisso.eCommerce_api.dto.response.CartDto;
import com.lautarorisso.eCommerce_api.service.CartService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/carts")
@RequiredArgsConstructor
public class CartController {

  private final CartService cartService;

  @GetMapping("/user/{userId}")
  public CartDto getCartByUserId(@PathVariable Long userId) {
    return cartService.getCartByUserId(userId);
  }

  @PostMapping("/{cartId}/items")
  @ResponseStatus(HttpStatus.CREATED)
  public CartDto addProduct(@PathVariable Long cartId, @RequestParam Long productId, @RequestParam int quantity) {
    return cartService.addProduct(cartId, productId, quantity);
  }

  @DeleteMapping("/{cartId}/items/{productId}")
  public CartDto removeProduct(@PathVariable Long cartId, @PathVariable Long productId) {
    return cartService.removeProduct(cartId, productId);
  }

  @PatchMapping("/{cartId}/items/{productId}")
  public CartDto updateQuantity(@PathVariable Long cartId, @PathVariable Long productId, @RequestParam int quantity) {
    return cartService.updateQuantity(cartId, productId, quantity);
  }

  @DeleteMapping("/{cartId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void clearCart(@PathVariable Long cartId) {
    cartService.clearCart(cartId);
  }

}
