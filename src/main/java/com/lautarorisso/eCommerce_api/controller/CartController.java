package com.lautarorisso.eCommerce_api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;

@Validated
@RestController
@RequestMapping("/api/carts")
@RequiredArgsConstructor
@Tag(name = "Cart", description = "Manage shopping carts")
public class CartController {

  private final CartService cartService;

  @GetMapping("/user/{userId}")
  @Operation(summary = "Get cart by user ID", description = "Returns the active cart for a given user")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Cart found"),
      @ApiResponse(responseCode = "404", description = "User or cart not found")
  })
  public CartDto getCartByUserId(@PathVariable Long userId) {
    return cartService.getCartByUserId(userId);
  }

  @PostMapping("/{cartId}/items")
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(summary = "Add product to cart", description = "Adds a product to the cart")
  @ApiResponses({
      @ApiResponse(responseCode = "201", description = "Product added to cart"),
      @ApiResponse(responseCode = "400", description = "Invalid quantity"),
      @ApiResponse(responseCode = "404", description = "Cart or product not found"),
      @ApiResponse(responseCode = "409", description = "Insufficient stock")
  })
  public CartDto addProduct(@PathVariable Long cartId, @RequestParam Long productId, @RequestParam @Positive int quantity) {
    return cartService.addProduct(cartId, productId, quantity);
  }

  @DeleteMapping("/{cartId}/items/{productId}")
  @Operation(summary = "Remove product from cart", description = "Removes a product from the cart")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Product removed from cart"),
      @ApiResponse(responseCode = "404", description = "Cart, product, or item not found")
  })
  public CartDto removeProduct(@PathVariable Long cartId, @PathVariable Long productId) {
    return cartService.removeProduct(cartId, productId);
  }

  @PatchMapping("/{cartId}/items/{productId}")
  @Operation(summary = "Update item quantity", description = "Updates the quantity of a product in the cart")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Quantity updated"),
      @ApiResponse(responseCode = "400", description = "Invalid quantity"),
      @ApiResponse(responseCode = "404", description = "Cart, product, or item not found")
  })
  public CartDto updateQuantity(@PathVariable Long cartId, @PathVariable Long productId, @RequestParam @Positive int quantity) {
    return cartService.updateQuantity(cartId, productId, quantity);
  }

  @DeleteMapping("/{cartId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(summary = "Clear cart", description = "Removes all items from the cart")
  @ApiResponses({
      @ApiResponse(responseCode = "204", description = "Cart cleared"),
      @ApiResponse(responseCode = "404", description = "Cart not found")
  })
  public void clearCart(@PathVariable Long cartId) {
    cartService.clearCart(cartId);
  }
}
