package com.lautarorisso.eCommerce_api.dto.response;

import java.math.BigDecimal;

public record ProductDto(Long id, String name, String description, BigDecimal price, Integer stock) {
}
