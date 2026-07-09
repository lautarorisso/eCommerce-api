package com.lautarorisso.eCommerce_api.dto.response;

import java.math.BigDecimal;

public record ProductDto(Long id, String name, String description, BigDecimal unitPrice, Integer stock,
    Long categoryId, String categoryName) {
}
