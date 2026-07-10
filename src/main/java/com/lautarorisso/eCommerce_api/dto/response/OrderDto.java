package com.lautarorisso.eCommerce_api.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.lautarorisso.eCommerce_api.enums.OrderStatus;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Order information")
public record OrderDto(
    @Schema(description = "Order ID", example = "1")
    Long id,

    @Schema(description = "Items in the order")
    List<OrderItemDto> items,

    @Schema(description = "Total price", example = "59.98")
    BigDecimal totalPrice,

    @Schema(description = "Order status", example = "PENDING")
    OrderStatus status,

    @Schema(description = "Order creation timestamp")
    LocalDateTime createdAt,

    @Schema(description = "User ID who placed the order", example = "1")
    Long userId) {
}
