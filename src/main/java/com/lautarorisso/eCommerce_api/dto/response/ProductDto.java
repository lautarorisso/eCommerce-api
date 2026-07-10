package com.lautarorisso.eCommerce_api.dto.response;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Product information")
public record ProductDto(
    @Schema(description = "Product ID", example = "1")
    Long id,

    @Schema(description = "Product name", example = "Wireless Mouse")
    String name,

    @Schema(description = "Product description", example = "Ergonomic wireless mouse with USB receiver")
    String description,

    @Schema(description = "Unit price", example = "29.99")
    BigDecimal unitPrice,

    @Schema(description = "Available stock", example = "100")
    Integer stock,

    @Schema(description = "Category ID", example = "1")
    Long categoryId,

    @Schema(description = "Category name", example = "Electronics")
    String categoryName) {
}
