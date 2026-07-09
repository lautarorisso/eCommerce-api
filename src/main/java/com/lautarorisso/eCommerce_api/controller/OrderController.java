package com.lautarorisso.eCommerce_api.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.lautarorisso.eCommerce_api.dto.response.OrderDto;
import com.lautarorisso.eCommerce_api.service.OrderService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

  private final OrderService orderService;

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public OrderDto createOrder(@RequestParam Long cartId) {
    return orderService.createOrder(cartId);
  }

  @GetMapping("/user/{userId}")
  public Page<OrderDto> getAllOrders(@PathVariable Long userId, @PageableDefault(size = 20) Pageable pageable) {
    return orderService.getAllOrders(userId, pageable);
  }

  @GetMapping("/{orderId}")
  public OrderDto getOrderById(@PathVariable Long orderId) {
    return orderService.getOrderById(orderId);
  }

  @PatchMapping("/{orderId}/cancel")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void cancelOrder(@PathVariable Long orderId) {
    orderService.cancelOrder(orderId);
  }

  @PatchMapping("/{orderId}/pay")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void payOrder(@PathVariable Long orderId) {
    orderService.payOrder(orderId);
  }

  @PatchMapping("/{orderId}/ship")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void shipOrder(@PathVariable Long orderId) {
    orderService.shipOrder(orderId);
  }

  @PatchMapping("/{orderId}/deliver")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deliverOrder(@PathVariable Long orderId) {
    orderService.deliverOrder(orderId);
  }
}
