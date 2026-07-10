package com.lautarorisso.eCommerce_api.controller;

import java.math.BigDecimal;

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
import com.lautarorisso.eCommerce_api.enums.OrderStatus;
import com.lautarorisso.eCommerce_api.service.OrderService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "Orders", description = "Manage orders")
public class OrderController {

  private final OrderService orderService;

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(summary = "Create an order", description = "Creates an order from a cart and marks the cart as checked out")
  @ApiResponses({
      @ApiResponse(responseCode = "201", description = "Order created"),
      @ApiResponse(responseCode = "400", description = "Cart is empty or invalid"),
      @ApiResponse(responseCode = "404", description = "Cart not found")
  })
  public OrderDto createOrder(@RequestParam Long cartId) {
    return orderService.createOrder(cartId);
  }

  @GetMapping("/user/{userId}")
  @Operation(summary = "Get user orders", description = "Returns a paginated list of orders for a user with optional filters")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Orders found"),
      @ApiResponse(responseCode = "404", description = "User not found")
  })
  public Page<OrderDto> getAllOrders(
      @PathVariable Long userId,
      @RequestParam(required = false) OrderStatus status,
      @RequestParam(required = false) BigDecimal minTotal,
      @RequestParam(required = false) BigDecimal maxTotal,
      @PageableDefault(size = 20) Pageable pageable) {
    return orderService.getAllOrders(userId, status, minTotal, maxTotal, pageable);
  }

  @GetMapping("/{orderId}")
  @Operation(summary = "Get order by ID", description = "Returns a single order")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Order found"),
      @ApiResponse(responseCode = "404", description = "Order not found")
  })
  public OrderDto getOrderById(@PathVariable Long orderId) {
    return orderService.getOrderById(orderId);
  }

  @PatchMapping("/{orderId}/cancel")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(summary = "Cancel an order", description = "Cancels an order (only if it is PENDING)")
  @ApiResponses({
      @ApiResponse(responseCode = "204", description = "Order cancelled"),
      @ApiResponse(responseCode = "400", description = "Order cannot be cancelled in its current status"),
      @ApiResponse(responseCode = "404", description = "Order not found")
  })
  public void cancelOrder(@PathVariable Long orderId) {
    orderService.cancelOrder(orderId);
  }

  @PatchMapping("/{orderId}/pay")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(summary = "Pay an order", description = "Marks an order as PAID (only if it is PENDING)")
  @ApiResponses({
      @ApiResponse(responseCode = "204", description = "Order paid"),
      @ApiResponse(responseCode = "400", description = "Order cannot be paid in its current status"),
      @ApiResponse(responseCode = "404", description = "Order not found")
  })
  public void payOrder(@PathVariable Long orderId) {
    orderService.payOrder(orderId);
  }

  @PatchMapping("/{orderId}/ship")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(summary = "Ship an order", description = "Marks an order as SHIPPED (only if it is PAID)")
  @ApiResponses({
      @ApiResponse(responseCode = "204", description = "Order shipped"),
      @ApiResponse(responseCode = "400", description = "Order cannot be shipped in its current status"),
      @ApiResponse(responseCode = "404", description = "Order not found")
  })
  public void shipOrder(@PathVariable Long orderId) {
    orderService.shipOrder(orderId);
  }

  @PatchMapping("/{orderId}/deliver")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(summary = "Deliver an order", description = "Marks an order as DELIVERED (only if it is SHIPPED)")
  @ApiResponses({
      @ApiResponse(responseCode = "204", description = "Order delivered"),
      @ApiResponse(responseCode = "400", description = "Order cannot be delivered in its current status"),
      @ApiResponse(responseCode = "404", description = "Order not found")
  })
  public void deliverOrder(@PathVariable Long orderId) {
    orderService.deliverOrder(orderId);
  }
}
