package com.lautarorisso.eCommerce_api.dto.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;

public record UpdateProductRequest(
    String name,
    String description,
    @DecimalMin("0.01") BigDecimal unitPrice,
    Long categoryId) {
}
