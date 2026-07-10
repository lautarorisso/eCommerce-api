package com.lautarorisso.eCommerce_api.dto.response;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Item in an order")
public record OrderItemDto(
    @Schema(description = "Product name", example = "Wireless Mouse")
    String productName,

    @Schema(description = "Quantity", example = "2")
    Integer quantity,

    @Schema(description = "Unit price", example = "29.99")
    BigDecimal unitPrice,

    @Schema(description = "Subtotal (quantity * unitPrice)", example = "59.98")
    BigDecimal subtotal) {
}
