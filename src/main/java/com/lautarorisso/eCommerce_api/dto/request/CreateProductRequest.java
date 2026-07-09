package com.lautarorisso.eCommerce_api.dto.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateProductRequest(
    @NotBlank String name,
    @NotBlank String description,
    @NotNull @DecimalMin("0.01") BigDecimal unitPrice,
    @NotNull @Min(0) Integer stock,
    @NotNull Long categoryId) {
}
