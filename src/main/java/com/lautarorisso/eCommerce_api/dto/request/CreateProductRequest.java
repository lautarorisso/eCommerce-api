package com.lautarorisso.eCommerce_api.dto.request;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Request to create a new product")
public record CreateProductRequest(
    @Schema(description = "Product name", example = "Wireless Mouse")
    @NotBlank String name,

    @Schema(description = "Product description", example = "Ergonomic wireless mouse with USB receiver")
    @NotBlank String description,

    @Schema(description = "Unit price", example = "29.99")
    @NotNull @DecimalMin("0.01") BigDecimal unitPrice,

    @Schema(description = "Initial stock quantity", example = "100")
    @NotNull @Min(0) Integer stock,

    @Schema(description = "Category ID", example = "1")
    @NotNull Long categoryId) {
}
