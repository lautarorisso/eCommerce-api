package com.lautarorisso.eCommerce_api.dto.response;

import java.math.BigDecimal;
import java.util.List;

import com.lautarorisso.eCommerce_api.enums.CartStatus;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Shopping cart information")
public record CartDto(
    @Schema(description = "Cart ID", example = "1")
    Long id,

    @Schema(description = "Items in the cart")
    List<CartItemDto> items,

    @Schema(description = "Cart status", example = "ACTIVE")
    CartStatus status,

    @Schema(description = "Total price", example = "59.98")
    BigDecimal total) {
}
