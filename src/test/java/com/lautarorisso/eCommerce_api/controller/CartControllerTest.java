package com.lautarorisso.eCommerce_api.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.lautarorisso.eCommerce_api.config.TestSecurityConfig;
import com.lautarorisso.eCommerce_api.dto.response.CartDto;
import com.lautarorisso.eCommerce_api.dto.response.CartItemDto;
import com.lautarorisso.eCommerce_api.enums.CartStatus;
import com.lautarorisso.eCommerce_api.security.CustomUserDetailsService;
import com.lautarorisso.eCommerce_api.security.JwtService;
import com.lautarorisso.eCommerce_api.service.CartService;

@WebMvcTest(CartController.class)
@Import(TestSecurityConfig.class)
class CartControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private CartService cartService;

  @MockitoBean
  private JwtService jwtService;

  @MockitoBean
  private CustomUserDetailsService userDetailsService;

  private final CartDto cart = new CartDto(1L, java.util.List.of(), CartStatus.ACTIVE, BigDecimal.ZERO);

  @Test
  @DisplayName("GET /api/carts/user/{userId} - should return cart")
  void getCartByUserId_returns200() throws Exception {
    when(cartService.getCartByUserId(1L)).thenReturn(cart);

    mockMvc.perform(get("/api/carts/user/{userId}", 1L))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1));
  }

  @Test
  @DisplayName("POST /api/carts/{cartId}/items - should add item and return 201")
  void addProduct_returns201() throws Exception {
    when(cartService.addProduct(any(), any(), anyInt())).thenReturn(cart);

    mockMvc.perform(post("/api/carts/{cartId}/items", 1L)
            .param("productId", "1")
            .param("quantity", "2"))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(1));
  }

  @Test
  @DisplayName("POST /api/carts/{cartId}/items - should return 400 when quantity is invalid")
  void addProduct_withInvalidQuantity_returns400() throws Exception {
    mockMvc.perform(post("/api/carts/{cartId}/items", 1L)
            .param("productId", "1")
            .param("quantity", "0"))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("DELETE /api/carts/{cartId}/items/{productId} - should remove item")
  void removeProduct_returns200() throws Exception {
    when(cartService.removeProduct(1L, 1L)).thenReturn(cart);

    mockMvc.perform(delete("/api/carts/{cartId}/items/{productId}", 1L, 1L))
        .andExpect(status().isOk());
  }

  @Test
  @DisplayName("DELETE /api/carts/{cartId} - should clear cart and return 204")
  void clearCart_returns204() throws Exception {
    doNothing().when(cartService).clearCart(1L);

    mockMvc.perform(delete("/api/carts/{cartId}", 1L))
        .andExpect(status().isNoContent());
  }
}
