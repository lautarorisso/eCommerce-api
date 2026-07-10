package com.lautarorisso.eCommerce_api.dto.request;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;

@Schema(description = "Request to update an existing product")
public record UpdateProductRequest(
    @Schema(description = "Product name", example = "Wireless Mouse")
    String name,

    @Schema(description = "Product description", example = "Ergonomic wireless mouse with USB receiver")
    String description,

    @Schema(description = "Unit price", example = "29.99")
    @DecimalMin("0.01") BigDecimal unitPrice,

    @Schema(description = "Category ID", example = "1")
    Long categoryId) {
}
