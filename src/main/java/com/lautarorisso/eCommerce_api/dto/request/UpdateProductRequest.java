package com.lautarorisso.eCommerce_api.dto.request;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;

@Schema(description = "Request to update an existing product")
public record UpdateProductRequest(
    @Schema(description = "Product name", example = "Wireless Mouse")
    @Size(max = 100) String name,

    @Schema(description = "Product description", example = "Ergonomic wireless mouse with USB receiver")
    @Size(max = 500) String description,

    @Schema(description = "Unit price", example = "29.99")
    @DecimalMin("0.01") BigDecimal unitPrice,

    @Schema(description = "Category ID", example = "1")
    Long categoryId) {
}
