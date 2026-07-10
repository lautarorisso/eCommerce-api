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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.lautarorisso.eCommerce_api.config.TestSecurityConfig;
import com.lautarorisso.eCommerce_api.dto.response.OrderDto;
import com.lautarorisso.eCommerce_api.enums.OrderStatus;
import com.lautarorisso.eCommerce_api.exceptions.ResourceNotFoundException;
import com.lautarorisso.eCommerce_api.security.CustomUserDetailsService;
import com.lautarorisso.eCommerce_api.security.JwtService;
import com.lautarorisso.eCommerce_api.service.OrderService;

@WebMvcTest(OrderController.class)
@Import(TestSecurityConfig.class)
class OrderControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private OrderService orderService;

  @MockitoBean
  private JwtService jwtService;

  @MockitoBean
  private CustomUserDetailsService userDetailsService;

  private final OrderDto order = new OrderDto(1L, java.util.List.of(), BigDecimal.valueOf(59.98), OrderStatus.PENDING);

  @Test
  @DisplayName("POST /api/orders - should create order and return 201")
  void createOrder_returns201() throws Exception {
    when(orderService.createOrder(1L)).thenReturn(order);

    mockMvc.perform(post("/api/orders")
            .param("cartId", "1"))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(1));
  }

  @Test
  @DisplayName("GET /api/orders/user/{userId} - should return paginated orders")
  void getAllOrders_returns200() throws Exception {
    var page = new PageImpl<>(java.util.List.of(order), PageRequest.of(0, 20), 1);
    when(orderService.getAllOrders(any(), any(), any(), any(), any())).thenReturn(page);

    mockMvc.perform(get("/api/orders/user/{userId}", 1L))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].id").value(1));
  }

  @Test
  @DisplayName("GET /api/orders/{orderId} - should return order")
  void getOrderById_whenExists_returns200() throws Exception {
    when(orderService.getOrderById(1L)).thenReturn(order);

    mockMvc.perform(get("/api/orders/{orderId}", 1L))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("PENDING"));
  }

  @Test
  @DisplayName("GET /api/orders/{orderId} - should return 404 when not found")
  void getOrderById_whenNotExists_returns404() throws Exception {
    when(orderService.getOrderById(99L)).thenThrow(new ResourceNotFoundException("Order", 99L));

    mockMvc.perform(get("/api/orders/{orderId}", 99L))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("PATCH /api/orders/{orderId}/cancel - should return 204")
  void cancelOrder_returns204() throws Exception {
    doNothing().when(orderService).cancelOrder(1L);

    mockMvc.perform(patch("/api/orders/{orderId}/cancel", 1L))
        .andExpect(status().isNoContent());
  }

  @Test
  @DisplayName("PATCH /api/orders/{orderId}/pay - should return 204")
  void payOrder_returns204() throws Exception {
    doNothing().when(orderService).payOrder(1L);

    mockMvc.perform(patch("/api/orders/{orderId}/pay", 1L))
        .andExpect(status().isNoContent());
  }

  @Test
  @DisplayName("PATCH /api/orders/{orderId}/ship - should return 204")
  void shipOrder_returns204() throws Exception {
    doNothing().when(orderService).shipOrder(1L);

    mockMvc.perform(patch("/api/orders/{orderId}/ship", 1L))
        .andExpect(status().isNoContent());
  }

  @Test
  @DisplayName("PATCH /api/orders/{orderId}/deliver - should return 204")
  void deliverOrder_returns204() throws Exception {
    doNothing().when(orderService).deliverOrder(1L);

    mockMvc.perform(patch("/api/orders/{orderId}/deliver", 1L))
        .andExpect(status().isNoContent());
  }
}
